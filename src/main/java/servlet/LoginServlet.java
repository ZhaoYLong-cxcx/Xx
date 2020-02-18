package servlet;

import com.mysql.jdbc.ReplicationMySQLConnection;
import entity.Account;
import javafx.concurrent.Task;
import util.DButil;

import javax.print.DocFlavor;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html : charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String username=req.getParameter("username");
        String password = req.getParameter("password");
        Connection connection=null;
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        try {
            String sql="select id,username,password from account where username=? and password=? ";
            connection=DButil.getConnection(true);
            statement = connection.prepareStatement(sql);
            statement.setString(1,username);
            statement.setString(2,password);
            resultSet=statement.executeQuery();
            Account account=new Account();
            if (resultSet.next()){
                account.setId(resultSet.getInt("id"));
                account.setName(resultSet.getString("username"));
                account.setPassword(resultSet.getString("password"));
            }
            Writer writer=resp.getWriter();
            if (account.getId()==null){
                //推送前端System.out.println("用户名后者密码错误");
                writer.write("<h2>用户名或者密码错误："+username+"</h2>");

            }else {
                HttpSession session=req.getSession();
                session.setAttribute("user",account);
                resp.sendRedirect("index.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            DButil.close(connection,statement,resultSet);
        }
    }
}
