# Laperfu - Back-End API
### 🎓 Proyecto Final - Talento Tech
**Creado por:** Gabriel Pablo Niotti

Este es el proyecto back-end para **Laperfu**, una API REST desarrollada en **Java con Spring Boot** para la gestión de un catálogo de perfumes. La aplicación incluye control de stock, excepciones personalizadas y relaciones relacionales con categorías.

## 🛠️ Tecnologías y Versiones Utilizadas
* **Java:** JDK 17 o superior
* **Framework:** Spring Boot 3.x
* **Persistencia:** Spring Data JPA / Hibernate
* **Base de Datos:** MySQL
* **Editor:** Visual Studio Code (con Lombok 1.18.48/Getter-Setter tradicionales para evitar fallas del compilador)

---

## 🗄️ Estructura de la Base de Datos (SQL)

La base de datos se compone de dos tablas fuertemente vinculadas mediante restricciones de clave foránea. Cada producto debe pertenecer obligatoriamente a una categoría.

```sql
-- Tabla de categorías
CREATE TABLE IF NOT EXISTS categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- Tabla de productos
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

### Reglas de Integridad Referencial:
* `ON UPDATE CASCADE`: Si cambia el ID de una categoría, se actualiza automáticamente en los productos asociados.
* `ON DELETE RESTRICT`: No se puede eliminar una categoría del sistema si todavía existen perfumes cargados que pertenecen a ella.

---

## 📁 Estructura del Proyecto (Backend Java)

El código fuente está organizado bajo el paquete principal `com.techlab.laperfu`:

```text
src/main/java/com/techlab/laperfu/
│
├── controller/
│   ├── CategoriaController.java     # Endpoints de consulta y carga de categorías
│   └── ProductoController.java      # Endpoints CRUD para perfumes
│
├── service/
│   ├── CategoriaService.java        # Lógica de negocio e integridad de categorías
│   └── ProductoService.java         # Lógica de negocio y control de stock de productos
│
├── repository/
│   ├── CategoriaRepository.java     # Acceso a datos de JPA para categorías
│   └── ProductoRepository.java      # Acceso a datos de JPA para productos
│
├── model/
│   ├── Categoria.java               # Entidad de persistencia Categoria
│   └── Producto.java                # Entidad de persistencia Producto (con relación @ManyToOne)
│
└── exception/
    ├── GlobalExceptionHandler.java  # Capturador centralizado de errores API
    ├── ProductoNoEncontradoException.class
    ├── CategoriaNoEncontradaException.class
    └── StockInsuficienteException.class
```

---

## 🚀 Guía de Pruebas Rápidas (REST Client)

Para testear los endpoints desde VS Code utilizando la extensión REST Client, podés usar el siguiente flujo secuencial dentro de tu archivo `.http`. 

> ⚠️ **Importante**: Debido a la restricción `NOT NULL` de la clave foránea, primero debés ejecutar la petición para **crear la categoría** antes de poder agregar cualquier perfume.

```http
### ==========================================
### ENDPOINTS DE CATEGORÍAS
### ==========================================

### 1. Crear una nueva categoría (Paso obligatorio inicial)
POST http://localhost:8080/categoria
Content-Type: application/json

{
  "nombre": "Perfumes Importados"
}

### 2. Listar todas las categorías (Para verificar los IDs creados)
GET http://localhost:8080/categoria
Accept: application/json


### ==========================================
### ENDPOINTS DE PRODUCTOS
### ==========================================

### 3. Listar todos los productos
GET http://localhost:8080/producto
Accept: application/json

### 4. Crear un producto de prueba
# Vincula el perfume enviando el objeto estructurado con el ID de la categoría
POST http://localhost:8080/producto
Content-Type: application/json

{
  "nombre": "Versace Eros",
  "precio": 160000.0,
  "stock": 7,
  "descripcion": "Notas de menta, manzana verde y haba tonka.",
  "categoria": {
    "id": 1
  }
}

### 5. Actualizar un producto existente (Ej: ID 7)
PUT http://localhost:8080/producto/7
Content-Type: application/json

{
  "nombre": "Flower by Kenzo",
  "precio": 175000.0,
  "stock": 5,
  "descripcion": "Perfume modified: notas de salida cítricas, pimienta negra y maderas.",
  "categoria": {
    "id": 1
  }
}

### 6. Eliminar un producto por ID (Ej: ID 7)
DELETE http://localhost:8080/producto/7
```
