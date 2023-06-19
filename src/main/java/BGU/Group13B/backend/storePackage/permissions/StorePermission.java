package BGU.Group13B.backend.storePackage.permissions;

import BGU.Group13B.backend.Pair;
import BGU.Group13B.backend.Repositories.Interfaces.IUserPermissionRepository;
import BGU.Group13B.backend.User.UserPermissions;
import BGU.Group13B.backend.User.UserPermissions.StoreRole;
import BGU.Group13B.backend.storePackage.Store;
import BGU.Group13B.backend.storePackage.WorkerCard;
import BGU.Group13B.backend.storePackage.permissions.storehelper.SetEnum;
import BGU.Group13B.backend.storePackage.permissions.storehelper.SetInteger;
import BGU.Group13B.backend.storePackage.permissions.storehelper.SetString;
import BGU.Group13B.service.SingletonCollection;
import jakarta.persistence.*;

import java.lang.reflect.Method;
import java.util.*;
import java.util.stream.Collectors;

@Entity
public class StorePermission {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int founderId;
    private HashMap<Pair<Integer,Integer>, List<Integer>> votes;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "StorePermission_userStoreFunctionPermissions",
            joinColumns = {@JoinColumn(name = "StorePermission_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "SetString_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "permissionNumber")
    private Map<Integer/*permissionNumber*/, SetString> userStoreFunctionPermissions;//THIS ONE INCLUDE WHAT EACH ADDITIONAL ROLE INCLUDES

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "StorePermission_defaultStoreRoleFunctionalities",
            joinColumns = {@JoinColumn(name = "StorePermission_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "SetString_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "storeRole_id")
    private  Map<StoreRole, SetString> defaultStoreRoleFunctionalities;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "StorePermission_StoreRole",
            joinColumns = {@JoinColumn(name = "StorePermission_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "userId")
    @Column(name = "storeRole")
    private  Map<Integer/*userId*/, StoreRole> userToStoreRole;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "StorePermission_appointedOwnersMap",
            joinColumns = {@JoinColumn(name = "StorePermission_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "SetInteger_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "appointed_by_id")
    private  Map<Integer/*got appointed by*/, SetInteger/*appointee*/> appointedOwnersMap;

    @OneToMany(cascade = CascadeType.ALL,fetch = FetchType.EAGER,orphanRemoval = true)
    @JoinTable(name = "StorePermission_userToIndividualPermissions",
            joinColumns = {@JoinColumn(name = "StorePermission_id", referencedColumnName = "id")},
            inverseJoinColumns = {@JoinColumn(name = "SetEnum_id", referencedColumnName = "id")})
    @MapKeyColumn(name = "user_id")
    private  Map<Integer/*userId*/, SetEnum> userToIndividualPermissions;//THIS ONE EXPLAINS THE "ADDITIONAL ROLES" OF EACH MANAGER
    @Transient
    private  IUserPermissionRepository userPermissionRepository = SingletonCollection.getUserPermissionRepository();


    public StorePermission(int founderId) {
        this.founderId= founderId;
        userStoreFunctionPermissions = new HashMap<>();
        defaultStoreRoleFunctionalities = new HashMap<>();
        initDefaultStoreRoleFunctionalities();
        userToStoreRole = new HashMap<>();
        appointedOwnersMap = new HashMap<>();
        userToStoreRole.put(founderId, StoreRole.FOUNDER);
        userToIndividualPermissions = new HashMap<>();
        SetEnum founderPermissions = new SetEnum();
        votes = new HashMap<>();
        founderPermissions.add(UserPermissions.IndividualPermission.STOCK);
        founderPermissions.add(UserPermissions.IndividualPermission.MESSAGES);
        founderPermissions.add(UserPermissions.IndividualPermission.POLICIES);
        founderPermissions.add(UserPermissions.IndividualPermission.AUCTION);
        founderPermissions.add(UserPermissions.IndividualPermission.WORKERS_INFO);
        founderPermissions.add(UserPermissions.IndividualPermission.HISTORY);
        founderPermissions.add(UserPermissions.IndividualPermission.STAFF);
        founderPermissions.add(UserPermissions.IndividualPermission.FONLY);
        founderPermissions.add(UserPermissions.IndividualPermission.STATS);
        userToIndividualPermissions.put(founderId, founderPermissions);
        initIndividualPermissionsFunctionalities();
        this.founderId=founderId;
    }

    public StorePermission() {
        this.founderId= 0;
        userStoreFunctionPermissions = new HashMap<>();
        defaultStoreRoleFunctionalities = new HashMap<>();
        userToStoreRole = new HashMap<>();
        appointedOwnersMap = new HashMap<>();
        userToIndividualPermissions = new HashMap<>();
    }



    private void initDefaultStoreRoleFunctionalities() {
        /*
         * TODO: complete the default permissions for each role
         * */
        SetString storeManagerFunctions = new SetString();
        SetString storeOwnerFunctions = new SetString();
        SetString storeFounderFunctions =new SetString();
        var storeClass = Store.class;
        for (Method method : storeClass.getMethods()) {
            if (method.isAnnotationPresent(DefaultManagerFunctionality.class))
                storeManagerFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(DefaultOwnerFunctionality.class))
                storeOwnerFunctions.add(method.getName());

            if (method.isAnnotationPresent(DefaultFounderFunctionality.class))
                storeFounderFunctions.add(method.getName());
        }
        defaultStoreRoleFunctionalities.put(StoreRole.MANAGER, storeManagerFunctions);
        defaultStoreRoleFunctionalities.put(StoreRole.OWNER, storeOwnerFunctions);
        defaultStoreRoleFunctionalities.put(StoreRole.FOUNDER, storeFounderFunctions);
    }

    private void initIndividualPermissionsFunctionalities() {
        SetString stockFunctions = new SetString();
        SetString messagesFunctions = new SetString();
        SetString policiesFunctions = new SetString();
        SetString auctionFunctions = new SetString();
        SetString infoFunctions = new SetString();
        SetString historyFunctions = new SetString();
        SetString staffFunctions = new SetString();
        SetString founderFunctions = new SetString();
        SetString statsFunctions = new SetString();
        var storeClass = Store.class;
        for (Method method : storeClass.getMethods()) {
            if (method.isAnnotationPresent(StockPermission.class))
                stockFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(MessagesPermission.class))
                messagesFunctions.add(method.getName());

            if (method.isAnnotationPresent(PoliciesPermission.class))
                policiesFunctions.add(method.getName());

            if (method.isAnnotationPresent(AuctionPermission.class))
                auctionFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(WorkersInfoPermission.class))
                infoFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(HistoryPermission.class))
                historyFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(StaffPermission.class))
                staffFunctions.add(getFunctionName(method));

            if (method.isAnnotationPresent(DefaultFounderFunctionality.class))
                founderFunctions.add(method.getName());

            if(method.isAnnotationPresent(StatsPermission.class))
                statsFunctions.add(getFunctionName(method));
        }
        userStoreFunctionPermissions.put(UserPermissions.IndividualPermission.STOCK.ordinal(), stockFunctions);
        userStoreFunctionPermissions.put(UserPermissions.IndividualPermission.MESSAGES.ordinal(), messagesFunctions);
        userStoreFunctionPermissions.put(UserPermissions.IndividualPermission.POLICIES.ordinal(), policiesFunctions);
        userStoreFunctionPermissions.put(UserPermissions.IndividualPermission.AUCTION.ordinal(), auctionFunctions);
        userStoreFunctionPermissions.put(UserPermissions.IndividualPermission.WORKERS_INFO.ordinal(), infoFunctions);
        userStoreFunctionPermissions.put(UserPermissions.IndividualPermission.HISTORY.ordinal(), historyFunctions);
        userStoreFunctionPermissions.put(UserPermissions.IndividualPermission.STAFF.ordinal(), staffFunctions);
        userStoreFunctionPermissions.put(UserPermissions.IndividualPermission.FONLY.ordinal(), founderFunctions);
        userStoreFunctionPermissions.put(UserPermissions.IndividualPermission.STATS.ordinal(), statsFunctions);
    }

    private static String getFunctionName(Method method) {
        var name = method.getName();
        return getFunctionName(name);
    }

    public static String getFunctionName(String methodName) {
        var index = methodName.lastIndexOf(".");
        return methodName.substring(index + 1);
    }

    public boolean checkPermission(int userId, boolean isStoreHidden) throws NoPermissionException {
        //check if the user is admin
        if(hasAdminPermission(userId)){
            return true;
        }

        String fullMethodCallName = Thread.currentThread().getStackTrace()[2].getMethodName();
        String storeFunctionName = getFunctionName(fullMethodCallName);

        //will throw exception if the store is hidden and the user doesn't have permission to view it
        //I did it here in order to avoid future bugs that can happen if we forget to check it in every function
        validateStoreVisibility(userId, isStoreHidden);

        //check for the user's permissions
        if (userToIndividualPermissions.containsKey(userId)) {
            boolean found = false;
            SetEnum individualPermissions = userToIndividualPermissions.get(userId);
            for (UserPermissions.IndividualPermission ip: individualPermissions.getSet()) {
                if(userStoreFunctionPermissions.get(ip.ordinal()).contains(storeFunctionName))
                    found = true;
            }
            return found;
        } /*else if (userToStoreRole.containsKey(userId)) {
            return defaultStoreRoleFunctionalities.get(userToStoreRole.get(userId)).contains(storeFunctionName);
        }*/
        return false;
    }

    private boolean hasAdminPermission(int userId)  {
        return getUserPermissionRepository().getUserPermission(userId).getUserPermissionStatus()
                .equals(UserPermissions.UserPermissionStatus.ADMIN);
    }

    public void validateStoreVisibility(int userId, boolean isStoreHidden) throws NoPermissionException {
        if (isStoreHidden && !this.hasAccessWhileHidden(userId))
            throw new NoPermissionException("This store does not exist");
    }

    public boolean hasAccessWhileHidden(int userId) {
        return hasAdminPermission(userId) || (userToStoreRole.containsKey(userId) &&
                (userToStoreRole.get(userId) == StoreRole.FOUNDER || userToStoreRole.get(userId) == StoreRole.OWNER));
    }

    public Set<Integer/*userId*/> getAllUsersWithPermission(String storeFunctionName) {
        Set<Integer> usersWithPermission = new HashSet<>();
        //all users with roles that have that permission
        for(var key : defaultStoreRoleFunctionalities.keySet()){
            if(defaultStoreRoleFunctionalities.get(key).contains(storeFunctionName)){
                for (Map.Entry<Integer, StoreRole> entry : userToStoreRole.entrySet()) {
                    if (entry.getValue() == key) {
                        usersWithPermission.add(entry.getKey());
                    }
                }
            }
        }
        //all users who got this permission individually
        Set<Integer> permissionNumber = new HashSet<>();
        for (var entry : userStoreFunctionPermissions.entrySet()) {
            if (entry.getValue().contains(storeFunctionName)) {
                permissionNumber.add(entry.getKey());
            }
        }
        for (var entry : userToIndividualPermissions.entrySet()) {
            for(int pn : permissionNumber){
                UserPermissions.IndividualPermission permission = UserPermissions.IndividualPermission.values()[pn];
                if (entry.getValue().contains(permission)) {
                    usersWithPermission.add(entry.getKey());
                }
            }
        }
        return usersWithPermission;
    }

    public void addOwnerPermission(int newOwnerId, int appointerId) throws ChangePermissionException {
        //check if not already in that role
        StoreRole userRole = userToStoreRole.get(newOwnerId);
        if (userRole == StoreRole.FOUNDER || userRole == StoreRole.OWNER)
            throw new ChangePermissionException("Cannot grant owner title to a user who is already an owner");

        userToStoreRole.put(newOwnerId, StoreRole.OWNER);
        SetEnum newSet = new SetEnum();
        newSet.add(UserPermissions.IndividualPermission.STOCK);
        newSet.add(UserPermissions.IndividualPermission.MESSAGES);
        newSet.add(UserPermissions.IndividualPermission.HISTORY);
        newSet.add(UserPermissions.IndividualPermission.AUCTION);
        newSet.add(UserPermissions.IndividualPermission.WORKERS_INFO);
        newSet.add(UserPermissions.IndividualPermission.POLICIES);
        newSet.add(UserPermissions.IndividualPermission.STAFF);
        newSet.add(UserPermissions.IndividualPermission.STATS);
        userToIndividualPermissions.put(newOwnerId, newSet);

        // Check if the appointerId is already present in the appointedOwnersMap
        SetInteger appointees = appointedOwnersMap.get(appointerId);

        // If appointerId is not present, create a new set and add it to the map
        if (appointees == null) {
            appointees = new SetInteger();
            appointedOwnersMap.put(appointerId, appointees);
        }

        // Add the newOwnerId to the set of appointees
        appointees.add(newOwnerId);
        save();
    }

    public Set<Integer> removeOwnerPermission(int removeOwnerId, int removerId, boolean removeManager) throws ChangePermissionException {
        Set<Integer> set = removeOwnerPermissionHelper(removeOwnerId, removerId, removeManager);
        save();
        return set;
    }

    public Set<Integer> removeOwnerPermissionHelper(int removeOwnerId, int removerId, boolean removeManager) throws ChangePermissionException {
        Set<Integer> removeUsersId = new HashSet<>();
        StoreRole removeOwnerRole = userToStoreRole.get(removeOwnerId);

        if (!(removeOwnerRole == StoreRole.OWNER || (removeManager && removeOwnerRole == StoreRole.MANAGER))) {
            throw new ChangePermissionException("Cannot remove owner title from a user who is not an owner");
        } else if (appointedOwnersMap.getOrDefault(removerId, new SetInteger()).contains(removeOwnerId)) {
            goodByeAll(removeOwnerId);
            SetInteger underlingsToRemove = appointedOwnersMap.get(removeOwnerId);
            if (underlingsToRemove != null) {
                for (Integer underlingId : underlingsToRemove.getSet()) {
                    removeUsersId.add(underlingId);
                    Set<Integer> recursiveFiringList = removeOwnerPermissionHelper(underlingId, removeOwnerId, true);
                    removeUsersId.addAll(recursiveFiringList);
                }
            }
            appointedOwnersMap.get(removerId).remove(removeOwnerId);
            userToStoreRole.remove(removeOwnerId);
            removeUsersId.add(removeOwnerId);
            if(userToIndividualPermissions.containsKey(removeOwnerId))
                userToIndividualPermissions.get(removeOwnerId).clear();
        } else {
            throw new ChangePermissionException("Cannot remove owner title from an owner you didn't appoint");
        }
        return removeUsersId;
    }

    public void addManagerPermission(int newManagerId, int appointerId) throws ChangePermissionException {
        //check if not already in that role
        StoreRole userRole = userToStoreRole.get(newManagerId);
        if (userRole == StoreRole.FOUNDER || userRole == StoreRole.OWNER || userRole == StoreRole.MANAGER) {
            throw new ChangePermissionException("Cannot grant manager title to a user who is aleady a manager");
        } else {
            userToStoreRole.put(newManagerId, StoreRole.MANAGER);
            if(userToIndividualPermissions.containsKey(newManagerId)){
                userToIndividualPermissions.get(newManagerId).clear();
                userToIndividualPermissions.get(newManagerId).add(UserPermissions.IndividualPermission.HISTORY);
            }
            else{
                SetEnum newSet = new SetEnum();
                newSet.add(UserPermissions.IndividualPermission.HISTORY);
                userToIndividualPermissions.put(newManagerId, newSet);
            }
            // Check if the appointerId is already present in the appointedOwnersMap
            SetInteger appointees = appointedOwnersMap.get(appointerId);

            // If appointerId is not present, create a new set and add it to the map
            if (appointees == null) {
                appointees = new SetInteger();
                appointedOwnersMap.put(appointerId, appointees);
            }

            // Add the newOwnerId to the set of appointees
            appointees.add(newManagerId);
            save();
        }
    }

    public void removeManagerPermission(int removeManagerId, int removerId) throws ChangePermissionException {
        StoreRole removeManagerRole = userToStoreRole.get(removeManagerId);

        if (removeManagerRole != StoreRole.MANAGER) {
            throw new ChangePermissionException("Cannot remove manager title from a user who is not a manager");
        } else if (appointedOwnersMap.getOrDefault(removerId,new SetInteger()).contains(removeManagerId)) {
            appointedOwnersMap.get(removerId).remove(removeManagerId);
            userToStoreRole.remove(removeManagerId);
            if(userToIndividualPermissions.containsKey(removeManagerId))
                userToIndividualPermissions.get(removeManagerId).clear();
        } else {
            throw new ChangePermissionException("Cannot remove manager title from an owner you didn't appoint");
        }
    }

    public void addIndividualPermission(int userId, UserPermissions.IndividualPermission individualPermission) throws ChangePermissionException {
        StoreRole userRole = userToStoreRole.get(userId);
        if (userRole != StoreRole.MANAGER)
            throw new ChangePermissionException("Cannot grant an individual permission to a user who is no a manager");
        if(userToIndividualPermissions.containsKey(userId))
            userToIndividualPermissions.get(userId).add(individualPermission);
        else{
            SetEnum newSet = new SetEnum();
            newSet.add(individualPermission);
            userToIndividualPermissions.put(userId, newSet);
        }
        save();
    }

    public void removeIndividualPermission(int userId, UserPermissions.IndividualPermission individualPermission) throws ChangePermissionException {
        StoreRole userRole = userToStoreRole.get(userId);
        if (userRole != StoreRole.MANAGER)
            throw new ChangePermissionException("Cannot grant an individual permission to a user who is no a manager");
        if(userToIndividualPermissions.containsKey(userId))
            userToIndividualPermissions.get(userId).remove(individualPermission);
        save();
    }

    public List<WorkerCard> getWorkersInfo() {
        List<WorkerCard> workerCards = new ArrayList<>();
        for (Map.Entry<Integer, StoreRole> entry : userToStoreRole.entrySet()) {
            Integer userId = entry.getKey();
            StoreRole role = entry.getValue();
            SetEnum userPermissions = userToIndividualPermissions.get(userId);
            WorkerCard workerCard = new WorkerCard(userId, role, userPermissions.getSet());
            workerCards.add(workerCard);
        }
        return workerCards;
    }

    public StoreRole getUserRole(int userId) {
        return userToStoreRole.get(userId);
    }

    public void clearForTest() {
        userToStoreRole.clear();
        appointedOwnersMap.clear();
        userToIndividualPermissions.clear();
    }

    public int getStoreFounder() {
       return userToStoreRole.entrySet().stream()
                .filter(entry -> entry.getValue() == StoreRole.FOUNDER)
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(-1);
    }

    public List<Integer> getStoreOwners(){
        System.out.println("HERE");
        return userToStoreRole.entrySet().stream()
                .filter(entry -> entry.getValue() == StoreRole.OWNER)
                .map(Map.Entry::getKey)
                .collect(Collectors.toList());
    }

    public Map<Integer/*userId*/, SetEnum> getUserToIndividualPermissions(){
        return userToIndividualPermissions;
    }
    public List<Integer> getAllUsersWithIndividualPermission(UserPermissions.IndividualPermission individualPermission){
        List<Integer> users = new ArrayList<>();
        for(Map.Entry<Integer, SetEnum> entry : userToIndividualPermissions.entrySet()){
            if(entry.getValue().contains(individualPermission))
                users.add(entry.getKey());
        }
        return users;
    }

    public List<Integer> newVoteOwnerPermission(int newOwnerId, int appointerId) throws ChangePermissionException {
        //check if not already in that role
        StoreRole userRole = userToStoreRole.get(newOwnerId);
        if (userRole == StoreRole.FOUNDER || userRole == StoreRole.OWNER)
            throw new ChangePermissionException("Cannot grant owner title to a user who is already an owner");
        Pair<Integer, Integer> newAndAppointerIds = new Pair<>(newOwnerId, appointerId);
        List<Integer> idOfOwnersAndFounder = new ArrayList<>();
        for (Map.Entry<Integer, UserPermissions.StoreRole> entry : userToStoreRole.entrySet()) {
            UserPermissions.StoreRole role = entry.getValue();
            if (role == UserPermissions.StoreRole.FOUNDER || role == UserPermissions.StoreRole.OWNER) {
                idOfOwnersAndFounder.add(entry.getKey());
            }
        }
        votes.put(newAndAppointerIds, idOfOwnersAndFounder);
        return idOfOwnersAndFounder;
    }

    public boolean updateVote(Pair<Integer, Integer> newAndAppointerIds, int voterId, boolean accept) throws ChangePermissionException {
        if(accept){
           return removeMeFromVote(newAndAppointerIds, voterId);
        }
        else {
            cancelVote(newAndAppointerIds, voterId);
        }
        return false;
    }

    public List<Pair<Integer, Integer>> getMyOpenVotes(int userId){
        List<Pair<Integer,Integer>> myVotes = new ArrayList<>();
        for (Map.Entry<Pair<Integer,Integer>, List<Integer>> entry : votes.entrySet()) {
            List<Integer> userList = entry.getValue();
            if (userList.contains(userId)) {
                myVotes.add(entry.getKey());
            }
        }
        return myVotes;
    }

    public boolean removeMeFromVote(Pair<Integer, Integer> newAndAppointerIds, int voterId){
        Integer xVoterId = voterId;
        for (Map.Entry<Pair<Integer,Integer>, List<Integer>> entry : votes.entrySet()) {
            if(Objects.equals(entry.getKey().getFirst(), newAndAppointerIds.getFirst()) && Objects.equals(entry.getKey().getSecond(), newAndAppointerIds.getSecond())){
                if(entry.getValue().contains(voterId))
                    entry.getValue().remove(xVoterId);
                if(entry.getValue().isEmpty())
                    return true;
            }
        }
        return false;
    }

    public void cancelVote(Pair<Integer, Integer> newAndAppointerIds, int voterId){
        Pair<Integer, Integer> deleteMe = null;
        for (Map.Entry<Pair<Integer,Integer>, List<Integer>> entry : votes.entrySet()) {
            if(Objects.equals(entry.getKey().getFirst(), newAndAppointerIds.getFirst()) && Objects.equals(entry.getKey().getSecond(), newAndAppointerIds.getSecond())){
                if(entry.getValue().contains(voterId))
                    deleteMe = entry.getKey();
            }
        }
        votes.remove(deleteMe);
    }

    public void goodByeAll(int firedId) throws ChangePermissionException {
        Integer xFiredId = firedId;
        List<Pair<Integer, Integer>> deleteUs = new LinkedList<>();
        for (Map.Entry<Pair<Integer,Integer>, List<Integer>> entry : votes.entrySet()) {
            if(entry.getValue().contains(firedId)){
                entry.getValue().remove(xFiredId);
                if(entry.getValue().isEmpty())
                    addOwnerPermission(entry.getKey().getFirst(), entry.getKey().getSecond());
            }
            if(entry.getKey().getSecond().equals(xFiredId))
                deleteUs.add(entry.getKey());
        }
        for(Pair<Integer, Integer> pair : deleteUs){
            votes.remove(pair);
        }
    }

    public void emptyMaps(){
        userToStoreRole.clear();
        appointedOwnersMap.clear();
        userStoreFunctionPermissions.clear();
        defaultStoreRoleFunctionalities.clear();
        save();
    }
    public int getFounderId() {
        return this.founderId;
    }


    //getters and setters

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }



    public void setFounderId(int founderId) {
        this.founderId = founderId;
    }


    public Map<Integer, StoreRole> getUserToStoreRole() {
        return userToStoreRole;
    }

    public void setUserToStoreRole(HashMap<Integer, StoreRole> userToStoreRole) {
        this.userToStoreRole = userToStoreRole;
    }

    public Map<Integer, SetString> getUserStoreFunctionPermissions() {
        return userStoreFunctionPermissions;
    }

    public void setUserStoreFunctionPermissions(Map<Integer, SetString> userStoreFunctionPermissions) {
        this.userStoreFunctionPermissions = userStoreFunctionPermissions;
    }

    public Map<StoreRole, SetString> getDefaultStoreRoleFunctionalities() {
        return defaultStoreRoleFunctionalities;
    }

    public void setDefaultStoreRoleFunctionalities(Map<StoreRole, SetString> defaultStoreRoleFunctionalities) {
        this.defaultStoreRoleFunctionalities = defaultStoreRoleFunctionalities;
    }

    public void setUserToStoreRole(Map<Integer, StoreRole> userToStoreRole) {
        this.userToStoreRole = userToStoreRole;
    }



    public void setUserToIndividualPermissions(Map<Integer, SetEnum> userToIndividualPermissions) {
        this.userToIndividualPermissions = userToIndividualPermissions;
    }


    public Map<Integer, SetInteger> getAppointedOwnersMap() {
        return appointedOwnersMap;
    }

    public void setAppointedOwnersMap(Map<Integer, SetInteger> appointedOwnersMap) {
        this.appointedOwnersMap = appointedOwnersMap;
    }

    public IUserPermissionRepository getUserPermissionRepository() {
        userPermissionRepository = SingletonCollection.getUserPermissionRepository();
        return userPermissionRepository;
    }

    public void setUserPermissionRepository(IUserPermissionRepository userPermissionRepository) {
        this.userPermissionRepository = userPermissionRepository;
    }

    private void save(){
        SingletonCollection.getStorePermissionRepository().save();
    }
}