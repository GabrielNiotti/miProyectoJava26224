# Laperfu - Full-Stack E-Commerce & Back-End API
### 🎓 Proyecto Final - Talento Tech
**Creado por:** Gabriel Pablo Niotti

Este es el proyecto integral para **Laperfu**, compuesto por una API REST desarrollada en **Java con Spring Boot** y un **Front-End interactivo (HTML5, Bootstrap 5, JavaScript ES6)** para la gestión de un catálogo de productos y la administración del ciclo de vida de pedidos comerciales en tiempo real. La aplicación incluye un panel de administración visual con subida de imágenes, validaciones de negocio centralizadas, cálculos dinámicos de financiación/descuentos, consultas detalladas mediante DTOs y gestión de estados en vivo.

## 🛠️ Tecnologías y Versiones Utilizadas
* **Back-End:** Java (JDK 25 LTS), Spring Boot 3.x, Spring Data JPA / Hibernate, Spring Boot DevTools
* **Base de Datos:** MySQL (Configurado localmente en el puerto `3307` vía XAMPP)
* **Front-End:** HTML5, CSS3, Bootstrap 5.3.8, FontAwesome 6, JavaScript Vanilla (Fetch API, FormData, LocalStorage, Toasts & Modals)
* **Editor:** Visual Studio Code (con Lombok 1.18.48 / Getters y Setters tradicionales)

---

## 🗄️ Estructura de la Base de Datos (SQL)

La base de datos relacional se compone de cuatro tablas fuertemente vinculadas mediante restricciones de clave foránea, garantizando la consistencia y la trazabilidad de los artículos y las ventas.

```sql
-- 1. Tabla de categorías
CREATE TABLE IF NOT EXISTS categoria (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL
);

-- 2. Tabla de productos
CREATE TABLE IF NOT EXISTS producto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nombre VARCHAR(100) NOT NULL,
    precio DECIMAL(10,2) NOT NULL,
    stock INT NOT NULL DEFAULT 0,
    categoria_id INT NOT NULL,
    descripcion TEXT,
    img VARCHAR(255) DEFAULT NULL,
    
    CONSTRAINT fk_producto_categoria
        FOREIGN KEY (categoria_id)
        REFERENCES categoria(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);

-- 3. Tabla de pedidos (Datos generales de la compra)
CREATE TABLE IF NOT EXISTS pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    fecha TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    total DECIMAL(10,2) NOT NULL,
    metodo_pago VARCHAR(50) NOT NULL DEFAULT 'efectivo',
    cuotas INT DEFAULT 1,
    estado VARCHAR(30) DEFAULT 'PENDIENTE'
);

-- 4. Tabla detalle de pedido (Renglones internos de cada orden)
CREATE TABLE IF NOT EXISTS detalle_pedido (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    pedido_id BIGINT NOT NULL,
    producto_id INT NOT NULL,
    cantidad INT NOT NULL,
    precio_unitario DECIMAL(10,2) NOT NULL,
    
    CONSTRAINT fk_detalle_pedido
        FOREIGN KEY (pedido_id)
        REFERENCES pedido(id)
        ON UPDATE CASCADE
        ON DELETE CASCADE,

    CONSTRAINT fk_detalle_producto
        FOREIGN KEY (producto_id)
        REFERENCES producto(id)
        ON UPDATE CASCADE
        ON DELETE RESTRICT
);
```

### Reglas de Integridad Referencial:
* `ON UPDATE CASCADE`: Si cambia el ID de una fila padre, los cambios impactan inmediatamente en sus claves foráneas.
* `ON DELETE RESTRICT`: Impide la eliminación de una categoría o producto si existen dependencias activas vinculadas a ellos en otras tablas del sistema.
* `ON DELETE CASCADE` (`pedido` ──> `detalle_pedido`): Si un pedido principal es eliminado, todos sus renglones e ítems asociados se remueven de forma automática para evitar registros huérfanos.

---

## 📁 Estructura del Proyecto

El código fuente se encuentra modularizado bajo el paquete principal `com.techlab.laperfu` y los recursos estáticos en `src/main/resources/static/`:

```text
src/main/java/com/techlab/laperfu/
│
├── config/
│   └── CorsConfig.java              # Configuración global de CORS y cabeceras HTTP
│
├── controller/
│   ├── CategoriaController.java     # Endpoints de consulta y carga de categorías
│   ├── ProductoController.java      # Endpoints CRUD, filtros y subida de imágenes (MultipartFile)
│   └── PedidoController.java        # Endpoints transaccionales, estados dinámicos y reportes
│
├── service/
│   ├── CategoriaService.java        # Lógica de negocio e integridad de categorías
│   └── ProductoService.java         # Lógica de negocio blindada, validaciones y mapeo de productos
│
├── repository/
│   ├── CategoriaRepository.java     # Acceso a datos de JPA para categorías
│   ├── ProductoRepository.java      # Acceso a datos y consultas filtradas de productos
│   └── PedidoRepository.java        # Acceso a datos y consultas filtradas de órdenes
│
├── dto/
│   ├── PedidoDTO.java               # DTO de entrada para procesar el carrito JSON
│   ├── PedidoConsultaDTO.java       # DTO de respuesta optimizado para reportes de pedidos
│   ├── DetallePedidoConsultaDTO.java# DTO de detalle por renglón (producto, cantidad y subtotal)
│   └── ProductoConsultaDTO.java     # DTO de respuesta limpio con el nombre de categoría
│
├── model/
│   ├── Categoria.java               # Entidad de persistencia Categoria
│   ├── Producto.java                # Entidad de persistencia Producto (Mapeo @ManyToOne)
│   ├── Pedido.java                  # Entidad Pedido (Mapeo @OneToMany)
│   └── DetallePedido.java           # Entidad intermedia de compra (Mapeo @ManyToOne)
│
├── util/
│   └── Validador.java               # Validaciones centralizadas de reglas de negocio (400 Bad Request)
│
└── exception/
    ├── GlobalExceptionHandler.java  # Capturador centralizado de errores API (400 / 404)
    ├── ProductoNoEncontradoException.java
    ├── CategoriaNoEncontradaException.java
    └── StockInsuficienteException.java

src/main/resources/static/
├── index.html                       # Tienda pública, catálogo y carrito de compras
├── pedidos.html                     # Panel de historial y gestión de estados de pedidos
├── admin.html                       # Backoffice visual para CRUD de productos y categorías
├── imagenes/                        # Almacenamiento de imágenes del catálogo
└── js/
    ├── script.js                    # Lógica de tienda, carrito persistente y checkout
    ├── pedidos.js                   # Lógica de reportes, filtros y cambio de estados
    └── admin.js                     # Lógica de gestión de inventario, subida de fotos y filtros
```

---

## 🖥️ Características del Front-End Integrado
* **Tienda Pública y Carrito Persistente (`index.html`):** Catálogo dinámico, gestión de cantidades en `LocalStorage` y asistente de checkout con cálculo automático de descuento del 10% en efectivo, pago neto con débito y financiación en cuotas fijas con tarjeta de crédito.
* **Backoffice / Panel de Administración (`admin.html`):** Interfaz visual para crear categorías y dar de alta, editar o eliminar productos en vivo. Incluye subida real de archivos de imagen desde la computadora (`FormData`), generación automática de nombres únicos en el servidor para evitar colisiones, vista previa en miniaturas en la tabla de inventario y filtros combinados por nombre y categoría.
* **Panel de Gestión de Pedidos (`pedidos.html`):** Visualización en tiempo real de las órdenes con desglose de productos comprados, máquina de estados cíclica (`PENDIENTE` ➔ `EN_PROCESO` ➔ `REALIZADO`), cancelación reversible (`CANCELADO` ➔ `Reactivar`) y actualización dinámica sin recargar la página.
* **Notificaciones No Intrusivas:** Confirmación de todas las operaciones mediante alertas flotantes (`Toast` de Bootstrap) con respaldo visual automático.

---

## 🚀 Guía de Pruebas Rápidas (REST Client)

Para testear los endpoints desde VS Code utilizando la extensión REST Client, podés usar el siguiente flujo secuencial dentro de tu archivo `.http`.

> ⚠️ **Importante**: Debido a la restricción `NOT NULL` de la clave foránea, primero debés ejecutar la petición para **crear la categoría** antes de poder agregar cualquier producto.

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
### ENDPOINTS DE PRODUCTOS (CRUD)
### ==========================================

### 3. Listar todos los productos
GET http://localhost:8080/producto
Accept: application/json

### 4. Crear un producto de prueba
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
  "descripcion": "Perfume modificado: notas de salida cítricas, pimienta negra y maderas.",
  "categoria": {
    "id": 1
  }
}

### 6. Eliminar un producto por ID (Ej: ID 7)
DELETE http://localhost:8080/producto/7


### ==========================================
### ENDPOINTS DE PEDIDOS Y ESTADOS 🛒
### ==========================================

### 7. Procesar y guardar un nuevo pedido (Simula el checkout del carrito)
POST http://localhost:8080/pedido
Content-Type: application/json

{
  "total": 350000.00,
  "metodoPago": "credito",
  "cuotas": 3,
  "items": [
    {
      "id": 4,
      "cantidad": 2,
      "precio": 4500.00
    },
    {
      "id": 5,
      "cantidad": 1,
      "precio": 250000.00
    }
  ]
}

### 8. Listar todo el historial de pedidos (con detalle de ítems)
GET http://localhost:8080/pedido
Accept: application/json


9. Cambiar el estado de un pedido de forma dinámica (PENDIENTE, EN_PROCESO, REALIZADO, CANCELADO)

PUT http://localhost:8080/pedido/1/estado?nuevoEstado=EN_PROCESO
Content-Type: application/json

==========================================


ENDPOINTS DE CONSULTAS Y FILTROS (DTOs 🔍)


==========================================


10. Filtrar productos por ID de Categoría

GET http://localhost:8080/producto/categoria/1
Accept: application/json

11. Filtrar pedidos por Estado (PENDIENTE, EN_PROCESO, REALIZADO, CANCELADO)

GET http://localhost:8080/pedido/filtrar/estado?estado=PENDIENTE
Accept: application/json

12. Filtrar pedidos por Método de Pago (ej: credito, efectivo)

GET http://localhost:8080/pedido/filtrar/pago?metodo=credito
Accept: application/json
```