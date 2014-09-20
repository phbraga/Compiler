package compilador.parser;

import compilador.gci.TokenGCI;
import compilador.scanner.Token;
import java.util.Iterator;
import java.util.Stack;

public class SymbolTable {
    
    private static SymbolTable symbolTable;
    private Stack<SymbolTableElement> stackSymbolTable;
    private int scope;
    
    private SymbolTable() {
        stackSymbolTable = new Stack<>();
        scope = 0;
    }
    
    public static SymbolTable getInstance () {
        if( symbolTable == null ) {
            symbolTable = new SymbolTable();
        }
        return symbolTable;
    }
    
    public TokenGCI lookup (String lexeme, int scope) {
        TokenGCI searched  = null;
        for(int i = stackSymbolTable.size() - 1 ; i >= 0; --i) {
            SymbolTableElement element = stackSymbolTable.get(i);
            if(lexeme.equals(element.getLexeme()) && scope == element.getScope()) {
                searched = new TokenGCI(element.getLexeme(), element.getType());
                break;
            }
        }
        return searched;
    }
    
    public TokenGCI lookup (String lexeme) {
        TokenGCI searched = null;
        for(int i = stackSymbolTable.size() - 1 ; i >= 0; --i) {
            SymbolTableElement element = stackSymbolTable.get(i);
            if(lexeme.equals(element.getLexeme())) {
                searched = new TokenGCI(element.getLexeme(), element.getType());
                break;
            }
        }
        return searched;
    }
    
    public void push (SymbolTableElement symbol) {
        stackSymbolTable.push(symbol);
    }
   
    public void incrementScope () {
        this.scope++;
    }
    
    public void decrementScope () {
        removeFromScope(scope);
        this.scope--;
    }
    
    public int getScope () {
        return scope;
    }

    private void removeFromScope(int scope) {
        Iterator<SymbolTableElement> iterator = stackSymbolTable.iterator();
        while(iterator.hasNext()) {
            if (iterator.next().getScope() == scope) {                
                iterator.remove();
            }
        }
    }
}
