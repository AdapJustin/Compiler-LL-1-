package BagelCode;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Scanner {

    BufferedReader reader;
    int lineCount = 1;
    char current;
    List<Token> tokenList = new ArrayList<>();

    public Scanner(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        current = readNextChar();
    }

    Token scanner() {

        int state = 1;
        final String[] KEY_WORDS = new String[]{
                "Start", "if", "then", "else", "elif", "Str", "Bool", "Int", "Const", "for", "to", "do", "when",
                "break", "continue", "in", "show", "toString", "toInt", "End", "out"};

        final String[] BOOL_CONST = new String[]{
                "true", "false"
        };
        List<String> key_words = Arrays.asList(KEY_WORDS);
        List<String> bool_const = Arrays.asList(BOOL_CONST);
        while (true) {
            if (current == (char) (-1)) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }
            switch (state) {
                //CHECKS IF THE CURRENT CHAR IS A SPACE OR OPERATOR
                case 1: {
                    if (current == ' ' || current == '\t'
                            || current == '\f' || current == '\b' || current == '\r') {
                        current = readNextChar();
                        continue;
                    } else if (current == '\n') {
                        lineCount++;
                        current = readNextChar();
                        continue;
                    } else if (current == ';') {
                        current = readNextChar();
                        return new Token("[ENDLINE]", ";", "[SC]", null);
                    } else if (current == '+') {
                        current = readNextChar();
                        return new Token("[ADD]", "+", "[OPERATOR]", null);
                    } else if (current == '-') {
                        current = readNextChar();
                        return new Token("[MINUS]", "-", "[OPERATOR]", null);
                    } else if (current == '*') {
                        current = readNextChar();
                        return new Token("[MULTIPLY]", "*", "[OPERATOR]", null);
                    } else if (current == '/') {
                        current = readNextChar();
                        return new Token("[DIVIDE]", "/", "[OPERATOR]", null);
                    } else if (current == '%') {
                        current = readNextChar();
                        return new Token("[MOD]", "%", "[OPERATOR]", null);
                    } else if (current == '{') {
                        current = readNextChar();
                        return new Token("[LBRACE]", "{", "[SC]", null);
                    } else if (current == '}') {
                        current = readNextChar();
                        return new Token("[RBRACE]", "}", "[SC]", null);
                    } else if (current == '(') {
                        current = readNextChar();
                        return new Token("[LPARA]", "(", "[SC]", null);
                    } else if (current == ')') {
                        current = readNextChar();
                        return new Token("[RPARA]", ")", "[SC]", null);
                    } else if (current == '[') {
                        current = readNextChar();
                        return new Token("[LBRACKET]", "[", "[SC]", null);
                    } else if (current == ']') {
                        current = readNextChar();
                        return new Token("[RBRACKET]", "]", "[SC]", null);
                    } else if (current == '=') {
                        current = readNextChar();
                        if (current == '=') {
                            current = readNextChar();
                            return new Token("[ISEQUAL]", "==", "[OPERATOR]", null);
                        } else {
                            return new Token("[ASSIGN]", "=", "[OPERATOR]", null);
                        }
                    } else if (current == '!') {
                        current = readNextChar();
                        if (current == '=') {
                            current = readNextChar();
                            return new Token("[NEQUAL]", "!=", "[OPERATOR]", null);
                        } else {
                            return new Token("[NOT]", "!", "[OPERATOR]", null);
                        }
                    } else if (current == '&') {
                        current = readNextChar();
                        return new Token("[AND]", "&", "[OPERATOR]", null);
                    } else if (current == '>') {
                        current = readNextChar();
                        if (current == '=') {
                            current = readNextChar();
                            return new Token("[GTE]", ">=", "[OPERATOR]", null);
                        } else {
                            return new Token("[GT]", ">", "[OPERATOR]", null);
                        }
                    } else if (current == '<') {
                        current = readNextChar();
                        if (current == '=') {
                            current = readNextChar();
                            return new Token("[LTE]", "<=", "[OPERATOR]", null);
                        } else {
                            return new Token("[LT]", "<", "[OPERATOR]", null);
                        }
                    } else if (current == '|') {
                        current = readNextChar();
                        return new Token("[OR]", "|", "[OPERATOR]", null);
                    } else if (current == '"') {
                        state = 4;
                        continue;
                    } else {
                        state = 2;
                        continue;
                    }

                }
                //CHECKS IF THE CURRENT IS A DIGIT
                case 2: {
                    boolean hasDecimal = false;
                    boolean intReturned = false;
                    if (isNumber(current)) {
                        String num = String.valueOf(current);
                        String decimals = "";
                        while (true) {
                            current = readNextChar();
                            if (isNumber(current)) {

                                if (hasDecimal) {
                                    decimals += String.valueOf(current);
                                } else {
                                    num += String.valueOf(current);
                                }
                            } else if (current == '.') {
                                hasDecimal = true;
                                decimals += String.valueOf(current);

                                continue;
                            } else {
                                if (hasDecimal) {
                                    System.out.println("IGNORED DECIMAL VALUES " + decimals + " on line " + lineCount);
                                    return new Token("[INT=]", "[INT="+num+"]", "[LITERAL]", null);

                                } else {
                                    intReturned = true;
                                    return new Token("[INT=]", "[INT="+num+"]", "[LITERAL]", null);
                                }
                            }
                        }
                    } else {
                        state = 3;
                    }
                }
                //CHECKS IF THE CURRENT IS A KEYWORD, BOOL CONSTANT, IDENTIFIER
                case 3: {
                    if (isLetter(current) || current == '_') {
                        String word = String.valueOf(current);
                        while (true) {
                            current = readNextChar();
                            if (isLetter(current) || current == '_' || isNumber(current)) {
                                word += String.valueOf(current);
                            } else {
                                if (key_words.contains(word)) {
                                    return new Token("[" + word.toUpperCase() + "]", word, "[KEYWORD]", null);
                                } else if (bool_const.contains(word)) {
                                    return new Token("[BOOL=]", "[BOOL="+word+"]", "[LITERAL]", null);
                                } else {
                                    if(tokenList.get(tokenList.size()-1).lexeme.equals("Str")){
                                        return new Token("[ID=]", "["+word+"]", "[STRING_IDENTIFIER]", null);
                                    }
                                    else if(tokenList.get(tokenList.size()-1).lexeme.equals("Int")){
                                        return new Token("[ID=]", "["+word+"]", "[INT_IDENTIFIER]", null);
                                    }
                                    else if(tokenList.get(tokenList.size()-1).lexeme.equals("Bool")){
                                        return new Token("[ID=]", "["+word+"]", "[BOOL_IDENTIFIER]", null);
                                    }
                                    else if(tokenList.get(tokenList.size()-1).lexeme.equals("Start")){
                                        return new Token("[ID=]", "["+word+"]", "[IDENTIFIER]", null);
                                    }
                                    return new Token("[ID=]", "["+word+"]", "[UNDECLARED_IDENTIFIER]", null);
                                }
                            }
                        }
                    } else {
                        state = 4;

                    }
                }

                //BUILDS A STRING CONSTANT
                case 4: {
                    if (current == '"') {

                        String word = String.valueOf(current);
                        while (true) {

                            current = readNextChar();
                            if (isLetter(current) || isNumber(current) || isSpace(current) || isSymbol(current)) {
                                word += String.valueOf(current);

                            } else if (current == '"') {
                                word += String.valueOf(current);
                                current = readNextChar();
                                return new Token("[STR=]",  "[STR="+word+"]" , "[LITERAL]", null);

                            } else {

                                if (current == (char) (-1)) {
                                    System.out.println("Unclosed String Literal on line " + lineCount);
                                    break;
                                }
                                System.out.println("Invalid String token " + String.valueOf(current) + " on line " + lineCount);

                                continue;

                            }
                        }
                    } else {
                        state = 5;
                    }

                }

                // IGNORES COMMENTS
                case 5: {
                    if (current == '#') {
                        current = readNextChar();
                        if (current != '*') {
                            while (current != '\n') {

                                if (current == (char) (-1)) {
                                    break;
                                }
                                current = readNextChar();
                            }
                            lineCount++;
                            current = readNextChar();
                            state = 1;
                            break;
                        } else {
                            current = readNextChar();

                            while (true) {
                                if (current != '*') {
                                    if (current == '\n') {
                                        lineCount++;
                                    }
                                    current = readNextChar();
                                    if (current == (char) (-1)) {
                                        break;
                                    }
                                } else {
                                    current = readNextChar();
                                    if (current == '#') {
                                        state = 1;
                                        break;
                                    } else if (current == (char) (-1)) {
                                        return null;
                                    } else {
                                        current = readNextChar();
                                    }
                                }
                            }
                        }
                        //Reads invalid tokens or end of files
                    } else {
                        String errorToken = String.valueOf(current);

                        if (current == (char) (-1)) {

                        } else {
                            System.err.println("INVALID TOKEN \"" + errorToken + "\"" + " on line " + lineCount);
                        }
                        state = 1;
                        current = readNextChar();
                        break;
                    }
                }

            }
        }
    }

    List<Token> addTokens() {
        Token token = scanner();
        while (token != null) {
            tokenList.add(token);
            token = scanner();
        }
        return tokenList;
    }

    char readNextChar() {
        try {
            return (char) reader.read();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return (char) (-1);
    }


    boolean isNumber(char c) {
        return Character.isDigit(c);
    }

    boolean isLetter(char c) {
        return Character.isLetter(c);
    }

    boolean isSpace(char c) {
        if (c == ' ' || c == '\n' || c == '\t'
                || c == '\f' || c == '\b' || c == '\r') {
            return true;
        }
        return false;
    }

    boolean isSymbol(char c) {
        if (c == '{' || c == '}' || c == '['
                || c == ']' || c == '(' || c == ')'
                || c == '!' || c == '&' || c == '|'
                || c == '^' || c == '=' || c == '@'
                || c == '#' || c == '&' || c == '$'
                || c == '_' || c == '+' || c == '-'
                || c == '*' || c == '/' || c == '%'
                || c == '<' || c == '>' || c == '?'
                || c == ';' || c == ':' || c == ','
                || c == '.' || c == '\\' || c == '\'') {
            return true;
        }
        return false;
    }

}