package JK_Lexer;

public class CallMethodExp implements Exp{
	public final Exp first; 
	public final Exp methodname; 
	public final Exp second; 
	
	public CallMethodExp(final Exp first,
						 final Exp methodname,
						 final Exp second) {
		this.first=first;
		this.methodname=methodname;
		this.second=second; 
	}
	public int hashCode() {
		return (first.hashCode()+
				methodname.hashCode()+
				second.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof CallMethodExp) {
			final CallMethodExp otherExp=(CallMethodExp)other; 
			return (otherExp.first.equals(first) &&
					otherExp.methodname.equals(methodname) &&
					otherExp.second.equals(second));
		}
		else {
			return false; 
		}
	}
}
