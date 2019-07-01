/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utilities;

import java.io.Serializable;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Query;
import javax.persistence.EntityNotFoundException;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import model.Order1;
import model.Orderdetail;
import model.Product;
import utilities.exceptions.NonexistentEntityException;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@SessionScoped
public class OrderdetailJpaController implements Serializable {

    public OrderdetailJpaController() {
    }
    
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Orderdetail orderdetail) throws RollbackFailureException, Exception {
        try {
            utx.begin();
            Order1 fkOid = orderdetail.getFkOid();
            if (fkOid != null) {
                fkOid = em.getReference(fkOid.getClass(), fkOid.getId());
                orderdetail.setFkOid(fkOid);
            }
            Product fkPid = orderdetail.getFkPid();
            if (fkPid != null) {
                fkPid = em.getReference(fkPid.getClass(), fkPid.getId());
                orderdetail.setFkPid(fkPid);
            }
            em.persist(orderdetail);
            if (fkOid != null) {
                fkOid.getOrderdetailCollection().add(orderdetail);
                fkOid = em.merge(fkOid);
            }
            if (fkPid != null) {
                fkPid.getOrderdetailCollection().add(orderdetail);
                fkPid = em.merge(fkPid);
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

    public void edit(Orderdetail orderdetail) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Orderdetail persistentOrderdetail = em.find(Orderdetail.class, orderdetail.getId());
            Order1 fkOidOld = persistentOrderdetail.getFkOid();
            Order1 fkOidNew = orderdetail.getFkOid();
            Product fkPidOld = persistentOrderdetail.getFkPid();
            Product fkPidNew = orderdetail.getFkPid();
            if (fkOidNew != null) {
                fkOidNew = em.getReference(fkOidNew.getClass(), fkOidNew.getId());
                orderdetail.setFkOid(fkOidNew);
            }
            if (fkPidNew != null) {
                fkPidNew = em.getReference(fkPidNew.getClass(), fkPidNew.getId());
                orderdetail.setFkPid(fkPidNew);
            }
            orderdetail = em.merge(orderdetail);
            if (fkOidOld != null && !fkOidOld.equals(fkOidNew)) {
                fkOidOld.getOrderdetailCollection().remove(orderdetail);
                fkOidOld = em.merge(fkOidOld);
            }
            if (fkOidNew != null && !fkOidNew.equals(fkOidOld)) {
                fkOidNew.getOrderdetailCollection().add(orderdetail);
                fkOidNew = em.merge(fkOidNew);
            }
            if (fkPidOld != null && !fkPidOld.equals(fkPidNew)) {
                fkPidOld.getOrderdetailCollection().remove(orderdetail);
                fkPidOld = em.merge(fkPidOld);
            }
            if (fkPidNew != null && !fkPidNew.equals(fkPidOld)) {
                fkPidNew.getOrderdetailCollection().add(orderdetail);
                fkPidNew = em.merge(fkPidNew);
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
                Integer id = orderdetail.getId();
                if (findOrderdetail(id) == null) {
                    throw new NonexistentEntityException("The orderdetail with id " + id + " no longer exists.");
                }
            }
            throw ex;
        } finally {
            if (em != null) {
                em.close();
            }
        }
    }

    public void destroy(Integer id) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Orderdetail orderdetail;
            try {
                orderdetail = em.getReference(Orderdetail.class, id);
                orderdetail.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The orderdetail with id " + id + " no longer exists.", enfe);
            }
            Order1 fkOid = orderdetail.getFkOid();
            if (fkOid != null) {
                fkOid.getOrderdetailCollection().remove(orderdetail);
                fkOid = em.merge(fkOid);
            }
            Product fkPid = orderdetail.getFkPid();
            if (fkPid != null) {
                fkPid.getOrderdetailCollection().remove(orderdetail);
                fkPid = em.merge(fkPid);
            }
            em.remove(orderdetail);
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

    public List<Orderdetail> findOrderdetailEntities() {
        return findOrderdetailEntities(true, -1, -1);
    }

    public List<Orderdetail> findOrderdetailEntities(int maxResults, int firstResult) {
        return findOrderdetailEntities(false, maxResults, firstResult);
    }

    private List<Orderdetail> findOrderdetailEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Orderdetail.class));
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

    public Orderdetail findOrderdetail(Integer id) {
        try {
            return em.find(Orderdetail.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrderdetailCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Orderdetail> rt = cq.from(Orderdetail.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
