package utilities;

import javax.inject.Named;
import javax.enterprise.context.ApplicationScoped;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;


@Named(value = "JDBCData")
@ApplicationScoped
public class JDBCData implements Serializable {

    private Connection conn = null;
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
    }

    /**
     * @return the conn
     */
    public Connection getConn() {
        return conn;
    }
}
