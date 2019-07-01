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
import model.Category;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import javax.annotation.Resource;
import javax.enterprise.context.SessionScoped;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import model.Productdetail;
import model.Orderdetail;
import model.Product;
import utilities.exceptions.IllegalOrphanException;
import utilities.exceptions.NonexistentEntityException;
import utilities.exceptions.RollbackFailureException;

/**
 *
 * @author eherbertz
 */
@SessionScoped
public class ProductJpaController implements Serializable {

    public ProductJpaController() {
    }
    
    @Resource
    private UserTransaction utx;
    @PersistenceContext
    private EntityManager em;

    public void create(Product product) throws RollbackFailureException, Exception {
        if (product.getCategoryCollection() == null) {
            product.setCategoryCollection(new ArrayList<Category>());
        }
        if (product.getProductdetailCollection() == null) {
            product.setProductdetailCollection(new ArrayList<Productdetail>());
        }
        if (product.getOrderdetailCollection() == null) {
            product.setOrderdetailCollection(new ArrayList<Orderdetail>());
        }
        try {
            utx.begin();
            Collection<Category> attachedCategoryCollection = new ArrayList<Category>();
            for (Category categoryCollectionCategoryToAttach : product.getCategoryCollection()) {
                categoryCollectionCategoryToAttach = em.getReference(categoryCollectionCategoryToAttach.getClass(), categoryCollectionCategoryToAttach.getId());
                attachedCategoryCollection.add(categoryCollectionCategoryToAttach);
            }
            product.setCategoryCollection(attachedCategoryCollection);
            Collection<Productdetail> attachedProductdetailCollection = new ArrayList<Productdetail>();
            for (Productdetail productdetailCollectionProductdetailToAttach : product.getProductdetailCollection()) {
                productdetailCollectionProductdetailToAttach = em.getReference(productdetailCollectionProductdetailToAttach.getClass(), productdetailCollectionProductdetailToAttach.getId());
                attachedProductdetailCollection.add(productdetailCollectionProductdetailToAttach);
            }
            product.setProductdetailCollection(attachedProductdetailCollection);
            Collection<Orderdetail> attachedOrderdetailCollection = new ArrayList<Orderdetail>();
            for (Orderdetail orderdetailCollectionOrderdetailToAttach : product.getOrderdetailCollection()) {
                orderdetailCollectionOrderdetailToAttach = em.getReference(orderdetailCollectionOrderdetailToAttach.getClass(), orderdetailCollectionOrderdetailToAttach.getId());
                attachedOrderdetailCollection.add(orderdetailCollectionOrderdetailToAttach);
            }
            product.setOrderdetailCollection(attachedOrderdetailCollection);
            em.persist(product);
            for (Category categoryCollectionCategory : product.getCategoryCollection()) {
                categoryCollectionCategory.getProductCollection().add(product);
                categoryCollectionCategory = em.merge(categoryCollectionCategory);
            }
            for (Productdetail productdetailCollectionProductdetail : product.getProductdetailCollection()) {
                Product oldFkPidOfProductdetailCollectionProductdetail = productdetailCollectionProductdetail.getFkPid();
                productdetailCollectionProductdetail.setFkPid(product);
                productdetailCollectionProductdetail = em.merge(productdetailCollectionProductdetail);
                if (oldFkPidOfProductdetailCollectionProductdetail != null) {
                    oldFkPidOfProductdetailCollectionProductdetail.getProductdetailCollection().remove(productdetailCollectionProductdetail);
                    oldFkPidOfProductdetailCollectionProductdetail = em.merge(oldFkPidOfProductdetailCollectionProductdetail);
                }
            }
            for (Orderdetail orderdetailCollectionOrderdetail : product.getOrderdetailCollection()) {
                Product oldFkPidOfOrderdetailCollectionOrderdetail = orderdetailCollectionOrderdetail.getFkPid();
                orderdetailCollectionOrderdetail.setFkPid(product);
                orderdetailCollectionOrderdetail = em.merge(orderdetailCollectionOrderdetail);
                if (oldFkPidOfOrderdetailCollectionOrderdetail != null) {
                    oldFkPidOfOrderdetailCollectionOrderdetail.getOrderdetailCollection().remove(orderdetailCollectionOrderdetail);
                    oldFkPidOfOrderdetailCollectionOrderdetail = em.merge(oldFkPidOfOrderdetailCollectionOrderdetail);
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

    public void edit(Product product) throws IllegalOrphanException, NonexistentEntityException, RollbackFailureException, Exception {
        try {
            utx.begin();
            Product persistentProduct = em.find(Product.class, product.getId());
            Collection<Category> categoryCollectionOld = persistentProduct.getCategoryCollection();
            Collection<Category> categoryCollectionNew = product.getCategoryCollection();
            Collection<Productdetail> productdetailCollectionOld = persistentProduct.getProductdetailCollection();
            Collection<Productdetail> productdetailCollectionNew = product.getProductdetailCollection();
            Collection<Orderdetail> orderdetailCollectionOld = persistentProduct.getOrderdetailCollection();
            Collection<Orderdetail> orderdetailCollectionNew = product.getOrderdetailCollection();
            List<String> illegalOrphanMessages = null;
            for (Productdetail productdetailCollectionOldProductdetail : productdetailCollectionOld) {
                if (!productdetailCollectionNew.contains(productdetailCollectionOldProductdetail)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Productdetail " + productdetailCollectionOldProductdetail + " since its fkPid field is not nullable.");
                }
            }
            for (Orderdetail orderdetailCollectionOldOrderdetail : orderdetailCollectionOld) {
                if (!orderdetailCollectionNew.contains(orderdetailCollectionOldOrderdetail)) {
                    if (illegalOrphanMessages == null) {
                        illegalOrphanMessages = new ArrayList<String>();
                    }
                    illegalOrphanMessages.add("You must retain Orderdetail " + orderdetailCollectionOldOrderdetail + " since its fkPid field is not nullable.");
                }
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Category> attachedCategoryCollectionNew = new ArrayList<Category>();
            for (Category categoryCollectionNewCategoryToAttach : categoryCollectionNew) {
                categoryCollectionNewCategoryToAttach = em.getReference(categoryCollectionNewCategoryToAttach.getClass(), categoryCollectionNewCategoryToAttach.getId());
                attachedCategoryCollectionNew.add(categoryCollectionNewCategoryToAttach);
            }
            categoryCollectionNew = attachedCategoryCollectionNew;
            product.setCategoryCollection(categoryCollectionNew);
            Collection<Productdetail> attachedProductdetailCollectionNew = new ArrayList<Productdetail>();
            for (Productdetail productdetailCollectionNewProductdetailToAttach : productdetailCollectionNew) {
                productdetailCollectionNewProductdetailToAttach = em.getReference(productdetailCollectionNewProductdetailToAttach.getClass(), productdetailCollectionNewProductdetailToAttach.getId());
                attachedProductdetailCollectionNew.add(productdetailCollectionNewProductdetailToAttach);
            }
            productdetailCollectionNew = attachedProductdetailCollectionNew;
            product.setProductdetailCollection(productdetailCollectionNew);
            Collection<Orderdetail> attachedOrderdetailCollectionNew = new ArrayList<Orderdetail>();
            for (Orderdetail orderdetailCollectionNewOrderdetailToAttach : orderdetailCollectionNew) {
                orderdetailCollectionNewOrderdetailToAttach = em.getReference(orderdetailCollectionNewOrderdetailToAttach.getClass(), orderdetailCollectionNewOrderdetailToAttach.getId());
                attachedOrderdetailCollectionNew.add(orderdetailCollectionNewOrderdetailToAttach);
            }
            orderdetailCollectionNew = attachedOrderdetailCollectionNew;
            product.setOrderdetailCollection(orderdetailCollectionNew);
            product = em.merge(product);
            for (Category categoryCollectionOldCategory : categoryCollectionOld) {
                if (!categoryCollectionNew.contains(categoryCollectionOldCategory)) {
                    categoryCollectionOldCategory.getProductCollection().remove(product);
                    categoryCollectionOldCategory = em.merge(categoryCollectionOldCategory);
                }
            }
            for (Category categoryCollectionNewCategory : categoryCollectionNew) {
                if (!categoryCollectionOld.contains(categoryCollectionNewCategory)) {
                    categoryCollectionNewCategory.getProductCollection().add(product);
                    categoryCollectionNewCategory = em.merge(categoryCollectionNewCategory);
                }
            }
            for (Productdetail productdetailCollectionNewProductdetail : productdetailCollectionNew) {
                if (!productdetailCollectionOld.contains(productdetailCollectionNewProductdetail)) {
                    Product oldFkPidOfProductdetailCollectionNewProductdetail = productdetailCollectionNewProductdetail.getFkPid();
                    productdetailCollectionNewProductdetail.setFkPid(product);
                    productdetailCollectionNewProductdetail = em.merge(productdetailCollectionNewProductdetail);
                    if (oldFkPidOfProductdetailCollectionNewProductdetail != null && !oldFkPidOfProductdetailCollectionNewProductdetail.equals(product)) {
                        oldFkPidOfProductdetailCollectionNewProductdetail.getProductdetailCollection().remove(productdetailCollectionNewProductdetail);
                        oldFkPidOfProductdetailCollectionNewProductdetail = em.merge(oldFkPidOfProductdetailCollectionNewProductdetail);
                    }
                }
            }
            for (Orderdetail orderdetailCollectionNewOrderdetail : orderdetailCollectionNew) {
                if (!orderdetailCollectionOld.contains(orderdetailCollectionNewOrderdetail)) {
                    Product oldFkPidOfOrderdetailCollectionNewOrderdetail = orderdetailCollectionNewOrderdetail.getFkPid();
                    orderdetailCollectionNewOrderdetail.setFkPid(product);
                    orderdetailCollectionNewOrderdetail = em.merge(orderdetailCollectionNewOrderdetail);
                    if (oldFkPidOfOrderdetailCollectionNewOrderdetail != null && !oldFkPidOfOrderdetailCollectionNewOrderdetail.equals(product)) {
                        oldFkPidOfOrderdetailCollectionNewOrderdetail.getOrderdetailCollection().remove(orderdetailCollectionNewOrderdetail);
                        oldFkPidOfOrderdetailCollectionNewOrderdetail = em.merge(oldFkPidOfOrderdetailCollectionNewOrderdetail);
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
                Integer id = product.getId();
                if (findProduct(id) == null) {
                    throw new NonexistentEntityException("The product with id " + id + " no longer exists.");
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
            Product product;
            try {
                product = em.getReference(Product.class, id);
                product.getId();
            } catch (EntityNotFoundException enfe) {
                throw new NonexistentEntityException("The product with id " + id + " no longer exists.", enfe);
            }
            List<String> illegalOrphanMessages = null;
            Collection<Productdetail> productdetailCollectionOrphanCheck = product.getProductdetailCollection();
            for (Productdetail productdetailCollectionOrphanCheckProductdetail : productdetailCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Product (" + product + ") cannot be destroyed since the Productdetail " + productdetailCollectionOrphanCheckProductdetail + " in its productdetailCollection field has a non-nullable fkPid field.");
            }
            Collection<Orderdetail> orderdetailCollectionOrphanCheck = product.getOrderdetailCollection();
            for (Orderdetail orderdetailCollectionOrphanCheckOrderdetail : orderdetailCollectionOrphanCheck) {
                if (illegalOrphanMessages == null) {
                    illegalOrphanMessages = new ArrayList<String>();
                }
                illegalOrphanMessages.add("This Product (" + product + ") cannot be destroyed since the Orderdetail " + orderdetailCollectionOrphanCheckOrderdetail + " in its orderdetailCollection field has a non-nullable fkPid field.");
            }
            if (illegalOrphanMessages != null) {
                throw new IllegalOrphanException(illegalOrphanMessages);
            }
            Collection<Category> categoryCollection = product.getCategoryCollection();
            for (Category categoryCollectionCategory : categoryCollection) {
                categoryCollectionCategory.getProductCollection().remove(product);
                categoryCollectionCategory = em.merge(categoryCollectionCategory);
            }
            em.remove(product);
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

    public List<Product> findProductEntities() {
        return findProductEntities(true, -1, -1);
    }

    public List<Product> findProductEntities(int maxResults, int firstResult) {
        return findProductEntities(false, maxResults, firstResult);
    }

    private List<Product> findProductEntities(boolean all, int maxResults, int firstResult) {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            cq.select(cq.from(Product.class));
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

    public Product findProduct(Integer id) {
        try {
            return em.find(Product.class, id);
        } finally {
            em.close();
        }
    }

    public int getProductCount() {
        try {
            CriteriaQuery cq = em.getCriteriaBuilder().createQuery();
            Root<Product> rt = cq.from(Product.class);
            cq.select(em.getCriteriaBuilder().count(rt));
            Query q = em.createQuery(cq);
            return ((Long) q.getSingleResult()).intValue();
        } finally {
            em.close();
        }
    }
    
}
