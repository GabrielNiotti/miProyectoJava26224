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

  let badgeStyle = '';
  let textoBoton = '';
  let proximoEstado = '';

  // Limpiamos espacios y pasamos todo a minúsculas para que machee siempre
  const estadoNormalizado = ped.estado ? ped.estado.trim().toLowerCase() : "pendiente";

  if (estadoNormalizado === "pendiente") {
    badgeStyle = 'background-color: #feebc8; color: #c05621;'; // Naranja
    textoBoton = 'Preparar';
    proximoEstado = 'en_proceso';
  } else if (estadoNormalizado === "en_proceso" || estadoNormalizado === "en proceso") {
    badgeStyle = 'background-color: #e2e8f0; color: #4a5568;'; // Gris / Azulado
    textoBoton = 'Entregar';
    proximoEstado = 'realizado';
  } else { 
    // Por descarte, si es realizado o cualquier otra cosa, permite resetearlo
    badgeStyle = 'background-color: #c6f6d5; color: #22543d;'; // Verde
    textoBoton = 'Resetear';
    proximoEstado = 'pendiente';
  }

  const botonAccion = `<button type="button" class="btn-cambiar-estado" data-id="${ped.id}" data-estado="${proximoEstado}" style="background-color: #212529; color: white; border: none; padding: 4px 10px; border-radius: 12px; font-size: 0.75rem; cursor: pointer; font-weight: 600; margin-left: 8px;">${textoBoton}</button>`;

  // ... resto de tu código donde armás el 'tr.innerHTML' y hacés el appendChild ...


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

// Envía el PUT con el QueryParam requerido por el endpoint de Spring Boot
function cambiarEstadoPedido(id, nuevoEstado) {
  fetch(`http://localhost:8080/pedido/${id}/estado?nuevoEstado=${nuevoEstado}`, {
    method: 'PUT'
  })
  .then(response => {
    if (!response.ok) throw new Error("No se pudo actualizar el estado");
    return response.json();
  })
  .then(() => {
    cargarHistorialDePedidos(); // Recarga la lista para pintar el nuevo color al instante
  })
  .catch(error => {
    console.error("Error al actualizar:", error);
    alert("No se pudo cambiar el estado del pedido");
  });
}

// Inicialización segura y captura de clics en los botones de la tabla
window.addEventListener("DOMContentLoaded", () => {
  cargarHistorialDePedidos();

  const btnActualizar = document.getElementById("btnActualizarPedidos");
  if (btnActualizar) {
    btnActualizar.addEventListener("click", cargarHistorialDePedidos);
  }

  // Escuchamos los clics en la tabla para detectar el botón dinámico
  const tablaBody = document.getElementById("tablaPedidosBody");
  if (tablaBody) {
    tablaBody.addEventListener("click", (e) => {
      if (e.target.classList.contains("btn-cambiar-estado")) {
        const idPedido = e.target.dataset.id;
        const estadoDestino = e.target.dataset.estado;
        cambiarEstadoPedido(idPedido, estadoDestino);
      }
    });
  }
});
