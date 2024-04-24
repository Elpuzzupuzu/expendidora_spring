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

    private String Status;




    // set and getters


    public String getStatus() {
        return Status;
    }

    public void setStatus(String status) {
        Status = status;
    }

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
         // arreglado el error de restar 2 veces los prodcutos vendidos :)

        for (Product product : products) {
            totalQuantity += product.getQuantity();
        }

        return totalQuantity ;
    }



}
