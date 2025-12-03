# 🚀 PROYECTO: Catálogo e Inventario de Productos Tech

### **Spring Boot 3 · Angular 18 · MySQL 8 · Docker · Jenkins CI/CD · Codecov Test Analytics**

[![codecov](https://codecov.io/gh/jsborbon/ProductCatalogo_NGSpring/branch/main/graph/badge.svg)](https://codecov.io/gh/jsborbon/ProductCatalogo_NGSpring)

Sistema de catálogo e inventario de productos tecnológicos construido
con un stack moderno:\
**Spring Boot (backend)** · **Angular (frontend)** · **MySQL
(database)**\
Integrado con **Jenkins** para CI/CD y **Codecov** para métricas de
cobertura y análisis de tests.

------------------------------------------------------------------------

# 🧩 Tecnologías principales

  Componente       Tecnología
  ---------------- -----------------------------------------
  Backend          Spring Boot 3, Spring Data JPA, Java 17
  Frontend         Angular 18, TypeScript, Karma/Jasmine
  Base de datos    MySQL 8
  Contenedores     Docker + Docker Compose
  CI/CD            Jenkins Multibranch Pipeline
  Calidad          SonarQube, Jacoco, Codecov
  Test Analytics   JUnit + Codecov CLI

------------------------------------------------------------------------

# 📦 1. Configuración Base de Datos (MySQL)

### **MySQL Community Server 9.1.0**

Crear base de datos:

    db_catalogo_ci

Archivo de configuración:

    backend-catalogo/src/main/resources/application.properties

``` properties
spring.application.name=backend-catalogo

spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.datasource.url=jdbc:mysql://localhost:3306/db_catalogo_ci?createDatabaseIfNotExist=true

spring.datasource.username=root
spring.datasource.password=12345678

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

> ⚠️ Ajustar credenciales y URL según el entorno.

------------------------------------------------------------------------

# 🅱️ 2. Backend: Spring Boot

### **Compilar**

``` bash
cd backend-catalogo
./mvnw clean package
```

### **Ejecutar**

``` bash
./mvnw spring-boot:run
```

### **Tests + Cobertura (Jacoco)**

``` bash
./mvnw test
```

Genera:

    backend-catalogo/target/site/jacoco/jacoco.xml

Usado en Jenkins + Codecov.

### ✔ Test de contexto (para cobertura 100% en la clase principal)

``` java
@SpringBootTest
@ActiveProfiles("test")
class BackendCatalogoApplicationTests {
    @Test
    void contextLoads() {}
}
```

------------------------------------------------------------------------

# 🅰️ 3. Frontend: Angular 18

Este proyecto usa **fnm (Fast Node Manager)**.

### ✔ Instalar Node 22 LTS

``` bash
fnm install 22.*
fnm use 22.*
```

### ✔ Instalar Angular CLI

``` bash
npm i -g @angular/cli
```

### ✔ Ejecutar frontend

``` bash
cd frontend-catalogo
ng serve -o
```

### ✔ Tests (Karma + JUnit XML)

``` bash
ng test --watch=false --code-coverage --browsers=ChromeHeadless
```

Genera:

    frontend-catalogo/coverage/lcov.info
    frontend-catalogo/coverage/frontend-tests.xml

------------------------------------------------------------------------

# 🧪 4. Codecov: Cobertura + Test Analytics

El proyecto sube:

### ✔ Cobertura Backend (Jacoco XML)

### ✔ Cobertura Frontend (lcov.info)

### ✔ Resultados JUnit Backend

### ✔ Resultados JUnit Frontend

Mediante el Codecov CLI en Jenkins:

``` bash
./codecov     -f backend-catalogo/target/site/jacoco/jacoco.xml     -f frontend-catalogo/coverage/lcov.info     -r backend-catalogo/target/surefire-reports     -r frontend-catalogo/coverage/frontend-tests.xml     --verbose
```

### 🔥 PR automation (comentarios automáticos)

Codecov comenta cada Pull Request con:

-   Diferencia de cobertura\
-   Archivos afectados\
-   Tests fallados\
-   Flaky tests\
-   Tiempo de ejecución

Controlado por `codecov.yml`:

``` yaml
codecov:
  require_ci_to_pass: yes

comment:
  layout: "diff, flags, files, tests"
  behavior: default

coverage:
  status:
    project:
      default:
        target: auto
        threshold: 1%
    patch:
      default:
        target: auto
        threshold: 1%
```

------------------------------------------------------------------------

# 🐳 5. Docker Compose

Ejecutar todo el stack con:

``` bash
docker-compose up -d --build
```

Archivo completo:

``` yaml
services:
  mysql:
    image: mysql:8
    container_name: mysql
    environment:
      MYSQL_ROOT_PASSWORD: 12345678
      MYSQL_DATABASE: db_catalogo_ci
    ports:
      - "3306:3306"
    volumes:
      - mysql_data:/var/lib/mysql
    networks:
      - appnet
    healthcheck:
      test: ["CMD", "mysqladmin", "ping", "-h", "localhost"]
      interval: 5s
      timeout: 3s
      retries: 10
      start_period: 10s

  backend-catalogo:
    build: ./backend-catalogo
    depends_on:
      mysql:
        condition: service_healthy
    environment:
      SPRING_DATASOURCE_URL: jdbc:mysql://mysql:3306/db_catalogo_ci
      SPRING_DATASOURCE_USERNAME: root
      SPRING_DATASOURCE_PASSWORD: 12345678
    ports:
      - "8080:8080"
    networks:
      - appnet

  frontend-catalogo:
    build: ./frontend-catalogo
    ports:
      - "8082:80"
    depends_on:
      - backend-catalogo
    networks:
      - appnet

volumes:
  mysql_data:

networks:
  appnet:
    driver: bridge
```

------------------------------------------------------------------------

# ⚙️ 6. Jenkins CI/CD Pipeline

El pipeline ejecuta:

1.  Checkout\
2.  Build Backend\
3.  Tests + Jacoco\
4.  SonarQube Analysis\
5.  Quality Gate\
6.  Build Frontend + Tests + JUnit XML\
7.  Subida de cobertura y tests a Codecov\
8.  Build de imágenes Docker\
9.  Deploy vía Docker Compose

Incluye GitHub Checks y Test Analytics.

------------------------------------------------------------------------

# 🌱 7. Seed Inicial de Productos (opcional)

``` sql
INSERT INTO products (name, description, price, quantity) VALUES
('Smartphone Galaxy A54', 'Pantalla AMOLED 120Hz, 8GB RAM', 379.90, 15),
('Laptop Lenovo ThinkPad E15', 'Intel i5 12th Gen, 16GB RAM, SSD 512GB', 799.00, 10),
('Mouse Logitech MX Master 3S', 'Bluetooth, DPI ajustable', 119.99, 25),
('Monitor LG UltraWide 34"', 'WFHD 34 pulgadas IPS', 349.00, 5),
('Audífonos Sony WH-1000XM5', 'Noise Cancelling', 399.99, 8),
('Teclado Keychron K8 Pro', 'Switch Brown, Wireless', 139.50, 12);
```

------------------------------------------------------------------------

# 📬 Contacto

Proyecto creado como parte de un entorno real de **CI/CD profesional**:\
Docker · Jenkins · SonarQube · Codecov · Angular · Spring Boot.
