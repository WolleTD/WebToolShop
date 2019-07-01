/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.Serializable;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import model.Address;
import model.Account;
import model.Order1;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import model.Customer;
import utilities.exceptions.IllegalOrphanException;
import utilities.exceptions.NonexistentEntityException;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@SessionScoped
public class CustomerJpaController implements Serializable {

    public CustomerJpaController() {
    }
    
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Customer customer) throws RollbackFailureException, Exception {
        if (customer.getOrder1Collection() == null) {
            customer.setOrder1Collection(new ArrayList<Order1>());
        }
        try {
            utx.begin();
            Address fkAddid = customer.getFkAddid();
            if (fkAddid != null) {
                fkAddid = em.getReference(fkAddid.getClass(), fkAddid.getId());
                customer.setFkAddid(fkAddid);
            }
            Account fkAccid = customer.getFkAccid();
            if (fkAccid != null) {
                fkAccid = em.getReference(fkAccid.getClass(), fkAccid.getId());
                customer.setFkAccid(fkAccid);
            }
            Collection<Order1> attachedOrder1Collection = new ArrayList<Order1>();
            for (Order1 order1CollectionOrder1ToAttach : customer.getOrder1Collection()) {
                order1CollectionOrder1ToAttach = em.getReference(order1CollectionOrder1ToAttach.getClass(), order1CollectionOrder1ToAttach.getId());
                attachedOrder1Collection.add(order1CollectionOrder1ToAttach);
            }
            customer.setOrder1Collection(attachedOrder1Collection);
            em.persist(customer);
            if (fkAddid != null) {
                fkAddid.getCustomerCollection().add(customer);
                fkAddid = em.merge(fkAddid);
            }
            if (fkAccid != null) {
                fkAccid.getCustomerCollection().add(customer);
                fkAccid = em.merge(fkAccid);
            }
            for (Order1 order1CollectionOrder1 : customer.getOrder1Collection()) {
                Customer oldFkCidOfOrder1CollectionOrder1 = order1CollectionOrder1.getFkCid();
                order1CollectionOrder1.setFkCid(customer);
                order1CollectionOrder1 = em.merge(order1CollectionOrder1);
                if (oldFkCidOfOrder1CollectionOrder1 != null) {
                    oldFkCidOfOrder1CollectionOrder1.getOrder1Collection().remove(order1CollectionOrder1);
                    oldFkCidOfOrder1CollectionOrder1 = em.merge(oldFkCidOfOrder1CollectionOrder1);
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void edit(Customer customer) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Customer persistentCustomer = em.find(Customer.class, customer.getId());
            Address fkAddidOld = persistentCustomer.getFkAddid();
            Address fkAddidNew = customer.getFkAddid();
            Account fkAccidOld = persistentCustomer.getFkAccid();
            Account fkAccidNew = customer.getFkAccid();
            Collection<Order1> order1CollectionOld = persistentCustomer.getOrder1Collection();
            Collection<Order1> order1CollectionNew = customer.getOrder1Collection();
            List<String> illegalOrphanMessages = null;
            for (Order1 order1CollectionOldOrder1 : order1CollectionOld) {
                if (!order1CollectionNew.contains(order1CollectionOldOrder1)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Order1 " + order1CollectionOldOrder1 + " since its fkCid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (fkAddidNew != null) {
                fkAddidNew = em.getReference(fkAddidNew.getClass(), fkAddidNew.getId());
                customer.setFkAddid(fkAddidNew);
            }
            if (fkAccidNew != null) {
                fkAccidNew = em.getReference(fkAccidNew.getClass(), fkAccidNew.getId());
                customer.setFkAccid(fkAccidNew);
            }
            Collection<Order1> attachedOrder1CollectionNew = new ArrayList<Order1>();
            for (Order1 order1CollectionNewOrder1ToAttach : order1CollectionNew) {
                order1CollectionNewOrder1ToAttach = em.getReference(order1CollectionNewOrder1ToAttach.getClass(), order1CollectionNewOrder1ToAttach.getId());
                attachedOrder1CollectionNew.add(order1CollectionNewOrder1ToAttach);
            }
            order1CollectionNew = attachedOrder1CollectionNew;
            customer.setOrder1Collection(order1CollectionNew);
            customer = em.merge(customer);
            if (fkAddidOld != null && !fkAddidOld.equals(fkAddidNew)) {
                fkAddidOld.getCustomerCollection().remove(customer);
                fkAddidOld = em.merge(fkAddidOld);
            }
            if (fkAddidNew != null && !fkAddidNew.equals(fkAddidOld)) {
                fkAddidNew.getCustomerCollection().add(customer);
                fkAddidNew = em.merge(fkAddidNew);
            }
            if (fkAccidOld != null && !fkAccidOld.equals(fkAccidNew)) {
                fkAccidOld.getCustomerCollection().remove(customer);
                fkAccidOld = em.merge(fkAccidOld);
            }
            if (fkAccidNew != null && !fkAccidNew.equals(fkAccidOld)) {
                fkAccidNew.getCustomerCollection().add(customer);
                fkAccidNew = em.merge(fkAccidNew);
            }
            for (Order1 order1CollectionNewOrder1 : order1CollectionNew) {
                if (!order1CollectionOld.contains(order1CollectionNewOrder1)) {
                    Customer oldFkCidOfOrder1CollectionNewOrder1 = order1CollectionNewOrder1.getFkCid();
                    order1CollectionNewOrder1.setFkCid(customer);
                    order1CollectionNewOrder1 = em.merge(order1CollectionNewOrder1);
                    if (oldFkCidOfOrder1CollectionNewOrder1 != null && !oldFkCidOfOrder1CollectionNewOrder1.equals(customer)) {
                        oldFkCidOfOrder1CollectionNewOrder1.getOrder1Collection().remove(order1CollectionNewOrder1);
                        oldFkCidOfOrder1CollectionNewOrder1 = em.merge(oldFkCidOfOrder1CollectionNewOrder1);
                    }
                }
            }
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            String msg = ex.getLocalizedMessage();
            if (msg == null || msg.length() == 0) {
                Integer id = customer.getId();
                if (findCustomer(id) == null) {
                    throw new NonexistentEntityException("The customer with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Customer customer;
            try {
                customer = em.getReference(Customer.class, id);
                customer.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The customer with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Order1> order1CollectionOrphanCheck = customer.getOrder1Collection();
            for (Order1 order1CollectionOrphanCheckOrder1 : order1CollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Customer (" + customer + ") cannot be destroyed since the Order1 " + order1CollectionOrphanCheckOrder1 + " in its order1Collection field has a non-nullable fkCid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Address fkAddid = customer.getFkAddid();
            if (fkAddid != null) {
                fkAddid.getCustomerCollection().remove(customer);
                fkAddid = em.merge(fkAddid);
            }
            Account fkAccid = customer.getFkAccid();
            if (fkAccid != null) {
                fkAccid.getCustomerCollection().remove(customer);
                fkAccid = em.merge(fkAccid);
            }
            em.remove(customer);
            utx.commit();
        } catch (Exception ex) {
            try {
                utx.rollback();
            } catch (Exception re) {
                throw new RollbackFailureException("An error occurred attempting to roll back the transaction.", re);
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public List<Customer> findCustomerEntities() {
        return findCustomerEntities(true, -1, -1);
    }

    public List<Customer> findCustomerEntities(int maxResults, int firstResult) {
        return findCustomerEntities(false, maxResults, firstResult);
    }

    private List<Customer> findCustomerEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Customer.class));
            Query q = em.createQuery(cq);
            if (!all) {
                q.setMaxResults(maxResults);
                q.setFirstResult(firstResult);
            }
            return q.getResultList();
        } finally {
            em.close();
        }
    }

    public Customer findCustomer(Integer id) {
        try {
            return em.find(Customer.class, id);
        } finally {
            em.close();
        }
    }

    public int getCustomerCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Customer> rt = cq.from(Customer.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
