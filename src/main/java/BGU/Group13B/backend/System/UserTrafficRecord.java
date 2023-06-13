package BGU.Group13B.backend.System;

//record of the number of users in the system at a specific date range
public record UserTrafficRecord(int numOfGuests, int numOfRegularMembers, int numOfStoreManagersThatAreNotOwners, int numOfStoreOwners, int numOfAdmins){
}
