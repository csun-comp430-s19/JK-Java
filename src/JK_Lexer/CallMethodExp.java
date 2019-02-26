package JK_Lexer;

public class CallMethodExp implements Exp{
	public final String first; 
	public final String methodname; 
	public final String second; 
	
	public CallMethodExp(final String first,
						 final String methodname,
						 final String second) {
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
