# Sistema de Gestión de Productos y Pedidos - Talento Tech 🛒

Aplicación de consola corporativa en **Java** para la gestión de inventario y pedidos de productos de estética, desarrollada bajo los lineamientos de arquitectura limpia en Talento Tech.

## 🚀 Características Principales

- **Arquitectura Multicapa (Separación de Responsabilidades):** Desacoplamiento total entre la lógica de presentación (`menuConsola`) y la lógica de negocio (`ProductoService`), permitiendo una fácil transición futura hacia una API REST (Spring Boot).
- **CRUD de Productos:** Gestión completa y validación estricta de datos antes de impactar el almacenamiento en memoria.
- **Gestión de Pedidos (Carrito de Compras):** Creación de pedidos multi-producto mediante un flujo interactivo que gestiona colecciones de `LineaPedido` y sincroniza los cambios de inventario con el servicio global en tiempo real.
- **Descuentos Inteligentes:** Motor de facturación que calcula subtotales y aplica un 10% de descuento automático para pagos en **Efectivo**.
- **Modelado POO Avanzado:** Uso exhaustivo de herencia, polimorfismo, clases abstractas e interfaces (`Vendible`, `Etiquetables`) para modelar entidades específicas (`Perfume`, `Crema`, `Shampoo`).

## ⚠️ Manejo Avanzado de Excepciones

El sistema está blindado mediante un bloque global de captura que intercepta errores de entrada y excepciones de negocio personalizadas:
- `ProductoNoEncontradoException`: Disparada cuando un ID buscado no existe en el servicio.
- `StockInsuficienteException`: Previene la sobreventa validando la disponibilidad antes de confirmar el pedido.
- `StockInvalidoException` y `PrecioInvalidoException`: Lanzadas por la capa utilitaria `Validador` para evitar inconsistencias en el inventario o valores nulos/negativos.

## 🕹️ Ejemplo de Uso (Consola)

```text
===== MENÚ =====
1 - Agregar Producto
2 - Modificar Producto
3 - Buscar Producto
4 - Eliminar Producto
5 - Listar productos
6 - Crear Pedido
7 - Listar Pedidos Realizados
8 - Salir
Seleccione una opción: 1

--- AGREGAR PRODUCTO ---
1 - Perfume
2 - Crema
3 - Shampoo
Seleccione el tipo de producto: 1
Ingrese nombre: Chanel
Ingrese precio: 10000
Ingrese stock: 5
Ingrese tamaño del envase (ml): 100
✔ Producto agregado correctamente.
ID asignado: 1
```

## 📦 Estructura de Paquetes

- **`com.techlab.service`**: Capa de negocio pura (`ProductoService`). Centraliza las operaciones CRUD, el contador de IDs secuenciales de la aplicación y la invocación al `Validador`. No interactúa con la consola.
- **`com.techlab.productos`**: Clases base abstractas, subclases e interfaces de la lógica de dominio.
- **`com.techlab.pedidos`**: Entidades transaccionales (`Pedido`, `LineaPedido`) encargadas de procesar el carrito, el stock transitorio y los descuentos de facturación.
- **`com.techlab.util`**: Clases de soporte utilitario como el `Validador` de campos.
- **`com.techlab.excepciones`**: Excepciones personalizadas del dominio de la aplicación.
- **`com.techlab.talentotech`**: Capa de presentación y punto de entrada (`menuConsola`).

## 🛠️ Requisitos y Ejecución

1. Abrir la carpeta raíz en tu IDE de preferencia (VS Code, IntelliJ o Eclipse) con **Java JDK 17+**.
2. Compilar y ejecutar la clase principal `menuConsola.java`.

## 🧑‍💻 Desarrollado por

- **Gabriel Niotti** — Estudiante de Talento Tech.
