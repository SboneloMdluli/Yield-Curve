package test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import exception.YieldCurveException;
import java.time.LocalDate;
import java.util.Arrays;
import org.junit.jupiter.api.*;
import service.Level;
import service.YieldCurve;

class YieldCurveTest {
  static YieldCurve yieldCurve = new YieldCurve();

  @BeforeAll
  public static void setup() {
    LocalDate date1 = LocalDate.of(2024, 5, 17);
    LocalDate date2 = LocalDate.of(2024, 8, 15);
    LocalDate date3 = LocalDate.of(2024, 11, 13);

    yieldCurve.put(date1, Arrays.asList(9.0, 9.05));
    yieldCurve.put(date2, Arrays.asList(10.1, 10.05));
    yieldCurve.put(date3, Arrays.asList(11.30, 11.35));
  }

  @Test
  @DisplayName("Date is before the first date of the yield curve")
  void getRateTestException() {
    Throwable exception =
        assertThrows(
            YieldCurveException.class,
            () -> yieldCurve.getRate(LocalDate.of(2024, 5, 16), Level.ASK));
    assertEquals("Request date is before the smallest date in dataset", exception.getMessage());
  }

  @Test
  @DisplayName("Flat interpolation")
  void getRateTestFlatInterpolation() throws YieldCurveException {
    assertEquals(11.3, yieldCurve.getRate(LocalDate.of(2024, 12, 16), Level.BID));
  }

  @Test
  @DisplayName("Interpolate mid rate")
  void getRateTestMidInterpolation() throws YieldCurveException {
    assertEquals(10.38, yieldCurve.getRate(LocalDate.of(2024, 9, 6), Level.MID));
  }

  @Test
  @DisplayName("Interpolate ask rate")
  void getRateInterpolationAsk() throws YieldCurveException {
    assertEquals(10.37, yieldCurve.getRate(LocalDate.of(2024, 9, 6), Level.ASK));
  }

  @Test
  @DisplayName("Interpolate bid rate")
  void getRateTestInterpolationBid() throws YieldCurveException {
    assertEquals(10.39, yieldCurve.getRate(LocalDate.of(2024, 9, 6), Level.BID));
  }
}
