/**
 * SA-GIT: Sistema de Gestión de Incidentes TI
 * Lógica de Frontend: Autenticación JWT y Gestión de Incidentes
 */

const API_BASE = '/api/v1';

// Estado global de la aplicación
const state = {
    token: localStorage.getItem('sagit_token') || null,
    username: localStorage.getItem('sagit_username') || null,
    rol: localStorage.getItem('sagit_rol') || null,
    incidentes: []
};

// =========================================================================
// INICIALIZACIÓN
// =========================================================================
document.addEventListener('DOMContentLoaded', () => {
    initApp();
    setupEventListeners();
});

function initApp() {
    if (state.token) {
        showDashboardView();
        cargarIncidentes();
    } else {
        showLoginView();
    }
}

// =========================================================================
// EVENT LISTENERS
// =========================================================================
function setupEventListeners() {
    // Formulario de Inicio de Sesión
    const loginForm = document.getElementById('login-form');
    if (loginForm) {
        loginForm.addEventListener('submit', handleLogin);
    }

    // Botón de Cerrar Sesión
    const btnLogout = document.getElementById('btn-logout');
    if (btnLogout) {
        btnLogout.addEventListener('click', handleLogout);
    }

    // Formulario de Registro de Incidentes
    const incidenteForm = document.getElementById('crear-incidente-form');
    if (incidenteForm) {
        incidenteForm.addEventListener('submit', handleCrearIncidente);
    }

    // Botón de Actualizar Lista
    const btnRefresh = document.getElementById('btn-refresh-list');
    if (btnRefresh) {
        btnRefresh.addEventListener('click', () => {
            btnRefresh.classList.add('rotating');
            cargarIncidentes().finally(() => {
                setTimeout(() => btnRefresh.classList.remove('rotating'), 500);
            });
        });
    }

    // Filtros de búsqueda y estado
    const filterSearch = document.getElementById('filter-search');
    if (filterSearch) {
        filterSearch.addEventListener('input', applyFilters);
    }

    const filterEstado = document.getElementById('filter-estado');
    if (filterEstado) {
        filterEstado.addEventListener('change', applyFilters);
    }

    // Modal de Detalle
    const btnCloseModal = document.getElementById('btn-close-modal');
    const btnModalCloseFooter = document.getElementById('btn-modal-close-footer');
    const modalBackdrop = document.getElementById('modal-detalle');

    if (btnCloseModal) btnCloseModal.addEventListener('click', closeModal);
    if (btnModalCloseFooter) btnModalCloseFooter.addEventListener('click', closeModal);
    if (modalBackdrop) {
        modalBackdrop.addEventListener('click', (e) => {
            if (e.target === modalBackdrop) closeModal();
        });
    }
}

// =========================================================================
// GESTIÓN DE VISTAS Y AUTENTICACIÓN
// =========================================================================
function showLoginView() {
    document.getElementById('login-view').classList.remove('hidden');
    document.getElementById('dashboard-view').classList.add('hidden');
    document.getElementById('user-profile-bar').classList.add('hidden');
}

function showDashboardView() {
    document.getElementById('login-view').classList.add('hidden');
    document.getElementById('dashboard-view').classList.remove('hidden');
    document.getElementById('user-profile-bar').classList.remove('hidden');

    // Actualizar datos del usuario en el header
    const avatar = document.getElementById('user-avatar');
    const userName = document.getElementById('user-name');
    const userRole = document.getElementById('user-role');

    if (avatar && state.username) avatar.textContent = state.username.charAt(0).toUpperCase();
    if (userName) userName.textContent = state.username;
    if (userRole) userRole.textContent = state.rol || 'USUARIO';
}

async function handleLogin(e) {
    e.preventDefault();
    const alertBox = document.getElementById('login-alert');
    const btnSubmit = document.getElementById('btn-login-submit');
    const btnText = btnSubmit.querySelector('.btn-text');
    const spinner = btnSubmit.querySelector('.btn-spinner');

    const username = document.getElementById('login-username').value.trim();
    const password = document.getElementById('login-password').value;

    alertBox.classList.add('hidden');
    alertBox.textContent = '';
    btnSubmit.disabled = true;
    btnText.textContent = 'Autenticando...';
    spinner.classList.remove('hidden');

    try {
        const response = await fetch(`${API_BASE}/auth/login`, {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        const data = await response.json();

        if (!response.ok) {
            throw new Error(data.message || 'Error al iniciar sesión. Verifique sus credenciales.');
        }

        // Guardar en estado y LocalStorage
        state.token = data.token;
        state.username = data.username;
        state.rol = data.rol;

        localStorage.setItem('sagit_token', data.token);
        localStorage.setItem('sagit_username', data.username);
        localStorage.setItem('sagit_rol', data.rol);

        showToast(`¡Bienvenido, ${data.username}! Sesión iniciada con rol ${data.rol}.`, 'success');
        showDashboardView();
        cargarIncidentes();

    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.classList.remove('hidden');
    } finally {
        btnSubmit.disabled = false;
        btnText.textContent = 'Ingresar al Sistema';
        spinner.classList.add('hidden');
    }
}

function handleLogout() {
    state.token = null;
    state.username = null;
    state.rol = null;
    state.incidentes = [];

    localStorage.removeItem('sagit_token');
    localStorage.removeItem('sagit_username');
    localStorage.removeItem('sagit_rol');

    const loginForm = document.getElementById('login-form');
    if (loginForm) loginForm.reset();

    showToast('Sesión cerrada correctamente.', 'success');
    showLoginView();
}

/**
 * Autocompleta credenciales para pruebas rápidas
 */
window.fillCredentials = function(username, password) {
    const userField = document.getElementById('login-username');
    const passField = document.getElementById('login-password');
    if (userField && passField) {
        userField.value = username;
        passField.value = password;
        userField.focus();
    }
};

// =========================================================================
// MÓDULO DE INCIDENTES (WIREFRAME Y CONSULTA)
// =========================================================================

/**
 * Carga los incidentes desde GET /api/v1/incidentes
 */
async function cargarIncidentes() {
    const loadingState = document.getElementById('table-loading-state');
    const emptyState = document.getElementById('table-empty-state');
    const tbody = document.getElementById('incidentes-tbody');

    if (loadingState) loadingState.classList.remove('hidden');
    if (emptyState) emptyState.classList.add('hidden');
    if (tbody) tbody.innerHTML = '';

    try {
        const response = await fetch(`${API_BASE}/incidentes`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${state.token}`,
                'Accept': 'application/json'
            }
        });

        if (response.status === 401) {
            handleLogout();
            showToast('Tu sesión ha expirado. Por favor inicia sesión nuevamente.', 'error');
            return;
        }

        if (!response.ok) {
            throw new Error('Error al cargar la lista de incidentes.');
        }

        const data = await response.json();
        state.incidentes = data;

        actualizarMetricas(data);
        renderTable(data);

    } catch (err) {
        showToast(err.message, 'error');
    } finally {
        if (loadingState) loadingState.classList.add('hidden');
    }
}

/**
 * Registra un nuevo incidente TI desde POST /api/v1/incidentes
 */
async function handleCrearIncidente(e) {
    e.preventDefault();

    const form = e.target;
    const btnSubmit = document.getElementById('btn-submit-incidente');
    const btnText = btnSubmit.querySelector('.btn-text');
    const spinner = btnSubmit.querySelector('.btn-spinner');
    const alertBox = document.getElementById('incidente-form-alert');

    alertBox.classList.add('hidden');
    alertBox.className = 'alert hidden';

    // Obtener valores del formulario Wireframe
    const fuenteMonitoreo = form.fuenteMonitoreo.value;
    const servicioAfectado = form.servicioAfectado.value.trim();
    const descripcionFalla = form.descripcionFalla.value.trim();
    const prioridadElement = form.querySelector('input[name="prioridad"]:checked');
    const prioridad = prioridadElement ? prioridadElement.value : 'MEDIA';

    if (!fuenteMonitoreo || !servicioAfectado || !descripcionFalla) {
        alertBox.textContent = 'Por favor complete todos los campos obligatorios (*)';
        alertBox.className = 'alert alert-error';
        return;
    }

    btnSubmit.disabled = true;
    btnText.textContent = 'Registrando...';
    spinner.classList.remove('hidden');

    try {
        const payload = {
            fuenteMonitoreo,
            servicioAfectado,
            descripcionFalla,
            prioridad
        };

        const response = await fetch(`${API_BASE}/incidentes`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': `Bearer ${state.token}`
            },
            body: JSON.stringify(payload)
        });

        const data = await response.json();

        if (response.status === 401) {
            handleLogout();
            showToast('Sesión no válida o expirada.', 'error');
            return;
        }

        if (!response.ok) {
            throw new Error(data.message || 'Error al registrar el incidente.');
        }

        showToast(`Incidente #${data.id} registrado exitosamente.`, 'success');
        form.reset();

        // Restaurar prioridad media por defecto
        const radioMedia = form.querySelector('input[name="prioridad"][value="MEDIA"]');
        if (radioMedia) radioMedia.checked = true;

        // Recargar la lista
        cargarIncidentes();

    } catch (err) {
        alertBox.textContent = err.message;
        alertBox.className = 'alert alert-error';
    } finally {
        btnSubmit.disabled = false;
        btnText.textContent = 'Registrar Incidente';
        spinner.classList.add('hidden');
    }
}

/**
 * Consulta y muestra el detalle de un incidente por ID (/api/v1/incidentes/{id})
 */
async function verDetalleIncidente(id) {
    try {
        const response = await fetch(`${API_BASE}/incidentes/${id}`, {
            method: 'GET',
            headers: {
                'Authorization': `Bearer ${state.token}`,
                'Accept': 'application/json'
            }
        });

        if (!response.ok) {
            throw new Error('No se pudo obtener el detalle del incidente.');
        }

        const incidente = await response.json();

        document.getElementById('modal-incidente-id').textContent = `#${incidente.id}`;
        document.getElementById('modal-servicio').textContent = incidente.servicioAfectado;
        document.getElementById('modal-fuente').textContent = incidente.fuenteMonitoreo;
        document.getElementById('modal-usuario').textContent = incidente.usuarioRegistro;
        document.getElementById('modal-fecha').textContent = formatFecha(incidente.fechaCreacion);
        document.getElementById('modal-descripcion').textContent = incidente.descripcionFalla;

        // Estado Badge
        const estadoContainer = document.getElementById('modal-estado');
        estadoContainer.innerHTML = `<span class="badge-estado badge-estado-${incidente.estado}">${incidente.estado.replace('_', ' ')}</span>`;

        // Prioridad Badge
        const prioridadContainer = document.getElementById('modal-prioridad');
        const prio = incidente.prioridad || 'MEDIA';
        prioridadContainer.innerHTML = `<span class="badge-prioridad badge-prioridad-${prio}">${prio}</span>`;

        // Abrir Modal
        document.getElementById('modal-detalle').classList.remove('hidden');

    } catch (err) {
        showToast(err.message, 'error');
    }
}

function closeModal() {
    document.getElementById('modal-detalle').classList.add('hidden');
}

// =========================================================================
// RENDERIZADO Y FILTRADO
// =========================================================================
function renderTable(list) {
    const tbody = document.getElementById('incidentes-tbody');
    const emptyState = document.getElementById('table-empty-state');

    tbody.innerHTML = '';

    if (!list || list.length === 0) {
        emptyState.classList.remove('hidden');
        return;
    }

    emptyState.classList.add('hidden');

    list.forEach(inc => {
        const tr = document.createElement('tr');

        const prio = inc.prioridad || 'MEDIA';
        const estado = inc.estado || 'ABIERTO';

        tr.innerHTML = `
            <td class="id-cell">#${inc.id}</td>
            <td class="fuente-cell">${escapeHtml(inc.fuenteMonitoreo)}</td>
            <td><strong>${escapeHtml(inc.servicioAfectado)}</strong></td>
            <td class="desc-cell" title="${escapeHtml(inc.descripcionFalla)}">${escapeHtml(inc.descripcionFalla)}</td>
            <td><span class="badge-estado badge-estado-${estado}">${estado.replace('_', ' ')}</span></td>
            <td><span class="badge-prioridad badge-prioridad-${prio}">${prio}</span></td>
            <td><span class="user-badge-cell">👤 ${escapeHtml(inc.usuarioRegistro)}</span></td>
            <td class="date-cell">${formatFecha(inc.fechaCreacion)}</td>
            <td style="text-align: center;">
                <button type="button" class="btn-view-detail" onclick="verDetalleIncidente(${inc.id})" title="Ver Ficha Técnica">
                    Ver
                </button>
            </td>
        `;

        tbody.appendChild(tr);
    });
}

function applyFilters() {
    const query = document.getElementById('filter-search').value.toLowerCase().trim();
    const estado = document.getElementById('filter-estado').value;

    const filtered = state.incidentes.filter(inc => {
        const matchesQuery = !query || 
            (inc.servicioAfectado && inc.servicioAfectado.toLowerCase().includes(query)) ||
            (inc.descripcionFalla && inc.descripcionFalla.toLowerCase().includes(query)) ||
            (inc.fuenteMonitoreo && inc.fuenteMonitoreo.toLowerCase().includes(query)) ||
            (inc.usuarioRegistro && inc.usuarioRegistro.toLowerCase().includes(query));

        const matchesEstado = estado === 'TODOS' || inc.estado === estado;

        return matchesQuery && matchesEstado;
    });

    renderTable(filtered);
}

function actualizarMetricas(list) {
    const total = list.length;
    const abiertos = list.filter(i => i.estado === 'ABIERTO').length;
    const proceso = list.filter(i => i.estado === 'EN_PROCESO').length;
    const resueltos = list.filter(i => i.estado === 'RESUELTO').length;

    document.getElementById('metric-total').textContent = total;
    document.getElementById('metric-abiertos').textContent = abiertos;
    document.getElementById('metric-en-proceso').textContent = proceso;
    document.getElementById('metric-resueltos').textContent = resueltos;
}

// =========================================================================
// UTILIDADES
// =========================================================================
function formatFecha(dateString) {
    if (!dateString) return '-';
    try {
        const d = new Date(dateString);
        return d.toLocaleDateString('es-ES', {
            year: 'numeric',
            month: '2-digit',
            day: '2-digit',
            hour: '2-digit',
            minute: '2-digit'
        });
    } catch {
        return dateString;
    }
}

function escapeHtml(text) {
    if (!text) return '';
    return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
}

function showToast(message, type = 'success') {
    const container = document.getElementById('toast-container');
    if (!container) return;

    const toast = document.createElement('div');
    toast.className = `toast toast-${type}`;
    toast.textContent = message;

    container.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transform = 'translateY(10px)';
        toast.style.transition = 'all 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 4000);
}

// Exponer funciones globales requeridas por atributos onclick en HTML
window.verDetalleIncidente = verDetalleIncidente;
