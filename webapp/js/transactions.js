const user = requireAuth();

async function loadTransactions() {
  const tbody = document.getElementById('txnBody');
  try {
    const txns = await apiGet(`/api/transactions?email=${encodeURIComponent(user.email)}`);

    if (txns.length === 0) {
      tbody.innerHTML = '<tr><td colspan="3" class="text-center text-muted">No transactions yet</td></tr>';
      return;
    }

    tbody.innerHTML = txns.map(t => {
      const cls = t.type === 'income' ? 'badge-success' : 'badge-danger';
      return `<tr>
        <td>${t.date ? t.date.substring(0, 10) : '-'}</td>
        <td><span class="badge ${cls}">${t.type}</span></td>
        <td style="${t.type === 'income' ? 'color: var(--chart-3)' : 'color: var(--destructive)'}">
          ${t.type === 'income' ? '+' : '-'} PKR ${formatCurrency(t.amount)}
        </td>
      </tr>`;
    }).join('');
  } catch (err) {
    document.getElementById('txnTableContainer').innerHTML =
      `<div class="empty-state"><p>Failed to load: ${err.message}</p></div>`;
  }
}

function showAddTransactionModal() {
  const body = `
    <form id="txnForm">
      <div class="form-group">
        <label class="label">Type</label>
        <select class="select" name="type" id="txnType" onchange="toggleCategoryField()">
          <option value="income">Income</option>
          <option value="expense">Expense</option>
        </select>
      </div>
      <div class="form-group">
        <label class="label">Category (for expense)</label>
        <input class="input" name="category" id="txnCategory" placeholder="e.g. Food, Transport">
      </div>
      <div class="form-group">
        <label class="label">Amount (PKR)</label>
        <input class="input" type="number" name="amount" step="0.01" min="0.01" placeholder="1000" required>
      </div>
    </form>`;

  showModal('Add Transaction', body, async (data, overlay) => {
    const type = document.getElementById('txnType').value;
    const amount = document.querySelector('[name="amount"]').value;
    const category = document.getElementById('txnCategory').value;

    try {
      await apiPost('/api/transactions', {
        email: user.email,
        type,
        amount,
        category: type === 'expense' ? category : ''
      });
      showToast('Transaction added!', 'success');
      overlay.remove();
      loadTransactions();
    } catch (err) {
      showToast(err.message, 'error');
    }
  });
}

function toggleCategoryField() {
  const cat = document.getElementById('txnCategory');
  cat.disabled = document.getElementById('txnType').value !== 'expense';
  if (cat.disabled) cat.value = '';
}

loadTransactions();