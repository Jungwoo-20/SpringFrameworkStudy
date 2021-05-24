package jpabook.jpashop.querydsl;

public class SearchParam {
    private String name;
    private Integer price;

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Integer price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public Integer getPrice() {
        return price;
    }
}
