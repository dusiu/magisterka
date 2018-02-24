package pl.edu.uj.dusinski.dao;

public enum Airline {

    WIZZIAR("wizziar");

    private final String value;

    Airline(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
