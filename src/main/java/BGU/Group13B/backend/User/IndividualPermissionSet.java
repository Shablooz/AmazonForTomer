package BGU.Group13B.backend.User;

import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;

@Entity
public class IndividualPermissionSet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ElementCollection
    @CollectionTable(name ="IndividualPermission")
    private Set<UserPermissions.IndividualPermission> individualPermissions;

    public IndividualPermissionSet(){
        this.individualPermissions = new HashSet<>();
    }

    public void add(UserPermissions.IndividualPermission individualPermission)
    {
        individualPermissions.add(individualPermission);
    }
    public void remove(UserPermissions.IndividualPermission individualPermission)
    {
        individualPermissions.remove(individualPermission);
    }
    public void clear()
    {
        individualPermissions.clear();
    }

    public Set<UserPermissions.IndividualPermission> getIndividualPermissions() {
        return individualPermissions;
    }

    public void setIndividualPermissions(Set<UserPermissions.IndividualPermission> individualPermissions) {
        this.individualPermissions = individualPermissions;
    }

    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

}
