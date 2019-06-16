/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import model.Product;
import utilities.ShopDatabase;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author eherbertz
 */
@Named(value = "productBean")
@RequestScoped
public class ProductBean {

    private List<Product> productList = null;
    @Inject
    private ShopDatabase dbConnect;

    /**
     * Creates a new instance of Produkte
     */
    public ProductBean() {
        this.productList = new ArrayList<>();
    }

    /**
     * @return the productList
     */
    public List<Product> getProductList() {
        this.dbGetProductList();
        return productList;
    }

    /**
     * @param productList the productList to set
     */
    public void setProductList(List<Product> productList) {
        this.productList = productList;
    }

    private void dbGetProductList() {
        String sql = "SELECT * FROM product ORDER BY Name";
        Connection conn = dbConnect.getConn();
        try {
            ResultSet rs = conn
                    .createStatement()
                    .executeQuery(sql);
            while (rs.next()) {
                productList.add(new Product(
                        rs.getString("Name"),
                        rs.getDouble("NetPrice"))
                );
                Logger.getLogger(ProductBean.class.getName())
                        .log(Level.INFO, rs.getString("Name"), rs);
            }//while
            rs.close();
        } catch (SQLException ex) {
            Logger
                    .getLogger(ProductBean.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    
}
