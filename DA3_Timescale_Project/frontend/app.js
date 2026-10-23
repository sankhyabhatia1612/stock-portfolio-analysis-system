const API_BASE_URL = 'http://localhost:8080/api';

let usersData = [], portfoliosData = [], stocksData = [];
let transactionsData = [], watchlistsData = [];
let sectorChartInstance = null, portfolioChartInstance = null;
let editingUserId = null, editingStockId = null;
let editingPortfolioId = null, editingTransactionId = null;
let editingWatchlistId = null;

const get = (o, ...keys) => {
    for (const k of keys) {
        if (o && o[k] !== undefined && o[k] !== null) return o[k];
    }
    return '';
};

const esc = v => String(v ?? '').replace(/[&<>"']/g, c => ({
    '&':'&amp;', '<':'&lt;', '>':'&gt;',
    '"':'&quot;', "'":'&#39;'
}[c]));

async function api(endpoint, method = 'GET', body = null, query = '') {
    const response = await fetch(
        `${API_BASE_URL}/${endpoint}${query}`,
        {
            method,
            headers: body ? {'Content-Type':'application/json'} : {},
            body: body ? JSON.stringify(body) : undefined
        }
    );

    const text = await response.text();

    if (!response.ok) throw new Error(text || `HTTP ${response.status}`);

    try {
        return text ? JSON.parse(text) : null;
    } catch {
        return text;
    }
}

async function loadLiveOracleData() {
    try {
        const [us, ss, ps, ts, ws] = await Promise.all([
            api('users'),
            api('stocks'),
            api('portfolios'),
            api('transactions'),
            api('watchlists')
        ]);

        usersData = us.map(u => ({
            id: String(get(u, 'USER_ID', 'userId')),
            name: `${get(u, 'FNAME', 'fname')} ${get(u, 'LNAME', 'lname')}`.trim(),
            email: get(u, 'EMAIL', 'email'),
             dob: get(u, 'DOB', 'dob'),
            risk: get(u, 'RISK_PROFILE', 'riskProfile') || 'MEDIUM'
        }));

        stocksData = ss.map(s => ({
            id: String(get(s, 'STOCK_ID', 'stockId')),
            symbol: get(s, 'STOCK_SYMBOL', 'stockSymbol'),
            name: get(s, 'STOCK_NAME', 'stockName'),
            exchange: get(s, 'EXCHANGE', 'exchange'),
            sector: get(s, 'SECTOR', 'sector'),
            price: Number(get(s, 'CURRENT_PRICE', 'currentPrice') || 0)
        }));

        portfoliosData = ps.map(p => ({
            id: String(get(p, 'PORTFOLIO_ID', 'portfolioId')),
            userId: String(get(p, 'USER_ID', 'userId')),
            name: get(p, 'PORTFOLIO_NAME', 'portfolioName'),
            balance: Number(get(p, 'CASH_BALANCE', 'cashBalance') || 0)
        }));

        transactionsData = ts.map(t => ({
            id: String(get(t, 'TRANS_ID', 'transId')),
            userId: String(get(t, 'USER_ID', 'userId')),
            portfolioId: String(get(t, 'PORTFOLIO_ID', 'portfolioId')),
            stockId: String(get(t, 'STOCK_ID', 'stockId')),
            type: String(get(t, 'TRANS_TYPE', 'transType') || 'BUY').toUpperCase(),
            qty: Number(get(t, 'QUANTITY', 'quantity') || 0),
            price: Number(get(t, 'PRICE', 'price') || 0),
            fee: Number(get(t, 'TRANS_FEE', 'transFee') || 0)
        }));

        watchlistsData = ws.map(w => ({
            id: String(get(w, 'WATCH_ID', 'watchId')),
            userId: String(get(w, 'USER_ID', 'userId')),
            stockId: String(get(w, 'STOCK_ID', 'stockId')),
            symbol: get(w, 'STOCK_SYMBOL', 'stockSymbol')
        }));

    } catch (e) {
        console.error('Oracle load failed:', e);
        alert('Could not load database data: ' + e.message);
    }

    renderAllTables();
    updateDashboardMetrics();
    initCharts();
}

window.switchTab = function(tabId) {
    document.querySelectorAll('.nav-btn, .nav-link').forEach(b => {
        b.classList.toggle('active', b.getAttribute('data-tab') === tabId);
    });

    document.querySelectorAll('.tab-pane, .tab-content').forEach(p => {
        const active = p.id === tabId;
        p.classList.toggle('active', active);
        p.style.setProperty('display', active ? 'block' : 'none', 'important');
    });
};

document.addEventListener('DOMContentLoaded', () => {
    document.querySelectorAll('.nav-btn, .nav-link').forEach(b => {
        b.addEventListener('click', e => {
            e.preventDefault();
            const t = b.getAttribute('data-tab');
            if (t) window.switchTab(t);
        });
    });

    document.querySelectorAll('[data-switch-tab]').forEach(b => {
        b.addEventListener('click', e => {
            e.preventDefault();
            const t = b.getAttribute('data-switch-tab');
            if (t) window.switchTab(t);
        });
    });

    const refresh = document.getElementById('btn-refresh-users');
    if (refresh) refresh.addEventListener('click', loadLiveOracleData);

    window.switchTab('dashboard');
    setupFormHandlers();
    setupFiltersAndSearch();
    loadLiveOracleData();
});

function renderAllTables() {
    renderUsersTable();
    renderStocksTable();
    renderPortfoliosTable();
    renderTransactionsTable();
    renderWatchlistsTable();
    renderDashStockSummary();
}

function updateDashboardMetrics() {
    const set = (id, value) => {
        const e = document.getElementById(id);
        if (e) e.textContent = value;
    };

    set('metric-total-users', usersData.length);
    set('metric-active-portfolios', portfoliosData.length);
    set('metric-listed-stocks', stocksData.length);
    set('metric-total-trades', transactionsData.length);
}

function renderUsersTable(f = '') {
    const tb = document.getElementById('users-tbody');
    if (!tb) return;
    tb.innerHTML = '';

    usersData.filter(u =>
        `${u.id} ${u.name} ${u.email}`.toLowerCase().includes(f.toLowerCase())
    ).forEach(u => {
        const risk = String(u.risk || 'MEDIUM').toUpperCase();
        const cls = ['LOW','CONSERVATIVE'].includes(risk)
            ? 'badge-low'
            : ['HIGH','AGGRESSIVE'].includes(risk)
                ? 'badge-high' : 'badge-medium';

        tb.insertAdjacentHTML('beforeend', `
            <tr>
                <td><strong>${esc(u.id)}</strong></td>
                <td>${esc(u.name)}</td>
                <td>${esc(u.email)}</td>
                <td><span class="badge-pill ${cls}">${esc(risk)}</span></td>
                <td>
                    <button class="btn btn-primary btn-sm"
                        onclick="modifyUser('${esc(u.id)}')">Modify</button>
                    <button class="btn btn-danger btn-sm"
                        onclick="deleteRecord('users','${esc(u.id)}')">Delete</button>
                </td>
            </tr>`);
    });
}

window.modifyUser = id => {
    const u = usersData.find(x => x.id === id);
    if (!u) return;

    editingUserId = id;
    const n = u.name.split(' ');

    setVal('user-id', u.id);
    setVal('user-fname', n[0]);
    setVal('user-lname', n.slice(1).join(' '));
    setVal('user-email', u.email);
    setVal('user-risk', u.risk);
    if (u.dob) {
        setVal('user-dob', u.dob.substring(0, 10));
    }

    setSubmitText('form-add-user', 'Update Investor Record');
    switchTab('users');
};

function renderStocksTable(f = '') {
    const tb = document.getElementById('stocks-tbody');
    if (!tb) return;
    tb.innerHTML = '';

    stocksData.filter(s =>
        `${s.id} ${s.symbol} ${s.name} ${s.sector}`.toLowerCase().includes(f.toLowerCase())
    ).forEach(s => tb.insertAdjacentHTML('beforeend', `
        <tr>
            <td><strong>${esc(s.id)}</strong></td>
            <td><span class="stock-symbol">${esc(s.symbol)}</span></td>
            <td>${esc(s.name)}</td>
            <td>${esc(s.exchange)}</td>
            <td>${esc(s.sector)}</td>
            <td>₹${s.price.toFixed(2)}</td>
            <td>
                <button class="btn btn-primary btn-sm"
                    onclick="modifyStock('${esc(s.id)}')">Modify</button>
                <button class="btn btn-danger btn-sm"
                    onclick="deleteRecord('stocks','${esc(s.id)}')">Delete</button>
            </td>
        </tr>`));
}

window.modifyStock = id => {
    const s = stocksData.find(x => x.id === id);
    if (!s) return;

    editingStockId = id;
    [
        ['stock-id',s.id], ['stock-symbol',s.symbol],
        ['stock-name',s.name], ['stock-exchange',s.exchange],
        ['stock-sector',s.sector], ['stock-price',s.price]
    ].forEach(([a,b]) => setVal(a,b));

    setSubmitText('form-add-stock', 'Update Stock');
    switchTab('stocks');
};

function renderPortfoliosTable(f = '') {
    const tb = document.getElementById('portfolios-tbody');
    if (!tb) return;
    tb.innerHTML = '';

    portfoliosData.filter(p =>
        `${p.id} ${p.userId} ${p.name}`.toLowerCase().includes(f.toLowerCase())
    ).forEach(p => tb.insertAdjacentHTML('beforeend', `
        <tr>
            <td><strong>${esc(p.id)}</strong></td>
            <td>${esc(p.userId)}</td>
            <td>${esc(p.name)}</td>
            <td>₹${p.balance.toFixed(2)}</td>
            <td>
                <button class="btn btn-primary btn-sm"
                    onclick="modifyPortfolio('${esc(p.id)}')">Modify</button>
                <button class="btn btn-danger btn-sm"
                    onclick="deleteRecord('portfolios','${esc(p.id)}')">Delete</button>
            </td>
        </tr>`));
}

window.modifyPortfolio = id => {
    const p = portfoliosData.find(x => x.id === id);
    if (!p) return;

    editingPortfolioId = id;
    [
        ['portfolio-id',p.id], ['portfolio-user-id',p.userId],
        ['portfolio-name',p.name], ['portfolio-balance',p.balance]
    ].forEach(([a,b]) => setVal(a,b));

    setSubmitText('form-add-portfolio', 'Update Portfolio');
    switchTab('portfolios');
};

function renderTransactionsTable(f = '') {
    const tb = document.getElementById('transactions-tbody');
    if (!tb) return;
    tb.innerHTML = '';

    transactionsData.filter(t =>
        `${t.id} ${t.userId} ${t.stockId}`.toLowerCase().includes(f.toLowerCase())
    ).forEach(t => tb.insertAdjacentHTML('beforeend', `
        <tr>
            <td><strong>${esc(t.id)}</strong></td>
            <td>${esc(t.userId)}</td>
            <td>${esc(t.stockId)}</td>
            <td><span class="badge ${t.type === 'BUY' ? 'badge-buy' : 'badge-sell'}">${esc(t.type)}</span></td>
            <td>${t.qty}</td>
            <td>₹${t.price.toFixed(2)}</td>
            <td>
                <button class="btn btn-primary btn-sm"
                    onclick="modifyTransaction('${esc(t.id)}')">Modify</button>
                <button class="btn btn-danger btn-sm"
                    onclick="deleteRecord('transactions','${esc(t.id)}')">Delete</button>
            </td>
        </tr>`));
}

window.modifyTransaction = id => {
    const t = transactionsData.find(x => x.id === id);
    if (!t) return;

    editingTransactionId = id;
    [
        ['trans-id',t.id], ['trans-user-id',t.userId],
        ['trans-stock-id',t.stockId], ['trans-type',t.type],
        ['trans-qty',t.qty], ['trans-price',t.price]
    ].forEach(([a,b]) => setVal(a,b));

    setSubmitText('form-add-transaction', 'Update Transaction');
    switchTab('transactions');
};

function renderWatchlistsTable(f = '') {
    const tb = document.getElementById('watchlists-tbody');
    if (!tb) return;
    tb.innerHTML = '';

    watchlistsData.filter(w =>
        `${w.id} ${w.userId} ${w.stockId} ${w.symbol}`.toLowerCase().includes(f.toLowerCase())
    ).forEach(w => tb.insertAdjacentHTML('beforeend', `
        <tr>
            <td><strong>${esc(w.id)}</strong></td>
            <td>${esc(w.userId)}</td>
            <td>${esc(w.stockId)}</td>
            <td><span class="stock-symbol">${esc(w.symbol)}</span></td>
            <td>
                <button class="btn btn-primary btn-sm"
                    onclick="modifyWatchlist('${esc(w.id)}','${esc(w.userId)}')">Modify</button>
                <button class="btn btn-danger btn-sm"
                    onclick="deleteRecord('watchlists','${esc(w.id)}','${esc(w.userId)}')">Delete</button>
            </td>
        </tr>`));
}

window.modifyWatchlist = (id, user) => {
    const w = watchlistsData.find(x => x.id === id && x.userId === user);
    if (!w) return;

    editingWatchlistId = {id, user};
    [
        ['watchlist-id',w.id], ['watchlist-user-id',w.userId],
        ['watchlist-stock-id',w.stockId], ['watchlist-symbol',w.symbol]
    ].forEach(([a,b]) => setVal(a,b));

    setSubmitText('form-add-watchlist', 'Update Watchlist');
    switchTab('watchlists');
};

function renderDashStockSummary() {
    const tb = document.getElementById('dash-stocks-summary-tbody');
    if (!tb) return;
    tb.innerHTML = '';

    stocksData.slice(0,5).forEach(s => tb.insertAdjacentHTML('beforeend', `
        <tr>
            <td><strong>${esc(s.symbol)}</strong></td>
            <td>${esc(s.name)}</td>
            <td>${esc(s.sector)}</td>
            <td>₹${s.price.toFixed(2)}</td>
        </tr>`));
}

window.deleteRecord = async (table, id, userId = null) => {
    if (!confirm(`Are you sure you want to delete record ${id}?`)) return;

    try {
        let query = `?id=${encodeURIComponent(id)}`;

        if (table === 'watchlists') {
            const w = watchlistsData.find(x =>
                x.id === id && (userId === null || x.userId === userId)
            );
            if (!w) throw new Error('Watchlist entry not found');

            query = `?userId=${encodeURIComponent(w.userId)}` +
                `&watchId=${encodeURIComponent(w.id)}`;
        }

        await api(table, 'DELETE', null, query);

        await loadLiveOracleData();
        alert('Record deleted successfully.');
    } catch (e) {
        alert('Delete failed: ' + e.message);
    }
};

function setupFormHandlers() {
    const bindForm = (formId, handler) => {
        const form = document.getElementById(formId);
        if (!form) return;

        form.addEventListener('submit', async e => {
            e.preventDefault();

            try {
                await handler();
                await loadLiveOracleData();
                form.reset();
            } catch (err) {
                alert('Save failed: ' + err.message);
            }
        });
    };

    bindForm('form-add-user', async () => {
        const id = val('user-id');

        const selectedDob = val('user-dob') || '2000-01-01';
        await api('users', editingUserId ? 'PUT' : 'POST', {
            USER_ID: id,
            FNAME: val('user-fname'),
            LNAME: val('user-lname'),
            EMAIL: val('user-email'),
            DOB: selectedDob,
            PASSWORD_HASH: `DEMO_HASH_${id}`,
            USER_STATUS: 'ACTIVE',
            RISK_PROFILE: val('user-risk')
        }, editingUserId ? `?id=${encodeURIComponent(editingUserId)}` : '');

        editingUserId = null;
        setSubmitText('form-add-user', 'Save Investor Record');
    });

    bindForm('form-add-stock', async () => {
        const d = {
            STOCK_ID: val('stock-id'),
            STOCK_SYMBOL: val('stock-symbol').toUpperCase(),
            STOCK_NAME: val('stock-name'),
            EXCHANGE: val('stock-exchange'),
            SECTOR: val('stock-sector'),
            CURRENT_PRICE: Number(val('stock-price'))
        };

        await api('stocks', editingStockId ? 'PUT' : 'POST', d,
            editingStockId ? `?id=${encodeURIComponent(editingStockId)}` : '');

        editingStockId = null;
        setSubmitText('form-add-stock', 'Add Stock to Market');
    });

    bindForm('form-add-portfolio', async () => {
        const d = {
            PORTFOLIO_ID: val('portfolio-id'),
            USER_ID: val('portfolio-user-id'),
            PORTFOLIO_NAME: val('portfolio-name'),
            CASH_BALANCE: Number(val('portfolio-balance'))
        };

        await api('portfolios', editingPortfolioId ? 'PUT' : 'POST', d,
            editingPortfolioId ? `?id=${encodeURIComponent(editingPortfolioId)}` : '');

        editingPortfolioId = null;
        setSubmitText('form-add-portfolio', 'Create Portfolio');
    });

    bindForm('form-add-transaction', async () => {
        const d = {
            TRANS_ID: val('trans-id'),
            USER_ID: val('trans-user-id'),
            STOCK_ID: val('trans-stock-id'),
            TRANS_TYPE: val('trans-type').toUpperCase(),
            QUANTITY: Number(val('trans-qty')),
            PRICE: Number(val('trans-price')),
            TRANS_FEE: 0
        };

        await api('transactions', editingTransactionId ? 'PUT' : 'POST', d,
            editingTransactionId ? `?id=${encodeURIComponent(editingTransactionId)}` : '');

        editingTransactionId = null;
        setSubmitText('form-add-transaction', 'Execute Transaction');
    });

    bindForm('form-add-watchlist', async () => {
        const d = {
            WATCH_ID: val('watchlist-id'),
            USER_ID: val('watchlist-user-id'),
            STOCK_ID: val('watchlist-stock-id'),
            STOCK_SYMBOL: val('watchlist-symbol').toUpperCase()
        };

        const edit = editingWatchlistId;

        await api('watchlists', edit ? 'PUT' : 'POST', d,
            edit ? `?userId=${encodeURIComponent(edit.user)}&watchId=${encodeURIComponent(edit.id)}` : '');

        editingWatchlistId = null;
        setSubmitText('form-add-watchlist', 'Track Stock');
    });
}

function setupFiltersAndSearch() {
    [
        ['search-users',renderUsersTable],
        ['search-stocks',renderStocksTable],
        ['search-portfolios',renderPortfoliosTable],
        ['search-transactions',renderTransactionsTable],
        ['search-watchlists',renderWatchlistsTable]
    ].forEach(([id, fn]) => {
        const e = document.getElementById(id);
        if (e) e.addEventListener('input', () => fn(e.value));
    });
}

function val(id) {
    return (document.getElementById(id)?.value || '').trim();
}

function setVal(id, value) {
    const e = document.getElementById(id);
    if (e) e.value = value ?? '';
}

function setSubmitText(id, text) {
    const form = document.getElementById(id);
    const button = form?.querySelector('button[type="submit"],input[type="submit"]');

    if (button) {
        if (button.tagName === 'INPUT') button.value = text;
        else button.textContent = text;
    }
}

function resetFormAndButton(id, text) {
    document.getElementById(id)?.reset();
    setSubmitText(id, text);
}

function cancelUserEdit() {
    editingUserId = null;
    resetFormAndButton('form-add-user','Save Investor Record');
}
function cancelStockEdit() {
    editingStockId = null;
    resetFormAndButton('form-add-stock','Add Stock to Market');
}
function cancelPortfolioEdit() {
    editingPortfolioId = null;
    resetFormAndButton('form-add-portfolio','Create Portfolio');
}
function cancelTransactionEdit() {
    editingTransactionId = null;
    resetFormAndButton('form-add-transaction','Execute Transaction');
}
function cancelWatchlistEdit() {
    editingWatchlistId = null;
    resetFormAndButton('form-add-watchlist','Track Stock');
}

function initCharts() {
    const a = document.getElementById('sectorChart');
    const b = document.getElementById('portfolioChart');

    if (a && typeof Chart !== 'undefined') {
        const counts = {};
        stocksData.forEach(s => {
            const sector = s.sector || 'Other';
            counts[sector] = (counts[sector] || 0) + 1;
        });

        if (sectorChartInstance) sectorChartInstance.destroy();

        sectorChartInstance = new Chart(a, {
            type: 'doughnut',
            data: {
                labels: Object.keys(counts),
                datasets: [{
                    data: Object.values(counts),
                    backgroundColor: [
                        '#007bff','#28a745','#ffc107',
                        '#dc3545','#17a2b8','#6c757d'
                    ]
                }]
            },
            options: {responsive:true, maintainAspectRatio:false}
        });
    }

    if (b && typeof Chart !== 'undefined') {
        if (portfolioChartInstance) portfolioChartInstance.destroy();

        portfolioChartInstance = new Chart(b, {
            type: 'bar',
            data: {
                labels: portfoliosData.map(p => p.name),
                datasets: [{
                    label: 'Cash Balance (₹)',
                    data: portfoliosData.map(p => p.balance),
                    backgroundColor: '#007bff'
                }]
            },
            options: {responsive:true, maintainAspectRatio:false}
        });
    }
}