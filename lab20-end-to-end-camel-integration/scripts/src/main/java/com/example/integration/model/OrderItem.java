package com.alnafi.integration.model;

import java.math.BigDecimal;

public class OrderItem {
 private String productCode;
 private String productName;
 private Integer quantity;
 private BigDecimal unitPrice;
 private BigDecimal lineTotal;

 // Constructors
 public OrderItem() {}

 public OrderItem(String productCode, Integer quantity, BigDecimal unitPrice) {
 this.productCode = productCode;
 this.quantity = quantity;
 this.unitPrice = unitPrice;
 this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
 }

 // Getters and Setters
 public String getProductCode() { return productCode; }
 public void setProductCode(String productCode) { this.productCode = productCode; }

 public String getProductName() { return productName; }
 public void setProductName(String productName) { this.productName = productName; }

 public Integer getQuantity() { return quantity; }
 public void setQuantity(Integer quantity) { this.quantity = quantity; }

 public BigDecimal getUnitPrice() { return unitPrice; }
 public void setUnitPrice(BigDecimal unitPrice) { this.unitPrice = unitPrice; }

 public BigDecimal getLineTotal() { return lineTotal; }
 public void setLineTotal(BigDecimal lineTotal) { this.lineTotal = lineTotal; }

 public void calculateLineTotal() {
 if (quantity != null && unitPrice != null) {
 this.lineTotal = unitPrice.multiply(BigDecimal.valueOf(quantity));
 }
 }
}
