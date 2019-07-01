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
import model.Role;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import model.Address;
import model.Supplier;
import model.Branch;
import model.Customer;
import utilities.exceptions.IllegalOrphanException;
import utilities.exceptions.NonexistentEntityException;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@SessionScoped
public class AddressJpaController implements Serializable {
    
    public AddressJpaController() {
    }
    
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Address address) throws RollbackFailureException, Exception {
        if (address.getRoleCollection() == null) {
            address.setRoleCollection(new ArrayList<Role>());
        }
        if (address.getSupplierCollection() == null) {
            address.setSupplierCollection(new ArrayList<Supplier>());
        }
        if (address.getBranchCollection() == null) {
            address.setBranchCollection(new ArrayList<Branch>());
        }
        if (address.getCustomerCollection() == null) {
            address.setCustomerCollection(new ArrayList<Customer>());
        }
        try {
            utx.begin();
            Collection<Role> attachedRoleCollection = new ArrayList<Role>();
            for (Role roleCollectionRoleToAttach : address.getRoleCollection()) {
                roleCollectionRoleToAttach = em.getReference(roleCollectionRoleToAttach.getClass(), roleCollectionRoleToAttach.getId());
                attachedRoleCollection.add(roleCollectionRoleToAttach);
            }
            address.setRoleCollection(attachedRoleCollection);
            Collection<Supplier> attachedSupplierCollection = new ArrayList<Supplier>();
            for (Supplier supplierCollectionSupplierToAttach : address.getSupplierCollection()) {
                supplierCollectionSupplierToAttach = em.getReference(supplierCollectionSupplierToAttach.getClass(), supplierCollectionSupplierToAttach.getId());
                attachedSupplierCollection.add(supplierCollectionSupplierToAttach);
            }
            address.setSupplierCollection(attachedSupplierCollection);
            Collection<Branch> attachedBranchCollection = new ArrayList<Branch>();
            for (Branch branchCollectionBranchToAttach : address.getBranchCollection()) {
                branchCollectionBranchToAttach = em.getReference(branchCollectionBranchToAttach.getClass(), branchCollectionBranchToAttach.getId());
                attachedBranchCollection.add(branchCollectionBranchToAttach);
            }
            address.setBranchCollection(attachedBranchCollection);
            Collection<Customer> attachedCustomerCollection = new ArrayList<Customer>();
            for (Customer customerCollectionCustomerToAttach : address.getCustomerCollection()) {
                customerCollectionCustomerToAttach = em.getReference(customerCollectionCustomerToAttach.getClass(), customerCollectionCustomerToAttach.getId());
                attachedCustomerCollection.add(customerCollectionCustomerToAttach);
            }
            address.setCustomerCollection(attachedCustomerCollection);
            em.persist(address);
            for (Role roleCollectionRole : address.getRoleCollection()) {
                roleCollectionRole.getAddressCollection().add(address);
                roleCollectionRole = em.merge(roleCollectionRole);
            }
            for (Supplier supplierCollectionSupplier : address.getSupplierCollection()) {
                Address oldFkAddidOfSupplierCollectionSupplier = supplierCollectionSupplier.getFkAddid();
                supplierCollectionSupplier.setFkAddid(address);
                supplierCollectionSupplier = em.merge(supplierCollectionSupplier);
                if (oldFkAddidOfSupplierCollectionSupplier != null) {
                    oldFkAddidOfSupplierCollectionSupplier.getSupplierCollection().remove(supplierCollectionSupplier);
                    oldFkAddidOfSupplierCollectionSupplier = em.merge(oldFkAddidOfSupplierCollectionSupplier);
                }
            }
            for (Branch branchCollectionBranch : address.getBranchCollection()) {
                Address oldFkAddidOfBranchCollectionBranch = branchCollectionBranch.getFkAddid();
                branchCollectionBranch.setFkAddid(address);
                branchCollectionBranch = em.merge(branchCollectionBranch);
                if (oldFkAddidOfBranchCollectionBranch != null) {
                    oldFkAddidOfBranchCollectionBranch.getBranchCollection().remove(branchCollectionBranch);
                    oldFkAddidOfBranchCollectionBranch = em.merge(oldFkAddidOfBranchCollectionBranch);
                }
            }
            for (Customer customerCollectionCustomer : address.getCustomerCollection()) {
                Address oldFkAddidOfCustomerCollectionCustomer = customerCollectionCustomer.getFkAddid();
                customerCollectionCustomer.setFkAddid(address);
                customerCollectionCustomer = em.merge(customerCollectionCustomer);
                if (oldFkAddidOfCustomerCollectionCustomer != null) {
                    oldFkAddidOfCustomerCollectionCustomer.getCustomerCollection().remove(customerCollectionCustomer);
                    oldFkAddidOfCustomerCollectionCustomer = em.merge(oldFkAddidOfCustomerCollectionCustomer);
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

    public void edit(Address address) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Address persistentAddress = em.find(Address.class, address.getId());
            Collection<Role> roleCollectionOld = persistentAddress.getRoleCollection();
            Collection<Role> roleCollectionNew = address.getRoleCollection();
            Collection<Supplier> supplierCollectionOld = persistentAddress.getSupplierCollection();
            Collection<Supplier> supplierCollectionNew = address.getSupplierCollection();
            Collection<Branch> branchCollectionOld = persistentAddress.getBranchCollection();
            Collection<Branch> branchCollectionNew = address.getBranchCollection();
            Collection<Customer> customerCollectionOld = persistentAddress.getCustomerCollection();
            Collection<Customer> customerCollectionNew = address.getCustomerCollection();
            List<String> illegalOrphanMessages = null;
            for (Supplier supplierCollectionOldSupplier : supplierCollectionOld) {
                if (!supplierCollectionNew.contains(supplierCollectionOldSupplier)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Supplier " + supplierCollectionOldSupplier + " since its fkAddid field is not nullable.");
                }
            }
            for (Branch branchCollectionOldBranch : branchCollectionOld) {
                if (!branchCollectionNew.contains(branchCollectionOldBranch)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Branch " + branchCollectionOldBranch + " since its fkAddid field is not nullable.");
                }
            }
            for (Customer customerCollectionOldCustomer : customerCollectionOld) {
                if (!customerCollectionNew.contains(customerCollectionOldCustomer)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Customer " + customerCollectionOldCustomer + " since its fkAddid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Role> attachedRoleCollectionNew = new ArrayList<Role>();
            for (Role roleCollectionNewRoleToAttach : roleCollectionNew) {
                roleCollectionNewRoleToAttach = em.getReference(roleCollectionNewRoleToAttach.getClass(), roleCollectionNewRoleToAttach.getId());
                attachedRoleCollectionNew.add(roleCollectionNewRoleToAttach);
            }
            roleCollectionNew = attachedRoleCollectionNew;
            address.setRoleCollection(roleCollectionNew);
            Collection<Supplier> attachedSupplierCollectionNew = new ArrayList<Supplier>();
            for (Supplier supplierCollectionNewSupplierToAttach : supplierCollectionNew) {
                supplierCollectionNewSupplierToAttach = em.getReference(supplierCollectionNewSupplierToAttach.getClass(), supplierCollectionNewSupplierToAttach.getId());
                attachedSupplierCollectionNew.add(supplierCollectionNewSupplierToAttach);
            }
            supplierCollectionNew = attachedSupplierCollectionNew;
            address.setSupplierCollection(supplierCollectionNew);
            Collection<Branch> attachedBranchCollectionNew = new ArrayList<Branch>();
            for (Branch branchCollectionNewBranchToAttach : branchCollectionNew) {
                branchCollectionNewBranchToAttach = em.getReference(branchCollectionNewBranchToAttach.getClass(), branchCollectionNewBranchToAttach.getId());
                attachedBranchCollectionNew.add(branchCollectionNewBranchToAttach);
            }
            branchCollectionNew = attachedBranchCollectionNew;
            address.setBranchCollection(branchCollectionNew);
            Collection<Customer> attachedCustomerCollectionNew = new ArrayList<Customer>();
            for (Customer customerCollectionNewCustomerToAttach : customerCollectionNew) {
                customerCollectionNewCustomerToAttach = em.getReference(customerCollectionNewCustomerToAttach.getClass(), customerCollectionNewCustomerToAttach.getId());
                attachedCustomerCollectionNew.add(customerCollectionNewCustomerToAttach);
            }
            customerCollectionNew = attachedCustomerCollectionNew;
            address.setCustomerCollection(customerCollectionNew);
            address = em.merge(address);
            for (Role roleCollectionOldRole : roleCollectionOld) {
                if (!roleCollectionNew.contains(roleCollectionOldRole)) {
                    roleCollectionOldRole.getAddressCollection().remove(address);
                    roleCollectionOldRole = em.merge(roleCollectionOldRole);
                }
            }
            for (Role roleCollectionNewRole : roleCollectionNew) {
                if (!roleCollectionOld.contains(roleCollectionNewRole)) {
                    roleCollectionNewRole.getAddressCollection().add(address);
                    roleCollectionNewRole = em.merge(roleCollectionNewRole);
                }
            }
            for (Supplier supplierCollectionNewSupplier : supplierCollectionNew) {
                if (!supplierCollectionOld.contains(supplierCollectionNewSupplier)) {
                    Address oldFkAddidOfSupplierCollectionNewSupplier = supplierCollectionNewSupplier.getFkAddid();
                    supplierCollectionNewSupplier.setFkAddid(address);
                    supplierCollectionNewSupplier = em.merge(supplierCollectionNewSupplier);
                    if (oldFkAddidOfSupplierCollectionNewSupplier != null && !oldFkAddidOfSupplierCollectionNewSupplier.equals(address)) {
                        oldFkAddidOfSupplierCollectionNewSupplier.getSupplierCollection().remove(supplierCollectionNewSupplier);
                        oldFkAddidOfSupplierCollectionNewSupplier = em.merge(oldFkAddidOfSupplierCollectionNewSupplier);
                    }
                }
            }
            for (Branch branchCollectionNewBranch : branchCollectionNew) {
                if (!branchCollectionOld.contains(branchCollectionNewBranch)) {
                    Address oldFkAddidOfBranchCollectionNewBranch = branchCollectionNewBranch.getFkAddid();
                    branchCollectionNewBranch.setFkAddid(address);
                    branchCollectionNewBranch = em.merge(branchCollectionNewBranch);
                    if (oldFkAddidOfBranchCollectionNewBranch != null && !oldFkAddidOfBranchCollectionNewBranch.equals(address)) {
                        oldFkAddidOfBranchCollectionNewBranch.getBranchCollection().remove(branchCollectionNewBranch);
                        oldFkAddidOfBranchCollectionNewBranch = em.merge(oldFkAddidOfBranchCollectionNewBranch);
                    }
                }
            }
            for (Customer customerCollectionNewCustomer : customerCollectionNew) {
                if (!customerCollectionOld.contains(customerCollectionNewCustomer)) {
                    Address oldFkAddidOfCustomerCollectionNewCustomer = customerCollectionNewCustomer.getFkAddid();
                    customerCollectionNewCustomer.setFkAddid(address);
                    customerCollectionNewCustomer = em.merge(customerCollectionNewCustomer);
                    if (oldFkAddidOfCustomerCollectionNewCustomer != null && !oldFkAddidOfCustomerCollectionNewCustomer.equals(address)) {
                        oldFkAddidOfCustomerCollectionNewCustomer.getCustomerCollection().remove(customerCollectionNewCustomer);
                        oldFkAddidOfCustomerCollectionNewCustomer = em.merge(oldFkAddidOfCustomerCollectionNewCustomer);
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
                Integer id = address.getId();
                if (findAddress(id) == null) {
                    throw new NonexistentEntityException("The address with id " + id + " no longer exists.");
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
            Address address;
            try {
                address = em.getReference(Address.class, id);
                address.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The address with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Supplier> supplierCollectionOrphanCheck = address.getSupplierCollection();
            for (Supplier supplierCollectionOrphanCheckSupplier : supplierCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Address (" + address + ") cannot be destroyed since the Supplier " + supplierCollectionOrphanCheckSupplier + " in its supplierCollection field has a non-nullable fkAddid field.");
            }
            Collection<Branch> branchCollectionOrphanCheck = address.getBranchCollection();
            for (Branch branchCollectionOrphanCheckBranch : branchCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Address (" + address + ") cannot be destroyed since the Branch " + branchCollectionOrphanCheckBranch + " in its branchCollection field has a non-nullable fkAddid field.");
            }
            Collection<Customer> customerCollectionOrphanCheck = address.getCustomerCollection();
            for (Customer customerCollectionOrphanCheckCustomer : customerCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Address (" + address + ") cannot be destroyed since the Customer " + customerCollectionOrphanCheckCustomer + " in its customerCollection field has a non-nullable fkAddid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Role> roleCollection = address.getRoleCollection();
            for (Role roleCollectionRole : roleCollection) {
                roleCollectionRole.getAddressCollection().remove(address);
                roleCollectionRole = em.merge(roleCollectionRole);
            }
            em.remove(address);
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

    public List<Address> findAddressEntities() {
        return findAddressEntities(true, -1, -1);
    }

    public List<Address> findAddressEntities(int maxResults, int firstResult) {
        return findAddressEntities(false, maxResults, firstResult);
    }

    private List<Address> findAddressEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Address.class));
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

    public Address findAddress(Integer id) {
        try {
            return em.find(Address.class, id);
        } finally {
            em.close();
        }
    }

    public int getAddressCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Address> rt = cq.from(Address.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
