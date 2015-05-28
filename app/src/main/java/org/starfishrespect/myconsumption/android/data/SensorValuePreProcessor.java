package org.starfishrespect.myconsumption.android.data;

import java.util.*;

/**
 * Class that contains useful methods to adapt the data to a given precision
 * S23Y (2015). Licensed under the Apache License, Version 2.0.
 */
public class SensorValuePreProcessor {

    private static final String TAG = "Preprocessor";

    public static List<SensorValue> fitToPrecision(List<SensorValue> data, int precision) {
        if (precision <= 1) {
            return data;
        }
        if (data.size() <= 1) {
            return data;
        }
        List<SensorValue> filtered = new ArrayList<>();
        Collections.sort(data);
        int currentStart = data.get(0).getTimestamp();
        int currentEnd;
        switch (precision) {
            case FrequencyData.DELAY_WEEK: {
                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(((long) currentStart) * 1000);
                calendar.set(Calendar.DAY_OF_WEEK, 1);
                currentStart = (int) (calendar.getTimeInMillis() / 1000);
                calendar.add(Calendar.WEEK_OF_YEAR, 1);
                currentEnd = (int) (calendar.getTimeInMillis() / 1000);
                break;
            }
            case FrequencyData.DELAY_MONTH: {
                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(((long) currentStart) * 1000);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                currentStart = (int) (calendar.getTimeInMillis() / 1000);
                calendar.add(Calendar.MONTH, 1);
                currentEnd = (int) (calendar.getTimeInMillis() / 1000);
                break;
            }
            case FrequencyData.DELAY_YEAR: {
                Calendar calendar = new GregorianCalendar();
                calendar.setTimeInMillis(((long) currentStart) * 1000);
                calendar.set(Calendar.MONTH, Calendar.JANUARY);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                currentStart = (int) (calendar.getTimeInMillis() / 1000);
                calendar.add(Calendar.YEAR, 1);
                currentEnd = (int) (calendar.getTimeInMillis() / 1000);
                break;
            }
            case FrequencyData.DELAY_DAY:
            case FrequencyData.DELAY_HOUR:
            case FrequencyData.DELAY_MINUTE:
            default:
                currentStart -= currentStart % precision;
                currentEnd = currentStart + precision;
                break;
        }

        int i = 0;
        int total = 0;
        int count = 0;
        while (i < data.size()) {
            int timestamp = data.get(i).getTimestamp();
            if (timestamp < currentEnd) {
                total += data.get(i).getValue();
                count++;
                i++;
            } else {
                if (count == 0) {
                    //filtered.add(new SensorValue(currentStart, 0));
                } else {
                    filtered.add(new SensorValue(currentStart, total / count));
                    total = 0;
                    count = 0;
                }
                switch (precision) {
                    case FrequencyData.DELAY_WEEK: {
                        currentStart = currentEnd;
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTimeInMillis(((long) currentStart) * 1000);
                        calendar.set(Calendar.DAY_OF_WEEK, 1);
                        calendar.add(Calendar.WEEK_OF_YEAR, 1);
                        currentEnd = (int) (calendar.getTimeInMillis() / 1000);
                        break;
                    }
                    case FrequencyData.DELAY_MONTH: {
                        currentStart = currentEnd;
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTimeInMillis(((long) currentStart) * 1000);
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.MONTH, 1);
                        currentEnd = (int) (calendar.getTimeInMillis() / 1000);
                        break;
                    }
                    case FrequencyData.DELAY_YEAR: {
                        currentStart = currentEnd;
                        Calendar calendar = new GregorianCalendar();
                        calendar.setTimeInMillis(((long) currentStart) * 1000);
                        calendar.set(Calendar.MONTH, Calendar.JANUARY);
                        calendar.set(Calendar.DAY_OF_MONTH, 1);
                        calendar.add(Calendar.YEAR, 1);
                        currentEnd = (int) (calendar.getTimeInMillis() / 1000);
                        break;
                    }
                    case FrequencyData.DELAY_DAY:
                    case FrequencyData.DELAY_HOUR:
                    case FrequencyData.DELAY_MINUTE:
                    default:
                        currentStart = currentEnd;
                        currentEnd = currentStart + precision;
                        break;

                }
            }
        }
        if (count != 0) {
            filtered.add(new SensorValue(currentStart, total / count));
        }

        return filtered;
    }
}
