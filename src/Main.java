import structure.DFA;

import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.*;

public class Main {

    public static void main(String[] args){
        Scanner scan = new Scanner(System.in);
        System.out.print("Enter the lex file:");
        String lexFile = scan.nextLine();
        try{
            lexGenerator.getGenerator().compile(lexFile);
            System.out.print("Completed...");
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
