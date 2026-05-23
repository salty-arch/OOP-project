const user = requireAuth();
document.getElementById('welcomeMsg').textContent = `Welcome back, ${user.email}!`;

async function loadDashboard() {
  try {
    const data = await apiGet(`/api/summary?email=${encodeURIComponent(user.email)}`);

    document.getElementById('statIncome').textContent = 'PKR ' + formatCurrency(data.totalIncome);
    document.getElementById('statExpense').textContent = 'PKR ' + formatCurrency(data.totalExpense);

    const savingsEl = document.getElementById('statSavings');
    savingsEl.textContent = 'PKR ' + formatCurrency(Math.abs(data.savings));
    savingsEl.style.color = data.savings >= 0 ? 'var(--chart-3)' : 'var(--destructive)';

    document.getElementById('statBudget').textContent = 'PKR ' + formatCurrency(data.totalSpent);
    document.getElementById('statTxCount').textContent = data.transactionCount;
    document.getElementById('statBudgetCount').textContent = data.budgetCount;
  } catch (err) {
    showToast('Failed to load dashboard: ' + err.message, 'error');
  }
}

loadDashboard();
