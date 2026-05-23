const user = requireAuth();
requireAdmin();

function requireAdmin() {
  if (user.role !== 'Admin') {
    window.location.href = '/dashboard.html';
    return false;
  }
  return true;
}

document.getElementById('welcomeMsg').textContent = 'Admin: ' + user.email;

async function adminGet(path) {
  return apiGet(path);
}

async function adminPost(path, data) {
  return apiPost(path, data);
}

// ---- Dashboard ----

async function loadAdminDashboard() {
  try {
    const users = await adminGet('/api/admin/users');
    document.getElementById('statUsers').textContent = users.length;

    const report = await adminGet('/api/admin/financial-report');
    document.getElementById('statBudgets').textContent = report.overall.totalEntries;
    document.getElementById('statTotalAmount').textContent = 'PKR ' + formatCurrency(report.overall.totalAmount);
    document.getElementById('statAvgBudget').textContent = 'PKR ' + formatCurrency(report.overall.avgBudget);

    const activity = await adminGet('/api/admin/activity');
    document.getElementById('statActivity').textContent = activity.length;
  } catch (err) {
    showToast('Failed to load dashboard: ' + err.message, 'error');
  }
}

function showChangePasswordModal() {
  const overlay = showModal('Change Password',
    '<form id="cpForm">' +
    '<div class="form-group"><label class="label">Current Password</label><input class="input" type="password" name="currentPassword" required></div>' +
    '<div class="form-group"><label class="label">New Password</label><input class="input" type="password" name="newPassword" required minlength="3"></div>' +
    '</form>',
    async (data, el) => {
      if (data.newPassword.length < 3) {
        showToast('Password must be at least 3 characters', 'error');
        return;
      }
      try {
        await adminPost('/api/admin/change-password', {
          email: user.email,
          newPassword: data.newPassword
        });
        showToast('Password changed successfully', 'success');
        el.remove();
      } catch (err) {
        showToast(err.message, 'error');
      }
    }
  );
  overlay.querySelector('.modal-title').textContent = 'Change Password';
}

// ---- Users ----

async function loadUsers() {
  const container = document.getElementById('userBody');
  showLoading(document.getElementById('userTableContainer'));
  try {
    const users = await adminGet('/api/admin/users');
    if (users.length === 0) {
      container.innerHTML = '<tr><td colspan="3" class="text-center text-muted">No users found</td></tr>';
      return;
    }
    container.innerHTML = users.map((u, i) =>
      '<tr>' +
      '<td>' + (i + 1) + '</td>' +
      '<td>' + u.email + '</td>' +
      '<td><button class="btn btn-destructive btn-sm" onclick="showDeleteModal(\'' + u.email.replace(/'/g, "\\'") + '\')">Delete</button></td>' +
      '</tr>'
    ).join('');
  } catch (err) {
    showError(document.getElementById('userTableContainer'), err.message);
  }
}

function showDeleteModal(clientEmail) {
  const overlay = showModal('Delete Client Account',
    '<form id="deleteForm">' +
    '<div class="form-group"><label class="label">Client Email</label><input class="input" type="text" value="' + clientEmail + '" readonly></div>' +
    '<div class="form-group"><label class="label">Your Admin Password</label><input class="input" type="password" name="adminPassword" required></div>' +
    '</form>',
    async (data, el) => {
      try {
        await adminPost('/api/admin/delete-user', {
          adminEmail: user.email,
          adminPassword: data.adminPassword,
          clientEmail: clientEmail
        });
        showToast('Client account deleted', 'success');
        el.remove();
        loadUsers();
      } catch (err) {
        showToast(err.message, 'error');
      }
    }
  );
  overlay.querySelector('.modal-title').textContent = 'Delete Client Account';
}

// ---- Activity ----

async function loadActivity() {
  const container = document.getElementById('activityBody');
  showLoading(document.getElementById('activityTableContainer'));
  try {
    const logs = await adminGet('/api/admin/activity');
    if (logs.length === 0) {
      container.innerHTML = '<tr><td colspan="4" class="text-center text-muted">No activity records found</td></tr>';
      return;
    }
    container.innerHTML = logs.map(log =>
      '<tr>' +
      '<td>' + log.user_email + '</td>' +
      '<td>' + log.activity_type + '</td>' +
      '<td>' + (log.activity_details || '-') + '</td>' +
      '<td>' + log.timestamp + '</td>' +
      '</tr>'
    ).join('');
  } catch (err) {
    showError(document.getElementById('activityTableContainer'), err.message);
  }
}

// ---- Reports ----

async function loadReport() {
  const container = document.getElementById('reportContainer');
  const overallContainer = document.getElementById('overallStats');
  showLoading(container);
  try {
    const report = await adminGet('/api/admin/financial-report');

    if (report.perUser.length === 0) {
      container.innerHTML = '<div class="empty-state"><p>No budget records found</p></div>';
    } else {
      const grouped = {};
      for (const entry of report.perUser) {
        if (!grouped[entry.email]) grouped[entry.email] = [];
        grouped[entry.email].push(entry);
      }
      container.innerHTML = Object.entries(grouped).map(([email, entries]) => {
        const total = entries.reduce((s, e) => s + e.amount, 0);
        return '<div class="card" style="margin-bottom: 1rem;">' +
          '<div class="card-header"><div class="card-title">' + email + '</div></div>' +
          '<table><thead><tr><th>Category</th><th>Budget</th></tr></thead><tbody>' +
          entries.map(e => '<tr><td>' + e.category + '</td><td>PKR ' + formatCurrency(e.amount) + '</td></tr>').join('') +
          '<tr class="total-row"><td><strong>Total</strong></td><td><strong>PKR ' + formatCurrency(total) + '</strong></td></tr>' +
          '</tbody></table></div>';
      }).join('');
    }

    overallContainer.innerHTML =
      '<div class="card">' +
      '<div class="card-header"><div class="card-title">System Summary</div></div>' +
      '<div class="grid grid-cols-2 gap-4" style="padding: 1rem;">' +
      '<div class="stat-card"><div class="stat-value">' + report.overall.totalUsers + '</div><div class="stat-label">Total Users</div></div>' +
      '<div class="stat-card"><div class="stat-value">' + report.overall.totalEntries + '</div><div class="stat-label">Budget Entries</div></div>' +
      '<div class="stat-card"><div class="stat-value">PKR ' + formatCurrency(report.overall.totalAmount) + '</div><div class="stat-label">Total Budgeted</div></div>' +
      '<div class="stat-card"><div class="stat-value">PKR ' + formatCurrency(report.overall.avgBudget) + '</div><div class="stat-label">Avg / User</div></div>' +
      '</div></div>';
  } catch (err) {
    showError(container, err.message);
  }
}
