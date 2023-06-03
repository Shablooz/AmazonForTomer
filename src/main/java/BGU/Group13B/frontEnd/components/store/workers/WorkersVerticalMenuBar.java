package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public class WorkersVerticalMenuBar extends VerticalLayout implements ResponseHandler {
    private final List<WorkerCard> workers;
    private final Session session;
    private final int userId;
    private final int storeId;

    public WorkersVerticalMenuBar(Session session, int userId, int storeId, Collection<WorkerCard> workers, HashMap<Integer, String> userIdToUsername){
        super();
        this.session = session;
        this.userId = userId;
        this.storeId = storeId;
        this.workers = new LinkedList<>(workers);
        for(WorkerCard worker : workers){
            add(new WorkerMenuBar(worker, userIdToUsername.get(worker.userId()), this));
        }

        setSpacing(false);

    }

    public int getNumOfWorkers(){
        return workers.size();
    }

    public void removeWorker(WorkerMenuBar workerMenuBar){
        WorkerCard worker = workerMenuBar.getWorkerCard();
        switch (worker.storeRole()) {
            case MANAGER -> {
                if(handleResponse(session.removeManager(userId, worker.userId(), storeId)) != null){
                    workers.remove(workerMenuBar.getWorkerCard());
                    remove(workerMenuBar);
                }
            }
            case OWNER -> {
                if(handleResponse(session.removeOwner(userId, worker.userId(), storeId)) != null){
                    workers.remove(workerMenuBar.getWorkerCard());
                    remove(workerMenuBar);
                }
            }
            case FOUNDER -> notifyWarning("Cannot remove founder");
        }



    }

}
