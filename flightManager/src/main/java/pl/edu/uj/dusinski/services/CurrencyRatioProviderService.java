package pl.edu.uj.dusinski.services;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import pl.edu.uj.dusinski.dao.YahooCurrency;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class CurrencyRatioProviderService implements Function<String, Double> {
    private static final Logger Log = LoggerFactory.getLogger(CurrencyRatioProviderService.class);

    private final RestTemplate restTemplate;
    private final Map<String, Double> currencyRatio = new HashMap<>();
    private final String yahooCurrencyApiUrl = "https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json";
    private final Gson gson = new Gson();
    private final int oneDayInMs = 24 * 60 * 60 * 1000;

    @Autowired
    public CurrencyRatioProviderService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Double apply(String currency) {
        return currencyRatio.getOrDefault(currency, 1.0);
    }

    @Scheduled(fixedDelay = oneDayInMs)
    public void updateCurrenciesRatio() {
        Log.info("Updating currency ratio");
        getYahooCurrencies();
        Log.info("Updated {} currencies", currencyRatio.size());
    }

    private void getYahooCurrencies() {
        try {
            YahooCurrency yahooCurrencyResponse = gson.fromJson(
                    restTemplate.getForObject(yahooCurrencyApiUrl, String.class), YahooCurrency.class);
            currencyRatio.putAll(getMissingCurrenciesWithPlnRatio(yahooCurrencyResponse));
        } catch (Exception e) {
            Log.error("Error during getting yahoo currencies, will try again in 10s", e);
            try {
                Thread.sleep(10_000);
            } catch (InterruptedException e1) {
            }
            getYahooCurrencies();
        }
    }

    private Map<String, Double> getMissingCurrenciesWithPlnRatio(YahooCurrency yahooCurrency) {
        Map<String, String> usdRatioMap = yahooCurrency.getList().getResources().stream()
                .filter(v -> v.getResource().getFields().getName() != null)
                .map(v -> Map.entry(resolveCurrencyName(v.getResource().getFields().getName()),
                        v.getResource().getFields().getPrice()))
                .filter(v -> v.getKey().length() == 3)
                .distinct()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        double usdPlnRatio = 1 / Double.valueOf(usdRatioMap.get("PLN"));
        return usdRatioMap.entrySet().stream()
                .map(entry -> Map.entry(entry.getKey(), getPlnInvertRatio(entry.getValue(), usdPlnRatio)))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private double getPlnInvertRatio(String usdRatio, double usdPlnRatio) {
        return 1 / (Double.valueOf(usdRatio) * usdPlnRatio);
    }

    private Double convertUsdRatioToPlnRatio(String usdRatio) {
        return currencyRatio.get("USD") / Double.valueOf(usdRatio);
    }

    private String resolveCurrencyName(String name) {
        return name.replace("USD/", "");
    }

}
