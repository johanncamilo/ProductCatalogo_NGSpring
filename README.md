# 🚀 PROYECTO: Catálogo e Inventario de Productos Tech  
### **Spring Boot 3 · Angular 18 · MySQL 8 · Docker · Jenkins CI/CD · Codecov**

[![codecov](https://codecov.io/gh/johanncamilo/ProductCatalogo_NGSpring/branch/main/graph/badge.svg)](https://codecov.io/gh/johanncamilo/ProductCatalogo_NGSpring)

Este proyecto implementa un sistema completo de catálogo e inventario de productos tecnológicos utilizando un stack moderno: **Spring Boot** para el backend, **Angular** para el frontend y **MySQL** como motor de base de datos.  
Incluye integración continua con **Jenkins** y análisis de cobertura con **Codecov**.

---

# 📦 1. Configuración Base de Datos (MySQL)

### **MySQL Community Server 9.1.0**

El backend se conecta a una base de datos llamada:

```
db_catalogo_ci
```

Configurar el archivo:

```
backend-catalogo/src/main/resources/application.properties
```

Con lo siguiente:

```properties
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

> ⚠️ Ajustar las credenciales según el entorno local.

---

# 🅱️ 2. Backend: Spring Boot

### **Compilar:**
```bash
cd backend-catalogo
./mvnw clean package
```

### **Ejecutar:**
```bash
./mvnw spring-boot:run
```

### **Tests + Cobertura (Jacoco):**
```bash
./mvnw test
```

Genera:
```
backend-catalogo/target/site/jacoco/jacoco.xml
```

Usado en Jenkins + Codecov.

---

# 🅰️ 3. Frontend: Angular 18

Este proyecto usa **fnm (Fast Node Manager)** para manejar versiones de Node.js.

### ✔ Verificar fnm
```bash
fnm --version
```

### ✔ Instalar Node LTS:
```bash
fnm install 22.*
fnm use 22.*
```

### ✔ Instalar Angular CLI:
```bash
npm i -g @angular/cli
```

### ✔ Crear proyecto (ya incluido en el repo):
```bash
ng new frontend-catalogo
```

### ✔ Ejecutar:
```bash
cd frontend-catalogo
ng serve -o
```

---

# 🐳 4. Docker Compose

Archivo `docker-compose.yml` (con MySQL + Backend + Frontend):

```yaml
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

---

# 🧪 5. Jenkins CI/CD + Codecov

El pipeline compila backend, ejecuta tests, sube cobertura a Codecov, construye imágenes Docker y despliega vía Docker Compose.

---

# 🌱 6. Seed Inicial de Productos (opcional)

```sql
INSERT INTO products (name, description, price, quantity) VALUES
('Smartphone Galaxy A54', 'Pantalla AMOLED 120Hz, 8GB RAM', 379.90, 15),
('Laptop Lenovo ThinkPad E15', 'Intel i5 12th Gen, 16GB RAM, SSD 512GB', 799.00, 10),
('Mouse Logitech MX Master 3S', 'Bluetooth, DPI ajustable', 119.99, 25),
('Monitor LG UltraWide 34"', 'WFHD 34 pulgadas IPS', 349.00, 5),
('Audífonos Sony WH-1000XM5', 'Noise Cancelling', 399.99, 8),
('Teclado Keychron K8 Pro', 'Switch Brown, Wireless', 139.50, 12);
```

---

# 📬 Contacto
Proyecto creado como parte de un flujo CI/CD completo con Docker, Jenkins y Codecov.
