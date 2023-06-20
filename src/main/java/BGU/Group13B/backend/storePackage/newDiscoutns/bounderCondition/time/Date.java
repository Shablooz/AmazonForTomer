package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time;

import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;
@Entity
public class Date implements Comparable<Date>{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(updatable = false)
    private final LocalDateTime date;

    public Date() {
        this.date = null;
    }


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
    public String toString(){
        return date.getDayOfMonth() + "/" + date.getMonth().getValue();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
