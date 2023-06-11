package BGU.Group13B.frontEnd;

import jakarta.persistence.*;

@MappedSuperclass
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class Noder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
}
