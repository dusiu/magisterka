package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FlightDetailsRequest {

    private String fromCode;
    private String toCode;
    private int minDaysToStay;
    private int maxDaysToStay;
    private boolean bothWay;

    private FlightDetailsRequest() {
    }

    public static FlightDetailsRequest createNewInstance() {
        return new FlightDetailsRequest();
    }

    @JsonCreator
    public FlightDetailsRequest(@JsonProperty("fromCode") String fromCode,
                                @JsonProperty("toCode") String toCode,
                                @JsonProperty("minDaysToStay") int minDaysToStay,
                                @JsonProperty("maxDaysToStay") int maxDaysToStay,
                                @JsonProperty("bothWay") boolean bothWay) {
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.minDaysToStay = minDaysToStay;
        this.bothWay = bothWay;
        this.maxDaysToStay = maxDaysToStay;
    }

    public boolean isBothWay() {
        return bothWay;
    }

    public void setBothWay(boolean bothWay) {
        this.bothWay = bothWay;
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getToCode() {
        return toCode;
    }

    public void setToCode(String toCode) {
        this.toCode = toCode;
    }

    public int getMinDaysToStay() {
        return minDaysToStay;
    }

    public void setMinDaysToStay(int minDaysToStay) {
        this.minDaysToStay = minDaysToStay;
    }

    public void setMaxDaysToStay(int maxDaysToStay) {
        this.maxDaysToStay = maxDaysToStay;
    }

    public int getMaxDaysToStay() {
        return maxDaysToStay;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
