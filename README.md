# 🛍️ La Perfu - Ecosistema Backend & API REST

Este repositorio contiene el backend para **La Perfu**, una plataforma diseñada bajo una arquitectura limpia y desacoplada que integra una base de datos relacional robusta en **MySQL** con una API REST escalable desarrollada en **Spring Boot 3.x**.

---

## 📋 1. Capa de Datos (MySQL)

El modelo de datos gestiona de forma estricta la integridad referencial del catálogo mediante una relación **uno a muchos (1:N)** entre categorías y productos.

### Script de Inicialización DDL
```sql
CREATE DATABASE IF NOT EXISTS laperfu;
USE laperfu;

-- Tabla de Categorías (Entidad Independiente)
CREATE TABLE IF NOT EXISTS categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(50) NOT NULL UNIQUE
);

-- Tabla de Productos (Entidad Dependiente)
CREATE TABLE IF NOT EXISTS producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria_id INT NOT NULL,
    descripcion TEXT,
    
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
```

---

## ☕ 2. Estructura del Proyecto (Spring Boot)

El código fuente en Java sigue el patrón de diseño clásico de arquitectura por capas (**MVC/Service/Repository**):

```text
src/main/java/com/laperfu/api/
│
├── controller/       # Capa de presentación (Endpoints HTTP / Controladores REST)
│   ├── CategoriaController.java
│   └── ProductoController.java
│
├── entity/           # Capa de dominio (Mapeo O/R con anotaciones JPA / Hibernate)
│   ├── Categoria.java
│   └── Producto.java
│
├── repository/       # Capa de acceso a datos (Abstracción de consultas con JpaRepository)
│   ├── CategoriaRepository.java
│   └── ProductoRepository.java
│
└── service/          # Capa de negocio (Validaciones lógicas y reglas comerciales)
    ├── CategoriaService.java
    └── ProductoService.java
```

### Configuración del Entorno (`application.properties`)
Ubicado en `src/main/resources/application.properties` para la conectividad local:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/laperfu?serverTimezone=UTC&useSSL=false
spring.datasource.username=root
spring.datasource.password=tu_contraseña
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# Configuración Hibernate / JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect
```

---

## 🚀 3. Documentación de Endpoints (API REST)

La API responde y procesa información utilizando el estándar **JSON**. Cuenta con prevención de recursión infinita mediante deserialización selectiva de JPA.

### Categorías (`/api/categorias`)
*   `GET /api/categorias`: Retorna el listado completo de categorías junto con sus productos anidados.
*   `POST /api/categorias`: Registra una nueva categoría en el sistema.
    ```json
    { "nombre": "Cosmética" }
    ```

### Productos (`/api/productos`)
*   `GET /api/productos`: Retorna el catálogo entero de productos con su respectiva información de categoría.
*   `GET /api/productos/{id}`: Busca un producto puntual por su ID numérico. Retorna `404 Not Found` si no existe.
*   `POST /api/productos`: Registra un producto vinculándolo a un ID de categoría existente (valida que el precio no sea menor a 0).
    ```json
    {
      "nombre": "Perfume Importado XYZ",
      "precio": 85000.00,
      "stock": 20,
      "descripcion": "Fragancia floral de alta duración",
      "categoria": { "id": 1 }
    }
    ```
*   `DELETE /api/productos/{id}`: Remueve físicamente el producto y retorna un código de estado `204 No Content`.
Esto es lo que tengo hasta ahora