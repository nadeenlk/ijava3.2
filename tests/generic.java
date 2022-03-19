package tests;

import java.util.LinkedList;

public class generic {
    public static void main(String[] args) {
        LinkedList<String> x = new LinkedList<>();
        x.size();
        x = null;
        x = new LinkedList<String>();
    }
}
