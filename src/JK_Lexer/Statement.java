package JK_Lexer;

import java.util.ArrayList;

public interface Statement {
    // not needed; used to indicate that these should be overridden
    public int hashCode();
    public boolean equals(Object other);
    public String toString();
}
