package JK_Lexer;

public class CStructType implements CType{
	String structname;
	boolean isPointer;
	
	public CStructType(String structName, boolean isPointer) {
		this.structname = structName;
		this.isPointer = isPointer;
	}
	
	public int hashCode() { 
		if(isPointer) return structname.hashCode() +1;
		else return structname.hashCode();
	}
	
	public boolean equals (final Object other) {
		return other instanceof CStructType && ((CStructType)other).structname.equals(this.structname) && ((CStructType)other).isPointer == isPointer;
	}
	public String toString() {
		if(isPointer) return "struct " + structname + "*";
		else return "struct " + structname;
	}
}
