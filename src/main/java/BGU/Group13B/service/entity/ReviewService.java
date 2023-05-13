package BGU.Group13B.service.entity;

public class ReviewService  {
    private  String userName;

    private String review;





    public ReviewService(String userName,String review) {
        this.review = review;
        this.userName = userName;

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
}
