package BGU.Group13B.backend.Repositories.Implementations.MessageRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.DiscountRepositoryJPA;
import BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl.StoreDiscountRootsRepositoryAsHashMap;
import BGU.Group13B.backend.Repositories.Implementations.StoreDiscountRootsRepositoryImpl.StoreDiscountRootsRepositoryAsHashMapJPA;
import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class MessageRepositorySingleService {
    private MessageRepositoryAsJPA messageRepositoryAsJPA;

    @Autowired
    public MessageRepositorySingleService(MessageRepositoryAsJPA messageRepositoryAsJPA) {
        this.messageRepositoryAsJPA = messageRepositoryAsJPA;
    }

    public MessageRepositorySingleService(){

    }

    public void save(MessageRepositorySingle messageRepositorySingle){
        SingletonCollection.setMessageRepository(this.messageRepositoryAsJPA.save(messageRepositorySingle));
    }

    public void delete(MessageRepositorySingle messageRepositorySingle){
        messageRepositoryAsJPA.delete(messageRepositorySingle);
    }

    public MessageRepositorySingle getMessageRepository() {
        return messageRepositoryAsJPA.findById(1).orElse(null);
    }

}
