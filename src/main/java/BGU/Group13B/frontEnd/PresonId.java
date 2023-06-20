package BGU.Group13B.frontEnd;

import jakarta.persistence.Embeddable;

import java.io.Serializable;

@Embeddable
public class PresonId implements Serializable {
    private int id;
    private String name;

    // Constructors, getters, setters, hashCode(), equals()
    public PresonId() {
        this.id = 1;
        this.name = "person";
    }

    public PresonId(int id,String name) {
        this.id = id;
        this.name = name;
    }
}