package JK_Lexer;

public class IndependentMethodCallStmt implements Statement{
	public final CallMethodExp methodcall;
	
	public IndependentMethodCallStmt(CallMethodExp methodcall) {
		this.methodcall = new CallMethodExp(methodcall.input, methodcall.methodname, methodcall.parameter);
	}
	
	public int hashcode() {
		return methodcall.hashCode();
	}
	
	public boolean equals(final Object other) {
		if(other instanceof IndependentMethodCallStmt) {
			return ((IndependentMethodCallStmt)other).methodcall.equals(this.methodcall);
		}else {
			return false;
		}
	}
	
	public String toString() {
		return methodcall.toString();
	}
	
}
