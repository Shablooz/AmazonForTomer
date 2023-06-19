package BGU.Group13B.frontEnd.components.views;
import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import BGU.Group13B.service.Session;
import BGU.Group13B.service.info.StoreInfo;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.data.renderer.NativeButtonRenderer;
import com.vaadin.flow.router.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

@PageTitle("MyVotes")
@Route(value = "myvotes", layout = MainLayout.class)
public class MyVotesView extends VerticalLayout implements BeforeEnterObserver, ResponseHandler, HasUrlParameter<String>{
    private Grid<Pair<Integer, Integer>> grid;
    private Session session;
    private final int userId = SessionToIdMapper.getInstance().getCurrentSessionId();
    private int storeId;
    private Pair<Integer, Integer> selectedPair;

    @Autowired
    public MyVotesView(Session session) {
        this.session = session;
        this.grid = new Grid<>();

    }

    public void start(){
        // Create a new grid and configure it
        grid.addColumn(pair -> {
            String username = handleResponse(session.getUserNameRes(pair.getFirst()));
            return username != null ? username : "Unknown";
        }).setHeader("New Owner");

        grid.addColumn(pair -> {
            String username = handleResponse(session.getUserNameRes(pair.getSecond()));
            return username != null ? username : "Unknown";
        }).setHeader("Appointer");


        grid.asSingleSelect().addValueChangeListener(event -> {
            selectedPair = event.getValue();
        });

        // Add buttons
        Button acceptButton = new Button("Accept", clickEvent -> {
            if (selectedPair != null) {
                handleResponse(session.voteForOwner(selectedPair, userId, true, storeId));
                updateGrid();
            }
        });

        Button rejectButton = new Button("Reject", clickEvent -> {
            if (selectedPair != null) {
                handleResponse(session.voteForOwner(selectedPair, userId, false, storeId));
                updateGrid();
            }
        });

        Button backToStoreButton = new Button("Back To Store", clickEvent -> {
            navigate("store/" + storeId);
        });

        // Initially, buttons are disabled
        acceptButton.setEnabled(false);
        acceptButton.getElement().getThemeList().add("primary");
        rejectButton.setEnabled(false);
        rejectButton.getElement().getThemeList().add("primary");

        // Enable buttons when a row is selected
        grid.asSingleSelect().addValueChangeListener(event -> {
            acceptButton.setEnabled(event.getValue() != null);
            rejectButton.setEnabled(event.getValue() != null);
        });

        // Add the grid and the buttons to the layout
        add(grid, acceptButton, rejectButton, backToStoreButton);

        // Populate the grid with data
        updateGrid();
    }

    private void updateGrid() {
        List<Pair<Integer, Integer>> myOpenVotes = handleResponse(session.getMyOpenVotes(userId, storeId));
        if (myOpenVotes != null) {
            grid.setItems(myOpenVotes);
        } else {
            // Handle the case when myOpenVotes is null
            // For example, you could clear the grid or display a notification
            grid.setItems(new ArrayList<>());
        }
    }


    @Override
    public void setParameter(BeforeEvent event, @OptionalParameter String param) {
        if (param != null && !param.isEmpty()) {
            storeId = Integer.parseInt(param);
        } else {
            storeId = -1;
        }
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        List<WorkerCard> workers = handleResponse(session.getStoreWorkersInfo(1 /*mafhhhiiddd*/, storeId), "");
        if(workers == null)
            workers = new LinkedList<>();
        WorkerCard currentWorker = workers.stream().filter(worker -> worker.userId() == userId).findFirst().orElse(new WorkerCard(userId, null, Set.of()));
        if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.STAFF)){
            event.rerouteTo("");
            return;
        }
        start();


    }

}
