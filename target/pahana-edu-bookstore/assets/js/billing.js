// Billing page JavaScript functionality - VERSION 2.0 with enhanced search
// Updated: August 19, 2025 - Fixed JSON parsing and added fallback data
let selectedCustomer = null;
let selectedItem = null;
let billItems = [];

// Independent debounce timers so typing in one field doesn't cancel the other's request
let customerDebounce = null;
let itemDebounce = null;

// Simple in-memory caches to avoid repeat network calls (reset per page load)
const customerCache = new Map(); // key: queryLower -> results array
const itemCache = new Map();     // key: queryLower -> results array

// Track keyboard navigation state
let customerActiveIndex = -1;
let itemActiveIndex = -1;

// Initialize page
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚀 Billing page loaded, context path:', window.contextPath);
    console.log('📝 Setting up search functionality...');
    setupCustomerSearch();
    setupItemSearch();
    console.log('✅ Search functionality initialized');
});

// Customer search functionality
function setupCustomerSearch() {
    const customerSearch = document.getElementById('customerSearch');
    const customerSuggestions = document.getElementById('customerSuggestions');
    
    customerSearch.addEventListener('input', function() {
        const query = this.value.trim();
        
        if (customerDebounce) clearTimeout(customerDebounce);
        
        if (query.length < 1) {
            customerSuggestions.style.display = 'none';
            customerActiveIndex = -1;
            return;
        }
        
        customerSuggestions.innerHTML = loadingTemplate();
        customerSuggestions.style.display = 'block';

        customerDebounce = setTimeout(() => {
            searchCustomers(query);
        }, 200); // Faster response for better user experience
    });

    customerSearch.addEventListener('keydown', e => handleKeydown(e, 'customer'));
    
    // Hide suggestions when clicking outside
    document.addEventListener('click', function(e) {
        if (!customerSearch.contains(e.target) && !customerSuggestions.contains(e.target)) {
            customerSuggestions.style.display = 'none';
            customerActiveIndex = -1;
        }
    });
}

function searchCustomers(query) {
    console.log('🔍 Searching customers for query:', query);
    const q = query.toLowerCase();
    if (customerCache.has(q)) {
        console.log('📋 Using cached results for:', query);
        displayCustomerSuggestions(customerCache.get(q), query);
        return;
    }

    // Use direct search endpoint
    const url = `${window.contextPath}/assets/data?t=c&q=${encodeURIComponent(query)}`;
    console.log('🌐 Fetching from URL:', url);
    
    fetch(url)
        .then(response => {
            console.log('📡 Response status:', response.status, response.statusText);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.text(); // Get as text first to debug
        })
        .then(text => {
            console.log('📄 Raw response text:', text);
            console.log('📏 Response length:', text.length);
            
            if (!text || text.trim() === '') {
                console.log('⚠️ Empty response, returning empty array');
                customerCache.set(q, []);
                displayCustomerSuggestions([], query);
                return;
            }
            
            try {
                const customers = JSON.parse(text);
                console.log('✅ Found customers:', customers);
                customerCache.set(q, customers);
                displayCustomerSuggestions(customers, query);
            } catch (parseError) {
                console.error('📋 JSON Parse Error:', parseError);
                console.log('🔍 First 200 chars of response:', text.substring(0, 200));
                throw new Error('Invalid JSON response from server');
            }
        })
        .catch(err => {
            console.error('❌ Error searching customers:', err);
            showErrorSuggestion('customerSuggestions', 'Error loading customers: ' + err.message);
        });
}

function displayCustomerSuggestions(customers, query) {
    const container = document.getElementById('customerSuggestions');
    customerActiveIndex = -1;

    if (!customers || customers.length === 0) {
        container.innerHTML = '<div class="typeahead-suggestion text-muted">No customers found</div>';
        container.style.display = 'block';
        return;
    }

    const qLower = query.toLowerCase();
    const html = customers.map((c, idx) => {
        const nameHighlighted = highlightMatch(escapeHtml(c.name || ''), qLower);
        const accHighlighted = highlightMatch(escapeHtml(c.accountNumber || ''), qLower);
        return `<div class="typeahead-suggestion" data-index="${idx}" data-id="${c.id}" data-account="${escapeHtml(c.accountNumber)}" data-name="${escapeHtml(c.name)}" data-phone="${escapeHtml(c.phone || '')}">
            <strong>${accHighlighted}</strong> - ${nameHighlighted}
            ${c.phone ? `<br><small class="text-muted">📞 ${escapeHtml(c.phone)}</small>` : ''}
            ${c.address ? `<br><small class="text-muted">📍 ${escapeHtml(c.address)}</small>` : ''}
        </div>`;
    }).join('');
    container.innerHTML = html;
    container.style.display = 'block';

    // Click delegation
    container.onclick = (e) => {
        const el = e.target.closest('.typeahead-suggestion');
        if (!el) return;
        selectCustomer(+el.dataset.id, el.dataset.account, el.dataset.name, el.dataset.phone);
    };
}

function selectCustomer(id, accountNumber, name, phone) {
    selectedCustomer = { id, accountNumber, name, phone };
    
    document.getElementById('customerSearch').value = `${accountNumber} - ${name}`;
    document.getElementById('customerSuggestions').style.display = 'none';
    
    const customerInfo = document.getElementById('customerInfo');
    customerInfo.innerHTML = `
        <strong>${accountNumber}</strong> - ${name}
        ${phone ? `<br><small>${phone}</small>` : ''}
    `;
    
    document.getElementById('selectedCustomer').style.display = 'block';
}

function clearCustomer() {
    selectedCustomer = null;
    document.getElementById('customerSearch').value = '';
    document.getElementById('selectedCustomer').style.display = 'none';
}

// Item search functionality
function setupItemSearch() {
    const itemSearch = document.getElementById('itemSearch');
    const itemSuggestions = document.getElementById('itemSuggestions');
    
    itemSearch.addEventListener('input', function() {
        const query = this.value.trim();

        if (itemDebounce) clearTimeout(itemDebounce);

        if (query.length < 1) {
            itemSuggestions.style.display = 'none';
            document.getElementById('quantitySection').style.display = 'none';
            itemActiveIndex = -1;
            return;
        }

        itemSuggestions.innerHTML = loadingTemplate();
        itemSuggestions.style.display = 'block';

        itemDebounce = setTimeout(() => searchItems(query), 200); // Faster response
    });

    itemSearch.addEventListener('keydown', e => handleKeydown(e, 'item'));
    
    // Hide suggestions when clicking outside
    document.addEventListener('click', function(e) {
        if (!itemSearch.contains(e.target) && !itemSuggestions.contains(e.target)) {
            itemSuggestions.style.display = 'none';
            itemActiveIndex = -1;
        }
    });
}

function searchItems(query) {
    console.log('🔍 Searching items for query:', query);
    const q = query.toLowerCase();
    if (itemCache.has(q)) {
        console.log('📋 Using cached results for:', query);
        displayItemSuggestions(itemCache.get(q), query);
        return;
    }
    
    // Use direct search endpoint
    const url = `${window.contextPath}/assets/data?t=i&q=${encodeURIComponent(query)}`;
    console.log('🌐 Fetching from URL:', url);
    
    fetch(url)
        .then(response => {
            console.log('📡 Response status:', response.status, response.statusText);
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.text(); // Get as text first to debug
        })
        .then(text => {
            console.log('📄 Raw response text:', text);
            console.log('📏 Response length:', text.length);
            
            if (!text || text.trim() === '') {
                console.log('⚠️ Empty response, returning empty array');
                itemCache.set(q, []);
                displayItemSuggestions([], query);
                return;
            }
            
            try {
                const items = JSON.parse(text);
                console.log('✅ Found items:', items);
                itemCache.set(q, items);
                displayItemSuggestions(items, query);
            } catch (parseError) {
                console.error('📋 JSON Parse Error:', parseError);
                console.log('🔍 First 200 chars of response:', text.substring(0, 200));
                throw new Error('Invalid JSON response from server');
            }
        })
        .catch(err => {
            console.error('❌ Error searching items:', err);
            showErrorSuggestion('itemSuggestions', 'Error loading items: ' + err.message);
        });
}

function displayItemSuggestions(items, query) {
    const container = document.getElementById('itemSuggestions');
    itemActiveIndex = -1;

    if (!items || items.length === 0) {
        container.innerHTML = '<div class="typeahead-suggestion text-muted"><i class="bi bi-search"></i> No items found</div>';
        container.style.display = 'block';
        return;
    }

    const qLower = query.toLowerCase();
    let html = '';
    items.forEach((item, idx) => {
        const skuHighlighted = highlightMatch(escapeHtml(item.sku || ''), qLower);
        const nameHighlighted = highlightMatch(escapeHtml(item.name || ''), qLower);
        const price = item.price || 0;
        const stockQuantity = item.stockQuantity || 0;
        
        html += `
            <div class="typeahead-suggestion" 
                 data-index="${idx}" data-id="${item.id}" data-sku="${escapeHtml(item.sku)}" 
                 data-name="${escapeHtml(item.name)}" data-price="${price}"
                 onclick="selectItem(${item.id}, '${escapeHtml(item.sku)}', '${escapeHtml(item.name)}', ${price})">
                <div class="d-flex justify-content-between align-items-center">
                    <div>
                        <strong>${nameHighlighted}</strong><br>
                        <small class="text-muted"><i class="bi bi-upc-scan"></i> ${skuHighlighted}</small>
                    </div>
                    <div class="text-end">
                        <strong class="text-success"><i class="bi bi-currency-dollar"></i>${Number(price).toFixed(2)}</strong>
                        <br><small class="text-muted"><i class="bi bi-box"></i> Stock: ${stockQuantity}</small>
                    </div>
                </div>
            </div>
        `;
    });
    
    container.innerHTML = html;
    container.style.display = 'block';
}

function selectItem(id, sku, name, unitPrice) {
    selectedItem = { id, sku, name, unitPrice };
    
    // Display selected item nicely in the search box
    document.getElementById('itemSearch').value = `${name} (${sku})`;
    document.getElementById('itemSuggestions').style.display = 'none';
    
    // Show quantity section
    const quantitySection = document.getElementById('quantitySection');
    quantitySection.style.display = 'block';
    
    // Reset quantity to 1 and focus
    const quantityInput = document.getElementById('quantity');
    quantityInput.value = '1';
    quantityInput.focus();
    quantityInput.select(); // Select the text for easy editing
    
    console.log('Selected item:', selectedItem);
}

function addToBill() {
    if (!selectedItem) {
        alert('Please select an item first');
        return;
    }
    
    const quantity = parseInt(document.getElementById('quantity').value);
    if (!quantity || quantity < 1) {
        alert('Please enter a valid quantity');
        return;
    }
    
    // Check if item already exists in bill
    const existingIndex = billItems.findIndex(item => item.id === selectedItem.id);
    
    if (existingIndex >= 0) {
        // Update quantity
        billItems[existingIndex].quantity += quantity;
        billItems[existingIndex].lineTotal = billItems[existingIndex].quantity * billItems[existingIndex].unitPrice;
    } else {
        // Add new item
        billItems.push({
            id: selectedItem.id,
            sku: selectedItem.sku,
            name: selectedItem.name,
            unitPrice: selectedItem.unitPrice,
            quantity: quantity,
            lineTotal: selectedItem.unitPrice * quantity
        });
    }
    
    // Clear selection
    selectedItem = null;
    document.getElementById('itemSearch').value = '';
    document.getElementById('quantity').value = '1';
    document.getElementById('quantitySection').style.display = 'none';
    
    updateBillDisplay();
}

function updateBillDisplay() {
    const billItemsContainer = document.getElementById('billItems');
    const billSummary = document.getElementById('billSummary');
    const clearBillBtn = document.getElementById('clearBillBtn');
    
    if (billItems.length === 0) {
        billItemsContainer.innerHTML = `
            <div class="text-center py-4 text-muted">
                <i class="bi bi-cart" style="font-size: 3rem;"></i>
                <h5 class="mt-2">No items added</h5>
                <p>Search and add items to create a bill</p>
            </div>
        `;
        billSummary.style.display = 'none';
        clearBillBtn.style.display = 'none';
        return;
    }
    
    let total = 0;
    const html = billItems.map((item, index) => {
        total += item.lineTotal;
        return `
            <div class="bill-item">
                <div class="row align-items-center">
                    <div class="col-md-6">
                        <strong>${escapeHtml(item.sku)}</strong> - ${escapeHtml(item.name)}
                        <br><small class="text-muted">$${item.unitPrice.toFixed(2)} each</small>
                    </div>
                    <div class="col-md-2">
                        <input type="number" class="form-control form-control-sm" 
                               value="${item.quantity}" min="1" max="9999"
                               onchange="updateQuantity(${index}, this.value)">
                    </div>
                    <div class="col-md-2 text-end">
                        <strong>$${item.lineTotal.toFixed(2)}</strong>
                    </div>
                    <div class="col-md-2 text-end">
                        <button type="button" class="btn btn-sm btn-outline-danger" 
                                onclick="removeFromBill(${index})">
                            <i class="bi bi-trash"></i>
                        </button>
                    </div>
                </div>
            </div>
        `;
    }).join('');
    
    billItemsContainer.innerHTML = html;
    document.getElementById('billTotal').textContent = `$${total.toFixed(2)}`;
    billSummary.style.display = 'block';
    clearBillBtn.style.display = 'inline-block';
}

function updateQuantity(index, newQuantity) {
    const quantity = parseInt(newQuantity);
    if (quantity < 1) {
        removeFromBill(index);
        return;
    }
    
    billItems[index].quantity = quantity;
    billItems[index].lineTotal = billItems[index].unitPrice * quantity;
    updateBillDisplay();
}

function removeFromBill(index) {
    billItems.splice(index, 1);
    updateBillDisplay();
}

function clearBill() {
    if (confirm('Are you sure you want to clear all items?')) {
        billItems = [];
        updateBillDisplay();
    }
}

function saveBill() {
    if (!selectedCustomer) {
        alert('Please select a customer');
        return;
    }
    
    if (billItems.length === 0) {
        alert('Please add at least one item');
        return;
    }
    
    const billData = {
        customerAccountNumber: selectedCustomer.accountNumber,
        items: billItems.map(item => ({
            itemId: item.id,
            qty: item.quantity
        }))
    };
    
    // Disable save button
    const saveBillBtn = document.getElementById('saveBillBtn');
    saveBillBtn.disabled = true;
    saveBillBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Saving...';
    
    fetch(`${window.contextPath}/api/billing`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        },
        body: JSON.stringify(billData)
    })
    .then(response => response.json())
    .then(data => {
        if (data.success) {
            showSuccessModal(data.billNo);
        } else {
            alert('Error creating bill: ' + (data.error || 'Unknown error'));
        }
    })
    .catch(error => {
        console.error('Error creating bill:', error);
        alert('Error creating bill. Please try again.');
    })
    .finally(() => {
        // Re-enable save button
        saveBillBtn.disabled = false;
        saveBillBtn.innerHTML = '<i class="bi bi-check-circle"></i> Save Bill';
    });
}

function showSuccessModal(billNo) {
    document.getElementById('billNumber').textContent = `Bill Number: ${billNo}`;
    window.createdBillNo = billNo;
    new bootstrap.Modal(document.getElementById('successModal')).show();
}

function viewBill() {
    if (!window.createdBillNo) {
        console.error('No bill number available');
        alert('No bill number available to view');
        return;
    }
    
    console.log('📄 Loading bill details for:', window.createdBillNo);
    
    // Show loading state
    const viewBillBtn = document.querySelector('[onclick*="viewBill"]');
    if (viewBillBtn) {
        viewBillBtn.disabled = true;
        viewBillBtn.innerHTML = '<i class="bi bi-hourglass-split"></i> Loading...';
    }
    
    // Fetch bill details using our QuickSearchServlet
    fetch(`/pahana-edu-bookstore/assets/data?t=bill&q=${encodeURIComponent(window.createdBillNo)}`)
        .then(response => {
            if (!response.ok) {
                throw new Error(`HTTP ${response.status}: ${response.statusText}`);
            }
            return response.json();
        })
        .then(billData => {
            console.log('📋 Bill data received:', billData);
            if (billData.error) {
                throw new Error(billData.error);
            }
            displayBillDetails(billData);
        })
        .catch(error => {
            console.error('❌ Error loading bill:', error);
            alert('Error loading bill details: ' + error.message);
        })
        .finally(() => {
            // Restore button state
            if (viewBillBtn) {
                viewBillBtn.disabled = false;
                viewBillBtn.innerHTML = '<i class="bi bi-eye"></i> View & Print Bill';
            }
        });
}

function displayBillDetails(bill) {
    // Debug the bill data
    console.log('📋 Displaying bill details:', bill);
    console.log('📦 Bill items:', bill.items);
    
    // Create modal HTML content
    const modalHTML = `
        <div id="billModal" style="
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: rgba(0,0,0,0.5);
            z-index: 10000;
            display: flex;
            align-items: center;
            justify-content: center;
        ">
            <div style="
                background: white;
                max-width: 800px;
                max-height: 90vh;
                width: 90%;
                border-radius: 8px;
                box-shadow: 0 4px 20px rgba(0,0,0,0.3);
                overflow-y: auto;
                position: relative;
            ">
                <div style="
                    padding: 20px;
                    font-family: Arial, sans-serif;
                    line-height: 1.6;
                    color: #333;
                ">
                    <!-- Close button -->
                    <button onclick="closeBillModal()" style="
                        position: absolute;
                        top: 15px;
                        right: 15px;
                        background: #dc3545;
                        color: white;
                        border: none;
                        width: 30px;
                        height: 30px;
                        border-radius: 50%;
                        cursor: pointer;
                        font-size: 18px;
                        display: flex;
                        align-items: center;
                        justify-content: center;
                    ">×</button>
                    
                    <!-- Bill Header -->
                    <div style="
                        text-align: center;
                        border-bottom: 2px solid #333;
                        padding-bottom: 15px;
                        margin-bottom: 25px;
                    ">
                        <h1 style="margin: 0; font-size: 24px;">PAHANA EDU BOOKSTORE</h1>
                        <h2 style="margin: 10px 0; font-size: 20px;">INVOICE</h2>
                    </div>
                    
                    <!-- Bill Info -->
                    <div style="
                        display: flex;
                        justify-content: space-between;
                        margin-bottom: 25px;
                        flex-wrap: wrap;
                        gap: 20px;
                    ">
                        <div style="flex: 1; min-width: 250px;">
                            <h3 style="margin-bottom: 10px;">Bill To:</h3>
                            <div><strong>Name:</strong> ${bill.customerName || 'N/A'}</div>
                            <div><strong>Account:</strong> ${bill.customerAccount || 'N/A'}</div>
                        </div>
                        
                        <div style="flex: 1; min-width: 250px;">
                            <h3 style="margin-bottom: 10px;">Bill Details:</h3>
                            <div><strong>Bill No:</strong> ${bill.billNo || 'N/A'}</div>
                            <div><strong>Date:</strong> ${bill.billDate || 'N/A'}</div>
                            <div><strong>Total:</strong> $${bill.total || '0.00'}</div>
                        </div>
                    </div>
                    
                    <!-- Items Table -->
                    <table style="
                        width: 100%;
                        border-collapse: collapse;
                        margin: 20px 0;
                    ">
                        <thead>
                            <tr style="background-color: #f5f5f5;">
                                <th style="border: 1px solid #ddd; padding: 12px; text-align: left;">SKU</th>
                                <th style="border: 1px solid #ddd; padding: 12px; text-align: left;">Item Name</th>
                                <th style="border: 1px solid #ddd; padding: 12px; text-align: right;">Qty</th>
                                <th style="border: 1px solid #ddd; padding: 12px; text-align: right;">Unit Price</th>
                                <th style="border: 1px solid #ddd; padding: 12px; text-align: right;">Line Total</th>
                            </tr>
                        </thead>
                        <tbody>
                            ${bill.items && bill.items.length > 0 ? 
                                bill.items.map(item => `
                                    <tr>
                                        <td style="border: 1px solid #ddd; padding: 12px;">${item.sku || 'N/A'}</td>
                                        <td style="border: 1px solid #ddd; padding: 12px;">${item.name || 'N/A'}</td>
                                        <td style="border: 1px solid #ddd; padding: 12px; text-align: right;">${item.qty || 0}</td>
                                        <td style="border: 1px solid #ddd; padding: 12px; text-align: right;">$${item.unitPrice || '0.00'}</td>
                                        <td style="border: 1px solid #ddd; padding: 12px; text-align: right;">$${item.lineTotal || '0.00'}</td>
                                    </tr>
                                `).join('') 
                                : 
                                '<tr><td colspan="5" style="border: 1px solid #ddd; padding: 12px; text-align: center; color: #999;">No items found</td></tr>'
                            }
                            <tr style="font-weight: bold; background-color: #f9f9f9;">
                                <td colspan="4" style="border: 1px solid #ddd; padding: 12px; text-align: right;"><strong>TOTAL:</strong></td>
                                <td style="border: 1px solid #ddd; padding: 12px; text-align: right;"><strong>$${bill.total || '0.00'}</strong></td>
                            </tr>
                        </tbody>
                    </table>
                    
                    <!-- Action Buttons -->
                    <div style="text-align: center; margin-top: 20px;">
                        <button onclick="printBill()" style="
                            background-color: #007bff;
                            color: white;
                            border: none;
                            padding: 10px 20px;
                            cursor: pointer;
                            border-radius: 4px;
                            margin: 0 5px;
                            font-size: 16px;
                        ">🖨️ Print Bill</button>
                        <button onclick="closeBillModal()" style="
                            background-color: #6c757d;
                            color: white;
                            border: none;
                            padding: 10px 20px;
                            cursor: pointer;
                            border-radius: 4px;
                            margin: 0 5px;
                            font-size: 16px;
                        ">❌ Close</button>
                    </div>
                </div>
            </div>
        </div>
    `;
    
    // Add modal to the page
    document.body.insertAdjacentHTML('beforeend', modalHTML);
    
    // Add event listener to close modal when clicking outside
    document.getElementById('billModal').addEventListener('click', function(e) {
        if (e.target === this) {
            closeBillModal();
        }
    });
}

// Function to close the bill modal
function closeBillModal() {
    const modal = document.getElementById('billModal');
    if (modal) {
        modal.remove();
    }
}

// Function to print the bill content
function printBill() {
    // Hide modal background and buttons for printing
    const modal = document.getElementById('billModal');
    const originalDisplay = modal.style.display;
    
    // Create print-specific styles
    const printStyle = document.createElement('style');
    printStyle.textContent = `
        @media print {
            body * { visibility: hidden; }
            #billModal, #billModal * { visibility: visible; }
            #billModal { 
                position: fixed !important;
                top: 0 !important;
                left: 0 !important;
                width: 100% !important;
                height: 100% !important;
                background: white !important;
            }
            #billModal > div {
                box-shadow: none !important;
                max-height: none !important;
                overflow: visible !important;
            }
            button { display: none !important; }
        }
    `;
    document.head.appendChild(printStyle);
    
    // Print
    window.print();
    
    // Remove print styles after printing
    setTimeout(() => {
        document.head.removeChild(printStyle);
    }, 1000);
}

function createNewBill() {
    location.reload();
}

function escapeHtml(text) {
    const div = document.createElement('div');
    div.textContent = text;
    return div.innerHTML;
}

// Highlight matched substring(s)
function highlightMatch(htmlEscapedText, queryLower) {
    if (!queryLower) return htmlEscapedText;
    try {
        const pattern = queryLower.replace(/[.*+?^${}()|[\]\\]/g, '\\$&');
        const regex = new RegExp(pattern, 'ig');
        return htmlEscapedText.replace(regex, m => `<mark>${m}</mark>`);
    } catch (e) {
        return htmlEscapedText; // Fallback silently
    }
}

function handleKeydown(e, type) {
    const isCustomer = type === 'customer';
    const container = document.getElementById(isCustomer ? 'customerSuggestions' : 'itemSuggestions');
    const input = document.getElementById(isCustomer ? 'customerSearch' : 'itemSearch');
    if (!container || container.style.display === 'none') return;
    const items = Array.from(container.querySelectorAll('.typeahead-suggestion'));
    if (items.length === 0) return;

    let idxRef = isCustomer ? customerActiveIndex : itemActiveIndex;

    switch (e.key) {
        case 'ArrowDown':
            e.preventDefault();
            idxRef = Math.min(idxRef + 1, items.length - 1);
            break;
        case 'ArrowUp':
            e.preventDefault();
            idxRef = Math.max(idxRef - 1, 0);
            break;
        case 'Enter':
            if (idxRef >= 0) {
                e.preventDefault();
                const el = items[idxRef];
                if (isCustomer) {
                    selectCustomer(+el.dataset.id, el.dataset.account, el.dataset.name, el.dataset.phone);
                } else {
                    selectItem(+el.dataset.id, el.dataset.sku, el.dataset.name, parseFloat(el.dataset.price));
                }
            }
            return;
        case 'Escape':
            container.style.display = 'none';
            return;
        default:
            return; // Ignore other keys
    }

    // Apply highlight class
    items.forEach(i => i.classList.remove('active'));
    if (idxRef >= 0) {
        items[idxRef].classList.add('active');
        items[idxRef].scrollIntoView({ block: 'nearest' });
    }
    if (isCustomer) customerActiveIndex = idxRef; else itemActiveIndex = idxRef;
}

function loadingTemplate() {
    return '<div class="typeahead-suggestion text-muted d-flex align-items-center"><span class="spinner-border spinner-border-sm me-2"></span>Searching...</div>';
}

function showErrorSuggestion(containerId, message) {
    const el = document.getElementById(containerId);
    if (!el) return;
    el.innerHTML = `<div class="typeahead-suggestion text-danger">${escapeHtml(message)}</div>`;
    el.style.display = 'block';
}


