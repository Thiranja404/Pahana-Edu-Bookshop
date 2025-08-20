<%@ include file="layout.jspf" %>

<!-- Debug Panel (remove this in production) -->
<div class="container-fluid bg-light py-2" style="border-bottom: 2px dashed #007bff;">

    <div class="row">
        <div class="col-12">
            <h2><i class="bi bi-receipt"></i> Create Bill</h2>
            <p class="text-muted">Search for customer and items to create a new bill</p>
        </div>
    </div>
    
    <div class="row">
        <!-- Customer Selection -->
        <div class="col-md-6">
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-person-search"></i> Select Customer</h5>
                </div>
                <div class="card-body">
                    <div class="typeahead-container">
                        <label for="customerSearch" class="form-label">Search Customer</label>
                        <input type="text" class="form-control" id="customerSearch" 
                               placeholder="Type account number or name..." autocomplete="off">
                        <div class="typeahead-suggestions list-group" id="customerSuggestions"></div>
                    </div>
                    
                    <div id="selectedCustomer" class="mt-3" style="display: none;">
                        <div class="alert alert-info">
                            <h6 class="mb-1">Selected Customer:</h6>
                            <div id="customerInfo"></div>
                            <button type="button" class="btn btn-sm btn-outline-secondary mt-2" onclick="clearCustomer()">
                                <i class="bi bi-x"></i> Change Customer
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
        
        <!-- Item Search -->
        <div class="col-md-6">
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-search"></i> Add Items</h5>
                </div>
                <div class="card-body">
                    <div class="typeahead-container">
                        <label for="itemSearch" class="form-label">Search Items</label>
                        <input type="text" class="form-control" id="itemSearch" 
                               placeholder="Type SKU or item name..." autocomplete="off">
                        <div class="typeahead-suggestions list-group" id="itemSuggestions"></div>
                    </div>
                    
                    <div class="row mt-3" id="quantitySection" style="display: none;">
                        <div class="col-8">
                            <input type="number" class="form-control" id="quantity" 
                                   placeholder="Quantity" min="1" max="9999" value="1">
                        </div>
                        <div class="col-4">
                            <button type="button" class="btn btn-success w-100" onclick="addToBill()">
                                <i class="bi bi-plus"></i> Add
                            </button>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Bill Cart -->
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header d-flex justify-content-between align-items-center">
                    <h5 class="mb-0"><i class="bi bi-cart"></i> Bill Items</h5>
                    <button type="button" class="btn btn-sm btn-outline-danger" onclick="clearBill()" id="clearBillBtn" style="display: none;">
                        <i class="bi bi-trash"></i> Clear All
                    </button>
                </div>
                <div class="card-body">
                    <div id="billItems">
                        <div class="text-center py-4 text-muted">
                            <i class="bi bi-cart" style="font-size: 3rem;"></i>
                            <h5 class="mt-2">No items added</h5>
                            <p>Search and add items to create a bill</p>
                        </div>
                    </div>
                    
                    <div id="billSummary" style="display: none;">
                        <div class="row mt-4 pt-4 border-top">
                            <div class="col-md-6 offset-md-6">
                                <div class="d-flex justify-content-between mb-2">
                                    <strong>Total:</strong>
                                    <strong id="billTotal">$0.00</strong>
                                </div>
                                <div class="d-grid gap-2">
                                    <button type="button" class="btn btn-primary" onclick="saveBill()" id="saveBillBtn">
                                        <i class="bi bi-check-circle"></i> Save Bill
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Success Modal -->
<div class="modal fade" id="successModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header bg-success text-white">
                <h5 class="modal-title"><i class="bi bi-check-circle"></i> Bill Created Successfully</h5>
            </div>
            <div class="modal-body text-center">
                <h4 id="billNumber"></h4>
                <p class="text-muted">Your bill has been created successfully.</p>
            </div>
            <div class="modal-footer justify-content-center">
                <button type="button" class="btn btn-primary" onclick="viewBill()">
                    <i class="bi bi-eye"></i> View & Print Bill
                </button>
                <button type="button" class="btn btn-secondary" onclick="createNewBill()">
                    <i class="bi bi-plus"></i> Create New Bill
                </button>
            </div>
        </div>
    </div>
</div>

<script>
// Set context path for use in billing.js
window.contextPath = '${pageContext.request.contextPath}';
</script>

<style>
/* Typeahead styles */
.typeahead-container {
    position: relative;
}
.typeahead-suggestions {
    position: absolute;
    width: 100%;
    max-height: 300px;
    overflow-y: auto;
    background: white;
    border: 1px solid #ced4da;
    border-radius: 0 0 8px 8px;
    box-shadow: 0 6px 20px rgba(0,0,0,0.15);
    z-index: 1050;
    display: none;
}
.typeahead-suggestion {
    padding: 12px 16px;
    border-bottom: 1px solid #f0f0f0;
    cursor: pointer;
    transition: all 0.2s ease;
    background: white;
}
.typeahead-suggestion:hover {
    background-color: #f8f9fa;
    transform: translateX(2px);
}
.typeahead-suggestion:last-child {
    border-bottom: none;
    border-radius: 0 0 8px 8px;
}
/* Enhanced interactive states */
.typeahead-suggestion.active, .typeahead-suggestion:hover {
    background-color: #e3f2fd;
    border-left: 4px solid #2196f3;
    box-shadow: inset 0 0 8px rgba(33, 150, 243, 0.1);
}
.typeahead-suggestion mark {
    background: linear-gradient(120deg, #fff3cd 0%, #ffeaa7 100%);
    color: #856404;
    padding: 2px 4px;
    border-radius: 4px;
    font-weight: 600;
    box-shadow: 0 1px 2px rgba(0,0,0,0.1);
}
/* No results styling */
.typeahead-suggestion.text-muted {
    font-style: italic;
    text-align: center;
    color: #6c757d !important;
    background: #f8f9fa;
}
/* Bill items styling */
.bill-item {
    padding: 15px;
    border: 1px solid #e9ecef;
    border-radius: 8px;
    margin-bottom: 10px;
    background: #f8f9fa;
    transition: all 0.2s ease;
}
.bill-item:hover {
    background: #e9ecef;
    border-color: #dee2e6;
}
</style>

<script src="${pageContext.request.contextPath}/assets/js/billing.js?v=2.0"></script>
