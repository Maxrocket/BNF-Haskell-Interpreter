package alexhappywriter;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

public class AlexHappyWriter {

    public static void main(String[] args) throws FileNotFoundException, IOException {

        ArrayList<Object[]> tokens = new ArrayList();

        FileReader reader = new FileReader("design.txt");
        BufferedReader br = new BufferedReader(reader);
        String name = br.readLine();
        String line = br.readLine();
        
        int maxDef = 0;
        int maxTokenDef = 0;
        int maxLabelDef = 0;
        
        do {
            String[] linePart = line.split(",,");
            maxDef = Math.max(linePart[0].length(), maxDef);
            maxLabelDef = Math.max(linePart[1].length(), maxLabelDef);
            String tokenDef = "Token" + linePart[2] + " AlexPosn";
            if (linePart.length > 3) {
                String[] param = new String[linePart.length - 3];
                for (int i = 0; i < linePart.length - 3; i++) {
                    param[i] = linePart[i + 3];
                }
                tokenDef += " " + param[0];
                tokens.add(new Object[]{linePart[0], linePart[1], linePart[2], param});
            } else {
                tokens.add(new Object[]{linePart[0], linePart[1], linePart[2]});
            }
            maxTokenDef = Math.max(tokenDef.length(), maxTokenDef);
            line = br.readLine();
        } while (line != null);
        
        FileWriter fwx = new FileWriter(name + "Tokens.x");
        fwx.write("{\n");
        fwx.append("module " + name + "Tokens where\n");
        fwx.append("}\n");
        fwx.append("\n");
        fwx.append("%wrapper \"posn\"\n");
        fwx.append("$digit = 0-9\n");
        fwx.append("$alpha = [a-zA-Z]\n");
        fwx.append("\n");
        fwx.append("tokens :-\n");
        fwx.append("$white+       ;\n");
        fwx.append("  \"--\".*        ;\n");
        for (Object[] token : tokens) {
            String label = (String) token[0];
            String tokenName = (String) token[2];
            String extraParam = "";
            if (token.length > 3) {
                String[] paramType = (String[]) token[3];
                switch (paramType[0]) {
                    case "String":
                        extraParam = " s";
                        break;
                    case "Int":
                        extraParam = " (read s)";
                        break;
                }
            }
            
            fwx.append("  " + label + blankSpace(maxDef - label.length()) + "   { \\p s -> Token" + tokenName + " p" + extraParam + " }\n");
        }
        fwx.append("\n");
        fwx.append("{\n");
        fwx.append("data Token = \n");
        for (int i = 0; i < tokens.size(); i++) {
            String tokenDef = "Token" + (String) (tokens.get(i)[2]) + " AlexPosn";
            if (tokens.get(i).length > 3) {
                String[] paramType = (String[]) tokens.get(i)[3];
                tokenDef += " " + paramType[0];
            }
            fwx.append("  " + tokenDef + blankSpace(maxTokenDef - tokenDef.length()));
            if (i == tokens.size() - 1) {
                fwx.append("\n");
            } else {
                fwx.append("   |\n");
            }
        }
        fwx.append("  deriving (Eq,Show)\n");
        fwx.append("\n");
        fwx.append("tokenPosn :: Token -> String\n");
        for (Object[] token : tokens) {
            String tokenName = (String) token[2];
            String extraParam = "";
            if (token.length > 3) {
                String[] paramType = (String[]) token[3];
                switch (paramType[0]) {
                    case "String":
                        extraParam = " x";
                        break;
                    case "Int":
                        extraParam = " n";
                        break;
                }
            }
            fwx.append("tokenPosn (Token" + tokenName + " (AlexPn a l c)" + extraParam + ") = show(l) ++ \":\" ++ show(c)\n");
        }
        fwx.append("\n");
        fwx.append("}\n");
        
        fwx.flush();
        fwx.close();
        
        FileWriter fwy = new FileWriter(name + "GrammerUnfinished.y");
        fwy.write("{\n");
        fwy.append("module " + name + "Grammer where\n");
        fwy.append("import " + name + "Tokens\n");
        fwy.append("}\n");
        fwy.append("\n");
        fwy.append("%name parseCalc\n");
        fwy.append("%tokentype { Token }\n");
        fwy.append("%error { parseError }\n");
        fwy.append("%token\n");
        for (Object[] token : tokens) {
            String label = (String) token[1];
            String tokenName = (String) token[2];
            String extraParam = "";
            if (token.length > 3) {
                String[] paramType = (String[]) token[3];
                switch (paramType[0]) {
                    case "String":
                        extraParam = " $$";
                        break;
                    case "Int":
                        extraParam = " $$";
                        break;
                }
            }
            
            fwy.append("    " + label + blankSpace(maxLabelDef - label.length()) + "   { \\p s -> Token" + tokenName + " _" + extraParam + " }\n");
        }
        fwy.append("\n");
        fwy.append("%%\n");
        fwy.append("\n");
        fwy.append("{\n");
        fwy.append("parseError :: [Token] -> a\n");
        fwy.append("parseError (x:_) = error (\"Parse error\" ++ (tokenPosn x))\n");
        fwy.append("}\n");
        
        fwy.flush();
        fwy.close();
        
        FileWriter fwm = new FileWriter(name + "Main.hs");
        fwm.write("\n");
        fwm.append("import " + name + "Tokens\n");
        fwm.append("import " + name + "Grammer\n");
        fwm.append("import System.Environment\n");
        fwm.append("import Control.Exception\n");
        fwm.append("import System.IO\n");
        fwm.append("\n");
        fwm.append("main :: IO ()\n");
        fwm.append("main = catch main' noParse\n");
        fwm.append("\n");
        fwm.append("main' = do (fileName : _ ) <- getArgs\n");
        fwm.append("           sourceText <- readFile fileName\n");
        fwm.append("           putStrLn (\"Parsing : \" ++ sourceText)\n");
        fwm.append("           let parsedProg = parseCalc (alexScanTokens sourceText)\n");
        fwm.append("           putStrLn (\"Parsed as \" ++ (show parsedProg))\n");
        fwm.append("\n");
        fwm.append("noParse :: ErrorCall -> IO ()\n");
        fwm.append("noParse e = do let err =  show e\n");
        fwm.append("               hPutStr stderr err\n");
        fwm.append("               return ()\n");
        
        fwm.flush();
        fwm.close();

    }
    
    public static String blankSpace(int length) {
        String space = "";
        for (int i = 0; i < length; i++) {
            space += " ";
        }
        return space;
    }

}
