package service;

import exception.YieldCurveException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class YieldCurve {

    private final Map<Long, List<Double>> entries = new HashMap<>();

    public void put(LocalDate date, List<Double> value) {
        this.entries.put(entries.size() * 90L, value);
    }

    public long getNumberOfDays(LocalDate date) {
        return ChronoUnit.DAYS.between(LocalDate.of(2024, 5, 17), date);
    }

    public long lowerKey(long request) {
        return  (long) Math.floor(request/(double) 90L)*90;
    }

    public long higherKey(long request) {
        return lowerKey(request)+90;
    }


    private double requestBeyondLastDate(Long day, Level level) {
        return switch (level) {
            case BID -> entries.get(day).getFirst();
            case ASK -> entries.get(day).getLast();
            default -> 0.5 * (entries.get(day).getLast() - entries.get(day).getFirst())+entries.get(day).getFirst();
        };
    }

    public double getRate(LocalDate request, Level level) throws YieldCurveException {

        long requestDay = getNumberOfDays(request);
        // early exit if request > last date in dataset
        if (requestDay > (entries.size()-1) * 90L) {
            requestDay = (entries.size()-1) * 90L;
            return requestBeyondLastDate(requestDay, level);
        }

        // throw request if date is before the first date
        if (requestDay < 0) {
            throw new YieldCurveException("Request date is before the smallest date in dataset");
        }

        return interpolateOnRequest(requestDay, level);
    }

    private double interpolateOnRequest(long requestDay, Level level) {
        long start = lowerKey(requestDay);
        long end = higherKey(requestDay);
        List<Double> startValues = entries.get(start);
        List<Double> endValues = entries.get(end);

        switch (level) {
            case BID -> {
                // x,y point1
                Double[] arrayP1 = {(double) start, startValues.getFirst()};
                List<Double> point1 = Arrays.asList(arrayP1);

                // x,y point2
                Double[] arrayP2 = {(double) end, endValues.getFirst()};
                List<Double> point2 = Arrays.asList(arrayP2);

                return interpolate(point1, point2, requestDay);
            }
            case ASK -> {
                // x,y point1
                Double[] arrayP1 = {(double) start, startValues.getLast()};
                List<Double> point1 = Arrays.asList(arrayP1);

                // x,y point2
                Double[] arrayP2 = {(double) end, endValues.getLast()};
                List<Double> point2 = Arrays.asList(arrayP2);

                return interpolate(point1, point2, requestDay);
            }
            default -> {
                Double[] arrayP1 = {
                        (double) start, 0.5 * (startValues.getLast() - startValues.getFirst()) + startValues.getFirst()
                }; // x,y point1
                List<Double> point1 = Arrays.asList(arrayP1);

                Double[] arrayP2 = {
                        (double) end, 0.5 * (endValues.getLast() - endValues.getFirst()) + endValues.getFirst()
                }; // (ASK-bid)/2
                List<Double> point2 = Arrays.asList(arrayP2);

                return interpolate(point1, point2, requestDay);
            }
        }
    }

    private static double interpolate(List<Double> p0, List<Double> p1, long x) {
        return p0.getLast() + ( p1.getLast() - p0.getLast())/(p1.getFirst()-p0.getFirst())*(x-p0.getFirst());
    }
}

