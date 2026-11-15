const API_URL_BASE = 'http://localhost:8080/pedido';

// Función principal que carga los pedidos (acepta una URL opcional para filtros)
function cargarHistorialDePedidos(url = API_URL_BASE) {
  const tablaBody = document.getElementById("tablaPedidosBody");
  const btnActualizar = document.getElementById("btnActualizarPedidos")
  if (!tablaBody) return;

  if (btnActualizar) {
    btnActualizar.disabled = true;
    btnActualizar.innerHTML = '<i class="fa-solid fa-spinner fa-spin me-1"></i> Actualizando...';
  }

  fetch(url)
    .then(response => {
      if (!response.ok) throw new Error("Error al obtener los pedidos del servidor");
      return response.json();
    })
    .then(pedidos => {
      tablaBody.innerHTML = "";

      if (pedidos.length === 0) {
        tablaBody.innerHTML = `<tr><td colspan="7" class="text-center text-muted py-4">No hay pedidos registrados para mostrar.</td></tr>`;
        return;
      }

      pedidos.forEach(ped => {
        const fechaFormateada = new Date(ped.fecha).toLocaleString("es-AR");

        // 1. Armamos la lista visual de perfumes comprados usando los datos del DTO
        let detalleProductosHTML = '<span class="text-muted small">Sin detalle</span>';
        if (ped.items && ped.items.length > 0) {
          detalleProductosHTML = `<ul class="list-unstyled mb-0 small">
            ${ped.items.map(item => `
              <li>
                <strong>${item.cantidad}x</strong> ${item.nombreProducto} 
                <span class="text-muted">($${item.precioUnitario.toLocaleString("es-AR")})</span>
              </li>
            `).join('')}
          </ul>`;
        }

               // 2. Lógica visual de colores y secuencia circular de estados (incluye CANCELADO reversible)
        let badgeStyle = '';
        let textoBoton = '';
        let proximoEstado = '';
        let botonCancelar = '';

        const estadoNormalizado = ped.estado ? ped.estado.trim().toLowerCase() : "pendiente";

        if (estadoNormalizado === "cancelado") {
          badgeStyle = 'background-color: #fed7d7; color: #9b2c2c;'; // Rojo suave
          textoBoton = 'Reactivar'; // Permite revertir el estado a PENDIENTE
          proximoEstado = 'PENDIENTE';
        } else if (estadoNormalizado === "pendiente") {
          badgeStyle = 'background-color: #feebc8; color: #c05621;'; // Naranja
          textoBoton = 'Preparar';
          proximoEstado = 'EN_PROCESO';
          botonCancelar = `<button type="button" class="btn-cancelar-pedido" data-id="${ped.id}" style="background-color: #dc3545; color: white; border: none; padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; cursor: pointer; font-weight: 600; margin-left: 6px;">Cancelar</button>`;
        } else if (estadoNormalizado === "en_proceso" || estadoNormalizado === "en proceso") {
          badgeStyle = 'background-color: #e2e8f0; color: #4a5568;'; // Gris / Azulado
          textoBoton = 'Entregar';
          proximoEstado = 'REALIZADO';
          botonCancelar = `<button type="button" class="btn-cancelar-pedido" data-id="${ped.id}" style="background-color: #dc3545; color: white; border: none; padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; cursor: pointer; font-weight: 600; margin-left: 6px;">Cancelar</button>`;
        } else {
          badgeStyle = 'background-color: #c6f6d5; color: #22543d;'; // Verde (REALIZADO)
          textoBoton = 'Resetear';
          proximoEstado = 'PENDIENTE';
        }

        const botonAccion = `<button type="button" class="btn-cambiar-estado" data-id="${ped.id}" data-estado="${proximoEstado}" style="background-color: #212529; color: white; border: none; padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; cursor: pointer; font-weight: 600; margin-left: 8px;">${textoBoton}</button>`;

        // 3. Renderizamos la fila completa (Asegurate de tener 7 columnas en tu <thead> HTML)
        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td class="fw-bold text-primary">#${ped.id}</td>
          <td>${fechaFormateada}</td>
          <td>${detalleProductosHTML}</td>
          <td class="text-capitalize">${ped.metodoPago}</td>
          <td>${ped.cuotas > 1 ? ped.cuotas + ' cuotas' : '1 pago'}</td>
          <td class="text-success fw-bold">${ped.total.toLocaleString("es-AR", { style: "currency", currency: "ARS" })}</td>
          <td>
            <div style="display: flex; flex-wrap; align-items: center; gap: 6px;">
              <span class="badge" style="${badgeStyle}">${ped.estado}</span>
              ${botonAccion}
              ${botonCancelar}
            </div>
          </td>
        `;
        tablaBody.appendChild(tr);
      });
    })
    .catch(error => {
      console.error("Error en el fetch de pedidos:", error);
      tablaBody.innerHTML = `<tr><td colspan="7" class="text-center text-danger py-3">Error de conexión con el servidor. Verificá que Spring Boot esté activo.</td></tr>`;
    })
    .finally(() => {
      if(btnActualizar) {
        btnActualizar.disabled = false;
        btnActualizar.innerHTML = 'Actualizar Lista <i class="fa-solid fa-rotate ms-1"></i>';
      
      }
    });
}

// Función para actualizar el estado en MySQL
function cambiarEstadoPedido(id, nuevoEstado) {
  fetch(`${API_URL_BASE}/${id}/estado?nuevoEstado=${nuevoEstado}`, {
    method: 'PUT'
  })
  .then(response => {
    if (!response.ok) throw new Error("No se pudo actualizar el estado");
    return response.json();
  })
  .then(() => {
    // Si hay un filtro activo en pantalla, respetamos ese filtro al recargar
    const filtroSelect = document.getElementById("filtroEstadoPedido");
    if (filtroSelect && filtroSelect.value !== "TODOS") {
      cargarHistorialDePedidos(`${API_URL_BASE}/filtrar/estado?estado=${filtroSelect.value}`);
    } else {
      cargarHistorialDePedidos();
    }
  })
  .catch(error => {
    console.error("Error al actualizar:", error);
    alert("No se pudo cambiar el estado del pedido");
  });
}

// Inicialización y captura de eventos
window.addEventListener("DOMContentLoaded", () => {
  cargarHistorialDePedidos();

  const btnActualizar = document.getElementById("btnActualizarPedidos");
  if (btnActualizar) {
    btnActualizar.addEventListener("click", () => {
      const filtroSelect = document.getElementById("filtroEstadoPedido");
      if (filtroSelect) filtroSelect.value = "TODOS";
      cargarHistorialDePedidos();
    });
  }

  // Listener opcional por si agregás un <select id="filtroEstadoPedido"> en tu HTML
  const filtroSelect = document.getElementById("filtroEstadoPedido");
  if (filtroSelect) {
    filtroSelect.addEventListener("change", (e) => {
      const estadoSeleccionado = e.target.value;
      if (estadoSeleccionado === "TODOS") {
        cargarHistorialDePedidos();
      } else {
        cargarHistorialDePedidos(`${API_URL_BASE}/filtrar/estado?estado=${estadoSeleccionado}`);
      }
    });
  }

  // Delegación de eventos para los botones de estado en la tabla
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
        if (confirm(`¿Estás seguro de que querés cancelar el pedido #${idPedido}?`)) {
          cambiarEstadoPedido(idPedido, "CANCELADO");
        }
      } 
    });
  }
});
