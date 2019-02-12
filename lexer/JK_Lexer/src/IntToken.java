package JK_Lexer; 
public class IntToken{
    public int hashCode(){ return 14;}
    public boolean equals(final Object other){
        return other instanceof IntToken; 
    }
    public String toString(){
        return "int";
    }
}