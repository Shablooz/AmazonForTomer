package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time;

import java.time.LocalDateTime;

public class Date implements Comparable<Date>{

    private final LocalDateTime date;



    public LocalDateTime getDate() {
        return date;
    }

    private Date(LocalDateTime date){
        this.date = date;
    }
    public static Date of(LocalDateTime date){
        return new Date(date);
    }

    @Override
    public int compareTo(Date o) {
        return  date.getMonth().getValue() == o.getDate().getMonth().getValue() ?
                date.getDayOfMonth() - o.getDate().getDayOfMonth() :
                date.getMonth().getValue() - o.getDate().getMonth().getValue();
    }
}
