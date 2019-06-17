/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package controller;

import java.io.IOException;
import utilities.JDBCData;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import java.io.Serializable;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.context.ExternalContext;
import javax.inject.Inject;

/**
 *
 * @author eherbertz
 */
@Named(value = "loginBean")
@SessionScoped
public class LoginBean implements Serializable {

    private String username = null;
    private String password = null;
    @Inject
    private JDBCData dbConnect;
    
    /**
     * Creates a new instance of LoginBean
     */
    public LoginBean() {
    }
    
    public String login() throws IOException {
        String sql = "SELECT * FROM account WHERE Name = '" + username + "'";
        Connection conn = dbConnect.getConn();
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            if(rs.first()) {
                MessageDigest sha256sum = MessageDigest.getInstance("SHA-256");
                sha256sum.update(password.getBytes(StandardCharsets.UTF_8));
                byte[] digest = sha256sum.digest();
                String pwHash = String.format("%064x", new BigInteger(1, digest));

                if(rs.getString("Pwd").equals(pwHash)) {
                    password = null;
                    FacesMessage msg = new FacesMessage("Erfolgreich eingeloggt!");
                    FacesContext.getCurrentInstance().addMessage("login-form", msg);
                    return "index.xhtml";
                } else {
                    FacesMessage msg = new FacesMessage("Password falsch!");
                    FacesContext.getCurrentInstance().addMessage("login-form", msg);
                }
            } else {
                username = null;
                FacesMessage msg = new FacesMessage("Benutzername unbekannt!");
                FacesContext.getCurrentInstance().addMessage("login-form", msg);
            }
            rs.close();
        } catch (NoSuchAlgorithmException | SQLException ex) {
            Logger.getLogger(ProductBean.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        return "login.xhtml";
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
