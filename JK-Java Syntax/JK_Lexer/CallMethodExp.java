package JK_Lexer;

import java.util.ArrayList;

public class CallMethodExp implements Exp{
	public final VariableExp input; 
	public final VariableExp methodname; 
	public final ArrayList<VariableExp> parameter; 
	
	public CallMethodExp(final VariableExp input,
						 final VariableExp methodname,
						 final ArrayList<VariableExp> parameter) {
		this.input=input;
		this.methodname=methodname;
		this.parameter= new ArrayList<VariableExp>(parameter); 
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
