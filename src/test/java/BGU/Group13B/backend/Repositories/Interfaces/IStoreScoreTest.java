package BGU.Group13B.backend.Repositories.Interfaces;

import BGU.Group13B.backend.Repositories.Implementations.IStoreScoreRepository.StoreScoreSingle;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

class IStoreScoreTest {

    static IStoreScore storeScore;
    static final int userId = 1;
    static final int storeId = 1;
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

    private void startAndWait(Thread[] threads) {
        for (Thread thread : threads) thread.start();

        for (Thread thread : threads)
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
    }

    //end of helper functions

    @BeforeEach
    void setUp() {
        storeScore = new StoreScoreSingle();
    }

    @AfterEach
    void tearDown() {
    }

    @RepeatedTest(15)
    void addStoreScore() {
        int sum = 0;
        int counter = 0;
        assertEquals(sum, storeScore.getStoreScore(storeId));
        storeScore.addStoreScore(counter, storeId, scoreDefault);
        counter++;
        sum += scoreDefault;
        assertEquals((float) sum / counter, storeScore.getStoreScore(storeId));
        storeScore.addStoreScore(counter, storeId, scoreDefault * 5);
        counter++;
        sum += scoreDefault * 5;
        assertEquals((float) sum / counter, storeScore.getStoreScore(storeId));
        storeScore.addStoreScore(counter, storeId, scoreDefault * 5);
        counter++;
        sum += scoreDefault * 5;
        assertEquals((float) sum / counter, storeScore.getStoreScore(storeId));

    }

    @RepeatedTest(15)
    void addStoreScore_MultiThread() {
        int[] scores = random_scores(10);
        float res = (float) Arrays.stream(scores).sum() / scores.length;
        MyRunnableFactory myRunnableFactory = (integers, chars, strings) -> () -> {
            storeScore.addStoreScore(integers[0], storeId, scores[integers[0]]);
        };
        Thread[] threads = new Thread[scores.length];

        for (int i = 0; i < scores.length; i++) {
            int[] integers = {i};
            threads[i] = new Thread(myRunnableFactory.createRunnable(integers, null, null));
        }
        startAndWait(threads);
        assertEquals(res, storeScore.getStoreScore(storeId));
    }

    @RepeatedTest(15)
    void removeStoreScore_MultiThread() {
        int[] scores = random_scores(10);
        float res = (float) Arrays.stream(scores).sum() / scores.length;
        for (int i = 0; i < scores.length; i++)
            storeScore.addStoreScore(i, storeId, scores[i]);

        MyRunnableFactory myRunnableFactory = (integers, chars, strings) -> () -> {
            storeScore.removeStoreScore(integers[0], storeId);
        };
        Thread[] threads = new Thread[scores.length];
        for (int i = 0; i < scores.length; i++) {
            int[] integers = {i};
            threads[i] = new Thread(myRunnableFactory.createRunnable(integers, null, null));
        }
        startAndWait(threads);
        assertEquals(0, storeScore.getStoreScore(storeId));
        assertEquals(0, storeScore.getNumberOfScores(storeId));
    }

    @RepeatedTest(15)
    void modifyStoreScore() {
        int[] scores = random_scores(10);
        for (int i = 0; i < scores.length; i++)
            storeScore.addStoreScore(i, storeId, scores[i]);

        MyRunnableFactory myRunnableFactory = (integers, chars, strings) -> () -> {
            storeScore.modifyStoreScore(integers[0], storeId, integers[0] % 6);
        };
        Thread[] threads = new Thread[scores.length];
        for (int i = 0; i < scores.length; i++) {
            int[] integers = {i};
            threads[i] = new Thread(myRunnableFactory.createRunnable(integers, null, null));
        }
        startAndWait(threads);
        float res = storeScore.getStoreScore(storeId);
        assertEquals(2.1f, res);
        assertEquals(10, storeScore.getNumberOfScores(storeId));
    }



    @RepeatedTest(15)
    void getStoreScore() {
        int[] scores = random_scores(10);
        for (int i = 0; i < scores.length; i++)
            storeScore.addStoreScore(i, storeId, scores[i]);
        float res = (float) Arrays.stream(scores).sum() / scores.length;
        assertEquals(res, storeScore.getStoreScore(storeId));
    }
}