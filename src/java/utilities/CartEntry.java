/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import model.Product;

/**
 *
 * @author Eicke Herbertz
 */
public class CartEntry {
    private Product product;
    private Integer amount;
    
    /**
     * Create a new cart entry. This intermediate type is required
     * as a HashMap isn't supported by Glassfish 4
     * @param product for this cart entry
     * @param amount to initialize
     */
    public CartEntry(Product product, Integer amount) {
        this.product = product;
        this.amount = amount;
    }

    /**
     * @return the product
     */
    public Product getProduct() {
        return product;
    }

    /**
     * @param product the product to set
     */
    public void setProduct(Product product) {
        this.product = product;
    }

    /**
     * @return the amount
     */
    public Integer getAmount() {
        return amount;
    }

    /**
     * @param amount the amount to set
     */
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
}
