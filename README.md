# Sistema de Gestión de Productos y Pedidos - Talento Tech 🛒

Aplicación de consola corporativa en **Java** para la gestión de inventario y pedidos de productos de estética, desarrollada bajo los lineamientos de arquitectura limpia y patrones de diseño en Talento Tech.

## 🚀 Características Principales

- **Arquitectura Multicapa con Inyección de Dependencias:** Desacoplamiento total y modularización de la interfaz de usuario en controladores de vista independientes (`MenuProducto`, `MenuPedido`). Las dependencias de `Scanner` y los servicios se inyectan a través de los constructores desde un punto de entrada centralizado (`Main`), emulando el comportamiento nativo de frameworks como Spring Boot.
- **CRUD de Productos Desacoplado:** Gestión completa del inventario y validación estricta de datos antes de impactar el almacenamiento en memoria de `ProductoService`.
- **Gestión de Pedidos Centralizada:** Creación de pedidos multi-producto mediante un flujo interactivo en `MenuPedido` que gestiona colecciones de `LineaPedido`, delega persistencia a `PedidoService` y sincroniza la baja del inventario real en tiempo real tras la confirmación de la compra.
- **Motor de Facturación Inteligente:** Calcula subtotales por ítem y aplica de forma automática un **10% de descuento** sobre el importe total si el método de pago seleccionado es **Efectivo**.
- **Modelado POO Avanzado:** Uso riguroso de herencia, polimorfismo, clases abstractas e interfaces comerciales para procesar dinámicamente entidades específicas (`Perfume`, `Crema`, `Shampoo`).

## 🧬 Abstracción e Interfaces de Negocio

El diseño de dominio utiliza contratos mediante interfaces para garantizar que los productos cumplan con comportamientos comerciales específicos requeridos por la organización:

- **`Vendible`**: Obliga a las clases del catálogo a implementar el método `aplicarDescuento(Double porcentaje)`. Esto permite que el sistema aplique promociones específicas sobre el producto de forma aislada, notificando el impacto comercial de la reducción del precio.
- **`Etiquetables`**: Contrato técnico que exige la implementación del método `generarEtiqueta()`. Se utiliza dentro del bucle de listado y búsqueda del catálogo; el sistema detecta a través de polimorfismo (`instanceof`) si un producto cumple con este contrato para imprimir dinámicamente etiquetas personalizadas en consola que detallan los atributos únicos de la subclase (el Género en `Perfume`, el Tipo de Piel en `Crema` y el Tipo de Cabello en `Shampoo`).

## ⚠️ Manejo de Excepciones de Negocio

El sistema está blindado en su orquestador principal mediante un bloque global de captura que intercepta errores de formato en la entrada (`NumberFormatException`) y excepciones de negocio personalizadas de la aplicación:
- `ProductoNoEncontradoException`: Disparada cuando un ID buscado no existe en el catálogo de servicios.
- `StockInsuficienteException`: Previene la sobreventa transaccional validando la disponibilidad de inventario antes de confirmar ítems en un carrito.
- `StockInvalidoException` y `PrecioInvalidoException`: Lanzadas por la capa utilitaria `Validador` para evitar inconsistencias de valores negativos o nulos al registrar y modificar artículos.

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
Ingrese nombre: Chanel 5
Ingrese precio: 150000
Ingrese stock: 5
Ingrese género para el perfume (HOMBRE / MUJER): MUJER
  Producto agregado correctamente.
ID asignado: 1
```

## 📦 Estructura de Paquetes Modularizada

- **`com.techlab`**: Contiene la clase `Main.java`, la cual actúa como el único orquestador del flujo general, inicializador de recursos compartidos (como el buffer de consola) y capturador central de excepciones.
- **`com.techlab.ui`**: Capa de presentación pura (`MenuProducto`, `MenuPedido`). Clases responsables de la interacción interactiva, lecturas y delegación a servicios. No contienen lógica de negocio ni bucles de control principal.
- **`com.techlab.service`**: Capa de negocio pura (`ProductoService`, `PedidoService`). Centraliza las colecciones en memoria de la aplicación, asigna IDs secuenciales y protege las reglas CRUD. Está completamente desacoplada de la entrada por consola.
- **`com.techlab.productos`**: Clases base abstractas, subclases e interfaces de dominio (`Perfume` estructurado por Género, `Crema` por Tipo de Piel y `Shampoo` por Tipo de Cabello).
- **`com.techlab.pedidos`**: Entidades transaccionales (`Pedido`, `LineaPedido`) encargadas de procesar el carrito de compras, cálculo de subtotales y deducción de descuentos comerciales.
- **`com.techlab.util`**: Capa de soporte que aloja al `Validador` de campos, optimizado para lecturas seguras de flujos numéricos en consola mediante conversión de texto.
- **`com.techlab.excepciones`**: Excepciones personalizadas que extienden de `RuntimeException`.

## 🛠️ Requisitos y Guía de Ejecución por Terminal

### Requisitos Mínimos
- Tener instalado **Java JDK 17 o superior** (configurado en las variables de entorno del sistema).
- Consola bash o terminal integrada de VS Code abierta en la **carpeta raíz del proyecto** (donde se encuentra el archivo `Main.java` o la carpeta `src`).

### Comandos de Compilación y Ejecución Manual
Si necesitas limpiar binarios corruptos en la caché de tu entorno de desarrollo o quieres correr el proyecto directamente desde la terminal de VS Code, ejecuta los siguientes comandos ordenados:

1. **Limpiar binarios previos y compilar todas las capas del proyecto:**
   ```bash
   javac -d bin src/com/techlab/util/*.java src/com/techlab/excepciones/*.java src/com/techlab/productos/*.java src/com/techlab/service/*.java src/com/techlab/ui/*.java Main.java
   ```
   *(Este comando lee todos los archivos fuente `.java` en orden de dependencias y genera los archivos compilados limpios dentro de la carpeta `bin`)*.

2. **Ejecutar la aplicación modular:**
   ```bash
   java -cp bin Main
   ```
   *(Ejecuta el punto de entrada de la aplicación indicando que busque el motor de clases dentro del directorio `bin`)*.

## 🧑‍💻 Desarrollado por

- **Gabriel Niotti** — Estudiante de Talento Tech.
