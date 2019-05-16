package JK_Lexer;

import java.util.ArrayList;

public class CFunctionCall implements CExp{
	public final String callingname;
	public final String classname;
	public final String name;
	public final ArrayList<CVariableExp> parameters;
	public final int funcptrnum;
	
	public CFunctionCall(String callingname, String classname, String name, ArrayList<CVariableExp> parameters, int num) {
		this.callingname = callingname;
		this.classname = classname;
		this.name = name;
		this.parameters = new ArrayList<CVariableExp>(parameters);
		this.funcptrnum = num;
	}
	
	public int hashcode() {
		return callingname.hashCode() + classname.hashCode() + name.hashCode() + parameters.hashCode() + funcptrnum;
	}
	
	public boolean equals(final Object other) {
		if(other instanceof CFunctionCall) {
			return ((CFunctionCall) other).callingname.equals(this.callingname) && 
					((CFunctionCall) other).classname.equals(this.classname) && 
					((CFunctionCall) other).name.equals(this.name) && 
					((CFunctionCall) other).parameters.equals(this.parameters) &&
					((CFunctionCall) other).funcptrnum == this.funcptrnum;
		}else {
			return false;
		}
	}
	
	public String toString() {
		//String s = classname + "_" + name + "(";
		String s = callingname + "->vtable[" + funcptrnum + "](";
		for(int i = 0 ; i < parameters.size(); i++) {
			if(i == parameters.size()-1) {
				s+= parameters.get(i).toString();
			}else {
				s+= parameters.get(i).toString() + ", ";
			}
		}
		s+= ")";
		
		return s;
	}
}
