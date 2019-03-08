package JK_Lexer;

public class CallMethodExp implements Exp{
	public final Exp input; 
	public final Exp methodname; 
	public final Exp parameter; 
	
	public CallMethodExp(final Exp input,
						 final Exp methodname,
						 final Exp parameter) {
		this.input=input;
		this.methodname=methodname;
		this.parameter=parameter; 
	}
	public int hashCode() {
		return (input.hashCode()+
				methodname.hashCode()+
				parameter.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof CallMethodExp) {
			final CallMethodExp otherExp=(CallMethodExp)other; 
			return (otherExp.input.equals(input) &&
					otherExp.methodname.equals(methodname) &&
					otherExp.parameter.equals(parameter));
		}
		else {
			return false; 
		}
	}
}
