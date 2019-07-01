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
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import model.Role;
import utilities.exceptions.NonexistentEntityException;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@SessionScoped
public class RoleJpaController implements Serializable {

    public RoleJpaController() {
    }
    
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Role role) throws RollbackFailureException, Exception {
        if (role.getAddressCollection() == null) {
            role.setAddressCollection(new ArrayList<Address>());
        }
        try {
            utx.begin();
            Collection<Address> attachedAddressCollection = new ArrayList<Address>();
            for (Address addressCollectionAddressToAttach : role.getAddressCollection()) {
                addressCollectionAddressToAttach = em.getReference(addressCollectionAddressToAttach.getClass(), addressCollectionAddressToAttach.getId());
                attachedAddressCollection.add(addressCollectionAddressToAttach);
            }
            role.setAddressCollection(attachedAddressCollection);
            em.persist(role);
            for (Address addressCollectionAddress : role.getAddressCollection()) {
                addressCollectionAddress.getRoleCollection().add(role);
                addressCollectionAddress = em.merge(addressCollectionAddress);
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

    public void edit(Role role) throws NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Role persistentRole = em.find(Role.class, role.getId());
            Collection<Address> addressCollectionOld = persistentRole.getAddressCollection();
            Collection<Address> addressCollectionNew = role.getAddressCollection();
            Collection<Address> attachedAddressCollectionNew = new ArrayList<Address>();
            for (Address addressCollectionNewAddressToAttach : addressCollectionNew) {
                addressCollectionNewAddressToAttach = em.getReference(addressCollectionNewAddressToAttach.getClass(), addressCollectionNewAddressToAttach.getId());
                attachedAddressCollectionNew.add(addressCollectionNewAddressToAttach);
            }
            addressCollectionNew = attachedAddressCollectionNew;
            role.setAddressCollection(addressCollectionNew);
            role = em.merge(role);
            for (Address addressCollectionOldAddress : addressCollectionOld) {
                if (!addressCollectionNew.contains(addressCollectionOldAddress)) {
                    addressCollectionOldAddress.getRoleCollection().remove(role);
                    addressCollectionOldAddress = em.merge(addressCollectionOldAddress);
                }
            }
            for (Address addressCollectionNewAddress : addressCollectionNew) {
                if (!addressCollectionOld.contains(addressCollectionNewAddress)) {
                    addressCollectionNewAddress.getRoleCollection().add(role);
                    addressCollectionNewAddress = em.merge(addressCollectionNewAddress);
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
                Integer id = role.getId();
                if (findRole(id) == null) {
                    throw new NonexistentEntityException("The role with id " + id + " no longer exists.");
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
            Role role;
            try {
                role = em.getReference(Role.class, id);
                role.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The role with id " + id + " no longer exists.", enfe);
            }
            Collection<Address> addressCollection = role.getAddressCollection();
            for (Address addressCollectionAddress : addressCollection) {
                addressCollectionAddress.getRoleCollection().remove(role);
                addressCollectionAddress = em.merge(addressCollectionAddress);
            }
            em.remove(role);
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

    public List<Role> findRoleEntities() {
        return findRoleEntities(true, -1, -1);
    }

    public List<Role> findRoleEntities(int maxResults, int firstResult) {
        return findRoleEntities(false, maxResults, firstResult);
    }

    private List<Role> findRoleEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Role.class));
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

    public Role findRole(Integer id) {
        try {
            return em.find(Role.class, id);
        } finally {
            em.close();
        }
    }

    public int getRoleCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Role> rt = cq.from(Role.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
