package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl.MessageRepositorySingle;
import BGU.Group13B.backend.User.Message;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.HashSet;

import static org.junit.jupiter.api.Assertions.*;

class IMessageRepositoryTest {

    static IMessageRepository messageRepository;

    //start of helper functions
    interface MyRunnableFactory {
        public Runnable createRunnable(int[] integers, char[] chars, String[] strings);
    }

    private void startAndWait(Thread[] threads) {
        for (Thread value : threads) value.start();
        for (Thread thread : threads)
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private Message[] constructMessages(int num, String receiverId) {
        Message[] messages = new Message[num];
        for (int i = 1; i <= num; i++) {
            String s = "Test" + i;
            messages[i - 1] = Message.constractMessage(s, i, s, s, receiverId == null ? s : receiverId);
        }
        return messages;
    }

    //end of helper functions

    @BeforeEach
    void setUp() {
        messageRepository = new MessageRepositorySingle(false);
    }

    @AfterEach
    void tearDown() {
    }

    @RepeatedTest(15)
    void sendMassage_ManyToMany() {
        Message[] messages = constructMessages(10, null);

        MyRunnableFactory senderFactory = (integers, chars, strings) -> () -> {
            messageRepository.sendMassage(messages[integers[0]]);
        };
        MyRunnableFactory readerFactory = (integers, chars, strings) -> () -> {
            Message message = messageRepository.readUnreadMassage(messages[integers[0]].getReceiverId());
            assertEquals(messages[integers[0]], message);
        };

        Thread[] senders = new Thread[10];
        Thread[] readers = new Thread[10];
        for (int i = 0; i < 10; i++) {
            int[] integers = {i};
            senders[i] = new Thread(senderFactory.createRunnable(integers, null, null));
            readers[i] = new Thread(readerFactory.createRunnable(integers, null, null));
        }
        startAndWait(senders);
        startAndWait(readers);
    }

    @RepeatedTest(15)
    void sendMassage_ManyToOne() {
        Message[] messages = constructMessages(10, "1");

        MyRunnableFactory senderFactory = (integers, chars, strings) -> () -> {
            messageRepository.sendMassage(messages[integers[0]]);
        };


        Thread[] senders = new Thread[10];

        for (int i = 0; i < 10; i++) {
            int[] integers = {i};
            senders[i] = new Thread(senderFactory.createRunnable(integers, null, null));

        }
        startAndWait(senders);
        HashSet<Message> messagesSet = new HashSet<>();
        for (int i = 0; i < messages.length; i++) {
            Message message = messageRepository.readUnreadMassage("1");
            assertFalse(messagesSet.contains(message));
            messagesSet.add(message);
            messageRepository.markAsRead(message.getReceiverId(), message.getSenderId(), message.getMessageId());
        }
        assertEquals(10, messagesSet.size());
    }

    @RepeatedTest(15)
    void readUnreadMassage() {
        Message[] messages = constructMessages(10, "1");

        for (int i = 0; i < 10; i++) {
            messageRepository.sendMassage(messages[i]);
        }
        Thread[] readers = new Thread[10];
        MyRunnableFactory readerFactory = (integers, chars, strings) -> () -> {
            for (int i = 0; i < messages.length; i++) {
                try {
                    Message message = messageRepository.readUnreadMassage(strings[0]);
                    messageRepository.markAsRead(message.getReceiverId(), message.getSenderId(), message.getMessageId());
                } catch (Exception ignored) {
                }
            }
            assertThrows(Exception.class, () -> messageRepository.readUnreadMassage(strings[0]));
        };

        for (int i = 0; i < 10; i++) {
            readers[i] = new Thread(readerFactory.createRunnable(null, null, new String[]{"1"}));
        }
        startAndWait(readers);
    }

    @RepeatedTest(15)
    void readReadMassage() {
        Message[] messages = constructMessages(10, "1");

        for (int i = 0; i < 10; i++) {
            messageRepository.sendMassage(messages[i]);
            Message message = messageRepository.readUnreadMassage("1");
            messageRepository.markAsRead(message.getReceiverId(), message.getSenderId(), message.getMessageId());
        }
        for (int i = 0; i < messages.length; i++) {
            messageRepository.readReadMassage("1");
        }
        assertThrows(Exception.class, () -> messageRepository.readReadMassage("1"));

    }

    @RepeatedTest(15)
    void markAsRead() {
        Message[] messages = constructMessages(10, "1");

        for (int i = 0; i < 10; i++) {
            messageRepository.sendMassage(messages[i]);
            Message message = messageRepository.readUnreadMassage("1");
            messageRepository.markAsRead(message.getReceiverId(), message.getSenderId(), message.getMessageId());
        }
        for (int i = 0; i < messages.length; i++) {
            messageRepository.readReadMassage("1");
        }
        assertThrows(Exception.class, () -> messageRepository.readReadMassage("1"));
    }

    @RepeatedTest(15)
    void refreshOldMessages() {
        Message[] messages = constructMessages(10, "1");

        for (int i = 0; i < 10; i++) {
            messageRepository.sendMassage(messages[i]);
            Message message = messageRepository.readUnreadMassage("1");
            messageRepository.markAsRead(message.getReceiverId(), message.getSenderId(), message.getMessageId());
        }
        for (int i = 0; i < messages.length; i++) {
            messageRepository.readReadMassage("1");
        }
        assertThrows(Exception.class, () -> messageRepository.readReadMassage("1"));
        messageRepository.refreshOldMessages("1");
        for (int i = 0; i < messages.length; i++) {
            messageRepository.readReadMassage("1");
        }
        assertThrows(Exception.class, () -> messageRepository.readReadMassage("1"));
    }
}