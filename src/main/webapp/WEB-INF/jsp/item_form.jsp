<%@ include file="layout.jspf" %>

<div class="container main-content">
    <div class="row">
        <div class="col-12">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="bi bi-box-seam"></i> ${item.id > 0 ? 'Edit' : 'Add'} Item</h2>
                <a href="${pageContext.request.contextPath}/app/items" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> Back to Items
                </a>
            </div>
        </div>
    </div>
    
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="bi bi-box-${item.id > 0 ? 'seam' : 'arrow-in-down'}"></i> 
                        Item Information
                    </h5>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/app/items/edit">
                        <c:if test="${item.id > 0}">
                            <input type="hidden" name="id" value="${item.id}">
                        </c:if>
                        
                        <div class="mb-3">
                            <label for="sku" class="form-label">SKU <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="sku" name="sku" 
                                   value="${item.sku}" required maxlength="50">
                            <div class="form-text">Stock Keeping Unit - must be unique</div>
                        </div>
                        
                        <div class="mb-3">
                            <label for="name" class="form-label">Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" 
                                   value="${item.name}" required maxlength="200">
                        </div>
                        
                        <div class="mb-3">
                            <label for="unitPrice" class="form-label">Unit Price <span class="text-danger">*</span></label>
                            <div class="input-group">
                                <span class="input-group-text">$</span>
                                <input type="number" class="form-control" id="unitPrice" name="unitPrice" 
                                       value="${item.unitPrice}" required min="0.01" max="99999999.99" step="0.01">
                            </div>
                        </div>
                        
                        <div class="mb-3">
                            <div class="form-check">
                                <input class="form-check-input" type="checkbox" id="active" name="active" 
                                       ${item.active ? 'checked' : ''}>
                                <label class="form-check-label" for="active">
                                    Active (available for sale)
                                </label>
                            </div>
                        </div>
                        
                        <div class="d-flex justify-content-end gap-2">
                            <a href="${pageContext.request.contextPath}/app/items" class="btn btn-secondary">
                                <i class="bi bi-x"></i> Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check"></i> ${item.id > 0 ? 'Update' : 'Save'} Item
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
