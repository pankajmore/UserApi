package com.userApi;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Created by pankaj on 5/11/16.
 */
public class Api {
    private static final Map<String, User> _emailToUserMap = new ConcurrentHashMap<>();
    private static final TreeMap<Double, TreeSet<User>> _avgScoreToUsersMap = new TreeMap<>();
    private static final Map<List<String>, List<User>> _bulkFetchCache = new HashMap<>();

    /**
     * Time : O(1)
     * Space: O(1)
     *
     * @param name  the name of the user
     * @param email the email address of the user
     * @return user object, creates a new one if not already exists, else return the old user
     */
    public static User addOrGet(@NotNull String name, @NotNull String email) {
        if (!_emailToUserMap.containsKey(email))
            _emailToUserMap.put(email, new User(email, name));
        return _emailToUserMap.get(email);
    }

    /**
     * Time: O(logN)
     *
     * @param email  email of the user
     * @param course course completed by the user
     * @param score  score achieved
     * @return true if it succeeded in adding this score else false(due to collisions)
     */
    public static boolean addScoreForUser(@NotNull String email, @NotNull String course, double score) {
        User u = _emailToUserMap.get(email);
        if (u == null) return false;
        double oldScore = u.avgScore();
        if (!u.addScore(course, score)) return false;
        double newScore = u.avgScore();

        if (_avgScoreToUsersMap.containsKey(oldScore))
            _avgScoreToUsersMap.get(oldScore).remove(u);
        if (_avgScoreToUsersMap.get(oldScore) == null || _avgScoreToUsersMap.get(oldScore).isEmpty())
            _avgScoreToUsersMap.remove(oldScore);

        if (!_avgScoreToUsersMap.containsKey(newScore))
            _avgScoreToUsersMap.put(newScore, new TreeSet<>());
        _avgScoreToUsersMap.get(newScore).add(u);

        return true;
    }

    /**
     * Time: O(k + logN)
     * N is the total number of users
     *
     * @param k number of top K users
     * @return list of K users ordered by score, email is used to break ties
     */
    public static List<User> topKByAvgScore(int k) {
        List<User> users = new ArrayList<>();
        final Iterator<TreeSet<User>> it = _avgScoreToUsersMap.descendingMap().values().iterator();
        while (it.hasNext()) {
            for (User u : it.next().descendingSet()) {
                users.add(u);
                k--;
                if (k == 0) return users;
            }
        }
        return users;
    }

    @Nullable
    public static User getByEmail(@NotNull String email) {
        return _emailToUserMap.get(email);
    }

    /**
     * Time: O(K + logN)
     * N is the total number of users
     *
     * @param user the given user
     * @param k    the number of users below user
     * @return the list of "up to" k users above user ordered by rank
     */
    public static List<User> kAbove(@NotNull User user, int k) {
        List<User> users = new ArrayList<>();
        final Iterator<TreeSet<User>> it = _avgScoreToUsersMap.tailMap(user.avgScore()).values().iterator();
        SortedSet<User> remainingInUserSet = it.next().tailSet(user, false);
        for (User u : remainingInUserSet) {
            users.add(u);
            k--;
            if (k == 0) return users;
        }
        while (it.hasNext()) {
            for (User u : it.next()) {
                users.add(u);
                k--;
                if (k == 0) return users;
            }
        }
        return users;
    }

    /**
     * Time: O(K + logN)
     * N is the total number of users
     *
     * @param user the given user
     * @param k    the number of users below user
     * @return the list of "up to" k users below user ordered by rank
     */
    public static List<User> kBelow(@NotNull User user, int k) {
        List<User> users = new ArrayList<>();
        final Iterator<TreeSet<User>> it = _avgScoreToUsersMap.descendingMap().tailMap(user.avgScore()).values().iterator();
        SortedSet<User> remainingInUserSet = it.next().descendingSet().tailSet(user, false);
        for (User u : remainingInUserSet) {
            users.add(u);
            k--;
            if (k == 0) return users;
        }
        while (it.hasNext()) {
            for (User u : it.next().descendingSet()) {
                users.add(u);
                k--;
                if (k == 0) return users;
            }
        }
        return users;
    }

    /**
     * Time : O(K) if cache miss, O(1) otherwise
     * Can also add a cache eviction policy if needed
     *
     * @param emails list of K emails
     * @return list of K users
     */
    public static List<User> bulkFetchUsers(List<String> emails) {
        if (!_bulkFetchCache.containsKey(emails))
            _bulkFetchCache.put(emails, emails.stream().map(_emailToUserMap::get).collect(Collectors.toList()));
        return _bulkFetchCache.get(emails);
    }
}
