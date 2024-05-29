import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import exception.YieldCurveException;
import service.Level;
import service.YieldCurve;

public class Main {
    public static void main(String[] args) throws YieldCurveException {

        YieldCurve yieldCurve =  new YieldCurve();

        LocalDate date1 = LocalDate.of(2024, 5, 17);
        LocalDate date2 = LocalDate.of(2024, 8, 15);
        LocalDate date3 = LocalDate.of(2024, 11, 13);
        LocalDate date4 = LocalDate.of(2025, 2, 11);
        LocalDate date5 = LocalDate.of(2025, 5, 12);
        LocalDate date6 = LocalDate.of(2025, 8, 10);
        LocalDate date7 = LocalDate.of(2025, 11, 8);
        LocalDate date8 = LocalDate.of(2026, 2, 6);
        LocalDate date9 = LocalDate.of(2026, 5, 7);


        yieldCurve.put(date2, pointFormatter(5.00,5.05)); //key=date ,value x,y point
        yieldCurve.put(date3, pointFormatter(6.00,6.05)); //key=date ,value x,y point
        yieldCurve.put(date4, pointFormatter(7.20,7.25)); //key=date ,value x,y point
        yieldCurve.put(date5, pointFormatter(7.60,7.65)); //key=date ,value x,y point
        yieldCurve.put(date6, pointFormatter(8.10,8.15)); //key=date ,value x,y point
        yieldCurve.put(date7, pointFormatter(9,9.05)); //key=date ,value x,y point
        yieldCurve.put(date8, pointFormatter(10,10.05)); //key=date ,value x,y point
        yieldCurve.put(date9, pointFormatter(11.30,11.35)); //key=date ,value x,y point
        yieldCurve.put(date1, pointFormatter(4.50,4.55)); //key=date ,value x,y point

        LocalDate test = LocalDate.of(2025, 9, 12);
        try {
            System.out.println(yieldCurve.getRate(test,Level.MID));// date and
        } catch (YieldCurveException e) {
            throw new YieldCurveException(e.getMessage());
        }


    }

    public static List<Double> pointFormatter(double BidRate, double AskRate){
        Double[] array = { BidRate, AskRate};
        return Arrays.asList(array);
    }
}


