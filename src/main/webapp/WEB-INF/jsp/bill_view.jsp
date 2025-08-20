<%@ include file="layout.jspf" %>

<div class="container main-content">
    <div class="row no-print">
        <div class="col-12">
            <div class="d-flex justify-content-between align-items-center mb-4">
                <h2><i class="bi bi-receipt"></i> Bill Details</h2>
                <div>
                    <button type="button" class="btn btn-primary" onclick="window.print()">
                        <i class="bi bi-printer"></i> Print Bill
                    </button>
                    <a href="${pageContext.request.contextPath}/app/billing" class="btn btn-outline-secondary">
                        <i class="bi bi-arrow-left"></i> Back to Billing
                    </a>
                </div>
            </div>
        </div>
    </div>
    
    <div class="row">
        <div class="col-12">
            <div class="card">
                <div class="card-body">
                    <!-- Bill Header -->
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <h3 class="text-primary"><i class="bi bi-book"></i> Pahana Edu Bookstore</h3>
                            <p class="mb-0">Educational Books & Supplies</p>
                            <p class="mb-0">123 Education Street</p>
                            <p class="mb-0">Phone: (555) 123-4567</p>
                        </div>
                        <div class="col-md-6 text-md-end">
                            <h4>BILL</h4>
                            <p class="mb-1"><strong>Bill No:</strong> ${bill.billNo}</p>
                            <p class="mb-1"><strong>Date:</strong> <fmt:formatDate value="${bill.billDate}" pattern="MMM dd, yyyy HH:mm" /></p>
                        </div>
                    </div>
                    
                    <!-- Customer Information -->
                    <div class="row mb-4">
                        <div class="col-md-6">
                            <h6>Bill To:</h6>
                            <div class="border-start border-primary ps-3">
                                <p class="mb-1"><strong>${bill.customer.name}</strong></p>
                                <p class="mb-1">Account: ${bill.customer.accountNumber}</p>
                                <c:if test="${not empty bill.customer.address}">
                                    <p class="mb-1">${bill.customer.address}</p>
                                </c:if>
                                <c:if test="${not empty bill.customer.phone}">
                                    <p class="mb-0">Phone: ${bill.customer.phone}</p>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Bill Items -->
                    <div class="row">
                        <div class="col-12">
                            <table class="table table-striped">
                                <thead class="table-dark">
                                    <tr>
                                        <th>SKU</th>
                                        <th>Item Name</th>
                                        <th class="text-center">Qty</th>
                                        <th class="text-end">Unit Price</th>
                                        <th class="text-end">Line Total</th>
                                    </tr>
                                </thead>
                                <tbody>
                                    <c:forEach items="${bill.billItems}" var="billItem">
                                        <tr>
                                            <td><strong>${billItem.item.sku}</strong></td>
                                            <td>${billItem.item.name}</td>
                                            <td class="text-center">${billItem.qty}</td>
                                            <td class="text-end">$<fmt:formatNumber value="${billItem.unitPrice}" pattern="#,##0.00"/></td>
                                            <td class="text-end">$<fmt:formatNumber value="${billItem.lineTotal}" pattern="#,##0.00"/></td>
                                        </tr>
                                    </c:forEach>
                                </tbody>
                                <tfoot>
                                    <tr class="table-light">
                                        <td colspan="4" class="text-end"><strong>TOTAL:</strong></td>
                                        <td class="text-end"><strong>$<fmt:formatNumber value="${bill.total}" pattern="#,##0.00"/></strong></td>
                                    </tr>
                                </tfoot>
                            </table>
                        </div>
                    </div>
                    
                    <!-- Bill Footer -->
                    <div class="row mt-4">
                        <div class="col-12">
                            <div class="border-top pt-3">
                                <div class="row">
                                    <div class="col-md-6">
                                        <h6>Payment Information:</h6>
                                        <p class="mb-1">Payment due within 30 days</p>
                                        <p class="mb-0">Thank you for your business!</p>
                                    </div>
                                    <div class="col-md-6 text-md-end">
                                        <h6>Contact Information:</h6>
                                        <p class="mb-1">Email: info@pahanaedu.com</p>
                                        <p class="mb-0">Website: www.pahanaedu.com</p>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </div>
                    
                    <!-- Print Timestamp -->
                    <div class="row mt-4 d-print-none no-print">
                        <div class="col-12">
                            <hr>
                            <small class="text-muted">
                                <i class="bi bi-info-circle"></i> 
                                Generated on <fmt:formatDate value="<%= new java.util.Date() %>" pattern="MMM dd, yyyy HH:mm:ss" />
                            </small>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

<style>
@media print {
    .container {
        max-width: 100%;
        margin: 0;
        padding: 0;
    }
    
    .card {
        border: none;
        box-shadow: none;
    }
    
    .card-body {
        padding: 0;
    }
    
    h3, h4, h6 {
        color: #000 !important;
    }
    
    .text-primary {
        color: #000 !important;
    }
    
    .border-primary {
        border-color: #000 !important;
    }
    
    .table-dark {
        background-color: #000 !important;
        color: #fff !important;
    }
    
    .table-dark th {
        border-color: #000 !important;
    }
}
</style>
