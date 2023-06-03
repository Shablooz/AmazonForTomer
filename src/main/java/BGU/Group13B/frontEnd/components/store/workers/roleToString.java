package BGU.Group13B.frontEnd.components.store.workers;

import BGU.Group13B.backend.User.UserPermissions;

public interface roleToString {

    default String roleToStringTitle(UserPermissions.StoreRole role){
        String roleString = role.toString();
        String[] words = roleString.split("\\s+");
        StringBuilder sb = new StringBuilder();
        for (String word : words) {
            sb.append(Character.toUpperCase(word.charAt(0)));
            sb.append(word.substring(1).toLowerCase());
            sb.append(" ");
        }
        return sb.toString().trim();
    }
}
