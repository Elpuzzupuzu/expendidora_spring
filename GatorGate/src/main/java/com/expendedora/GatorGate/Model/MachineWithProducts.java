package com.expendedora.GatorGate.Model;

import java.util.List;

public class MachineWithProducts {

    private long id;
    private String location;
    private double stonks;
    private int minimalproduct;
    private List<Product> products;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public double getStonks() {
        return stonks;
    }

    public void setStonks(double stonks) {
        this.stonks = stonks;
    }


    public int getMinimalproduct() {
        return minimalproduct;
    }

    public void setMinimalproduct(int minimalproduct) {
        this.minimalproduct = minimalproduct;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
