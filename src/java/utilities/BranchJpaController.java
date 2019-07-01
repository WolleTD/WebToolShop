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
import model.Branch;
import utilities.exceptions.NonexistentEntityException;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@SessionScoped
public class BranchJpaController implements Serializable {

    public BranchJpaController() {
    }
    
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;
    
    public void create(Branch branch) throws RollbackFailureException, Exception {
        if (branch.getOrder1Collection() == null) {
            branch.setOrder1Collection(new ArrayList<Order1>());
        }
        try {
            utx.begin();
            Address fkAddid = branch.getFkAddid();
            if (fkAddid != null) {
                fkAddid = em.getReference(fkAddid.getClass(), fkAddid.getId());
                branch.setFkAddid(fkAddid);
            }
            Collection<Order1> attachedOrder1Collection = new ArrayList<Order1>();
            for (Order1 order1CollectionOrder1ToAttach : branch.getOrder1Collection()) {
                order1CollectionOrder1ToAttach = em.getReference(order1CollectionOrder1ToAttach.getClass(), order1CollectionOrder1ToAttach.getId());
                attachedOrder1Collection.add(order1CollectionOrder1ToAttach);
            }
            branch.setOrder1Collection(attachedOrder1Collection);
            em.persist(branch);
            if (fkAddid != null) {
                fkAddid.getBranchCollection().add(branch);
                fkAddid = em.merge(fkAddid);
            }
            for (Order1 order1CollectionOrder1 : branch.getOrder1Collection()) {
                Branch oldFKBranchIDOfOrder1CollectionOrder1 = order1CollectionOrder1.getFKBranchID();
                order1CollectionOrder1.setFKBranchID(branch);
                order1CollectionOrder1 = em.merge(order1CollectionOrder1);
                if (oldFKBranchIDOfOrder1CollectionOrder1 != null) {
                    oldFKBranchIDOfOrder1CollectionOrder1.getOrder1Collection().remove(order1CollectionOrder1);
                    oldFKBranchIDOfOrder1CollectionOrder1 = em.merge(oldFKBranchIDOfOrder1CollectionOrder1);
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

    public void edit(Branch branch) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Branch persistentBranch = em.find(Branch.class, branch.getId());
            Address fkAddidOld = persistentBranch.getFkAddid();
            Address fkAddidNew = branch.getFkAddid();
            Collection<Order1> order1CollectionOld = persistentBranch.getOrder1Collection();
            Collection<Order1> order1CollectionNew = branch.getOrder1Collection();
            if (fkAddidNew != null) {
                fkAddidNew = em.getReference(fkAddidNew.getClass(), fkAddidNew.getId());
                branch.setFkAddid(fkAddidNew);
            }
            Collection<Order1> attachedOrder1CollectionNew = new ArrayList<Order1>();
            for (Order1 order1CollectionNewOrder1ToAttach : order1CollectionNew) {
                order1CollectionNewOrder1ToAttach = em.getReference(order1CollectionNewOrder1ToAttach.getClass(), order1CollectionNewOrder1ToAttach.getId());
                attachedOrder1CollectionNew.add(order1CollectionNewOrder1ToAttach);
            }
            order1CollectionNew = attachedOrder1CollectionNew;
            branch.setOrder1Collection(order1CollectionNew);
            branch = em.merge(branch);
            if (fkAddidOld != null && !fkAddidOld.equals(fkAddidNew)) {
                fkAddidOld.getBranchCollection().remove(branch);
                fkAddidOld = em.merge(fkAddidOld);
            }
            if (fkAddidNew != null && !fkAddidNew.equals(fkAddidOld)) {
                fkAddidNew.getBranchCollection().add(branch);
                fkAddidNew = em.merge(fkAddidNew);
            }
            for (Order1 order1CollectionOldOrder1 : order1CollectionOld) {
                if (!order1CollectionNew.contains(order1CollectionOldOrder1)) {
                    order1CollectionOldOrder1.setFKBranchID(null);
                    order1CollectionOldOrder1 = em.merge(order1CollectionOldOrder1);
                }
            }
            for (Order1 order1CollectionNewOrder1 : order1CollectionNew) {
                if (!order1CollectionOld.contains(order1CollectionNewOrder1)) {
                    Branch oldFKBranchIDOfOrder1CollectionNewOrder1 = order1CollectionNewOrder1.getFKBranchID();
                    order1CollectionNewOrder1.setFKBranchID(branch);
                    order1CollectionNewOrder1 = em.merge(order1CollectionNewOrder1);
                    if (oldFKBranchIDOfOrder1CollectionNewOrder1 != null && !oldFKBranchIDOfOrder1CollectionNewOrder1.equals(branch)) {
                        oldFKBranchIDOfOrder1CollectionNewOrder1.getOrder1Collection().remove(order1CollectionNewOrder1);
                        oldFKBranchIDOfOrder1CollectionNewOrder1 = em.merge(oldFKBranchIDOfOrder1CollectionNewOrder1);
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
                Integer id = branch.getId();
                if (findBranch(id) == null) {
                    throw new NonexistentEntityException("The branch with id " + id + " no longer exists.");
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
            Branch branch;
            try {
                branch = em.getReference(Branch.class, id);
                branch.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The branch with id " + id + " no longer exists.", enfe);
            }
            Address fkAddid = branch.getFkAddid();
            if (fkAddid != null) {
                fkAddid.getBranchCollection().remove(branch);
                fkAddid = em.merge(fkAddid);
            }
            Collection<Order1> order1Collection = branch.getOrder1Collection();
            for (Order1 order1CollectionOrder1 : order1Collection) {
                order1CollectionOrder1.setFKBranchID(null);
                order1CollectionOrder1 = em.merge(order1CollectionOrder1);
            }
            em.remove(branch);
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

    public List<Branch> findBranchEntities() {
        return findBranchEntities(true, -1, -1);
    }

    public List<Branch> findBranchEntities(int maxResults, int firstResult) {
        return findBranchEntities(false, maxResults, firstResult);
    }

    private List<Branch> findBranchEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Branch.class));
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

    public Branch findBranch(Integer id) {
        try {
            return em.find(Branch.class, id);
        } finally {
            em.close();
        }
    }

    public int getBranchCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Branch> rt = cq.from(Branch.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
