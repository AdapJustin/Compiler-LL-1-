package BagelCode;

public class Token {

    String name;
    String lexeme;
    String type;
    Object value;

    public Token(String name, String lexeme, String type, Object value) {
        this.name = name;
        this.lexeme = lexeme;
        this.type = type;
        this.value = value;
    }

    public String toString() {
        return formatOutPut(lexeme, name);
    }

    String formatOutPut(String l, String t) {
        StringBuilder outPut = new StringBuilder(l);
        for (int i = l.length(); i < 16; i++) {
            outPut.append(' ');
        }
        return outPut + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }
}

