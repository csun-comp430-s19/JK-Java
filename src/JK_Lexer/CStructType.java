package JK_Lexer;

public class CStructType implements CType{
	String structname;
	
	public CStructType(String structName) {
		this.structname = structName;
	}
	
	public int hashCode() { return structname.hashCode(); }
	public boolean equals (final Object other) {
		return other instanceof CStructType && ((CStructType)other).structname.equals(this.structname);
	}
	public String toString() {
		return "struct " + structname;
	}
}
