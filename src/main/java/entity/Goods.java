package entity;

import lombok.Data;

@Data
public class Goods {
    private Integer id;
    private String name;
    private Integer stock;
    private String introduce;
    private String unit;
    private Integer price;
    private Integer discount;
    private Integer buyGoodsNum;//购买商品数量
    public Double getPrice() {
        return price * 1.0 / 100;
    }

    public int getPriceInt() {
        return price;
    }
}
