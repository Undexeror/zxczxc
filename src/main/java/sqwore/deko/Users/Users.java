package sqwore.deko.Users;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import org.jspecify.annotations.Nullable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import sqwore.deko.Orders.Orders;
import sqwore.deko.Tokens.Token;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Entity(name = "users")
@Table(
        name = "users"
)
public class Users implements UserDetails {

    @Id
    @Column(
            name = "id",
            updatable = false
    )
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(
            name = "username",
            unique = true,
            nullable = false,
            columnDefinition = "TEXT"
    )
    private String username;
    @Column(
            name = "count",
            nullable = false,
            columnDefinition = "INTEGER DEFAULT 0"

    )
    private Integer count = 0;

    @Column(
            name = "password",
            nullable = false
    )
    private String password;

    @OneToMany(mappedBy = "userId")
    @JsonManagedReference
    private List<Orders> orders = new ArrayList<>();


    @OneToMany(mappedBy = "user")
    @JsonManagedReference
    private List<Token> tokens = new ArrayList<>();

    public Users() {}

    public Users(String username){
        this.username = username;
    }

    public Long getId(){
        return  id;
    }

    public  void setId(Long id){
        this.id = id;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public @Nullable String getPassword() {
        return password;
    }

    @Override
    public String getUsername(){
        return  username;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }

    public  void setUsername(String username){
        this.username = username;
    }
    public Integer getCount(){
        return  count;
    }
    public void setCount(Integer count){this.count = count;}

    public  void calculateCount(List<Orders> orders){
        if (orders == null || orders.isEmpty()){
            this.count = 0;
        }else{
            for (Orders order:orders){
                if (order.getRubAmount()!=null){
                    this.count+=order.getRubAmount();
                }
            }
        }
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<Orders> getOrders() {
        return orders;
    }

    public void setOrders(List<Orders> orders) {
        this.orders = orders;
    }

    @Override
    public String toString(){
        return "Users{"+
                "id="+id+
                ", username="+username+'\''+
                ", count="+count+
                '}';
    }

}
