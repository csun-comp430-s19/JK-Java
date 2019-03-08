package JK_Lexer;
//EXP that Creates new instance of a class
public class NewExp implements Exp{
	public final Exp classname; 
	public final Exp variable; 
	
	public NewExp(final Exp classname,
				  final Exp variable) {
		this.classname=classname;
		this.variable=variable;
	}
	public int hashCode() {
		return (classname.hashCode()+
				variable.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof NewExp) {
			final NewExp otherExp=(NewExp)other; 
			return (otherExp.classname.equals(classname) &&
					otherExp.variable.equals(variable)); 
		}
		else {
			return false; 
		}
	}
}
