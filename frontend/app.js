/* ========================================================
   Splitwise Frontend — app.js
   API Base: http://localhost:8080
   
   API facts (from DTOs):
     POST /api/users           -> { userId, name, email }
     POST /api/auth/login      -> { token, message }  (JWT sub = email)
     POST /api/groups          -> { groupId, name, createdByName }
     GET  /api/groups/{id}/balances -> [{ userName, netBalance }]
     POST /api/expenses        -> { expenseId, description, amount, paidByName }
     GET  /api/expenses/group/{id} -> [{ expenseId, description, amount, paidByName }]
======================================================== */

const API = 'http://localhost:8080';

// ─── STATE ───────────────────────────────────────────────
let state = {
  token:         null,
  userId:        null,
  userName:      null,
  userEmail:     null,
  groups:        [],   // [{ groupId, name }]
  totalExpenses: 0,
};

// ─── INIT ─────────────────────────────────────────────────
(function init() {
  const saved = JSON.parse(localStorage.getItem('sw_session') || 'null');
  if (saved && saved.token) {
    Object.assign(state, saved);
    enterApp();
  }
})();

// ─── JWT DECODE (client-side, no verification) ────────────
function decodeJwtPayload(token) {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    return JSON.parse(atob(base64));
  } catch { return {}; }
}

// ─── TOAST ────────────────────────────────────────────────
let toastTimer;
function showToast(msg, type = 'info') {
  const el = document.getElementById('toast');
  el.textContent = msg;
  el.className = 'toast ' + type + ' show';
  clearTimeout(toastTimer);
  toastTimer = setTimeout(() => el.classList.remove('show'), 3500);
}

// ─── AUTH TABS ────────────────────────────────────────────
function switchAuthTab(tab) {
  document.getElementById('login-form').classList.toggle('hidden', tab !== 'login');
  document.getElementById('register-form').classList.toggle('hidden', tab !== 'register');
  document.getElementById('tab-login').classList.toggle('active', tab === 'login');
  document.getElementById('tab-register').classList.toggle('active', tab === 'register');
}

// ─── LOADING HELPERS ──────────────────────────────────────
function setLoading(btnId, loading) {
  const btn = document.getElementById(btnId);
  if (!btn) return;
  btn.querySelector('.btn-label').classList.toggle('hidden', loading);
  btn.querySelector('.btn-spinner').classList.toggle('hidden', !loading);
  btn.disabled = loading;
}

// ─── API HELPER ───────────────────────────────────────────
async function api(method, path, body, auth = true) {
  const headers = { 'Content-Type': 'application/json' };
  if (auth && state.token) headers['Authorization'] = 'Bearer ' + state.token;
  const res = await fetch(API + path, {
    method,
    headers,
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  let data;
  try { data = JSON.parse(text); } catch { data = text; }
  if (!res.ok) {
    const msg = (data && typeof data === 'object' && (data.message || data.error)) || text || res.statusText;
    throw new Error(msg);
  }
  return data;
}

// ─── AUTH: REGISTER ───────────────────────────────────────
async function handleRegister(e) {
  e.preventDefault();
  setLoading('register-btn', true);
  try {
    const body = {
      name:     document.getElementById('reg-name').value.trim(),
      email:    document.getElementById('reg-email').value.trim(),
      password: document.getElementById('reg-password').value,
    };
    const res = await api('POST', '/api/users', body, false);
    // res = { userId, name, email }
    showToast('Account created! User ID: ' + res.userId + '. Now log in.', 'success');
    switchAuthTab('login');
    document.getElementById('login-email').value = body.email;
    document.getElementById('register-form').reset();
  } catch (err) {
    showToast(err.message || 'Registration failed', 'error');
  } finally {
    setLoading('register-btn', false);
  }
}

// ─── AUTH: LOGIN ──────────────────────────────────────────
async function handleLogin(e) {
  e.preventDefault();
  setLoading('login-btn', true);
  try {
    const email    = document.getElementById('login-email').value.trim();
    const password = document.getElementById('login-password').value;
    const res      = await api('POST', '/api/auth/login', { email, password }, false);
    // res = { token, message }
    const payload  = decodeJwtPayload(res.token);
    // JWT sub = email; we derive display name from email prefix
    state.token     = res.token;
    state.userEmail = payload.sub || email;
    state.userName  = state.userEmail.split('@')[0];
    state.userId    = null; // backend doesn't return userId on login
    saveSession();
    enterApp();
  } catch (err) {
    showToast(err.message || 'Login failed. Check your credentials.', 'error');
  } finally {
    setLoading('login-btn', false);
  }
}

// ─── AUTH: LOGOUT ─────────────────────────────────────────
function handleLogout() {
  state = { token: null, userId: null, userName: null, userEmail: null, groups: [], totalExpenses: 0 };
  localStorage.removeItem('sw_session');
  document.getElementById('auth-page').className = 'page active';
  document.getElementById('app-page').className  = 'page hidden';
  showToast('Logged out successfully.', 'info');
}

// ─── SESSION ──────────────────────────────────────────────
function saveSession() {
  localStorage.setItem('sw_session', JSON.stringify({
    token:     state.token,
    userId:    state.userId,
    userName:  state.userName,
    userEmail: state.userEmail,
    groups:    state.groups,
  }));
}

// ─── ENTER APP ────────────────────────────────────────────
function enterApp() {
  document.getElementById('auth-page').className = 'page hidden';
  document.getElementById('app-page').className  = 'page active';
  updateSidebar();
  showSection('dashboard');
}

function updateSidebar() {
  const name = state.userName || state.userEmail || 'User';
  document.getElementById('sidebar-name').textContent = name;
  document.getElementById('sidebar-avatar').textContent = name.charAt(0).toUpperCase();
  document.getElementById('dash-name').textContent = name;
  document.getElementById('stat-user-id').textContent = state.userId !== null ? state.userId : 'N/A';
}

// ─── NAVIGATION ───────────────────────────────────────────
function showSection(name) {
  document.querySelectorAll('.content-section').forEach(s => s.classList.remove('active'));
  document.querySelectorAll('.nav-item').forEach(n => n.classList.remove('active'));
  const sec = document.getElementById('section-' + name);
  if (sec) sec.classList.add('active');
  const nav = document.getElementById('nav-' + name);
  if (nav) nav.classList.add('active');
  if (name === 'dashboard') refreshDashboard();
  if (name === 'groups') renderGroupsList();
}

// ─── DASHBOARD ────────────────────────────────────────────
function refreshDashboard() {
  document.getElementById('stat-groups').textContent   = state.groups.length;
  document.getElementById('stat-expenses').textContent = state.totalExpenses;
  document.getElementById('stat-user-id').textContent  = state.userId !== null ? state.userId : 'N/A';
  updateSidebar();
}

// ─── GROUPS: CREATE ───────────────────────────────────────
async function handleCreateGroup(e) {
  e.preventDefault();
  setLoading('cg-btn', true);
  try {
    const name      = document.getElementById('cg-name').value.trim();
    const creatorId = document.getElementById('cg-creator').value;
    const res = await api('POST', '/api/groups?creatorUserId=' + creatorId, { name });
    // res = { groupId, name, createdByName }
    state.groups.push({ groupId: res.groupId, name: res.name });
    saveSession();
    showToast('Group "' + res.name + '" created! (ID: ' + res.groupId + ')', 'success');
    closeModal('modal-create-group');
    document.getElementById('create-group-form').reset();
    renderGroupsList();
    refreshDashboard();
  } catch (err) {
    showToast(err.message || 'Failed to create group', 'error');
  } finally {
    setLoading('cg-btn', false);
  }
}

// ─── GROUPS: ADD MEMBER ───────────────────────────────────
async function handleAddMember(e) {
  e.preventDefault();
  try {
    const gid = document.getElementById('am-group-id').value;
    const uid = document.getElementById('am-user-id').value;
    await api('POST', '/api/groups/' + gid + '/members/' + uid);
    showToast('User ' + uid + ' added to Group ' + gid + '!', 'success');
    document.getElementById('add-member-form').reset();
  } catch (err) {
    showToast(err.message || 'Failed to add member', 'error');
  }
}

// ─── GROUPS: RENDER ───────────────────────────────────────
function renderGroupsList() {
  const container = document.getElementById('groups-list');
  const badge     = document.getElementById('groups-count');
  if (badge) badge.textContent = state.groups.length;
  if (!state.groups.length) {
    container.innerHTML = '<p class="empty-state">No groups yet. Create your first group!</p>';
    return;
  }
  container.innerHTML = state.groups.map(g => `
    <div class="list-item">
      <div class="list-item-icon">&#128101;</div>
      <div class="list-item-body">
        <span class="list-item-title">${escHtml(g.name)}</span>
        <span class="list-item-sub">Group ID: ${g.groupId}</span>
      </div>
      <div class="list-item-actions">
        <button class="btn btn-sm btn-secondary" onclick="quickLoadExpenses(${g.groupId})">Expenses</button>
        <button class="btn btn-sm btn-secondary" onclick="quickLoadBalances(${g.groupId})">Balances</button>
      </div>
    </div>
  `).join('');
}

function quickLoadExpenses(gid) {
  document.getElementById('fe-group-id').value = gid;
  showSection('expenses');
  fetchExpenses(gid);
}
function quickLoadBalances(gid) {
  document.getElementById('fb-group-id').value = gid;
  showSection('balances');
  fetchBalances(gid);
}

// ─── EXPENSES: FETCH ──────────────────────────────────────
async function handleFetchExpenses(e) {
  e.preventDefault();
  const gid = parseInt(document.getElementById('fe-group-id').value);
  await fetchExpenses(gid);
}

async function fetchExpenses(gid) {
  const label = document.getElementById('expenses-group-label');
  if (label) label.textContent = '— Group ' + gid;
  document.getElementById('expenses-list').innerHTML = '<p class="empty-state">Loading…</p>';
  try {
    const data = await api('GET', '/api/expenses/group/' + gid);
    state.totalExpenses = Array.isArray(data) ? data.length : 0;
    saveSession();
    refreshDashboard();
    renderExpenses(data, gid);
    renderDashRecent(data);
  } catch (err) {
    showToast(err.message || 'Failed to load expenses', 'error');
    document.getElementById('expenses-list').innerHTML = '<p class="empty-state">Failed to load expenses.</p>';
  }
}

function renderExpenses(data, gid) {
  const container = document.getElementById('expenses-list');
  if (!Array.isArray(data) || !data.length) {
    container.innerHTML = '<p class="empty-state">No expenses in this group yet.</p>';
    return;
  }
  container.innerHTML = data.map(exp => `
    <div class="list-item" id="exp-row-${exp.expenseId}">
      <div class="list-item-icon">&#128176;</div>
      <div class="list-item-body">
        <span class="list-item-title">${escHtml(exp.description)}</span>
        <span class="list-item-sub">Paid by <strong>${escHtml(exp.paidByName)}</strong> &bull; ID: ${exp.expenseId}</span>
      </div>
      <span class="list-item-amount">&#8377;${Number(exp.amount).toFixed(2)}</span>
      <div class="list-item-actions">
        <button class="btn btn-sm btn-secondary" onclick="openEditExpense(${exp.expenseId}, ${JSON.stringify(exp.description)}, ${exp.amount}, ${gid || 0})">Edit</button>
        <button class="btn btn-sm btn-danger" onclick="handleDeleteExpense(${exp.expenseId})">Delete</button>
      </div>
    </div>
  `).join('');
}

function renderDashRecent(data) {
  const container = document.getElementById('dash-recent');
  if (!container) return;
  if (!Array.isArray(data) || !data.length) {
    container.innerHTML = '<p class="empty-state">No recent expenses.</p>';
    return;
  }
  const recent = [...data].slice(-5).reverse();
  container.innerHTML = recent.map(exp => `
    <div class="list-item">
      <div class="list-item-icon">&#128176;</div>
      <div class="list-item-body">
        <span class="list-item-title">${escHtml(exp.description)}</span>
        <span class="list-item-sub">Paid by ${escHtml(exp.paidByName)}</span>
      </div>
      <span class="list-item-amount">&#8377;${Number(exp.amount).toFixed(2)}</span>
    </div>
  `).join('');
}

// ─── EXPENSES: ADD ────────────────────────────────────────
async function handleAddExpense(e) {
  e.preventDefault();
  setLoading('ae-btn', true);
  try {
    const splitRaw = document.getElementById('ae-split').value;
    const splitIds = splitRaw.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
    if (!splitIds.length) throw new Error('Please enter at least one user ID to split between.');
    const body = {
      groupId:             parseInt(document.getElementById('ae-group-id').value),
      paidByUserId:        parseInt(document.getElementById('ae-paid-by').value),
      amount:              parseFloat(document.getElementById('ae-amount').value),
      description:         document.getElementById('ae-desc').value.trim(),
      splitBetweenUserIds: splitIds,
    };
    await api('POST', '/api/expenses', body);
    showToast('Expense added successfully!', 'success');
    closeModal('modal-add-expense');
    document.getElementById('add-expense-form').reset();
    const visGid = document.getElementById('fe-group-id').value;
    if (visGid && parseInt(visGid) === body.groupId) await fetchExpenses(body.groupId);
  } catch (err) {
    showToast(err.message || 'Failed to add expense', 'error');
  } finally {
    setLoading('ae-btn', false);
  }
}

// ─── EXPENSES: DELETE ─────────────────────────────────────
async function handleDeleteExpense(expenseId) {
  if (!confirm('Delete this expense? This cannot be undone.')) return;
  try {
    await api('DELETE', '/api/expenses/' + expenseId);
    showToast('Expense deleted.', 'success');
    const row = document.getElementById('exp-row-' + expenseId);
    if (row) row.remove();
  } catch (err) {
    showToast(err.message || 'Failed to delete expense', 'error');
  }
}

// ─── EXPENSES: EDIT ───────────────────────────────────────
let editExpenseGroupId = null;

function openEditExpense(id, description, amount, gid) {
  editExpenseGroupId = gid;
  document.getElementById('ee-expense-id').value = id;
  document.getElementById('ee-desc').value       = description;
  document.getElementById('ee-amount').value     = amount;
  document.getElementById('ee-paid-by').value    = '';
  document.getElementById('ee-split').value      = '';
  openModal('modal-edit-expense');
}

async function handleEditExpense(e) {
  e.preventDefault();
  setLoading('ee-btn', true);
  try {
    const expenseId = document.getElementById('ee-expense-id').value;
    const splitRaw  = document.getElementById('ee-split').value;
    const splitIds  = splitRaw.split(',').map(s => parseInt(s.trim())).filter(n => !isNaN(n));
    if (!splitIds.length) throw new Error('Please enter at least one user ID to split between.');
    const body = {
      paidByUserId:        parseInt(document.getElementById('ee-paid-by').value),
      amount:              parseFloat(document.getElementById('ee-amount').value),
      description:         document.getElementById('ee-desc').value.trim(),
      splitBetweenUserIds: splitIds,
    };
    await api('PUT', '/api/expenses/' + expenseId, body);
    showToast('Expense updated!', 'success');
    closeModal('modal-edit-expense');
    if (editExpenseGroupId) await fetchExpenses(editExpenseGroupId);
  } catch (err) {
    showToast(err.message || 'Failed to update expense', 'error');
  } finally {
    setLoading('ee-btn', false);
  }
}

// ─── BALANCES ─────────────────────────────────────────────
async function handleFetchBalances(e) {
  e.preventDefault();
  const gid = parseInt(document.getElementById('fb-group-id').value);
  await fetchBalances(gid);
}

async function fetchBalances(gid) {
  const label = document.getElementById('balances-group-label');
  if (label) label.textContent = '— Group ' + gid;
  document.getElementById('balances-list').innerHTML = '<p class="empty-state">Loading…</p>';
  try {
    const data = await api('GET', '/api/groups/' + gid + '/balances');
    renderBalances(data);
  } catch (err) {
    showToast(err.message || 'Failed to load balances', 'error');
    document.getElementById('balances-list').innerHTML = '<p class="empty-state">Failed to load balances.</p>';
  }
}

function renderBalances(data) {
  const container = document.getElementById('balances-list');
  if (!Array.isArray(data) || !data.length) {
    container.innerHTML = '<p class="empty-state">No balances found for this group.</p>';
    return;
  }
  container.innerHTML = data.map(b => {
    const net = parseFloat(b.netBalance);
    const cls = net > 0 ? 'amount-positive' : net < 0 ? 'amount-negative' : 'amount-neutral';
    const arrow = net > 0 ? '&#9650;' : net < 0 ? '&#9660;' : '&#8226;';
    const label = net > 0 ? 'gets back' : net < 0 ? 'owes' : 'settled up';
    return `
      <div class="list-item">
        <div class="list-item-icon">${arrow}</div>
        <div class="list-item-body">
          <span class="list-item-title">${escHtml(b.userName)}</span>
          <span class="list-item-sub">${label}</span>
        </div>
        <span class="list-item-amount ${cls}">&#8377;${Math.abs(net).toFixed(2)}</span>
      </div>
    `;
  }).join('');
}

// ─── SETTLE ───────────────────────────────────────────────
async function handleSettle(e) {
  e.preventDefault();
  setLoading('settle-btn', true);
  try {
    const body = {
      groupId:      parseInt(document.getElementById('st-group-id').value),
      paidByUserId: parseInt(document.getElementById('st-paid-by').value),
      paidToUserId: parseInt(document.getElementById('st-paid-to').value),
      amount:       parseFloat(document.getElementById('st-amount').value),
    };
    await api('POST', '/api/settlements', body);
    showToast('Settlement recorded successfully!', 'success');
    document.getElementById('settle-form').reset();
  } catch (err) {
    showToast(err.message || 'Failed to record settlement', 'error');
  } finally {
    setLoading('settle-btn', false);
  }
}

// ─── MODALS ───────────────────────────────────────────────
function openModal(id) {
  document.getElementById(id).classList.add('open');
  document.body.style.overflow = 'hidden';
}
function closeModal(id) {
  document.getElementById(id).classList.remove('open');
  document.body.style.overflow = '';
}
function closeModalOnOverlay(e, id) {
  if (e.target === e.currentTarget) closeModal(id);
}

document.addEventListener('keydown', e => {
  if (e.key === 'Escape') {
    document.querySelectorAll('.modal-overlay.open').forEach(m => m.classList.remove('open'));
    document.body.style.overflow = '';
  }
});

// ─── UTILS ────────────────────────────────────────────────
function escHtml(str) {
  return String(str)
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}
