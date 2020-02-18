package common;

import lombok.*;

@Getter
@ToString
public enum OrderStatus {
    PLAYING(1,"待支付"),OK(2,"支付完成");
    private int flg;
    private String desc;

    OrderStatus(int flg, String desc) {
        this.flg = flg;
        this.desc = desc;
    }

    //浏览订单是 直接拿到你
    public static OrderStatus valueOf(int flg){
        for (OrderStatus orderStatus:OrderStatus.values()) {
            if (orderStatus.flg==flg){
                return orderStatus;
            }
        }
        throw new  RuntimeException("orderStatus is not fount");
    }
}
