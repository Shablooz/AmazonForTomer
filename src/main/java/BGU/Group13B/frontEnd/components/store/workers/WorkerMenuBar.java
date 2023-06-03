package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.User.UserPermissions;
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
    private WorkerCard workerCard;
    private final WorkerProfileDialog workerProfileDialog;
    private final MenuBar workerMenuBar = new MenuBar();
    private final WorkersVerticalMenuBar workersVerticalMenuBar;
    private final String workerName;

    public WorkerMenuBar(Session session, int userId, int storeId, WorkerCard worker, String workerName, WorkersVerticalMenuBar workersVerticalMenuBar, WorkerCard currentWorker){
        super();
        this.workerCard = worker;
        this.workersVerticalMenuBar = workersVerticalMenuBar;
        this.workerProfileDialog = new WorkerProfileDialog(worker, workerName);
        this.workerName = workerName;

        Avatar avatar = new Avatar(workerName);
        workerMenuBar.addThemeVariants(MenuBarVariant.LUMO_TERTIARY_INLINE);

        MenuItem menuAvatarItem = workerMenuBar.addItem(avatar);
        SubMenu subMenu = menuAvatarItem.getSubMenu();
        MenuItem profile = subMenu.addItem("Profile");
        MenuItem editPermissions = subMenu.addItem("Edit Permissions");
        MenuItem removeWorker = subMenu.addItem("Remove Worker");

        add(workerMenuBar, new Label(workerName));
        setAlignItems(Alignment.BASELINE);

        //actions
        profile.addClickListener(e -> workerProfileDialog.open());
        editPermissions.addClickListener(e -> {
            if(workerCard.userId() == userId){
                notifyWarning("Cannot edit your own permissions");
                return;
            }
            if(workerCard.storeRole() != UserPermissions.StoreRole.MANAGER){
                notifyWarning("You can only change permissions of managers");
                return;
            }
            new EditWorkerPermissionsDialog(session, userId, storeId, this).open();
        });
        removeWorker.addClickListener(e -> {
            if(workerCard.userId() == userId){
                notifyWarning("Cannot remove yourself");
                return;
            }
            if(workerCard.storeRole() == UserPermissions.StoreRole.FOUNDER){
                notifyWarning("Cannot remove founder");
                return;
            }
            this.workersVerticalMenuBar.removeWorker(this);
        });


        //visibility
        if(workerCard.userId() == userId){
            editPermissions.setVisible(false);
            removeWorker.setVisible(false);
        }
        else if(workerCard.storeRole() == UserPermissions.StoreRole.FOUNDER){
            editPermissions.setVisible(false);
            removeWorker.setVisible(false);
        }
        else if(workerCard.storeRole() == UserPermissions.StoreRole.OWNER){
            editPermissions.setVisible(false);
        }

        if(!currentWorker.userPermissions().contains(UserPermissions.IndividualPermission.STAFF)){
            editPermissions.setVisible(false);
            removeWorker.setVisible(false);
        }

    }

    public WorkerCard getWorkerCard() {
        return workerCard;
    }

    public String getWorkerName(){
        return workerName;
    }

    public void updateWorkerCard(WorkerCard workerCard){
        this.workerCard = workerCard;
        this.workerProfileDialog.updateWorkerCard(workerCard);
    }
}
