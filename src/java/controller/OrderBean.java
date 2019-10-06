/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import model.Address;
import model.Customer;
import model.Orders;
import model.Orderdetail;
import model.Product;
import utilities.CartEntry;
import utilities.Data;

/**
 *
 * @author Eicke Herbertz
 */
@Named(value = "orderBean")
@RequestScoped
public class OrderBean {
    private Orders order;
    
    @Inject
    private LoginBean lb;
    @Inject
    private CartBean cb;
    @Inject
    private Data db;

    /**
     * Creates a new instance of OrderBean
     */
    public OrderBean() {
    }
    
    
    public String placeOrder() {
        if(!lb.getLoggedIn()) {
            lb.setReturnUrl("checkout.xhtml");
            return "login.xhtml";
        }
        else {
            order = new Orders(null);
            order.setStatus("Bestellungsprüfung");
            order.setFkCid(lb.getCustomer());
            ArrayList<Orderdetail> details = new ArrayList<>();
            cb.getProductList().forEach((entry) -> {
                Orderdetail d = new Orderdetail(null, entry.getAmount(), new BigDecimal(0), new Date());
                d.setFkPid(entry.getProduct());
                d.setFkOid(order);
                details.add(d);
            });
            if (db.addOrder(order, details)) {
                return "checkout.xhtml";
            } else {
                FacesMessage msg = new FacesMessage("Die Bestellung konnte nicht verarbeitet werden!");
                FacesContext.getCurrentInstance().addMessage("order-error", msg);
                return "cart.xhtml";
            }
        }
    }

    /**
     * @return the order
     */
    public Orders getOrder() {
        return order;
    }

    /**
     * @param order the order to set
     */
    public void setOrder(Orders order) {
        this.order = order;
    }
}
