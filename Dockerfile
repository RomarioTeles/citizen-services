# ============================================================
# ETAPA 1 — BUILD
# ============================================================
# Utilizamos o JDK porque precisamos compilar a aplicação.
# O JDK contém ferramentas como o javac, necessárias para
# transformar o código Java em um arquivo JAR.
FROM eclipse-temurin:21-jdk AS builder

# Define o diretório de trabalho dentro do container.
# A partir daqui, os comandos serão executados em /app.
WORKDIR /app


# ------------------------------------------------------------
# Maven Wrapper
# ------------------------------------------------------------

# Copia a configuração do Maven Wrapper.
COPY .mvn/ .mvn/

# Copia:
# - mvnw  → Maven Wrapper
# - pom.xml → configuração e dependências do projeto
#
# Ainda não copiamos o código-fonte.
COPY mvnw pom.xml ./


# ------------------------------------------------------------
# Dependências
# ------------------------------------------------------------

# Baixa antecipadamente as dependências do Maven.
#
# Isso ajuda o Docker a aproveitar o cache das camadas.
# Se alterarmos apenas o código Java, não será necessário
# baixar todas as dependências novamente.
#
# -q = modo silencioso (quiet)
RUN ./mvnw dependency:go-offline -q


# ------------------------------------------------------------
# Código-fonte
# ------------------------------------------------------------

# Agora copiamos o código da aplicação.
COPY src/ src/


# ------------------------------------------------------------
# Build da aplicação
# ------------------------------------------------------------

# Executa o build:
#
# clean  → limpa builds anteriores
# package → compila e gera o JAR
# -DskipTests → não executa os testes durante a criação da imagem
# -q → reduz a quantidade de logs
#
# O JAR será gerado em:
#
# /app/target/
RUN ./mvnw clean package -DskipTests -q



# ============================================================
# ETAPA 2 — RUNTIME
# ============================================================
# Aqui começamos uma nova imagem.
#
# Não precisamos mais do JDK, Maven ou código-fonte.
# Precisamos apenas de um ambiente capaz de executar o JAR.
FROM eclipse-temurin:21-jre

# Diretório de trabalho da aplicação.
WORKDIR /app


# ------------------------------------------------------------
# Aplicação compilada
# ------------------------------------------------------------

# Copia o JAR produzido na etapa "builder".
#
# --from=builder indica que o arquivo será copiado
# da primeira etapa do Dockerfile.
#
# A imagem final não recebe:
# - código-fonte
# - Maven
# - JDK
# - arquivos de build
#
# Recebe somente o JAR necessário para executar a aplicação.
COPY --from=builder /app/target/*.jar app.jar


# ------------------------------------------------------------
# Porta da aplicação
# ------------------------------------------------------------

# Documenta que a aplicação utiliza a porta 8080
# dentro do container.
#
# IMPORTANTE:
# EXPOSE não publica a porta no computador.
# A publicação é feita pelo Docker Compose.
EXPOSE 8080


# ------------------------------------------------------------
# Inicialização
# ------------------------------------------------------------

# Comando executado quando o container for iniciado.
#
# Equivale a:
#
# java -jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]