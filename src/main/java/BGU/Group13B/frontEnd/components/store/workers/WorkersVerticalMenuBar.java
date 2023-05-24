package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WorkersVerticalMenuBar extends VerticalLayout implements ResponseHandler {
    private List<WorkerCard> workers;

    public WorkersVerticalMenuBar(Collection<WorkerCard> workers, HashMap<Integer, String> userIdToUsername){
        super();
        this.workers = new LinkedList<>(workers);
        for(WorkerCard worker : workers){
            add(new WorkerMenuBar(worker, userIdToUsername.get(worker.userId())));
        }

        setSpacing(false);

    }

    public int getNumOfWorkers(){
        return workers.size();
    }

}
