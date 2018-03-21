package pl.edu.uj.dusinski;

import java.util.Map;

import static java.util.Map.entry;

public class CurrencyResolverUtils {

    private CurrencyResolverUtils() {
    }

    private final static Map<String, String> SYMBOL_CURRENCY_MAP = Map.ofEntries(
            entry("Dhs", "AED"),
            entry("KM", "BAM"),
            entry("lv", "BGN"),
            entry("CHF", "CHF"),
            entry("Kč", "CZK"),
            entry("€", "EUR"),
            entry("£", "GBP"),
            entry("GEL", "GEL"),
            entry("kn", "HRK"),
            entry("Ft", "HUF"),
            entry("₪", "ILS"),
            entry("MDL", "MDL"),
            entry("MKD", "MKD"),
            entry("zł", "PLN"),
            entry("lei", "RON"),
            entry("din", "RSD"),
            entry("UAH", "UAH"),
            entry("$", "USD"));

    private final static Map<String, String> FROM_CODE_CURRENCY_MAP = Map.ofEntries(
            entry("BLL", "DKK"),
            entry("CPH", "DKK"),
            entry("AES", "NOK"),
            entry("BGO", "NOK"),
            entry("HAU", "NOK"),
            entry("KRS", "NOK"),
            entry("MOL", "NOK"),
            entry("TRF", "NOK"),
            entry("SVG", "NOK"),
            entry("TOS", "NOK"),
            entry("TRD", "NOK"),
            entry("GOT", "SEK"),
            entry("MMX", "SEK"),
            entry("NYO", "SEK"),
            entry("VXO", "SEK"));

    public static String resolveCurrencyFromSymbol(String symbol, String fromCode) {
        return SYMBOL_CURRENCY_MAP.getOrDefault(symbol, FROM_CODE_CURRENCY_MAP.getOrDefault(fromCode, ""));
    }
}