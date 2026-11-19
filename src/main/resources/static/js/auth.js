// =======================================================
// GESTIÓN CENTRALIZADA DE SESIÓN Y TOKENS JWT - LA PERFU
// =======================================================

const AUTH_KEYS = {
  TOKEN: 'laperfu_token',
  EMAIL: 'laperfu_email',
  ROL: 'laperfu_rol'
};

function guardarSesion(token, email, rol) {
  localStorage.setItem(AUTH_KEYS.TOKEN, token);
  localStorage.setItem(AUTH_KEYS.EMAIL, email);
  localStorage.setItem(AUTH_KEYS.ROL, rol);
}

function obtenerToken() {
  return localStorage.getItem(AUTH_KEYS.TOKEN);
}

function obtenerUsuarioActual() {
  const token = obtenerToken();
  if (!token) return null;
  return {
    token,
    email: localStorage.getItem(AUTH_KEYS.EMAIL) || '',
    rol: localStorage.getItem(AUTH_KEYS.ROL) || 'USER'
  };
}

function esAdmin() {
  const user = obtenerUsuarioActual();
  return user !== null && user.rol === 'ADMIN';
}

function cerrarSesion() {
  localStorage.removeItem(AUTH_KEYS.TOKEN);
  localStorage.removeItem(AUTH_KEYS.EMAIL);
  localStorage.removeItem(AUTH_KEYS.ROL);
  window.location.href = 'index.html';
}

// Devuelve los encabezados listos para fetch con Authorization: Bearer <token>
function getAuthHeaders(incluirJson = true) {
  const headers = {};
  if (incluirJson) {
    headers['Content-Type'] = 'application/json';
  }
  const token = obtenerToken();
  if (token) {
    headers['Authorization'] = `Bearer ${token}`;
  }
  return headers;
}

// Guardia de seguridad para páginas protegidas (admin.html / pedidos.html)
function protegerPagina(soloAdmin = false) {
  const user = obtenerUsuarioActual();
  if (!user) {
    alert('Debés iniciar sesión para acceder a esta sección.');
    window.location.href = 'login.html';
    return false;
  }
  if (soloAdmin && user.rol !== 'ADMIN') {
    alert('Acceso denegado: esta sección es exclusiva para Administradores.');
    window.location.href = 'index.html';
    return false;
  }
  return true;
}
