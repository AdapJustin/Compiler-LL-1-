package TreeBuilder;
import java.util.ArrayList;
import java.util.Stack;

public class ToPostFix
{
    public void ToPostFix(){

    }

    // A utility function to return
    // precedence of a given operator
    // Higher returned value means
    // higher precedence
    static int Prec(String C)
    {
        if(C.equals("[NOT]")) return 7;
        else if(C.equals("[MULTIPLY]")|| C.equals("[DIVIDE]")|| C.equals("[MOD]")) return 6;
        else if(C.equals("[ADD]") || C.equals("[MINUS]")) return 5;
        else if(C.equals("[GT]")||C.equals("[LT]")||C.equals("[GTE]")||C.equals("[LTE]"))return 4; //add >= <-
        else if(C.equals("[ISEQUAL]")|| C.equals("[NEQUAL]")) return 4;
        else if(C.equals("[AND]")) return 2;
        else if(C.equals("[OR]")) return 1;

        return 0;
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

    // The main method that converts
    // given infix expression
    // to postfix expression.
    public ArrayList<String> infixToPostfix(ArrayList<String> exp)
    {
        // initializing empty String for result
        String result = new String("");
        ArrayList<String> pf = new ArrayList<>();
        // initializing empty stack
        Stack<String> stack = new Stack<>();

        for (int i = 0; i<exp.size(); ++i)
        {
            String c = exp.get(i);
            if(c.equals("[LPARA]"))
                stack.push(c);

            else if(c.equals("[RPARA]"))
            {
                while (!stack.isEmpty() &&
                        (!stack.peek().equals("[LPARA]")))
                    //result += stack.pop();
                    pf.add(stack.pop());
                stack.pop();
            }
            // If the scanned character is an
            // operand, add it to output.
            else if (!isOperator(c))
                pf.add(c);

                // If the scanned character is an '(',
                // push it to the stack.
                // else if (c == '(')


                //  If the scanned character is an ')',
                // pop and output from the stack
                // until an '(' is encountered.
                //else if (c == ')')

            else // an operator is encountered
            {
                while (!stack.isEmpty() && Prec(c)
                        <= Prec(stack.peek())){

                    //result += stack.pop();
                    pf.add(stack.pop());
                }
                stack.push(c);
            }

        }

        // pop all the operators from the stack
        while (!stack.isEmpty()){
            if(stack.peek().equals("[LPARA]"))
                throw new RuntimeException("Invalid Expression");
            //result += stack.pop();
            pf.add(stack.pop());
        }
        return pf;
    }

    // Driver method
//    public static void main(String[] args)
//    {
//       ArrayList<String> test = new ArrayList<>();
//        
//        test.add("1");
//        test.add("[ADD]");
//        test.add("2");
//        test.add("[MINUS]");
//        test.add("5");
//        
//        System.out.println(infixToPostfix(test));;
//    }
}