package pl.edu.uj.dusinski;

import java.util.Map;

import static java.util.Map.entry;

public class CurrencyResolverUtils {

    private CurrencyResolverUtils() {
    }

    private final static Map<String, String> SYMBOL_CURRENCY_MAP = Map.ofEntries(
            entry("Dhs", "AED"),//dwc/sof
            entry("KM", "BAM"),//sjj/bud
            entry("lv", "BGN"),//boj/bud
            entry("CHF", "CHF"),//bsl/osi
            entry("Kč", "CZK"),//prg/ltn
            entry("€", "EUR"),//vie/tzl
            entry("£", "GBP"),//ltn/boj
            entry("GEL", "GEL"),//kut/ltn
            entry("kn", "HRK"),//osi/bsl
            entry("Ft", "HUF"),//bud/gyd
            entry("₪", "ILS"),//tln/vie
            entry("MDL", "MDL"),//kiv/ath
            entry("MKD", "MKD"),//skp/crl
            entry("zł", "PLN"),//ktw/ltn
            entry("lei", "RON"),//otp/crl
            entry("din", "RSD"),//beg/dtm
            entry("UAH", "UAH"),//lwq/ltn
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