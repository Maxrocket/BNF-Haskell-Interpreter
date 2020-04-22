package alexhappywriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

public class BNF {

    private HashMap<String, String[][]> bnf;
    
    public BNF(String filepath) {
        bnf = new HashMap<>();
        try {
            //reads in lines
            BufferedReader br = new BufferedReader(new FileReader(filepath));
            String line;
            while ((line = br.readLine()) != null) {
                //split into sections between |
                String[] options = line.split("::=")[1].split("\\|");
                String[][] rules = new String[options.length][];
                //split on space
                for (int j = 0; j < options.length; j++) {
                    options[j] = options[j].trim();
                    rules[j] = options[j].split(" ");
                }
                //put in hashmap
                bnf.put(line.split("::=")[0].trim(), rules);
            }
        } catch (FileNotFoundException f) {
            System.out.println(f.getMessage() + " - file not found");
        } catch (IOException i) {
            System.out.println(i.getMessage() + " - io");
        }
    }

    public void print() {
        for (String key : bnf.keySet()) {
            System.out.print(key + " ::= ");
            String[][] cur = bnf.get(key);
            for (int j = 0; j < cur[0].length; j++) {
                System.out.print(cur[0][j] + " ");
            }
            for (int i = 1; i < cur.length; i++) {
                System.out.print("| ");
                for (int j = 0; j < cur[i].length; j++) {
                    System.out.print(cur[i][j] + " ");
                }
            }
            System.out.println("");
        }
    }

}