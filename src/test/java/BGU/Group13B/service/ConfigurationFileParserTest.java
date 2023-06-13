package BGU.Group13B.service;

import BGU.Group13B.backend.User.UserPermissions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationFileParserTest {
    @BeforeEach
    void setUp() {
        SingletonCollection.reset_system();
    }

    @Test
    void parseConfig1() {
        ConfigurationFileParser.setConfigurationFileName("configurationFile1.json");
        ConfigurationFileParser.parse();
        assertEquals(7, SingletonCollection.getUserRepository().getAllUserCards().size());
        assertEquals(1, SingletonCollection.getStoreRepository().getAllStoresId().size());
        assertEquals(2, SingletonCollection.getStoreRepository().getStore(0).getStoreFounder());
        assertEquals(UserPermissions.StoreRole.OWNER, SingletonCollection.getUserRepository().
                getUser(1 + 2).getUserPermissions().getStoreRole(0));
        assertEquals(UserPermissions.StoreRole.OWNER, SingletonCollection.getUserRepository().
                getUser(1 + 3).getUserPermissions().getStoreRole(0));
        //test that all the objects were created
    }

    @Test
    void parseConfig2() {
        ConfigurationFileParser.setConfigurationFileName("configurationFile2.json");
        ConfigurationFileParser.parse();
        assertEquals(7, SingletonCollection.getUserRepository().getAllUserCards().size());
        assertEquals(1, SingletonCollection.getStoreRepository().getAllStoresId().size());
        assertEquals(2, SingletonCollection.getStoreRepository().getStore(0).getStoreFounder());
        assertEquals(1, SingletonCollection.getProductRepository().getProductByName("Bamba").size());
        assertEquals(30.0, SingletonCollection.getProductRepository().getProductByName("Bamba").get(0).getPrice());
        assertEquals(20, SingletonCollection.getProductRepository().getProductByName("Bamba").get(0).getStockQuantity());
        assertEquals(UserPermissions.StoreRole.MANAGER, SingletonCollection.getUserRepository().
                getUser(1 + 2).getUserPermissions().getStoreRole(0));
        assertTrue(SingletonCollection.getUserRepository().
                getUser(1 + 2).getUserPermissions().getIndividualPermissions(0).contains(UserPermissions.IndividualPermission.STOCK));
        assertEquals(UserPermissions.StoreRole.OWNER, SingletonCollection.getUserRepository().
                getUser(1 + 3).getUserPermissions().getStoreRole(0));
        assertEquals(UserPermissions.StoreRole.OWNER, SingletonCollection.getUserRepository().
                getUser(1 + 4).getUserPermissions().getStoreRole(0));
    }
}