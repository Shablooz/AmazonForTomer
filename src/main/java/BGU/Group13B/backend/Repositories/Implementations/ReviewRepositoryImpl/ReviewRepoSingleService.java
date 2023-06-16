package BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl;

import BGU.Group13B.service.SingletonCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ReviewRepoSingleService {

    private ReviewRepoSingleJPA reviewRepoSingleJPA;

    @Autowired
    public ReviewRepoSingleService(ReviewRepoSingleJPA reviewRepoSingleJPA) {
        this.reviewRepoSingleJPA = reviewRepoSingleJPA;
    }

    public ReviewRepoSingleService(){

    }

    public void save(ReviewRepoSingle reviewRepoSingle){
       SingletonCollection.setReviewRepository( reviewRepoSingleJPA.save(reviewRepoSingle));
    }

    public void delete(ReviewRepoSingle reviewRepoSingle){
        reviewRepoSingleJPA.delete(reviewRepoSingle);
    }

    public ReviewRepoSingle getReviewRepoSingleJPA() {
        return reviewRepoSingleJPA.findById(1).orElse(null);
    }
}
