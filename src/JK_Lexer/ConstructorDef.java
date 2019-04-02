package JK_Lexer;

import java.util.ArrayList;

public class ConstructorDef {
	public final Modifier mod; 
	public final String name;
	public final ArrayList<VariableDecExp> parameters;
	public final ArrayList<Statement> block;
	
	public ConstructorDef(final Modifier mod,
						final String name,
						final ArrayList<VariableDecExp> parameters,
						final ArrayList<Statement> block) {
		this.mod=mod;
		this.name=name;
		this.parameters = new ArrayList<VariableDecExp>(parameters);
		this.block = new ArrayList<Statement>(block);
	}
	public int hashCode() {
		return (mod.hashCode()+
				name.hashCode()+
				parameters.hashCode()+
				block.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof ConstructorDef) {
			final ConstructorDef otherExp=(ConstructorDef)other; 
			return (otherExp.mod.equals(mod) &&
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
				   "Name: " + name +"\n" +
				   "Parameters: " + parameters.toString()+ "\n"+
				   "Block: " + block.toString();
		return s;
	}
}