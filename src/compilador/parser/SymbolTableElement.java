package compilador.parser;

public class SymbolTableElement {
    
    private String lexeme;
    private int scope;
    private int type;
    
    public SymbolTableElement (String lexeme, int scope, int type) {
        this.lexeme = lexeme;
        this.scope = scope;
        this.type = type;
    }
    
    public String getLexeme () {
        return lexeme;
    }
    
    public int getScope () {
        return scope;
    }
    
    public int getType () {
        return type;
    }
}
