package dam.projects.projectdam.helpers;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by Renato on 20/05/2016 : 18:16.
 * Class with helpful methods to manipulate date and time objects.
 */
public class HelpersDate {
    public static final DateTimeFormatter HOUR_FORMAT_SMALL = DateTimeFormat.forPattern("HH:mm");
    public static final DateTimeFormatter HOUR_FORMAT_BIG = DateTimeFormat.forPattern("HH:mm:ss");
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd");
    public static final DateTimeFormatter DATETIME_FORMAT = DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss");

    public static String hourToString(LocalTime hour, DateTimeFormatter formatter) {
        return hour.toString(formatter);
    }

    public static String dateToString(LocalDate date, DateTimeFormatter formatter){
        return date.toString(formatter);
    }

    public static LocalTime dateStringToHour(String datetime) {
        /* improve */
        String[] dateTimeTemp = datetime.split(" ");
        String[] parcels = dateTimeTemp[1].split(":");
        if (parcels.length == 2) {
            return new LocalTime(Integer.parseInt(parcels[0]), Integer.parseInt(parcels[1]));
        } else if (parcels.length == 3) {
            return new LocalTime(Integer.parseInt(parcels[0]), Integer.parseInt(parcels[1]), Integer.parseInt(parcels[2]));
        }
        return null;
    }

    public static LocalTime stringToHour(String hour) {
        /* improve */
        String[] parcels = hour.split(":");
        if (parcels.length == 2) {
            return new LocalTime(Integer.parseInt(parcels[0]), Integer.parseInt(parcels[1]));
        } else if (parcels.length == 3) {
            return new LocalTime(Integer.parseInt(parcels[0]), Integer.parseInt(parcels[1]), Integer.parseInt(parcels[2]));
        }
        return null;
    }

    public static LocalDate dateStringToDate(String datetime){
        String[] dateTimeTemp = datetime.split(" ");
        String[] parcels = dateTimeTemp[0].split("-");
        if (parcels.length == 3) {
            return new LocalDate(Integer.parseInt(parcels[0]), Integer.parseInt(parcels[1]), Integer.parseInt(parcels[2]));
        }
        return null;
    }

    public static LocalDate stringToDate(String date){
        String[] parcels = date.split("-");
        if (parcels.length == 3) {
            return new LocalDate(Integer.parseInt(parcels[0]), Integer.parseInt(parcels[1]), Integer.parseInt(parcels[2]));
        }
        return null;
    }

    public static DateTime stringToDateTimeUPT(String datetime) {
        try {
            String[] dateTimeTemp = datetime.split(" ");
            String[] parcels = dateTimeTemp[0].split("-");
            String[] parcelsTime = dateTimeTemp[1].split(":");
            if (parcels.length == 3) {
                return new DateTime(Integer.parseInt(parcels[0]),
                        Integer.parseInt(parcels[1]),
                        Integer.parseInt(parcels[2]),
                        Integer.parseInt(parcelsTime[0]),
                        Integer.parseInt(parcelsTime[1]));
            }
            return null;
        } catch (Exception e) {
            return null;
        }
    }

}
