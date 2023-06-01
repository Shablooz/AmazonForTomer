package BGU.Group13B.frontEnd;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EdenService {

    @Autowired
    private EdenRepo edenRepo;

    public void saveEden() {
        edenRepo.save(new Eden());
    }
}
