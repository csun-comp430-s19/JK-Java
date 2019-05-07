package JK_Lexer;

public class VariableExp implements Exp {
    public final String name;

    public VariableExp(final String name) {
        this.name = name;
    }

    public int hashCode() { return name.hashCode(); }
    public boolean equals(final Object other) {
        return (other instanceof VariableExp &&
                ((VariableExp)other).name.equals(name));
    }
    public String toString() {
        return name;
    }
}
