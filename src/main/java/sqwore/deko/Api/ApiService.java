package sqwore.deko.Api;

import org.hibernate.jdbc.Expectation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;
@Service
public class ApiService {
    private final WebClient webClient;

    @Value("${external.api.email}")
    private String apiEmail;

    @Value("@{external.api.password}")
    private String apiPassword;

    public ApiService(WebClient webClient) {
        this.webClient = webClient;
    }


    public String getToken(String login,String password){
        var data = new HashMap<String, String>();
        data.put("email",login);
        data.put("password",password);
        String response = webClient.post()
                .uri("https://api.ns.gifts/api/v1/get_token" )
                .bodyValue(data)
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException("External API error: " + errorBody)))
                )
                .bodyToMono(String.class)
                .block();
        ObjectMapper mapper = new ObjectMapper();
        Map<String, Object> jsonMap = mapper.readValue(response, new TypeReference<Map<String, Object>>() {});
        String token = (String) jsonMap.get("access_token");
        if (response == null || token == null){
            throw new RuntimeException("Token not received from API");
        }
        System.out.println("токен - "+token);
        return token;
    }

    public String authorization(String url,HashMap<String,Object> data){
        String token = getToken("xfcewq","UlJV7Obzt2");
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization","Bearer "+token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        String response = webClient.post()
                .uri(url.trim())
                .headers(httpHeaders -> httpHeaders.addAll(headers))
                .bodyValue(data != null ? data : new HashMap<>())
                .retrieve()
                .onStatus(HttpStatusCode::isError, clientResponse ->
                        clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> Mono.error(new RuntimeException(
                                        "External API error: " + clientResponse.statusCode() + " - " + errorBody)))
                )
                .bodyToMono(String.class)
                .blockOptional()
                .orElse("No response body");
        System.out.println(response);
        return response;
    }

    public String checkBalance(){
        String url = "https://api.ns.gifts/api/v1/check_balance";
        String response = authorization(url, null);
        return response;
    }

    public String creatOrder(Double count,String custom_id,String steam_login){
        Integer serviceId = 1;
        String url = "https://api.ns.gifts/api/v1/create_order";
        var data  = new HashMap<String,Object>();
        data.put("service_id",serviceId);
        data.put("quantity",count);
        data.put("custom_id",custom_id);
        data.put("data",steam_login);

        try {
            String response = authorization(url, data);
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);

            JsonNode customId = json.path("custom_id");
            if (customId.isMissingNode() || customId.isNull()) {
                return "custom_id not found";
            }
            return customId.asString();

        } catch (RuntimeException e) {
            return e.getMessage();
        } catch (Exception e) {
            return "Error: " + e.getMessage();
        }

    }

    public String payOrder(String customId){
        String url = "https://api.ns.gifts/api/v1/pay_order";
        var data = new HashMap<String,Object>();
        data.put("custom_id",customId);
        String response = authorization(url,data);
        return response;
    }

     public Double getUsdPrice(Integer count){
         String url = "https://api.ns.gifts/api/v1/steam/get_amount";
         var data = new HashMap<String,Object>();
         data.put("amount",count);
         String response = authorization(url,data);
         ObjectMapper mapper = new ObjectMapper();
         JsonNode json = mapper.readTree(response);
         JsonNode amount = json.path("usd_price");
        return amount.asDouble();
     }
     @Cacheable(value = "currencyRates", key = "#currency")
     public Integer getRubAmount(Integer count, String currency){
        if (count == null || count<=0){
            return 0;
        }
        String url = "https://api.ns.gifts/api/v1/steam/get_currency_rate";
        String response = authorization(url,null);
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);
            String rateKey = currency.toLowerCase() + "/usd";
            double rate = json.path(rateKey).asDouble();
            double usdPrice = count / rate;
            double rubAmount = usdPrice*json.path("rub/usd").asDouble();
            return (int) Math.round(rubAmount);

        } catch (Exception e){
            System.err.println("Ошибка при получении курса: " + e.getMessage());
            return 0;
        }
     }
}
