package utilities;

import controller.ProductBean;
import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import model.Account;
import model.Product;


@Named(value = "JDBCData")
@ApplicationScoped
public class JDBCData implements Serializable {

    private Connection conn = null;
    private ArrayList<Account> accountList
            = new ArrayList<Account>();
    private ArrayList<Product> productList
            = new ArrayList<Product>();
    /**
     * Creates a new instance of JDBCConnect
     * @throws java.lang.ClassNotFoundException
     */
    public JDBCData() throws ClassNotFoundException{

        String dbDriverClass, dbURL;
        dbDriverClass = "com.mysql.jdbc.Driver";
        Class.forName(dbDriverClass);
        dbURL = "jdbc:mysql://localhost:3306/toolshop";
        try {
            conn = DriverManager.getConnection(dbURL, "toolshop", "toolshop");
        } catch (Exception ex) {
            Logger
                    .getLogger(JDBCData.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
        
        loadProductList();
        loadAccountList();
    }
    
    private void loadProductList() { 
        String sql = "SELECT Name, NetPrice, Comment, CategoryName FROM product p "
                + "INNER JOIN productcategory pc ON pc.FK_PID = p.ID "
                + "INNER JOIN category c ON pc.FK_CATID = c.ID "
                + "ORDER BY Name";
        try {
            ResultSet rs = conn
                    .createStatement()
                    .executeQuery(sql);
            while (rs.next()) {
                productList.add(new Product(
                        rs.getString("Name"),
                        new BigDecimal(rs.getDouble("NetPrice")),
                        rs.getString("CategoryName"),
                        rs.getString("Comment"))
                );
                Logger.getLogger(ProductBean.class.getName())
                        .log(Level.INFO, rs.getString("Name"), rs);
            }//while
            rs.close();
        } catch (SQLException ex) {
            Logger
                    .getLogger(ProductBean.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }

    private void loadAccountList() { 
        String sql = "SELECT * FROM account";
        try {
            ResultSet rs = conn.createStatement().executeQuery(sql);
            while (rs.next()) {
                accountList.add(new Account(
                        rs.getString("Name"),
                        rs.getString("Pwd"))
                );
            }//while
        } catch (SQLException ex) {
            Logger.getLogger(ProductBean.class.getName())
                    .log(Level.SEVERE, null, ex);
        }
    }
    /**
     * @return the conn
     */
    public Connection getConn() {
        return conn;
    }

    /**
     * @return the accountList
     */
    public ArrayList<Account> getAccountList() {
        return accountList;
    }

    /**
     * @return the productList
     */
    public ArrayList<Product> getProductList() {
        return productList;
    }
}
