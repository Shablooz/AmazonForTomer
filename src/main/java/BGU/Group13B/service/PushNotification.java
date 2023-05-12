package BGU.Group13B.service;

import BGU.Group13B.frontEnd.components.SessionToIdMapper;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.server.VaadinSession;

public class PushNotification {

   /* public static boolean pushNotification(String message,int id)
    {

        VaadinSession idSession=SessionToIdMapper.getInstance().getSession(id);
        if(idSession==null)
            return false;
        idSession.access(()->{
            Notification notification= new Notification(message,3000);
            notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
            notification.open();
        });
        return true;

    }*/
   public static boolean pushNotification(String message,int id) {
       VaadinSession idSession = SessionToIdMapper.getInstance().getSession(id);

       if (idSession == null) {
           return false;
       }

           idSession.access(() -> {
               Notification notification = new Notification(message, 3000);
               notification.addThemeVariants(NotificationVariant.LUMO_PRIMARY);
               notification.open();
           });


       return true;
   }
}
