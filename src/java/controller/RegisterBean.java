/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.enterprise.context.RequestScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.sql.ResultSet;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.persistence.PersistenceContext;
import model.Account;
import utilities.AccountJpaController;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@Named(value = "registerBean")
@RequestScoped
public class RegisterBean implements Serializable {

    private String username = null;
    private String password = null;
    private String passwordRepeat = null;
    private boolean registered = false;
    @PersistenceContext
    private AccountJpaController ac;
    
    /**
     * Creates a new instance of LoginBean
     */
    public RegisterBean() {
    }
    
    public void register() {
        if(!password.matches(passwordRepeat)) {
            FacesMessage msg = new FacesMessage("Passwörter stimmen nicht überein!");
            FacesContext.getCurrentInstance().addMessage("register-form", msg);
            return;
        }
        try {
            ac.create(new Account(null, false, username, new Date(), password));
        } catch (Exception ex) {
            Logger.getLogger(ProductBean.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * @return the username
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username the username to set
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password the password to set
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return if a user is logged in
     */
    public boolean getLoggedIn() {
        return username != null && password == null;
    }

    /**
     * @return the passwordRepeat
     */
    public String getPasswordRepeat() {
        return passwordRepeat;
    }

    /**
     * @param passwordRepeat the passwordRepeat to set
     */
    public void setPasswordRepeat(String passwordRepeat) {
        this.passwordRepeat = passwordRepeat;
    }

    /**
     * @return the registered
     */
    public boolean isRegistered() {
        return registered;
    }

    /**
     * @param registered the registered to set
     */
    public void setRegistered(boolean registered) {
        this.registered = registered;
    }
}
