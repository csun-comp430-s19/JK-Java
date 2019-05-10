package JK_Lexer;

import java.util.ArrayList;

public class GenericClassDefinition extends ClassDefExp{
	public ArrayList<VariableExp> genericList; 
	public GenericClassDefinition(Modifier m, String s, ArrayList<ConstructorDef> c,
								  ArrayList<InstanceDecExp> i, ArrayList<MethodDefExp> me,
								  boolean b, String e, ArrayList<VariableExp> gList) {
		super(m,s,c,i,me,b,e);
		this.genericList=gList; 
	}
	public int hashCode() {
		return super.hashCode()+genericList.hashCode(); 
	}
	public boolean equals(Object other) {
		if(other instanceof GenericClassDefinition) {
			final GenericClassDefinition otherG = (GenericClassDefinition)other; 
			return (otherG.mod.equals(mod) &&
					otherG.name.equals(name) &&
					otherG.constructors.equals(constructors)&&
					otherG.members.equals(members) &&
					otherG.methods.equals(methods) &&
					otherG.genericList.equals(genericList));
		}else {
			return false; 
		}
	}
	public String toString() {
		return super.toString()+"Generics: "+genericList.toString(); 
	}
}
