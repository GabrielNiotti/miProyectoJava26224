# 🧴 La Perfu — E-Commerce Full-Stack (Spring Boot + JWT + MySQL)

Aplicación web Full-Stack de perfumería y cosmética desarrollada con **Java 25**, **Spring Boot 3.5**, **Spring Security + JWT**, **Spring Data JPA / Hibernate** y **MySQL**, acompañada de un Front-End interactivo en **HTML5, CSS3, Bootstrap 5 y JavaScript**.

---

## 🔐 Credenciales de Acceso para Evaluación

El sistema cuenta con control de acceso basado en roles (`ADMIN` y `USER`) mediante **JSON Web Tokens (JWT)** y contraseñas encriptadas con **BCrypt**:

| Rol | Email | Contraseña | Permisos habilitados |
| :--- | :--- | :--- | :--- |
| **Administrador (`ADMIN`)** | `admin@mail.com` | `1234` | Acceso a `admin.html`, alta/edición/baja de productos y categorías, subida de imágenes, visualización de pedidos y cambio de estados. |
| **Cliente (`USER`)** | `usuario@mail.com` | `1234` | Acceso a la tienda (`index.html`), carrito con financiación, confirmación de compras e historial de pedidos en modo lectura (`pedidos.html`). |

> **Nota:** También es posible registrar cuentas nuevas desde `login.html` (pestaña *Registrarse*) o mediante `POST /auth/register`. Por seguridad, todo registro nuevo recibe automáticamente el rol `USER`.

---

## 🛠️ Tecnologías Utilizadas

* **Back-End:** Java 25, Spring Boot 3.5, Spring Web, Spring Data JPA, Spring Security, JJWT (`0.13.0`), Maven.
* **Base de Datos:** MySQL (con validación de esquema `spring.jpa.hibernate.ddl-auto=validate`).
* **Front-End:** HTML5, CSS3, Bootstrap 5, Font Awesome, JavaScript (Fetch API, LocalStorage, autenticación Bearer JWT).

---

## 🗄️ Configuración de Base de Datos (MySQL)

El proyecto utiliza `ddl-auto=validate`, por lo que las tablas deben existir previamente en MySQL. Ejecutá el siguiente script para crear la base de datos **`laperfu`**, todas sus tablas y los usuarios iniciales de prueba (con contraseña `1234` hasheada en BCrypt):

```sql
CREATE DATABASE IF NOT EXISTS laperfu CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE laperfu;

CREATE TABLE IF NOT EXISTS categoria (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS producto (
    id BIGINT NOT NULL AUTO_INCREMENT,
    nombre VARCHAR(255) NOT NULL,
    descripcion VARCHAR(500),
    precio DOUBLE NOT NULL,
    stock INT NOT NULL,
    img VARCHAR(255),
    categoria_id BIGINT,
    PRIMARY KEY (id),
    CONSTRAINT fk_producto_categoria FOREIGN KEY (categoria_id) REFERENCES categoria(id)
);

CREATE TABLE IF NOT EXISTS pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    fecha DATETIME(6) NOT NULL,
    total DOUBLE NOT NULL,
    metodo_pago VARCHAR(100) NOT NULL,
    cuotas INT NOT NULL,
    estado VARCHAR(50) NOT NULL,
    usuario_id BIGINT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_pedido_usuario FOREIGN KEY (usuario_id) REFERENCES usuario(id)
);


CREATE TABLE IF NOT EXISTS detalle_pedido (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cantidad INT NOT NULL,
    precio_unitario DOUBLE NOT NULL,
    pedido_id BIGINT NOT NULL,
    producto_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    CONSTRAINT fk_item_pedido FOREIGN KEY (pedido_id) REFERENCES pedido(id),
    CONSTRAINT fk_item_producto FOREIGN KEY (producto_id) REFERENCES producto(id)
);

CREATE TABLE IF NOT EXISTS usuario (
    id BIGINT NOT NULL AUTO_INCREMENT,
    email VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL,
    rol VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uk_usuario_email (email)
);

-- Usuarios de prueba precargados (contraseña para ambos: 1234)
INSERT IGNORE INTO usuario (email, password, rol) VALUES
('admin@mail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'ADMIN'),
('usuario@mail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'USER');
```

---

## ⚙️ Configuración de `application.properties`

Verificá que `src/main/resources/application.properties` tenga configurada la conexión a tu instancia de MySQL y la clave secreta JWT (mínimo 32 caracteres):

```properties
spring.application.name=laperfu

# Conexión MySQL
spring.datasource.url=jdbc:mysql://localhost:3307/laperfu?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true
spring.datasource.username=root
spring.datasource.password=

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true

# Seguridad JWT
jwt.secret=clave-secreta-laperfu-proyecto-final-2026
jwt.expiration=3600000
```

---

## 🚀 Cómo Ejecutar el Proyecto

1. Cloná el repositorio e importalo en VS Code o IntelliJ IDEA como proyecto Maven.
2. Ejecutá el script SQL en MySQL para crear la base `laperfu` y sus tablas.
3. Iniciá la aplicación ejecutando `LaperfuApplication.java` (o con `./mvnw spring-boot:run`).
4. Abrí en tu navegador:
   * **Tienda Principal:** `http://localhost:8080/index.html`
   * **Login y Registro:** `http://localhost:8080/login.html`
   * **Historial de Pedidos (requiere login):** `http://localhost:8080/pedidos.html`
   * **Panel de Administración (solo `ADMIN`):** `http://localhost:8080/admin.html`

---

## 📡 Endpoints de la API REST y Reglas de Acceso

| Método | Endpoint | Acceso / Rol | Descripción |
| :--- | :--- | :--- | :--- |
| `POST` | `/auth/register` | Público | Registra un usuario nuevo con hash BCrypt y rol `USER`. |
| `POST` | `/auth/login` | Público | Valida credenciales y devuelve `{ token, email, rol }`. |
| `GET` | `/producto` | Público | Lista el catálogo completo de productos. |
| `GET` | `/categoria` | Público | Lista todas las categorías disponibles. |
| `POST` | `/producto` | `ADMIN` | Crea un nuevo producto en el catálogo. |
| `PUT` | `/producto/{id}` | `ADMIN` | Actualiza los datos o stock de un producto. |
| `DELETE` | `/producto/{id}` | `ADMIN` | Elimina un producto del catálogo. |
| `POST` | `/producto/subir-imagen` | `ADMIN` | Sube una imagen al servidor y devuelve el nombre de archivo. |
| `POST` | `/categoria` | `ADMIN` | Crea una nueva categoría. |
| `GET` | `/pedido` | Autenticado (`USER` / `ADMIN`) | Lista el historial de pedidos registrados. |
| `POST` | `/pedido` | Autenticado (`USER` / `ADMIN`) | Registra un pedido nuevo y descuenta el stock. |
| `PUT` | `/pedido/{id}/estado` | Autenticado (`ADMIN` en UI) | Actualiza el estado del pedido y repone stock si se cancela. |

---

## 📂 Estructura del Proyecto

```text
laperfu/
├── pom.xml
├── pruebas/
│   └── pruebas-auth.http
└── src/main/
    ├── java/com/techlab/laperfu/
    │   ├── config/          # CorsConfig y SecurityConfig (reglas Stateless JWT)
    │   ├── controller/      # AuthController, ProductoController, CategoriaController, PedidoController
    │   ├── dto/             # AuthRequest, PedidoRequestDTO, PedidoResponseDTO, etc.
    │   ├── model/           # Usuario, Producto, Categoria, Pedido, DetallePedido
    │   ├── repository/      # Repositorios Spring Data JPA
    │   ├── security/        # JwtService, JwtAuthenticationFilter, UsuarioDetailsService
    │   └── service/         # Lógica de negocio y control de stock
    └── resources/
        ├── application.properties
        └── static/          # index.html, login.html, admin.html, pedidos.html, js/, css/, imagenes/
```
