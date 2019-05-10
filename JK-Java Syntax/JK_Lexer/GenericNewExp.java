package JK_Lexer;

import java.util.ArrayList;

public class GenericNewExp implements Exp{
	public final VariableExp className; 
	public ArrayList<Type> typeList; 
	public ArrayList<VariableExp> varList; 
	public GenericNewExp(VariableExp c, ArrayList<Type> t, ArrayList<VariableExp> v ) {
		className=c;
		typeList=t;
		varList=v; 
	}
	public int hashCode() {
		return className.hashCode()+typeList.hashCode()+varList.hashCode(); 
	}
	public boolean equals(final Object other) {
		if(other instanceof GenericNewExp) {
			final GenericNewExp otherN = (GenericNewExp)other; 
			return otherN.className.equals(className) && otherN.typeList.equals(typeList) && otherN.varList.equals(varList); 
		}
		else
			return false; 
	}
}
