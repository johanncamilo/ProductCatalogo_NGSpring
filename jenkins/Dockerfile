FROM --platform=linux/amd64 jenkins/jenkins:lts

USER root

# ---------------------------------------------------
# 1. Actualizar repos e instalar dependencias básicas
# ---------------------------------------------------
RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    zip \
    unzip \
    apt-transport-https \
    software-properties-common \
    wget \
    git

# ---------------------------------------------------
# 2. Instalar Chromium (para Angular + Karma)
# ---------------------------------------------------
RUN apt-get install -y chromium-browser chromium && \
    ln -sf /usr/bin/chromium /usr/bin/chrome && \
    ln -sf /usr/bin/chromium /usr/bin/chromium-browser

# ---------------------------------------------------
# 3. Instalar Docker CLI (versión binaria AMD64)
# ---------------------------------------------------
RUN curl -L https://download.docker.com/linux/static/stable/x86_64/docker-27.1.1.tgz \
    | tar -xz --strip-components=1 -C /usr/local/bin

# ---------------------------------------------------
# 4. Instalar Docker Compose v2
# ---------------------------------------------------
RUN curl -L "https://github.com/docker/compose/releases/download/v2.29.2/docker-compose-linux-x86_64" \
    -o /usr/local/bin/docker-compose && chmod +x /usr/local/bin/docker-compose

# ---------------------------------------------------
# 5. Instalar Node.js 18
# ---------------------------------------------------
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs

# ---------------------------------------------------
# 6. Instalar Maven 3.9.6 (URL estable archive.apache.org)
# ---------------------------------------------------
RUN curl -fsSL https://archive.apache.org/dist/maven/maven-3/3.9.6/binaries/apache-maven-3.9.6-bin.tar.gz \
    | tar zx -C /opt && \
    ln -s /opt/apache-maven-3.9.6 /opt/maven

ENV MAVEN_HOME="/opt/maven"
ENV PATH="$MAVEN_HOME/bin:${PATH}"

# ---------------------------------------------------
# 7. Dar permisos de Docker al usuario Jenkins
# ---------------------------------------------------
RUN usermod -aG docker jenkins

USER jenkins