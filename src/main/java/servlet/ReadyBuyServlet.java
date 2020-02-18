package servlet;

import com.mysql.jdbc.ReplicationMySQLConnection;
import entity.*;
import common.OrderStatus;
import util.DButil;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet("/pay")
public class ReadyBuyServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html : charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        List<Goods>list=new ArrayList<>();
        //在浏览器界面中输入的信息都是请求体，
        // 我们拿到请求体中输入的的goodsIdAndNum
        String goodsIdAndNum=req.getParameter("goodsIdAndNum");
        String[]strings=goodsIdAndNum.split(",");
        //12-2 15-3
        for (String s:strings) {
            String []strings1=s.split("-");
            //12代表id，2代表需要购买商品数量
            Goods goods=this.getGoods(Integer.valueOf(strings1[0]));
            if (goods!=null){
                list.add(goods);
                goods.setBuyGoodsNum(Integer.valueOf(strings1[1]));
            }else {
                Writer writer=resp.getWriter();
                writer.write("<h2>没有此货物！</h2>");
                throw new RuntimeException("没有此货物！");
            }
        }
        System.out.println("当前需要购买商品"+list);
        //再订单中我们是需要获取到账户的信息，我们因为之前在login的界面中，写入了session，
        //现在也是通过get来获取到  账户信息的对象
        HttpSession session=req.getSession();
        Account account=(Account)session.getAttribute("user");
        session.setAttribute("list",list);
        //生成订单
        Order order=new Order();
        //order的id是根据时间来生成
        order.setId(String.valueOf(System.currentTimeMillis()));
        order.setAccount_id(account.getId());
        order.setAccount_name(account.getName());
        //写入格式化时间
        DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        order.setCreate_time(LocalDateTime.now().format(formatter));
        int totalMoney=0;
        int actualMoney=0;
        /*
        * 每个货物都是一个orederItem
        * */
        for (Goods goods:list) {
            OrederItem orederItem=new OrederItem();
            orederItem.setOrderId(order.getId());
            orederItem.setGoodsId(goods.getId());
            orederItem.setGoodsName(goods.getName());
            orederItem.setGoodsIntroduce(goods.getIntroduce());
            orederItem.setGoodsNum(goods.getBuyGoodsNum());
            orederItem.setGoodsUnit(goods.getUnit());
            orederItem.setGoodsPrice(goods.getPriceInt());
            orederItem.setGoodsDiscount(goods.getDiscount());
            //我们现在将我们是解析的订单项信息填写到我们的list中，此时订单项的对象是属于oreder中的
            order.orederItemList.add(orederItem);
            //我们需要记录实际支付的钱，和总金额后面放入到数据库的订单中
            int currentMoney=goods.getBuyGoodsNum()*goods.getPriceInt();
            totalMoney+=currentMoney;
            actualMoney+=currentMoney*goods.getDiscount()/100;
        }
        order.setTotal_money(totalMoney);
        order.setActual_amount(actualMoney);
        order.setOrderStatus(OrderStatus.PLAYING);
        //将order写入到session中方便我们后面来进行获取

        HttpSession session1=req.getSession();
        session1.setAttribute("order",order);

        resp.getWriter().println("<html>");
        resp.getWriter().println("<p>"+"【用户名称】："+order.getAccount_name()+"</p>");
        resp.getWriter().println("<p>"+"【订单编号】："+order.getId()+"</p>");
        resp.getWriter().println("<p>"+"【订单状态】："+order.getOrderStatus_desc().getDesc()+"</p>");
        resp.getWriter().println("<p>"+"【创建时间】："+order.getCreate_time()+"</p>");
        resp.getWriter().println("<p>"+"编号  "+"名称  "+"数量  "+"单位  "+"单价(元)  "+"</p>");
        resp.getWriter().println("<ol>");
        for (OrederItem orederItem1:order.orederItemList) {
            resp.getWriter().println("<li>"+orederItem1.getGoodsName()+" "+orederItem1.getGoodsNum()+" "+
            orederItem1.getGoodsUnit()+" "+orederItem1.getGoodsPrice()+"</li>");
        }
        resp.getWriter().println("</ol>");
        resp.getWriter().println("<p>"+"【总金额】："+order.getTotal_money()+"</p>");
        resp.getWriter().println("<p>"+"【优惠金额】："+order.getDiscount()+"</p>");
        resp.getWriter().println("<p>"+"【实付金额】："+order.getActual_amount()+"</p>");
        resp.getWriter().println("<a href= \"buyGoodsServlet\">确认</a>");
        resp.getWriter().println("<a href= \"index.html\">取消</a>");
        resp.getWriter().println("</html>");
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
