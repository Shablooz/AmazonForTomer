package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.frontEnd.ResponseHandler;
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
    private final WorkerCard worker;
    private final WorkerProfileDialog workerProfileDialog;
    private final MenuBar workerMenuBar = new MenuBar();

    public WorkerMenuBar(WorkerCard worker, String username){
        super();
        this.worker = worker;
        this.workerProfileDialog = new WorkerProfileDialog(worker, username);

        Avatar avatar = new Avatar(username);
        workerMenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem menuAvatarItem = workerMenuBar.addItem(avatar);
        SubMenu subMenu = menuAvatarItem.getSubMenu();
        MenuItem profile = subMenu.addItem("Profile");
        MenuItem removeWorker = subMenu.addItem("Remove Worker");

        add(workerMenuBar, new Label(username));
        setAlignItems(Alignment.BASELINE);

        //actions
        profile.addClickListener(e -> workerProfileDialog.open());
        removeWorker.addClickListener(e -> notifyInfo("not implemented yet in GUI :("));
    }
}
