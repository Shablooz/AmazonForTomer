package BGU.Group13B.backend.System;

import com.nimbusds.jose.crypto.RSASSAVerifier;
import jakarta.persistence.*;

import java.time.LocalDate;
@Entity
public class DailyUserTraffic {
    @Id
    private LocalDate date;
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

    public DailyUserTraffic() {
        this.date = null;
        this.numOfAdmins = 420;
        this.numOfGuests = 420;
        this.numOfRegularMembers = 420;
        this.numOfStoreManagersThatAreNotOwners = 420;
        this.numOfStoreOwners = 420;
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


    //should not be used.
    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setNumOfGuests(int numOfGuests) {
        this.numOfGuests = numOfGuests;
    }

    public void setNumOfRegularMembers(int numOfRegularMembers) {
        this.numOfRegularMembers = numOfRegularMembers;
    }

    public void setNumOfStoreManagersThatAreNotOwners(int numOfStoreManagersThatAreNotOwners) {
        this.numOfStoreManagersThatAreNotOwners = numOfStoreManagersThatAreNotOwners;
    }

    public void setNumOfStoreOwners(int numOfStoreOwners) {
        this.numOfStoreOwners = numOfStoreOwners;
    }

    public void setNumOfAdmins(int numOfAdmins) {
        this.numOfAdmins = numOfAdmins;
    }

}
