/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TreeBuilder;

import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Godwin Sabigan
 */
public class PFtoET {

    public static Node toTree(ArrayList<String> postfix){
//        Stack<NaryTreeNode> s = new Stack<>();
//        
//        for (int i = 0; i < postfix.size(); i++) {
//            if(isOperator(postfix.get(i))){
//                NaryTreeNode rightOperand = s.pop();
//                NaryTreeNode leftOperand = s.pop();
//                NaryTreeNode newNode = new NaryTreeNode(postfix.get(i));
//                newNode.children.add(leftOperand);
//                newNode.children.add(rightOperand);
//                //newNode.addChild(rightOperand.LABEL);
//                s.push(newNode);
//            }
//            else{
//                s.push(new NaryTreeNode(postfix.get(i)));
//            }
//        }
        Stack<Node> s = new Stack<>();

        for (int i = 0; i < postfix.size(); i++) {
            if(isOperator(postfix.get(i))){
                Node rightOperand = s.pop();
                Node leftOperand = s.pop();


                s.push(new Node(postfix.get(i),leftOperand,rightOperand));
            }
            else{
                s.push(new Node(postfix.get(i)));
            }
        }
        return s.pop();

        //System.out.println();
        //- + 5 1 2
        //Node.print(root);


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
//    public static void main(String[] args) {
//        ArrayList<String> test = new ArrayList<>();
//        //1+2-5
//        //12+5-
//        
//        test.add("1");
//        test.add("2");
//        test.add("[ADD]");
//        test.add("5");
//        test.add("[MINUS]");
//        
//        
//        toTree(test);
//        
//        
//    }
}
