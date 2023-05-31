package BGU.Group13B.frontEnd;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Preson {

    @Id
    private int id;
    private String name;

    public Preson(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public Preson() {
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
