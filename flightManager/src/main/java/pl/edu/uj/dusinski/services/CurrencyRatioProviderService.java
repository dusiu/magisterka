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
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class CurrencyRatioProviderService implements Function<String, Double> {
    private static final Logger Log = LoggerFactory.getLogger(CurrencyRatioProviderService.class);

    private final RestTemplate restTemplate;
    private final Map<String, Double> currencyRatio = new HashMap<>();
    private final String currencyApiUrl = "https://api.fixer.io/latest?base=PLN";
    private final String yahooCurrencyApiUrl = "https://finance.yahoo.com/webservice/v1/symbols/allcurrencies/quote?format=json";
    private final Gson gson = new Gson();
    private final Set<String> missingCurrencies = Set.of("AED", "BAM", "GEL", "MDL", "MKD", "RSD", "UAH", "MAD");
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
        String currencyJson = restTemplate.getForObject(currencyApiUrl, String.class);
        Map<String, Double> currencyMap = (Map<String, Double>) gson.fromJson(currencyJson, Map.class).get("rates");
        currencyRatio.putAll(currencyMap
                .entrySet()
                .stream()
                .collect(Collectors.toMap(Map.Entry::getKey, v -> 1 / v.getValue())));

        getYahooCurrencies();
        Log.info("Updated {} currencies", currencyRatio.size());
    }

    private void getYahooCurrencies() {
        try {
            YahooCurrency yahooCurrency = gson.fromJson(
                    restTemplate.getForObject(yahooCurrencyApiUrl, String.class), YahooCurrency.class);
            currencyRatio.putAll(getMissingCurrenciesWithPlnRatio(yahooCurrency));
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
        return yahooCurrency.getList().getResources().stream()
                .map(v -> Map.entry(resolveCurrencyName(v.getResource().getFields().getName()),
                        v.getResource().getFields().getPrice()))
                .filter(v -> missingCurrencies.contains(v.getKey()))
                .distinct()
                .map(v -> Map.entry(v.getKey(), convertUsdRatioToPlnRatio(v.getValue())))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    private Double convertUsdRatioToPlnRatio(String usdRatio) {
        return currencyRatio.get("USD") / Double.valueOf(usdRatio);
    }

    private String resolveCurrencyName(String name) {
        return name.replace("USD/", "");
    }

}
