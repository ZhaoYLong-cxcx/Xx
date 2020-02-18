package servlet;
import	java.sql.Statement;
import util.DButil;

import java.sql.*;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/register")
public class RegisterServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html : charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String username=req.getParameter("username");
        String password = req.getParameter("password");
        Connection connection=null;
        PreparedStatement statement=null;
        try {
            String sql="INSERT INTO account(username, password) VALUES (?,?)";
            connection=DButil.getConnection(true);
            statement=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,username);
            statement.setString(2,password);
            int w=statement.executeUpdate();
            if (w==1){
                resp.sendRedirect("login.html");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            DButil.close(connection,statement,null);
        }
    }
}
