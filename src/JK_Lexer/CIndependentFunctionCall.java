package JK_Lexer;

public class CIndependentFunctionCall implements CStatement{
	public final CFunctionCall func;
	
	public CIndependentFunctionCall(CFunctionCall func) {
		this.func = func;
	}
	
	public int hashcode() {
		return func.hashcode();
	}
	
	public boolean equals(final Object other) {
		return (other instanceof CIndependentFunctionCall) && ((CIndependentFunctionCall)other).func.equals(this.func);
	}
	
	public String toString() {
		return func.toString();
	}
	
}
