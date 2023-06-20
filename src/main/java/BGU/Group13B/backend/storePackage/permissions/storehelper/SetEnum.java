package BGU.Group13B.backend.storePackage.permissions.storehelper;

import BGU.Group13B.backend.User.UserPermissions;
import jakarta.persistence.*;
import jakartac.Cache.Cache;

import java.util.HashSet;
import java.util.Set;

@Entity
public class SetEnum {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Cache(policy = Cache.PolicyType.LRU)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name ="set_IndividualPermission")
    private Set<UserPermissions.IndividualPermission> set;

    public SetEnum(Set<UserPermissions.IndividualPermission> set) {
        this.set = set;
    }

    public SetEnum() {
        set= new HashSet<>();
    }

    public void add(UserPermissions.IndividualPermission s){
        set.add(s);
    }
    public void remove(UserPermissions.IndividualPermission s){
        set.remove(s);
    }
    public boolean contains(UserPermissions.IndividualPermission s){
        return set.contains(s);
    }
    public void clear(){
        set.clear();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Set<UserPermissions.IndividualPermission> getSet() {
        return set;
    }

    public void setSet(Set<UserPermissions.IndividualPermission> set) {
        this.set = set;
    }
}
