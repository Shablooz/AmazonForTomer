package BGU.Group13B.frontEnd;

import jakarta.persistence.*;

import java.util.HashMap;
import java.util.Map;

@Entity
public class Eden {
    @Id
    private int id;

    @OneToMany(cascade = CascadeType.ALL)
        @JoinTable(name = "eden_people",
        joinColumns = {@JoinColumn(name = "eden_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name = "preson_pk", referencedColumnName = "id")})
        @MapKey(name = "id")
    private Map<Integer, Preson> people;

    public Eden() {
        people = new HashMap<>();
        id = 1;
        people.put(1, new Preson(50, "eden"));
        people.put(2, new Preson(100, "eyal"));
        people.put(3, new Preson(150, "yoav"));
    }
}
