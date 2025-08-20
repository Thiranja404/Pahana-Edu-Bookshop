<%@ include file="layout.jspf" %>

<div class="container main-content">
    <div class="row">
        <div class="col-12">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="bi bi-speedometer2"></i> Dashboard</h2>
                <span class="text-muted">Welcome back, ${sessionScope.username}!</span>
            </div>
        </div>
    </div>
    
    <div class="row">
        <div class="col-lg-3 col-md-6 mb-4">
            <div class="card text-white bg-primary">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h6 class="card-title">Customers</h6>
                            <h4 class="mb-0">Manage</h4>
                        </div>
                        <div class="align-self-center">
                            <i class="bi bi-people" style="font-size: 2rem;"></i>
                        </div>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/app/customers" class="card-footer text-white text-decoration-none">
                    View Customers <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </div>
        
        <div class="col-lg-3 col-md-6 mb-4">
            <div class="card text-white bg-success">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h6 class="card-title">Items</h6>
                            <h4 class="mb-0">Catalog</h4>
                        </div>
                        <div class="align-self-center">
                            <i class="bi bi-box" style="font-size: 2rem;"></i>
                        </div>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/app/items" class="card-footer text-white text-decoration-none">
                    View Items <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </div>
        
        <div class="col-lg-3 col-md-6 mb-4">
            <div class="card text-white bg-warning">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h6 class="card-title">Billing</h6>
                            <h4 class="mb-0">Create</h4>
                        </div>
                        <div class="align-self-center">
                            <i class="bi bi-receipt" style="font-size: 2rem;"></i>
                        </div>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/app/billing" class="card-footer text-white text-decoration-none">
                    Create Bill <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </div>
        
        <div class="col-lg-3 col-md-6 mb-4">
            <div class="card text-white bg-info">
                <div class="card-body">
                    <div class="d-flex justify-content-between">
                        <div>
                            <h6 class="card-title">Billing</h6>
                            <h4 class="mb-0">History</h4>
                        </div>
                        <div class="align-self-center">
                            <i class="bi bi-person-badge" style="font-size: 2rem;"></i>
                        </div>
                    </div>
                </div>
                <a href="${pageContext.request.contextPath}/app/account" class="card-footer text-white text-decoration-none">
                    View Account <i class="bi bi-arrow-right"></i>
                </a>
            </div>
        </div>
    </div>
    
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-info-circle"></i> Quick Start Guide</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <h6><i class="bi bi-1-circle"></i> Manage Customers</h6>
                            <p class="text-muted">Add new customers or edit existing ones. Each customer gets a unique account number.</p>
                            
                            <h6><i class="bi bi-2-circle"></i> Manage Items</h6>
                            <p class="text-muted">Add books and other items to your catalog with SKU, name, and pricing.</p>
                        </div>
                        <div class="col-md-6">
                            <h6><i class="bi bi-3-circle"></i> Create Bills</h6>
                            <p class="text-muted">Search for customers and items, add quantities, and generate bills.</p>
                            
                            <h6><i class="bi bi-4-circle"></i> View Account Details</h6>
                            <p class="text-muted">Look up customer information and their billing history.</p>
                        </div>
                    </div>
                    <div class="text-center mt-3">
                        <a href="${pageContext.request.contextPath}/app/help" class="btn btn-outline-primary">
                            <i class="bi bi-question-circle"></i> Need Help?
                        </a>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
