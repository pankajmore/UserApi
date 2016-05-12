package com.userApi;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class CommandLine {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        PrintWriter out = new PrintWriter(System.out);
        while (in.hasNextLine()) {
            out.flush();
            String s = in.nextLine();
            final String[] words = s.split(" ");
            if (words.length == 0) throw new IllegalArgumentException("Received invalid input: " + s);
            String command = words[0];
            switch (command) {
                case "ADD_USER":
                    if (words.length != 3) throw new IllegalArgumentException("Received invalid input: " + s);
                    Api.addOrGet(words[1], words[2]);
                    break;
                case "ADD_SCORE":
                    if (words.length != 4) throw new IllegalArgumentException("Received invalid input: " + s);
                    Api.addScoreForUser(words[1], words[2], Double.valueOf(words[3]));
                    break;
                case "TOP_K": {
                    if (words.length != 2) throw new IllegalArgumentException("Received invalid input: " + s);
                    int k = Integer.valueOf(words[1]);
                    out.println(String.format("Top %d users: ", k));
                    Api.topKByAvgScore(k).forEach(out::println);
                    out.println();
                    break;
                }
                case "K_ABOVE": {
                    if (words.length != 3) throw new IllegalArgumentException("Received invalid input: " + s);
                    String email = words[1];
                    int k = Integer.valueOf(words[2]);
                    User user = Api.getByEmail(email);
                    out.println(String.format("%d Above for User: %s", k, email));
                    Api.kAbove(user, k).forEach(out::println);
                    out.println();
                    break;
                }
                case "K_BELOW": {
                    if (words.length != 3) throw new IllegalArgumentException("Received invalid input: " + s);
                    String email = words[1];
                    int k = Integer.valueOf(words[2]);
                    User user = Api.getByEmail(email);
                    out.println(String.format("%d Below for User: %s", k, email));
                    Api.kBelow(user, k).forEach(out::println);
                    out.println();
                    break;
                }
                case "BULK_FETCH":
                    if (words.length == 1) throw new IllegalArgumentException("Received invalid input: " + s);
                    List<String> emails = new ArrayList<>(Arrays.asList(words).subList(1, words.length));
                    out.println("Bulk Fetch Query: \n");
                    Api.bulkFetchUsers(emails).forEach(out::println);
                    out.println();
                    break;
                default:
                    throw new IllegalArgumentException("Received invalid input: " + s);
            }
        }
    }
}
