
// 1. DEFINICION DE VARIABLES GLOBALES

let productos = [];
let listaProductosCarrito = cargarCarritoDeStorage();


// 2. CONEXIÓN CON EL BACK-END EN SPRING BOOT (PUERTO 8080)

function obtenerProductosDelBackend() {
  const API_URL = 'http://localhost:8080/producto'; 

  fetch(API_URL)
    .then(response => {
      if (!response.ok) {
        throw new Error('No se pudo conectar con el servidor de Spring Boot');
      }
      return response.json(); 
    })
    .then(datos => {
      productos = datos; // Llenamos el array global con los perfumes de tu Base de Datos
      insertarProductos(productos); // Dibujamos las tarjetas en la pantalla
    })
    .catch(error => {
      console.error('Error al cargar los productos desde el back-end:', error);
    });
}

// =================================================================
// 3. LOGICA Y FUNCIONES DEL FRONT-END (LA PERFU)

// Función que inserta los productos dinámicamente en el HTML
function insertarProductos(lista) {
  const contenedorProductos = document.querySelector("#productos .row");
  if (!contenedorProductos) return;

  contenedorProductos.innerHTML = '<h2 class="productos w-100">Productos</h2>';

  for (const { id, img, nombre, descripcion, precio } of lista) {
    const imagenFinal = img ? img : 'logo.png'; 

    const nuevoElemento = document.createElement("div");
    nuevoElemento.className = "card col-10 col-sm-5 col-lg-3 m-2";
    nuevoElemento.innerHTML = `
                    <img src="./imagenes/${imagenFinal}" class="card-img-top" alt="${nombre}">
                    <div class="card-body text-center">
                        <h5 class="card-title">${nombre}</h5>
                        <p class="m-0">
                            <a 
                                class="link-info link-opacity-50-hover link-underline-opacity-0" 
                                href="#."
                                data-descripcion="${descripcion}"
                            >
                                Ver descripción
                            </a>
                        </p>
                        <div class="descripcion"></div>
                        <p class="card-text text-danger fs-3 mt-2">${precio.toLocaleString(
                          "es-AR",
                          { style: "currency", currency: "ARS" }
                        )}</p>
                        <button 
                            type="button" 
                            class="btn btn-dark pb-2 btn-agregar-carrito"
                            data-id="${id}"
                        >
                            Agregar al carrito
                        </button>
                    </div>
        `;
    contenedorProductos.appendChild(nuevoElemento);
  }
}

// Función para mostrar/ocultar la descripción del producto
function mostrarDescripcion(datosEvento) {
  const elementoEvento = datosEvento.target.tagName;
  if (elementoEvento === "A") {
    const elementoClicado = datosEvento.target;
    const descripcionProducto = elementoClicado.dataset.descripcion;
    const divCard = elementoClicado.closest(".card");
    const divDescripcion = divCard.querySelector(".descripcion");

    if (divDescripcion.children.length == 0) {
      const parrafoDescripcion = document.createElement("p");
      parrafoDescripcion.textContent = descripcionProducto;
      divDescripcion.appendChild(parrafoDescripcion);
      elementoClicado.textContent = "Ocultar descripción";
    } else {
      elementoClicado.textContent = "Ver descripción";
      divDescripcion.innerHTML = "";
    }
  }
}

// Busca un producto en la lista del carrito por su ID
function buscarEnLista(id, lista) {
  for (const producto of lista) {
    if (producto.id === id) return id;
  }
  return -1;
}

// Busca los datos completos de un producto por su ID
function buscarProductoPorId(id, lista) {
  for (let i = 0; i < lista.length; i++) {
    if (lista[i].id === id) return lista[i];
  }
  return null;
}

// Inserta de forma visual un producto dentro de la lista del carrito

function insertarProductoHTML(producto) {
  const listaCarrito = document.querySelector("#carrito .list-group");
  if (!listaCarrito) return;

  const li = document.createElement("li");
  li.className = "list-group-item d-flex justify-content-between align-items-center";
  li.id = `producto-${producto.id}`;

  li.innerHTML = `
        <span>
            <strong>${producto.nombre}</strong> <br>
            Precio: ${producto.precio.toLocaleString("es-AR", {
              style: "currency",
              currency: "ARS",
            })}
        </span>

        <div class="d-flex align-items-center gap-3">
            <!-- Controles de cantidad -->
            <div class="d-flex align-items-center gap-2">
                <button class="btn btn-sm btn-dark restar" data-id="${producto.id}">−</button>
                <span class="cantidad">${producto.cantidad}</span>
                <button class="btn btn-sm btn-dark sumar" data-id="${producto.id}">+</button>
            </div>

            <!-- Subtotal y Botón de Eliminar de un solo clic -->
            <div class="d-flex flex-column text-end" style="min-width: 80px;">
                <span class="subtotal text-danger fw-bold">
                    ${(producto.cantidad * producto.precio).toLocaleString("es-AR", { style: "currency", currency: "ARS" })}
                </span>
                <button type="button" class="btn btn-sm btn-link text-muted p-0 mt-1 eliminar-directo" data-id="${producto.id}" style="font-size: 0.8rem; text-decoration: none;">
                    <i class="fa fa-trash text-secondary eliminar-directo" data-id="${producto.id}"></i> Eliminar
                </button>
                </button>
            </div>
        </div>
    `;

  listaCarrito.appendChild(li);
}


// Actualiza el número de la burbuja del carrito
function actualizarContador() {
  const contenedorNumero = document.querySelector("#carrito .contador");
  if (!contenedorNumero) return;
  const totalCantidad = listaProductosCarrito.reduce((acc, p) => acc + p.cantidad, 0);
  contenedorNumero.textContent = totalCantidad;
}

// Calcula y muestra el total de la compra
function actualizarTotal() {
  const totalCarrito = document.getElementById("totalCarrito");
  if (!totalCarrito) return;
  const total = listaProductosCarrito.reduce((acc, p) => acc + (p.precio * p.cantidad), 0);
  totalCarrito.textContent = `Total: ${total.toLocaleString("es-AR", { style: "currency", currency: "ARS" })}`;
}

// Carga inicial del Storage
function cargarCarritoDeStorage() {
  const carrito = localStorage.getItem("listaCarrito");
  return carrito ? JSON.parse(carrito) : [];
}

// Guarda la lista del carrito en el Local Storage
function guardarCarritoEnStorage(lista) {
  localStorage.setItem("listaCarrito", JSON.stringify(lista));
}

// Función encargada de vaciar el carrito por completo
function eliminarCarrito() {
  listaProductosCarrito = [];
  localStorage.removeItem("listaCarrito");
  const listaCarritoHTML = document.querySelector("#carrito .list-group");
  if (listaCarritoHTML) listaCarritoHTML.innerHTML = "";
  actualizarContador();
  actualizarTotal();
}

// Actualiza un producto específico dentro del HTML del carrito
function actualizarProductoHTML(producto) {
  const idBuscado = "producto-" + producto.id;
  const li = document.getElementById(idBuscado);
  
  if (!li) return;
  
  li.querySelector(".cantidad").textContent = producto.cantidad;
  li.querySelector(".subtotal").textContent = (producto.precio * producto.cantidad).toLocaleString("es-AR", { 
    style: "currency", 
    currency: "ARS" 
  });
}


// Agrega un producto seleccionado al carrito
function agregarAlCarrito(datosEvento) {
  if (datosEvento.target.classList.contains("btn-agregar-carrito")) {
    const idProducto = parseInt(datosEvento.target.dataset.id);
    const idEncontrado = buscarEnLista(idProducto, listaProductosCarrito);

    if (idEncontrado === -1) {
      const productoEncontrado = buscarProductoPorId(idProducto, productos);
      if (!productoEncontrado) return;

      const prodCarrito = {
        id: productoEncontrado.id,
        nombre: productoEncontrado.nombre,
        precio: productoEncontrado.precio,
        cantidad: 1
      };

      listaProductosCarrito.push(prodCarrito);
      insertarProductoHTML(prodCarrito);
    } else {
      const producto = listaProductosCarrito.find(p => p.id === idProducto);
      producto.cantidad++;
      actualizarProductoHTML(producto);
    }
    actualizarContador();
    actualizarTotal();
    guardarCarritoEnStorage(listaProductosCarrito);
  }
}

// Variables de control para el proceso de pago
let modalCheckout = null;
let subtotalCompra = 0;

function abrirModalCheckout() {
  if (listaProductosCarrito.length === 0) {
    alert("El carrito está vacío.");
    return;
  }

  // Calculamos el subtotal base de los productos
  subtotalCompra = listaProductosCarrito.reduce((acc, p) => acc + (p.precio * p.cantidad), 0);
  
  // Inicializamos la modal de Bootstrap si no está creada
  if (!modalCheckout) {
    modalCheckout = new bootstrap.Modal(document.getElementById('modalExitoCompra'));
  }

  // Actualizamos los textos iniciales en la pantalla emergente
  document.getElementById("resumenSubtotal").textContent = `Subtotal Carrito: ${subtotalCompra.toLocaleString("es-AR", { style: "currency", currency: "ARS" })}`;
  document.getElementById("selectMetodoPago").value = "efectivo";
  document.getElementById("bloqueCuotas").classList.add("d-none");
  
  calcularMontoFinalYCuotas();
  modalCheckout.show();
}

function calcularMontoFinalYCuotas() {
  const metodo = document.getElementById("selectMetodoPago").value;
  const selectorCuotas = document.getElementById("selectCuotas");
  const bloqueCuotas = document.getElementById("bloqueCuotas");
  const textoCuota = document.getElementById("textoValorCuota");
  const totalLabel = document.getElementById("totalFinalPago");

  let totalFinal = subtotalCompra;
  bloqueCuotas.classList.add("d-none");
  textoCuota.textContent = "";

  if (metodo === "efectivo") {
    totalFinal = subtotalCompra * 0.90; // Aplica el 10% de descuento 🎉
  } else if (metodo === "credito") {
    bloqueCuotas.classList.remove("d-none");
    const cuotas = parseInt(selectorCuotas.value);
    
    // Calculamos el valor de cada cuota dividiendo el total neto
    const valorCuota = totalFinal / cuotas;
    textoCuota.textContent = `${cuotas} cuotas de ${valorCuota.toLocaleString("es-AR", { style: "currency", currency: "ARS" })} por mes.`;
  }

  // Guardamos el número final en una propiedad del elemento para leerlo al enviar
  totalLabel.dataset.valor = totalFinal;
  totalLabel.textContent = totalFinal.toLocaleString("es-AR", { style: "currency", currency: "ARS" });
}

function procesarPagoYEnviarBackend() {
  const metodoPago = document.getElementById("selectMetodoPago").value;
  const cantidadCuotas = metodoPago === "credito" ? parseInt(document.getElementById("selectCuotas").value) : 1;
  const montoFinal = parseFloat(document.getElementById("totalFinalPago").dataset.valor);

  const datosPedido = {
    total: montoFinal,
    metodoPago: metodoPago,
    cuotas: cantidadCuotas,
    items: listaProductosCarrito.map(p => ({
      id: p.id,
      cantidad: p.cantidad,
      precio: p.precio
    }))
  };

  fetch('http://localhost:8080/pedido', {
    method: 'POST',
    headers: { 'Content-Type': 'application/json' },
    body: JSON.stringify(datosPedido)
  })
  .then(response => {
    if (!response.ok) throw new Error("Error en el servidor al guardar el pedido");
    return response.json();
  })
    .then(resultado => {
    if (resultado.status === "success") {
      modalCheckout.hide();
      eliminarCarrito(); // Limpia la pantalla y el LocalStorage

      // Dispara la notificación flotante de Bootstrap
      const toastEl = document.getElementById('toastCompraExitosa');
      if (toastEl) {
        const toastBootstrap = bootstrap.Toast.getOrCreateInstance(toastEl, { delay: 4500 });
        toastBootstrap.show();
      }
    }
  })

  .catch(error => {
    console.error("Error en la transacción:", error);
    alert("Hubo un problema al procesar el pago.");
  });
}



// ======================================================================================
// 4. INICIALIZACION SEGURO DE LISTENERS (PREVIENE TODOS LOS ERRORES DE LA CONSOLA)
// ======================================================================================
window.addEventListener("DOMContentLoaded", () => {
  // 🚀 Buscamos los productos en Java
  obtenerProductosDelBackend();

  // Listener para descripciones y agregado de productos
  const contenedorProductos = document.querySelector("#productos .row");
  if (contenedorProductos) {
    contenedorProductos.addEventListener("click", mostrarDescripcion);
    contenedorProductos.addEventListener("click", agregarAlCarrito);
  }

  // Listener del botón vaciar carrito
  const botonVaciarCarrito = document.querySelector("#carrito .vaciarCarrito");
  if (botonVaciarCarrito) {
    botonVaciarCarrito.addEventListener("click", () => {
      eliminarCarrito();
    });
  }

  // Cargar elementos persistentes del LocalStorage si existen
  if (listaProductosCarrito.length !== 0) {
    for (const producto of listaProductosCarrito) {
      insertarProductoHTML(producto);
    }
    actualizarContador();
    actualizarTotal();
  }

  // 🛒 Listeners para los botones individuales (+ y −) dentro del carrito (CORREGIDO)
   // 🛒 Listeners para los botones individuales (+, − y ELIMINAR DIRECTO) dentro del carrito
  const listaCarrito = document.querySelector("#carrito .list-group");
  if (listaCarrito) {
    listaCarrito.addEventListener("click", function (e) {
      const id = parseInt(e.target.dataset.id);
      if (!id) return;
      
      const producto = listaProductosCarrito.find((p) => p.id === id);

      // Opción A: Botón Sumar (+)
      if (e.target.classList.contains("sumar")) {
        producto.cantidad++;
      }

      // Opción B: Botón Restar (−)
      if (e.target.classList.contains("restar")) {
        producto.cantidad--;

        if (producto.cantidad === 0) {
          listaProductosCarrito = listaProductosCarrito.filter((p) => p.id !== id);
          const itemHTML = document.getElementById("producto-" + id);
          if (itemHTML) itemHTML.remove();
        }
      }

      // BOTÓN ELIMINAR producto completo con un solo clic (nuevo)
      if (e.target.classList.contains("eliminar-directo")) {
        // Removemos el producto por completo de la lista global
        listaProductosCarrito = listaProductosCarrito.filter((p) => p.id !== id);
        
        // Lo borramos visualmente de la pantalla
        const itemHTML = document.getElementById("producto-" + id);
        if (itemHTML) itemHTML.remove();
      }

      // Actualizamos los totales y el LocalStorage
      if (producto && producto.cantidad > 0) {
        actualizarProductoHTML(producto);
      }

         // Cambiamos el comportamiento del botón original del carrito para que abra la nueva modal
  const botonFinalizar = document.querySelector(".finalizarCompra");
  if (botonFinalizar) {
    // 🔄 Cambiado aquí para que primero abra el asistente de pagos
    botonFinalizar.addEventListener("click", abrirModalCheckout); 
  }

  // 🚀 AGREGA ESTOS NUEVOS LISTENERS INTERACTIVOS AQUÍ ADENTRO:
  const selectPago = document.getElementById("selectMetodoPago");
  if (selectPago) {
    selectPago.addEventListener("change", calcularMontoFinalYCuotas);
  }

  const selectCuotas = document.getElementById("selectCuotas");
  if (selectCuotas) {
    selectCuotas.addEventListener("change", calcularMontoFinalYCuotas);
  }

  const btnConfirmarFinal = document.getElementById("btnConfirmarPagoFinal");
  if (btnConfirmarFinal) {
    btnConfirmarFinal.addEventListener("click", procesarPagoYEnviarBackend);
  }

  // =========================================================
// INICIALIZACIÓN DE EVENTOS DEL CHECKOUT Y PAGOS
// =========================================================
document.addEventListener("DOMContentLoaded", () => {
 // =========================================================
// INICIALIZACIÓN INMEDIATA DE EVENTOS DEL CHECKOUT Y PAGOS
// =========================================================

// 1. Botón "Finalizar Compra" en el carrito
document.addEventListener("click", (e) => {
  if (e.target.closest(".finalizarCompra")) {
    abrirModalCheckout();
  }
});

// 2. Cambio en el método de pago (Efectivo, Débito, Crédito)
const selectMetodo = document.getElementById("selectMetodoPago");
if (selectMetodo) {
  selectMetodo.addEventListener("change", calcularMontoFinalYCuotas);
}

// 3. Cambio en la cantidad de cuotas (1, 3, 6)
const selectCuotas = document.getElementById("selectCuotas");
if (selectCuotas) {
  selectCuotas.addEventListener("change", calcularMontoFinalYCuotas);
}

// 4. Botón "Confirmar y Pagar" dentro del Modal
const btnConfirmar = document.getElementById("btnConfirmarPagoFinal");
if (btnConfirmar) {
  btnConfirmar.addEventListener("click", procesarPagoYEnviarBackend);
}

});




      actualizarContador();
      actualizarTotal();
      guardarCarritoEnStorage(listaProductosCarrito);
    });
  } 
});