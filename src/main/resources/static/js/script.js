// =================================================================
// 1. DEFINICIÓN DE VARIABLES GLOBALES
// =================================================================
let productos = [];
let listaProductosCarrito = cargarCarritoDeStorage();
let modalCheckout = null;
let subtotalCompra = 0;

// =================================================================
// 2. CONEXIÓN CON EL BACK-END EN SPRING BOOT (PUERTO 8080)
// =================================================================
function obtenerProductosDelBackend() {
  const API_URL = 'http://localhost:8080/producto';
  const contenedorProductos = document.querySelector("#productos .row");

  if (contenedorProductos) {
    contenedorProductos.innerHTML =
      '<h2 class="productos w-100">Productos</h2>' +
      '<div class="col-12 py-5 text-white">' +
        '<div class="spinner-border text-light" role="status">' +
          '<span class="visually-hidden">Cargando...</span>' +
        '</div>' +
        '<div class="small mt-2">Cargando fragancias desde el servidor...</div>' +
      '</div>';
  }

  fetch(API_URL)
    .then(response => {
      if (!response.ok) {
        throw new Error('No se pudo conectar con el servidor de Spring Boot');
      }
      return response.json();
    })
    .then(datos => {
      productos = datos;
      insertarProductos(productos);
    })
    .catch(error => {
      console.error('Error al cargar los productos desde el back-end:', error);
      if (contenedorProductos) {
        contenedorProductos.innerHTML =
          '<h2 class="productos w-100">Productos</h2>' +
          '<p class="text-warning fw-bold py-4">No se pudo conectar con el servidor. Verificá que Spring Boot esté activo.</p>';
      }
    });
}

// =================================================================
// 3. LÓGICA DE CATÁLOGO Y CARRITO (LA PERFU)
// =================================================================
function insertarProductos(lista) {
  const contenedorProductos = document.querySelector("#productos .row");
  if (!contenedorProductos) return;

  contenedorProductos.innerHTML = '<h2 class="productos w-100">Productos</h2>';

  for (const { id, img, nombre, descripcion, precio, stock, categoria } of lista) {
    const imagenFinal = img ? img : 'logo.png';
    const srcImagen = (imagenFinal.startsWith('http://') || imagenFinal.startsWith('https://'))
      ? imagenFinal
      : './imagenes/' + imagenFinal;

    const stockNum = stock !== undefined && stock !== null ? stock : 0;
    const sinStock = stockNum <= 0;
    const descTexto = descripcion || 'Fragancia exclusiva de La Perfu.';
    const nombreCategoria = categoria && categoria.nombre ? categoria.nombre : 'General';
    const precioFormateado = precio.toLocaleString("es-AR", { style: "currency", currency: "ARS" });
    const claseBoton = sinStock ? 'btn-secondary' : 'btn-dark';
    const attrDisabled = sinStock ? 'disabled' : '';
    const textoBoton = sinStock ? 'Sin stock' : 'Agregar al carrito';
    const textoStock = sinStock
      ? '<span class="badge bg-danger">Sin stock (0 un.)</span>'
      : '<span class="badge bg-success-subtle text-success border border-success-subtle">Stock: ' + stockNum + ' un.</span>';

    const nuevoElemento = document.createElement("div");
    nuevoElemento.className = "card col-10 col-sm-5 col-lg-3 m-2";
    nuevoElemento.innerHTML =
      '<img src="' + srcImagen + '" onerror="this.src=\'./imagenes/logo.png\'" class="card-img-top" alt="' + nombre + '">' +
      '<div class="card-body text-center">' +
        '<div class="d-flex justify-content-between align-items-center mb-2">' +
          '<span class="badge text-bg-light border text-secondary">#' + id + '</span>' +
          '<span class="badge text-bg-light border text-dark">' + nombreCategoria + '</span>' +
        '</div>' +
        '<h5 class="card-title">' + nombre + '</h5>' +
        '<p class="m-0">' +
          '<a class="link-info link-opacity-50-hover link-underline-opacity-0" href="#." data-descripcion="' + descTexto + '">' +
            'Ver descripción' +
          '</a>' +
        '</p>' +
        '<div class="descripcion"></div>' +
        '<p class="card-text text-danger fs-3 mt-2 mb-1">' + precioFormateado + '</p>' +
        '<div class="mb-3">' + textoStock + '</div>' +
        '<button type="button" class="btn ' + claseBoton + ' pb-2 btn-agregar-carrito" data-id="' + id + '" ' + attrDisabled + '>' +
          textoBoton +
        '</button>' +
      '</div>';
    contenedorProductos.appendChild(nuevoElemento);
  }
}


function mostrarDescripcion(datosEvento) {
  if (datosEvento.target.tagName === "A") {
    datosEvento.preventDefault();
    const elementoClicado = datosEvento.target;
    const descripcionProducto = elementoClicado.dataset.descripcion;
    const divCard = elementoClicado.closest(".card");
    const divDescripcion = divCard.querySelector(".descripcion");

    if (divDescripcion.children.length === 0) {
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

function buscarEnLista(id, lista) {
  for (const producto of lista) {
    if (producto.id === id) return id;
  }
  return -1;
}

function buscarProductoPorId(id, lista) {
  for (let i = 0; i < lista.length; i++) {
    if (lista[i].id === id) return lista[i];
  }
  return null;
}

function insertarProductoHTML(producto) {
  const listaCarrito = document.querySelector("#carrito .list-group");
  if (!listaCarrito) return;

  const li = document.createElement("li");
  li.className = "list-group-item d-flex justify-content-between align-items-center";
  li.id = "producto-" + producto.id;

  const precioUnit = producto.precio.toLocaleString("es-AR", { style: "currency", currency: "ARS" });
  const subTotal = (producto.cantidad * producto.precio).toLocaleString("es-AR", { style: "currency", currency: "ARS" });

  li.innerHTML =
    '<span>' +
      '<strong>' + producto.nombre + '</strong> <br>' +
      'Precio: ' + precioUnit +
    '</span>' +
    '<div class="d-flex align-items-center gap-3">' +
      '<div class="d-flex align-items-center gap-2">' +
        '<button class="btn btn-sm btn-dark restar" data-id="' + producto.id + '">−</button>' +
        '<span class="cantidad">' + producto.cantidad + '</span>' +
        '<button class="btn btn-sm btn-dark sumar" data-id="' + producto.id + '">+</button>' +
      '</div>' +
      '<div class="d-flex flex-column text-end" style="min-width: 80px;">' +
        '<span class="subtotal text-danger fw-bold">' + subTotal + '</span>' +
        '<button type="button" class="btn btn-sm btn-link text-muted p-0 mt-1 eliminar-directo" data-id="' + producto.id + '" style="font-size: 0.8rem; text-decoration: none;">' +
          '<i class="fa fa-trash text-secondary eliminar-directo" data-id="' + producto.id + '"></i> Eliminar' +
        '</button>' +
      '</div>' +
    '</div>';

  listaCarrito.appendChild(li);
}

function actualizarContador() {
  const contenedorNumero = document.querySelector("#carrito .contador");
  if (!contenedorNumero) return;
  const totalCantidad = listaProductosCarrito.reduce((acc, p) => acc + p.cantidad, 0);
  contenedorNumero.textContent = totalCantidad;
}

function actualizarTotal() {
  const totalCarrito = document.getElementById("totalCarrito");
  if (!totalCarrito) return;
  const total = listaProductosCarrito.reduce((acc, p) => acc + (p.precio * p.cantidad), 0);
  totalCarrito.textContent = "Total: " + total.toLocaleString("es-AR", { style: "currency", currency: "ARS" });
}

function cargarCarritoDeStorage() {
  const carrito = localStorage.getItem("listaCarrito");
  return carrito ? JSON.parse(carrito) : [];
}

function guardarCarritoEnStorage(lista) {
  localStorage.setItem("listaCarrito", JSON.stringify(lista));
}

function eliminarCarrito() {
  listaProductosCarrito = [];
  localStorage.removeItem("listaCarrito");
  const listaCarritoHTML = document.querySelector("#carrito .list-group");
  if (listaCarritoHTML) listaCarritoHTML.innerHTML = "";
  actualizarContador();
  actualizarTotal();
}

function actualizarProductoHTML(producto) {
  const li = document.getElementById("producto-" + producto.id);
  if (!li) return;
  li.querySelector(".cantidad").textContent = producto.cantidad;
  li.querySelector(".subtotal").textContent = (producto.precio * producto.cantidad).toLocaleString("es-AR", {
    style: "currency",
    currency: "ARS"
  });
}

function agregarAlCarrito(datosEvento) {
  if (datosEvento.target.classList.contains("btn-agregar-carrito")) {
    const idProducto = parseInt(datosEvento.target.dataset.id);
    const productoEncontrado = buscarProductoPorId(idProducto, productos);
    if (!productoEncontrado) return;

    if (productoEncontrado.stock <= 0) {
      alert("Lo sentimos, este producto ya no tiene stock disponible.");
      return;
    }

    const idEncontrado = buscarEnLista(idProducto, listaProductosCarrito);

    if (idEncontrado === -1) {
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
      if (producto.cantidad >= productoEncontrado.stock) {
        alert("Alcanzaste el stock máximo disponible de " + producto.nombre + " (" + productoEncontrado.stock + " un.).");
        return;
      }
      producto.cantidad++;
      actualizarProductoHTML(producto);
    }
    actualizarContador();
    actualizarTotal();
    guardarCarritoEnStorage(listaProductosCarrito);
  }
}
function agregarAlCarrito(datosEvento) {
  if (datosEvento.target.classList.contains("btn-agregar-carrito")) {
    const idProducto = parseInt(datosEvento.target.dataset.id);
    const productoEncontrado = buscarProductoPorId(idProducto, productos);
    if (!productoEncontrado) return;

    if (productoEncontrado.stock <= 0) {
      alert("Lo sentimos, este producto ya no tiene stock disponible.");
      return;
    }

    const idEncontrado = buscarEnLista(idProducto, listaProductosCarrito);

    if (idEncontrado === -1) {
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
      if (producto.cantidad >= productoEncontrado.stock) {
        alert("Alcanzaste el stock máximo disponible de " + producto.nombre + " (" + productoEncontrado.stock + " un.).");
        return;
      }
      producto.cantidad++;
      actualizarProductoHTML(producto);
    }
    actualizarContador();
    actualizarTotal();
    guardarCarritoEnStorage(listaProductosCarrito);
  }
}


// =================================================================
// 4. CHECKOUT, FINANCIACIÓN Y ENVÍO DE PEDIDO CON TOKEN JWT
// =================================================================
function abrirModalCheckout() {
  if (listaProductosCarrito.length === 0) {
    alert("El carrito está vacío.");
    return;
  }

  if (typeof obtenerToken === "function" && !obtenerToken()) {
    alert("Debés iniciar sesión para finalizar tu compra. Tu carrito quedará guardado.");
    window.location.href = "login.html";
    return;
  }

  subtotalCompra = listaProductosCarrito.reduce((acc, p) => acc + (p.precio * p.cantidad), 0);

  if (!modalCheckout) {
    modalCheckout = new bootstrap.Modal(document.getElementById('modalExitoCompra'));
  }

  document.getElementById("resumenSubtotal").textContent =
    "Subtotal Carrito: " + subtotalCompra.toLocaleString("es-AR", { style: "currency", currency: "ARS" });
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
totalFinal = subtotalCompra * 0.90;
} else if (metodo === "credito") {
bloqueCuotas.classList.remove("d-none");
const cuotas = parseInt(selectorCuotas.value);
const valorCuota = totalFinal / cuotas;
const cuotaFormateada = valorCuota.toLocaleString("es-AR", { style: "currency", currency: "ARS" });
textoCuota.textContent = cuotas + " cuotas de " + cuotaFormateada + " por mes.";
}
totalLabel.dataset.valor = totalFinal;
totalLabel.textContent = totalFinal.toLocaleString("es-AR", { style: "currency", currency: "ARS" });
}
async function procesarPagoYEnviarBackend() {
const metodoPago = document.getElementById("selectMetodoPago").value;
const cuotas = metodoPago === "credito" ? parseInt(document.getElementById("selectCuotas").value) : 1;
const totalFinal = parseFloat(document.getElementById("totalFinalPago").dataset.valor);
const pedidoDTO = {
total: totalFinal,
metodoPago: metodoPago,
cuotas: cuotas,
items: listaProductosCarrito.map(item => ({
id: item.id,
cantidad: item.cantidad,
precio: item.precio
}))
};
try {
const response = await fetch("http://localhost:8080/pedido", {
method: "POST",
headers: typeof getAuthHeaders === "function"
? getAuthHeaders(true)
: { "Content-Type": "application/json" },
body: JSON.stringify(pedidoDTO)
});
const data = await response.json().catch(() => ({}));
if (!response.ok) {
throw new Error(data.error || data.mensaje || "No se pudo procesar el pedido.");
}
if (modalCheckout) modalCheckout.hide();
eliminarCarrito();
alert("¡Compra realizada con éxito! Tu pedido fue registrado y el stock actualizado.");
obtenerProductosDelBackend();
} catch (error) {
alert("Error al confirmar el pedido: " + error.message);
}
}
// =================================================================
// 5. CONTROL DE BARRA DE SESIÓN Y EVENTOS INICIALES
// =================================================================
function actualizarNavbarSesion() {
  if (typeof obtenerUsuarioActual !== "function") return;

  const usuario = obtenerUsuarioActual();
  const navPedidos = document.getElementById("navPedidosLink");
  const navAdmin = document.getElementById("navAdminLink");
  const navAuth = document.getElementById("navAuthArea");

  if (!usuario) {
    if (navPedidos) navPedidos.classList.add("d-none");
    if (navAdmin) navAdmin.classList.add("d-none");
    if (navAuth) {
      navAuth.innerHTML = '<a href="login.html">Ingresar</a>';
    }
    return;
  }

  if (navPedidos) navPedidos.classList.remove("d-none");

  if (navAdmin) {
    if (usuario.rol === "ADMIN") {
      navAdmin.classList.remove("d-none");
    } else {
      navAdmin.classList.add("d-none");
    }
  }

  if (navAuth) {
    navAuth.innerHTML = '<a href="#" onclick="cerrarSesion()">Salir (' + usuario.email + ')</a>';
  }
}


window.addEventListener("DOMContentLoaded", () => {
actualizarNavbarSesion();
obtenerProductosDelBackend();
const contenedorProductos = document.querySelector("#productos .row");
if (contenedorProductos) {
contenedorProductos.addEventListener("click", mostrarDescripcion);
contenedorProductos.addEventListener("click", agregarAlCarrito);
}
const botonVaciarCarrito = document.querySelector("#carrito .vaciarCarrito");
if (botonVaciarCarrito) {
botonVaciarCarrito.addEventListener("click", eliminarCarrito);
}
if (listaProductosCarrito.length !== 0) {
for (const producto of listaProductosCarrito) {
insertarProductoHTML(producto);
}
actualizarContador();
actualizarTotal();
}
const listaCarrito = document.querySelector("#carrito .list-group");
if (listaCarrito) {
listaCarrito.addEventListener("click", function (e) {
const id = parseInt(e.target.dataset.id);
if (!id) return;
const producto = listaProductosCarrito.find((p) => p.id === id);
      if (e.target.classList.contains("sumar")) {
        const productoCatalogo = buscarProductoPorId(id, productos);
        const stockMaximo = productoCatalogo ? productoCatalogo.stock : 999;

        if (producto.cantidad >= stockMaximo) {
          alert("No podés agregar más unidades. Stock disponible de " + producto.nombre + ": " + stockMaximo + " un.");
          return;
        }
        producto.cantidad++;
      }

if (e.target.classList.contains("restar")) {
producto.cantidad--;
if (producto.cantidad === 0) {
listaProductosCarrito = listaProductosCarrito.filter((p) => p.id !== id);
const itemHTML = document.getElementById("producto-" + id);
if (itemHTML) itemHTML.remove();
}
}
if (e.target.classList.contains("eliminar-directo")) {
listaProductosCarrito = listaProductosCarrito.filter((p) => p.id !== id);
const itemHTML = document.getElementById("producto-" + id);
if (itemHTML) itemHTML.remove();
}
if (producto && producto.cantidad > 0) {
actualizarProductoHTML(producto);
}
actualizarContador();
actualizarTotal();
guardarCarritoEnStorage(listaProductosCarrito);
});
}
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
});