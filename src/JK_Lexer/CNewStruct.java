package JK_Lexer;

import java.util.ArrayList;

public class CNewStruct implements CStatement {
	ArrayList<CStatement> block;
	
	public CNewStruct(ArrayList<CStatement> block) {
		this.block = block;
	}
	
	public int hashcode() {
		return block.hashCode();
	}
	
	public boolean equals(Object other) {
		return (other instanceof CNewStruct) && ((CNewStruct)other).block.equals(block);
	}
	
	public String toString() {
		String str = "";
		for(CStatement stmt : block) {
			str += stmt.toString() + ";";
		}
		return str;
	}
}
