package TreeBuilder;

import BagelCode.Token;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tree {
    Queue<String> rules = new LinkedList<String>();

    List<Token> inputTree = new ArrayList<>();
    String[] terminals = {"[START]", "[LBRACKET]", "[RBRACKET]", "[END]", "[ID=]", "[STR=]", "[BOOL=]", "[INT=]", "[INT]", "[STR]",
            "[BOOL]", "[CONST]", "[SHOW]", "[LPARA]", "[RPARA]", "[ASSIGN]", "[IN]", "[TOSTRING]", "[TOINT]", "[ENDLINE]",
            "[ELSE]", "[LBRACE]",
            "[RBRACE]", "[ELIF]", "[IF]", "[WHEN]", "[DO]", "[FOR]", "[TO]","[BREAK]","[CONTINUE]", "$"
    };
    // };
    String[]  nonTers = {"S", "body", "id", "constant", "data-type", "stmt", "type", "assign", "assign'", "endline", "expr", "A'", "B", "B'", "C", "C'", "D", "D'", "E", "F", "ifstmt", "cond'", "condB", "condB'", "condC", "whenloop", "forloop","endExp"
            ,  "[MINUS]", "[ADD]", "[MULTIPLY]", "[DIVIDE]", "[MOD]","[ISEQUAL]", "[NEQUAL]", "[GTE]", "[LTE]", "[GT]", "[LT]", "[AND]", "[OR]", "[NOT]"
            ,  "MINUS", "ADD", "MULTIPLY", "DIVIDE", "MOD","ISEQUAL", "NEQUAL", "GTE", "LTE", "GT", "LT", "AND", "OR", "NOT"};

    String node="";
    ArrayList<String> v = new ArrayList<>();
    String [] included = {"[SHOW]","[ASSIGN]","[IN]","[TOSTRING]","[TOINT]","[AND]","[OR]","[ISEQUAL]","[NEQUAL]","[GTE]", "[LTE]",
            "[GT]", "[LT]", "[ADD]", "[MINUS]", "[MULTIPLY]", "[DIVIDE]", "[MOD]", "[ELSE]",
            "[ELIF]", "[IF]", "[WHEN]", "[DO]", "[FOR]", "[TO]", "[NOT]","[BREAK]","[CONTINUE]",};
    // String [] excluded = {"B","A'","C","B'","D","C'","E","D'","F","constant","data-type","id",};
    public Tree(){

    }

    public Tree(Queue<String> rules, List<Token> inputTree) {
        this.rules = rules;
        this.inputTree = inputTree;
    }

    public String buildTree(Queue<ArrayList<String>> rules, Stack<String> inputTree,ArrayList<String> values,NaryTreeNode currentNode){
        List<String> tokens = new ArrayList<>();
//        String[] ruleArray;
//        String rule = rules.remove();
//         Pattern pat = Pattern.compile("\\\\([.*?]\\\\)");
//
        //  here get a matcher object

        //System.out.println("Current Rule>> "+rule);
//        System.out.println(rule +" is a value:" + isValue(rule,values));
//        if(isValue(rule,values)&&!isNonTerminal(rule)){
//               System.out.println(rule+" is a Value");
//             Matcher length = pat.matcher(rule);
//             Matcher mat = pat.matcher(rule);
//             int i =0;
//             while(length.find()){
//                 i++;
//             }
//             System.out.println("rule length: "+i);
//             ruleArray = new String[i];
//             while(mat.find()){
//                 ruleArray[i]=rule.substring(mat.start(),mat.end());
//             }
//             for(String s: ruleArray){
//                 System.out.print(s+" ");
//             }
//             rule = rule.substring(1,rule.length()-2);
//             System.out.println("Substring of value rule: "+rule);
//             ruleArray = rule.split("] [");
//
//
//        }
//         if(isValue(rule,values)){
//            ruleArray=new String[1];
//            ruleArray[0]=rule;
//        }
//        else{
//            ruleArray = rule.split(" ");
//        }
//
        ArrayList<String> ruleArray= rules.remove();
        for (int i = 0; i < ruleArray.size(); i++) {


            //System.out.println("Current>> "+ ruleArray.get(i));
            if(ruleArray.get(i).isEmpty()||ruleArray.get(i)==null){
                //System.out.println("Is empty");
                tokens.add("[]");
            }
            else if(!isNonTerminal (ruleArray.get(i))){

                if(!ruleArray.get(i).equals("$")){

                    //if(isIncluded(ruleArray[i])||isValue(ruleArray[i],values)){
                    String removedBracket = removeBrace(ruleArray.get(i));
                    //System.out.println("Removed Bracket>>" +removedBracket);
                    currentNode.addChild(removedBracket);
                    tokens.add("["+removedBracket+ " # []],");


                    //}

                }
                else{

                }


            }
            else if(isNonTerminal(ruleArray.get(i))){
//                if(isExcluded(ruleArray[i])){
//
//                tokens.add("["+ruleArray[i]+ " # " + "["+buildTree(rules,inputTree,values,currentNode)+"]"+"],");
//                }else{
                if(isOperator(ruleArray.get(i))){
                    // ruleArray[i]=ruleArray[i].substring(1, ruleArray[i].length()-1);
                    ruleArray.set(i, removeBrace(ruleArray.get(i)));
                }
                currentNode.addChild(ruleArray.get(i));
                tokens.add("["+ruleArray.get(i)+ " # " + "["+buildTree(rules,inputTree,values,currentNode.getChild(i))+"]"+"],");
                //}


            }


        }

        String tree = new String();
        for(String x : tokens){
            tree = tree.concat(x);
        }
        return tree;
    }
    String removeBrace(String x){
        if(x.endsWith("]")){
            return x.substring(1,x.length()-1);
        }
        else{
            return x.substring(1,x.length()-2);
        }

    }


    boolean isIncluded (String x){
        boolean isInc = Arrays.asList(included).contains(x);
        return isInc;
    }
    //    boolean isExcluded (String x){
//        boolean isInc = Arrays.asList(excluded).contains(x);
//        return isInc;
//    }
    boolean isNonTerminal(String x){

        boolean isTer = Arrays.asList(nonTers).contains(x);
        //System.out.println(x+" is Terminal: " +isTer);
        return isTer;
    }
    public boolean isValue(String t,ArrayList<String> values){
        //System.out.println("ISvalue " + t +":" +values.contains(t.toString()));

        return values.contains(t.toString());
    }
    static boolean isOperator(String C)
    {
        // System.out.print(C+" is : ");
        return((C.equals("[NOT]")) ||
                (C.equals("[MULTIPLY]")|| C.equals("[DIVIDE]")|| C.equals("[MOD]")) ||
                (C.equals("[GT]")||C.equals("[LT]")||C.equals("[GTE]")||C.equals("[LTE]"))||
                (C.equals("[ISEQUAL]")|| C.equals("[NEQUAL]"))||
                (C.equals("[ADD]") || C.equals("[MINUS]"))||
                C.equals("[AND]")||C.equals("[OR]"));


    }
}
//            rule = ll_output.pop(0)
//            tokens = []
//            for item in RULES[rule]:
//            if len(item) > 1: tokens.append(tree(input, ll_output))
//            else: tokens.append(input.pop(0))