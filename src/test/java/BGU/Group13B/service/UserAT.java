package BGU.Group13B.service;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class UserAT extends ProjectTest {


    @Test
    void joinAsGuest(){
        //we check that the id counter advances and that the user exists in the repository
        Assertions.assertEquals(this.session.enterAsGuest(),7);
        Assertions.assertEquals(this.session.enterAsGuest(),8);
        //the lines below should throw exception if test fails
        try {
            Assertions.assertNotNull(session.getUserName(7));
            Assertions.assertNotNull(session.getUserName(8));
        }catch (Exception e){
            Assertions.fail();
        }
    }

    @Test
    void loginTest(){
        int id = this.session.enterAsGuest();
        session.register(id,"testingname","verySecurePass123","goodmall@gmail.com","ans1","","");
        session.login(id,"testingname","verySecurePass123","ans1","","");
        Assertions.assertTrue(session.isUserLogged(id));
    }

    @Test
    void logoutTest(){
        int id = this.session.enterAsGuest();
        session.register(id,"testingname","verySecurePass123","goodmall@gmail.com","ans1","","");
        session.login(id,"testingname","verySecurePass123","ans1","","");
        session.logout(id);
        Assertions.assertFalse(session.isUserLogged(id));
    }

    @Test
    void registerTest() {
        int id = this.session.enterAsGuest();
        try {
            session.register(id, "testingname", "verySecurePass123", "goodmall@gmail.com", "ans1", "", "");
        } catch (Exception e) {
            Assertions.fail();
        }

        int id2 = this.session.enterAsGuest();
        try {
            session.register(id2, "seconduser", "verySecurePass123", "goo1dma2ll@gmail.com", "ans31", "", "");
        } catch (Exception e) {
            Assertions.fail();
        }
    }
}
