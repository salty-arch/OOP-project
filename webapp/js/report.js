const user = requireAuth();

(function populateYears() {
  const sel = document.getElementById('reportYear');
  const cur = new Date().getFullYear();
  for (let y = cur; y >= cur - 10; y--) {
    const opt = document.createElement('option');
    opt.value = y;
    opt.textContent = y;
    if (y === cur) opt.selected = true;
    sel.appendChild(opt);
  }
  const m = new Date().getMonth() + 1;
  document.getElementById('reportMonth').value = m;
})();

async function generateReport() {
  const month = document.getElementById('reportMonth').value;
  const year = document.getElementById('reportYear').value;
  const container = document.getElementById('reportContainer');

  container.innerHTML = '<div class="loading">Generating report...</div>';

  try {
    const url = `/api/report?email=${encodeURIComponent(user.email)}&month=${month}&year=${year}`;
    const res = await fetch(url);

    if (!res.ok) {
      throw new Error('Failed to generate report');
    }

    const html = await res.text();
    container.innerHTML = html;
  } catch (err) {
    container.innerHTML = `<div class="empty-state"><p>Error: ${err.message}</p></div>`;
    showToast(err.message, 'error');
  }
}