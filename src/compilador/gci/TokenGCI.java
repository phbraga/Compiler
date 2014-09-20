/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package compilador.gci;

/**
 *
 * @author Pedro
 */
public class TokenGCI {
    
    private String lexema;
    private int code;

    public TokenGCI() {
        this.lexema = "";
        this.code = -1;
    }
    
    public TokenGCI(String lexema, int code) {
        this.lexema = lexema;
        this.code = code;
    }

    public String getLexema() {
        return lexema;
    }

    public void setLexema(String lexema) {
        this.lexema = lexema;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    @Override
    public String toString() {
        return "Classificacao: " + this.getCode() + ", Lexema: " + this.getLexema();
    }
}
