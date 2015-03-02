package org.starfishrespect.myconsumption.android.data;

import java.io.Serializable;

import biz.manex.sr.myconsumption.api.dto.ExtremumDTO;
import biz.manex.sr.myconsumption.api.dto.Period;

/**
 * Created by thibaud on 02.03.15.
 */
public class Stat implements Serializable {
    private Period period;

    private ExtremumDTO min; // cf java
    private ExtremumDTO max;
    private Integer average;
    private Integer consumption;
    private Integer diffLastTwo;


    public Stat(Period p) {period = p;}

    public Period getPeriod() {
        return period;
    }

    public ExtremumDTO getMin() {
        return min;
    }

    public void setMin(ExtremumDTO min) {
        this.min = min;
    }

    public ExtremumDTO getMax() {
        return max;
    }

    public void setMax(ExtremumDTO max) {
        this.max = max;
    }

    public Integer getAverage() {
        return average;
    }

    public void setAverage(Integer average) {
        this.average = average;
    }

    public Integer getConsumption() {
        return consumption;
    }

    public void setConsumption(Integer consumption) {
        this.consumption = consumption;
    }

    public Integer getDiffLastTwo() {
        return diffLastTwo;
    }

    public void setDiffLastTwo(Integer diffLastTwo) {
        this.diffLastTwo = diffLastTwo;
    }
}
