/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import model.Account;
import utilities.Data;

/**
 *
 * @author Eicke Herbertz
 */
@Named(value = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String username = null;
    private String password = null;
    @Inject
    private Data db;
    
    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }
    
    public String login() throws NoSuchAlgorithmException {
        Account acc = db.findAccount(username);
        if(acc != null) {
            MessageDigest sha256sum = MessageDigest.getInstance("SHA-256");
            sha256sum.update(password.getBytes(StandardCharsets.UTF_8));
            byte[] digest = sha256sum.digest();
            String pwHash = String.format("%064x", new BigInteger(1, digest));

            if(acc.getPwd().equals(pwHash)) {
                password = null;
                FacesMessage msg = new FacesMessage("Erfolgreich eingeloggt!");
                FacesContext.getCurrentInstance().addMessage("login-form", msg);
                return "index.xhtml";
            } else {
                username = null;
                password = null;
                FacesMessage msg = new FacesMessage("Password falsch!");
                FacesContext.getCurrentInstance().addMessage("login-form", msg);
                return "login.xhtml";
            }
        } else {
            username = null;
            FacesMessage msg = new FacesMessage("Benutzername unbekannt!");
            FacesContext.getCurrentInstance().addMessage("login-form", msg);
            return "login.xhtml";
        }
    }

    public String logout() {
        username = null;
        FacesMessage msg = new FacesMessage("Erfolgreich ausgeloggt!");
        FacesContext.getCurrentInstance().addMessage("login-form", msg);
        return "login.xhtml";
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
}
