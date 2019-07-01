/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author eherbertz
 */
@Entity
@Table(name = "orderdetail")
@XmlRootElement
@NamedQueries({
    @NamedQuery(name = "Orderdetail.findAll", query = "SELECT o FROM Orderdetail o")
    , @NamedQuery(name = "Orderdetail.findById", query = "SELECT o FROM Orderdetail o WHERE o.id = :id")
    , @NamedQuery(name = "Orderdetail.findByQuantity", query = "SELECT o FROM Orderdetail o WHERE o.quantity = :quantity")
    , @NamedQuery(name = "Orderdetail.findByPriceRebatePercent", query = "SELECT o FROM Orderdetail o WHERE o.priceRebatePercent = :priceRebatePercent")
    , @NamedQuery(name = "Orderdetail.findByTStamp", query = "SELECT o FROM Orderdetail o WHERE o.tStamp = :tStamp")})
public class Orderdetail implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Basic(optional = false)
    @NotNull
    @Column(name = "Quantity")
    private int quantity;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Basic(optional = false)
    @NotNull
    @Column(name = "PriceRebatePercent")
    private BigDecimal priceRebatePercent;
    @Basic(optional = false)
    @NotNull
    @Column(name = "TStamp")
    @Temporal(TemporalType.TIMESTAMP)
    private Date tStamp;
    @JoinColumn(name = "FK_OID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Order1 fkOid;
    @JoinColumn(name = "FK_PID", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Product fkPid;

    public Orderdetail() {
    }

    public Orderdetail(Integer id) {
        this.id = id;
    }

    public Orderdetail(Integer id, int quantity, BigDecimal priceRebatePercent, Date tStamp) {
        this.id = id;
        this.quantity = quantity;
        this.priceRebatePercent = priceRebatePercent;
        this.tStamp = tStamp;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public BigDecimal getPriceRebatePercent() {
        return priceRebatePercent;
    }

    public void setPriceRebatePercent(BigDecimal priceRebatePercent) {
        this.priceRebatePercent = priceRebatePercent;
    }

    public Date getTStamp() {
        return tStamp;
    }

    public void setTStamp(Date tStamp) {
        this.tStamp = tStamp;
    }

    public Order1 getFkOid() {
        return fkOid;
    }

    public void setFkOid(Order1 fkOid) {
        this.fkOid = fkOid;
    }

    public Product getFkPid() {
        return fkPid;
    }

    public void setFkPid(Product fkPid) {
        this.fkPid = fkPid;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (id != null ? id.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Orderdetail)) {
            return false;
        }
        Orderdetail other = (Orderdetail) object;
        if ((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "model.Orderdetail[ id=" + id + " ]";
    }
    
}
