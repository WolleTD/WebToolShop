/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import javax.inject.Inject;
import model.Product;
import utilities.Data;

/**
 *
 * @author wolle
 */
@Named(value = "cartBean")
@SessionScoped
public class CartBean implements Serializable {
    
    private HashMap<Product,Integer> productList = new HashMap();
    @Inject
    private Data dbBean;
    
    /**
     * Creates a new instance of CartBean
     */
    public CartBean() {
    }

    /**
     * @return the productList
     */
    public HashMap<Product,Integer> getProductList() {
        return productList;
    }

    /**
     * @param productList the productList to set
     */
    public void setProductList(HashMap<Product,Integer> productList) {
        this.productList = productList;
    }
    
    public void addToCart(Product product) {
        if(this.productList.containsKey(product)) {
            int amount = this.productList.get(product).intValue() + 1;
            this.productList.replace(product, amount);
        }
        else {
            this.productList.put(product, 1);
        }
    }
    
    public void changeAmount(Product product, int newAmount) {
        if(newAmount == 0) {
            this.productList.remove(product);
        }
        else {
            this.productList.replace(product, newAmount);
        }
    }
}
