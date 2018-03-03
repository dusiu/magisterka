package pl.edu.uj.dusinski.dao;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FlightDetailsRequest {

    private String fromCode;
    private String toCode;
    private int daysToStay;
    private boolean bothWay;

    public FlightDetailsRequest() {
    }

    @JsonCreator
    public FlightDetailsRequest(@JsonProperty("fromCode") String fromCode,
                                @JsonProperty("toCode") String toCode,
                                @JsonProperty("daysToStay") int daysToStay,
                                @JsonProperty("bothWay") boolean bothWay) {
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.daysToStay = daysToStay;
        this.bothWay = bothWay;
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

    public int getDaysToStay() {
        return daysToStay;
    }

    public void setDaysToStay(int daysToStay) {
        this.daysToStay = daysToStay;
    }

    @Override
    public String toString() {
        return ToStringBuilder.reflectionToString(this, ToStringStyle.JSON_STYLE);
    }
}
