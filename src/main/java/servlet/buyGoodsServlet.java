package servlet;
import java.sql.*;

import common.OrderStatus;
import entity.*;
import javafx.scene.effect.Effect;
import sun.management.snmp.util.SnmpTableCache;
import util.DButil;

import javax.print.attribute.standard.MediaSize;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@WebServlet("/buyGoodsServlet")
//使用a标签是需要使用doget来请求
public class buyGoodsServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doPost(req,resp);
    }
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    HttpSession session=req.getSession();
    //我们使用order对象来接收返回的session信息，session的返回值是Object我们需要进行强转
    Order order=(Order) session.getAttribute("order");
    //接收我们的都是实物的list
    List<Goods> goodsList=(List<Goods>) session.getAttribute("list");
    DateTimeFormatter formatter=DateTimeFormatter.ofPattern("yyyy-MM-dd");
    order.setFinish_time(LocalDateTime.now().format(formatter));
    order.setOrderStatus(OrderStatus.OK);
    //现在我们的表示的是支付成功我们需要提交到数据库，
        //在原有的数据库中我们要做更新购买商品的个数，在订单表和订单项中我们需要插入购买信息
        boolean effect=this.commitOrder(order);
        //当插入数据库为真
        //遍历货物，将货物的库存进行修改   我们可以发现 在read购买的servlet中
        // list存入的就是我们准备买的货物，   我们可以将list写到session中  然后这边来接收
        if (effect){
            for (Goods goods:goodsList) {
                //我们后面通过这个方法来进行更新操作，传输的是我们的goods对象，和我们的购买货物的数量
               boolean isUpdate= this.updateAfterBuy(goods,goods.getBuyGoodsNum());
               if (isUpdate){
                   System.out.println("更新库存成功！");

               }else {
                   System.out.println("更新库存失败！");
               }
            }
            //这界面表示是购买成功，  但是如果就素更新失败  也会跳到此处
            //个人觉得调整到更新库存成功的if判断下更好
            resp.sendRedirect("buyGoodsSuccess.html");
        }else {
            throw new RuntimeException("更新失败");
        }
    }
    private boolean commitOrder(Order order){
        Connection connection=null;
        PreparedStatement statement=null;
        try {
            String insertOrder ="INSERT into `order`(id, account_id, account_name, create_time, finish_time, actual_amount, total_money, order_status) VALUES (?,?,?,?,now(),?,?,?)";
            String insertOrderItem ="INSERT into order_item(order_id, goods_id, goods_name, goods_introduce, goods_num, goods_unit, goods_price, goods_discount) VALUES (?,?,?,?,?,?,?,?)";
            connection=DButil.getConnection(false);
            statement = connection.prepareStatement(insertOrder);
            statement.setString(1,order.getId());
            statement.setInt(2,order.getAccount_id());
            statement.setString(3,order.getAccount_name());
            statement.setString(4,order.getCreate_time());
            statement.setInt(5,order.getActual_amountInt());
            statement.setInt(6,order.getTotal_moneyInt());
            statement.setInt(7,order.getOrderStatus_desc().getFlg());
            if (statement.executeUpdate()==0){
                throw new RuntimeException("插入订单失败");
            }
            //插入订单成功
            //我们开始插入订单项
            statement = connection.prepareStatement(insertOrderItem);
            //批量插入
            for (OrederItem orederItem:order.orederItemList) {
                statement.setString(1,order.getId());
                statement.setInt(2,orederItem.getGoodsId());
                statement.setString(3,orederItem.getGoodsName());
                statement.setString(4,orederItem.getGoodsIntroduce());
                statement.setInt(5,orederItem.getGoodsNum());
                statement.setString(6,orederItem.getGoodsUnit());
                statement.setInt(7,orederItem.getGoodsPriceInt());
                statement.setInt(8,orederItem.getGoodsDiscount());
                //将每一项进行缓存
                statement.addBatch();
            }
              int []effects=statement.executeBatch();
            for (int e:effects) {
                if (e==0){
                    throw new RuntimeException("插入订单项失败");
                }
            }
            //程序走在这里如果没有停，就表示我们所有的插入都成功
            //手动提交
           connection.commit();
        } catch (Exception e) {
            e.printStackTrace();
            if (connection!=null){
                try {
                    connection.rollback();
                } catch (SQLException e1) {
                    e1.printStackTrace();
                }
            }
            return false;
        } finally {
            DButil.close(connection,statement,null);
        }
        return true;
    }
    private boolean updateAfterBuy(Goods goods, Integer buyGoodsNum) {
        Connection connection=null;
        PreparedStatement statement=null;
        Boolean effect = false;
        try {
            String sql="update goods set stock=? where id=?";
            connection=DButil.getConnection(true);
            statement = connection.prepareStatement(sql);
            statement.setInt(1,goods.getStock()-buyGoodsNum);
            statement.setInt(2,goods.getId());
            if (statement.executeUpdate()==1){
                effect=true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DButil.close(connection,statement, null);
        }
        return effect;
    }
}
