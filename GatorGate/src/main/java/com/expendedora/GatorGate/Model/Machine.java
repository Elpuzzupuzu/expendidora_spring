package com.expendedora.GatorGate.Model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
@Entity

public class Machine {



    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String location;

    @JsonManagedReference // esto evita la carga de n datos
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "machine")
    private List<Product> products;

    private int minimalproduct;

    private double stonks;











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

    public List<Product> getProducts() {
        return products;
    }




    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getStonks() {
        return stonks;
    }

    public void setStonks(double stonks) {
        this.stonks = stonks;
    }

    public void setMinimalproduct(int minimal) {


        this.minimalproduct=minimal;
    }

    public int getMinimalproduct() {
        return minimalproduct;
    }

    public int calculateMinimalProduct() {
        int totalQuantity = 0;
        int totalQuantitySold = 0;

        // Itera sobre la lista de productos y suma las cantidades y cantidades vendidas
        for (Product product : products) {
            totalQuantity += product.getQuantity();
            totalQuantitySold += product.getQuantitysold();
        }

        // Calcula el minimalproduct restando la cantidad total y la cantidad vendida
        minimalproduct = totalQuantity - totalQuantitySold;

        return minimalproduct;
    }

}
