package BGU.Group13B.backend.Repositories.Implementations.DailyUserTrafficRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ConditionRepositoryImpl.ConditionRepositoryAsHashMapService;
import BGU.Group13B.backend.Repositories.Interfaces.IDailyUserTrafficRepository;
import BGU.Group13B.backend.System.DailyUserTraffic;
import BGU.Group13B.backend.System.UserTrafficRecord;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.LinkedList;
import java.util.List;
@Entity
public class DailyUserTrafficRepositoryAsList implements IDailyUserTrafficRepository {

    @Transient
    private boolean saveMode;

    //this list is sorted by date and will contain all the dates from the first day the system was created until today
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = jakarta.persistence.CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "DailyUserTrafficRepositoryAsList_DailyUserTraffic",
            joinColumns = {@JoinColumn(name = "DailyUserTrafficRepositoryAsList_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "date_for_daily", referencedColumnName = "date")})
    private List<DailyUserTraffic> dailyUserTrafficList;

    public DailyUserTrafficRepositoryAsList(){
        dailyUserTrafficList = new LinkedList<>();
        this.saveMode = true;
    }

    private synchronized void fillUpToToday(){
        dailyUserTrafficList.sort((o1, o2) -> o2.getDate().compareTo(o1.getDate()));
        if(dailyUserTrafficList.isEmpty())
            dailyUserTrafficList.add(new DailyUserTraffic(LocalDate.now()));
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
        save();
    }

    @Override
    public synchronized void addRegularMember() {
        fillUpToToday();
        DailyUserTraffic dailyUserTraffic = dailyUserTrafficList.get(0);
        dailyUserTraffic.addRegularUser();
        dailyUserTraffic.removeGuest();
        save();
    }

    @Override
    public synchronized void addStoreManagerThatIsNotOwner() {
        fillUpToToday();
        DailyUserTraffic dailyUserTraffic = dailyUserTrafficList.get(0);
        dailyUserTraffic.addStoreManagerThatIsNotOwner();
        dailyUserTraffic.removeGuest();
        save();
    }

    @Override
    public synchronized void addStoreOwner() {
        fillUpToToday();
        DailyUserTraffic dailyUserTraffic = dailyUserTrafficList.get(0);
        dailyUserTraffic.addStoreOwner();
        dailyUserTraffic.removeGuest();
        save();
    }

    @Override
    public synchronized void addAdmin() {
        fillUpToToday();
        DailyUserTraffic dailyUserTraffic = dailyUserTrafficList.get(0);
        dailyUserTraffic.addAdmin();
        dailyUserTraffic.removeGuest();
        save();
    }

    @Override
    public synchronized UserTrafficRecord getUserTrafficOfRage(LocalDate start, LocalDate end) {
        if(start.isAfter(end))
            throw new IllegalArgumentException("start date must be before end date");

        fillUpToToday();
        int numOfGuests = 0;
        int numOfRegularMembers = 0;
        int numOfStoreManagersThatAreNotOwners = 0;
        int numOfStoreOwners = 0;
        int numOfAdmins = 0;

        for(DailyUserTraffic dailyUserTraffic : dailyUserTrafficList){
            LocalDate date = dailyUserTraffic.getDate();
            if(date.isAfter(end)) continue;
            if(date.isBefore(start)) break;

            numOfGuests += dailyUserTraffic.getNumOfGuests();
            numOfRegularMembers += dailyUserTraffic.getNumOfRegularMembers();
            numOfStoreManagersThatAreNotOwners += dailyUserTraffic.getNumOfStoreManagersThatAreNotOwners();
            numOfStoreOwners += dailyUserTraffic.getNumOfStoreOwners();
            numOfAdmins += dailyUserTraffic.getNumOfAdmins();
        }

        return new UserTrafficRecord(numOfGuests, numOfRegularMembers, numOfStoreManagersThatAreNotOwners, numOfStoreOwners, numOfAdmins);

    }

    @Override
    public synchronized void reset() {
        dailyUserTrafficList.clear();
        dailyUserTrafficList.add(new DailyUserTraffic(LocalDate.now()));
        save();
    }

    @Override
    public void setSaveMode(boolean saveMode) {
        this.saveMode = saveMode;
    }


    private void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(DailyUserTrafficRepositoryAsListService.class).save(this);
    }

    public boolean isSaveMode() {
        return saveMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<DailyUserTraffic> getDailyUserTrafficList() {
        return dailyUserTrafficList;
    }

    public void setDailyUserTrafficList(List<DailyUserTraffic> dailyUserTrafficList) {
        this.dailyUserTrafficList = dailyUserTrafficList;
    }
}
