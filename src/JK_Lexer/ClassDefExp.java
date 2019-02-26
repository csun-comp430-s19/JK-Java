package JK_Lexer;

import java.util.ArrayList;

public class ClassDefExp {
	public final Modifier mod; 
	public final String name;
	public final ArrayList<InstanceDecExp> members;
	public final ArrayList<MethodDefExp> methods;
	
	public ClassDefExp(final Modifier mod,
						final String name,
						final ArrayList<InstanceDecExp> members,
						final ArrayList<MethodDefExp> methods) {
		this.mod=mod;
		this.name=name;
		this.members = new ArrayList<InstanceDecExp>(members);
		this.methods = new ArrayList<MethodDefExp>(methods);
	}
	public int hashCode() {
		return (mod.hashCode()+
				name.hashCode()+
				members.hashCode()+
				methods.hashCode()); 
	}
	public boolean equals(final Object other) {
		if(other instanceof ClassDefExp) {
			final ClassDefExp otherExp=(ClassDefExp)other; 
			return (otherExp.mod.equals(mod) &&
					otherExp.name.equals(name) &&
					otherExp.members.equals(members) &&
					otherExp.methods.equals(methods));
		}
		else {
			return false; 
		}
	}
	
	public String toString() {
		String s = "Modifier: " + mod.toString() + "\n"+
				   "Name: " + name +"\n" +
				   "Members: " + members.toString()+ "\n"+
				   "Methods: " + methods.toString();
		return s;
	}
}
