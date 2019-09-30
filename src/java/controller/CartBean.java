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
import java.util.ArrayList;
import javax.inject.Inject;
import model.Product;
import utilities.CartEntry;
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
    @Inject
    private LoginBean lb;
    
    /**
     * Creates a new instance of CartBean
     */
    public CartBean() {
    }

    /**
     * @return the productList
     */
    public ArrayList<CartEntry> getProductList() {
        ArrayList<CartEntry> plist = new ArrayList();
        this.productList.entrySet().forEach((entry) -> {
            plist.add(new CartEntry(entry.getKey(), entry.getValue()));
        });
        return plist;
    }

    /**
     * @param productList the productList to set
     */
    public void setProductList(ArrayList<CartEntry> productList) {
        this.productList = new HashMap();
        productList.forEach((entry) -> {
            this.productList.put(entry.getProduct(), entry.getAmount());
        });
    }
    
    public void addToCart(Product product) {
        if(this.productList.containsKey(product)) {
            int amount = this.productList.get(product) + 1;
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
    
    public String placeOrder() {
        if(!lb.getLoggedIn()) {
            return "login.xhtml";
        }
        else {
            return "checkout.xhtml";
        }
    }
}
