const API_URL_BASE = 'http://localhost:8080/pedido';

if (typeof protegerPagina === 'function') {
  protegerPagina(false);
}

function cargarHistorialDePedidos(url = API_URL_BASE) {
  const tablaBody = document.getElementById("tablaPedidosBody");
  const btnActualizar = document.getElementById("btnActualizarPedidos");
  if (!tablaBody) return;

  tablaBody.innerHTML =
    '<tr>' +
      '<td colspan="8" class="text-center py-5">' +
        '<div class="spinner-border text-dark" role="status">' +
          '<span class="visually-hidden">Cargando...</span>' +
        '</div>' +
        '<div class="text-muted small mt-2">Cargando historial desde el servidor...</div>' +
      '</td>' +
    '</tr>';

  if (btnActualizar) {
    btnActualizar.disabled = true;
    btnActualizar.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-1"></i> Actualizando...';
  }

  fetch(url, {
    headers: typeof getAuthHeaders === 'function' ? getAuthHeaders(false) : {}
  })
    .then(response => {
      if (response.status === 403 || response.status === 401) {
        throw new Error("Tu sesión expiró. Volvé a iniciar sesión.");
      }
      if (!response.ok) throw new Error("Error al obtener los pedidos del servidor");
      return response.json();
    })
    .then(pedidos => {
      tablaBody.innerHTML = "";

      if (pedidos.length === 0) {
        tablaBody.innerHTML = '<tr><td colspan="8" class="text-center text-muted py-4">No hay pedidos registrados para mostrar.</td></tr>';
        return;
      }

      const usuarioEsAdmin = typeof esAdmin === 'function' && esAdmin();

      pedidos.forEach(ped => {
        const fechaFormateada = new Date(ped.fecha).toLocaleString("es-AR");

        let detalleProductosHTML = '<span class="text-muted small">Sin detalle</span>';
        if (ped.items && ped.items.length > 0) {
          const itemsLi = ped.items.map(item =>
            '<li><strong>' + item.cantidad + 'x</strong> ' + item.nombreProducto +
            ' <span class="text-muted">($' + item.precioUnitario.toLocaleString("es-AR") + ')</span></li>'
          ).join('');
          detalleProductosHTML = '<ul class="list-unstyled mb-0 small">' + itemsLi + '</ul>';
        }

        let badgeStyle = '';
        let textoBoton = '';
        let proximoEstado = '';
        let botonCancelar = '';

        const estadoNormalizado = ped.estado ? ped.estado.trim().toLowerCase() : "pendiente";

        if (estadoNormalizado === "cancelado") {
          badgeStyle = 'background-color: #fed7d7; color: #9b2c2c;';
          textoBoton = 'Reactivar';
          proximoEstado = 'PENDIENTE';
        } else if (estadoNormalizado === "pendiente") {
          badgeStyle = 'background-color: #feebc8; color: #c05621;';
          textoBoton = 'Preparar';
          proximoEstado = 'EN_PROCESO';
          botonCancelar = '<button type="button" class="btn-cancelar-pedido" data-id="' + ped.id + '" style="background-color: #dc3545; color: white; border: none; padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; cursor: pointer; font-weight: 600; margin-left: 6px;">Cancelar</button>';
        } else if (estadoNormalizado === "en_proceso" || estadoNormalizado === "en proceso") {
          badgeStyle = 'background-color: #e2e8f0; color: #4a5568;';
          textoBoton = 'Entregar';
          proximoEstado = 'REALIZADO';
          botonCancelar = '<button type="button" class="btn-cancelar-pedido" data-id="' + ped.id + '" style="background-color: #dc3545; color: white; border: none; padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; cursor: pointer; font-weight: 600; margin-left: 6px;">Cancelar</button>';
        } else {
          badgeStyle = 'background-color: #c6f6d5; color: #22543d;';
          textoBoton = 'Resetear';
          proximoEstado = 'PENDIENTE';
        }

        const controlesAdmin = usuarioEsAdmin
          ? '<button type="button" class="btn-cambiar-estado" data-id="' + ped.id + '" data-estado="' + proximoEstado + '" style="background-color: #212529; color: white; border: none; padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; cursor: pointer; font-weight: 600; margin-left: 8px;">' + textoBoton + '</button>' + botonCancelar
          : '';

        const textoCuotas = ped.cuotas > 1 ? ped.cuotas + ' cuotas' : '1 pago';
        const totalFormateado = ped.total.toLocaleString("es-AR", { style: "currency", currency: "ARS" });
        const clienteEmail = ped.usuarioEmail || 'Sin asignar';

        const tr = document.createElement("tr");
        tr.innerHTML =
          '<td class="fw-bold text-primary">#' + ped.id + '</td>' +
          '<td><span class="badge bg-light text-dark border" style="text-transform: none;">' + clienteEmail + '</span></td>' +
          '<td>' + fechaFormateada + '</td>' +
          '<td>' + detalleProductosHTML + '</td>' +
          '<td class="text-capitalize">' + ped.metodoPago + '</td>' +
          '<td>' + textoCuotas + '</td>' +
          '<td class="text-success fw-bold">' + totalFormateado + '</td>' +
          '<td>' +
            '<div style="display: flex; flex-wrap: wrap; align-items: center; gap: 6px;">' +
              '<span class="badge" style="' + badgeStyle + '">' + ped.estado + '</span>' +
              controlesAdmin +
            '</div>' +
          '</td>';
        tablaBody.appendChild(tr);
      });
    })
    .catch(error => {
      console.error("Error en el fetch de pedidos:", error);
      tablaBody.innerHTML = '<tr><td colspan="8" class="text-center text-danger py-3">' + error.message + '</td></tr>';
    })
    .finally(() => {
      if (btnActualizar) {
        btnActualizar.disabled = false;
        btnActualizar.innerHTML = 'Actualizar Lista <i class="fa-solid fa-rotate ms-1"></i>';
      }
    });
}

function cambiarEstadoPedido(id, nuevoEstado) {
  fetch(API_URL_BASE + '/' + id + '/estado?nuevoEstado=' + nuevoEstado, {
    method: 'PUT',
    headers: typeof getAuthHeaders === 'function' ? getAuthHeaders(false) : {}
  })
    .then(async response => {
      if (!response.ok) {
        const errData = await response.json().catch(() => ({}));
        throw new Error(errData.error || "No se pudo actualizar el estado");
      }
      return response.json();
    })
    .then(() => {
      const filtroSelect = document.getElementById("filtroEstadoPedido");
      if (filtroSelect && filtroSelect.value !== "TODOS") {
        cargarHistorialDePedidos(API_URL_BASE + '/filtrar/estado?estado=' + filtroSelect.value);
      } else {
        cargarHistorialDePedidos();
      }
    })
    .catch(error => {
      console.error("Error al actualizar:", error);
      alert(error.message);
    });
}

window.addEventListener("DOMContentLoaded", () => {
  const usuario = typeof obtenerUsuarioActual === 'function' ? obtenerUsuarioActual() : null;
  const badgeUser = document.getElementById("badgeUserPedidos");
  if (usuario && badgeUser) {
    badgeUser.textContent = usuario.email;
  }

  cargarHistorialDePedidos();

  const btnActualizar = document.getElementById("btnActualizarPedidos");
  if (btnActualizar) {
    btnActualizar.addEventListener("click", () => {
      const filtroSelect = document.getElementById("filtroEstadoPedido");
      if (filtroSelect) filtroSelect.value = "TODOS";
      cargarHistorialDePedidos();
    });
  }

  const filtroSelect = document.getElementById("filtroEstadoPedido");
  if (filtroSelect) {
    filtroSelect.addEventListener("change", (e) => {
      const estadoSeleccionado = e.target.value;
      if (estadoSeleccionado === "TODOS") {
        cargarHistorialDePedidos();
      } else {
        cargarHistorialDePedidos(API_URL_BASE + '/filtrar/estado?estado=' + estadoSeleccionado);
      }
    });
  }

  const tablaBody = document.getElementById("tablaPedidosBody");
  if (tablaBody) {
    tablaBody.addEventListener("click", (e) => {
      if (e.target.classList.contains("btn-cambiar-estado")) {
        const idPedido = e.target.dataset.id;
        const estadoDestino = e.target.dataset.estado;
        cambiarEstadoPedido(idPedido, estadoDestino);
      }

      if (e.target.classList.contains("btn-cancelar-pedido")) {
        const idPedido = e.target.dataset.id;
        if (confirm("¿Estás seguro de que querés cancelar el pedido #" + idPedido + "?")) {
          cambiarEstadoPedido(idPedido, "CANCELADO");
        }
      }
    });
  }
});
