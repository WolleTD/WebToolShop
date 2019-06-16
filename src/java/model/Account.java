/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import javax.inject.Named;
import javax.enterprise.context.Dependent;

/**
 *
 * @author eherbertz
 */
@Named(value = "account")
@Dependent
public class Account {

    private String name;
    private String pwdHash;

    /**
     * Creates a new instance of User
     */
    public Account() {
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the pwdSha
     */
    public String getPwdHash() {
        return pwdHash;
    }

    /**
     * @param pwdHash the pwdHash to set
     */
    public void setPwdHash(String pwdHash) {
        this.pwdHash = pwdHash;
    }
}
