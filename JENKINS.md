# Jenkins local com Docker Desktop

Este documento registra os passos que funcionaram para preparar um Jenkins local e uma imagem de agente com Java 21, Git e Docker CLI no Windows com Docker Desktop.

> **Estado atual:** o Jenkins está em execução na porta `8081`, a imagem do agente foi criada e o agente consegue consultar o Docker Engine quando o comando de teste é executado como `root`. O cadastro e a conexão permanente do agente no Jenkins ainda não foram concluídos.

## 1. Pré-requisitos

- Windows com PowerShell
- Docker Desktop usando contêineres Linux
- Contêiner Jenkins criado com a imagem `jenkins/jenkins:lts-jdk21`
- Contêiner do Jenkins chamado `jenkins`, acessível em `http://localhost:8081`
- Pasta de trabalho usada neste laboratório: `C:\Users\romar\OneDrive\Documents\projetos\jenkins-lab\agent`

## 2. Criar a pasta do agente

No PowerShell, caso a pasta ainda não exista:

```powershell
New-Item -ItemType Directory -Force -Path "C:\dev\jenkins-lab\agent"
```

Entre na pasta que contém o `Dockerfile`. Neste laboratório, o diretório usado foi:

```powershell
Set-Location "C:\Users\romar\OneDrive\Documents\projetos\jenkins-lab\agent"
```

Use o caminho real da sua pasta se for diferente.

## 3. Criar o Dockerfile do agente

Crie um arquivo chamado `Dockerfile` com este conteúdo:

```dockerfile
FROM jenkins/inbound-agent:jdk21

USER root

RUN apt-get update \
    && apt-get install -y --no-install-recommends \
       ca-certificates \
       curl \
       git \
       gnupg \
    && install -m 0755 -d /etc/apt/keyrings \
    && curl -fsSL https://download.docker.com/linux/debian/gpg \
       -o /etc/apt/keyrings/docker.asc \
    && chmod a+r /etc/apt/keyrings/docker.asc \
    && echo "Types: deb" > /etc/apt/sources.list.d/docker.sources \
    && echo "URIs: https://download.docker.com/linux/debian" >> /etc/apt/sources.list.d/docker.sources \
    && echo "Suites: trixie" >> /etc/apt/sources.list.d/docker.sources \
    && echo "Components: stable" >> /etc/apt/sources.list.d/docker.sources \
    && echo "Architectures: $(dpkg --print-architecture)" >> /etc/apt/sources.list.d/docker.sources \
    && echo "Signed-By: /etc/apt/keyrings/docker.asc" >> /etc/apt/sources.list.d/docker.sources \
    && apt-get update \
    && apt-get install -y --no-install-recommends docker-ce-cli \
    && rm -rf /var/lib/apt/lists/*

USER jenkins
```

### Por que este Dockerfile?

- `jenkins/inbound-agent:jdk21` fornece um agente Jenkins com Java 21.
- `USER root` permite instalar os pacotes durante a construção da imagem.
- O bloco `apt-get` instala certificados, `curl`, Git e ferramentas para configurar o repositório oficial do Docker.
- O arquivo `docker.sources` configura o repositório oficial do Docker para Debian Trixie.
- `docker-ce-cli` instala apenas o cliente Docker, não um daemon Docker separado.
- `USER jenkins` restaura o usuário não privilegiado para a execução normal do agente.

## 4. Construir a imagem do agente

Na pasta que contém o `Dockerfile`, execute:

```powershell
docker build -t citizen-services-jenkins-agent:jdk21 .
```

Isso constrói uma imagem chamada `citizen-services-jenkins-agent` com a etiqueta `jdk21`. O ponto final (`.`) indica que a pasta atual é o contexto do build.

## 5. Verificar Java 21

```powershell
docker run --rm --entrypoint java citizen-services-jenkins-agent:jdk21 -version
```

Resultado observado: Java `21.0.12.1` (Temurin).

## 6. Verificar Git

```powershell
docker run --rm --entrypoint git citizen-services-jenkins-agent:jdk21 --version
```

Resultado observado:

```text
git version 2.47.3
```

## 7. Verificar Docker CLI

```powershell
docker run --rm --entrypoint docker citizen-services-jenkins-agent:jdk21 --version
```

Resultado observado:

```text
Docker version 29.9.0, build f415da8
```

Esse teste confirma que o cliente Docker está instalado na imagem, mas não confirma que ele consegue acessar o Docker Engine.

## 8. Conferir o contexto do Docker Desktop

```powershell
docker context inspect desktop-linux
```

No ambiente usado, o contexto apontou para o named pipe do Windows:

```text
npipe:////./pipe/dockerDesktopLinuxEngine
```

O cliente Docker dentro do contêiner Linux não usa diretamente esse named pipe do Windows, por isso foi necessário testar o acesso ao Engine separadamente.

## 9. Testar o acesso ao Docker Engine pelo socket

Primeiro, foi testada a montagem do socket:

```powershell
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock --entrypoint docker citizen-services-jenkins-agent:jdk21 info
```

O socket ficou visível, mas o acesso foi negado ao usuário `jenkins`.

Para inspecionar o usuário e as permissões:

```powershell
docker run --rm -v /var/run/docker.sock:/var/run/docker.sock --entrypoint sh citizen-services-jenkins-agent:jdk21 -c "id; ls -l /var/run/docker.sock"
```

Resultado observado:

```text
uid=1000(jenkins) gid=1000(jenkins) groups=1000(jenkins)
srw-rw---- 1 root root 0 Oct  9 12:17 /var/run/docker.sock
```

O socket pertencia a `root:root` e o usuário `jenkins` não tinha permissão para acessá-lo.

### Teste que funcionou

```powershell
docker run --rm -u 0 -v /var/run/docker.sock:/var/run/docker.sock --entrypoint docker citizen-services-jenkins-agent:jdk21 info
```

Esse comando retornou as informações de cliente e servidor do Docker Engine. O servidor informou Docker Engine `29.7.2`, Docker Desktop e kernel WSL2.

**Importante:** o teste usou `-u 0`, executando apenas o comando temporário como root dentro do contêiner. Isso confirmou que o problema era de permissão no socket. Ainda não é uma configuração permanente do agente e não significa que devemos executar o agente inteiro como root.

O acesso ao socket Docker concede controle amplo sobre o daemon e os contêineres. Antes de usar esse acesso no pipeline, deve ser definida uma configuração apropriada para o agente.

## 10. Criar uma rede Docker para o Jenkins

```powershell
docker network create jenkins-net
```

Esse comando cria uma rede bridge chamada `jenkins-net` para permitir a comunicação entre os contêineres conectados a ela.

Conecte o contêiner existente do Jenkins à rede:

```powershell
docker network connect jenkins-net jenkins
```

O comando terminou sem exibir mensagem, comportamento normal quando a operação é bem-sucedida.

Para confirmar:

```powershell
docker network inspect jenkins-net
```

Resultado relevante observado:

```text
Name: jenkins-net
Driver: bridge
Containers:
  Name: jenkins
  IPv4Address: 172.19.0.2/16
```

O endereço IP interno pode variar. Na configuração futura, prefira usar o nome do contêiner na rede em vez de depender desse IP.

## 11. Acessar a interface do Jenkins

Abra no navegador:

[http://localhost:8081](http://localhost:8081)

O Jenkins usa a porta `8081` no host para evitar conflito com a aplicação Citizen Services, que utiliza a porta `8080`.

## 12. Próximas etapas

As etapas abaixo ainda não foram concluídas:

1. Cadastrar um nó/agente permanente no Jenkins.
2. Configurar a conexão inbound do agente com o controlador.
3. Definir como o agente acessará o Docker Engine sem executar o agente inteiro como root.
4. Validar que o agente executa os testes do projeto, incluindo os testes de integração com Testcontainers.
5. Criar e executar o `Jenkinsfile` para build, testes, publicação dos relatórios JUnit e construção da imagem da aplicação.

Até essas etapas serem concluídas, o pipeline de CI não deve ser considerado pronto.

## Comandos úteis

Ver contêineres em execução:

```powershell
docker ps
```

Inspecionar a rede:

```powershell
docker network inspect jenkins-net
```

Ver a imagem do agente:

```powershell
docker image ls citizen-services-jenkins-agent
```
