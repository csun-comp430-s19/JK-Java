package JK_Lexer;

public class COp implements CInstruction{
	public final Op op;
	public COp(final Op op) {
		this.op=op; 
	}
	public int hashCode() {
		return op.hashCode(); 
	}
	public boolean equals(final Object other) {
		return (other instanceof COp && ((COp)other).op==op);
	}
	public String toString() {
		return op.toString(); 
	}
}
