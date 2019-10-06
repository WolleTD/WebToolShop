/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import javax.inject.Named;
import javax.enterprise.context.SessionScoped;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceUnit;
import javax.persistence.TypedQuery;
import javax.transaction.UserTransaction;
import model.Account;
import model.Address;
import model.Customer;
import model.Orderdetail;
import model.Orders;
import model.Product;

/**
 *
 * @author Eicke Herbertz
 */
@Named(value = "dBBean")
@SessionScoped
public class Data implements Serializable {
    @PersistenceUnit
    private EntityManagerFactory emf;
    
    @Resource
    private UserTransaction ut;
    
    /**
     * Creates a new instance of DBSessionBean
     */
    public Data() {
    }
    
    public List<Product> getProductList(){
        EntityManager em = emf.createEntityManager();
        TypedQuery<Product> query = em.createNamedQuery("Product.findAll", Product.class);
        return query.getResultList();
    }
    
    public Account findAccount(String accName){
        EntityManager em = emf.createEntityManager();
        try{    
            TypedQuery<Account> query = em.createNamedQuery("Account.findByName", Account.class);
            query.setParameter("name", accName);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    public Customer findKundeByAccount(Account account){
        EntityManager em = emf.createEntityManager();
        try{    
            TypedQuery<Customer> query = em.createNamedQuery("Customer.findByAccount", Customer.class);
            query.setParameter("account", account);
            return query.getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    public boolean registerUser(Account account, Address address, Customer customer) {
        EntityManager em = emf.createEntityManager();
        try{
            ut.begin();
            em.joinTransaction();
            em.persist(account);
            em.persist(address);
            em.persist(customer);
            ut.commit();
            return true;
        } catch(Exception e) {
            try { ut.rollback();}
            catch (Exception e1) {}
            return false;
        }
    }
    
    public boolean addOrder(Orders order, ArrayList<Orderdetail> details) {
        EntityManager em = emf.createEntityManager();
        try{
            ut.begin();
            em.joinTransaction();
            em.persist(order);
            details.forEach((entry) -> {
                em.persist(entry);
            });
            ut.commit();
            return true;
        } catch(Exception e) {
            try { ut.rollback();}
            catch (Exception e1) {}
            return false;
        }
    }
}
