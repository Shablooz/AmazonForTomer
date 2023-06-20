package BGU.Group13B.frontEnd;

import jakarta.persistence.Entity;

@Entity
public class NoderSon extends Noder{
    private String name;

    public NoderSon(String name){
        super();
        this.name = name;
    }


    public NoderSon() {
        super();
        this.name = "מה שתגיד אחי";
    }
}
