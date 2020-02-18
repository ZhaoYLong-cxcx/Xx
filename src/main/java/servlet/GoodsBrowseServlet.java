package servlet;

import com.fasterxml.jackson.databind.ObjectMapper;
import entity.Goods;
import util.DButil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.util.*;

/*
 * 查询goods表中的所有商品
 * */
@WebServlet("/browseGoods")
public class GoodsBrowseServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html : charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        List<Goods> list = new ArrayList<>();
        /*创建list集合我们是为了，将结果集中的每一个goods对象放入到集合中,
        * ObjectMapper这个工具可以帮我们将我们的对象转化为json形式
        * 我们是需要通过IO流才可以将我们的数据推送给浏览器使用PrintWriter
        * 推送前端需要我们使用Writer，进行前端显示
        *
        * */
        try {
            String sql = "select id,name,introduce,stock,unit,price,discount from goods";
            connection = DButil.getConnection(true);
            statement = connection.prepareStatement(sql);
            resultSet = statement.executeQuery();
            //解析结果集中的内容
            while (resultSet.next()) {
                //我们需要将获取到数据库中的结果集全部放入到list集合中
                //解析结果集
                Goods goods = this.extractGoods(resultSet);
                if (goods != null) {
                    list.add(goods);
                }
            }
            System.out.println(list);
            //把list转为json
            //方便将模型转换为JSON
            ObjectMapper mapper=new ObjectMapper();
            //将响应包推送给浏览器
            //PrintWriter 使用io流
            PrintWriter printWriter=resp.getWriter();
            //将list转化为字符串，同时将当前json字符串填充到printWriter流中
            mapper.writeValue(printWriter,list);
            //给前端发数据的方法
            Writer writer=resp.getWriter();
            //把流响应发送给前端页面
            writer.write(printWriter.toString());

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DButil.close(connection, statement, resultSet);
        }
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
