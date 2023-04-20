package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.User.Message;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class IStoreMessagesRepositoryTest {

    public static IStoreMessagesRepository storeMessagesRepository;
    @BeforeEach
    void setUp() {
        storeMessagesRepository = SingletonCollection.getStoreMessagesRepository();

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void sendMassage() {
      // storeMessagesRepository.sendMassage(Message.constractMessage(),1,"test",1,"test"));
    }

    @Test
    void readUnreadMassage() {
    }

    @Test
    void readReadMassage() {
    }

    @Test
    void markAsRead() {
    }

    @Test
    void refreshOldMassage() {
    }
}