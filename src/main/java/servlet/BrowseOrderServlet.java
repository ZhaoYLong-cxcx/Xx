package servlet;
import com.fasterxml.jackson.databind.ObjectMapper;
import common.OrderStatus;
import entity.*;
import util.DButil;


import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
@WebServlet("/browseOrder")
public class BrowseOrderServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        resp.setContentType("text/html ; charset=UTF-8");
        resp.setCharacterEncoding("UTF-8");
        //1.根据当前的用户id进行订单的查找
        //2.查询结果可能是多个订单，使用list<Order>
        //3.需要判断查询的结果，如果是null说明没有订单，
        //4.如果不是空，将list转为json发送给前端
        HttpSession session=req.getSession();
        Account account=(Account) session.getAttribute("user");
        List<Order>order=this.queryOrder(account.getId());
        if (order==null){
            System.out.println("订单为空");
        }else {
            System.out.println("==========");
            System.out.println(order);
            ObjectMapper mapper=new ObjectMapper();
            //将响应包推送给浏览器
            //PrintWriter 使用io流
            PrintWriter printWriter=resp.getWriter();
            //将list转化为字符串，同时将当前json字符串填充到printWriter流中
            mapper.writeValue(printWriter,order);
            //给前端发数据的方法
            Writer writer=resp.getWriter();
            //把流响应发送给前端页面
            writer.write(printWriter.toString());
        }
    }

    private List<Order> queryOrder(Integer id) {

        Connection connection=null;
        PreparedStatement statement = null;
        ResultSet rs = null;
        List<Order>list=new ArrayList<>();
        try {
            String sql=this.getSql("@query");
            connection=DButil.getConnection(true);
            statement=connection.prepareStatement(sql);
            statement.setInt(1,id);
            rs=statement.executeQuery();
            Order order =null;
            while (rs.next()){
                //1.订单需要解析
                /*Order order = new Order();
                this.extractOrder(order,rs);
                list.add(order);*/
                if (order==null){
                    order=new Order();
                    this.extractOrder(order,rs);
                    list.add(order);
                }
                String orderId=rs.getString("order_id");
                if (!orderId.equals(order.getId())){
                    order=new Order();
                    this.extractOrder(order,rs);
                    list.add(order);
                }
                //2.订单项需要解析
                OrederItem orederItem=this.extractOrederItem(rs);
                order.orederItemList.add(orederItem);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            DButil.close(connection,statement,rs);
        }

        System.out.println("浏览订单:"+list);
        return list;
    }
/*
* 解析订单项
* */
    private OrederItem extractOrederItem(ResultSet rs) throws SQLException {
        OrederItem orederItem=new OrederItem();
        orederItem.setId(rs.getInt("item_id"));
        orederItem.setOrderId(rs.getString("order_id"));
        orederItem.setGoodsId(rs.getInt("goods_id"));
        orederItem.setGoodsName(rs.getString("goods_name"));
        orederItem.setGoodsIntroduce(rs.getString("goods_introduce"));
        orederItem.setGoodsNum(rs.getInt("goods_num"));
        orederItem.setGoodsUnit(rs.getString("goods_unit"));
        orederItem.setGoodsPrice(rs.getInt("goods_price"));
        orederItem.setGoodsDiscount(rs.getInt("goods_discount"));
        return orederItem;

    }
/*
* 解析订单
*
* */
    private void extractOrder(Order order, ResultSet rs)throws SQLException {
        order.setId(rs.getString("order_id"));
        order.setAccount_id(rs.getInt("account_id"));
        order.setAccount_name(rs.getString("account_name"));
        order.setCreate_time(rs.getString("create_time"));
        order.setFinish_time(rs.getString("finish_time"));
        order.setActual_amount(rs.getInt("actual_amount"));
        order.setTotal_money(rs.getInt("total_money"));
        order.setOrderStatus(OrderStatus.valueOf(rs.getInt("order_status")));
    }

    /*
* 生成一条sql语句
*
* */
    public String getSql(String sqlName) {
        System.out.println("sqlName:"+sqlName);
        //InputStream 是字节流  this.getClass获取当前的Class对象
        try (InputStream in = this.getClass()
                //获取类加载器
                .getClassLoader()
                //这个方法是用来获取配置文件的，方法传入的参数是一个路径
                .getResourceAsStream("scricpt/" + sqlName.substring(1) + ".sql");
             // 从1 开始提取的原因是：sqlName: @query_order_by_account 去掉@符号
        ) {
            if (in == null) {
                throw new RuntimeException("load sql " + sqlName + " failed");
            } else {
                //把字节流转为字符流
                //InputStreamReader :字节流 通向字符流的桥梁
                try (InputStreamReader isr = new InputStreamReader(in);
                     //BufferedReader -> 从字符输入流中读取文本并缓冲字符
                     BufferedReader reader = new BufferedReader(isr)) {//isr字符流

                    StringBuilder stringBuilder = new StringBuilder();

                    stringBuilder.append(reader.readLine());

                    String line;
                    while (( line = reader.readLine()) != null) {
                        stringBuilder.append(" ").append(line);
                    }

                    System.out.println("value:" + stringBuilder.toString());
                    return stringBuilder.toString();
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("load sql " + sqlName + " failed");
        }
    }
}
