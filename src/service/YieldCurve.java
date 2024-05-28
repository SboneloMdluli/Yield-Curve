package service;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class YieldCurve {

  private final TreeMap<LocalDate, List<Double>> entries = new TreeMap<>();

  public void put(LocalDate key, List<Double> value) {
    entries.put(key, value);
  }

  private double requestBeyondLastDate(LocalDate date,Level level){
      switch (level){
          case BID:
              return  entries.get(date).getFirst();
          case ASK:
              return  entries.get(date).getLast();
          default:
              return 0.5*(entries.get(date).getLast()-entries.get(date).getFirst());
      }
  }

  public double getRate(LocalDate request, Level level) {

      // early exit if request > last date in dataset
      if(request.isAfter(entries.lastKey())){
          request = entries.lastKey();
          return requestBeyondLastDate(request,level);
      }
      Map.Entry<LocalDate, List<Double>> start = entries.lowerEntry(request);
      Map.Entry<LocalDate, List<Double>> end = entries.higherEntry(request);

    List<Double> startValues = start.getValue();
    List<Double> endValues = end.getValue();

    Double x =
        Double.valueOf(ChronoUnit.DAYS.between(start.getKey(), request)); // point to interpolate
    Double endDate =
        Double.valueOf(ChronoUnit.DAYS.between(start.getKey(), end.getKey())); // end date

    switch (level) {
      case BID:
        {
          Double[] arrayP1 = {0.0, startValues.getFirst()}; // x,y point1
          List<Double> point1 = Arrays.asList(arrayP1);

          Double[] arrayP2 = {endDate, endValues.getFirst()}; // x,y point2
          List<Double> point2 = Arrays.asList(arrayP2);

          return interpolate(point1, point2, x);
        }

      case ASK:
        {
          Double[] arrayP1 = {0.0, startValues.getLast()}; // x,y point1
          List<Double> point1 = Arrays.asList(arrayP1);

          Double[] arrayP2 = {endDate, endValues.getLast()}; // x,y point2
          List<Double> point2 = Arrays.asList(arrayP2);

          return interpolate(point1, point2, x);
        }
      default:
        {
          Double[] arrayP1 = {0.0, 0.5 * (startValues.getLast() - startValues.getFirst())}; // x,y point1
          List<Double> point1 = Arrays.asList(arrayP1);

          Double[] arrayP2 = {endDate, 0.5 * (startValues.getLast() - startValues.getFirst())}; // (ASK-bid)/2
          List<Double> point2 = Arrays.asList(arrayP2);

          return interpolate(point1, point2, x);
        }
    }
  }

  private static double interpolate(List<Double> p0, List<Double> p1, double x) {
    return p0.get(1) + (x / p1.get(0)) * (p1.get(1) - p0.get(1));
  }
}
