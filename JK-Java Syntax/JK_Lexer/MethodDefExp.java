package JK_Lexer;

import java.util.ArrayList;

public class MethodDefExp {
	public final Modifier mod; 
	public final Type type;
	public final String name;
	public final ArrayList<VariableDecExp> parameters;
	public final ArrayList<Statement> block;
	
	public MethodDefExp(final Modifier mod,
						final Type type,
						final String name,
						final ArrayList<VariableDecExp> parameters,
						final ArrayList<Statement> block) {
		this.mod=mod;
		this.type=type;
		this.name=name;
		this.parameters = new ArrayList<VariableDecExp>(parameters);
		this.block = new ArrayList<Statement>(block);
	}
	public int hashCode() {
		return (mod.hashCode()+
				type.hashCode()+
				name.hashCode()+
				parameters.hashCode()+
				block.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof MethodDefExp) {
			final MethodDefExp otherExp=(MethodDefExp)other; 
			return (otherExp.mod.equals(mod) &&
					otherExp.type.equals(type) &&
					otherExp.name.equals(name) &&
					otherExp.parameters.equals(parameters) &&
					otherExp.block.equals(block));
		}
		else {
			return false; 
		}
	}
	public String toString() {
		String s = "Modifier: " + mod.toString() + "\n"+
				   "Type: " + type.toString() +"\n"+
				   "Name: " + name +"\n" +
				   "Parameters: " + parameters.toString()+ "\n"+
				   "Block: " + block.toString();
		return s;
	}
}
