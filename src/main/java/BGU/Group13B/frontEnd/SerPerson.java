package BGU.Group13B.frontEnd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SerPerson {


    private final PersonJPA personJPA;

    @Autowired
    public SerPerson(PersonJPA personJPA) {
        this.personJPA = personJPA;
    }


    public void addPerson(Preson person){
        personJPA.save(person);
    }

    public void removePerson(Preson person){
        personJPA.delete(person);
    }

    public void updatePerson(Preson person){
        personJPA.save(person);
    }

    public Preson getPerson(int id){
        return personJPA.findById(id).orElse(null);
    }
}
