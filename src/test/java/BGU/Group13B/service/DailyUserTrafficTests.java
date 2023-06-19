package BGU.Group13B.service;

import BGU.Group13B.backend.Repositories.Interfaces.IDailyUserTrafficRepository;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DailyUserTrafficTests extends ProjectTest{
    private int baseGuests;
    private int baseRegularMembers;
    private int baseStoreManagersThatAreNotOwners;
    private int baseStoreOwners;
    private int baseAdmins;

    private void setupBaseTraffic() {
        var baseTraffic = handleResponse(session.getUserTrafficOfRange(userIds[UsersIndex.ADMIN.ordinal()], LocalDate.now(), LocalDate.now()));
        baseGuests = baseTraffic.numOfGuests();
        baseRegularMembers = baseTraffic.numOfRegularMembers();
        baseStoreManagersThatAreNotOwners = baseTraffic.numOfStoreManagersThatAreNotOwners();
        baseStoreOwners = baseTraffic.numOfStoreOwners();
        baseAdmins = baseTraffic.numOfAdmins();
    }

    @Test
    public void testAddGuest() {
        setupBaseTraffic();
        session.enterAsGuest();
        var traffic = handleResponse(session.getUserTrafficOfRange(userIds[UsersIndex.ADMIN.ordinal()], LocalDate.now(), LocalDate.now()));
        assertEquals(baseGuests + 1, traffic.numOfGuests());
        assertEquals(baseRegularMembers, traffic.numOfRegularMembers());
        assertEquals(baseStoreManagersThatAreNotOwners, traffic.numOfStoreManagersThatAreNotOwners());
        assertEquals(baseStoreOwners, traffic.numOfStoreOwners());
        assertEquals(baseAdmins, traffic.numOfAdmins());
    }

    @Test
    public void testAddMultipleGuests(){
        setupBaseTraffic();
        session.enterAsGuest();
        session.enterAsGuest();
        session.enterAsGuest();
        var traffic = handleResponse(session.getUserTrafficOfRange(userIds[UsersIndex.ADMIN.ordinal()], LocalDate.now(), LocalDate.now()));
        assertEquals(baseGuests + 3, traffic.numOfGuests());
        assertEquals(baseRegularMembers, traffic.numOfRegularMembers());
        assertEquals(baseStoreManagersThatAreNotOwners, traffic.numOfStoreManagersThatAreNotOwners());
        assertEquals(baseStoreOwners, traffic.numOfStoreOwners());
        assertEquals(baseAdmins, traffic.numOfAdmins());
    }

    @Test
    public void testAddRegularMember() {
        setupBaseTraffic();
        int id = session.enterAsGuest();
        String username = "UserName12342";
        String password = "Password12342";
        session.register(id, username, password, "test@gmail.com", "", "", "", LocalDateTime.now());
        session.login(id, username, password, "", "", "");
        var traffic = handleResponse(session.getUserTrafficOfRange(userIds[UsersIndex.ADMIN.ordinal()], LocalDate.now(), LocalDate.now()));
        assertEquals(baseGuests, traffic.numOfGuests());
        assertEquals(baseRegularMembers + 1, traffic.numOfRegularMembers());
        assertEquals(baseStoreManagersThatAreNotOwners, traffic.numOfStoreManagersThatAreNotOwners());
        assertEquals(baseStoreOwners, traffic.numOfStoreOwners());
        assertEquals(baseAdmins, traffic.numOfAdmins());
    }

    @Test
    public void testAddMultipleRegularMembers() {
        setupBaseTraffic();
        for (int i = 0; i < 5; i++) {
            int id = session.enterAsGuest();
            String username = "UserName12342" + id;
            String password = "Password12342" + id;
            session.register(id, username, password, "test" + id + "@gmail.com", "", "", "", LocalDateTime.now());
            session.login(id, username, password, "", "", "");
        }
        var traffic = handleResponse(session.getUserTrafficOfRange(userIds[UsersIndex.ADMIN.ordinal()], LocalDate.now(), LocalDate.now()));
        assertEquals(baseGuests, traffic.numOfGuests());
        assertEquals(baseRegularMembers + 5, traffic.numOfRegularMembers());
        assertEquals(baseStoreManagersThatAreNotOwners, traffic.numOfStoreManagersThatAreNotOwners());
        assertEquals(baseStoreOwners, traffic.numOfStoreOwners());
        assertEquals(baseAdmins, traffic.numOfAdmins());
    }

    @Test
    public void testNoPermissions_fail(){
        assertTrue(session.getUserTrafficOfRange(userIds[UsersIndex.GUEST.ordinal()], LocalDate.now(), LocalDate.now()).didntSucceed());
    }
}
