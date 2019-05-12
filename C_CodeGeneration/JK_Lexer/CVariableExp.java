package JK_Lexer;

public class CVariableExp implements CExp{
	    public final String name;
	    public final boolean userdefined;

	    public CVariableExp(final String name,final boolean userdefined) {
	        this.name = name;
	        this.userdefined = userdefined;
	    }

	    public int hashCode() { return name.hashCode(); }
	    public boolean equals(final Object other) {
	        return (other instanceof CVariableExp &&
	                ((CVariableExp)other).name.equals(name))&&
	        		((CVariableExp)other).userdefined == userdefined;
	    }
	    public String toString() {
	    	if(userdefined) return "user_"+name;
	    	else return name;
	    }
}