package sqwore.deko.Orders;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.jackson.autoconfigure.JacksonProperties;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import sqwore.deko.Api.ApiService;
import sqwore.deko.Users.Users;
import sqwore.deko.Users.UsersRepository;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

@Service
public class OrderService {
    private final OrdersRepository ordersRepository;
    private final UsersRepository usersRepository;
    private final ApiService apiService;


    public OrderService(UsersRepository usersRepository, OrdersRepository ordersRepository, ApiService apiService) {
        this.usersRepository = usersRepository;
        this.ordersRepository = ordersRepository;
        this.apiService = apiService;
    }



    public Orders createOrder(@RequestBody OrderRequest orderRequest){
        Users user = usersRepository.findById(orderRequest.getUserId())
                .orElseThrow(()-> new RuntimeException("User not found"));
        int count = apiService.getRubAmount(orderRequest.getAmount(), orderRequest.getCurrency());
        Orders order = new Orders(orderRequest,user);
        order.setRubAmount(count);
        order = ordersRepository.save(order);
        double usdPrice = apiService.getUsdPrice(count);
        String externalResponse = apiService.creatOrder(usdPrice, order.getOrderUuid().toString(),orderRequest.getInfo());
        if (externalResponse!=null){
            order.setStatus("CREATED");
        }else{
            order.setStatus("FAILED");
        }
        user.calculateCount(user.getOrders());
        usersRepository.save(user);
        return ordersRepository.save(order);
    }
}
