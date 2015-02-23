package org.starfishrespect.myconsumption.android.data;

import java.util.Date;

/**
 * Used to store data that will be displayed on a spinner to select dates
 */
public class SpinnerDateData {
    private Date date;
    private String strValue;

    public SpinnerDateData(Date date, String strValue) {
        this.date = date;
        this.strValue = strValue;
    }

    public Date getDate() {
        return date;
    }

    public String getStrValue() {
        return strValue;
    }
}
