package BGU.Group13B.backend.storePackage;


import BGU.Group13B.backend.Repositories.Interfaces.IStoreRepository;

public class Market {
    private final IStoreRepository storeRepository;

    public Market(IStoreRepository storeRepository) {
        this.storeRepository = storeRepository;
    }

}
