package com.example.bdgasbookingsystem.Class;

public class Product {

    private String Id;
    private String ProductName;
    private String ProductId;

    public Product(String Id,String ProductName,String ProductId){
        this.Id=Id;
        this.ProductName=ProductName;
        this.ProductId=ProductId;
    }

    public String getId() {
        return Id;
    }

    public String getProductName() {
        return ProductName;
    }
    public String getProductId() {
        return ProductId;
    }
}
