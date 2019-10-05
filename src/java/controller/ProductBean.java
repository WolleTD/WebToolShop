/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Product;
import java.util.List;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import utilities.Data;

/**
 *
 * @author Eicke Herbertz
 */
@Named(value = "productBean")
@RequestScoped
public class ProductBean {

    @Inject
    private Data db;

    /**
     * Creates a new instance of Produkte
     */
    public ProductBean() {
    }

    /**
     * @return the productList
     */
    public List<Product> getProducts() {
        return db.getProductList();
    }
}
