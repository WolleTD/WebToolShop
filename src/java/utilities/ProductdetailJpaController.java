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
import model.Product;
import model.Productdetail;
import utilities.exceptions.NonexistentEntityException;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@SessionScoped
public class ProductdetailJpaController implements Serializable {

    public ProductdetailJpaController() {
    }
    
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Productdetail productdetail) throws RollbackFailureException, Exception {
        try {
            utx.begin();
            Product fkPid = productdetail.getFkPid();
            if (fkPid != null) {
                fkPid = em.getReference(fkPid.getClass(), fkPid.getId());
                productdetail.setFkPid(fkPid);
            }
            em.persist(productdetail);
            if (fkPid != null) {
                fkPid.getProductdetailCollection().add(productdetail);
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

    public void edit(Productdetail productdetail) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Productdetail persistentProductdetail = em.find(Productdetail.class, productdetail.getId());
            Product fkPidOld = persistentProductdetail.getFkPid();
            Product fkPidNew = productdetail.getFkPid();
            if (fkPidNew != null) {
                fkPidNew = em.getReference(fkPidNew.getClass(), fkPidNew.getId());
                productdetail.setFkPid(fkPidNew);
            }
            productdetail = em.merge(productdetail);
            if (fkPidOld != null && !fkPidOld.equals(fkPidNew)) {
                fkPidOld.getProductdetailCollection().remove(productdetail);
                fkPidOld = em.merge(fkPidOld);
            }
            if (fkPidNew != null && !fkPidNew.equals(fkPidOld)) {
                fkPidNew.getProductdetailCollection().add(productdetail);
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
                Integer id = productdetail.getId();
                if (findProductdetail(id) == null) {
                    throw new NonexistentEntityException("The productdetail with id " + id + " no longer exists.");
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
            Productdetail productdetail;
            try {
                productdetail = em.getReference(Productdetail.class, id);
                productdetail.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The productdetail with id " + id + " no longer exists.", enfe);
            }
            Product fkPid = productdetail.getFkPid();
            if (fkPid != null) {
                fkPid.getProductdetailCollection().remove(productdetail);
                fkPid = em.merge(fkPid);
            }
            em.remove(productdetail);
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

    public List<Productdetail> findProductdetailEntities() {
        return findProductdetailEntities(true, -1, -1);
    }

    public List<Productdetail> findProductdetailEntities(int maxResults, int firstResult) {
        return findProductdetailEntities(false, maxResults, firstResult);
    }

    private List<Productdetail> findProductdetailEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Productdetail.class));
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

    public Productdetail findProductdetail(Integer id) {
        try {
            return em.find(Productdetail.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductdetailCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Productdetail> rt = cq.from(Productdetail.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
