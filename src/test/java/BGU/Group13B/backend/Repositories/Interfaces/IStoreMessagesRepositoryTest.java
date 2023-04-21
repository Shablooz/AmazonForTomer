package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl.StoreMessageSingle;
import BGU.Group13B.backend.User.Message;
import BGU.Group13B.service.SingletonCollection;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.concurrent.ThreadPoolExecutor;

import static org.junit.jupiter.api.Assertions.*;

class IStoreMessagesRepositoryTest {
    public static IStoreMessagesRepository storeMessagesRepository;

    //start of helper functions
    interface MyRunnableFactory {
        public Runnable createRunnable(int[] integers, char[] chars, String[] strings);
    }

    private void startAndWait(Thread[] threads) {
        for (int i = 0; i < threads.length; i++)
            threads[i].start();

        for (int i = 0; i < threads.length; i++)
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    private Message[] constructMessages(int num) {
        Message[] messages = new Message[num];
        for (int i = 1; i <= num; i++) {
            String s = "Test" + i;
            messages[i - 1] = Message.constractMessage(s, i, s, s, s);
        }
        return messages;
    }

    //end of helper functions


    @BeforeEach
    void setUp() {
        storeMessagesRepository = new StoreMessageSingle();

    }

    @AfterEach
    void tearDown() {
    }


    @Test
    void sendMassage() {

        storeMessagesRepository.sendMassage(Message.constractMessage("Test", 1, "Test", "Test", "Test"), 1, 1);
        storeMessagesRepository.sendMassage(Message.constractMessage("Test1", 2, "Test", "Test1", "Test1"), 1, 2);
        Message message = storeMessagesRepository.readUnreadMassage(1, 1);
        assertEquals(message.getMessageId(), 1);
        assertEquals(message.getSenderId(), "Test");
    }

    @Test
    void sendMassage_TreadVersion() {
        Thread[] threads = new Thread[10];
        Message[] messages = constructMessages(10);
        for (int i = 0; i < 10; i++) {
            int finalI = i;
            threads[i] = new Thread(() -> {
                storeMessagesRepository.sendMassage(messages[finalI], 1, finalI);
            });
        }
        startAndWait(threads);
        int numOfMessages = 0;
        HashSet<Message> allMessages = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            Message message = storeMessagesRepository.readUnreadMassage(1, i);
            assert !allMessages.contains(message);
            allMessages.add(message);
            storeMessagesRepository.markAsRead(1, message.getSenderId(), message.getMessageId(), i);
            numOfMessages++;
        }
        assertEquals(10, allMessages.size());
        assertEquals(10, numOfMessages);

    }

    @Test
    void readUnreadMassage() {
        Message message = constructMessages(1)[0];
        storeMessagesRepository.sendMassage(message, 1, 1);
        Message message1 = storeMessagesRepository.readUnreadMassage(1, 1);
        assertEquals(message, message1);
    }

    @Test
    void readUnreadMassage_TreadVersion_One_Store() {
        int messageNum = 1;
        int threadNum = 10;
        Message[] message = constructMessages(messageNum);
        for (int i = 0; i < messageNum; i++)
            storeMessagesRepository.sendMassage(message[i], 1, i);


        Thread[] threads = new Thread[threadNum];
        MyRunnableFactory factory = (int[] integers, char[] chars, String[] strings) -> {
            return () -> {
                try {
                    Message message1 = storeMessagesRepository.readUnreadMassage(1, integers[0]);
                    storeMessagesRepository.markAsRead(1, message1.getSenderId(), message1.getMessageId(), integers[0]);
                } catch (Exception e) {
                }
            };
        };

        for (int i = 0; i < threadNum; i++) {
            int[] integers = {i};
            threads[i] = new Thread(factory.createRunnable(integers, null, null));
        }
        startAndWait(threads);
        Message message1 = storeMessagesRepository.readReadMassage(1, 1);
        assertEquals(message[0], message1);
        assertThrows(Exception.class, () -> {
            storeMessagesRepository.readReadMassage(1, 1);
        });


    }


    @Test
    void readReadMassage_TreadVersion() {
        Message[] messages = constructMessages(2);
        for (int i = 0; i < messages.length; i++) {
            storeMessagesRepository.sendMassage(messages[i], 1, i);
            storeMessagesRepository.markAsRead(1, messages[i].getSenderId(), messages[i].getMessageId(), i);
        }
        Thread[] threads = new Thread[10];
        MyRunnableFactory factory = (int[] integers, char[] chars, String[] strings) -> {
            return () -> {
                for (int i = 0; i < messages.length; i++) {
                    Message message1 = storeMessagesRepository.readReadMassage(1, integers[0]);
                    assertEquals(message1, messages[i]);
                }
                assertThrows(Exception.class, () -> {
                    storeMessagesRepository.readReadMassage(1, integers[0]);
                });
            };
        };
        for (int i = 0; i < 10; i++) {
            int[] integers = {i};
            threads[i] = new Thread(factory.createRunnable(integers, null, null));
        }
        startAndWait(threads);

    }


    @Test
    void markAsRead() {
        Message message = constructMessages(1)[0];
        storeMessagesRepository.sendMassage(message, 1, 1);
        storeMessagesRepository.markAsRead(1, message.getSenderId(), message.getMessageId(), 1);
        Message message1 = storeMessagesRepository.readReadMassage(1, 1);
        assertEquals(message, message1);
    }

    @Test
    void markAsRead_TreadVersion() {
        Message[] messages = constructMessages(10);
        Thread[] senders = new Thread[10];
        Thread[] readers = new Thread[10];

        MyRunnableFactory addMessageFactory = (int[] integers, char[] chars, String[] strings) -> {
            return () -> {
                storeMessagesRepository.sendMassage(messages[integers[0]], 1, integers[0]);
            };
        };
        MyRunnableFactory markAsReadFactory = (int[] integers, char[] chars, String[] strings) -> {
            return () -> {
                int finished = 0;
                for (int i = 0; i < messages.length && finished == 0; i++) {
                    try {
                        Message message1 = storeMessagesRepository.readUnreadMassage(1, integers[0]);
                        storeMessagesRepository.markAsRead(1, message1.getSenderId(), message1.getMessageId(), integers[0]);
                    } catch (Exception e) {
                        if (e.getMessage().equals("No unread messages"))
                            finished++;

                    }
                }
                assertThrows(Exception.class, () -> {
                    storeMessagesRepository.readUnreadMassage(1, integers[0]);
                });

            };
        };
        for (int i = 0; i < 10; i++) {
            int[] integers = {i};
            senders[i] = new Thread(addMessageFactory.createRunnable(integers, null, null));
            readers[i] = new Thread(markAsReadFactory.createRunnable(integers, null, null));
        }
        startAndWait(senders);
        startAndWait(readers);
        for (int i = 0; i < 10; i++) {
            Message message1 = storeMessagesRepository.readReadMassage(1, 0);
        }
        assertThrows(Exception.class, () -> {
            storeMessagesRepository.readReadMassage(1, 0);
        });
    }

    @Test
    void refreshOldMassage() {
        Message[] messages = constructMessages(10);
        for (int i = 0; i < messages.length; i++)
            storeMessagesRepository.sendMassage(messages[i], 1, i);
        for (int i = 0; i < messages.length; i++)
            storeMessagesRepository.markAsRead(1, messages[i].getSenderId(), messages[i].getMessageId(), i);
        for (int i = 0; i < messages.length; i++) {
            storeMessagesRepository.readReadMassage(1, 1);
        }
        storeMessagesRepository.refreshOldMassage(1, 1);
        assertEquals(storeMessagesRepository.readReadMassage(1, 1), messages[0]);
    }

    @Test
    void refreshOldMassage_ThreadVersion() {
        Message[] messages = constructMessages(10);
        for (int i = 0; i < messages.length; i++)
            storeMessagesRepository.sendMassage(messages[i], 1, i);
        for (int i = 0; i < messages.length; i++)
            storeMessagesRepository.markAsRead(1, messages[i].getSenderId(), messages[i].getMessageId(), i);

        Thread[] threads = new Thread[10];
        MyRunnableFactory factory = (int[] integers, char[] chars, String[] strings) -> {
            return () -> {
                for (int i = 0; i < messages.length; i++) {
                    storeMessagesRepository.readReadMassage(1, integers[0]);
                }
                storeMessagesRepository.refreshOldMassage(1, integers[0]);
                assertEquals(storeMessagesRepository.readReadMassage(1, integers[0]), messages[0]);
                assertEquals(storeMessagesRepository.readReadMassage(1, integers[0]), messages[1]);
            };
        };

        for (int i = 0; i < 10; i++) {
            int[] integers = {i};
            threads[i] = new Thread(factory.createRunnable(integers, null, null));
        }
        startAndWait(threads);

    }
}