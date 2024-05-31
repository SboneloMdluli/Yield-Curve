package service;

import exception.YieldCurveException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class YieldCurve {

  private final Map<Long, List<Double>> entries = new HashMap<>();

  LocalDate startDate;

  long frequency = 0;

  /**
   * Put.
   *
   * @param date the date
   * @param value the value
   */
  public void put(LocalDate date, List<Double> value) {
    if (entries.isEmpty()) {
      this.startDate = date;
    }
    if (entries.size() == 1) {
      this.frequency = ChronoUnit.DAYS.between(startDate, date);
    }
    this.entries.put(entries.size() * frequency, value);
  }

  /**
   * Gets number of days from first date.
   *
   * @param date the date
   * @return the number of days
   */
  public long getNumberOfDays(LocalDate date) {
    return ChronoUnit.DAYS.between(startDate, date);
  }

  /**
   * Next lowest key after the request.
   *
   * @param request request
   * @return key
   */
  public long lowerKey(long request) {
    return (long) Math.floor(request / (double) frequency) * frequency;
  }

  /**
   * Next greatest key after the request.
   *
   * @param request request
   * @return key
   */
  public long higherKey(long request) {
    return lowerKey(request) + frequency;
  }

  /**
   * Flat interpolation. Returns the last value in the dataset from a level.
   *
   * @param request the date
   * @param level level
   * @return key
   */
  private double requestBeyondLastDate(Long request, Level level) {
    return switch (level) {
      case BID -> entries.get(request).getFirst();
      case ASK -> entries.get(request).getLast();
      default ->
          0.5 * (entries.get(request).getLast() - entries.get(request).getFirst())
              + entries.get(request).getFirst();
    };
  }

  /**
   * Get rate.
   *
   * @param request request
   * @param level level
   * @return interpolated rate
   * @throws YieldCurveException Exception for yield curve operations
   */
  public double getRate(LocalDate request, Level level) throws YieldCurveException {

    long requestDay = getNumberOfDays(request);
    // early exit if request > last date in dataset
    if (requestDay > (entries.size() - 1) * frequency) {
      requestDay = (entries.size() - 1) * frequency;
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
          (double) start,
          0.5 * (startValues.getLast() - startValues.getFirst()) + startValues.getFirst()
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

  /**
   * Perform linear interpolation from p0 to p1;
   *
   * @param p0 first coordinate
   * @param p1 last coordinate
   * @return interpolated value
   */
  private static double interpolate(List<Double> p0, List<Double> p1, long x) {
    double interpolationValue =
        p0.getLast()
            + (p1.getLast() - p0.getLast()) / (p1.getFirst() - p0.getFirst()) * (x - p0.getFirst());

    return Math.round(interpolationValue * 100.0) / 100.0;
  }
}
