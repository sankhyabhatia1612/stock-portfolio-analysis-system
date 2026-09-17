// ==========================================
// ORACLE DATABASE API CONFIGURATION
// ==========================================
const API_BASE_URL = 'http://localhost:8080/api';

async function loadLiveOracleData() {
    try {
        const stocksRes = await fetch(`${API_BASE_URL}/stocks`);
        if (stocksRes.ok) {
            const oracleStocks = await stocksRes.json();
            if (oracleStocks && oracleStocks.length > 0) {
                stocksData = oracleStocks.map(s => ({
                    id: s.STOCK_ID,
                    symbol: s.STOCK_SYMBOL || s.SYMBOL,
                    name: s.STOCK_NAME || s.NAME,
                    exchange: s.EXCHANGE,
                    sector: s.SECTOR,
                    price: parseFloat(s.CURRENT_PRICE || s.PRICE || 0)
                }));
                renderAllTables();
                updateDashboardMetrics();
                initCharts();
            }
        }
    } catch (err) {
        console.log("Oracle API offline. Using local dataset fallback.");
    }
}

// ==========================================
// GLOBAL TAB SWITCHING ENGINE
// ==========================================
window.switchTab = function(tabId) {
    const navButtons = document.querySelectorAll('.nav-btn, .nav-link');
    navButtons.forEach(btn => {
        if (btn.getAttribute('data-tab') === tabId) {
            btn.classList.add('active');
        } else {
            btn.classList.remove('active');
        }
    });

    const allPanes = document.querySelectorAll('.tab-pane, .tab-content');
    allPanes.forEach(pane => {
        if (pane.id === tabId) {
            pane.classList.add('active');
            pane.style.setProperty('display', 'block', 'important');
        } else {
            pane.classList.remove('active');
            pane.style.setProperty('display', 'none', 'important');
        }
    });
};

// ==========================================
// LOCAL DATA STORES (UPDATED TO 3-4-6-4 METRICS)
// ==========================================
let usersData = [
    { id: 'U001', name: 'Aarav Sharma', email: 'aarav@example.com', risk: 'MEDIUM' },
    { id: 'U002', name: 'Diya Mehta', email: 'diya@example.com', risk: 'LOW' },
    { id: 'U003', name: 'Rohan Patel', email: 'rohan@example.com', risk: 'HIGH' }
];

let portfoliosData = [
    { id: 'P001', userId: 'U001', name: 'Growth Portfolio', balance: 50000.00 },
    { id: 'P002', userId: 'U002', name: 'Retirement Fund', balance: 125000.00 },
    { id: 'P003', userId: 'U003', name: 'Aggressive Tech', balance: 75000.00 },
    { id: 'P004', userId: 'U001', name: 'Dividend Income', balance: 35000.00 }
];

let stocksData = [
    { id: 'S001', symbol: 'RELIANCE', name: 'Reliance Industries', exchange: 'NSE', sector: 'Energy', price: 1450.00 },
    { id: 'S002', symbol: 'TCS', name: 'Tata Consultancy Services', exchange: 'NSE', sector: 'IT', price: 3200.00 },
    { id: 'S003', symbol: 'INFY', name: 'Infosys', exchange: 'NSE', sector: 'IT', price: 1550.00 },
    { id: 'S004', symbol: 'HDFCBANK', name: 'HDFC Bank', exchange: 'NSE', sector: 'Banking', price: 1750.00 },
    { id: 'S005', symbol: 'ITC', name: 'ITC Limited', exchange: 'NSE', sector: 'FMCG', price: 480.00 },
    { id: 'S006', symbol: 'WIPRO', name: 'Wipro Limited', exchange: 'NSE', sector: 'IT', price: 520.00 }
];

let transactionsData = [
    { id: 'T001', userId: 'U001', stockId: 'S001', type: 'BUY', qty: 10, price: 1450.00 },
    { id: 'T002', userId: 'U002', stockId: 'S002', type: 'SELL', qty: 5, price: 3200.00 },
    { id: 'T003', userId: 'U003', stockId: 'S003', type: 'BUY', qty: 20, price: 1550.00 },
    { id: 'T004', userId: 'U001', stockId: 'S004', type: 'BUY', qty: 15, price: 1750.00 }
];

let watchlistsData = [
    { id: 'W001', userId: 'U001', stockId: 'S003', symbol: 'INFY' },
    { id: 'W002', userId: 'U003', stockId: 'S004', symbol: 'HDFCBANK' }
];

let sectorChartInstance = null;
let portfolioChartInstance = null;

// ==========================================
// DOM INITIALIZATION
// ==========================================
document.addEventListener("DOMContentLoaded", () => {
    document.querySelectorAll('.nav-btn, .nav-link').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const target = btn.getAttribute('data-tab');
            if (target) window.switchTab(target);
        });
    });

    document.querySelectorAll('[data-switch-tab]').forEach(btn => {
        btn.addEventListener('click', (e) => {
            e.preventDefault();
            const target = btn.getAttribute('data-switch-tab');
            if (target) window.switchTab(target);
        });
    });

    const refreshBtn = document.getElementById('btn-refresh-users');
    if (refreshBtn) {
        refreshBtn.addEventListener('click', () => {
            renderUsersTable();
        });
    }

    window.switchTab('dashboard');

    renderAllTables();
    updateDashboardMetrics();
    initCharts();
    setupFormHandlers();
    setupFiltersAndSearch();
    loadLiveOracleData();
});

// ==========================================
// RENDERING ENGINE
// ==========================================
function renderAllTables() {
    renderUsersTable();
    renderStocksTable();
    renderPortfoliosTable();
    renderTransactionsTable();
    renderWatchlistsTable();
    renderDashStockSummary();
}

function updateDashboardMetrics() {
    const totalUsersEl = document.getElementById('metric-total-users');
    const activePortfoliosEl = document.getElementById('metric-active-portfolios');
    const listedStocksEl = document.getElementById('metric-listed-stocks');
    const totalTradesEl = document.getElementById('metric-total-trades');

    if (totalUsersEl) totalUsersEl.textContent = usersData.length;
    if (activePortfoliosEl) activePortfoliosEl.textContent = portfoliosData.length;
    if (listedStocksEl) listedStocksEl.textContent = stocksData.length;
    if (totalTradesEl) totalTradesEl.textContent = transactionsData.length;
}

function renderUsersTable(filterText = '') {
    const tbody = document.getElementById('users-tbody');
    if (!tbody) return;
    tbody.innerHTML = '';

    const filtered = usersData.filter(u => 
        u.id.toLowerCase().includes(filterText.toLowerCase()) ||
        u.name.toLowerCase().includes(filterText.toLowerCase()) ||
        u.email.toLowerCase().includes(filterText.toLowerCase())
    );

    filtered.forEach(u => {
        const tr = document.createElement('tr');
        
        let badgeClass = 'badge-medium';
        const riskVal = (u.risk || '').toUpperCase();
        if (riskVal === 'LOW' || riskVal === 'CONSERVATIVE') badgeClass = 'badge-low';
        if (riskVal === 'HIGH' || riskVal === 'AGGRESSIVE') badgeClass = 'badge-high';

        tr.innerHTML = `
            <td><strong>${u.id}</strong></td>
            <td>${u.name}</td>
            <td>${u.email}</td>
            <td><span class="badge-pill ${badgeClass}">${riskVal || 'MEDIUM'}</span></td>
            <td><button class="btn btn-danger btn-sm" onclick="deleteRecord('users', '${u.id}')">Delete</button></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderStocksTable(filterText = '') {
    const tbody = document.getElementById('stocks-tbody');
    if (!tbody) return;
    tbody.innerHTML = '';

    const filtered = stocksData.filter(s => 
        s.id.toLowerCase().includes(filterText.toLowerCase()) ||
        s.symbol.toLowerCase().includes(filterText.toLowerCase()) ||
        s.name.toLowerCase().includes(filterText.toLowerCase()) ||
        s.sector.toLowerCase().includes(filterText.toLowerCase())
    );

    filtered.forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${s.id}</strong></td>
            <td><span class="stock-symbol">${s.symbol}</span></td>
            <td>${s.name}</td>
            <td>${s.exchange}</td>
            <td>${s.sector}</td>
            <td>₹${parseFloat(s.price).toFixed(2)}</td>
            <td><button class="btn btn-danger btn-sm" onclick="deleteRecord('stocks', '${s.id}')">Delete</button></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderPortfoliosTable(filterText = '') {
    const tbody = document.getElementById('portfolios-tbody');
    if (!tbody) return;
    tbody.innerHTML = '';

    const filtered = portfoliosData.filter(p =>
        p.id.toLowerCase().includes(filterText.toLowerCase()) ||
        p.userId.toLowerCase().includes(filterText.toLowerCase()) ||
        p.name.toLowerCase().includes(filterText.toLowerCase())
    );

    filtered.forEach(p => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${p.id}</strong></td>
            <td>${p.userId}</td>
            <td>${p.name}</td>
            <td>₹${parseFloat(p.balance).toFixed(2)}</td>
            <td><button class="btn btn-danger btn-sm" onclick="deleteRecord('portfolios', '${p.id}')">Delete</button></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderTransactionsTable(filterText = '') {
    const tbody = document.getElementById('transactions-tbody');
    if (!tbody) return;
    tbody.innerHTML = '';

    const filtered = transactionsData.filter(t =>
        t.id.toLowerCase().includes(filterText.toLowerCase()) ||
        t.userId.toLowerCase().includes(filterText.toLowerCase()) ||
        t.stockId.toLowerCase().includes(filterText.toLowerCase())
    );

    filtered.forEach(t => {
        const tr = document.createElement('tr');
        const badgeClass = t.type === 'BUY' ? 'badge-buy' : 'badge-sell';
        tr.innerHTML = `
            <td><strong>${t.id}</strong></td>
            <td>${t.userId}</td>
            <td>${t.stockId}</td>
            <td><span class="badge ${badgeClass}">${t.type}</span></td>
            <td>${t.qty}</td>
            <td>₹${parseFloat(t.price).toFixed(2)}</td>
            <td><button class="btn btn-danger btn-sm" onclick="deleteRecord('transactions', '${t.id}')">Delete</button></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderWatchlistsTable(filterText = '') {
    const tbody = document.getElementById('watchlists-tbody');
    if (!tbody) return;
    tbody.innerHTML = '';

    const filtered = watchlistsData.filter(w =>
        w.id.toLowerCase().includes(filterText.toLowerCase()) ||
        w.userId.toLowerCase().includes(filterText.toLowerCase()) ||
        w.symbol.toLowerCase().includes(filterText.toLowerCase())
    );

    filtered.forEach(w => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${w.id}</strong></td>
            <td>${w.userId}</td>
            <td>${w.stockId}</td>
            <td><span class="stock-symbol">${w.symbol}</span></td>
            <td><button class="btn btn-danger btn-sm" onclick="deleteRecord('watchlists', '${w.id}')">Delete</button></td>
        `;
        tbody.appendChild(tr);
    });
}

function renderDashStockSummary() {
    const tbody = document.getElementById('dash-stocks-summary-tbody');
    if (!tbody) return;
    tbody.innerHTML = '';

    stocksData.slice(0, 5).forEach(s => {
        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td><strong>${s.symbol}</strong></td>
            <td>${s.name}</td>
            <td>${s.sector}</td>
            <td>₹${parseFloat(s.price).toFixed(2)}</td>
        `;
        tbody.appendChild(tr);
    });
}

function setupFormHandlers() {
    const userForm = document.getElementById('form-add-user');
    if (userForm) {
        userForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const id = document.getElementById('user-id').value.trim();
            
            if (usersData.some(u => u.id.toUpperCase() === id.toUpperCase())) {
                alert(`Primary Key Violation: User ID '${id}' already exists!`);
                return;
            }

            const fname = document.getElementById('user-fname').value.trim();
            const lname = document.getElementById('user-lname').value.trim();
            const email = document.getElementById('user-email').value.trim();
            const risk = document.getElementById('user-risk').value;

            usersData.push({
                id: id,
                name: `${fname} ${lname}`,
                email: email,
                risk: risk
            });

            userForm.reset();
            renderUsersTable();
            updateDashboardMetrics();
        });
    }

    const stockForm = document.getElementById('form-add-stock');
    if (stockForm) {
        stockForm.addEventListener('submit', (e) => {
            e.preventDefault();
            const id = document.getElementById('stock-id').value.trim();
            if (stocksData.some(s => s.id.toUpperCase() === id.toUpperCase())) {
                alert(`Primary Key Violation: Stock ID '${id}' already exists!`);
                return;
            }
            stocksData.push({
                id: id,
                symbol: document.getElementById('stock-symbol').value.trim().toUpperCase(),
                name: document.getElementById('stock-name').value.trim(),
                exchange: document.getElementById('stock-exchange').value,
                sector: document.getElementById('stock-sector').value.trim(),
                price: parseFloat(document.getElementById('stock-price').value)
            });
            stockForm.reset();
            renderStocksTable();
            renderDashStockSummary();
            updateDashboardMetrics();
            initCharts();
        });
    }
}

function setupFiltersAndSearch() {
    const searchUsers = document.getElementById('search-users');
    if (searchUsers) searchUsers.addEventListener('input', (e) => renderUsersTable(e.target.value));

    const searchStocks = document.getElementById('search-stocks');
    if (searchStocks) searchStocks.addEventListener('input', (e) => renderStocksTable(e.target.value));
}

window.deleteRecord = function(table, id) {
    if (!confirm(`Are you sure you want to delete record ${id}?`)) return;
    if (table === 'users') usersData = usersData.filter(u => u.id !== id);
    if (table === 'stocks') stocksData = stocksData.filter(s => s.id !== id);
    if (table === 'portfolios') portfoliosData = portfoliosData.filter(p => p.id !== id);
    if (table === 'transactions') transactionsData = transactionsData.filter(t => t.id !== id);
    if (table === 'watchlists') watchlistsData = watchlistsData.filter(w => w.id !== id);
    renderAllTables();
    updateDashboardMetrics();
    initCharts();
};

function initCharts() {
    const sectorCtx = document.getElementById('sectorChart');
    const portfolioCtx = document.getElementById('portfolioChart');

    if (sectorCtx && typeof Chart !== 'undefined') {
        const sectorCounts = {};
        stocksData.forEach(s => sectorCounts[s.sector] = (sectorCounts[s.sector] || 0) + 1);

        if (sectorChartInstance) sectorChartInstance.destroy();
        sectorChartInstance = new Chart(sectorCtx, {
            type: 'doughnut',
            data: {
                labels: Object.keys(sectorCounts),
                datasets: [{
                    data: Object.values(sectorCounts),
                    backgroundColor: ['#007bff', '#28a745', '#ffc107', '#dc3545', '#17a2b8', '#6c757d']
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        });
    }

    if (portfolioCtx && typeof Chart !== 'undefined') {
        if (portfolioChartInstance) portfolioChartInstance.destroy();
        portfolioChartInstance = new Chart(portfolioCtx, {
            type: 'bar',
            data: {
                labels: portfoliosData.map(p => p.name),
                datasets: [{
                    label: 'Cash Balance (₹)',
                    data: portfoliosData.map(p => p.balance),
                    backgroundColor: '#007bff'
                }]
            },
            options: { responsive: true, maintainAspectRatio: false }
        });
    }
}