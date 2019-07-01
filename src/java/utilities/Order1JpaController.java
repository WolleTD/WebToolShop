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
import model.Customer;
import model.Branch;
import model.Orderdetail;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import model.Order1;
import utilities.exceptions.IllegalOrphanException;
import utilities.exceptions.NonexistentEntityException;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@SessionScoped
public class Order1JpaController implements Serializable {

    public Order1JpaController() {
    }
    
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Order1 order1) throws RollbackFailureException, Exception {
        if (order1.getOrderdetailCollection() == null) {
            order1.setOrderdetailCollection(new ArrayList<Orderdetail>());
        }
        try {
            utx.begin();
            Customer fkCid = order1.getFkCid();
            if (fkCid != null) {
                fkCid = em.getReference(fkCid.getClass(), fkCid.getId());
                order1.setFkCid(fkCid);
            }
            Branch FKBranchID = order1.getFKBranchID();
            if (FKBranchID != null) {
                FKBranchID = em.getReference(FKBranchID.getClass(), FKBranchID.getId());
                order1.setFKBranchID(FKBranchID);
            }
            Collection<Orderdetail> attachedOrderdetailCollection = new ArrayList<Orderdetail>();
            for (Orderdetail orderdetailCollectionOrderdetailToAttach : order1.getOrderdetailCollection()) {
                orderdetailCollectionOrderdetailToAttach = em.getReference(orderdetailCollectionOrderdetailToAttach.getClass(), orderdetailCollectionOrderdetailToAttach.getId());
                attachedOrderdetailCollection.add(orderdetailCollectionOrderdetailToAttach);
            }
            order1.setOrderdetailCollection(attachedOrderdetailCollection);
            em.persist(order1);
            if (fkCid != null) {
                fkCid.getOrder1Collection().add(order1);
                fkCid = em.merge(fkCid);
            }
            if (FKBranchID != null) {
                FKBranchID.getOrder1Collection().add(order1);
                FKBranchID = em.merge(FKBranchID);
            }
            for (Orderdetail orderdetailCollectionOrderdetail : order1.getOrderdetailCollection()) {
                Order1 oldFkOidOfOrderdetailCollectionOrderdetail = orderdetailCollectionOrderdetail.getFkOid();
                orderdetailCollectionOrderdetail.setFkOid(order1);
                orderdetailCollectionOrderdetail = em.merge(orderdetailCollectionOrderdetail);
                if (oldFkOidOfOrderdetailCollectionOrderdetail != null) {
                    oldFkOidOfOrderdetailCollectionOrderdetail.getOrderdetailCollection().remove(orderdetailCollectionOrderdetail);
                    oldFkOidOfOrderdetailCollectionOrderdetail = em.merge(oldFkOidOfOrderdetailCollectionOrderdetail);
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

    public void edit(Order1 order1) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Order1 persistentOrder1 = em.find(Order1.class, order1.getId());
            Customer fkCidOld = persistentOrder1.getFkCid();
            Customer fkCidNew = order1.getFkCid();
            Branch FKBranchIDOld = persistentOrder1.getFKBranchID();
            Branch FKBranchIDNew = order1.getFKBranchID();
            Collection<Orderdetail> orderdetailCollectionOld = persistentOrder1.getOrderdetailCollection();
            Collection<Orderdetail> orderdetailCollectionNew = order1.getOrderdetailCollection();
            List<String> illegalOrphanMessages = null;
            for (Orderdetail orderdetailCollectionOldOrderdetail : orderdetailCollectionOld) {
                if (!orderdetailCollectionNew.contains(orderdetailCollectionOldOrderdetail)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orderdetail " + orderdetailCollectionOldOrderdetail + " since its fkOid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            if (fkCidNew != null) {
                fkCidNew = em.getReference(fkCidNew.getClass(), fkCidNew.getId());
                order1.setFkCid(fkCidNew);
            }
            if (FKBranchIDNew != null) {
                FKBranchIDNew = em.getReference(FKBranchIDNew.getClass(), FKBranchIDNew.getId());
                order1.setFKBranchID(FKBranchIDNew);
            }
            Collection<Orderdetail> attachedOrderdetailCollectionNew = new ArrayList<Orderdetail>();
            for (Orderdetail orderdetailCollectionNewOrderdetailToAttach : orderdetailCollectionNew) {
                orderdetailCollectionNewOrderdetailToAttach = em.getReference(orderdetailCollectionNewOrderdetailToAttach.getClass(), orderdetailCollectionNewOrderdetailToAttach.getId());
                attachedOrderdetailCollectionNew.add(orderdetailCollectionNewOrderdetailToAttach);
            }
            orderdetailCollectionNew = attachedOrderdetailCollectionNew;
            order1.setOrderdetailCollection(orderdetailCollectionNew);
            order1 = em.merge(order1);
            if (fkCidOld != null && !fkCidOld.equals(fkCidNew)) {
                fkCidOld.getOrder1Collection().remove(order1);
                fkCidOld = em.merge(fkCidOld);
            }
            if (fkCidNew != null && !fkCidNew.equals(fkCidOld)) {
                fkCidNew.getOrder1Collection().add(order1);
                fkCidNew = em.merge(fkCidNew);
            }
            if (FKBranchIDOld != null && !FKBranchIDOld.equals(FKBranchIDNew)) {
                FKBranchIDOld.getOrder1Collection().remove(order1);
                FKBranchIDOld = em.merge(FKBranchIDOld);
            }
            if (FKBranchIDNew != null && !FKBranchIDNew.equals(FKBranchIDOld)) {
                FKBranchIDNew.getOrder1Collection().add(order1);
                FKBranchIDNew = em.merge(FKBranchIDNew);
            }
            for (Orderdetail orderdetailCollectionNewOrderdetail : orderdetailCollectionNew) {
                if (!orderdetailCollectionOld.contains(orderdetailCollectionNewOrderdetail)) {
                    Order1 oldFkOidOfOrderdetailCollectionNewOrderdetail = orderdetailCollectionNewOrderdetail.getFkOid();
                    orderdetailCollectionNewOrderdetail.setFkOid(order1);
                    orderdetailCollectionNewOrderdetail = em.merge(orderdetailCollectionNewOrderdetail);
                    if (oldFkOidOfOrderdetailCollectionNewOrderdetail != null && !oldFkOidOfOrderdetailCollectionNewOrderdetail.equals(order1)) {
                        oldFkOidOfOrderdetailCollectionNewOrderdetail.getOrderdetailCollection().remove(orderdetailCollectionNewOrderdetail);
                        oldFkOidOfOrderdetailCollectionNewOrderdetail = em.merge(oldFkOidOfOrderdetailCollectionNewOrderdetail);
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
                Integer id = order1.getId();
                if (findOrder1(id) == null) {
                    throw new NonexistentEntityException("The order1 with id " + id + " no longer exists.");
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
            Order1 order1;
            try {
                order1 = em.getReference(Order1.class, id);
                order1.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The order1 with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Orderdetail> orderdetailCollectionOrphanCheck = order1.getOrderdetailCollection();
            for (Orderdetail orderdetailCollectionOrphanCheckOrderdetail : orderdetailCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Order1 (" + order1 + ") cannot be destroyed since the Orderdetail " + orderdetailCollectionOrphanCheckOrderdetail + " in its orderdetailCollection field has a non-nullable fkOid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Customer fkCid = order1.getFkCid();
            if (fkCid != null) {
                fkCid.getOrder1Collection().remove(order1);
                fkCid = em.merge(fkCid);
            }
            Branch FKBranchID = order1.getFKBranchID();
            if (FKBranchID != null) {
                FKBranchID.getOrder1Collection().remove(order1);
                FKBranchID = em.merge(FKBranchID);
            }
            em.remove(order1);
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

    public List<Order1> findOrder1Entities() {
        return findOrder1Entities(true, -1, -1);
    }

    public List<Order1> findOrder1Entities(int maxResults, int firstResult) {
        return findOrder1Entities(false, maxResults, firstResult);
    }

    private List<Order1> findOrder1Entities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Order1.class));
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

    public Order1 findOrder1(Integer id) {
        try {
            return em.find(Order1.class, id);
        } finally {
            em.close();
        }
    }

    public int getOrder1Count() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Order1> rt = cq.from(Order1.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
