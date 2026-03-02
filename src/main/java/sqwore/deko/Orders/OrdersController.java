package sqwore.deko.Orders;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import sqwore.deko.Api.ApiService;
import sqwore.deko.Users.UsersRepository;
import sqwore.deko.Users.Users;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/orders")
public class OrdersController {

    private final UsersRepository usersRepository;
    private final OrdersRepository ordersRepository;
    private final OrderService orderService;


    public OrdersController(UsersRepository usersRepository, OrdersRepository ordersRepository,OrderService orderService){
        this.ordersRepository = ordersRepository;
        this.usersRepository = usersRepository;
        this.orderService = orderService;
    }

    @PostMapping
    public Orders createOrder(@RequestBody OrderRequest orderRequest){
        return orderService.createOrder(orderRequest);
    }

    @GetMapping
    public List<Orders> getAllOrders(){
        return ordersRepository.findAll();
    }
}
