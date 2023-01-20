/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package TreeBuilder;

import java.util.ArrayList;
import java.util.Arrays;

public class NaryTreeNode {
    final String LABEL;
    // final int N;
    public ArrayList<NaryTreeNode> children;
    String[] toRemove = {"START", "LBRACKET", "RBRACKET", "END", "LPARA", "RPARA","ENDLINE", "LBRACE", "RBRACE","endline","endExp"};

    public NaryTreeNode(String LABEL) {
        this.LABEL = LABEL;
        // this.N = n;
        children = new ArrayList<>();
    }

    private boolean addChild(NaryTreeNode node) {
        // if (children.size() < N) {
        return children.add(node);
        // }

        //  return false;
    }

    public boolean addChild(String label) {
        return addChild(new NaryTreeNode(label));
    }

    public ArrayList<NaryTreeNode> getChildren() {
        return new ArrayList<>(children);
    }
    public void setChildren(ArrayList<NaryTreeNode> children) {
        this.children = children;
    }

    public void setChild(int index, NaryTreeNode n){
        this.children.set(index, n);
    }

    public NaryTreeNode getChild(int index) {
        if (index < children.size()) {
            return children.get(index);
        }

        return null;
    }



    public static String print(NaryTreeNode root) {
        String AST =  printUtil(root, 0);
        return AST;
    }

    private static String printUtil(NaryTreeNode node, int depth) {
        String AST = "["+ node.LABEL+ " # [";
        for (NaryTreeNode child: node.getChildren()){
            AST = AST + printUtil(child, depth);
        }
        AST = AST + "],],";
        return AST;

    }

    public NaryTreeNode flatten(NaryTreeNode node){
        NaryTreeNode root = node;
        ArrayList<NaryTreeNode> newChildren = new ArrayList<NaryTreeNode>();
        for(NaryTreeNode x: root.children){
            if (Arrays.asList(toRemove).contains(x.LABEL)) {
                //System.out.println("Removing from children:" + x.LABEL);
            }else if(x.children.size() > 0){
                //System.out.println("Children Size:" + x.children.size());
                newChildren.add(flatten(x));
            }else {
                //System.out.println("Adding new child: " + x.LABEL);
                newChildren.add(x);
            }
        }
        root.setChildren(newChildren);
        if(root.children.size() == 1){
            return root.children.get(0);
        }
        return root;
    }



    public NaryTreeNode cutShow(NaryTreeNode n){
        ArrayList<NaryTreeNode> removedStmt = n.children;

        NaryTreeNode x = new NaryTreeNode(n.getChild(0).LABEL);

        x.addChild(n.getChild(1).LABEL);

        return x;


    }

    public String getLABEL() {
        return LABEL;
    }




    public NaryTreeNode removeStmt(NaryTreeNode node){
        //ID stmt

        int z =0;

        for(NaryTreeNode x: node.children){

            // System.out.println(x.LABEL);
            if(x.LABEL.equals("stmt")){
                // System.out.println("Found stmt");
                //System.out.println(x.getChild(0).LABEL);
                if(x.getChild(0).LABEL.equals("SHOW")){

                    node.setChild(z, cutShow(node.getChild(z)));
                }
                if(x.getChild(0).LABEL.equals("BREAK")){
                    // node.setChild(z, new NaryTreeNode("BREAK"));
                }
            }
            removeStmt(x);
            z++;
        }
        return node;

    }

    public NaryTreeNode removeWhenLoop(NaryTreeNode node){
        int z =0;


        for(NaryTreeNode x: node.children){
            if(x.LABEL.equals("whenloop")){
                ArrayList<NaryTreeNode> whenChildren = new ArrayList<>();

                if(x.getChild(0).LABEL.equals("DO")){//DO WHEN
                    NaryTreeNode when = new NaryTreeNode("DO-WHEN");
                    whenChildren.add(x.children.remove(x.children.size()-1));
                    whenChildren.add(x.getChild(1));
                    when.setChildren(whenChildren);
                    swapIfStmt(when);
                    swapBody(when);
                    node.setChild(z,when);

                }


                else if(x.getChild(0).LABEL.equals("WHEN")){

                    NaryTreeNode when = new NaryTreeNode(x.getChild(0).LABEL);
                    whenChildren.add(x.getChild(1));
                    whenChildren.add(x.getChild(3));
                    when.setChildren(whenChildren);

                    swapBody(when);
                    swapIfStmt(when);
                    node.setChild(z, when);
                }


            }
            removeWhenLoop(x);
            z++;
        }
        return node;
    }

    public NaryTreeNode removeForLoop(NaryTreeNode node){
        int z= 0;

        for(NaryTreeNode x: node.children){
            if(x.LABEL.equals("forloop")){
                ArrayList<NaryTreeNode> forChildren = new ArrayList<>();

                NaryTreeNode forloop = new NaryTreeNode(x.getChild(0).LABEL);
                forChildren.add(x.getChild(2));
                forChildren.get(0).addChild(x.getChild(1).LABEL);
                forChildren.get(0).addChild(x.getChild(3).LABEL);
                forChildren.add(x.getChild(4));
                forloop.setChildren(forChildren);

                swapBody(forloop);
                swapIfStmt(forloop);
                node.setChild(z, forloop);
            }

            removeForLoop(x);
            z++;

        }
        return node;
    }
    public NaryTreeNode swapType(NaryTreeNode node){

        int z =0;

        for(NaryTreeNode x: node.children){
            if(x.LABEL.equals("type")){

                //ASSIGN
                ArrayList<NaryTreeNode> assnChildren = new ArrayList<>();
                if(x.children.size()==2){
                    NaryTreeNode assignment = new NaryTreeNode(x.getChild(1).getChild(0).LABEL);

                    assnChildren.add(x.getChild(0));
                    if(x.getChild(1).getChild(1).LABEL.equals("assign'")){
                        NaryTreeNode assign = new NaryTreeNode(x.getChild(1).getChild(1).getChild(0).LABEL);
                        assign.addChild(x.getChild(1).getChild(1).getChild(1).LABEL);
                        assnChildren.add(assign);
                        assignment.setChildren(assnChildren);

                    }
                    else{

                        assnChildren.add(x.getChild(1).getChild(1));
                        assignment.setChildren(assnChildren);
                    }


                    node.setChild(z, assignment);
                }
                else if(x.children.size()==3){
                    NaryTreeNode assignment = new NaryTreeNode(x.getChild(2).getChild(0).LABEL);

                    assnChildren.add(x.getChild(1));
                    if(x.getChild(2).getChild(1).LABEL.equals("assign'")){
                        NaryTreeNode assign = new NaryTreeNode(x.getChild(2).getChild(1).getChild(0).LABEL);
                        assign.addChild(x.getChild(2).getChild(1).getChild(1).LABEL);
                        assnChildren.add(assign);
                        assignment.setChildren(assnChildren);
                    }
                    else{
                        assnChildren.add(x.getChild(2).getChild(1));
                        assignment.setChildren(assnChildren);
                    }

                    node.setChild(z, assignment);
                }
                else{}



            }

            swapType(x);
            z++;
        }
        return node;
    }
    public NaryTreeNode swapBody(NaryTreeNode node){
        NaryTreeNode root = node;
        ArrayList<NaryTreeNode> newChildren = new ArrayList<NaryTreeNode>();
        int nodeIndex = 0;
        for (int i = 0; i < root.children.size(); i++){
            if(root.children.get(i).LABEL.equals("body")){
                for (NaryTreeNode x: root.children.get(i).getChildren()) {
                    newChildren.add(x);
                }
                nodeIndex = i;

            }
        }
        root.children.remove(nodeIndex);
        for (NaryTreeNode x: newChildren) {
            root.children.add(x);
        }

        ArrayList<String> labelList = new ArrayList<String>();
        for (NaryTreeNode x: root.children) {
            labelList.add(x.LABEL);
        }
        if(labelList.contains("body")){
            return swapBody(root);
        }else{
            return root;
        }

    }
    public NaryTreeNode swapIfStmt(NaryTreeNode node){
        NaryTreeNode root = node;
        ArrayList<NaryTreeNode> newIfstmtChildren = new ArrayList<NaryTreeNode>();
        int nodeIndex = 0;
        for (NaryTreeNode x: root.children) {
            if(x.LABEL.equals("ifstmt")){
                for (NaryTreeNode y : x.children) {
                    switch (y.LABEL){
                        case "condB":
                            for (NaryTreeNode j : y.children) {
                                switch (j.LABEL){
                                    case "condC":
                                        ArrayList<NaryTreeNode> newIfChildren = j.children;
                                        NaryTreeNode ifNode = new NaryTreeNode(newIfChildren.get(0).LABEL);
                                        newIfChildren.remove(0);
                                        //newIfChildren.remove(0);
                                        ifNode.setChildren(newIfChildren);
                                        newIfChildren = ifNode.getChildren();

                                        for (NaryTreeNode d:newIfChildren.get(1).getChildren()) {
                                            if(d.LABEL.equals("ifstmt")){
                                                NaryTreeNode body = swapIfStmt(newIfChildren.get(1));
                                                ifNode.getChildren().set(1,body);

                                            }
                                        }
                                        newIfstmtChildren.add(ifNode);

                                        swapBody(ifNode);
                                        break;
                                    case "condB'":
                                        if(j.children.size() != 0){
                                            ArrayList<NaryTreeNode> newElifChildren = j.children;
                                            NaryTreeNode elifNode = new NaryTreeNode(newElifChildren.get(0).LABEL);
                                            newElifChildren.remove(0);
                                            //newElifChildren.remove(0);
                                            elifNode.setChildren(newElifChildren);
                                            newIfChildren = elifNode.getChildren();

                                            for (NaryTreeNode d:newIfChildren.get(1).getChildren()) {
                                                if(d.LABEL.equals("ifstmt")){
                                                    NaryTreeNode body = swapIfStmt(newIfChildren.get(1));
                                                    elifNode.getChildren().set(1,body);

                                                }
                                            }
                                            newIfstmtChildren.add(elifNode);
                                            swapBody(elifNode);
                                            //swapIfStmt(elifNode);
                                            break;
                                        }
                                        break;

                                }
                            }
                            break;

                        case "cond'":
                            if(y.children.size() != 0){
                                ArrayList<NaryTreeNode> newElseChildren = y.children;
                                NaryTreeNode elseNode = new NaryTreeNode(newElseChildren.get(0).LABEL);
                                newElseChildren.remove(0);
                                elseNode.setChildren(newElseChildren);

                                newElseChildren = elseNode.getChildren();

                                for (NaryTreeNode d:newElseChildren.get(0).getChildren()) {
                                    if(d.LABEL.equals("ifstmt")){
                                        NaryTreeNode body = swapIfStmt(newElseChildren.get(0));
                                        elseNode.getChildren().set(0,body);

                                    }
                                }
                                newIfstmtChildren.add(elseNode);
                                swapBody(elseNode);
                                break;
                            }
                            break;

                    }
                }
                NaryTreeNode ifstmt = new NaryTreeNode("if-stmt");
                ifstmt.setChildren(newIfstmtChildren);

                root.children.set(nodeIndex, ifstmt);
                for (NaryTreeNode k: root.children) {
                    if(k.LABEL.equals("ifstmt")){
                        root = swapIfStmt(root);
                    }
                }
                break;
            }else{
                nodeIndex++;
            }
        }

        return root;
    }
    public NaryTreeNode shortenTree(NaryTreeNode node){
        NaryTreeNode newRoot = node.flatten(node);
        newRoot = newRoot.swapBody(newRoot);
        newRoot =newRoot.removeWhenLoop(newRoot);
        newRoot = newRoot.removeForLoop(newRoot);
        newRoot = newRoot.swapIfStmt(newRoot);
        newRoot = newRoot.removeStmt(newRoot);
        newRoot = newRoot.swapType(newRoot);
        return newRoot;
    }
    public static String printRule(NaryTreeNode n){
        String rule ="";

        rule=rule.concat(n.LABEL+",");

        for (NaryTreeNode child: n.getChildren()){
            printRule(child);
        }
        return rule;
    }
}