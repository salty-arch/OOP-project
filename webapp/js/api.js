const API_BASE = '';

async function apiGet(path) {
  const res = await fetch(API_BASE + path);
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }));
    throw new Error(err.error || 'Request failed');
  }
  return res.json();
}

async function apiPost(path, data) {
  const params = new URLSearchParams();
  for (const [key, val] of Object.entries(data)) {
    if (val !== null && val !== undefined) params.append(key, val);
  }
  const res = await fetch(API_BASE + path, {
    method: 'POST',
    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
    body: params
  });
  if (!res.ok) {
    const err = await res.json().catch(() => ({ error: res.statusText }));
    throw new Error(err.error || 'Request failed');
  }
  return res.json();
}

function formatCurrency(n) {
  return Number(n).toLocaleString('en-US');
}

function showToast(message, type = 'info') {
  const existing = document.querySelector('.toast');
  if (existing) existing.remove();
  const toast = document.createElement('div');
  toast.className = `toast toast-${type}`;
  toast.textContent = message;
  document.body.appendChild(toast);
  setTimeout(() => toast.remove(), 3500);
}

function getUser() {
  const u = localStorage.getItem('user');
  return u ? JSON.parse(u) : null;
}

function requireAuth() {
  const user = getUser();
  if (!user) {
    window.location.href = '/login.html';
    return null;
  }
  return user;
}

function logout() {
  localStorage.removeItem('user');
  showToast('Logged out', 'info');
  window.location.href = '/login.html';
}

function showLoading(container) {
  container.innerHTML = '<div class="loading">Loading...</div>';
}

function showError(container, msg) {
  container.innerHTML = `<div class="empty-state"><p>${msg}</p></div>`;
}

function showModal(title, bodyHtml, onSubmit) {
  const overlay = document.createElement('div');
  overlay.className = 'modal-overlay';
  overlay.innerHTML = `
    <div class="modal">
      <div class="modal-header">
        <div class="modal-title">${title}</div>
        <button class="modal-close" onclick="this.closest('.modal-overlay').remove()">&times;</button>
      </div>
      <div class="modal-body">${bodyHtml}</div>
      <div style="margin-top: 1rem; display: flex; gap: 0.5rem; justify-content: flex-end;">
        <button class="btn btn-secondary" onclick="this.closest('.modal-overlay').remove()">Cancel</button>
        <button class="btn btn-primary" id="modalSubmit">Submit</button>
      </div>
    </div>`;
  document.body.appendChild(overlay);
  document.getElementById('modalSubmit').addEventListener('click', () => {
    const form = overlay.querySelector('form');
    if (form) {
      const data = new FormData(form);
      onSubmit(Object.fromEntries(data), overlay);
    } else {
      onSubmit({}, overlay);
    }
  });
  return overlay;
}

function toggleDark() {
  const html = document.documentElement;
  html.classList.toggle('dark');
  localStorage.setItem('darkMode', html.classList.contains('dark'));
}

(function initTheme() {
  if (localStorage.getItem('darkMode') === 'true') {
    document.documentElement.classList.add('dark');
  }
})();