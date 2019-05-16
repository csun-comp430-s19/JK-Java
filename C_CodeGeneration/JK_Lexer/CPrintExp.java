package JK_Lexer;

public class CPrintExp implements CExp{
	public final String printstring;
	public final CType type;
	public CPrintExp(final String printstring, CType type) {
		this.printstring=printstring; 
		this.type = type;
	}
	public int hashCode(){
		return printstring.hashCode() + type.hashCode(); 
	}
	public boolean equals(final Object other) {
		return (other instanceof CPrintExp && ((CPrintExp)other).printstring==printstring) && ((CPrintExp)other).type.equals(type);
	}
	public String toString() {
		if(type instanceof Cint) {
			return "printf(\"%d\\n\"," +printstring+")";
		}else if (type instanceof CChar) {
			return "printf(\"%s\\n\"," +printstring+")";
		}
		else {
			return "printf(\"\\n\")";
		}
	}
}
