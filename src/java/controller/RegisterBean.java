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
import java.security.NoSuchAlgorithmException;
import java.sql.ResultSet;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import model.Account;
import utilities.Data;

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
    @Inject
    private Data db;
    
    /**
     * Creates a new instance of LoginBean
     */
    public RegisterBean() {
    }
    
    public String register() {
        if(!password.matches(passwordRepeat)) {
            FacesMessage msg = new FacesMessage("Passwörter stimmen nicht überein!");
            FacesContext.getCurrentInstance().addMessage("register-form", msg);
            return "register.xhtml";
        }
        try {
            MessageDigest sha256sum = MessageDigest.getInstance("SHA-256");
            sha256sum.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] digest = sha256sum.digest();
            String pwHash = String.format("%064x", new BigInteger(1, digest));
            db.setAccount(new Account(null, false, username, new Date(), pwHash));
            FacesMessage msg = new FacesMessage("Erfolgreich registriert! Einloggen:");
            FacesContext.getCurrentInstance().addMessage("login-form", msg);
            return "login.xhtml";
        } catch (Exception ex) {
            Logger.getLogger(ProductBean.class.getName())
                    .log(Level.SEVERE, null, ex);
            return "register.xhtml";
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
