package servlet;

import util.DButil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;

@WebServlet("/delGoods")
public class DelGoodsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html : charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String goodsId=req.getParameter("id");
        System.out.println(goodsId);
        Connection connection=null;
        PreparedStatement statement=null;
        try {
            String sql="delete from goods where id=?";
            connection=DButil.getConnection(true);
            statement=connection.prepareStatement(sql);
            statement.setInt(1,Integer.valueOf(goodsId));
            int e=statement.executeUpdate();
            if (e==1){
                System.out.println("商品下架成功");
            }else {
                System.out.println("商品下架失败");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DButil.close(connection,statement,null);
        }
    }
}
