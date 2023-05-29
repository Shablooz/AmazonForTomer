package BGU.Group13B.backend.User.security;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Table(name = "tomer_ofek")
@Entity
public class TomerOfek {

    public TomerOfek() {
        this.id = 420;
        this.ofek = "ofek";
    }
    public TomerOfek(int id, String ofek) {
        this.id = id;
        this.ofek = ofek;
    }

    @Column
    @Id
    private int id;

    @Column
    private String ofek;
}
