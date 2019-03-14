package JK_Lexer;

import java.util.ArrayList;

public class Program {
	public ArrayList<Statement> statementList;
	public ArrayList<ClassDefExp> classDefList;

	public Program() {
		statementList = new ArrayList<Statement>();
		classDefList = new ArrayList<ClassDefExp>();
	}

	public Program(final ArrayList<Statement> statementList, final ArrayList<ClassDefExp> classDefList) {
		this.statementList = new ArrayList<Statement>(statementList);
		this.classDefList = new ArrayList<ClassDefExp>(classDefList);
	}

	public int hashCode() {
		return (statementList.hashCode() + classDefList.hashCode());
	}

	public boolean equals(final Object other) {
		if (other instanceof Program) {
			final Program otherExp = (Program) other;
			return (otherExp.statementList.equals(statementList) && otherExp.classDefList.equals(classDefList));
		} else {
			return false;
		}
	}

	public String toString() {
		String s = "ClassDefs: " + classDefList.toString() + "\n" + "Statements: " + statementList.toString();
		return s;
	}
}