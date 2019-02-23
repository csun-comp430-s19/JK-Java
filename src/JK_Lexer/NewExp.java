package JK_Lexer;
//EXP that Creates new instance of a class
public class NewExp implements Exp{
	public final String classname; 
	public final String variable; 
	
	public NewExp(final String classname,
				  final String variable) {
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
