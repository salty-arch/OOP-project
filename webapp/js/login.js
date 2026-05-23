var registerMode = false;

toggleDark();

function togglePassword() {
  var input = document.getElementById('password');
  if (input.type === 'password') {
    input.type = 'text';
  } else {
    input.type = 'password';
  }
}

function switchToRegister() {
  registerMode = true;
  document.getElementById('submitBtn').textContent = 'Register';
  document.querySelector('h1').textContent = 'Create Account';
  document.querySelector('.glass-form p.text-muted').textContent = 'Sign up to start managing your finances';
  // Change the password toggle button to be on the regPassword field
  document.getElementById('email').placeholder = 'Choose your email';
  document.getElementById('password').placeholder = 'Choose a password';
  document.querySelector('.link-accent').parentElement.style.display = 'none';
  document.querySelector('p.text-center').innerHTML =
    'Already have an account? <a href="#" onclick="event.preventDefault(); switchToLogin()" class="link-accent">Sign In</a>';
}

function switchToLogin() {
  registerMode = false;
  document.getElementById('submitBtn').textContent = 'Sign In';
  document.querySelector('h1').textContent = 'Welcome';
  document.querySelector('.glass-form p.text-muted').textContent = 'Access your account and continue your journey';
  document.getElementById('email').placeholder = 'Enter your email address';
  document.getElementById('password').placeholder = 'Enter your password';
  document.querySelector('.link-accent').parentElement.style.display = '';
  document.querySelector('p.text-center').innerHTML =
    'New here? <a href="#" onclick="event.preventDefault(); switchToRegister()" class="link-accent">Create Account</a>';
}

async function handleAuth(e) {
  e.preventDefault();
  var errorEl = document.getElementById('errorMsg');
  errorEl.style.display = 'none';

  var email = document.getElementById('email').value.trim();
  var password = document.getElementById('password').value;

  if (!email || !password) {
    errorEl.textContent = 'Please fill in all fields';
    errorEl.style.display = 'block';
    return false;
  }

  if (registerMode && password.length < 3) {
    errorEl.textContent = 'Password must be at least 3 characters';
    errorEl.style.display = 'block';
    return false;
  }

  var endpoint = registerMode ? '/api/register' : '/api/login';
  var btn = document.getElementById('submitBtn');
  btn.disabled = true;
  btn.textContent = 'Loading...';

  try {
    var result = await apiPost(endpoint, { email: email, password: password });
    localStorage.setItem('user', JSON.stringify(result));
    showToast(registerMode ? 'Registration successful!' : 'Login successful!', 'success');
    if (result.role === 'Admin') {
      window.location.href = '/admin/dashboard.html';
    } else {
      window.location.href = '/dashboard.html';
    }
  } catch (err) {
    errorEl.textContent = err.message;
    errorEl.style.display = 'block';
  } finally {
    btn.disabled = false;
    btn.textContent = registerMode ? 'Register' : 'Sign In';
  }
  return false;
}
if (getUser()) {
  window.location.href = '/dashboard.html';
}