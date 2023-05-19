package BGU.Group13B.service.entity;

public class ReviewService  {
    private  String userName;

    private String review;

    private String score;




    public ReviewService(String userName,String review,String score) {
        this.review = review;
        this.userName = userName;
        this.score=score;

    }

    public String getReview() {
        return review;
    }
    public void setReview(String review) {
        this.review= review;
    }



    public String getUserName() {
        return userName;
    }
    public void setUserName(String userName) {
        this.userName= userName;
    }


    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
