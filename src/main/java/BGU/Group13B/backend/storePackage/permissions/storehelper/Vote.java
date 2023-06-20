package BGU.Group13B.backend.storePackage.permissions.storehelper;

import jakarta.persistence.*;
import jakartac.Cache.Cache;

import java.util.List;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    private int firstKey; // first integer in Pair THE CANDIDATE
    private int secondKey; // second integer in Pair THE APPOINTER

    @Cache(policy = Cache.PolicyType.LRU)
    @ElementCollection(fetch = FetchType.EAGER)
    private List<Integer> votes;

    public void setCandidateId(int candidateId) {
        firstKey = candidateId;
    }

    public void setAppointerId(int appointerId) {
        secondKey = appointerId;
    }

    public void setVoteList(List<Integer> idOfOwnersAndFounder) {
        votes = idOfOwnersAndFounder;
    }

    public List<Integer> getVoteList() {
        return votes;
    }

    public int getCandidateId() {
        return firstKey;
    }

    public int getAppinterId() {
        return secondKey;
    }


}

