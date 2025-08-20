<%@ include file="layout.jspf" %>

<div class="container main-content">
    <div class="row">
        <div class="col-12">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="bi bi-box"></i> Items</h2>
                <button type="button" class="btn btn-primary" data-bs-toggle="modal" data-bs-target="#addItemModal">
                    <i class="bi bi-plus"></i> Add Item
                </button>
            </div>
        </div>
    </div>
    
    <!-- Items List -->
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">All Items</h5>
                </div>
                <div class="card-body">
                    <c:choose>
                        <c:when test="${not empty items}">
                            <div class="table-responsive">
                                <table class="table table-striped" id="itemsTable">
                                    <thead>
                                        <tr>
                                            <th>SKU</th>
                                            <th>Name</th>
                                            <th>Unit Price</th>
                                            <th>Status</th>
                                            <th>Actions</th>
                                        </tr>
                                    </thead>
                                    <tbody>
                                        <c:forEach items="${items}" var="item">
                                            <tr class="${!item.active ? 'table-secondary' : ''}">
                                                <td><strong>${item.sku}</strong></td>
                                                <td>${item.name}</td>
                                                <td>$<fmt:formatNumber value="${item.unitPrice}" pattern="#,##0.00"/></td>
                                                <td>
                                                    <c:choose>
                                                        <c:when test="${item.active}">
                                                            <span class="badge bg-success">In Stock</span>
                                                        </c:when>
                                                        <c:otherwise>
                                                            <span class="badge bg-danger">Out of Stock</span>
                                                        </c:otherwise>
                                                    </c:choose>
                                                </td>
                                                <td>
                                                    <a href="${pageContext.request.contextPath}/app/items/edit?id=${item.id}" 
                                                       class="btn btn-sm btn-outline-primary">
                                                        <i class="bi bi-pencil"></i> Edit
                                                    </a>
                                                    <c:if test="${item.active}">
                                                        <form method="post" style="display:inline;" 
                                                              action="${pageContext.request.contextPath}/app/items"
                                                              onsubmit="return confirm('Are you sure you want to mark this item as out of stock?')">
                                                            <input type="hidden" name="action" value="delete">
                                                            <input type="hidden" name="id" value="${item.id}">
                                                            <button type="submit" class="btn btn-sm btn-outline-warning">
                                                                <i class="bi bi-exclamation-triangle"></i> Out of Stock
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                    <c:if test="${!item.active}">
                                                        <form method="post" style="display:inline;" 
                                                              action="${pageContext.request.contextPath}/app/items"
                                                              onsubmit="return confirm('Are you sure you want to mark this item as in stock?')">
                                                            <input type="hidden" name="action" value="activate">
                                                            <input type="hidden" name="id" value="${item.id}">
                                                            <button type="submit" class="btn btn-sm btn-outline-success">
                                                                <i class="bi bi-check-circle"></i> In Stock
                                                            </button>
                                                        </form>
                                                    </c:if>
                                                </td>
                                            </tr>
                                        </c:forEach>
                                    </tbody>
                                </table>
                            </div>
                            
                            <!-- Pagination (client-side) -->
                            <div class="d-flex justify-content-between align-items-center mt-3">
                                <div>
                                    <span class="text-muted" id="tableInfo">Showing all items</span>
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
                                <i class="bi bi-box" style="font-size: 3rem; color: #ddd;"></i>
                                <h5 class="text-muted mt-2">No items found</h5>
                                <p class="text-muted">Click "Add Item" to create your first item.</p>
                            </div>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
        </div>
    </div>
</div>

<!-- Add Item Modal -->
<div class="modal fade" id="addItemModal" tabindex="-1">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title"><i class="bi bi-box-arrow-in-down"></i> Add New Item</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
            </div>
            <form method="post" action="${pageContext.request.contextPath}/app/items">
                <div class="modal-body">
                    <div class="mb-3">
                        <label for="sku" class="form-label">SKU <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="sku" name="sku" 
                               value="${sku}" required maxlength="50">
                        <div class="form-text">Stock Keeping Unit - must be unique</div>
                    </div>
                    
                    <div class="mb-3">
                        <label for="name" class="form-label">Name <span class="text-danger">*</span></label>
                        <input type="text" class="form-control" id="name" name="name" 
                               value="${name}" required maxlength="200">
                    </div>
                    
                    <div class="mb-3">
                        <label for="unitPrice" class="form-label">Unit Price <span class="text-danger">*</span></label>
                        <div class="input-group">
                            <span class="input-group-text">$</span>
                            <input type="number" class="form-control" id="unitPrice" name="unitPrice" 
                                   value="${unitPrice}" required min="0.01" max="99999999.99" step="0.01">
                        </div>
                    </div>
                    
                    <div class="mb-3">
                        <div class="form-check">
                            <input class="form-check-input" type="checkbox" id="active" name="active" 
                                   ${active == 'on' or active == 'true' or empty active ? 'checked' : ''}>
                            <label class="form-check-label" for="active">
                                Active (available for sale)
                            </label>
                        </div>
                    </div>
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancel</button>
                    <button type="submit" class="btn btn-primary">
                        <i class="bi bi-check"></i> Save Item
                    </button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
// Simple client-side pagination
document.addEventListener('DOMContentLoaded', function() {
    const table = document.getElementById('itemsTable');
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
        tableInfo.textContent = `Showing ${showingStart}-${showingEnd} of ${totalRows} items`;
        
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
        tableInfo.textContent = `Showing all ${totalRows} items`;
    }
});

// Show modal if there are validation errors
<c:if test="${not empty error and (not empty sku or not empty name or not empty unitPrice)}">
    document.addEventListener('DOMContentLoaded', function() {
        new bootstrap.Modal(document.getElementById('addItemModal')).show();
    });
</c:if>
</script>
