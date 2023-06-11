package BGU.Group13B.backend.System;

import java.time.LocalDate;

public class DailyUserTraffic {
    private final LocalDate date;

    private int numOfGuests;
    private int numOfRegularMembers; // members that are not store owners or managers
    private int numOfStoreManagersThatAreNotOwners;
    private int numOfStoreOwners;
    private int numOfAdmins;

    public DailyUserTraffic(LocalDate date) {
        this.date = date;
        this.numOfGuests = 0;
        this.numOfRegularMembers = 0;
        this.numOfStoreManagersThatAreNotOwners = 0;
        this.numOfStoreOwners = 0;
        this.numOfAdmins = 0;
    }

    public LocalDate getDate() {
        return date;
    }

    public int getNumOfGuests() {
        return numOfGuests;
    }

    public int getNumOfRegularMembers() {
        return numOfRegularMembers;
    }

    public int getNumOfStoreManagersThatAreNotOwners() {
        return numOfStoreManagersThatAreNotOwners;
    }

    public int getNumOfStoreOwners() {
        return numOfStoreOwners;
    }

    public int getNumOfAdmins() {
        return numOfAdmins;
    }

    public void addGuest() {
        numOfGuests++;
    }

    public void addRegularUser() {
        numOfRegularMembers++;
    }

    public void addStoreManagerThatIsNotOwner() {
        numOfStoreManagersThatAreNotOwners++;
    }

    public void addStoreOwner() {
        numOfStoreOwners++;
    }

    public void addAdmin() {
        numOfAdmins++;
    }

    public void removeGuest() { //will be used when a guest logs in
        numOfGuests--;
    }


}
