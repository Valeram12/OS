package manager;

import java.io.IOException;
import java.util.Scanner;

public class Main {
    private static String inpNumber(){
        Scanner s = new Scanner(System.in);
        return s.next();
    }

    public static void main(String[] args) throws IOException {

        System.out.println("Enter a number");
        String p = inpNumber();

        Manager.run(p);
    }


}
