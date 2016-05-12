package com.userApi;

import com.sun.istack.internal.NotNull;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by pankaj on 5/11/16.
 */
public class User implements Comparable<User> {
    private final String _email; //primary key, must be unique
    private final String _name;
    private final Map<String, Double> _courseToScoreMap;
    private double _avgScore;

    public User(@NotNull String email, @NotNull String name) {
        _email = email;
        _name = name;
        _courseToScoreMap = new HashMap<>();
        _avgScore = 0.0;
    }

    public double avgScore() {
        return _avgScore;
    }

    public boolean addScore(@NotNull String course, double score) {
        if (_courseToScoreMap.containsKey(course)) return false;
        int numCourses = _courseToScoreMap.size();
        _avgScore = (_avgScore * numCourses + score) / (numCourses + 1);
        _courseToScoreMap.put(course, score);
        return true;
    }

    public Map<String, Double> courseToScoreMap() {
        return _courseToScoreMap;
    }

    public String email() {
        return _email;
    }

    public String name() {
        return _name;
    }

    /**
     * @param u user to compare against
     * @return the user with higher score has a higher rank, ties are broken using lexicographic ordering on email address, the user with "smaller" email address gets higher rank
     * There could be other ways of breaking a tie like insertion order
     */
    @Override
    public int compareTo(User u) {
        return u._email.compareTo(this._email);
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("Name: %s\n", _name));
        sb.append(String.format("Email: %s\n", _email));
        sb.append("Courses: \n");
        for (Map.Entry<String, Double> entry : courseToScoreMap().entrySet()) {
            sb.append(String.format("%s: %f\n", entry.getKey(), entry.getValue()));
        }
        sb.append(String.format("Average Score: %f", _avgScore));
        return sb.toString();
    }
}
