package org.example;

import com.google.common.base.Joiner;

public class App {
    public static void main(String... args) {
        String[] words = {"Hello", "Otus", "Java"};
        String result = Joiner.on(" ").join(words);
        System.out.println(result);
    }
}
