package BGU.Group13B.backend.Repositories.Implementations.DiscountAccumulationRepositoryImpl;

import BGU.Group13B.backend.Repositories.Implementations.ProductRepositoryImpl.ProductRepositoryAsHashMapService;
import BGU.Group13B.backend.Repositories.Interfaces.IDiscountAccumulationRepository;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.StoreDiscount;
import BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.*;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;
@Entity
public class DiscountAccumulationRepositoryAsHashMap implements IDiscountAccumulationRepository {

    @Transient
    private boolean saveMode;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "DiscountAccumulationRepositoryAsHashMap_DiscountAccumulationNode",
            joinColumns = {@JoinColumn(name = "DiscountAccumulationRepositoryAsHashMap_tableid", referencedColumnName = "id")},
            inverseJoinColumns = {
                    @JoinColumn(name = "DiscountAccumulationNode_id", referencedColumnName = "nodeId")})
    @MapKeyJoinColumn(name = "discountNodeId")
    private Map<Integer/*discountNodeId*/, DiscountAccumulationNode> noders;

    private AtomicInteger nextId;

    public DiscountAccumulationRepositoryAsHashMap() {
        noders = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(0);
        this.saveMode = true;
    }

    public DiscountAccumulationRepositoryAsHashMap(boolean saveMode) {
        noders = new ConcurrentHashMap<>();
        nextId = new AtomicInteger(0);
        this.saveMode = saveMode;
    }


    @Override
    public DiscountAccumulationNode addDiscountAsRoot(StoreDiscount discount) {
        int rootId = nextId.getAndIncrement();
        DiscountAccumulationNode discountNode = new DiscountNode(rootId, discount);
        noders.put(rootId, discountNode);
        save();
        return discountNode;
    }

    @Override
    public DiscountAccumulationNode addDiscountToMAXRoot(int rootId ,StoreDiscount discount) {
        DiscountAccumulationNode discountNode = addDiscountAsRoot(discount);
        int newRootId = nextId.getAndIncrement();
        DiscountAccumulationNode root = getDiscountAccumulationNode(rootId);
        DiscountAccumulationTree newRoot = new MaxDiscount(newRootId, root, discountNode);
        noders.put(newRootId, newRoot);
        save();
        return newRoot;
    }


    @Override
    public DiscountAccumulationNode addDiscountToXORRoot(int rootId ,StoreDiscount discount) {
        DiscountAccumulationNode discountNode = addDiscountAsRoot(discount);
        int newRootId = nextId.getAndIncrement();
        DiscountAccumulationNode root = getDiscountAccumulationNode(rootId);
        DiscountAccumulationTree newRoot = new XorDiscount(newRootId, root, discountNode);
        noders.put(newRootId, newRoot);
        save();
        return newRoot;
    }

    @Override
    public DiscountAccumulationNode addDiscountToADDRoot(int rootId ,StoreDiscount discount) {
        DiscountAccumulationNode discountNode = addDiscountAsRoot(discount);
        int newRootId = nextId.getAndIncrement();
        DiscountAccumulationNode root = getDiscountAccumulationNode(rootId);
        DiscountAccumulationTree newRoot = new AddDiscount(newRootId, root, discountNode);
        noders.put(newRootId, newRoot);
        save();
        return newRoot;
    }

    @Override
    public DiscountAccumulationNode getDiscountAccumulationNode(int nodeId) {
        if(!noders.containsKey(nodeId))
            throw new IllegalArgumentException("the node with node id: " + nodeId + " does not exists");

        return noders.get(nodeId);
    }

    @Override
    public void removeDiscountAccumulationNode(int nodeId) {
        noders.remove(nodeId);
    }

    @Override
    public void reset() {
        noders.clear();
        nextId.set(0);
    }

    @Override
    public void setSaveMode(boolean saveMode) {
        this.saveMode = saveMode;
    }

    private void save(){
        if(saveMode)
            SingletonCollection.getContext().getBean(DiscountAccumulationRepositoryAsHashMapService.class).save(this);
    }

    public boolean isSaveMode() {
        return saveMode;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Map<Integer, DiscountAccumulationNode> getNoders() {
        return noders;
    }


    public AtomicInteger getNextId() {
        return nextId;
    }

    public void setNextId(AtomicInteger nextId) {
        this.nextId = nextId;
    }

    public void setNoders(Map<Integer, BGU.Group13B.backend.storePackage.newDiscoutns.discountHandler.productDisountTree.DiscountAccumulationNode> noders) {
        this.noders = noders;
    }
}
