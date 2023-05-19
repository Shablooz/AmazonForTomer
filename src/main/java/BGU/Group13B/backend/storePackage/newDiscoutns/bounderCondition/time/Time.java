package BGU.Group13B.backend.storePackage.newDiscoutns.bounderCondition.time;

import java.time.LocalDateTime;

public class Time implements Comparable<Time> {

    private final LocalDateTime time;

    private Time(LocalDateTime time) {
        this.time = time;
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

    @Override
    public String toString() {
        return time.getHour() + ":" + time.getMinute();
    }
}
