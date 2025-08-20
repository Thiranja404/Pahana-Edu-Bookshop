<%@ include file="layout.jspf" %>

<div class="container main-content">
    <div class="row">
        <div class="col-12">
            <h2><i class="bi bi-person-circle"></i> Account Details</h2>
            <p class="text-muted">Search for customer account information and billing history</p>
        </div>
    </div>
    
    <!-- Customer Search -->
    <div class="row">
        <div class="col-lg-8">
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-search"></i> Search Customer</h5>
                </div>
                <div class="card-body">
                    <div class="typeahead-container">
                        <label for="customerSearch" class="form-label">Customer Name or Account Number</label>
                        <input type="text" class="form-control" id="customerSearch" 
                               placeholder="Type customer name or account number..." 
                               autocomplete="off">
                        <div class="typeahead-suggestions" id="customerSuggestions"></div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <!-- Selected Customer Info -->
    <div class="row" id="customerInfoSection" style="display: none;">
        <div class="col-12">
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0"><i class="bi bi-person-check"></i> Customer Information</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <strong>Account Number:</strong>
                            <div id="selectedAccountNumber" class="text-primary fs-5 fw-bold">-</div>
                        </div>
                        <div class="col-md-6">
                            <strong>Customer Name:</strong>
                            <div id="selectedCustomerName" class="fs-5">-</div>
                        </div>
                    </div>
                    <button type="button" class="btn btn-outline-secondary btn-sm mt-2" onclick="clearSelection()">
                        <i class="bi bi-x"></i> Clear Selection
                    </button>
                </div>
            </div>
        </div>
    </div>

    <!-- Billing History -->
    <div class="row" id="billingSection" style="display: none;">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-receipt"></i> Billing History</h5>
                </div>
                <div class="card-body">
                    <div id="billsDisplay">
                        <div class="text-center text-muted py-4">
                            <i class="bi bi-receipt" style="font-size: 3rem; opacity: 0.3;"></i>
                            <p>Loading billing history...</p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<script>
let currentCustomer = null;
let searchTimeout = null;

document.addEventListener('DOMContentLoaded', function() {
    console.log('Account page loaded');
    setupSearch();
});

function setupSearch() {
    const searchInput = document.getElementById('customerSearch');
    const suggestions = document.getElementById('customerSuggestions');
    
    searchInput.addEventListener('input', function() {
        const query = this.value.trim();
        
        if (searchTimeout) {
            clearTimeout(searchTimeout);
        }
        
        if (query.length < 2) {
            suggestions.style.display = 'none';
            return;
        }
        
        suggestions.innerHTML = '<div class="list-group-item text-muted">Searching...</div>';
        suggestions.style.display = 'block';
        
        searchTimeout = setTimeout(function() {
            searchCustomers(query);
        }, 300);
    });
    
    // Hide suggestions when clicking outside
    document.addEventListener('click', function(e) {
        if (!searchInput.contains(e.target) && !suggestions.contains(e.target)) {
            suggestions.style.display = 'none';
        }
    });
}

function searchCustomers(query) {
    console.log('Searching for:', query);
    
    fetch('/pahana-edu-bookstore/assets/data?t=c&q=' + encodeURIComponent(query))
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(customers => {
            console.log('Search response:', customers);
            displaySearchResults(customers, query);
        })
        .catch(error => {
            console.error('Search error:', error);
            showError('Error searching customers: ' + error.message);
        });
}

function displaySearchResults(customers, query) {
    const suggestions = document.getElementById('customerSuggestions');
    
    console.log('Found customers:', customers);
    
    if (!customers || customers.length === 0) {
        suggestions.innerHTML = '<div class="list-group-item text-muted"><i class="bi bi-search"></i> No customers found</div>';
        suggestions.style.display = 'block';
        return;
    }
    
    let html_content = '';
    customers.forEach(function(customer) {
        html_content += '<div class="list-group-item list-group-item-action customer-option" ';
        html_content += 'data-account="' + escapeHtml(customer.accountNumber) + '" ';
        html_content += 'data-name="' + escapeHtml(customer.name) + '" ';
        html_content += 'style="cursor: pointer;">';
        html_content += '<div class="d-flex justify-content-between align-items-center">';
        html_content += '<div>';
        html_content += '<strong><i class="bi bi-person-circle"></i> ' + escapeHtml(customer.accountNumber) + '</strong>';
        html_content += '<br><span>' + escapeHtml(customer.name) + '</span>';
        if (customer.phone) {
            html_content += '<br><small class="text-muted"><i class="bi bi-telephone"></i> ' + escapeHtml(customer.phone) + '</small>';
        }
        html_content += '</div>';
        html_content += '<div class="text-end">';
        html_content += '<i class="bi bi-arrow-right-circle text-primary"></i>';
        html_content += '</div>';
        html_content += '</div>';
        html_content += '</div>';
    });
    
    suggestions.innerHTML = html_content;
    suggestions.style.display = 'block';
    
    // Add click handlers
    const options = suggestions.querySelectorAll('.customer-option');
    options.forEach(function(option) {
        option.addEventListener('click', function() {
            selectCustomer(this.dataset.account, this.dataset.name);
        });
    });

// Helper function to escape HTML
function escapeHtml(text) {
    if (!text) return '';
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}
}

function selectCustomer(accountNumber, customerName) {
    console.log('Selected customer:', accountNumber, customerName);
    
    currentCustomer = {
        accountNumber: accountNumber,
        name: customerName
    };
    
    // Update search input
    document.getElementById('customerSearch').value = accountNumber + ' - ' + customerName;
    
    // Hide suggestions
    document.getElementById('customerSuggestions').style.display = 'none';
    
    // Show customer info
    document.getElementById('selectedAccountNumber').textContent = accountNumber;
    document.getElementById('selectedCustomerName').textContent = customerName;
    document.getElementById('customerInfoSection').style.display = 'block';
    
    // Show billing section and load bills
    document.getElementById('billingSection').style.display = 'block';
    loadBills(accountNumber);
}

function loadBills(accountNumber) {
    console.log('Loading bills for:', accountNumber);
    
    const billsDisplay = document.getElementById('billsDisplay');
    billsDisplay.innerHTML = '<div class="text-center text-muted py-4"><i class="bi bi-hourglass-split"></i> Loading bills...</div>';
    
    fetch('/pahana-edu-bookstore/assets/data?t=b&q=' + encodeURIComponent(accountNumber))
        .then(response => {
            if (!response.ok) {
                throw new Error('HTTP ' + response.status);
            }
            return response.json();
        })
        .then(bills => {
            console.log('Bills loaded:', bills);
            displayBills(bills, accountNumber);
        })
        .catch(error => {
            console.error('Error loading bills:', error);
            billsDisplay.innerHTML = '<div class="alert alert-warning">Error loading bills: ' + error.message + '</div>';
        });
}

function displayBills(bills, accountNumber) {
    const billsDisplay = document.getElementById('billsDisplay');
    
    if (!bills || bills.length === 0) {
        billsDisplay.innerHTML = '<div class="text-center text-muted py-4">' +
            '<i class="bi bi-receipt" style="font-size: 3rem; opacity: 0.3;"></i>' +
            '<h5 class="mt-3">No bills found</h5>' +
            '<p>Customer ' + accountNumber + ' has no billing history.</p>' +
            '</div>';
        return;
    }
    
    let html = '<div class="table-responsive">' +
        '<table class="table table-striped table-hover">' +
        '<thead class="table-dark">' +
        '<tr>' +
        '<th><i class="bi bi-receipt"></i> Bill Number</th>' +
        '<th><i class="bi bi-calendar3"></i> Date</th>' +
        '<th><i class="bi bi-currency-dollar"></i> Total</th>' +
        '</tr>' +
        '</thead>' +
        '<tbody>';
    
    bills.forEach(function(bill) {
        const date = new Date(bill.billDate);
        const formattedDate = date.toLocaleDateString('en-US', {
            year: 'numeric',
            month: 'short',
            day: 'numeric',
            hour: '2-digit',
            minute: '2-digit'
        });
        
        html += '<tr>' +
            '<td><span class="fw-bold text-dark">' + bill.billNo + '</span></td>' +
            '<td>' + formattedDate + '</td>' +
            '<td><span class="badge bg-success fs-6">$' + bill.total + '</span></td>' +
            '</tr>';
    });
    
    html += '</tbody></table></div>';
    html += '<div class="mt-3"><small class="text-muted">';
    html += '<i class="bi bi-info-circle"></i> Found ' + bills.length + ' bill';
    if (bills.length !== 1) html += 's';
    html += ' for customer ' + accountNumber;
    html += '</small></div>';
    
    billsDisplay.innerHTML = html;
}

function clearSelection() {
    currentCustomer = null;
    document.getElementById('customerSearch').value = '';
    document.getElementById('customerInfoSection').style.display = 'none';
    document.getElementById('billingSection').style.display = 'none';
    document.getElementById('customerSuggestions').style.display = 'none';
}

function showError(message) {
    const suggestions = document.getElementById('customerSuggestions');
    suggestions.innerHTML = '<div class="list-group-item text-danger">' + message + '</div>';
    suggestions.style.display = 'block';
}
</script>
