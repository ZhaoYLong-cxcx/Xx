package entity;

import lombok.Data;

@Data
public class OrederItem {
    private Integer id;
    private String orderId;
    private Integer goodsId;
    private String goodsName;
    private String goodsIntroduce;
    private Integer goodsNum;
    private String goodsUnit;
    private Integer goodsPrice;
    private Integer goodsDiscount;//购买商品数量
    public Double getGoodsPrice() {
        return goodsPrice * 1.0 / 100;
    }

    public int getGoodsPriceInt() {
        return goodsPrice;
    }

}
