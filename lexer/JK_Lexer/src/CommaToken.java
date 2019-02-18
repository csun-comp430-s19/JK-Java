package JK_Lexer;

public class CommaToken implements Token {
    public int hashCode() { return 26; }
    public boolean equals(final Object other) {
        return other instanceof CommaToken;
    }
    public String toString() {
        return ",";
    }
}