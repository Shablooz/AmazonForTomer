package BGU.Group13B.frontEnd;


import jakarta.persistence.Entity;
import jakarta.persistence.Id;

@Entity
public class Person {

    @Id
    private Long id;

    private String name;




    public Person() {
        this.id = 1L;
        this.name = "John Doe";
    }
    public Person(Long id, String name) {
        this.id = id;
        this.name = name;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }
}
