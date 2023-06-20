package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;
@Entity
public class Time implements Comparable<Time> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private LocalDateTime time;

    private Time(LocalDateTime time) {
        this.time = time;
    }

    public Time() {
        this.time = null;
    }

    public static Time of(LocalDateTime time) {
        return new Time(time);
    }

    public LocalDateTime getTime() {
        return time;
    }

    //compare time without date
    @Override
    public int compareTo(Time o) {
        return time.getHour() * 60 + time.getMinute() - o.getTime().getHour() * 60 - o.getTime().getMinute();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return time.getHour() + ":" + time.getMinute();
    }
}
