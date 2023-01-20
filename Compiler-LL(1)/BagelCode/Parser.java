package BagelCode;

import TreeBuilder.NaryTreeNode;
import TreeBuilder.Node;
import TreeBuilder.PFtoET;
import TreeBuilder.ToPostFix;
import TreeBuilder.Tree;
import grtree.NPLviewer;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;

public class Parser {
    //input
    public List<Token> input = new ArrayList<>();
    private int indexOfInput = -1;
    ArrayList<String> values = new ArrayList<>();
    //Stack
    Stack<String> stack = new Stack<String>();
    LinkedHashMap<String, Token> symbolTable = new LinkedHashMap<>();

    //Table of rules
    String[][] table = {
            {"[START] id [LBRACKET] body [RBRACKET] [END] $", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, "", null, "stmt body", null, null, null, "stmt body", "stmt body", "stmt body", "stmt body", "stmt body", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "", null, "ifstmt body", "whenloop body", "whenloop body", "forloop body", null, null, "stmt body", "stmt body"},
            {null, null, null, null, "[ID=]", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, "id", "[STR=]", "[BOOL=]", "[INT=]", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,},
            {null, null, null, null, null, null, null, null, "[INT]", "[STR]", "[BOOL]", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},

            {null, null, null, null, "type", null, null, null, "type", "type", "type", "[CONST] type", "[SHOW] [LPARA] constant [RPARA] endline", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "[BREAK] [ENDLINE]", "[CONTINUE] [ENDLINE]"},
            {null, null, null, null, "id assign", null, null, null, "data-type id assign", "data-type id assign", "data-type id assign", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "[ASSIGN] assign'", null, null, null, "endline", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, "expr endExp endline", "expr endExp endline", "expr endExp endline", "expr endExp endline", null, null, null, null, null, "expr endExp endline", null, null, "[IN] [LPARA] constant [RPARA] endline", "[TOSTRING] [LPARA] constant [RPARA] endline", "[TOINT] [LPARA] constant [RPARA] endline", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "expr endExp endline"},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "[ENDLINE]", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},

            {null, null, null, null, "B A'", "B A'", "B A'", "B A'", null, null, null, null, null, "B A'", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "B A'", "B A'", null, null, null, null, null, null, null, null, null, null, null, null, "B A'"},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "", null, null, null, null, "", "[AND] B A'", "[OR] B A'", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, "C B'", "C B'", "C B'", "C B'", null, null, null, null, null, "C B'", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "C B'", "C B'", null, null, null, null, null, null, null, null, null, null, null, null, "C B'"},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "", null, null, null, null,  "", "", "", "[ISEQUAL] C B'", "[NEQUAL] C B'", "[GTE] C B'", "[LTE] C B'", "[GT] C B'", "[LT] C B'", null, null, null, null, null, null, null, null, null, null, null, null, null, null},
            {null, null, null, null, "D C'", "D C'", "D C'", "D C'", null, null, null, null, null, "D C'", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "D C'", "D C'", null, null, null, null, null, null, null, null, null, null, null, null, "D C'"},

            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "", null, null, null, null, "", "", "", "", "", "", "", "", "", "[ADD] D C'", "[MINUS] D C'", null, null, null, null, null, null, null, null, null, null, null, null, "[NOT] D C'"},
            {null, null, null, null, "E D'", "E D'", "E D'", "E D'", null, null, null, null, null, "E D'", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "E D'", "E D'", null, null, null, null, null, null, null, null, null, null, null, null, "E D'"},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, "", null, null, null, null, "", "", "", "", "", "", "", "", "", "", "", "[MULTIPLY] E D'", "[DIVIDE] E D'", "[MOD] E D'", null, null, null, null, null, null, null, null, null, ""},
            {null, null, null, null, "F", "F", "F", "F", null, null, null, null, null, "F", null, null, null, null, null, null, null, null, null, null, null, null, null, null, "[NOT] E", "[MINUS] E", null, null, null, null, null, null, null, null, null, null, null, null, "[NOT] E"},
            {null, null, null, null, "constant", "constant", "constant", "constant", null, null, null, null, null, "[LPARA] expr [RPARA]", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null},

            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "condB cond'", null, null, null, null, null},
            {null, null, "", null, "", null, null, null, "", "", "", "", "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "[ELSE] [LBRACE] body [RBRACE]", null, "", null, "", "", "", "", null, null, "", ""},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "condC condB'", null, null, null, null, null},
            {null, null, "", null, "", null, null, null, "", "", "", "", "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "", null, "", "[ELIF] [LPARA] expr endExp [RPARA] [LBRACE] body [RBRACE] condB'", "", "", "", "", null, null, "", ""},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "[IF] [LPARA] expr endExp [RPARA] [LBRACE] body [RBRACE]", null, null, null, null, null},

            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null,  null, "[WHEN] [LPARA] expr endExp [RPARA] [DO] [LBRACE] body [RBRACE]", "[DO] [LBRACE] body [RBRACE] [WHEN] [LPARA] expr endExp [RPARA]", null, null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, "[FOR] constant [TO] constant [LBRACE] body [RBRACE]", null, null},
            {null, null, null, null, null, null, null, null, null, null, null, null, null, null,"", null, null, null, null, "", null, null, null, null, null, null, null, null, null, null, null, null, null, "", null, null, null, null, null, null, null, null, null},

    };
    //4 , 15, 35
    String[] terminals = {"[START]", "[LBRACKET]", "[RBRACKET]", "[END]", "[ID=]", "[STR=]", "[BOOL=]", "[INT=]", "[INT]", "[STR]", "[BOOL]", "[CONST]", "[SHOW]", "[LPARA]", "[RPARA]", "[ASSIGN]", "[IN]", "[TOSTRING]", "[TOINT]", "[ENDLINE]", "[AND]", "[OR]", "[ISEQUAL]", "[NEQUAL]", "[GTE]", "[LTE]", "[GT]", "[LT]", "[ADD]", "[MINUS]", "[MULTIPLY]", "[DIVIDE]", "[MOD]", "[ELSE]", "[LBRACE]", "[RBRACE]", "[ELIF]", "[IF]", "[WHEN]", "[DO]", "[FOR]", "[TO]", "[NOT]", "[BREAK]", "[CONTINUE]", "$"};
    String[]  nonTers = {"S", "body", "id", "constant", "data-type", "stmt", "type", "assign", "assign'", "endline", "expr", "A'", "B", "B'", "C", "C'", "D", "D'", "E", "F", "ifstmt", "cond'", "condB", "condB'", "condC", "whenloop", "forloop","endExp"};
    Queue<ArrayList<String>> rules = new LinkedList<>();

    List<Token> inputTree = new ArrayList<>();

    public Parser() {

    }
    public Parser(List<Token> in, LinkedHashMap<String, Token> sb) {
        this.input = in;
        this.inputTree = in;
        this.symbolTable=sb;
    }

    private void pushRule(String rule) {
//        int getIndex = indexOfInput;
//        System.out.println("INDEXOFINPUT "+ getIndex+": "+input.get(getIndex).lexeme);
//         if(rule.equals("[ID=]")|| rule.equals("[BOOL=]")||rule.equals("[INT=]")||rule.equals("[STR=]")){
//
//            Token inputToken = this.input.get(getIndex);
//            String str = inputToken.lexeme;
//
//            push(str);
//        }else{
        String[] split = rule.split(" ");
        rules.add(toAList(split));

        System.out.println("");
        if (!rule.equals("")) {
            for (int i = split.length; i > 0; i--) {
                push(split[i - 1]);
            }
        }
        //        }


    }
    private void pushRuleX(String rule) {
//        int getIndex = indexOfInput;
//        System.out.println("INDEXOFINPUT "+ getIndex+": "+input.get(getIndex).lexeme);
//         if(rule.equals("[ID=]")|| rule.equals("[BOOL=]")||rule.equals("[INT=]")||rule.equals("[STR=]")){
//
//            Token inputToken = this.input.get(getIndex);
//            String str = inputToken.lexeme;
//
//            push(str);
//        }else{
        String[] split = rule.split(" ");
        //rules.add(toAList(split));
        if (!rule.equals("")) {
            for (int i = split.length; i > 0; i--) {
                push(split[i - 1]);
            }
        }
        //        }


    }
    private ArrayList<String> toAList(String r){
        ArrayList<String> rule = new ArrayList<>();

        rule.add(r);

        return rule;
    }

    private ArrayList<String> toAList (String[] r){
        ArrayList<String> rule = new ArrayList<>();

        for(String x : r){
            rule.add(x);
        }
        return rule;
    }

    //algorithm
    public NaryTreeNode startParse() {
        // push(this.input.charAt(0)+"");//
        System.out.println("===========================START PARSE===========================");
        push("S");

        rules.add(toAList("S"));
        //Read one token from input
        String token = read();
        String top = null;
        boolean parseExp = false;
        NaryTreeNode semanticRoot = new NaryTreeNode(" ");
        do {


            if(!parseExp){
                if (!stack.isEmpty()) top = this.pop();
                if (isTerminal(top)||isValue(top)) {

                    if (!top.equals(token)) {
                        System.out.println(top + " does not equal "+token);
                        error("Invalid token: " + top+ " , By Grammer rule . Token : (" + token + ") expected");
                    } else {
                        if (!top.equals("$")) {


                            System.out.println("Matching: Terminal :( " + token + " )");
                            token = read();
                        }
                    }
                }

                //if top is non-terminal
                else if (isNonTerminal(top)) {
                    String rule ="";

                    if(top.equals("id")){

                        //If it is not declared but there is a declared identifier
                        //if the type of id is equal to the type of key in table

                        if(input.get(indexOfInput).type.equals("[LITERAL]")){
                            error("CONSTANT CANT BE ID");
                        }
                        if((input.get(indexOfInput).type.equals("[UNDECLARED_IDENTIFIER]")
                                && !symbolTable.get(input.get(indexOfInput).lexeme).type.equals("[UNDECLARED_IDENTIFIER]")) ||
                                (input.get(indexOfInput).type.equals(symbolTable.get(input.get(indexOfInput).lexeme).type) ||
                                        symbolTable.get(input.get(indexOfInput).lexeme).type.equals("[IDENTIFIER]"))){

                            rule=input.get(indexOfInput).lexeme;
                            push(rule);
                            rules.add(toAList(rule));
                        }

                        else{
                            error("Invalid Declaration of ID "+ input.get(indexOfInput).lexeme);
                        }


                        //}
                    }
                    else if(top.equals("constant")){//||top.equals("[BOOL=]")||top.equals("[INT=]")){
                        if(symbolTable.containsKey(input.get(indexOfInput).lexeme)){
                            if(symbolTable.get(input.get(indexOfInput).lexeme).type.equals("[UNDECLARED_IDENTIFIER]")){
                                error(input.get(indexOfInput).lexeme +" not declared");
                            }
                        }

                        rule=input.get(indexOfInput).lexeme;
                        push(rule);
                        rules.add(toAList(rule));
                    }
                    else if(top.equals("expr")){
                        rule = this.getRule(top, token);

                        this.pushRuleX(rule);
                        parseExp=true;
                        continue;

                    }
                    else{
                        rule = this.getRule(top, token);
                        this.pushRule(rule);
                    }

                    //this.pushRule(rule);

                    System.out.println(rule);
                    System.out.println("Stack: " + stack);
                    //System.out.println(nonTers[0]);

                } else {
                    error("Never Happens , Because top : ( " + top + " )");
                }
                if (token.equals("$") && top.equals("$")) {
                    break;
                }

            }

            //%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%% Parsing Expressions
            else{
                //System.out.println("\n\n----------------Parsing an Expression-----------");
                //Parsing Expressions
                ArrayList<String> expression = new ArrayList<>();
                while(!top.equals("endExp")){

                    if (!stack.isEmpty()) top = this.pop();
                    if (isTerminal(top)||isValue(top)) {

                        if (!top.equals(token)) {
                            System.out.println(top + " does not equal "+token);
                            error("Invalid token: " + top+ " , By Grammer rule . Token : (" + token + ") expected");
                        } else {
                            if (!top.equals("$")) {


                                System.out.println("Matching: Terminal :( " + token + " )");
                                expression.add(top);
                                token = read();
                            }
                        }
                    }

                    //if top is non-terminal
                    else if (isNonTerminal(top)||isValue(top)) {

                        String rule = "";
                        if(top.equals("id")){

                            if((input.get(indexOfInput).type.equals("[UNDECLARED_IDENTIFIER]")
                                    && !symbolTable.get(input.get(indexOfInput).lexeme).type.equals("[UNDECLARED_IDENTIFIER]")) ||
                                    (input.get(indexOfInput).type.equals(symbolTable.get(input.get(indexOfInput).lexeme).type) ||
                                            symbolTable.get(input.get(indexOfInput).lexeme).type.equals("[IDENTIFIER]"))){

                                rule=input.get(indexOfInput).lexeme;
                                push(rule);
                                rules.add(toAList(rule));
                            }

                            else{
                                error("Invalid Declaration of ID "+ input.get(indexOfInput).lexeme);
                            }

                        }
                        else if(top.equals("constant")){//||top.equals("[BOOL=]")||top.equals("[INT=]")){

                            if(symbolTable.containsKey(input.get(indexOfInput).lexeme)){
                                if(symbolTable.get(input.get(indexOfInput).lexeme).type.equals("[UNDECLARED_IDENTIFIER]")){
                                    error(input.get(indexOfInput).lexeme +" not declared");
                                }
                            }
                            rule=input.get(indexOfInput).lexeme;
                            push(rule);
                        }
                        else if(top.equals("expr")){
                            rule = this.getRule(top, token);

                            this.pushRuleX(rule);
                            parseExp = true;
                            continue;


                        }
                        else{
                            rule = this.getRule(top, token);
                            this.pushRuleX(rule);
                        }

                        //this.pushRule(rule);
                        //rules.add(rule);
                        System.out.println(rule);
                        System.out.println("Stack: " + stack);
                        //System.out.println(nonTers[0]);

                    } else {
                        error("Never Happens , Because top : ( " + top + " )");
                    }
                    if (token.equals("$") || top.equals("$")) {
                        error("Reached End of File while Parsing");
                    }

                }


                parseExp=false;
                ToPostFix pf = new ToPostFix();
                // System.out.println("\n\nINFIX: "+expression);
                //System.out.println("PostFIX:"+pf.infixToPostfix(expression));


                ArrayList<ArrayList<String>> expRules =new ArrayList<>();
                //Node.getExpressionRule(PFtoET.toTree(pf.infixToPostfix(expression)),expRules);
                //rules.add(expRules);
                Node.getExpression(PFtoET.toTree(pf.infixToPostfix(expression)), expRules);
                for(ArrayList<String> x: expRules){
                    rules.add(x);
                }
                Node.hasRoot=false;
                String rule = this.getRule(top, token);
                rules.add(toAList(rule));

                values.add(rule);
                // System.out.println("\n\n----------------End Expression-----------");

            }

            //if top is terminal
        } while (true);//out of the loop when $

        //accept
        if (token.equals("$")) {
            System.out.println("Input is Accepted by LL1");
            System.out.println("-----------------------END PARSE RESULTS---------------------------");
            Stack<String> inputTreeStack = new Stack<String>();
            for (Token x: inputTree) {
                inputTreeStack.push(x.name);
            }

            System.out.println("===========================Concrete Tree===============================");
            //System.out.println("Input tree stack" + inputTreeStack);
            //System.out.println("\nRules: " +rules);
            Tree parseTree = new Tree();
            NaryTreeNode root = new NaryTreeNode("PROGRAM");
            String tree= parseTree.buildTree(rules, inputTreeStack,values,root);
            tree = tree.replace("$", "");
            System.out.println(tree);
            System.out.println("\n===========================End Concrete Tree===========================");
            System.out.println("\n===========================Abstract Tree===============================");
            root=root.shortenTree(root);
            String AST = NaryTreeNode.print(root);
            semanticRoot = root;
            System.out.println("\n===========================End abstract Tree===========================");

            try {
                File treeFile = new File("src\\BagelCode\\ParseTree.txt");
                FileWriter treeWriter = new FileWriter(treeFile);
                treeWriter.write(AST);//replace AST with tree for concrete tree
                treeWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //NPLviewer grtree = new NPLviewer("src\\BagelCode\\ParseTree.txt");

        } else {
            System.out.println("Input is not Accepted by LL1");
        }
        return semanticRoot;
    }

    private boolean isTerminal(String s) {
        boolean isNonTers = Arrays.asList(terminals).contains(s);
        return isNonTers;
    }

    private boolean isNonTerminal(String s) {
        boolean isNonTers = Arrays.asList(nonTers).contains(s);
        return isNonTers;
    }

    private String read() {
        indexOfInput++;
        if(indexOfInput <= input.size()){
            Token inputToken = this.input.get(indexOfInput);
            String str = inputToken.name;


            if(str.equals("[ID=]")||str.equals("[BOOL=]")||str.equals("[INT=]")
                    ||str.equals("[STR=]")){

                String value = inputToken.lexeme;
                values.add(value);
                return value;
            }

            //System.out.println(this.input[indexOfInput]);
            return str;
        }else{
            return "";
        }
    }
    public boolean isValue(String t){
        //System.out.println("ISvalue " + t +":" +values.contains(t.toString()));
        return values.contains(t.toString());
    }

    private void push(String s) {
        this.stack.push(s);
    }

    private String pop() {
        return this.stack.pop();
    }

    private void error(String message) {
        System.err.println("ERROR: " + message);
        //throw new RuntimeException(message);
    }

    public String getRule(String non, String term) {
        int row = getnonTermIndex(non);
        int column = getTermIndex(term);
        String rule = this.table[row][column];
        System.out.print("Rule: " + this.nonTers[row] + " -> ");
        if (rule == null) {
            error("There is no Rule by this , Non-Terminal: (" + non + ") ,Terminal: (" + term + ") ");
        }
        return rule;
    }

    private int getnonTermIndex(String non) {

        for (int i = 0; i < this.nonTers.length; i++) {
            if (non.equals(this.nonTers[i])) {
                return i;
            }
        }
        error(non + " is not NonTerminal");
        return -1;
    }

    private int getTermIndex(String term) {
        Token inputToken = this.input.get(indexOfInput);
        term= inputToken.name;

        for (int i = 0; i < this.terminals.length; i++) {
            if (term.equals(this.terminals[i])) {
                return i;
            }
        }
        error(term + " is not Terminal");
        return -1;
    }



}