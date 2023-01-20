package TreeBuilder;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Node
{
    Node left;
    Node right;
    String value;


    public static boolean hasRoot= false;

    public Node( String value )
    {
        this.value = value;
        this.left=null;
        this.right=null;
    }
    public Node(String value, Node left,Node right){
        this.value=value;
        this.left=left;
        this.right=right;
    }



    public static void getExpression(Node root, ArrayList<ArrayList<String>> rules){

        if (root==null){
            return;
        }

        ArrayList<String> rule = new ArrayList<String>();

        if(!hasRoot){

            rule.add(root.value);
            rules.add(rule);
            hasRoot=true;
            getExpression(root,rules);
        }
        else{
            if(root.left == null &&root.right == null){
                return;
            }
            if(root.left!=null){
                rule.add(root.left.value);

            }
            if(root.right!=null){
                rule.add(root.right.value);
            }

            rules.add(rule);

            if(isOperator(root.left.value)){
                getExpression(root.left,rules);
            }
            if(isOperator(root.right.value)){
                getExpression(root.right,rules);
            }

        }
        // System.out.println("Rules"+ rules);

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