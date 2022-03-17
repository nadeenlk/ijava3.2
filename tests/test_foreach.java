package tests;

import java.util.LinkedList;

public class test_foreach {
    public static void main(String[] args) {
        LinkedList i = new LinkedList<>();
        i.add("lol");
        i.add(1);
        i.add(true);
        for (Object s : i) {
            System.out.println(s);
        }
        char[] i2 = "nadeenudantha".toCharArray();
        for (char c : i2) {
            System.out.println(c);
        }
    }
}
