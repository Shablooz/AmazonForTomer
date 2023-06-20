package BGU.Group13B.backend.Repositories.Implementations.StoreMessageRepositoyImpl;

import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StoreMessageSingleService {
    private StoreMessageSingleJPA storeMessageSingleJPA;

    @Autowired
    public StoreMessageSingleService(StoreMessageSingleJPA storeMessageSingleJPA) {
        this.storeMessageSingleJPA = storeMessageSingleJPA;
    }

    public StoreMessageSingleService(){

    }

    public void save(StoreMessageSingle storeMessageSingle){
         SingletonCollection.setStoreMessagesRepository( storeMessageSingleJPA.save(storeMessageSingle));
    }

    public void delete(StoreMessageSingle storeMessageSingle){
        storeMessageSingleJPA.delete(storeMessageSingle);
    }

    public StoreMessageSingle getStoreMessageSingle() {
        return storeMessageSingleJPA.findById(1).orElse(null);
    }
}
