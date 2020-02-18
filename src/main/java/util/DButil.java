package util;

import com.mysql.jdbc.jdbc2.optional.MysqlDataSource;

import javax.sql.DataSource;
import java.sql.*;

public class DButil {
    private static  final String URL="jdbc:mysql://localhost:3306/cash";
    private static final String USERNAME="root";
    private static final String PASSWORD="123456";
    private static volatile DataSource dataSource;
    private static DataSource getDataSource(){
        if (dataSource==null){
            synchronized (DButil.class){
                if (dataSource==null) {
                    dataSource = new MysqlDataSource();
                    ((MysqlDataSource)dataSource).setURL(URL);
                    ((MysqlDataSource)dataSource).setUser(USERNAME);
                    ((MysqlDataSource)dataSource).setPassword(PASSWORD);
                }
            }
        }
        return dataSource;
    }
    public static Connection getConnection(boolean autoCommit){
        try {
            Connection connection=getDataSource().getConnection();
            connection.setAutoCommit(autoCommit);
            return connection;
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException("数据库连接失败");
        }
    }
    public static void close(Connection connection,PreparedStatement statement,ResultSet resultSet){
        try {
            if (resultSet!=null){
                resultSet.close();
            }
            if (statement!=null){
                statement.close();
            }
            if (connection!=null){
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
