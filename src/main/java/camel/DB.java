package camel;


import org.apache.log4j.Logger;
import java.sql.*;

public class DB {
    private static Logger log = Logger.getLogger(DB.class.getName());

    private String url;
    private String user;
    private String password;

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    private static final String deleteTable1 = "DROP TABLE IF EXISTS TABLE1";
    private static final String createTable1 =
            "CREATE TABLE TABLE1 (TEXT_ID INT PRIMARY KEY, TEXT VARCHAR NOT NULL," +
                    " TIME VARCHAR NOT NULL)";
    private static final String insertResults =
            "INSERT INTO TABLE1 (TEXT_ID, TEXT, TIME) VALUES (?, ?, ?)";

    public void createDB() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            Statement statement = connection.createStatement();
            statement.executeUpdate(deleteTable1);
            statement.executeUpdate(createTable1);
            log.info("DB is ready");
            statement.close();
        } catch (SQLException e) {
            log.info("Exception: {}", e);
        }
    }

    // В таблицу1 записывем данные TEXT_ID и TEXT и TIME
    public void insertFields1(int TEXT_ID, String TEXT, String TIME) throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            PreparedStatement preparedStatement = connection.prepareStatement(insertResults);
            preparedStatement.setInt(1, TEXT_ID);
            preparedStatement.setString(2, TEXT);
            preparedStatement.setString(3, TIME);
            preparedStatement.addBatch();
            preparedStatement.executeBatch();
            log.info("Inserting into table1 values: "+
                    String.valueOf(TEXT_ID)+", "+TEXT+", "+TIME);
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            log.error("Exception: {}", e);
        }
    }


    public void initDB() throws SQLException {
        try {
            createDB();
            log.info("Init DB");
        } catch (SQLException e) {
            log.error("Exception: {}", e);
        }
    }

    public void destroyDB() throws SQLException {
        try {
            Connection connection = DriverManager.getConnection(url, user, password);
            connection.close();
            log.info("Destroy DB");
        } catch (SQLException e) {
            log.error("Exception: {}", e);
        }
    }


}
