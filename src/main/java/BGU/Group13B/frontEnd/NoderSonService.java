package BGU.Group13B.frontEnd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class NoderSonService {
    private final NoderJPA noderJPA;

    @Autowired
    public NoderSonService(NoderJPA noderJPA) {
        this.noderJPA = noderJPA;
    }


    public void addPerson(NoderSon noderSon){
        noderJPA.save(noderSon);
    }

    public void removePerson(NoderSon noderSon){
        noderJPA.delete(noderSon);
    }

    public void updatePerson(NoderSon noderSon){
        noderJPA.save(noderSon);
    }

    public NoderSon getNoder(int id){
        return noderJPA.findById(id).orElse(null);
    }
}
