<%@ include file="layout.jspf" %>

<div class="container main-content">
    <div class="row">
        <div class="col-12">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="bi bi-person-gear"></i> ${customer.id > 0 ? 'Edit' : 'Add'} Customer</h2>
                <a href="${pageContext.request.contextPath}/app/customers" class="btn btn-outline-secondary">
                    <i class="bi bi-arrow-left"></i> Back to Customers
                </a>
            </div>
        </div>
    </div>
    
    <div class="row justify-content-center">
        <div class="col-lg-8">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0">
                        <i class="bi bi-person-${customer.id > 0 ? 'gear' : 'plus'}"></i> 
                        Customer Information
                    </h5>
                </div>
                <div class="card-body">
                    <form method="post" action="${pageContext.request.contextPath}/app/customers/edit">
                        <c:if test="${customer.id > 0}">
                            <input type="hidden" name="id" value="${customer.id}">
                            
                            <div class="mb-3">
                                <label class="form-label">Account Number</label>
                                <input type="text" class="form-control" value="${customer.accountNumber}" readonly>
                                <div class="form-text">Account number cannot be changed</div>
                            </div>
                        </c:if>
                        
                        <div class="mb-3">
                            <label for="name" class="form-label">Name <span class="text-danger">*</span></label>
                            <input type="text" class="form-control" id="name" name="name" 
                                   value="${customer.name}" required maxlength="100">
                        </div>
                        
                        <div class="mb-3">
                            <label for="address" class="form-label">Address</label>
                            <textarea class="form-control" id="address" name="address" rows="3" 
                                      maxlength="500">${customer.address}</textarea>
                        </div>
                        
                        <div class="mb-3">
                            <label for="phone" class="form-label">Phone</label>
                            <input type="tel" class="form-control" id="phone" name="phone" 
                                   value="${customer.phone}" maxlength="20" 
                                   pattern="[+\-\s\d()]+">
                            <div class="form-text">Only numbers, spaces, parentheses, + and - allowed</div>
                        </div>
                        
                        <div class="d-flex justify-content-end gap-2">
                            <a href="${pageContext.request.contextPath}/app/customers" class="btn btn-secondary">
                                <i class="bi bi-x"></i> Cancel
                            </a>
                            <button type="submit" class="btn btn-primary">
                                <i class="bi bi-check"></i> ${customer.id > 0 ? 'Update' : 'Save'} Customer
                            </button>
                        </div>
                    </form>
                </div>
            </div>
        </div>
    </div>
</div>
