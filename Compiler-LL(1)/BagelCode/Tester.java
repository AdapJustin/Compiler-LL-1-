package BagelCode;

import TreeBuilder.NaryTreeNode;

import java.io.File;
import java.util.*;

public class Tester {

    static void setVar(String key, Token token, LinkedHashMap<String, Token> table) {
        if(token.name.equals("[ID=]")){
            if(!table.containsKey(key))
                table.put(key, token);
        }
    }

    public static void main(String[] args) {
        LinkedHashMap<String, Token> table = new LinkedHashMap<>();
        File file = new File("src/BagelCode/input.txt");
        List<Token> outputList = new ArrayList<>();
        try {
            //TODO Scanner here
            Scanner scannerClass = new Scanner(file);
            List<Token> tokenList = scannerClass.addTokens();
            for (Token token : tokenList) {
                outputList.add(token);
                setVar(token.lexeme, token, table);


            }
            System.out.println("\n============================================");
            System.out.println("SCANNING");
            System.out.println("============================================\n");
//            System.out.println("TOKENS");
//            System.out.printf("%-30.30s  %-30.30s  %-30.30s%n", "Token Name", "Token Type", "Lexeme");
//            System.out.printf("%-30.30s  %-30.30s  %-30.30s%n", "===================", "===================", "===================");
            Iterator<Map.Entry<String, Token>> it = table.entrySet().iterator();
            String output = "";
//            while (it.hasNext()) {
//                Map.Entry<String, Token> pair = it.next();
//                System.out.printf("%-30.30s  %-30.30s  %s%n", pair.getValue().name, pair.getValue().type, pair.getValue().lexeme);
//            }

            System.out.println("\nSYMBOL TABLE");
            System.out.printf("%-30.30s  %-30.30s%n", "KEY", "DETAIL");
            System.out.printf("%-30.30s  %-30.30s%n", "===================", "===================");
            it = table.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry<String, Token> pair = it.next();
                if (pair.getValue().name.equals("[ID=]")) {
                    output = pair.getValue().type;
                    System.out.printf("%-30.30s  %s%n", pair.getValue().lexeme, output);
                }

            }
            System.out.println("\nOUTPUT");
            System.out.println("========");
            for (Token t : outputList) {
                if (t.type.equals("[LITERAL]") || t.type.equals("[IDENTIFIER]")) {
                    output = t.lexeme;
                } else {
                    output = t.name;
                }
                System.out.println(output);
            }
            System.out.println("\n============================================");
            System.out.println("PARSING");
            System.out.println("============================================\n");

            outputList.add(new Token("$", "$","$", null));
            Parser parser = new Parser(outputList, table);
            NaryTreeNode semanticRoot = parser.startParse();

            System.out.println("\n============================================");
            System.out.println("INTERPRETING");
            System.out.println("============================================\n");
            Interpreter interpreter = new Interpreter();
            interpreter.doPGM(semanticRoot, table);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}


