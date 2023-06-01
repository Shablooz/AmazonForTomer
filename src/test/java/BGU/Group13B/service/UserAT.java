package BGU.Group13B.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

public class UserAT extends ProjectTest {


    @BeforeEach
    void userATsetUp() {
        SingletonCollection.reset_system();
    }
    @Test
    void joinAsGuest(){
        //we check that the id counter advances and that the user exists in the repository
        Assertions.assertEquals(this.session.enterAsGuest(),6);
        Assertions.assertEquals(this.session.enterAsGuest(),7);
        //the lines below should throw exception if test fails
        try {
            Assertions.assertNotNull(session.getUserName(6));
            Assertions.assertNotNull(session.getUserName(7));
        }catch (Exception e){
            Assertions.fail();
        }
    }


    @Test
    void loginTest(){
        int id = this.session.enterAsGuest();
        session.register(id,"TetTesting","verySecurePass123","goodmall@gmail.com","ans1","","", LocalDate.MIN);
        session.login(id,"TetTesting","verySecurePass123","ans1","","");
        Assertions.assertTrue(session.isUserLogged(id));
    }

    @Test
    void logoutTest(){
        int id = this.session.enterAsGuest();
        session.register(id,"testingname","verySecurePass123","goodmall@gmail.com","ans1","","", LocalDate.MIN);
        session.login(id,"testingname","verySecurePass123","ans1","","");
        session.logout(id);
        Assertions.assertFalse(session.isUserLogged(id));
    }

    @Test
    void registerTest() {
        int id = this.session.enterAsGuest();
        try {
            session.register(id, "testingname", "verySecurePass123", "goodmall@gmail.com", "ans1", "", "", LocalDate.MIN);
        } catch (Exception e) {
            Assertions.fail();
        }

        int id2 = this.session.enterAsGuest();
        try {
            session.register(id2, "seconduser", "verySecurePass123", "goo1dma2ll@gmail.com", "ans31", "", "", LocalDate.MIN);
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}
