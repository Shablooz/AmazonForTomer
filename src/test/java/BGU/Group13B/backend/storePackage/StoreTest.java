package BGU.Group13B.backend.storePackage;

import BGU.Group13B.backend.Repositories.Implementations.BIDRepositoryImpl.BIDRepositoryAsList;
import BGU.Group13B.backend.User.User;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.permissions.ChangePermissionException;
import BGU.Group13B.backend.storePackage.permissions.NoPermissionException;
import BGU.Group13B.backend.storePackage.permissions.StorePermission;
import BGU.Group13B.service.SingletonCollection;
import BGU.Group13B.service.callbacks.AddToUserCart;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;

import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Set;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

class StoreTest {


    private static Store store;
    private static int managerWithPermission;
    private static int managerWithoutPermission;
    private static int storeId;
    private static int productId;
    private static int amount;
    private static double proposedPrice;
    private static AddToUserCart addToUserCart;
    private static BIDRepositoryAsList bidRepository;
    private static AlertManager alertManager;
    private static String msg;
    private int tUserOwnerId;
    private int tUserOwnerId2;
    private int tUserId;
    private int tUserId2;
    private int storeId1;

    private int u1Id;

    private int u2Id;

    private int u3Id;

    @BeforeEach
    void setUp() throws NoPermissionException {
        /*initializing data*/

        var storePermission = Mockito.mock(StorePermission.class);
        alertManager = Mockito.mock(AlertManager.class);
        bidRepository = new BIDRepositoryAsList();
        addToUserCart = Mockito.mock(AddToUserCart.class);
        String functionName = "purchaseProposalSubmit";
        managerWithPermission = 1;
        managerWithoutPermission = 2;
        storeId = 1;
        productId = 1;
        amount = 1;
        proposedPrice = 10.0;

        /*Defining behaviour */

        Mockito.when(storePermission.checkPermission(managerWithPermission, false)).thenReturn(true);
        Mockito.when(storePermission.checkPermission(managerWithoutPermission, false)).thenReturn(false);
        Mockito.when(storePermission.getAllUsersWithPermission(functionName)).thenReturn(Set.of(managerWithPermission));
        /*verify the method invocation*/
        Mockito.doNothing().when(addToUserCart).apply(managerWithPermission, storeId, productId);
        msg = "User " + managerWithPermission + " has submitted a purchase proposal for product " + productId + " in store " + storeId;
        Mockito.doNothing().when(alertManager).sendAlert(managerWithPermission, msg);
        store = new Store(storeId, "store name", "category", null, null, null,
                alertManager, storePermission, addToUserCart, bidRepository, null, null, null,
                null, null, null);

    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void purchaseProposalSubmitWithPermissions() {
        try {
            store.purchaseProposalSubmit(managerWithPermission, productId, proposedPrice, amount);
            Assertions.assertEquals(bidRepository.getBID(0),
                    Optional.of(new BID(0, managerWithPermission, productId, proposedPrice, amount)));
            assertFalse(bidRepository.getBID(0).orElseThrow(() ->
                    new NoSuchElementException("No such bid with id 0")).isRejected());
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void purchaseProposalSubmitWithoutPermissions() {
        try {
            store.purchaseProposalSubmit(managerWithoutPermission, productId, proposedPrice, amount);
        } catch (NoPermissionException e) {
            Assertions.assertEquals("User " + managerWithoutPermission + " has no permission to add product to store " + storeId, e.getMessage());
        } catch (Exception e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void purchaseProposalSubmitWithNegativeAmount() {
        try {
            store.purchaseProposalSubmit(managerWithPermission, productId, proposedPrice, -1);
        } catch (NoPermissionException e) {
            fail("Exception was thrown");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Amount must be positive", e.getMessage());
        }
    }

    @Test
    void purchaseProposalSubmitWithNegativePrice() {
        try {
            store.purchaseProposalSubmit(managerWithPermission, productId, -1, amount);
        } catch (NoPermissionException e) {
            fail("Exception was thrown");
        } catch (IllegalArgumentException e) {
            Assertions.assertEquals("Price must be positive", e.getMessage());
        }
    }
    //run this test 10 times to make sure it works

    @RepeatedTest(25)
    void purchaseProposalSubmitOneRejectOneAccept() {
        //with threads, where ane thread tries to accept and the other to reject
        AtomicInteger firstThreadToFinish = new AtomicInteger(-1);
        setupForTheConcurrentTest();

        Thread thread1 = new Thread(() -> {
            try {
                if (Math.random() > 0.5)//adding randomness to the order of the threads
                    Thread.sleep(10);
                store.purchaseProposalApprove(managerWithPermission, 0);
                synchronized (firstThreadToFinish) {
                    if (firstThreadToFinish.get() == -1)
                        firstThreadToFinish.set(1);
                }
            } catch (NoPermissionException e) {
                fail("Exception was thrown");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            } catch (IllegalArgumentException e) {
                if (!(e.getMessage().equals("There is no such bid for store 1")
                        && firstThreadToFinish.get() == 2))
                    fail("Exception was thrown " + e.getMessage());
            }
        });
        Thread thread2 = new Thread(() -> {
            try {
                if (Math.random() > 0.5)
                    Thread.sleep(10);
                store.purchaseProposalReject(managerWithPermission, 0);

                synchronized (firstThreadToFinish) {
                    if (firstThreadToFinish.get() == -1)
                        firstThreadToFinish.set(2);
                }
            } catch (NoPermissionException e) {
                fail("Exception was thrown");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        thread1.start();
        thread2.start();
        try {
            thread1.join();
            thread2.join();
            if (firstThreadToFinish.get() == 1)
                Mockito.verify(addToUserCart,
                        Mockito.times(1)).apply(managerWithPermission, storeId, productId);
            else if (firstThreadToFinish.get() == 2)
                Mockito.verify(alertManager,
                        Mockito.times(1)).sendAlert(managerWithPermission, msg);
            else
                fail("No thread finished QA's fault");
        } catch (InterruptedException ignore) {
        } catch (NoSuchElementException e) {
            fail("No such bid with id 0");
        }

    }

    private static void setupForTheConcurrentTest() {
        try {
            store.purchaseProposalSubmit(managerWithPermission, productId, proposedPrice, amount);
        } catch (NoPermissionException e) {
            fail("Exception was thrown");
        }
    }

    @Test
    void purchaseProposalApprove() {
        //write a test for this function
        try {
            store.purchaseProposalSubmit(managerWithPermission, productId, proposedPrice, amount);

            store.purchaseProposalApprove(managerWithPermission, 0);
            Mockito.verify(addToUserCart,
                    Mockito.times(1)).apply(managerWithPermission, storeId, productId);
            Assertions.assertFalse(bidRepository.getBID(0).orElseThrow().isRejected());

        } catch (NoPermissionException | NoSuchElementException e) {
            fail();
        }
    }

    @Test
    void purchaseProposalReject() {
        try {
            store.purchaseProposalSubmit(managerWithPermission, productId, proposedPrice, amount);

            store.purchaseProposalReject(managerWithPermission, 0);
            Mockito.verify(addToUserCart,
                    Mockito.times(0)).apply(managerWithPermission, storeId, productId);

            Assertions.assertEquals(bidRepository.getBID(0), Optional.empty());

        } catch (NoPermissionException e) {
            throw new RuntimeException(e);
        }
    }

    public void customSetUp() {
        tUserOwnerId = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(tUserOwnerId, new User(tUserOwnerId));

        tUserOwnerId2 = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(tUserOwnerId2, new User(tUserOwnerId2));

        tUserId = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(tUserId, new User(tUserId));
        tUser = SingletonCollection.getUserRepository().getUser(tUserId);

        tUserId2 = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(tUserId2, new User(tUserId2));
        tUser2 = SingletonCollection.getUserRepository().getUser(tUserId2);
        storeId1 = SingletonCollection.getStoreRepository().addStore(tUserOwnerId, "hireTestStore", "time travel");

        omTestStore = SingletonCollection.getStoreRepository().getStore(storeId1);
    }

    public void RecursiveFireTestSetUp(){
        u1Id = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(u1Id, new User(u1Id));

        u2Id = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(u2Id, new User(u2Id));

        u3Id = SingletonCollection.getUserRepository().getNewUserId();
        SingletonCollection.getUserRepository().addUser(u3Id, new User(u3Id));

        u1 = SingletonCollection.getUserRepository().getUser(u1Id);
        u2 = SingletonCollection.getUserRepository().getUser(u2Id);
        u3 = SingletonCollection.getUserRepository().getUser(u3Id);

        s1Id = SingletonCollection.getStoreRepository().addStore(u1Id, "s1", "burn them all");

        s1 = SingletonCollection.getStoreRepository().getStore(storeId1);
    }

    @Test
    void recursiveFireSuccess() {
        RecursiveFireTestSetUp();
        try {
            s1.addOwner(u1Id, u2Id);
            s1.addOwner(u2Id, u3Id);
            Assertions.assertThrows(ChangePermissionException.class, () -> s1.removeOwner(u3Id, u2Id));
            s1.removeOwner(u1Id, u2Id);
            assertNull(s1.getStorePermission().getUserRole(u2Id));
            assertNull(s1.getStorePermission().getUserRole(u3Id));
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        } finally {
            tearTheBuildingDown();
        }
    }

    private static User tUser;
    private static Store omTestStore;
    private static User tUser2;

    private static User u1;
    private static User u2;
    private static User u3;
    private static Store s1;
    private int s1Id;

    public void customTearDown() {
        omTestStore.getStorePermission().clearForTest();
        tUser.getUserPermissions().clearForTest();
        tUser2.getUserPermissions().clearForTest();
        SingletonCollection.getStorePurchasePolicyRepository().reset();
        //SingletonCollection.reset_system();
    }

    public void tearTheBuildingDown() {
        s1.getStorePermission().clearForTest();
        u1.getUserPermissions().clearForTest();
        u2.getUserPermissions().clearForTest();
        SingletonCollection.getStorePurchasePolicyRepository().reset();
        //SingletonCollection.reset_system();
    }


    @Test
    void addOwnerSuccess() {
        customSetUp();
        try {
            omTestStore.addOwner(tUserOwnerId, tUserId);
            Assertions.assertEquals(omTestStore.getStorePermission().getUserRole(tUserId), UserPermissions.StoreRole.OWNER);
            Assertions.assertEquals(tUser.getUserPermissions().getStoreRole(omTestStore.getStoreId()), UserPermissions.StoreRole.OWNER);
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        } finally {
            customTearDown();
        }
    }

    @Test
    void addOwnerFail() {
        customSetUp();
        try {
            omTestStore.addOwner(tUserOwnerId, tUserId);
            Assertions.assertThrows(ChangePermissionException.class, () -> omTestStore.addOwner(tUserOwnerId, tUserId));
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        } finally {
            customTearDown();
        }
    }

    @Test
    void removeOwnerSuccess() {
        customSetUp();
        try {
            omTestStore.addOwner(tUserOwnerId, tUserId);
            omTestStore.removeOwner(tUserOwnerId, tUserId);
            assertNull(omTestStore.getStorePermission().getUserRole(tUserId));
            assertNull(tUser.getUserPermissions().getStoreRole(tUserOwnerId));
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        } finally {
            customTearDown();
        }
    }

    @Test
    void removeOwnerFail() {
        customSetUp();
        try {
            omTestStore.addOwner(tUserOwnerId, tUserId);
            omTestStore.addOwner(tUserOwnerId, tUserId2);
            Assertions.assertThrows(ChangePermissionException.class, () -> omTestStore.removeOwner(tUserId2, tUserId));
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        } finally {
            customTearDown();
        }
    }

    @Test
    void addManagerSuccess() {
        customSetUp();
        try {
            omTestStore.addManager(tUserOwnerId, tUserId);
            Assertions.assertEquals(omTestStore.getStorePermission().getUserRole(tUserId), UserPermissions.StoreRole.MANAGER);
            Assertions.assertEquals(tUser.getUserPermissions().getStoreRole(omTestStore.getStoreId()), UserPermissions.StoreRole.MANAGER);
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        } finally {
            customTearDown();
        }
    }

    @Test
    void addManagerFail() {
        customSetUp();
        try {
            omTestStore.addManager(tUserOwnerId, tUserId);
            Assertions.assertThrows(ChangePermissionException.class, () -> omTestStore.addManager(tUserOwnerId, tUserId));
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        } finally {
            customTearDown();
        }
    }

    @Test
    void removeManagerSuccess() {
        customSetUp();
        try {
            omTestStore.addManager(tUserOwnerId, tUserId);
            omTestStore.removeManager(tUserOwnerId, tUserId);
            assertNull(omTestStore.getStorePermission().getUserRole(tUserId));
            assertNull(tUser.getUserPermissions().getStoreRole(tUserOwnerId));
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        }
        customTearDown();
    }

    @Test
    void removeManagerFail() {
        customSetUp();
        try {
            omTestStore.addManager(tUserOwnerId, tUserId);
            omTestStore.addOwner(tUserOwnerId, tUserId2);
            Assertions.assertThrows(ChangePermissionException.class, () -> omTestStore.removeManager(tUserId2, tUserId));
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        } finally {
            customTearDown();
        }
    }

    @Test
    void addIndividualPermissionSuccess() {
        customSetUp();
        try {
            omTestStore.addManager(tUserOwnerId, tUserId);
            omTestStore.addPermissionToManager(tUserOwnerId, tUserId, UserPermissions.IndividualPermission.STOCK);
            Assertions.assertTrue(omTestStore.getStorePermission().getUserToIndividualPermissions().get(tUserId).contains(UserPermissions.IndividualPermission.STOCK));
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        } finally {
            customTearDown();
        }
    }


    @RepeatedTest(25)
    void addOwnerMultiThreadTest() {
        customSetUp();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Boolean> task1 = () -> {
            try {
                omTestStore.addOwner(tUserOwnerId, tUserId);
                return true;
            } catch (NoPermissionException | ChangePermissionException e) {
                return false;
            }
        };

        Callable<Boolean> task2 = () -> {
            try {
                omTestStore.addOwner(tUserOwnerId2, tUserId);
                return true;
            } catch (NoPermissionException | ChangePermissionException e) {
                return false;
            }
        };

        Future<Boolean> future1 = executorService.submit(task1);
        Future<Boolean> future2 = executorService.submit(task2);

        try {
            // Ensure both tasks have completed
            boolean task1Result = future1.get();
            boolean task2Result = future2.get();

            // Check if one succeeded and the other failed
            Assertions.assertTrue((task1Result && !task2Result) || (!task1Result && task2Result));

        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } finally {
            executorService.shutdown(); // Always shut down executor service
            customTearDown();
        }
    }


    @RepeatedTest(25)
    void removeOwnerMultiThreadTest() {
        customSetUp();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // We add owner in the setup.
        try {
            omTestStore.addOwner(tUserOwnerId, tUserId);
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        }

        Callable<Boolean> task1 = () -> {
            try {
                omTestStore.removeOwner(tUserOwnerId, tUserId);
                return true;
            } catch (NoPermissionException | ChangePermissionException e) {
                return false;
            }
        };

        Callable<Boolean> task2 = () -> {
            try {
                omTestStore.removeOwner(tUserOwnerId2, tUserId);
                return true;
            } catch (NoPermissionException | ChangePermissionException e) {
                return false;
            }
        };

        Future<Boolean> future1 = executorService.submit(task1);
        Future<Boolean> future2 = executorService.submit(task2);

        try {
            // Ensure both tasks have completed
            boolean task1Result = future1.get();
            boolean task2Result = future2.get();

            // Check if one succeeded and the other failed
            Assertions.assertTrue((task1Result && !task2Result) || (!task1Result && task2Result));

        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } finally {
            executorService.shutdown(); // Always shut down executor service
            customTearDown();
        }
    }


    @RepeatedTest(25)
    void addManagerMultiThreadTest() {
        customSetUp();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        Callable<Boolean> task1 = () -> {
            try {
                omTestStore.addManager(tUserOwnerId, tUserId);
                return true;
            } catch (NoPermissionException | ChangePermissionException e) {
                return false;
            }
        };

        Callable<Boolean> task2 = () -> {
            try {
                omTestStore.addManager(tUserOwnerId2, tUserId);
                return true;
            } catch (NoPermissionException | ChangePermissionException e) {
                return false;
            }
        };

        Future<Boolean> future1 = executorService.submit(task1);
        Future<Boolean> future2 = executorService.submit(task2);

        try {
            // Ensure both tasks have completed
            boolean task1Result = future1.get();
            boolean task2Result = future2.get();

            // Check if one succeeded and the other failed
            Assertions.assertTrue((task1Result && !task2Result) || (!task1Result && task2Result));

        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } finally {
            executorService.shutdown(); // Always shut down executor service
            customTearDown();
        }
    }


    @RepeatedTest(25)
    void removeManagerMultiThreadTest() {
        customSetUp();
        ExecutorService executorService = Executors.newFixedThreadPool(2);

        // We add manager in the setup.
        try {
            omTestStore.addManager(tUserOwnerId, tUserId);
        } catch (NoPermissionException | ChangePermissionException e) {
            fail(e);
        }

        Callable<Boolean> task1 = () -> {
            try {
                omTestStore.removeManager(tUserOwnerId, tUserId);
                return true;
            } catch (NoPermissionException | ChangePermissionException e) {
                return false;
            }
        };

        Callable<Boolean> task2 = () -> {
            try {
                omTestStore.removeManager(tUserOwnerId2, tUserId);
                return true;
            } catch (NoPermissionException | ChangePermissionException e) {
                return false;
            }
        };

        Future<Boolean> future1 = executorService.submit(task1);
        Future<Boolean> future2 = executorService.submit(task2);

        try {
            // Ensure both tasks have completed
            boolean task1Result = future1.get();
            boolean task2Result = future2.get();

            // Check if one succeeded and the other failed
            Assertions.assertTrue((task1Result && !task2Result) || (!task1Result && task2Result));

        } catch (InterruptedException | ExecutionException e) {
            fail(e);
        } finally {
            executorService.shutdown(); // Always shut down executor service
            customTearDown();
        }
    }

}