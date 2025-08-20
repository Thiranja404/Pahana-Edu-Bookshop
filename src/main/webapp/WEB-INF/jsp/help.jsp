<%@ include file="layout.jspf" %>

<div class="container main-content">
    <div class="row">
        <div class="col-12">
            <h2><i class="bi bi-question-circle"></i> Help & User Guide</h2>
            <p class="text-muted">Complete guide to using the Pahana Edu Bookstore Billing System</p>
        </div>
    </div>
    
    <!-- Quick Start Guide -->
    <div class="row">
        <div class="col-12">
            <div class="card mb-4">
                <div class="card-header bg-primary text-white">
                    <h5 class="mb-0"><i class="bi bi-rocket"></i> Quick Start Guide</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <h6><i class="bi bi-1-circle-fill text-primary"></i> Login to System</h6>
                            <ul>
                                <li>Visit the <strong>/login</strong> page</li>
                                <li>Default credentials: <code>admin</code> / <code>admin</code></li>
                                <li>Click "Login" to access the dashboard</li>
                            </ul>
                        </div>
                        <div class="col-md-6">
                            <h6><i class="bi bi-2-circle-fill text-primary"></i> Add Customers & Items</h6>
                            <ul>
                                <li>Use <strong>Customers</strong> menu to add new customers</li>
                                <li>Use <strong>Items</strong> menu to add products/books</li>
                                <li>Fill required fields and save</li>
                            </ul>
                        </div>
                    </div>
                    <div class="row mt-3">
                        <div class="col-md-6">
                            <h6><i class="bi bi-3-circle-fill text-primary"></i> Create Bills</h6>
                            <ul>
                                <li>Go to <strong>Billing</strong> section</li>
                                <li>Select customer using typeahead search</li>
                                <li>Add items with quantities</li>
                                <li>Review total and create bill</li>
                            </ul>
                        </div>
                        <div class="col-md-6">
                            <h6><i class="bi bi-4-circle-fill text-primary"></i> Print & Logout</h6>
                            <ul>
                                <li>View bills and use browser print (Ctrl+P)</li>
                                <li>Check <strong>Account Details</strong> for history</li>
                                <li>Click <strong>Logout</strong> when done</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Detailed Instructions -->
    <div class="row">
        <div class="col-lg-6">
            <!-- Login Section -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-box-arrow-in-right"></i> Login Instructions</h5>
                </div>
                <div class="card-body">
                    <h6>Default Admin Account:</h6>
                    <div class="alert alert-info">
                        <strong>Username:</strong> <code>admin</code><br>
                        <strong>Password:</strong> <code>admin</code>
                    </div>
                    
                    <h6>Steps to Login:</h6>
                    <ol>
                        <li>Navigate to <strong>/login</strong> page</li>
                        <li>Enter username and password</li>
                        <li>Click "Login" button</li>
                        <li>You'll be redirected to dashboard</li>
                    </ol>
                    
                    <div class="alert alert-warning">
                        <strong>Note:</strong> All pages except login require authentication. 
                        You'll be redirected to login if not authenticated.
                    </div>
                </div>
            </div>
            
            <!-- Customer Management -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-people"></i> Customer Management</h5>
                </div>
                <div class="card-body">
                    <h6>Adding New Customers:</h6>
                    <ol>
                        <li>Click <strong>"Customers"</strong> in navigation</li>
                        <li>Click <strong>"Add New Customer"</strong> button</li>
                        <li>Fill in customer details:
                            <ul>
                                <li><strong>Name</strong> (required)</li>
                                <li><strong>Phone</strong> (optional)</li>
                                <li><strong>Address</strong> (optional)</li>
                            </ul>
                        </li>
                        <li>Click <strong>"Save Customer"</strong></li>
                        <li>System auto-generates account number</li>
                    </ol>
                    
                    <h6>Managing Existing Customers:</h6>
                    <ul>
                        <li>View all customers in the list</li>
                        <li>Click <strong>"Edit"</strong> to modify details</li>
                        <li>Use search to find specific customers</li>
                        <li>Account numbers are unique and auto-generated</li>
                    </ul>
                </div>
            </div>
        </div>
        
        <div class="col-lg-6">
            <!-- Item Management -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-box"></i> Item Management</h5>
                </div>
                <div class="card-body">
                    <h6>Adding New Items/Books:</h6>
                    <ol>
                        <li>Click <strong>"Items"</strong> in navigation</li>
                        <li>Click <strong>"Add New Item"</strong> button</li>
                        <li>Fill in item details:
                            <ul>
                                <li><strong>Name</strong> (required)</li>
                                <li><strong>Description</strong> (optional)</li>
                                <li><strong>Price</strong> (required, decimal format)</li>
                            </ul>
                        </li>
                        <li>Click <strong>"Save Item"</strong></li>
                        <li>Item codes are auto-generated</li>
                    </ol>
                    
                    <h6>Price Management:</h6>
                    <ul>
                        <li>Enter prices in decimal format (e.g., 29.99)</li>
                        <li>System supports up to 2 decimal places</li>
                        <li>Click <strong>"Edit"</strong> to update prices</li>
                        <li>Price changes apply to new bills only</li>
                    </ul>
                </div>
            </div>
            
            <!-- Billing Process -->
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-receipt"></i> Billing Process</h5>
                </div>
                <div class="card-body">
                    <h6>Creating a New Bill:</h6>
                    <ol>
                        <li>Click <strong>"Billing"</strong> in navigation</li>
                        <li>Select customer using typeahead search:
                            <ul>
                                <li>Type customer name or account number</li>
                                <li>Click on suggested customer</li>
                            </ul>
                        </li>
                        <li>Add items to bill:
                            <ul>
                                <li>Type item name in search box</li>
                                <li>Select item from suggestions</li>
                                <li>Enter quantity</li>
                                <li>Click <strong>"Add Item"</strong></li>
                            </ul>
                        </li>
                        <li>Review bill details and total</li>
                        <li>Click <strong>"Create Bill"</strong></li>
                    </ol>
                    
                    <h6>Bill Features:</h6>
                    <ul>
                        <li>Real-time total calculation</li>
                        <li>Remove items before creating bill</li>
                        <li>Automatic bill number generation</li>
                        <li>Date/time stamping</li>
                    </ul>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Advanced Features -->
    <div class="row">
        <div class="col-12">
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-gear"></i> Advanced Features</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <h6><i class="bi bi-search"></i> Smart Search Features</h6>
                            <ul>
                                <li><strong>Typeahead Search:</strong> Start typing to see suggestions</li>
                                <li><strong>Customer Search:</strong> Search by name or account number</li>
                                <li><strong>Item Search:</strong> Search by name or item code</li>
                                <li><strong>Debounced Search:</strong> Reduced server calls for better performance</li>
                            </ul>
                            
                            <h6><i class="bi bi-person-badge"></i> Account Details</h6>
                            <ul>
                                <li>Search customers by account number or name</li>
                                <li>View complete billing history</li>
                                <li>See total purchase amounts</li>
                                <li>Quick access to view bills</li>
                            </ul>
                        </div>
                        <div class="col-md-6">
                            <h6><i class="bi bi-printer"></i> Printing Bills</h6>
                            <ul>
                                <li>Bills open in print-friendly format</li>
                                <li>Use browser print function (Ctrl+P)</li>
                                <li>Optimized layout for standard paper</li>
                                <li>Clean, professional appearance</li>
                            </ul>
                            
                            <h6><i class="bi bi-shield-check"></i> Security Features</h6>
                            <ul>
                                <li>Session-based authentication</li>
                                <li>Password hashing with SHA-256</li>
                                <li>Protected routes (auto-redirect to login)</li>
                                <li>Secure logout functionality</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
    
    <!-- Troubleshooting -->
    <div class="row">
        <div class="col-12">
            <div class="card mb-4">
                <div class="card-header">
                    <h5 class="mb-0"><i class="bi bi-tools"></i> Troubleshooting</h5>
                </div>
                <div class="card-body">
                    <div class="row">
                        <div class="col-md-6">
                            <h6>Common Issues:</h6>
                            <dl>
                                <dt>Cannot login with admin/admin:</dt>
                                <dd>Ensure database is running and seed data is loaded</dd>
                                
                                <dt>Search not working:</dt>
                                <dd>Check internet connection for Bootstrap/JavaScript dependencies</dd>
                                
                                <dt>Bill not creating:</dt>
                                <dd>Ensure customer is selected and at least one item is added</dd>
                                
                                <dt>Page redirects to login:</dt>
                                <dd>Session expired - login again</dd>
                            </dl>
                        </div>
                        <div class="col-md-6">
                            <h6>Browser Requirements:</h6>
                            <ul>
                                <li>Modern browser with JavaScript enabled</li>
                                <li>Internet connection for Bootstrap CDN</li>
                                <li>Cookies enabled for session management</li>
                                <li>Popup blocker disabled for bill viewing</li>
                            </ul>
                            
                            <h6>System Requirements:</h6>
                            <ul>
                                <li>Java 21 or higher</li>
                                <li>Apache Tomcat 10.1</li>
                                <li>MySQL 8.0 or higher</li>
                                <li>Modern web browser</li>
                            </ul>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>
