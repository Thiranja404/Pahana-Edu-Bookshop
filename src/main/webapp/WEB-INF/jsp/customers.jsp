<%@ include file="layout.jspf" %>

<div class="container main-content">
    <div class="row">
        <div class="col-12">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="bi bi-people"></i> Customers</h2>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addCustomerModal">
                    <i class="bi bi-plus"></i> Add Customer
                </button>
            </div>
        </div>
    </div>
    
    <!-- Customers List -->
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">All Customers</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty customers}">
                            <div class="table-responsive">
                                <table class="table table-striped" id="customersTable">
                                    <thead>
                                        <tr>
                                            <th>Account Number</th>
                                            <th>Name</th>
                                            <th>Address</th>
                                            <th>Phone</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${customers}" var="customer">
                                            <tr>
                                                <td><strong>${customer.accountNumber}</strong></td>
                                                <td>${customer.name}</td>
                                                <td>${customer.address}</td>
                                                <td>${customer.phone}</td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/app/customers/edit?id=${customer.id}" 
                                                       class="btn btn-sm btn-outline-primary me-1">
                                                        <i class="bi bi-pencil"></i> Edit
                                                    </a>
                                                    <button type="button" class="btn btn-sm btn-outline-danger" 
                                                            onclick="deleteCustomer('${customer.id}', '${fn:escapeXml(customer.name)}')">
                                                        <i class="bi bi-trash"></i> Delete
                                                    </button>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            
                            <!-- Pagination (client-side) -->
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <div>
                                    <span class="text-muted" id="tableInfo">Showing all customers</span>
                                </div>
                                <div>
                                    <button class="btn btn-sm btn-outline-secondary" id="prevPage" disabled>
                                        <i class="bi bi-chevron-left"></i> Previous
                                    </button>
                                    <button class="btn btn-sm btn-outline-secondary" id="nextPage" disabled>
                                        Next <i class="bi bi-chevron-right"></i>
                                    </button>
                                </div>
                            </div>
                        </c:when>
                        <c:otherwise>
                            <div class="text-center py-4">
                                <i class="bi bi-people" style="font-size: 3rem; color: #ddd;"></i>
                                <h5 class="text-muted mt-2">No customers found</h5>
                                <p class="text-muted">Click "Add Customer" to create your first customer.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Add Customer Modal -->
<div class="modal fade" id="addCustomerModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="bi bi-person-plus"></i> Add New Customer</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/app/customers">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="name" class="form-label">Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="name" name="name" 
                               value="${name}" required maxlength="100">
                    </div>
                    
                    <div class="mb-3">
                        <label for="address" class="form-label">Address</label>
                        <textarea class="form-control" id="address" name="address" rows="3" 
                                  maxlength="500">${address}</textarea>
                    </div>
                    
                    <div class="mb-3">
                        <label for="phone" class="form-label">Phone</label>
                        <input type="tel" class="form-control" id="phone" name="phone" 
                               value="${phone}" maxlength="20" 
                               pattern="[+\-\s\d()]+">
                        <div class="form-text">Only numbers, spaces, parentheses, + and - allowed</div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check"></i> Save Customer
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
// Simple client-side pagination
document.addEventListener('DOMContentLoaded', function() {
    const table = document.getElementById('customersTable');
    if (!table) return;
    
    const rowsPerPage = 10;
    const tbody = table.querySelector('tbody');
    const rows = Array.from(tbody.querySelectorAll('tr'));
    const totalRows = rows.length;
    const totalPages = Math.ceil(totalRows / rowsPerPage);
    
    let currentPage = 1;
    
    const prevBtn = document.getElementById('prevPage');
    const nextBtn = document.getElementById('nextPage');
    const tableInfo = document.getElementById('tableInfo');
    
    function showPage(page) {
        const start = (page - 1) * rowsPerPage;
        const end = start + rowsPerPage;
        
        rows.forEach((row, index) => {
            row.style.display = (index >= start && index < end) ? '' : 'none';
        });
        
        const showingStart = Math.min(start + 1, totalRows);
        const showingEnd = Math.min(end, totalRows);
        tableInfo.textContent = `Showing ${showingStart}-${showingEnd} of ${totalRows} customers`;
        
        prevBtn.disabled = page === 1;
        nextBtn.disabled = page === totalPages;
    }
    
    if (totalPages > 1) {
        prevBtn.style.display = 'inline-block';
        nextBtn.style.display = 'inline-block';
        
        prevBtn.addEventListener('click', () => {
            if (currentPage > 1) {
                currentPage--;
                showPage(currentPage);
            }
        });
        
        nextBtn.addEventListener('click', () => {
            if (currentPage < totalPages) {
                currentPage++;
                showPage(currentPage);
            }
        });
        
        showPage(currentPage);
    } else {
        tableInfo.textContent = `Showing all ${totalRows} customers`;
    }
});

// Delete customer function
function deleteCustomer(customerId, customerName) {
    if (confirm('Are you sure you want to delete customer "' + customerName + '"?\\n\\nThis action cannot be undone.')) {
        // Create a form and submit it for deletion
        const form = document.createElement('form');
        form.method = 'POST';
        form.action = '${pageContext.request.contextPath}/app/customers/delete';
        
        const idInput = document.createElement('input');
        idInput.type = 'hidden';
        idInput.name = 'id';
        idInput.value = customerId;
        form.appendChild(idInput);
        
        document.body.appendChild(form);
        form.submit();
    }
}

// Show modal if there are validation errors
<c:if test="${not empty error and (not empty name or not empty address or not empty phone)}">
    document.addEventListener('DOMContentLoaded', function() {
        new bootstrap.Modal(document.getElementById('addCustomerModal')).show();
    });
</c:if>
</script>
