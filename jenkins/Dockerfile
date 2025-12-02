FROM --platform=linux/amd64 jenkins/jenkins:lts

USER root

# 1. Actualizar sistema e instalar dependencias
RUN apt-get update && apt-get install -y \
    ca-certificates \
    curl \
    gnupg \
    lsb-release \
    apt-transport-https \
    software-properties-common \
    wget \
    git

# 2. Instalar Chromium para pruebas Angular/Karma
RUN apt-get install -y chromium chromium-common chromium-l10n \
    && ln -s /usr/bin/chromium /usr/bin/chrome \
    && ln -s /usr/bin/chromium /usr/bin/chromium-browser

# 3. Instalar Docker CLI (binario estático AMD64)
RUN curl -L https://download.docker.com/linux/static/stable/x86_64/docker-27.1.1.tgz \
    | tar -xz --strip-components=1 -C /usr/local/bin

# 4. Instalar Docker Compose v2
RUN curl -L "https://github.com/docker/compose/releases/download/v2.29.2/docker-compose-linux-x86_64" \
    -o /usr/local/bin/docker-compose && \
    chmod +x /usr/local/bin/docker-compose

# 5. Instalar Node.js 18 (AMD64)
RUN curl -fsSL https://deb.nodesource.com/setup_18.x | bash - && \
    apt-get install -y nodejs


# 6. Instalar Maven 3.9.6 (GitHub Release = reliable)
RUN curl -fsSL https://github.com/apache/maven/archive/refs/tags/maven-3.9.6.tar.gz \
    | tar zx -C /opt && \
    mv /opt/maven-maven-3.9.6 /opt/apache-maven-3.9.6 && \
    ln -s /opt/apache-maven-3.9.6 /opt/maven

ENV MAVEN_HOME=/opt/maven
ENV PATH="$MAVEN_HOME/bin:${PATH}"

# 7. Dar permisos Docker al usuario Jenkins
RUN usermod -aG docker jenkins

USER jenkins