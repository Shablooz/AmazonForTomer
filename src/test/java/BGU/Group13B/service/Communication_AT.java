package BGU.Group13B.service;

import BGU.Group13B.backend.User.Message;
import BGU.Group13B.backend.storePackage.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class Communication_AT extends ProjectTest{


    @Test
    public void openComplaint_Valid()
    {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_2.ordinal()], "complaint", "complaint");
        Message message= session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]);
        assertEquals(message.getHeader(), "complaint");
        String name= session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]);
        assertEquals(message.getSenderId(), name);
    }
    @Test
    public void openComplaint_NotValid()
    {
         Exception exception= assertThrows(Exception.class,()->session.openComplaint(userIds[UsersIndex.GUEST.ordinal()], "complaint", "complaint"));
         assertEquals("BGU.Group13B.backend.storePackage.permissions.NoPermissionException: Only registered users can open complaints",exception.getMessage());
    }
    @Test
    public void getComplaint_Valid()
    {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "complaint", "complaint");
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_2.ordinal()], "complaint2", "complaint2");

        Message message= session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]);
        assertEquals("complaint",message.getHeader());
        String name= session.getUserName(userIds[UsersIndex.STORE_OWNER_1.ordinal()]);
        assertEquals(message.getSenderId(), name);

        session.markMessageAsReadAdmin(userIds[UsersIndex.ADMIN.ordinal()],message.getReceiverId(),message.getSenderId(),message.getMessageId());

        message= session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]);
        assertEquals("complaint2",message.getHeader());
        name= session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]);
        assertEquals(message.getSenderId(), name);
    }
    @Test
    public void getComplaint_NotValid()
    {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "complaint", "complaint");
        Exception exception= assertThrows(Exception.class,()->session.getComplaint(userIds[UsersIndex.STORE_OWNER_2.ordinal()]));
        assertEquals("BGU.Group13B.backend.storePackage.permissions.NoPermissionException: Only admin can read complaints",exception.getMessage());
    }
    @Test
    public void markMessageAsRead_Valid()
    {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "complaint", "complaint");
        Message message= session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]);
        session.markMessageAsReadAdmin(userIds[UsersIndex.ADMIN.ordinal()],message.getReceiverId(),message.getSenderId(),message.getMessageId());
        Exception exception= assertThrows(Exception.class,()->session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]));
        assertEquals("no unread messages",exception.getMessage());
    }
    @Test
    public void markMessageAsRead_NotValid()
    {
        session.openComplaint(userIds[UsersIndex.STORE_OWNER_1.ordinal()], "complaint", "complaint");
        Message message= session.getComplaint(userIds[UsersIndex.ADMIN.ordinal()]);
        session.markMessageAsReadAdmin(userIds[UsersIndex.ADMIN.ordinal()],message.getReceiverId(),message.getSenderId(),message.getMessageId());
        Exception exception= assertThrows(Exception.class,()->session.markMessageAsReadAdmin(userIds[UsersIndex.ADMIN.ordinal()],message.getReceiverId(),message.getSenderId(),message.getMessageId()));
        assertEquals("No unread messages",exception.getMessage());
    }
    @Test
    public void sendMassageAdmin_Valid()
    {
        session.sendMassageAdmin(userIds[UsersIndex.ADMIN.ordinal()], session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]), "massage","massage");
        Message message= session.readMassage(userIds[UsersIndex.STORE_OWNER_2.ordinal()]);
        assertEquals("massage",message.getHeader());
        String name= session.getUserName(userIds[UsersIndex.STORE_OWNER_2.ordinal()]);
        assertEquals(name,message.getReceiverId());
    }


}
