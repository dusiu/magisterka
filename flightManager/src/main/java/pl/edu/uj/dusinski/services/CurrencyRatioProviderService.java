package pl.edu.uj.dusinski.services;

import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class CurrencyRatioProviderService implements Function<String, Double> {

    private final RestTemplate restTemplate;
    private final Map<String, Double> currencyRatio = new HashMap<>();
    private final String currencyApiUrl = "https://api.fixer.io/latest?base=PLN";
    private final Gson gson = new Gson();

    @Autowired
    public CurrencyRatioProviderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Scheduled(fixedDelay = 24 * 60 * 60 * 1000)
    public void updateCurrenciesRatio() {
        String currencyJson = restTemplate.getForObject(currencyApiUrl, String.class);
        Map<String, Double> currencyMap = (Map<String, Double>) gson.fromJson(currencyJson, Map.class).get("rates");
        currencyRatio.putAll(currencyMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> 1 / v.getValue())));
    }

    @Override
    public Double apply(String currency) {
//        todo find all needed currencies
        return currencyRatio.getOrDefault(currency, 1.0);
    }

}
