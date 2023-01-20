package BagelCode;

import TreeBuilder.NaryTreeNode;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

enum ErrorType {
    //Undeclared variable error
    NO_DECLARATION,

    //Multiple declaration of variable in a scope error
    MULTIPLE_DECLARATION,

    //Type mismatch errors
    INCOMPATIBLE_TYPE,
    INVALID_INT_VALUE,
    INVALID_BOOL_VALUE,
    INVALID_STR_VALUE,
    CONVERSION_ERROR,

    INVALID_CONDITION, //invalid condition error
    OPERATION_ERROR_1,
    OPERATION_ERROR_2,
    OPERATION_ERROR_3,
    OPERATION_ERROR_4,
    OPERATION_ERROR_5,
}
public class Interpreter {

    LinkedHashMap<String, Token> table;
    private int errors;

    public Interpreter() {


    }

    public boolean doPGM(NaryTreeNode root, LinkedHashMap<String,Token> sT){
        this.table=sT;
        printSymbolTable();
        String pgmName= root.children.remove(0).getLABEL();
        if(root.getLABEL().equals("S")){
            System.out.println("===========================Output=====================================");
            interpret(root.children);
            System.out.println("===========================End======================================");
        }
        return true;
    }
    public void printSymbolTable(){
        System.out.println("===========================Symbol Table================================");
        System.out.printf("%-30.30s  %-30.30s  %-30.30s%n", "KEY", "DETAIL", "VALUE");
        System.out.printf("%-30.30s  %-30.30s  %-30.30s%n", "===================", "===================", "===================");
        Iterator<Map.Entry<String, Token>> it = table.entrySet().iterator();
        it = table.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Token> pair = it.next();
            if (pair.getValue().name.equals("[ID=]")) {
                System.out.printf("%-30.30s  %-30.30s   %s%n", pair.getValue().lexeme, pair.getValue().type, pair.getValue().value);
            }

        }
        System.out.println("\n======================================================================");
    }

    public void interpret(ArrayList<NaryTreeNode> children){

        for(int i=0;i<children.size();i++){

            if(children.get(i).getLABEL().equals("SHOW")){
                doSHOW(children.get(i).getChild(0));
            }
            if(children.get(i).getLABEL().equals("ASSIGN")){
                doASSIGN(children.get(i));
            }
            if(children.get(i).getLABEL().equals("FOR")){
                doFOR(children.get(i));
            }
            if(children.get(i).getLABEL().equals("WHEN")){
                doWHEN(children.get(i));
            }
            if(children.get(i).getLABEL().equals("DO-WHEN")){
                doDOWHEN(children.get(i));
            }
            if(children.get(i).getLABEL().equals("if-stmt")){
                doIFSTMT(children.get(i));
            }
        }

    }

    public void doSHOW(NaryTreeNode print){

        if(table.containsKey("["+print.getLABEL()+"]")){
            if(table.containsKey("["+print.getLABEL()+"]")){
                System.out.println(table.get("["+print.getLABEL()+"]").value);
            }

        }
        else if(print.getLABEL().startsWith("STR=")){
            System.out.println(print.getLABEL().substring(5, print.getLABEL().length()-1));
        }
        else if(print.getLABEL().startsWith("INT=")){
            System.out.println(print.getLABEL().substring(4));
        }
        else if(print.getLABEL().startsWith("BOOL=")){
            System.out.println(print.getLABEL().substring(5));
        }

        else return;
    }
    public void doASSIGN(NaryTreeNode assign){
        Token toAssign = table.get("["+assign.getChild(0).getLABEL()+"]");

        if(toAssign.type.equals("[INT_IDENTIFIER]")){
            if(assign.children.get(1).getLABEL().startsWith("INT=")){
                int a = Integer.parseInt(assign.getChild(1).getLABEL().substring(4));
                toAssign.value = a;
            }
            else if(assign.getChild(1).getLABEL().startsWith("TOINT")){
                String toInt = assign.getChild(1).getChild(0).getLABEL();
                try{
                    int a = Integer.parseInt(toInt.substring(5,assign.getChild(1).getChild(0).getLABEL().length()-1));

                    toAssign.value=a;


                }catch(Exception e){
                    System.err.println("Could not convert "+toInt.substring(5,assign.getChild(1).getChild(0).getLABEL().length()-1) );
                }
            }
            else if(assign.getChild(1).getLABEL().startsWith("IN")){
                Scanner s = new Scanner(System.in);

                System.out.println(assign.getChild(1).getChild(0).getLABEL().substring(5, assign.getChild(1).getChild(0).getLABEL().length()-1));
                try{
                    int a = s.nextInt();
                    toAssign.value=a;
                }catch(Exception e){
                    System.err.println("Input is not an Int");
                }


            }
            //AN INT IDENTIFIER
            else if(table.containsKey("["+assign.getChild(1).getLABEL()+"]")){
                if(!table.get("["+assign.getChild(1).getLABEL()+"]").type.equals("[INT_IDENTIFIER]")){
                    System.err.println("CAN'T ASSIGN ID"+assign.getChild(1).getLABEL()+" TO "+ assign.getChild(0).getLABEL());
                    System.exit(0);
                }
                else{
                    toAssign.value = getNum(assign.getChild(1));
                }
            }

            //AN EXPRESSION;
            else if(evaluatesInt(assign.children.get(1).getLABEL())){

                toAssign.value = evalTree(assign.getChild(1));



            }

            else{
                System.err.println("Could not assign value to Int identifier: "+ assign.getChild(0).getLABEL() );
                System.exit(0);

            }

        }

        else if(toAssign.type.equals("[STRING_IDENTIFIER]")){

            if(assign.children.get(1).getLABEL().startsWith("STR=")){
                String a = assign.getChild(1).getLABEL().substring(5,assign.getChild(1).getLABEL().length()-1);
                toAssign.value = a;
            }

            else if(assign.getChild(1).getLABEL().startsWith("TOSTRING")){
                try{
                    if(assign.children.get(1).getChild(0).getLABEL().startsWith("BOOL=")){
                        String b = assign.getChild(1).getChild(0).getLABEL().substring(5);
                        toAssign.value= b;
                    }
                    else if(assign.children.get(1).getChild(0).getLABEL().startsWith("INT=")){

                        String b = assign.getChild(1).getChild(0).getLABEL().substring(4);
                        toAssign.value=b;
                    }

                }catch(Exception e){
                    System.err.println("Could not convert to String");
                    System.exit(0);
                }
            }
            else if(assign.getChild(1).getLABEL().startsWith("IN")){
                Scanner s = new Scanner(System.in);

                System.out.println(assign.getChild(1).getChild(0).getLABEL().substring(5, assign.getChild(1).getChild(0).getLABEL().length()-1));
                try{
                    String a = s.nextLine();
                    toAssign.value=a;
                }catch(Exception e){
                    System.err.println("Invalid input");
                    System.exit(0);
                }


            }
            else if(table.containsKey("["+assign.getChild(1).getLABEL()+"]")){
                if(!table.get("["+assign.getChild(1).getLABEL()+"]").type.equals("[STRING_IDENTIFIER]")){
                    System.err.println("CAN'T ASSIGN ID"+assign.getChild(1).getLABEL()+" TO "+ assign.getChild(0).getLABEL());
                    System.exit(0);
                }
                else{
                    toAssign.value = getStr(assign.getChild(1));
                }
            }

            //ANOTHER STRING IDENTIFIER:


            else{
                System.err.println("Could not assign "+ assign.getChild(1).getLABEL().substring(5,assign.getChild(1).getLABEL().length()-1)+ " to " +assign.getChild(0).getLABEL());
                System.exit(0);
            }
        }



        else if(assign.children.get(1).getLABEL().startsWith("BOOL=")){
            if(toAssign.type.equals("[BOOL_IDENTIFIER]")){
                boolean a = Boolean.valueOf(assign.getChild(1).getLABEL().substring(5));
                toAssign.value = a;

            }
            else{
                System.err.println("Could not assign "+ assign.getChild(1).getLABEL().substring(5,assign.getChild(1).getLABEL().length()-1)+ " to " +assign.getChild(0).getLABEL());
                System.exit(0);
            }
        }



        //AN EXPRESSION
        else{

        }
    }
    public void doFOR(NaryTreeNode forNode){
        int start;
        int end;
        int x=0;
        int y=0;
        //evaluate TO condition
        //forNode.getChild(0).getChild(0)
        try{
            x = getNum(forNode.getChild(0).getChild(0));
            y = getNum(forNode.getChild(0).getChild(1));

        }catch(Exception e){
            error(ErrorType.INVALID_CONDITION, "Invalid values for FOR");

        }

        if(x<y){
            start=x;
            end =y;
        }
        else{
            start=y;
            end = x;
        }
        forNode.getChildren().remove(0);
        for (int i = start; i < end; i++) {
            interpret(forNode.getChildren());
        }
    }
    public void doWHEN(NaryTreeNode whenNode){
        NaryTreeNode condition = whenNode.children.remove(0);

        if(requiresBool(condition.getLABEL())||requiresInt(condition.getLABEL())){

            while(evalExp(condition)){
               interpret(whenNode.children);
            }
        }
        else{
            error(ErrorType.INVALID_CONDITION, "FOR WHEN CONDITION");
        }
    }
    public void doDOWHEN(NaryTreeNode whenNode){
        NaryTreeNode condition = whenNode.children.remove(0);

        if(requiresBool(condition.getLABEL())||requiresInt(condition.getLABEL())){

            do{
                interpret(whenNode.children);
            }while(evalExp(condition));
        }
        else{
            error(ErrorType.INVALID_CONDITION, "DO WHEN CONDITION");
            System.exit(0);
        }
    }

    public void doIFSTMT(NaryTreeNode ifstmtNode){
        for(NaryTreeNode if_stmt: ifstmtNode.children){
            if(if_stmt.getLABEL().equals("IF")){
                if(evalExp(if_stmt.getChild(0))){
                    ArrayList<NaryTreeNode> if_stmtChildren = new ArrayList<>();
                    for (int i = 1; i < if_stmt.children.size(); i++) {
                        if_stmtChildren.add(if_stmt.getChild(i));
                    }
                    interpret(if_stmtChildren);
                    break;
                }else{
                    continue;
                }
            }else if(if_stmt.getLABEL().equals("ELIF")){
                if(evalExp(if_stmt.getChild(0))){
                    ArrayList<NaryTreeNode> if_stmtChildren = new ArrayList<>();
                    for (int i = 1; i < if_stmt.children.size(); i++) {
                        if_stmtChildren.add(if_stmt.getChild(i));
                    }
                    interpret(if_stmtChildren);
                    break;
                }else{
                    continue;
                }

            }else if(if_stmt.getLABEL().equals("ELSE")){
                ArrayList<NaryTreeNode> if_stmtChildren = new ArrayList<>();
                for (int i = 0; i < if_stmt.children.size(); i++) {
                    if_stmtChildren.add(if_stmt.getChild(i));
                }
                interpret(if_stmtChildren);
                break;
            }else{
                break;
            }
        }
    }

    // check program conditions
//    private boolean checkConditions(NaryTreeNode node) {
//        try {
//            if (node.children.size() == 0) {
//                if(table.containsKey("[" + node.getLABEL() + "]")){
//                    return (Boolean) table.get("[" + node.getLABEL() + "]").value;
//                }else{
//                    return Boolean.valueOf(node.getLABEL().substring(5));
//                }
//            } else {
//                NaryTreeNode op1 = node.getChild(0);
//                NaryTreeNode op2 = node.getChild(1);
//                switch (node.getLABEL()) {
//                    case "ADD":
//                    case "MINUS":
//                    case "DIVIDE":
//                    case "MOD":
//                    case "MULTIPLY":
//                    case "AND":
//                        boolean one = false;
//                        boolean two = false;
//                        if (op1.children.size() != 0) {
//                            one = checkConditions(op1);
//                        }else{
//                            if(table.containsKey("[" + op1.getLABEL() + "]")){
//                                one = (Boolean) table.get("[" + op1.getLABEL() + "]").value;
//                            }else{
//                                one = Boolean.valueOf(op1.getLABEL().substring(5));
//                            }
//
//                        }
//                        if (op2.children.size() != 0) {
//                            two = checkConditions(op2);
//                        }else{
//                            if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                two = (Boolean) table.get("[" + op2.getLABEL() + "]").value;
//                            }else{
//                                two = Boolean.valueOf(op2.getLABEL().substring(5));
//                            }
//
//                        }
//                        return (one && two);
//                    case "OR":
//                        boolean oneOR = false;
//                        boolean twoOR = false;
//                        if (op1.children.size() != 0) {
//                            oneOR = checkConditions(op1);
//                        }else{
//                            if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                oneOR = (Boolean) table.get("[" + op1.getLABEL() + "]").value;
//                            }else{
//                                oneOR = Boolean.valueOf(op1.getLABEL().substring(5));
//                            }
//                        }
//                        if (op2.children.size() != 0) {
//                            twoOR = checkConditions(op2);
//                        }else {
//                            if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                twoOR = (Boolean) table.get("[" + op2.getLABEL() + "]").value;
//                            }else{
//                                twoOR = Boolean.valueOf(op2.getLABEL().substring(5));
//                            }
//                        }
//                        return (oneOR || twoOR);
//
//                    case "LT":
//                        int oneLT = 0;
//                        int twoLT = 1;
//                        if(table.containsKey("[" + op1.getLABEL() + "]")){
//                            oneLT = (Integer) table.get("[" + op1.getLABEL() + "]").value;
//                        }else{
//                            oneLT = Integer.valueOf(op1.getLABEL().substring(4));
//                        }
//                        if(table.containsKey("[" + op2.getLABEL() + "]")){
//                            twoLT = (Integer) table.get("[" + op2.getLABEL() + "]").value;
//                        }else{
//                            twoLT = Integer.valueOf(op2.getLABEL().substring(4));
//                        }
//                        return (oneLT < twoLT);
//
//                    case "GT":
//                        int oneGT = 0;
//                        int twoGT = 0;
//                        if(table.containsKey("[" + op1.getLABEL() + "]")){
//                            oneGT = (Integer) table.get("[" + op1.getLABEL() + "]").value;
//                        }else{
//                            oneGT = Integer.valueOf(op1.getLABEL().substring(4));
//                        }
//                        if(table.containsKey("[" + op2.getLABEL() + "]")){
//                            twoGT = (Integer) table.get("[" + op2.getLABEL() + "]").value;
//                        }else{
//                            twoGT = Integer.valueOf(op2.getLABEL().substring(4));
//                        }
//                        return (oneGT > twoGT);
//                    case "LTE":
//                        int oneLTE = 0;
//                        int twoLTE = 0;
//                        if(table.containsKey("[" + op1.getLABEL() + "]")){
//                            oneLTE = (Integer) table.get("[" + op1.getLABEL() + "]").value;
//                        }else{
//                            oneLTE = Integer.valueOf(op1.getLABEL().substring(4));
//                        }
//                        if(table.containsKey("[" + op2.getLABEL() + "]")){
//                            twoLTE = (Integer) table.get("[" + op2.getLABEL() + "]").value;
//                        }else{
//                            twoLTE = Integer.valueOf(op2.getLABEL().substring(4));
//                        }
//                        return (oneLTE <= twoLTE);
//                    case "GTE":
//                        int oneGTE = 0;
//                        int twoGTE = 0;
//                        if(table.containsKey("[" + op1.getLABEL() + "]")){
//                            oneGTE = (Integer) table.get("[" + op1.getLABEL() + "]").value;
//                        }else{
//                            oneGTE = Integer.valueOf(op1.getLABEL().substring(4));
//                        }
//                        if(table.containsKey("[" + op2.getLABEL() + "]")){
//                            twoGTE = (Integer) table.get("[" + op2.getLABEL() + "]").value;
//                        }else{
//                            twoGTE = Integer.valueOf(op2.getLABEL().substring(4));
//                        }
//                        return (oneGTE >= twoGTE);
//
//                    case "ISEQUAL":
//                        if(op1.getLABEL().startsWith("STR=") && op2.getLABEL().startsWith("STR=")){
//                            String oneISEQUAL = op1.getLABEL().substring(5, op1.getLABEL().length()-1);
//                            String twoISEQUAL = op2.getLABEL().substring(5, op2.getLABEL().length()-1);
//                            return oneISEQUAL.equals(twoISEQUAL);
//                        }else if(op1.getLABEL().startsWith("INT=") && op2.getLABEL().startsWith("INT=")){
//                            int oneISEQUAL = Integer.valueOf(op1.getLABEL().substring(4));
//                            int twoISEQUAL = Integer.valueOf(op2.getLABEL().substring(4));
//                            return (oneISEQUAL == twoISEQUAL);
//                        }else{
//                            if(table.containsKey("[" + op1.getLABEL() + "]")){
//                                String type = table.get("[" + op1.getLABEL() + "]").type;
//                                switch (type){
//                                    case "[INT_IDENTIFIER]":
//                                        int oneISEQUAL =(Integer) table.get("[" + op1.getLABEL() + "]").value;
//                                        int twoISEQUAL = 0;
//                                        if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                            twoISEQUAL = (Integer) table.get("[" + op2.getLABEL() + "]").value;
//                                        }else{
//                                            twoISEQUAL = Integer.valueOf(op2.getLABEL().substring(4));
//                                        }
//                                        return (oneISEQUAL == twoISEQUAL);
//                                    case "[STR_IDENTIFIER]":
//                                        String oneSTRISEQUAL = (String) table.get("[" + op1.getLABEL() + "]").value;
//                                        String twoSTRISEQUAL = "";
//                                        if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                            twoSTRISEQUAL = (String) table.get("[" + op2.getLABEL() + "]").value;
//                                        }else{
//                                            twoSTRISEQUAL = op2.getLABEL().substring(5, op2.getLABEL().length()-1);
//                                        }
//                                        return oneSTRISEQUAL.equals(twoSTRISEQUAL);
//                                }
//                            }else if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                String type = table.get("[" + op1.getLABEL() + "]").type;
//                                switch (type){
//                                    case "[INT_IDENTIFIER]":
//                                        int oneISEQUAL = 0;
//                                        int twoISEQUAL = (Integer) table.get("[" + op2.getLABEL() + "]").value;
//                                        if(table.containsKey("[" + op1.getLABEL() + "]")){
//                                            oneISEQUAL = (Integer) table.get("[" + op1.getLABEL() + "]").value;
//                                        }else{
//                                            oneISEQUAL = Integer.valueOf(op1.getLABEL().substring(4));
//                                        }
//                                        return (oneISEQUAL == twoISEQUAL);
//                                    case "[STR_IDENTIFIER]":
//                                        String oneSTRISEQUAL = "";
//                                        String twoSTRISEQUAL = (String) table.get("[" + op2.getLABEL() + "]").value;
//                                        if(table.containsKey("[" + op1.getLABEL() + "]")){
//                                            oneSTRISEQUAL = (String) table.get("[" + op1.getLABEL() + "]").value;
//                                        }else{
//                                            oneSTRISEQUAL = op1.getLABEL().substring(5, op1.getLABEL().length()-1);
//                                        }
//                                        return oneSTRISEQUAL.equals(twoSTRISEQUAL);
//                                }
//                            }else{
//                                error(ErrorType.INVALID_CONDITION, op1.getLABEL() + node.getLABEL() + op2.getLABEL());
//                            }
//                        }
//
//                    case "NEQUAL":
//                        System.out.println();
//                        if(op1.getLABEL().startsWith("STR=") && op2.getLABEL().startsWith("STR=")){
//                            String oneNEQUAL = "";
//                            String twoNEQUAL = "";
//                            if(table.containsKey("[" + op1.getLABEL() + "]")){
//                                oneNEQUAL = (String) table.get("[" + op1.getLABEL() + "]").value;
//                            }else{
//                                oneNEQUAL = op1.getLABEL().substring(5, op1.getLABEL().length()-1);
//                            }
//                            if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                twoNEQUAL = (String) table.get("[" + op2.getLABEL() + "]").value;
//                            }else{
//                                twoNEQUAL = op2.getLABEL().substring(5, op2.getLABEL().length()-1);
//                            }
//                            return !oneNEQUAL.equals(twoNEQUAL);
//                        }else if(op1.getLABEL().startsWith("INT=") && op2.getLABEL().startsWith("INT=")){
//                            int oneNEQUAL = 0;
//                            int twoNEQUAL = 0;
//                            if(table.containsKey("[" + op1.getLABEL() + "]")){
//                                oneNEQUAL = (Integer) table.get("[" + op1.getLABEL() + "]").value;
//                            }else{
//                                oneNEQUAL = Integer.valueOf(op1.getLABEL().substring(4));
//                            }
//                            if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                twoNEQUAL = (Integer) table.get("[" + op2.getLABEL() + "]").value;
//                            }else{
//                                twoNEQUAL = Integer.valueOf(op2.getLABEL().substring(4));
//                            }
//                            return (oneNEQUAL != twoNEQUAL);
//                        }else{
//                            if(table.containsKey("[" + op1.getLABEL() + "]")){
//                                String type = table.get("[" + op1.getLABEL() + "]").type;
//                                switch (type){
//                                    case "[INT_IDENTIFIER]":
//                                        int oneNEQUAL =(Integer) table.get("[" + op1.getLABEL() + "]").value;
//                                        int twoNEQUAL = 0;
//                                        if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                            twoNEQUAL = (Integer) table.get("[" + op2.getLABEL() + "]").value;
//                                        }else{
//                                            twoNEQUAL = Integer.valueOf(op2.getLABEL().substring(4));
//                                        }
//                                        return (oneNEQUAL != twoNEQUAL);
//                                    case "[STR_IDENTIFIER]":
//                                        String oneSTRNEQUAL = (String) table.get("[" + op1.getLABEL() + "]").value;
//                                        String twoSTRNEQUAL = "";
//                                        if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                            twoSTRNEQUAL = (String) table.get("[" + op2.getLABEL() + "]").value;
//                                        }else{
//                                            twoSTRNEQUAL = op2.getLABEL().substring(5, op2.getLABEL().length()-1);
//                                        }
//                                        return !oneSTRNEQUAL.equals(twoSTRNEQUAL);
//                                }
//                            }else if(table.containsKey("[" + op2.getLABEL() + "]")){
//                                String type = table.get("[" + op1.getLABEL() + "]").type;
//                                switch (type){
//                                    case "[INT_IDENTIFIER]":
//                                        int oneNEQUAL = 0;
//                                        int twoNEQUAL = (Integer) table.get("[" + op2.getLABEL() + "]").value;
//                                        if(table.containsKey("[" + op1.getLABEL() + "]")){
//                                            oneNEQUAL = (Integer) table.get("[" + op1.getLABEL() + "]").value;
//                                        }else{
//                                            oneNEQUAL = Integer.valueOf(op1.getLABEL().substring(4));
//                                        }
//                                        return (oneNEQUAL != twoNEQUAL);
//                                    case "[STR_IDENTIFIER]":
//                                        String oneSTRNEQUAL = "";
//                                        String twoSTRNEQUAL = (String) table.get("[" + op2.getLABEL() + "]").value;
//                                        if(table.containsKey("[" + op1.getLABEL() + "]")){
//                                            oneSTRNEQUAL = (String) table.get("[" + op1.getLABEL() + "]").value;
//                                        }else{
//                                            oneSTRNEQUAL = op1.getLABEL().substring(5, op1.getLABEL().length()-1);
//                                        }
//                                        return !oneSTRNEQUAL.equals(twoSTRNEQUAL);
//                                }
//                            }else{
//                                error(ErrorType.INVALID_CONDITION);
//                            }
//                        }
//                }
//            }
//        } catch (Exception e) {
//            error(ErrorType.INVALID_CONDITION);
//        }
//        return false;
//    }


    private int evalTree(NaryTreeNode n){
        if (n.getLABEL()==null)
            return 0;

        if(requiresInt(n.getLABEL())||requiresBool(n.getLABEL())){
            error(ErrorType.OPERATION_ERROR_5, n.getLABEL());
        }
        if(evaluatesInt(n.getLABEL())){
            switch(n.getLABEL()){
                case "ADD": return evalTree(n.getChild(0))+evalTree(n.getChild(1));
                case "MINUS": return evalTree(n.getChild(0))-evalTree(n.getChild(1));
                case "MULTIPLY": return evalTree(n.getChild(0))*evalTree(n.getChild(1));
                case "DIVIDE": return evalTree(n.getChild(0))/evalTree(n.getChild(1));
                case "MOD": return evalTree(n.getChild(0))%evalTree(n.getChild(1));
                default:
                    System.err.println("Invalid operation");
                    System.exit(0);

            }
        }
        else if(n.getLABEL().startsWith("INT=")||table.get("["+n.getLABEL()+"]").type.equals("[INT_IDENTIFIER]")){
            try{
                return getNum(n);
            }catch(Exception e){
                error(ErrorType.INCOMPATIBLE_TYPE, n.getLABEL());
            }
        }

        return 0;
    }

    private boolean evalExp(NaryTreeNode n){
        if(n.getLABEL()==null){
            error(ErrorType.OPERATION_ERROR_1, n.getLABEL().toString());
        }

        if(requiresInt(n.getLABEL())){
            switch(n.getLABEL()){
                case "LT": return evalTree(n.getChild(0))<evalTree(n.getChild(1));
                case "GT": return evalTree(n.getChild(0))>evalTree(n.getChild(1));
                case "LTE": return evalTree(n.getChild(0))<=evalTree(n.getChild(1));
                case "GTE": return evalTree(n.getChild(0))>=evalTree(n.getChild(1));
                case "ISEQUAL": return evalTree(n.getChild(0))==evalTree(n.getChild(1));
                case "NEQUAL": return evalTree(n.getChild(0))!=evalTree(n.getChild(1));
            }
        }else if(requiresBool(n.getLABEL())){
            switch(n.getLABEL()){
                case "AND": return evalExp(n.getChild(0))&&evalExp(n.getChild(1));
                case "OR": return evalExp(n.getChild(0))||evalExp(n.getChild(1));
            }
        }
        else if(n.getLABEL().startsWith("BOOL=")||table.get("["+n.getLABEL()+"]").type.equals("[BOOL_IDENTIFIER]")){
            try{
                return getBool(n);
            }catch(Exception e){
                error(ErrorType.INCOMPATIBLE_TYPE, n.getLABEL());
            }
        }
        return false;
    }
    private int getNum(NaryTreeNode n){
        int x;

        if(n.getLABEL().startsWith("INT=")){
            x = Integer.parseInt(n.getLABEL().substring(4));
        }

        else if(table.containsKey("["+n.getLABEL()+"]")&&table.get("["+n.getLABEL()+"]").type.equals("[INT_IDENTIFIER]")){
            x= Integer.parseInt(table.get("["+n.getLABEL()+"]").value.toString());
        }
        else{
            x=0;
            error(ErrorType.INVALID_INT_VALUE, n.getLABEL());
        }
        return x;
    }
    private boolean getBool(NaryTreeNode n){
        boolean x;

        if(n.getLABEL().startsWith("BOOL=")){
            x = Boolean.valueOf(n.getLABEL().substring(5));
        }

        else if(table.containsKey("["+n.getLABEL()+"]")&&table.get("["+n.getLABEL()+"]").type.equals("[BOOL_IDENTIFIER]")){
            x= Boolean.valueOf(table.get("["+n.getLABEL()+"]").value.toString());
        }
        else{
            x=false;
            error(ErrorType.INVALID_BOOL_VALUE, n.getLABEL());
        }
        return x;
    }
    private String getStr(NaryTreeNode n){
        String x="";

        if(n.getLABEL().startsWith("STR=")){
            x = (n.getLABEL().substring(5,n.getLABEL().length()-1));
        }

        else if(table.containsKey("["+n.getLABEL()+"]")&&table.get("["+n.getLABEL()+"]").type.equals("[INT_IDENTIFIER]")){
            x= (table.get("["+n.getLABEL()+"]").value.toString());
        }
        else{
            error(ErrorType.INVALID_STR_VALUE, n.getLABEL());
        }
        return x;
    }



    private boolean requiresInt(String x){
        return x.equals("GT")||x.equals("LT")||x.equals("GTE")||x.equals("LTE")||x.equals("ISEQUAL")
                ||x.equals("NEQUAL");
    }
    private boolean requiresBool(String x){
        return x.equals("AND")||x.equals("OR");
    }
    private boolean evaluatesInt(String x){
        return x.equals("ADD")||x.equals("MINUS")||x.equals("MULTIPLY")||x.equals("DIVIDE")||x.equals("MOD");
    }


    // print errors report
    private void error(ErrorType errorType, String parm){
        errors++;
        switch (errorType) {
            case MULTIPLE_DECLARATION:
                System.err.println("Declaration Error: MULTIPLE DECLARATION -" + parm);
                break;
            case NO_DECLARATION:
                System.err.println("Declaration Error: NO DECLARATION -" + parm);
                break;
            case INVALID_CONDITION:
                System.err.println("Invalid Condition: INVALID CONDITION -" + parm);
                break;
            case INCOMPATIBLE_TYPE:
                System.err.println("Data Type Error: INCOMPATIBLE TYPE -" + parm);
                break;
            case CONVERSION_ERROR:
                System.err.println("Data Type Error: CONVERSION ERROR -" + parm);
                break;
            case INVALID_INT_VALUE:
                System.err.println("Value error: INVALID INT VALUE -" + parm);
                break;
            case INVALID_BOOL_VALUE:
                System.err.println("Value error: INVALID BOOL VALUE -" + parm);
                break;
            case INVALID_STR_VALUE:
                System.err.println("Value error: INVALID STR VALUE -" + parm);
                break;
            case OPERATION_ERROR_1:
                System.err.println("Expression Invalid: EXPRESSION ERROR -" + parm);
                break;
            case OPERATION_ERROR_2:
                System.err.println("Expression Invalid: OPERATION ERROR -" + parm);
                break;
            case OPERATION_ERROR_3:
                System.err.println("Expression Invalid: ARITHMETIC ERROR -" + parm);
                break;
            case OPERATION_ERROR_4:
                System.err.println("Expression Invalid: BOOLEAN ERROR -" + parm);
                break;
            case OPERATION_ERROR_5:
                System.err.println("Expression Invalid: BAD OPERAND -" + parm);
                break;

            default:
                break;
        }
        System.exit(0);
    }
}