package pl.edu.uj.dusinski.dao;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

public class FlightDetailsRequest {

    private String fromCode;
    private String toCode;
    private int daysToStay;

    public FlightDetailsRequest() {
    }

    public FlightDetailsRequest(String fromCode, String toCode, int daysToStay) {
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.daysToStay = daysToStay;
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
