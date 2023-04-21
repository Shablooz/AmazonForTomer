package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.Repositories.Implementations.ReviewRepositoryImpl.ReviewRepoSingle;
import BGU.Group13B.backend.storePackage.Review;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

class IRepositoryReviewTest {

    static IRepositoryReview repositoryReview;
    static final int storeId = 1;
    static final int productId = 1;
    static final int scoreDefault = 1;

    //helper functions
    interface MyRunnableFactory {
        public Runnable createRunnable(int[] integers, char[] chars, String[] strings);
    }

    int[] random_scores(int size) {
        int[] scores = new int[size];
        for (int i = 0; i < size; i++) {
            scores[i] = (int) (Math.random() * 6);
        }
        return scores;
    }

    boolean isSameReview(Review a, Review b) {
        return a.getReview().equals(b.getReview()) && a.getStoreId() == b.getStoreId() && a.getProductId() == b.getProductId() && a.getUserId() == b.getUserId();
    }

    Review[] reviewsCreator(int num) {
        Review[] reviews = new Review[num];
        for (int i = 1; i <= num; i++) {
            String s = "Test" + i;
            reviews[i - 1] = new Review(s, 1, 1, i);
        }
        return reviews;
    }

    private void startAndWait(Thread[] threads) {
        for (int i = 0; i < threads.length; i++)
            threads[i].start();

        for (int i = 0; i < threads.length; i++)
            try {
                threads[i].join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    //end of helper functions

    @BeforeEach
    void setUp() {
        repositoryReview = new ReviewRepoSingle();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void addReview() {
        Review[] review = reviewsCreator(10);
        for (int i = 0; i < 10; i++) {
            repositoryReview.addReview(review[i].getReview(), review[i].getStoreId(), review[i].getProductId(), review[i].getUserId());
        }
        for (Review r : review) {
            assertTrue(isSameReview(r, repositoryReview.getReview(r.getStoreId(), r.getProductId(), r.getUserId())));
        }
    }

    @Test
    void addReviewTwice() {
        Review review = reviewsCreator(1)[0];
        repositoryReview.addReview(review.getReview(), review.getStoreId(), review.getProductId(), review.getUserId());
        assertThrows(Exception.class, () -> repositoryReview.addReview(review.getReview(), review.getStoreId(), review.getProductId(), review.getUserId()));

    }

    @Test
    void addReview_multiThread() {
        Review[] review = reviewsCreator(10);

        MyRunnableFactory factory = (integers, chars, strings) -> () -> {
            Review review1 = review[integers[0]];
            repositoryReview.addReview(review1.getReview(), review1.getStoreId(), review1.getProductId(), review1.getUserId());
        };
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(factory.createRunnable(new int[]{i}, new char[]{'a'}, new String[]{"a"}));
        }
        startAndWait(threads);
        for (Review r : review) {
            assertTrue(isSameReview(r, repositoryReview.getReview(r.getStoreId(), r.getProductId(), r.getUserId())));
        }
    }

    @Test
    void removeReview() {
        Review[] review = reviewsCreator(10);
        for (int i = 0; i < 10; i++) {
            repositoryReview.addReview(review[i].getReview(), review[i].getStoreId(), review[i].getProductId(), review[i].getUserId());
        }
        for (int i = 0; i < 10; i++) {
            repositoryReview.removeReview(review[i].getStoreId(), review[i].getProductId(), review[i].getUserId());
        }
        for (Review r : review) {
            try {
                repositoryReview.getReview(r.getStoreId(), r.getProductId(), r.getUserId());
                fail();
            } catch (Exception e) {
                assertTrue(true);
            }
        }
    }

    @Test
    void getReview() {
        Review[] review = reviewsCreator(10);
        for (int i = 0; i < 10; i++) {
            repositoryReview.addReview(review[i].getReview(), review[i].getStoreId(), review[i].getProductId(), review[i].getUserId());
        }
        for (Review r : review) {
            assertTrue(isSameReview(r, repositoryReview.getReview(r.getStoreId(), r.getProductId(), r.getUserId())));
        }
    }

    @Test
    void getReview_multiThread() {
        Review[] review = reviewsCreator(10);
        for (int i = 0; i < 10; i++) {
            repositoryReview.addReview(review[i].getReview(), review[i].getStoreId(), review[i].getProductId(), review[i].getUserId());
        }
        MyRunnableFactory factory = (integers, chars, strings) -> () -> {
            Review getReview = repositoryReview.getReview(review[integers[0]].getStoreId(), review[integers[0]].getProductId(), review[integers[0]].getUserId());
            assertTrue(isSameReview(review[integers[0]], getReview));
        };
        Thread[] threads = new Thread[10];
        for (int i = 0; i < 10; i++) {
            threads[i] = new Thread(factory.createRunnable(new int[]{i}, null, null));
        }
        startAndWait(threads);

    }

    @Test
    void getProductScore() {
        int[] scores = random_scores(10);
        for (int i = 0; i < scores.length; i++)
            repositoryReview.addAndSetProductScore(storeId, productId, i, scores[i]);
        float res = (float) Arrays.stream(scores).sum() / scores.length;
        assertEquals(res, repositoryReview.getProductScore(storeId, productId));

    }

    @Test
    void addAndSetProductScore_multiThread() {
        int[] scores = random_scores(10);
        float res = (float) Arrays.stream(scores).sum() / scores.length;
        IStoreScoreTest.MyRunnableFactory myRunnableFactory = (integers, chars, strings) -> () -> {
            repositoryReview.addAndSetProductScore(1, 1, integers[0], scores[integers[0]]);
        };
        Thread[] threads = new Thread[scores.length];

        for (int i = 0; i < scores.length; i++) {
            int[] integers = {i};
            threads[i] = new Thread(myRunnableFactory.createRunnable(integers, null, null));
        }
        startAndWait(threads);
        assertEquals(res, repositoryReview.getProductScore(1, 1));
    }


    @Test
    void addAndSetProductScore() {
        int sum = 0;
        int counter = 0;
        assertEquals(sum, repositoryReview.getProductScore(storeId, productId));
        repositoryReview.addAndSetProductScore(storeId, productId, counter, scoreDefault);
        counter++;
        sum += scoreDefault;
        assertEquals((float) sum / counter, repositoryReview.getProductScore(storeId, productId));
        repositoryReview.addAndSetProductScore(storeId, productId, counter, scoreDefault * 5);
        counter++;
        sum += scoreDefault * 5;
        assertEquals((float) sum / counter, repositoryReview.getProductScore(storeId, productId));
        repositoryReview.addAndSetProductScore(storeId, productId, counter, scoreDefault * 5);
        counter++;
        sum += scoreDefault * 5;
        assertEquals((float) sum / counter, repositoryReview.getProductScore(storeId, productId));
    }

    @Test
    void removeProductScore_multiThread() {
        int[] scores = random_scores(10);
        float res = (float) Arrays.stream(scores).sum() / scores.length;
        for (int i = 0; i < scores.length; i++)
            repositoryReview.addAndSetProductScore(storeId, productId, i, scores[i]);

        IStoreScoreTest.MyRunnableFactory myRunnableFactory = (integers, chars, strings) -> () -> {
            repositoryReview.removeProductScore(storeId, productId, integers[0]);
        };
        Thread[] threads = new Thread[scores.length];
        for (int i = 0; i < scores.length; i++) {
            int[] integers = {i};
            threads[i] = new Thread(myRunnableFactory.createRunnable(integers, null, null));
        }
        startAndWait(threads);
        assertEquals(0, repositoryReview.getProductScore(storeId, productId));

    }

}