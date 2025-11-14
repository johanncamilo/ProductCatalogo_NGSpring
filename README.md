# PROYECTO CATÁLOGO INVENTARIO DE PRODUCTOS TECH (Spring + Angular + MySQL)

## CONFIGURACIÓN CONEXIÓN BASE DE DATOS
### MySQL Community Server - GPL Ver 9.1.0

En el archivo `backend-catalogo/src/main/resources/application.properties` del proyecto Spring Boot, configurar la conexión a la base de datos MySQL de la siguiente manera:

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
> Nota: Modificar las variables `spring.datasource.username` y `spring.datasource.password` con las credenciales correspondientes a la instalación local de MySQL.


## Parte Angular

uso fnm para manejar dependencias de node

fnm --version
fnm install 22.*
fnm use 22.*


```shell
npm i -g @angular/cli

ng new frontend-catalogo
```
