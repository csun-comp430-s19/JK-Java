package JK_Lexer;

import java.util.ArrayList;

public class CNewStruct implements CExp {
	String classname;
	int methodcount;
	String constructorname;
	ArrayList<CExp> params;
	
	public CNewStruct(ClassDefExp c, String constructorname, ArrayList<CExp> params) {
		this.classname = c.name;
		methodcount = c.methods.size();
		this.constructorname = constructorname;
		this.params = params;
	}
	
	public int hashcode() {
		return classname.hashCode() + constructorname.hashCode() + params.hashCode();
	}
	
	public boolean equals(Object other) {
		return (other instanceof CNewStruct) && ((CNewStruct)other).classname.equals(classname) && ((CNewStruct)other).constructorname.equals(constructorname) && ((CNewStruct)other).params.equals(params);
	}
	
	public String toString() {
		String str = constructorname+"(malloc(sizeof(struct "+classname+")), ";
		for(int i = 0; i < params.size(); i ++) {
			if(i == params.size()-1) {
				str+=params.get(i).toString();
			}else {
				str+= params.get(i).toString() + ", ";
			}
		}
		str+= ")";
		return str;
	}
}
