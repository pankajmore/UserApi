package com.userApi;

import org.junit.Test;

import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by pankaj on 5/12/16.
 */
public class ApiTest {
    @Test
    public void simpleTest() {
        final User userA = Api.addOrGet("A", "A@foo.com");
        final User userB = Api.addOrGet("B", "B@foo.com");
        final User userC = Api.addOrGet("C", "C@foo.com");
        final User userD = Api.addOrGet("D", "D@foo.com");
        Api.addScoreForUser("A@foo.com", "C1", 100.0);
        Api.addScoreForUser("A@foo.com", "C2", 100.0);
        Api.addScoreForUser("B@foo.com", "C1", 100.0);
        Api.addScoreForUser("B@foo.com", "C2", 100.0);
        Api.addScoreForUser("C@foo.com", "C1", 100.0);
        Api.addScoreForUser("C@foo.com", "C2", 100.0);
        Api.addScoreForUser("D@foo.com", "C1", 100.0);
        Api.addScoreForUser("D@foo.com", "C2", 100.0);

        assertEquals(Api.topKByAvgScore(5), Arrays.asList(userA, userB, userC, userD));
        assertEquals(Api.kAbove(userC, 2), Arrays.asList(userB, userA));
        assertEquals(Api.kBelow(userC, 2), Arrays.asList(userD));

        Api.addScoreForUser("A@foo.com", "C3", 40.0);
        assertEquals(userA.avgScore(), 80.0, 1E-3);
        assertEquals(Api.topKByAvgScore(4), Arrays.asList(userB, userC, userD, userA));
        assertEquals(Api.kAbove(userA, 2), Arrays.asList(userD, userC));
        assertTrue(Api.kBelow(userA, 2).isEmpty());
        assertEquals(Api.kBelow(userB, 4), Arrays.asList(userC, userD, userA));

        assertEquals(Api.bulkFetchUsers(Arrays.asList("A@foo.com", "B@foo.com")),
                Arrays.asList(userA, userB));
    }
}