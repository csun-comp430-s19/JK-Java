package JK_Lexer;

import java.util.ArrayList;

public class GenericObjectType implements Type{
	public final String className; 
	public ArrayList<Type> typeArray; 
	public GenericObjectType(String c, ArrayList<Type> t) {
		className=c; 
		typeArray=t;
	}
	public int hashcode() { return className.hashCode()+typeArray.hashCode(); }
	public boolean equals(final Object other) {
		if(other instanceof GenericObjectType) {
			final GenericObjectType otherT = (GenericObjectType)other; 
			return otherT.className.contentEquals(className) && otherT.typeArray.equals(typeArray); 
		}
		else
			return false; 
	}
	public String toString() {
		String s = className+"<"; 
		for(Type t: typeArray) {
			s+=t.toString(); 
			s+=", ";
		}
		s+=">";
		return s; 
	}
}
