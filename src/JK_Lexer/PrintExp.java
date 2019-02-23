package JK_Lexer;

public class PrintExp implements Exp{
	public final String expression; 
	
	public PrintExp(final String expression) {
		this.expression=expression;
	}
	public int hashCode() {
		return expression.hashCode(); 
	}
	public boolean equals(final Object other) {
		if(other instanceof PrintExp) {
			final PrintExp otherExp= (PrintExp)other; 
			return otherExp.expression.equals(expression);
		}
		else {
			return false; 
		}
	}
}