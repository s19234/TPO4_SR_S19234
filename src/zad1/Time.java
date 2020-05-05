/**
 *
 *  @author Szewczyk Ryszard S19234
 *
 */

package zad1;


import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Locale;

public class Time {
    private static DateTimeFormatter formatter =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm", new Locale("pl"));
    private static SimpleDateFormat simpleDateFormat =
            new SimpleDateFormat("yyyy-MM-dd", new DateFormatSymbols(new Locale("pl")));
    private static Exception exception;

    public static String passed(String from, String to){
        if(from.matches("^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")
                && to.matches("^((19|2[0-9])[0-9]{2})-(0[1-9]|1[012])-(0[1-9]|[12][0-9]|3[01])$")){
            try {
                LocalDate fromDate = LocalDate.parse(from),
                        toDate = LocalDate.parse(to);
                return getLocalDateDiff(fromDate, toDate);
            } catch (Exception ex){
                exception = ex;
            }
        } else {
            try {
                LocalDateTime fromLocalDateTime = LocalDateTime.parse(from, formatter),
                        toLocalDateTime = LocalDateTime.parse(to, formatter);
                return getLocalDateTimeDiff(fromLocalDateTime, toLocalDateTime);
            } catch (Exception ex){
                exception = ex;
            }
        }
        return "**** " + exception.getClass().getName() + ": " + exception.getMessage();
    }

    private static String getLocalDateTimeDiff(LocalDateTime from, LocalDateTime to){
        StringBuilder stringBuilder = new StringBuilder(getTime(from, to, false));
        int period = (int)ChronoUnit.DAYS.between(from, to);
        ZoneId zoneId = ZoneId.of("Europe/Warsaw");
        ZonedDateTime fZonedDateTime = from.atZone(zoneId),
                tZonedDateTime = to.atZone(zoneId).withZoneSameInstant(simpleDateFormat.getTimeZone().toZoneId());

        int hours = (int)ChronoUnit.HOURS.between(fZonedDateTime, tZonedDateTime),
                minutes = (int)ChronoUnit.MINUTES.between(fZonedDateTime, tZonedDateTime);

        stringBuilder.append(append(" - godzin: ", hours, ", minut: ", minutes, "\n"));
        if(period != 0)
            stringBuilder.append(calendar(from.toLocalDate(), to.toLocalDate()));
        return stringBuilder.toString();
    }

    private static String getLocalDateDiff(LocalDate from, LocalDate to){
        StringBuilder stringBuilder = new StringBuilder(getTime(from, to, true));
        int period = (int)ChronoUnit.DAYS.between(from, to);
        if(period != 0)
            stringBuilder.append(calendar(from, to));
        return stringBuilder.toString();
    }

    private static String getTime(Object from, Object to, boolean isSimple){
        StringBuilder stringBuilder = new StringBuilder();
        String fromMonth, fromWeekday, toMonth, toWeekday, tmp1, tmp2;
        int days, fromDay, fromYear, fromHour, fromMinute;
        int toDay, toHour, toMinute, toYear;
        double period;
        double weeks;
        DecimalFormat decimalFormat;

        if(isSimple){
            fromMonth = simpleDateFormat.getDateFormatSymbols().getMonths()[((LocalDate) from).getMonthValue() - 1];
            days = ((LocalDate) from).getDayOfWeek().getValue() + 1;
            if(days > 7)
                days = 1;
            fromWeekday = simpleDateFormat.getDateFormatSymbols().getWeekdays()[days];

            fromDay = ((LocalDate) from).getDayOfMonth();
            toDay = ((LocalDate) to).getDayOfMonth();

            toMonth = simpleDateFormat.getDateFormatSymbols().getMonths()[((LocalDate) to).getMonthValue() - 1];
            days = ((LocalDate) to).getDayOfWeek().getValue() + 1;
            if(days > 7)
                days = 1;
            toWeekday = simpleDateFormat.getDateFormatSymbols().getWeekdays()[days];

            fromYear = ((LocalDate) from).getYear();
            toYear = ((LocalDate) to).getYear();
            stringBuilder.append(append("Od ", fromDay, " ", fromMonth, " ", fromYear, " (", fromWeekday,
                    ") ", "do ", toDay, " ", toMonth, " ", toYear, " (", toWeekday, ")\n"));
            period = (double)ChronoUnit.DAYS.between((LocalDate)from, (LocalDate)to);
            weeks = period / 7.0;
            decimalFormat = new DecimalFormat("####,####.##");
            stringBuilder.append(append(" - mija: ", (int)period, " ", days((int)period), ", tygodni ",
                    decimalFormat.format(weeks).replace(",","."), "\n"));
        } else {
            fromMonth = simpleDateFormat.getDateFormatSymbols().getMonths()[((LocalDateTime) from).getMonthValue() - 1];
            days = ((LocalDateTime) from).getDayOfWeek().getValue() + 1;
            if(days > 7)
                days = 1;
            fromWeekday = simpleDateFormat.getDateFormatSymbols().getWeekdays()[days];
            fromDay = ((LocalDateTime) from).getDayOfMonth();
            toDay = ((LocalDateTime) to).getDayOfMonth();

            toMonth = simpleDateFormat.getDateFormatSymbols().getMonths()[((LocalDateTime) to).getMonthValue() - 1];
            days = ((LocalDateTime) to).getDayOfWeek().getValue() + 1;
            if(days > 7)
                days = 1;
            toWeekday = simpleDateFormat.getDateFormatSymbols().getWeekdays()[days];
            fromYear = ((LocalDateTime) from).getYear();
            fromHour = ((LocalDateTime) from).getHour();
            fromMinute = ((LocalDateTime) from).getMinute();

            toYear = ((LocalDateTime) to).getYear();
            toHour = ((LocalDateTime) to).getHour();
            toMinute = ((LocalDateTime) to).getMinute();

            tmp1 = append("godz: ", fromHour, ":", String.format("%02d", fromMinute));
            tmp2 = append("godz: ", toHour, ":", String.format("%02d", toMinute));
            stringBuilder.append(append("Od ", fromDay, " ", fromMonth, " ", fromYear, " (", fromWeekday,
                    ") ", tmp1, " do ", toDay, " ", toMonth, " ", toYear, " (", toWeekday, ") ", tmp2, "\n"));
            period = (double)ChronoUnit.DAYS.between(((LocalDateTime) from).toLocalDate(), ((LocalDateTime) to).toLocalDate());
            weeks = period / 7.0;
            decimalFormat = new DecimalFormat("####,####.##");
            stringBuilder.append(append(" - mija: ", (int)period, " ", days((int)period), ", tygodni ",
                    decimalFormat.format(weeks).replace(',','.'), "\n"));
        }
        return stringBuilder.toString();
    }

    private static String append(Object... args){
        StringBuilder stringBuilder = new StringBuilder();
        for(Object object : args){
            stringBuilder.append(object);
        }
        return stringBuilder.toString();
    }

    private static String days(int day){
        if(day > 1)
            return "dni";
        return "dzień";
    }

    private static String months(int value){
        String str = String.valueOf(value);
        if(str.length() == 1){
            if(Integer.parseInt(str) == 1)
                return "miesiąc";
            else if(Integer.parseInt(str) > 1 && Integer.parseInt(str) < 5)
                return "miesięce";
            return "miesięcy";
        } else
            return "miesięcy";
    }

    private static String years(int value){
        String string = String.valueOf(value);
        if(string.length() == 1){
            if(Integer.parseInt(string) > 1 && Integer.parseInt(string) < 5)
                return "lata";
            else if(Integer.parseInt(string) < 1 && Integer.parseInt(string) > 5)
                return "lat";
            else
                return "rok";
        } else {
            if(Integer.parseInt(String.valueOf(string.charAt(string.length() - 2))) == 1
                    || Integer.parseInt(String.valueOf(string.charAt(string.length() - 1))) == 1){
                if(Integer.parseInt(String.valueOf(string.charAt(string.length() - 1))) > 2
                        && Integer.parseInt(String.valueOf(string.charAt(string.length() - 1))) < 5)
                    return "lata";
                else
                    return "lat";
            } else {
                if(Integer.parseInt(String.valueOf(string.charAt(string.length() - 1))) > 1
                        && Integer.parseInt(String.valueOf(string.charAt(string.length() - 1))) < 5)
                    return "lata";
                else
                    return "lat";
            }
        }
    }

    private static String calendar(LocalDate from, LocalDate to){
        StringBuilder result = new StringBuilder(" - kalendarzowo: ");

        Period period = Period.between(from, to);
        int years = period.getYears();
        int months = period.getMonths();
        int days = period.getDays();

        if(years != 0){
            result.append(years).append(" ").append(years(years));
        }
        if(months != 0){
            if(years != 0){
                result.append(", ");
            }
            result.append(months).append(" ").append(months(months));
        }
        if(days != 0){
            if(months != 0 || years != 0)
                result.append(", ");
            result.append(days).append(" ").append(days(days));
        }
        return result.toString();
    }
}
