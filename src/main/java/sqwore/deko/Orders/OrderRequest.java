package sqwore.deko.Orders;

import sqwore.deko.Users.Users;

import java.util.Date;

public class OrderRequest {
    private Long id;
    private Long userId;
    private String info;
    private String currency;
    private Integer amount;
    private Integer rubAmount;
    private String status;
    private Date orderDate;
    private String orderUuid;

    public OrderRequest(){}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }

    public Integer getRubAmount() {
        return rubAmount;
    }

    public void setRubAmount(Integer rub_amount) {
        this.rubAmount = rub_amount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getOrderDate() {
        return orderDate;
    }

    public void setOrderDate(Date orderDate) {
        this.orderDate = orderDate;
    }

    public String getOrderUuid() {
        return orderUuid;
    }

    public void setOrderUuid(String orderUuid) {
        this.orderUuid = orderUuid;
    }
}
