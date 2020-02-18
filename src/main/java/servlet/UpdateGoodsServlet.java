package servlet;

import entity.Goods;
import util.DButil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
/*
* 首先通过更新商品的id，查找改id是否存在对应的商品
* 如果不存在  更新失败
* 存在  根据id找到对应的货物，对该货物的属性进行修改
* 随后将修改后的货物，进行数据库的更新
*
* */
@WebServlet("/updateGoods")
public class UpdateGoodsServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html : charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        String goodsIdString=req.getParameter("goodsID");
        int goodsId=Integer.parseInt(goodsIdString);
        String name=req.getParameter("name");
        Integer stock = Integer.parseInt(req.getParameter("stock"));
        String introduce=req.getParameter("introduce");
        String unit=req.getParameter("unit");
        String  price=req.getParameter("price");
        double doublePrice=Double.valueOf(price);
        int realPrice=new Double(doublePrice*100).intValue();
        Integer  discount=Integer.parseInt(req.getParameter("discount"));
        Goods goods=this.getGoods(goodsId);
        if (goods==null){
            System.out.println("没有该商品");
            resp.sendRedirect("index.html");
        }else {
            goods.setName(name);
            goods.setIntroduce(introduce);
            goods.setStock(stock);
            goods.setUnit(unit);
            goods.setPrice(realPrice);
            goods.setDiscount(discount);

            //把查询的商品进行更新，随后对数据库进行操作
            boolean effect=this.modifyGoods(goods);
            if (effect){
                System.out.println("更新成功");
                resp.sendRedirect("goodsbrowse.html");
            }else {
                System.out.println("更新失败");
                resp.sendRedirect("index.html");
            }
        }
    }

    private boolean modifyGoods(Goods goods) {
        Connection connection=null;
        PreparedStatement statement=null;
        Boolean effect=false;
        try {
            String sql="update goods set name=?,introduce=?,stock=?,unit=?,price=?,discount=? where id=?";
            connection=DButil.getConnection(true);
            statement=connection.prepareStatement(sql);
            statement.setString(1,goods.getName());
            statement.setString(2,goods.getIntroduce());
            statement.setInt(3,goods.getStock());
            statement.setString(4,goods.getUnit());
            statement.setInt(5,goods.getPriceInt());
            statement.setInt(6,goods.getDiscount());
            statement.setInt(7,goods.getId());
            effect=(statement.executeUpdate()==1);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DButil.close(connection,statement,null);
        }
        return effect;
    }

    private Goods getGoods(int goodsId){
        Connection connection=null;
        PreparedStatement statement=null;
        ResultSet resultSet=null;
        Goods goods=null;
        try {
            String sql="select * from goods where id=?";
            connection=DButil.getConnection(true);
            statement=connection.prepareStatement(sql);
            statement.setInt(1,goodsId);
            resultSet=statement.executeQuery();
            if (resultSet.next()){
                goods=this.extractGoods(resultSet);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DButil.close(connection,statement,resultSet);
        }
        return goods;
    }
    private Goods extractGoods(ResultSet resultSet) throws SQLException {
        Goods goods = new Goods();
        goods.setId(resultSet.getInt("id"));
        goods.setName(resultSet.getString("name"));
        goods.setIntroduce(resultSet.getString("introduce"));
        goods.setStock(resultSet.getInt("stock"));
        goods.setUnit(resultSet.getString("unit"));
        goods.setPrice(resultSet.getInt("price"));
        goods.setDiscount(resultSet.getInt("discount"));
        return goods;
    }
}
