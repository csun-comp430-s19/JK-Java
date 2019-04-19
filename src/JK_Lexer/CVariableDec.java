package JK_Lexer;

public class CVariableDec implements CStatement{
	public final CType type;
	public final CVariableExp var; 
	
	public CVariableDec(final CType type,
						  final CVariableExp var) {
		this.type=type;
		this.var=var;
	}
	public int hashCode() {
		return (type.hashCode()+
				var.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof CVariableDec) {
			final CVariableDec otherExp=(CVariableDec)other; 
			return (otherExp.type.equals(type) &&
					otherExp.var.equals(var));
		}
		else {
			return false; 
		}
	}
	public String toString() {
		if(type instanceof CChar) {
			return type.toString() + " " + var.toString() + "[]";
		}
		else {		
			return type.toString() + " " +  var.toString()+ ";";
		}
	}
}
