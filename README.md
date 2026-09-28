# Sistema de Gestión de Productos y Pedidos - Talento Tech 🛒

Este proyecto es una aplicación de consola en **Java** desarrollada como parte de la cursada de Talento Tech. Modela un sistema de inventario y facturación para productos de estética y cuidado personal (Perfumes, Cremas y Shampoos), permitiendo la gestión del inventario (CRUD) y la simulación de compras mediante pedidos con control de stock y manejo de excepciones.

## 🚀 Características Principales

- **CRUD Completo de Productos:** Alta, baja, modificación, búsqueda y listado en memoria utilizando colecciones (`List` e `Iterator`).
- **Gestión de Pedidos:** Creación de carritos de compras validados mediante un objeto intermedio (`LineaPedido`).
- **Descuentos Inteligentes:** Implementación de un **10% de descuento automático** si el cliente selecciona abonar en **Efectivo**.
- **Manejo Avanzado de Excepciones:** Flujo protegido contra ingresos inválidos en consola (`NumberFormatException`) y control de stock mediante la excepción personalizada `StockInsuficienteException`.

## 📦 Estructura de Paquetes

El código se encuentra organizado bajo buenas prácticas de modularización:

- `com.techlab.productos`: Contiene la clase abstracta `Producto`, las subclases (`Perfume`, `Crema`, `Shampoo`) y las interfaces (`Vendible`, `Etiquetables`).
- `com.techlab.pedidos`: Contiene las clases `Pedido` y `LineaPedido` para la lógica de facturación.
- `com.techlab.excepciones`: Contiene la excepción personalizada `StockInsuficienteException`.
- `com.techlab.talentotech`: Aloja la clase principal ejecutable `menuConsola`.

## 🛠️ Requisitos y Ejecución

1. **Clonar el repositorio** o abrir la carpeta del proyecto en tu IDE preferido (recomendado: **Visual Studio Code** con la extensión *Extension Pack for Java*).
2. Asegurarte de tener instalado **Java JDK 17** o superior.
3. Ejecutar el archivo `menuConsola.java` para iniciar la interfaz interactiva.

## 🧑‍💻 Desarrollado por
- Gabriel Niotti / Estudiante de Talento Tech
