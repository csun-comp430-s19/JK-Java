package JK_Lexer;

public class CPrintExp implements CExp{
	public final String printstring; 
	public CPrintExp(final String printstring) {
		this.printstring=printstring; 
	}
	public int hashCode(){
		return printstring.hashCode(); 
	}
	public boolean equals(final Object other) {
		return (other instanceof CPrintExp && ((CPrintExp)other).printstring==printstring);
	}
	public String toString() {
		return "printf(" +printstring+")";
	}
}
