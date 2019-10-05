/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.inject.Named;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;

/**
 *
 * @author Eicke Herbertz
 */
@Named(value = "orderBean")
@RequestScoped
public class OrderBean {
    @Inject
    private LoginBean lb;

    /**
     * Creates a new instance of OrderBean
     */
    public OrderBean() {
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
