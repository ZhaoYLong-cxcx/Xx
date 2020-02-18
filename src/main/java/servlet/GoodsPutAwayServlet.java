package servlet;
import util.DButil;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.sql.*;
@WebServlet("/inbound")
public class GoodsPutAwayServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html : charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String name=req.getParameter("name");
        Integer stock = Integer.parseInt(req.getParameter("stock"));
        String introduce=req.getParameter("introduce");
        String unit=req.getParameter("unit");
        String  price=req.getParameter("price");
        double doublePrice=Double.valueOf(price);
        int realPrice=new Double(doublePrice*100).intValue();
        Integer  discount=Integer.parseInt(req.getParameter("discount"));
        Connection connection=null;
        PreparedStatement statement=null;
        try {
            String sql="INSERT INTO goods(name,stock,introduce,unit,price,discount) VALUES (?,?,?,?,?,?)";
            connection=DButil.getConnection(true);
            statement=connection.prepareStatement(sql,Statement.RETURN_GENERATED_KEYS);
            statement.setString(1,name);
            statement.setInt(2,stock);
            statement.setString(3,introduce);
            statement.setString(4,unit);
            statement.setInt(5,realPrice);
            statement.setInt(6,discount);
            int ret=statement.executeUpdate();
            if (ret==1){
                resp.sendRedirect("goodsbrowse.html");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            DButil.close(connection,statement,null);
        }
    }
}
