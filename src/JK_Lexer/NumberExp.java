package JK_Lexer;

public class NumberExp implements Exp {
    public final int number;
    
    public NumberExp(final int number) {
        this.number = number;
    }

    public int hashCode() { return number; }
    public boolean equals(final Object other) {
        return (other instanceof NumberExp &&
                ((NumberExp)other).number == number);
    }
    public String toString() {
        return Integer.toString(number);
    }
}