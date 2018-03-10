package pl.edu.uj.dusinski.dao;

public class EnrichedFlightDetailsGroup {
    private final EnrichedFlightDetails from;
    private final EnrichedFlightDetails to;
    private final double totalPlnPrice;

    public EnrichedFlightDetailsGroup(EnrichedFlightDetails from,
                                      EnrichedFlightDetails to) {
        this.from = from;
        this.to = to;
        this.totalPlnPrice = from.getPlnPrice() + to.getPlnPrice();
    }

    public EnrichedFlightDetails getFrom() {
        return from;
    }

    public EnrichedFlightDetails getTo() {
        return to;
    }

    public double getTotalPlnPrice() {
        return totalPlnPrice;
    }
}
