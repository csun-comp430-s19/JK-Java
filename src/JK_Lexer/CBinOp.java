package JK_Lexer;

public class CBinOp implements CExp {
	public final CExp left;
	public final Op op;
	public final CExp right;

	public CBinOp(final CExp left, final Op op, final CExp right) {
		this.left = left;
		this.op = op;
		this.right = right;
	}

	public int hashCode() {
		return (left.hashCode() + op.hashCode() + right.hashCode());
	}

	public boolean equals(final Object other) {
		if (other instanceof BinopExp) {
			final CBinOp otherExp = (CBinOp) other;
			return (otherExp.left.equals(left) && otherExp.op.equals(op) && otherExp.right.equals(right));
		} else {
			return false;
		}
	}

	public String toString() {
		return ("(" + left.toString() + " " + op.toString() + " " + right.toString() + ")");
	}
}
