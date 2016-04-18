package pl.test.face.facetest;

import java.util.Random;

/**
 * Created by lukasz on 13.04.2016.
 */
public class RandomPostGenerator {

    public static String[] randomPosts= new String[]{
      "Sprzedam OPLA",
        "Kupię rolki",
        "Lubię to!",
            "Zapraszam do kontaktu",
            "Rozdaję wejściówki na koncert Popka",
            "Dostałem darmowe doładowania sms!",
            "Pomoże ktoś?",
            "Jadę za granicę",
            "Ktoś na rynek?",
            ""
    }; public static String[] randomPostsDescriptions= new String[]{
      "rocznik 90",
        "naprawdę",
        "#takbylo",
            "świętuję",
            "dzis wieczorem",
            "TAK","#potwierdzoneinfo",
            "..."
    };

    private RandomPostGenerator(){}
    public static String createTitle(){
        int index = new Random().nextInt(randomPosts.length);
        return randomPosts[index];
    }
    public static String createDescription(){
        int index = new Random().nextInt(randomPostsDescriptions.length);
        return randomPostsDescriptions[index];
    }
}
