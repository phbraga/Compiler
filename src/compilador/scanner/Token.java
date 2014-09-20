package compilador.scanner;

public class Token {
    
    // 0 -> id ::= (letra | "_") (letra | "_" | dígito)*
    public static final int IDENTIFICADOR = 0;
    
    // [1-6] -> oprelacional ::= <  |  >  |  <=  |  >=  |  ==  |  !=
    public static final int MENOR = 1;
    public static final int MAIOR = 2;
    public static final int MENOR_IGUAL = 3;
    public static final int MAIOR_IGUAL = 4;
    public static final int IGUAL = 5;
    public static final int DIFERENTE = 6;
    
    // [7-11] -> oparitmético ::= "+"  |  "-"  |  "*"  |  "/"  |  "="
    public static final int SOMA = 7;
    public static final int SUBTRACAO = 8;
    public static final int MULTIPLICACAO = 9;
    public static final int DIVISAO = 10;
    public static final int ATRIBUICAO = 11;
    
    // [12-17] -> especial ::= ")"  |  "("  |  "{"  |  "}"  |  ","  |  ";"
    public static final int ABRE_PARENTESES = 12;
    public static final int FECHA_PARENTESES = 13;
    public static final int ABRE_CHAVES = 14;
    public static final int FECHA_CHAVES = 15;
    public static final int VIRGULA = 16;
    public static final int PONTO_VIRGULA = 17;
    
    // [18-26] -> palreservada ::= main  |  if  |  else  |  while  |  do  |  for  |  int  |  float  |  char
    public static final int MAIN = 18;
    public static final int IF = 19;
    public static final int ELSE = 20;
    public static final int WHILE = 21;
    public static final int DO = 22;
    public static final int FOR = 23;
    public static final int INT = 24;
    public static final int FLOAT = 25;
    public static final int CHAR = 26;
    
    // [27-29] -> tipos int | float | char
    public static final int TIPO_INT = 27;
    public static final int TIPO_FLOAT = 28;
    public static final int TIPO_CHAR = 29;

    private static Token token;
    private String lexema;
    private int code;

    private Token() {
        this.lexema = "";
        this.code = -1;
    }
    
    public static Token getInstance() {
        if( token == null )
            token = new Token();
        return token;
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
