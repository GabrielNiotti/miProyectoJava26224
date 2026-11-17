const API_PRODUCTOS = 'http://localhost:8080/producto';
const API_CATEGORIAS = 'http://localhost:8080/categoria';

let listaProductosGlobal = [];
let listaCategoriasGlobal = [];

// Función preparada para JWT: envía el token en cada petición si el usuario inició sesión
function getAuthHeaders(incluirJson = true) {
  const headers = {};
  if (incluirJson) {
    headers['Content-Type'] = 'application/json';
  }
  const token = localStorage.getItem('tokenJWT');
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

// Notificación flotante unificada (Toast Bootstrap)
function mostrarNotificacion(mensaje, tipo = 'dark') {
  const toastEl = document.getElementById('toastAdmin');
  const toastMsg = document.getElementById('toastAdminMensaje');
  if (!toastEl || !toastMsg) return;

  toastEl.className = `toast align-items-center text-bg-${tipo} border-0 shadow-lg`;
  toastMsg.textContent = mensaje;

  if (typeof bootstrap !== 'undefined') {
    const toast = bootstrap.Toast.getOrCreateInstance(toastEl, { delay: 3500 });
    toast.show();
  } else {
    // Respaldo visual flotante puro sin usar alert() del navegador
    toastEl.classList.add('show');
    setTimeout(() => toastEl.classList.remove('show'), 3500);
  }
}



// ==========================================
// 1. GESTIÓN DE CATEGORÍAS
// ==========================================
function cargarCategorias() {
  fetch(API_CATEGORIAS, { headers: getAuthHeaders(false) })
    .then(res => {
      if (!res.ok) throw new Error('Error al obtener categorías');
      return res.json();
    })
    .then(categorias => {
      listaCategoriasGlobal = categorias;
      const selectForm = document.getElementById('prodCategoria');
      const selectFiltro = document.getElementById('filtroCategoria');

      if (selectForm) {
        selectForm.innerHTML = '<option value="">Seleccioná una categoría...</option>';
        categorias.forEach(cat => {
          selectForm.innerHTML += `<option value="${cat.id}">${cat.nombre}</option>`;
        });
      }

      if (selectFiltro) {
        selectFiltro.innerHTML = '<option value="TODAS">Todas las categorías</option>';
        categorias.forEach(cat => {
          selectFiltro.innerHTML += `<option value="${cat.id}">${cat.nombre}</option>`;
        });
      }
    })
    .catch(err => console.error('Error al cargar categorías:', err));
}

function crearCategoria(e) {
  e.preventDefault();
  const inputNombre = document.getElementById('nombreCategoria');
  const nombre = inputNombre.value.trim();
  if (!nombre) return;

  fetch(API_CATEGORIAS, {
    method: 'POST',
    headers: getAuthHeaders(true),
    body: JSON.stringify({ nombre })
  })
    .then(async res => {
      if (!res.ok) {
        const errorText = await res.text();
        throw new Error(errorText || 'No se pudo crear la categoría');
      }
      return res.json();
    })
    .then(() => {
      inputNombre.value = '';
      mostrarNotificacion('Categoría creada con éxito', 'success');
      cargarCategorias(); // Refresca los selectores en vivo
    })
    .catch(err => mostrarNotificacion(err.message, 'danger'));
}

// ==========================================
// 2. CRUD Y FILTROS DE PRODUCTOS
// ==========================================
function cargarProductos(url = API_PRODUCTOS) {
  const tbody = document.getElementById('tablaAdminProductos');
  if (!tbody) return;

  fetch(url, { headers: getAuthHeaders(false) })
    .then(res => {
      if (!res.ok) throw new Error('Error al obtener productos');
      return res.json();
    })
    .then(productos => {
      listaProductosGlobal = productos;
      renderizarTablaProductos(productos);
    })
    .catch(err => {
      console.error('Error al cargar productos:', err);
      tbody.innerHTML = '<tr><td colspan="6" class="text-center text-danger py-3">Error al conectar con el servidor. Verificá que Spring Boot esté corriendo.</td></tr>';
    });
}

function renderizarTablaProductos(productos) {
  const tbody = document.getElementById('tablaAdminProductos');
  tbody.innerHTML = '';

  if (productos.length === 0) {
    tbody.innerHTML = '<tr><td colspan="6" class="text-center text-muted py-4">No se encontraron productos.</td></tr>';
    return;
  }

    productos.forEach(prod => {
    const nombreCategoria = prod.categoria ? prod.categoria.nombre : 'Sin categoría';
    const badgeStock = prod.stock > 0 
      ? `<span class="badge bg-success">${prod.stock} un.</span>` 
      : `<span class="badge bg-danger">Sin stock</span>`;

    // Si el producto no tiene imagen asignada, usa logo.png como respaldo
    const rutaImagen = prod.img ? `./imagenes/${prod.img}` : './imagenes/logo.png';

    const tr = document.createElement('tr');
    tr.innerHTML = `
      <td class="fw-bold text-secondary">#${prod.id}</td>
      <td>
        <div class="d-flex align-items-center gap-3">
          <img src="${rutaImagen}" alt="${prod.nombre}" 
               onerror="this.src='./imagenes/logo.png'"
               style="width: 48px; height: 48px; object-fit: cover; border-radius: 8px; border: 1px solid #dee2e6;">
          <div>
            <div class="fw-semibold">${prod.nombre}</div>
            <small class="text-muted">${prod.descripcion || ''}</small>
          </div>
        </div>
      </td>
      <td><span class="badge text-bg-light border">${nombreCategoria}</span></td>
      <td class="fw-bold text-success">${prod.precio.toLocaleString('es-AR', { style: 'currency', currency: 'ARS' })}</td>
      <td>${badgeStock}</td>
      <td class="text-end">
        <div class="d-flex justify-content-end gap-1">
          <button type="button" class="btn btn-sm btn-outline-primary btn-editar" data-id="${prod.id}" title="Editar">
            <i class="fa-solid fa-pen"></i>
          </button>
          <button type="button" class="btn btn-sm btn-outline-danger btn-eliminar" data-id="${prod.id}" title="Eliminar">
            <i class="fa-solid fa-trash"></i>
          </button>
        </div>
      </td>
    `;
    tbody.appendChild(tr);
  });

}

async function guardarOActualizarProducto(e) {
  e.preventDefault();

  const id = document.getElementById('productoId').value;
  const inputArchivo = document.getElementById('prodArchivoImg');
  let nombreImagenFinal = document.getElementById('prodImg').value.trim() || null;

  try {
    // 1. Si el usuario seleccionó un archivo nuevo, lo subimos primero al backend
    if (inputArchivo && inputArchivo.files.length > 0) {
      const formData = new FormData();
      formData.append('archivo', inputArchivo.files[0]);

      const headersSubida = {};
      const token = localStorage.getItem('tokenJWT');
      if (token) headersSubida['Authorization'] = `Bearer ${token}`;

      const resImg = await fetch(`${API_PRODUCTOS}/subir-imagen`, {
        method: 'POST',
        headers: headersSubida, // No ponemos Content-Type para que el navegador genere el boundary automático
        body: formData
      });

            if (!resImg.ok) {
        const errData = await resImg.json().catch(() => ({}));
        throw new Error(errData.error || 'No se pudo subir la imagen al servidor.');
      }


      const dataImg = await resImg.json();
      nombreImagenFinal = dataImg.nombreArchivo;
    }

    // 2. Armamos el objeto del producto con el nombre de la imagen subida (o la que ya tenía)
    const payload = {
      nombre: document.getElementById('prodNombre').value.trim(),
      precio: parseFloat(document.getElementById('prodPrecio').value),
      stock: parseInt(document.getElementById('prodStock').value),
      descripcion: document.getElementById('prodDescripcion').value.trim(),
      img: nombreImagenFinal,
      categoria: {
        id: parseInt(document.getElementById('prodCategoria').value)
      }
    };

    const esEdicion = id !== '';
    const url = esEdicion ? `${API_PRODUCTOS}/${id}` : API_PRODUCTOS;
    const metodo = esEdicion ? 'PUT' : 'POST';

    const resProd = await fetch(url, {
      method: metodo,
      headers: getAuthHeaders(true),
      body: JSON.stringify(payload)
    });

    if (!resProd.ok) {
      const errorText = await resProd.text();
      throw new Error(errorText || 'Error al guardar el producto');
    }

    mostrarNotificacion(esEdicion ? 'Producto actualizado correctamente' : 'Producto creado con éxito', 'success');
    resetearFormularioProducto();
    cargarProductos();

  } catch (err) {
    mostrarNotificacion(err.message, 'danger');
  }
}

function prepararEdicion(id) {
  const prod = listaProductosGlobal.find(p => p.id === parseInt(id));
  if (!prod) return;

  document.getElementById('productoId').value = prod.id;
  document.getElementById('prodNombre').value = prod.nombre;
  document.getElementById('prodPrecio').value = prod.precio;
  document.getElementById('prodStock').value = prod.stock;
  document.getElementById('prodDescripcion').value = prod.descripcion || '';
  document.getElementById('prodImg').value = prod.img || '';
  document.getElementById('prodArchivoImg').value = ''; // Limpia el input file
  
  const infoImg = document.getElementById('infoImgActual');
  if (infoImg) {
    infoImg.textContent = prod.img ? `Imagen actual: ${prod.img} (elegí otra solo si querés cambiarla)` : 'Sin imagen asignada.';
  }

  if (prod.categoria) {
    document.getElementById('prodCategoria').value = prod.categoria.id;
  }

  document.getElementById('tituloFormProducto').innerHTML = `<i class="fa-solid fa-pen-to-square me-2"></i>Editando Producto #${prod.id}`;
  document.getElementById('btnGuardarProducto').innerHTML = '<i class="fa-solid fa-check me-1"></i> Actualizar Producto';
  document.getElementById('btnGuardarProducto').classList.replace('btn-primary', 'btn-warning');
  document.getElementById('btnCancelarEdicion').classList.remove('d-none');
}

function resetearFormularioProducto() {
  document.getElementById('formProducto').reset();
  document.getElementById('productoId').value = '';
  document.getElementById('prodImg').value = '';

  const infoImg = document.getElementById('infoImgActual');
  if (infoImg) {
    infoImg.textContent = 'Seleccioná una imagen desde tu computadora (JPG, PNG, WEBP).';
  }

  document.getElementById('tituloFormProducto').innerHTML = '<i class="fa-solid fa-plus-circle me-2"></i>Nuevo Producto';
  document.getElementById('btnGuardarProducto').innerHTML = '<i class="fa-solid fa-floppy-disk me-1"></i> Guardar Producto';
  document.getElementById('btnGuardarProducto').classList.replace('btn-warning', 'btn-primary');
  document.getElementById('btnCancelarEdicion').classList.add('d-none');
}


function eliminarProducto(id) {
  if (!confirm(`¿Seguro que querés eliminar el producto #${id}?`)) return;

  fetch(`${API_PRODUCTOS}/${id}`, {
    method: 'DELETE',
    headers: getAuthHeaders(false)
  })
    .then(async res => {
      if (!res.ok) {
        throw new Error('No se puede eliminar: el producto está asociado a pedidos existentes.');
      }
      mostrarNotificacion('Producto eliminado del catálogo', 'success');
      cargarProductos();
    })
    .catch(err => mostrarNotificacion(err.message, 'danger'));
}

// ==========================================
// 3. FILTROS EN TIEMPO REAL
// ==========================================
function aplicarFiltros() {
  const texto = document.getElementById('filtroNombre').value.trim().toLowerCase();
  const catId = document.getElementById('filtroCategoria').value;

  const filtrados = listaProductosGlobal.filter(prod => {
    const coincideNombre = prod.nombre.toLowerCase().includes(texto);
    const coincideCat = catId === 'TODAS' || (prod.categoria && prod.categoria.id === parseInt(catId));
    return coincideNombre && coincideCat;
  });

  renderizarTablaProductos(filtrados);
}

// ==========================================
// 4. INICIALIZACIÓN DE EVENTOS
// ==========================================
document.addEventListener('DOMContentLoaded', () => {
  cargarCategorias();
  cargarProductos();

  document.getElementById('formCategoria').addEventListener('submit', crearCategoria);
  document.getElementById('formProducto').addEventListener('submit', guardarOActualizarProducto);
  document.getElementById('btnCancelarEdicion').addEventListener('click', resetearFormularioProducto);

document.getElementById('filtroNombre').addEventListener('input', aplicarFiltros);
document.getElementById('filtroCategoria').addEventListener('change', aplicarFiltros);
document.getElementById('btnLimpiarFiltros').addEventListener('click', () => {
document.getElementById('filtroNombre').value = '';
document.getElementById('filtroCategoria').value = 'TODAS';
renderizarTablaProductos(listaProductosGlobal);
});
document.getElementById('tablaAdminProductos').addEventListener('click', (e) => {
const btnEditar = e.target.closest('.btn-editar');
const btnEliminar = e.target.closest('.btn-eliminar');
if (btnEditar) prepararEdicion(btnEditar.dataset.id);
if (btnEliminar) eliminarProducto(btnEliminar.dataset.id);
});
});