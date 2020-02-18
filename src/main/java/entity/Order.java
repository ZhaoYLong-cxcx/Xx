package entity;

import lombok.Data;
import common.OrderStatus;

import java.util.*;

@Data
public class Order {
    private String id;
    private Integer account_id;
    private String account_name;
    private String create_time;
    private String finish_time;
    private Integer actual_amount;
    private Integer total_money;
    //订单状态，我们是使用的枚举来表示
    private OrderStatus orderStatus;
    public OrderStatus getOrderStatus_desc() {
        return orderStatus;
    }

    public String getOrderStatus() {
        return orderStatus.getDesc();
    }

    //因为订单项是属于订单的，所以也需要存储到当前订单内
    public List<OrederItem>orederItemList=new ArrayList<>();

    public double getActual_amount() {
        return actual_amount*1.0/100;
    }
    public Integer getActual_amountInt() {
        return actual_amount;
    }

    public double getTotal_money() {
        return total_money*1.0/100;
    }

    public Integer getTotal_moneyInt() {
        return total_money;
    }
    //计算优惠金额
    public double getDiscount(){

        /*return (this.getTotal_money()-this.getActual_amount());*/
        //上面的我们算出来的小数点太多了，我们换一种来使得小数正常
        return (this.getTotal_moneyInt()-this.getActual_amountInt())*1.0/100;
    }
}
