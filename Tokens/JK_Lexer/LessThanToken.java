package JK_Lexer;

public class LessThanToken implements Token {
    public int hashCode() { return 28; }
    public boolean equals(final Object other) {
        return other instanceof LessThanToken;
    }
    public String toString() {
        return "<";
    }
}