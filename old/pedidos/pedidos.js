const API_URL_HISTORIAL = 'http://localhost:8080/pedido';

function cargarHistorialDePedidos() {
  const tablaBody = document.getElementById("tablaPedidosBody");
  if (!tablaBody) return;

  fetch(API_URL_HISTORIAL)
    .then(response => {
      if (!response.ok) throw new Error("No se pudo conectar con el servidor de Java");
      return response.json();
    })
    .then(pedidos => {
      tablaBody.innerHTML = ""; 

      if (pedidos.length === 0) {
        tablaBody.innerHTML = `<tr><td colspan="6" class="text-center text-muted py-3">No hay pedidos registrados en la base de datos.</td></tr>`;
        return;
      }

      pedidos.forEach(ped => {
        const fechaFormateada = new Date(ped.fecha).toLocaleString("es-AR");

        // Dynamic Badge Color: verde si está completado, naranja si está pendiente
        const esPendiente = ped.estado === "pendiente";
        const badgeStyle = esPendiente 
          ? 'background-color: #feebc8; color: #c05621;' 
          : 'background-color: #c6f6d5; color: #22543d;';

        // 🚀 BOTÓN ACCIÓN: Si está pendiente muestra el botón para completarlo, si no, no muestra nada
        const botonAccion = esPendiente
          ? `<button type="button" class="btn-completar-orden" data-id="${ped.id}" style="background-color: #212529; color: white; border: none; padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; cursor: pointer; font-weight: 600; margin-left: 8px;">Check</button>`
          : "";

        const tr = document.createElement("tr");
        tr.innerHTML = `
          <td class="fw-bold text-primary">#${ped.id}</td>
          <td>${fechaFormateada}</td>
          <td class="text-capitalize">${ped.metodoPago}</td>
          <td>${ped.cuotas > 1 ? ped.cuotas + ' cuotas' : '1 pago'}</td>
          <td class="text-success fw-bold">${ped.total.toLocaleString("es-AR", { style: "currency", currency: "ARS" })}</td>
          <td>
            <div style="display: flex; align-items: center;">
              <span class="badge" style="${badgeStyle}">${ped.estado}</span>
              ${botonAccion}
            </div>
          </td>
        `;
        tablaBody.appendChild(tr);
      });
    })
    .catch(error => {
      console.error("Error en el fetch de pedidos:", error);
    });
}

// 🚀 NUEVA FUNCIÓN: Envía la petición PUT a Spring Boot para actualizar el estado en MySQL
function cambiarEstadoACompletado(id) {
  fetch(`http://localhost:8080/pedido/${id}/completar`, {
    method: 'PUT'
  })
  .then(response => {
    if (!response.ok) throw new Error("No se pudo actualizar el estado");
    return response.json();
  })
  .then(resultado => {
    if (resultado.status === "success") {
      cargarHistorialDePedidos(); // Recarga la lista automáticamente para ver el cambio
    }
  })
  .catch(error => {
    console.error("Error al actualizar:", error);
    alert("No se pudo completar el pedido");
  });
}

// Inicialización segura y captura de clics en los botones de la tabla
window.addEventListener("DOMContentLoaded", () => {
  cargarHistorialDePedidos();

  const btnActualizar = document.getElementById("btnActualizarPedidos");
  if (btnActualizar) {
    btnActualizar.addEventListener("click", cargarHistorialDePedidos);
  }

  // Escuchamos los clics en la tabla para detectar el botón "Check"
  const tablaBody = document.getElementById("tablaPedidosBody");
  if (tablaBody) {
    tablaBody.addEventListener("click", (e) => {
      if (e.target.classList.contains("btn-completar-orden")) {
        const idPedido = e.target.dataset.id;
        cambiarEstadoACompletado(idPedido);
      }
    });
  }
});
