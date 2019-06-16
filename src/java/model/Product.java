package model;

import java.math.BigDecimal;
import javax.enterprise.context.Dependent;
import javax.inject.Named;

@Named(value = "product")
@Dependent
public class Product {

    private String name;
    private BigDecimal netPrice;
    private String type;
    private String comment;
    /**
     * Creates a new instance of Product
     * @param name of the product
     * @param price of the product
     * @param type of the product
     * @param comment to the product
     */
    public Product(String name, BigDecimal price, String type, String comment) {
        this.name = name;
        this.netPrice = price;
        this.type = type;
        this.comment = comment;
    }

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name the name to set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return the netPrice
     */
    public BigDecimal getNetPrice() {
        return netPrice;
    }

    /**
     * @param netPrice the netPrice to set
     */
    public void setNetPrice(BigDecimal netPrice) {
        this.netPrice = netPrice;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * @return the comment
     */
    public String getComment() {
        return comment;
    }

    /**
     * @param comment the comment to set
     */
    public void setComment(String comment) {
        this.comment = comment;
    }
}
