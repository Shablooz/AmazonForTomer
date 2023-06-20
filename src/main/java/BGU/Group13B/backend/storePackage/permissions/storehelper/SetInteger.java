package BGU.Group13B.backend.storePackage.permissions.storehelper;

import jakarta.persistence.*;
import jakartac.Cache.Cache;

import java.util.HashSet;
import java.util.Set;

@Entity
public class SetInteger {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Cache(policy = Cache.PolicyType.LRU)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name ="set_integer_here")
    private Set<Integer> set;

    public SetInteger(Set<Integer> set) {
        this.set = set;
    }

    public SetInteger() {
        set= new HashSet<>();
    }

    public void add(Integer s){
        set.add(s);
    }
    public void remove(Integer s){
        set.remove(s);
    }
    public boolean contains(Integer s){
        return set.contains(s);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<Integer> getSet() {
        return set;
    }

    public void setSet(Set<Integer> set) {
        this.set = set;
    }
}
