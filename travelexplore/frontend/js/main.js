/**
 * main.js — TravelExplore SPA
 *
 * Responsibilities:
 *  - SPA Router: show/hide <section class="view"> without page reloads
 *  - Auth state: stored in localStorage, nav updates dynamically
 *  - Fetch packages from PHP API and render cards
 *  - Handle login form, show alerts, persist session
 *  - Populate admin packages table
 */

'use strict';

// ── Configuration ──────────────────────────────────────────────────────────
// Update API_BASE to match your server path, e.g. 'http://localhost/travelexplore/backend/api.php'
const API_BASE = '../backend/api.php';

// ── State ──────────────────────────────────────────────────────────────────
const state = {
  user: null,          // { id, email, role } or null
  packages: [],        // cached package list
};

// ── DOM references ─────────────────────────────────────────────────────────
const views        = document.querySelectorAll('.view');
const navGuest     = document.querySelectorAll('.nav-guest');
const navUser      = document.querySelectorAll('.nav-user');
const navAdmin     = document.querySelectorAll('.nav-admin');
const navAuth      = document.querySelectorAll('.nav-auth');
const navUserName  = document.getElementById('navUserName');
const navLinks     = document.getElementById('navLinks');
const navToggle    = document.getElementById('navToggle');
const logoutBtn    = document.getElementById('logoutBtn');

const loginForm       = document.getElementById('loginForm');
const loginEmail      = document.getElementById('loginEmail');
const loginPassword   = document.getElementById('loginPassword');
const loginAlert      = document.getElementById('loginAlert');
const loginSubmit     = document.getElementById('loginSubmit');
const togglePassword  = document.getElementById('togglePassword');

const packagesGrid    = document.getElementById('packagesGrid');
const packagesEmpty   = document.getElementById('packagesEmpty');
const adminPackagesTbody = document.getElementById('adminPackagesTbody');
const adminPackageCount  = document.getElementById('adminPackageCount');

const toast = document.getElementById('toast');

// ── Utilities ──────────────────────────────────────────────────────────────

/** Format a number as USD currency string */
function formatPrice(price) {
  return new Intl.NumberFormat('en-US', {
    style: 'currency',
    currency: 'USD',
    minimumFractionDigits: 0,
    maximumFractionDigits: 0,
  }).format(price);
}

/** Show a toast message (auto-hides after 3 s) */
let toastTimer = null;
function showToast(message, type = 'default') {
  clearTimeout(toastTimer);
  toast.textContent = message;
  toast.className = `toast toast--${type}`;
  toast.classList.remove('d-none');

  toastTimer = setTimeout(() => {
    toast.classList.add('d-none');
  }, 3000);
}

// ── Router ─────────────────────────────────────────────────────────────────

/**
 * Navigate to a view by name ('home', 'destinations', 'login',
 * 'dashboard', 'admin').
 * Guards unauthorised access and triggers data loading.
 */
function navigate(viewName) {
  // Access guards
  if (viewName === 'dashboard' && (!state.user)) {
    showToast('Please log in to access your dashboard.', 'error');
    viewName = 'login';
  }
  if (viewName === 'admin' && state.user?.role !== 'admin') {
    showToast('Admin access required.', 'error');
    viewName = state.user ? 'dashboard' : 'login';
  }
  // If already logged in, skip login page
  if (viewName === 'login' && state.user) {
    viewName = state.user.role === 'admin' ? 'admin' : 'dashboard';
  }

  // Hide all views, show the target
  views.forEach(v => v.classList.add('d-none'));
  const target = document.getElementById(`view-${viewName}`);
  if (target) {
    target.classList.remove('d-none');
  }

  // Close mobile nav
  navLinks.classList.remove('is-open');

  // Trigger view-specific data loads
  if (viewName === 'destinations') {
    loadPackages();
  }
  if (viewName === 'admin') {
    loadPackages(true);  // force-refresh for admin
  }

  // Scroll to top
  window.scrollTo({ top: 0, behavior: 'smooth' });
}

// ── Auth / Session ─────────────────────────────────────────────────────────

function loadSession() {
  try {
    const stored = localStorage.getItem('te_user');
    if (stored) {
      state.user = JSON.parse(stored);
    }
  } catch {
    state.user = null;
  }
}

function saveSession(user) {
  state.user = user;
  localStorage.setItem('te_user', JSON.stringify(user));
}

function clearSession() {
  state.user = null;
  localStorage.removeItem('te_user');
}

function updateNav() {
  const isLoggedIn = Boolean(state.user);
  const isAdmin    = state.user?.role === 'admin';

  // Guest nav items
  navGuest.forEach(el => el.classList.toggle('d-none', isLoggedIn));
  // User-only nav items
  navUser.forEach(el  => el.classList.toggle('d-none', !(isLoggedIn && !isAdmin)));
  // Admin-only nav items
  navAdmin.forEach(el => el.classList.toggle('d-none', !isAdmin));
  // Shared auth items (username + logout)
  navAuth.forEach(el  => el.classList.toggle('d-none', !isLoggedIn));

  if (isLoggedIn) {
    navUserName.textContent = state.user.email.split('@')[0];
  }
}

// ── API Calls ──────────────────────────────────────────────────────────────

async function fetchPackages() {
  const res  = await fetch(`${API_BASE}?action=packages`);
  if (!res.ok) throw new Error(`HTTP ${res.status}`);
  const json = await res.json();
  if (!json.success) throw new Error(json.message || 'Unknown error');
  return json.data;
}

async function fetchLogin(email, password) {
  const res  = await fetch(`${API_BASE}?action=login`, {
    method:  'POST',
    headers: { 'Content-Type': 'application/json' },
    body:    JSON.stringify({ email, password }),
  });
  const json = await res.json();
  if (!json.success) throw new Error(json.message || 'Login failed');
  return json.data;
}

// ── Rendering ──────────────────────────────────────────────────────────────

function renderPackageCard(pkg) {
  return `
    <article class="pkg-card">
      <div class="pkg-card__img-wrap">
        <img
          class="pkg-card__img"
          src="${escHtml(pkg.image_url)}"
          alt="${escHtml(pkg.title)}"
          loading="lazy"
          onerror="this.src='https://images.unsplash.com/photo-1488085061387-422e29b40080?w=800&q=80'"
        />
        <span class="pkg-card__badge">Featured</span>
      </div>
      <div class="pkg-card__body">
        <h3 class="pkg-card__title">${escHtml(pkg.title)}</h3>
        <p class="pkg-card__desc">${escHtml(pkg.description)}</p>
        <div class="pkg-card__footer">
          <div class="pkg-card__price">
            ${formatPrice(pkg.price)} <span>/ person</span>
          </div>
          <button
            class="btn btn--primary btn--sm"
            onclick="handleBookNow(${Number(pkg.id)}, '${escHtml(pkg.title)}')"
          >
            Book Now
          </button>
        </div>
      </div>
    </article>
  `;
}

function renderAdminRow(pkg) {
  return `
    <tr>
      <td>${Number(pkg.id)}</td>
      <td>
        <img
          src="${escHtml(pkg.image_url)}"
          alt="${escHtml(pkg.title)}"
          loading="lazy"
          onerror="this.src='https://images.unsplash.com/photo-1488085061387-422e29b40080?w=800&q=80'"
        />
      </td>
      <td>
        <strong>${escHtml(pkg.title)}</strong><br>
        <span class="text-muted text-sm">${formatPrice(pkg.price)} / person</span>
      </td>
      <td>${formatPrice(pkg.price)}</td>
      <td>
        <div class="table-actions">
          <button class="btn btn--outline btn--sm">Edit</button>
          <button class="btn btn--danger btn--sm">Delete</button>
        </div>
      </td>
    </tr>
  `;
}

/** Simple HTML-escape to prevent XSS */
function escHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;')
    .replace(/'/g, '&#039;');
}

async function loadPackages(forceAdmin = false) {
  // Show skeletons while loading (destinations view only)
  if (!forceAdmin) {
    packagesGrid.innerHTML = '';
    for (let i = 0; i < 6; i++) {
      packagesGrid.insertAdjacentHTML('beforeend', '<div class="skeleton-card"></div>');
    }
    packagesEmpty.classList.add('d-none');
  }

  try {
    const packages = await fetchPackages();
    state.packages  = packages;

    // Destinations grid
    if (!forceAdmin) {
      packagesGrid.innerHTML = '';
      if (packages.length === 0) {
        packagesEmpty.classList.remove('d-none');
      } else {
        packages.forEach(pkg => {
          packagesGrid.insertAdjacentHTML('beforeend', renderPackageCard(pkg));
        });
      }
    }

    // Admin table
    if (forceAdmin || state.packages.length) {
      if (adminPackagesTbody) {
        adminPackagesTbody.innerHTML = packages.map(renderAdminRow).join('');
      }
      if (adminPackageCount) {
        adminPackageCount.textContent = packages.length;
      }
    }
  } catch (err) {
    console.error('Failed to load packages:', err);
    if (!forceAdmin) {
      packagesGrid.innerHTML = '';
      packagesEmpty.classList.remove('d-none');
    }
    showToast('Could not load packages. Is the PHP server running?', 'error');
  }
}

// ── Event Handlers ─────────────────────────────────────────────────────────

function handleBookNow(pkgId, pkgTitle) {
  if (!state.user) {
    showToast('Please log in to book a package.', 'error');
    navigate('login');
    return;
  }
  showToast(`"${pkgTitle}" booking coming soon!`, 'success');
}
// Expose for inline onclick
window.handleBookNow = handleBookNow;

loginForm.addEventListener('submit', async (e) => {
  e.preventDefault();

  const email    = loginEmail.value.trim();
  const password = loginPassword.value;

  if (!email || !password) {
    showLoginAlert('Please enter both email and password.', 'error');
    return;
  }

  loginSubmit.disabled    = true;
  loginSubmit.textContent = 'Signing in…';
  hideLoginAlert();

  try {
    const user = await fetchLogin(email, password);
    saveSession(user);
    updateNav();
    showToast(`Welcome back, ${user.email.split('@')[0]}!`, 'success');
    navigate(user.role === 'admin' ? 'admin' : 'dashboard');
  } catch (err) {
    showLoginAlert(err.message || 'Login failed. Please try again.', 'error');
  } finally {
    loginSubmit.disabled    = false;
    loginSubmit.textContent = 'Sign In';
  }
});

togglePassword.addEventListener('click', () => {
  const isPassword = loginPassword.type === 'password';
  loginPassword.type = isPassword ? 'text' : 'password';
  togglePassword.textContent = isPassword ? '🙈' : '👁';
});

logoutBtn.addEventListener('click', () => {
  clearSession();
  updateNav();
  showToast('You have been logged out.', 'default');
  navigate('home');
});

// Hamburger menu
navToggle.addEventListener('click', () => {
  navLinks.classList.toggle('is-open');
});

// ── Global navigation delegation ───────────────────────────────────────────
// Handles all [data-view="..."] anchor/button clicks anywhere on the page
document.addEventListener('click', (e) => {
  const trigger = e.target.closest('[data-view]');
  if (!trigger) return;
  e.preventDefault();
  navigate(trigger.dataset.view);
});

// ── Login alert helpers ─────────────────────────────────────────────────────
function showLoginAlert(message, type) {
  loginAlert.textContent = message;
  loginAlert.className   = `form-alert form-alert--${type}`;
  loginAlert.classList.remove('d-none');
}

function hideLoginAlert() {
  loginAlert.classList.add('d-none');
}

// ── Initialise ─────────────────────────────────────────────────────────────
(function init() {
  loadSession();
  updateNav();
  navigate('home');
})();
