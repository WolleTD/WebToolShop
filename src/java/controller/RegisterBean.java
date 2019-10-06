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
import java.util.regex.Pattern;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.persistence.PersistenceContext;
import model.Account;
import model.Address;
import model.Customer;
import utilities.Data;

/**
 *
 * @author Eicke Herbertz
 */
@Named(value = "registerBean")
@RequestScoped
public class RegisterBean implements Serializable {

    private String firstname = null;
    private String lastname = null;
    private String street = null;
    private String streetNr = null;
    private String city = null;
    private String postcode = null;
    private String email = null;
    private String phone = null;
    private String username = null;
    private String password = null;
    private String passwordRepeat = null;
    private boolean registered = false;
    private boolean validLastName = false;
    private boolean validPhone = false;
    @Inject
    private Data db;
    @Inject
    private LoginBean lb;
    
    /**
     * Creates a new instance of LoginBean
     */
    public RegisterBean() {
    }
    
    /**
     * Add a new user to the database, provided that all inputs
     * are valid and no user with that username already exists.
     * @return page to be redirected to
     */
    public String register() {
        if(!this.validLastName) {
            FacesMessage msg = new FacesMessage("Nachname zu kurz!");
            FacesContext.getCurrentInstance().addMessage("register-form:lastname", msg);
            return "register.xhtml";
        }
        if(!this.validPhone) {
            FacesMessage msg = new FacesMessage("Telefonnummer ungültig!");
            FacesContext.getCurrentInstance().addMessage("register-form:phone", msg);
            return "register.xhtml";
        }
        if(!password.matches(passwordRepeat)) {
            FacesMessage msg = new FacesMessage("Passwörter stimmen nicht überein!");
            FacesContext.getCurrentInstance().addMessage("register-form:password", msg);
            return "register.xhtml";
        }
        if(db.findAccount(username) == null) {
            try {
                MessageDigest sha256sum = MessageDigest.getInstance("SHA-256");
                sha256sum.update(password.getBytes(StandardCharsets.UTF_8));
                byte[] digest = sha256sum.digest();
                String pwHash = String.format("%064x", new BigInteger(1, digest));
                Account acc = new Account(null, false, username, new Date(), pwHash);
                Address add = new Address(null, street, streetNr, city, postcode, "Deutschland", new Date());
                Customer cust = new Customer(null, firstname, lastname, email, phone);
                cust.setFkAccid(acc);
                cust.setFkAddid(add);
                db.registerUser(acc, add, cust);
                lb.setUsername(username);
                lb.setPassword(null);
                return "index.xhtml";
            } catch(NoSuchAlgorithmException ex) {
                Logger.getLogger(ProductBean.class.getName())
                    .log(Level.SEVERE, null, ex);
                return "register.xhtml";
            }
        } else {
            FacesMessage msg = new FacesMessage("Benutzername bereits vergeben!");
            FacesContext.getCurrentInstance().addMessage("register-form:username", msg);
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
    
    /**
     * Validator for last name – must contain at least 3 characters
     * @param context
     * @param comp
     * @param value
     */
    public void validateLastName(FacesContext context, UIComponent comp, Object value){
        String regex = "[A-Z][a-z]{2,}";
        String name = (String) value;
        this.validLastName = false;
        if(!Pattern.matches(regex, name)){
            FacesMessage msg = new FacesMessage("Nachname muss aus mind. 3 Zeichen bestehen!");
            context.addMessage("register-form:lastname", msg);
        } else {
            this.validLastName = true;
        }
    }
    
    /**
     * Validator for phone number
     * @param context
     * @param comp
     * @param value
     */
    public void validatePhone(FacesContext context, UIComponent comp, Object value){
        String regex = "(?:\\+|0)\\d+([/ -]?\\d)+";
        String telnr = (String) value;
        this.validPhone = false;
        if(!Pattern.matches(regex, telnr)){
            FacesMessage msg = new FacesMessage("TelNr muss mit 0 beginnen, 8-15 Zahlen und nur Zahlen");
            context.addMessage("register-form:phone", msg);
        } else {
            this.validPhone = true;
        }
    }

    /**
     * @return the firstname
     */
    public String getFirstname() {
        return firstname;
    }

    /**
     * @param firstname the firstname to set
     */
    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    /**
     * @return the lastname
     */
    public String getLastname() {
        return lastname;
    }

    /**
     * @param lastname the lastname to set
     */
    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    /**
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email the email to set
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return the phone
     */
    public String getPhone() {
        return phone;
    }

    /**
     * @param phone the phone to set
     */
    public void setPhone(String phone) {
        this.phone = phone;
    }

    /**
     * @return the street
     */
    public String getStreet() {
        return street;
    }

    /**
     * @param street the street to set
     */
    public void setStreet(String street) {
        this.street = street;
    }

    /**
     * @return the streetNr
     */
    public String getStreetNr() {
        return streetNr;
    }

    /**
     * @param streetNr the streetNr to set
     */
    public void setStreetNr(String streetNr) {
        this.streetNr = streetNr;
    }

    /**
     * @return the city
     */
    public String getCity() {
        return city;
    }

    /**
     * @param city the city to set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * @return the postcode
     */
    public String getPostcode() {
        return postcode;
    }

    /**
     * @param postcode the postcode to set
     */
    public void setPostcode(String postcode) {
        this.postcode = postcode;
    }
}
