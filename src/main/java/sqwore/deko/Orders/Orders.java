package sqwore.deko.Orders;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;
import sqwore.deko.Api.ApiService;
import sqwore.deko.Users.Users;

import java.time.LocalDateTime;
import java.util.UUID;


@Entity(name="orders")
@Table(name= "orders")
public class Orders {

    @Id
    @Column(
            name = "id",
            updatable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonBackReference
    private Users userId;

    @Column(
            name = "info",
            nullable = false
    )
    private String info;

    @Column(
            name = "currency",
            nullable = false
    )
    private String currency;

    @Column(
            name = "amount"
    )
    private Integer amount;

    @Column(
            name = "rubAmount",
            nullable = false
    )
    private Integer rubAmount;

    @Column(
            name = "status",
            nullable = false
    )
    private String status = "PENDING";


    @Column(name = "orderDate",
            nullable = false,
            updatable = false)
    @CreationTimestamp
    private LocalDateTime orderDate;

    @Column(
            name = "orderUuid",
            nullable = false
    )
    private UUID orderUuid = UUID.randomUUID();



    public Orders(){
    }

    public Orders(OrderRequest request, Users user) {
        this.info = request.getInfo();
        this.currency = request.getCurrency();
        this.amount = request.getAmount();
        this.userId = user;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
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

    public void setRubAmount(Integer rubAmount) {
        this.rubAmount = rubAmount;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public LocalDateTime getOrderDate() {
        return orderDate;
    }


    public UUID getOrderUuid() {
        return orderUuid;
    }


    public void setUserId(Users userId) {
        this.userId = userId;
    }

    public Users getUserId() {
        return userId;
    }

    @Override
    public String toString() {
        return "Orders{" +
                "id=" + id +
                ", user_id=" + userId +
                ", info='" + info + '\'' +
                ", currency='" + currency + '\'' +
                ", amount=" + amount +
                ", rub_amount=" + rubAmount +
                ", status='" + status + '\'' +
                ", order_date=" + orderDate +
                ", order_uuid='" + orderUuid + '\'' +
                '}';
    }
}
