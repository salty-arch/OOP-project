const user = requireAuth();
let currentFilter = 'active';

async function loadGoals() {
  const tbody = document.getElementById('goalBody');
  try {
    const params = `email=${encodeURIComponent(user.email)}${currentFilter !== 'all' ? `&status=${currentFilter}` : ''}`;
    const goals = await apiGet(`/api/goals?${params}`);

    if (goals.length === 0) {
      document.getElementById('goalTableContainer').innerHTML =
        '<div class="empty-state"><p>No goals found.</p></div>';
      return;
    }

    tbody.innerHTML = goals.map(g => {
      const badgeClass = g.status === 'completed' ? 'badge-success' : g.status === 'missed' ? 'badge-danger' : 'badge-primary';
      return `<tr>
        <td>${g.goalType}</td>
        <td>${g.category || '-'}</td>
        <td>PKR ${formatCurrency(g.amount)}</td>
        <td>${g.deadline || '-'}</td>
        <td><span class="badge ${badgeClass}">${g.status}</span></td>
      </tr>`;
    }).join('');
  } catch (err) {
    document.getElementById('goalTableContainer').innerHTML =
      `<div class="empty-state"><p>Failed to load: ${err.message}</p></div>`;
  }
}

function filterGoals(status, btn) {
  currentFilter = status;
  document.querySelectorAll('.tabs .tab').forEach(t => t.classList.remove('active'));
  btn.classList.add('active');
  loadGoals();
}

function showAddGoalModal() {
  const body = `
    <form id="goalForm">
      <div class="form-group">
        <label class="label">Goal Type</label>
        <select class="select" name="goal_type" required>
          <option value="saving">Saving</option>
          <option value="limit spending">Limit Spending</option>
        </select>
      </div>
      <div class="form-group">
        <label class="label">Category (optional)</label>
        <input class="input" name="category" placeholder="e.g. Emergency Fund, Car">
      </div>
      <div class="form-group">
        <label class="label">Target Amount (PKR)</label>
        <input class="input" type="number" name="amount" step="0.01" min="0.01" required>
      </div>
      <div class="form-group">
        <label class="label">Deadline</label>
        <input class="input" type="date" name="deadline" required>
      </div>
    </form>`;

  showModal('Add Goal', body, async (data, overlay) => {
    const form = overlay.querySelector('form');
    const fd = new FormData(form);

    try {
      await apiPost('/api/goals', {
        email: user.email,
        goal_type: fd.get('goal_type'),
        category: fd.get('category'),
        amount: fd.get('amount'),
        deadline: fd.get('deadline')
      });
      showToast('Goal added!', 'success');
      overlay.remove();
      loadGoals();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });
}

loadGoals();