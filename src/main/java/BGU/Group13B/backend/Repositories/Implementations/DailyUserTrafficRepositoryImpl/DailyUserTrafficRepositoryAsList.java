package BGU.Group13B.backend.Repositories.Implementations.DailyUserTrafficRepositoryImpl;

import BGU.Group13B.backend.Repositories.Interfaces.IDailyUserTrafficRepository;
import BGU.Group13B.backend.System.DailyUserTraffic;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;

public class DailyUserTrafficRepositoryAsList implements IDailyUserTrafficRepository {

    //this list is sorted by date and will contain all the dates from the first day the system was created until today
    private final List<DailyUserTraffic> dailyUserTrafficList = new LinkedList<>();

    public DailyUserTrafficRepositoryAsList(){
        dailyUserTrafficList.add(new DailyUserTraffic(LocalDate.now()));
    }

    private synchronized void fillUpToToday(){
        LocalDate today = LocalDate.now();
        LocalDate lastDate = dailyUserTrafficList.get(0).getDate();
        while(lastDate.isBefore(today)){
            lastDate = lastDate.plusDays(1);
            dailyUserTrafficList.add(0, new DailyUserTraffic(lastDate));
        }
    }

    @Override
    public synchronized void addGuest() {
        fillUpToToday();
        dailyUserTrafficList.get(0).addGuest();
    }

    @Override
    public synchronized void addRegularMember() {
        fillUpToToday();
        var v = dailyUserTrafficList.get(0);
        v.addRegularUser();
        v.removeGuest();
    }

    @Override
    public synchronized void addStoreManagerThatIsNotOwner() {
        fillUpToToday();
        var v = dailyUserTrafficList.get(0);
        v.addStoreManagerThatIsNotOwner();
        v.removeGuest();
    }

    @Override
    public synchronized void addStoreOwner() {
        fillUpToToday();
        var v = dailyUserTrafficList.get(0);
        v.addStoreOwner();
        v.removeGuest();
    }

    @Override
    public synchronized void addAdmin() {
        fillUpToToday();
        var v = dailyUserTrafficList.get(0);
        v.addAdmin();
        v.removeGuest();
    }

    @Override
    public synchronized void reset() {
        dailyUserTrafficList.clear();
        dailyUserTrafficList.add(new DailyUserTraffic(LocalDate.now()));
    }
}
