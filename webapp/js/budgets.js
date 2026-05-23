const user = requireAuth();

async function loadBudgets() {
  const tbody = document.getElementById('budgetBody');
  try {
    const budgets = await apiGet(`/api/budgets?email=${encodeURIComponent(user.email)}`);

    if (budgets.length === 0) {
      document.getElementById('budgetTableContainer').innerHTML =
        '<div class="empty-state"><p>No budgets set yet. Click "Add Budget" to start.</p></div>';
      document.getElementById('budgetSummary').innerHTML = '';
      return;
    }

    let totalBudget = 0, totalSpent = 0, totalRemaining = 0;

    tbody.innerHTML = budgets.map(b => {
      totalBudget += b.budget;
      totalSpent += b.spent;
      totalRemaining += b.remaining;

      const barColor = b.progress > 80 ? 'var(--destructive)' : b.progress > 50 ? '#f0c88d' : 'var(--chart-3)';
      return `<tr>
        <td class="font-semibold">${b.category}</td>
        <td>PKR ${formatCurrency(b.budget)}</td>
        <td>PKR ${formatCurrency(b.spent)}</td>
        <td>PKR ${formatCurrency(b.remaining)}</td>
        <td>
          <div style="display: flex; align-items: center; gap: 0.5rem;">
            <div style="flex: 1; height: 8px; background: var(--muted); border-radius: 4px; overflow: hidden;">
              <div style="width: ${Math.min(b.progress, 100)}%; height: 100%; background: ${barColor}; border-radius: 4px;"></div>
            </div>
            <span class="text-sm" style="color: var(--muted-foreground);">${b.progress}%</span>
          </div>
        </td>
      </tr>`;
    }).join('');

    document.getElementById('budgetSummary').innerHTML = `
      <div class="card stat-card"><div class="stat-value" style="color: var(--primary);">PKR ${formatCurrency(totalBudget)}</div><div class="stat-label">Total Budget</div></div>
      <div class="card stat-card"><div class="stat-value" style="color: var(--chart-3);">PKR ${formatCurrency(totalRemaining)}</div><div class="stat-label">Remaining</div></div>
      <div class="card stat-card"><div class="stat-value" style="color: var(--destructive);">PKR ${formatCurrency(totalSpent)}</div><div class="stat-label">Spent</div></div>`;
  } catch (err) {
    document.getElementById('budgetTableContainer').innerHTML =
      `<div class="empty-state"><p>Failed to load: ${err.message}</p></div>`;
  }
}

function showAddBudgetModal() {
  const body = `
    <form id="budgetForm">
      <div class="form-group">
        <label class="label">Category</label>
        <input class="input" name="category" placeholder="e.g. Food, Transport, Utilities" required>
      </div>
      <div class="form-group">
        <label class="label">Budget Amount (PKR)</label>
        <input class="input" type="number" name="amount" step="0.01" min="0.01" placeholder="10000" required>
      </div>
    </form>`;

  showModal('Add Budget', body, async (data, overlay) => {
    const form = overlay.querySelector('form');
    const category = form.querySelector('[name="category"]').value;
    const amount = form.querySelector('[name="amount"]').value;

    try {
      await apiPost('/api/budgets', { email: user.email, category, amount });
      showToast('Budget added!', 'success');
      overlay.remove();
      loadBudgets();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });
}

loadBudgets();