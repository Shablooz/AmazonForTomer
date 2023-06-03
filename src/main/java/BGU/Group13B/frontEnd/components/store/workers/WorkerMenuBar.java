package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
import BGU.Group13B.service.Session;
import com.vaadin.flow.component.avatar.Avatar;
import com.vaadin.flow.component.contextmenu.MenuItem;
import com.vaadin.flow.component.contextmenu.SubMenu;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Label;
import com.vaadin.flow.component.menubar.MenuBar;
import com.vaadin.flow.component.menubar.MenuBarVariant;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

public class WorkerMenuBar extends HorizontalLayout implements ResponseHandler {
    private final WorkerCard workerCard;
    private final WorkerProfileDialog workerProfileDialog;
    private final MenuBar workerMenuBar = new MenuBar();
    private final WorkersVerticalMenuBar workersVerticalMenuBar;

    public WorkerMenuBar(WorkerCard worker, String workerName, WorkersVerticalMenuBar workersVerticalMenuBar){
        super();
        this.workerCard = worker;
        this.workersVerticalMenuBar = workersVerticalMenuBar;
        this.workerProfileDialog = new WorkerProfileDialog(worker, workerName);

        Avatar avatar = new Avatar(workerName);
        workerMenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem menuAvatarItem = workerMenuBar.addItem(avatar);
        SubMenu subMenu = menuAvatarItem.getSubMenu();
        MenuItem profile = subMenu.addItem("Profile");
        MenuItem removeWorker = subMenu.addItem("Remove Worker");

        add(workerMenuBar, new Label(workerName));
        setAlignItems(Alignment.BASELINE);

        //actions
        profile.addClickListener(e -> workerProfileDialog.open());
        removeWorker.addClickListener(e -> this.workersVerticalMenuBar.removeWorker(this));
    }

    public WorkerCard getWorkerCard() {
        return workerCard;
    }
}
