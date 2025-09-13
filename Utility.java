import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class Utility {

    public LocalDate parseDate(String stringDate)
    {
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try{
            LocalDate date = LocalDate.parse(stringDate, format);
            return date;
        }
        catch(DateTimeParseException e){
            return null;
        }
    }
    public LocalTime parseTime(String stringTime){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss");
        try{
            LocalTime time = LocalTime.parse(stringTime, formatter);
            return time;
        }
        catch(Exception e){
            return null;
        }
    }
    public LocalDateTime parseDateTime(String dt){
        if (dt == null) {
            return null;
        }
        String[] arr = dt.split("T");
        if (arr.length != 2){
            return null;
        }  
        LocalDate d = parseDate(arr[0]);
        LocalTime t = parseTime(arr[1]);
        if(d == null || t == null){
            return null;
        }
        return LocalDateTime.of(d,t);
        }
}
