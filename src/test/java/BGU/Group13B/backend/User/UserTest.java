package BGU.Group13B.backend.User;

import BGU.Group13B.backend.Repositories.Interfaces.IUserRepository;
import BGU.Group13B.backend.storePackage.Market;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.util.Assert;

import static org.junit.jupiter.api.Assertions.*;

class UserTest {

    private User user1;
    private User user2;
    private User user3;
    private User user4;
    private User user5;

    private IUserRepository userRepository = SingletonCollection.getUserRepository();

    private final String goodUsername1 = "goodUsername1";
    private final String goodUsername2 = "greatname";
    private final String goodUsername3 = "bestname";


    private final String badUsername1 = "goo)(*&^%$dU%ser#n!ame1";
    private final String badUsername2 = "-+!@#$helloimbad";
    private final String badUsername3 = "awful  name";


    private final String badPassword1 = "ihavenocapslock123";
    private final String badPassword2 = "HELLO";
    private final String badPassword3 = "123458797654321";

    private final String goodPassword1 = "goodPassword1";
    private final String goodPassword2 = "Ookodoo1234";
    private final String goodPassword3 = "ShtrudelEater420";


    private final String goodEmail1 = "eylalozof123@gmail.com";
    private final String goodEmail2 = "eyalthegever@gmail.com";
    private final String goodEmail3 = "eyalisthebest123@gmail.com";

    private final String badEmail1 = "tefsadgvnspoiseropgesrgpoe123542@gmail.com";
    private final String badEmail2 = "hello@gmail.lmao";
    private final String badEmail3 = "a@waaaail.com";


    @BeforeEach
    void setUp() {
        user1 = new User(1);
        user2 = new User(2);
        user3 = new User(3);
        user4 = new User(4);
        user5 = new User(5);
        userRepository.addUser(1,user1);
        userRepository.addUser(2,user2);
        userRepository.addUser(3,user3);
        userRepository.addUser(4,user4);
        userRepository.addUser(5,user5);
    }

    @AfterEach
    void tearDown() {
        userRepository.removeUser(1);
        userRepository.removeUser(2);
        userRepository.removeUser(3);
        userRepository.removeUser(4);
        userRepository.removeUser(5);
    }

    @Test
    void register() {
        //user1
        try {
            user1.register(goodUsername1, goodPassword1, goodEmail1, "", "", "");
        }catch(Exception e){
            Assertions.fail(e.getMessage());
        }

        try {
            user2.register(goodUsername2,goodPassword2,goodEmail2,"","","");
        }catch(Exception e){
            Assertions.fail(e.getMessage());
        }

        try {
            user3.register(goodUsername3,goodPassword3,goodEmail3,"","","");
        }catch(Exception e){
            Assertions.fail(e.getMessage());
        }

        try {
            user4.register(badUsername1,badPassword1,badEmail1,"","","");
            Assertions.fail();
        }catch(Exception e){

        }

        try {
            user5.register(badUsername2,badPassword2,badEmail2,"","","");
            Assertions.fail();
        }catch(Exception e){

        }

        //now lets check that we cant register twice!
        try {
            user1.register(goodUsername1, goodPassword1, goodEmail1, "", "", "");
        }catch(Exception e){
            Assertions.fail();
        }


    }

    @Test
    void login() {
        user1.register(goodUsername1, goodPassword1, goodEmail1, "yellow", "", "");
        user2.register(goodUsername2,goodPassword2,goodEmail2,"","yak","");
        user3.register(goodUsername3,goodPassword3,goodEmail3,"","","");

        user4.register("orangesLove", goodPassword1, "orangeslover@gmail.com", "yellow", "", "");

        try {
            user1.login(goodUsername1, goodPassword1, "yellow", "", "");
        }catch(Exception e){
            Assertions.fail(e.getMessage());
        }

        try {
            user2.login(goodUsername2,goodPassword2,"","yak","");
        }catch(Exception e){
            Assertions.fail(e.getMessage());
        }

        try {
            user3.login(goodUsername3,goodPassword3,"","","");
        }catch(Exception e){
            Assertions.fail(e.getMessage());
        }

        //should fail because he didnt answer the questions
        try {
            user4.login("orangesLove",goodPassword1,"","","");
            Assertions.fail();
        }catch(Exception e){
        }
    }

    @Test
    void logout() {
    }

    @Test
    void testRegister() {
    }

    @Test
    void testLogin() {
    }

    @Test
    void testLogout() {
    }
}