# Sistema de Gestión de Productos y Pedidos - Talento Tech 🛒

Aplicación de consola en **Java** para la gestión de inventario y pedidos de productos de estética, desarrollada en Talento Tech.

## 🚀 Características Principales

- **CRUD de Productos:** Gestión completa en memoria usando colecciones (`List`, `Iterator`).
- **Gestión de Pedidos:** Creación de pedidos mediante `LineaPedido` validando stock en tiempo real.
- **Descuentos Inteligentes:** 10% de descuento automático en pagos en **Efectivo**.
- **Modelado POO:** Uso de clases abstractas, herencia, encapsulamiento e interfaces (`Vendible`, `Etiquetables`).

## ⚠️ Manejo Avanzado de Excepciones

- `ProductoNoEncontradoException`: ID inexistente.
- `StockInsuficienteException`: Supera el stock disponible.
- `StockInvalidoException` y `PrecioInvalidoException`: Previenen valores negativos o nulos.

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
✅ Producto agregado correctamente.
ID asignado: 1
```

## 📦 Estructura de Paquetes

- `com.techlab.productos`: Clases base, subclases (`Perfume`, `Crema`, `Shampoo`) e interfaces.
- `com.techlab.pedidos`: Lógica de transacciones y facturación.
- `com.techlab.excepciones`: Excepciones personalizadas.
- `com.techlab.talentotech`: Punto de entrada (`menuConsola`).

## 🛠️ Requisitos y Ejecución

1. Abrir en tu IDE (VS Code o Eclipse) con **Java JDK 17+**.
2. Ejecutar el archivo `menuConsola.java`.

## 🧑‍💻 Desarrollado por

- **Gabriel Niotti** — Estudiante de Talento Tech.
