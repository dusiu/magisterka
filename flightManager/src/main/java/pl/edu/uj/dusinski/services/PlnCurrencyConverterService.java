package pl.edu.uj.dusinski.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.function.BiFunction;

@Service
public class PlnCurrencyConverterService implements BiFunction<String, Double, Double> {

    private final CurrencyRatioProviderService currencyRatioProviderService;

    @Autowired
    public PlnCurrencyConverterService(CurrencyRatioProviderService currencyRatioProviderService) {
        this.currencyRatioProviderService = currencyRatioProviderService;
    }

    @Override
    public Double apply(String currency, Double price) {
//        todo finish this
        return price;
    }
}
