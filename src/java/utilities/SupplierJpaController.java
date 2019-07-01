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
import model.Address;
import model.Supplier;
import utilities.exceptions.NonexistentEntityException;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@SessionScoped
public class SupplierJpaController implements Serializable {

    public SupplierJpaController() {
    }
    
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Supplier supplier) throws RollbackFailureException, Exception {
        try {
            utx.begin();
            Address fkAddid = supplier.getFkAddid();
            if (fkAddid != null) {
                fkAddid = em.getReference(fkAddid.getClass(), fkAddid.getId());
                supplier.setFkAddid(fkAddid);
            }
            em.persist(supplier);
            if (fkAddid != null) {
                fkAddid.getSupplierCollection().add(supplier);
                fkAddid = em.merge(fkAddid);
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

    public void edit(Supplier supplier) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Supplier persistentSupplier = em.find(Supplier.class, supplier.getId());
            Address fkAddidOld = persistentSupplier.getFkAddid();
            Address fkAddidNew = supplier.getFkAddid();
            if (fkAddidNew != null) {
                fkAddidNew = em.getReference(fkAddidNew.getClass(), fkAddidNew.getId());
                supplier.setFkAddid(fkAddidNew);
            }
            supplier = em.merge(supplier);
            if (fkAddidOld != null && !fkAddidOld.equals(fkAddidNew)) {
                fkAddidOld.getSupplierCollection().remove(supplier);
                fkAddidOld = em.merge(fkAddidOld);
            }
            if (fkAddidNew != null && !fkAddidNew.equals(fkAddidOld)) {
                fkAddidNew.getSupplierCollection().add(supplier);
                fkAddidNew = em.merge(fkAddidNew);
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
                Integer id = supplier.getId();
                if (findSupplier(id) == null) {
                    throw new NonexistentEntityException("The supplier with id " + id + " no longer exists.");
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
            Supplier supplier;
            try {
                supplier = em.getReference(Supplier.class, id);
                supplier.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The supplier with id " + id + " no longer exists.", enfe);
            }
            Address fkAddid = supplier.getFkAddid();
            if (fkAddid != null) {
                fkAddid.getSupplierCollection().remove(supplier);
                fkAddid = em.merge(fkAddid);
            }
            em.remove(supplier);
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

    public List<Supplier> findSupplierEntities() {
        return findSupplierEntities(true, -1, -1);
    }

    public List<Supplier> findSupplierEntities(int maxResults, int firstResult) {
        return findSupplierEntities(false, maxResults, firstResult);
    }

    private List<Supplier> findSupplierEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Supplier.class));
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

    public Supplier findSupplier(Integer id) {
        try {
            return em.find(Supplier.class, id);
        } finally {
            em.close();
        }
    }

    public int getSupplierCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Supplier> rt = cq.from(Supplier.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
