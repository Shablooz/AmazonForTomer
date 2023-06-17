package BGU.Group13B.backend.storePackage.permissions.storehelper;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
@Entity
public class SetString {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name ="set_string_here")
    private Set<String> set;

    public SetString(Set<String> set) {
        this.set = set;
    }

    public SetString() {
        set= new HashSet<>();
    }

    public void add(String s){
        set.add(s);
    }
    public void remove(String s){
        set.remove(s);
    }
    public boolean contains(String s){
        return set.contains(s);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<String> getSet() {
        return set;
    }

    public void setSet(Set<String> set) {
        this.set = set;
    }
}
