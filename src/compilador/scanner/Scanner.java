package compilador.scanner;

import compilador.util.Utils;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Pattern;

public class Scanner {
    
    public static final char EOF = '\uffff';
    public static Scanner scanner;
    
    private FileReader fileReader;
    
    private Token token = Token.getInstance();
    private char lookahead = ' ';
    
    private int lin = 1;
    private int col = 0;
    
    private Scanner() {
    }
    
    public static Scanner getInstance () {
        if ( scanner == null ) {
            scanner = new Scanner();
        }
        return scanner;
    }
      
    public Token getToken() {
        return this.token;
    }
    
    public char getLookahead() {
        return this.lookahead;
    }
    
    public int getLinha() {
        return this.lin;
    }
    
    public int getColuna() {
        return this.col;
    }
    
    public void setToken(String lexema, int code) {
        this.token.setLexema(lexema);
        this.token.setCode(code);
    } 
    
    public void setFileReader(FileReader fileReader) {
        this.fileReader = fileReader;
    }

    public char readChar(){
        char character = 0;
        try {
            int reader = this.fileReader.read();
            
            character = (char) reader;
            
            if( character == '\n') {
                this.col = 0;
                ++this.lin;
            }else if ( character == '\t')
                this.col += 4;
            else 
                ++this.col;
            
        } catch (IOException ex) {
            System.out.println("IOException - readChar()");
        }
        return character;
    }

    public Token scan (){
        
        if (null == fileReader) {
            System.out.println("Erro - FileReader Nulo");
            System.exit(-1);
        }
        
        try {
            String buffer = "";
            
            if (fileReader.ready() || lookahead != ' ') {
                
                while (Pattern.matches("[ \\t\\r\\n]", String.valueOf(lookahead)))
                    lookahead = readChar();
                
                buffer += lookahead;
                
                if(Pattern.matches("[0-9.]", String.valueOf(lookahead))) {
                    boolean dotControllerer = false;
                       
                    if (lookahead == '.')
                        dotControllerer = true;
                        
                    while (Pattern.matches("[0-9.]", String.valueOf(lookahead = readChar())) ) {
                        if (Character.isDigit(lookahead)){
                            buffer += lookahead;
                        }else if (lookahead == '.') {
                            if (!dotControllerer) {
                                buffer += lookahead;
                                dotControllerer = true;
                            } else {
                                Utils.showErrorMessage(this.token, this.lin, this.col, "Tipo float mal formado");
                            }
                        }
                    }
                    
                    if (dotControllerer) {
                        if (buffer.charAt( buffer.length() - 1) == '.')    
                            Utils.showErrorMessage(this.token, this.lin, this.col, "Tipo float mal formado");
                        setToken(buffer, Token.TIPO_FLOAT); 
                    } else {
                        setToken(buffer, Token.TIPO_INT);
                    }
                    
                } else if (Pattern.matches("[a-zA-Z_]", String.valueOf(lookahead))) {
                    
                    while (Pattern.matches("[a-zA-Z0-9_]", String.valueOf(lookahead = readChar()))) {
                        buffer += lookahead;
                    }
                    
                    if (buffer.equals("main")) {
                        setToken(buffer, Token.MAIN);
                    } else if (buffer.equals("if")) {
                        setToken(buffer, Token.IF);
                    } else if (buffer.equals("else")) {
                        setToken(buffer, Token.ELSE);
                    } else if (buffer.equals("while")) {
                        setToken(buffer, Token.WHILE);
                    } else if (buffer.equals("do")) {
                        setToken(buffer, Token.DO);
                    } else if (buffer.equals("for")) {
                        setToken(buffer, Token.FOR);
                    } else if (buffer.equals("int")) {
                        setToken(buffer, Token.INT);
                    } else if (buffer.equals("float")) {
                        setToken(buffer, Token.FLOAT);
                    } else if (buffer.equals("char")) {
                        setToken(buffer, Token.CHAR);
                    } else {
                        setToken(buffer, Token.IDENTIFICADOR);
                    }
                } else if (lookahead == '\'') {
                    
                    if (Pattern.matches("[a-zA-Z0-9]", String.valueOf(lookahead = readChar()))) {
                        buffer += lookahead;
                        if((lookahead = readChar()) == '\'') {
                            buffer += lookahead;
                            lookahead = readChar();
                            setToken(buffer, Token.TIPO_CHAR);
                        } else {
                            Utils.showErrorMessage(this.token, this.lin, this.col, "Tipo char mal formado");
                        }
                    } else {
                        Utils.showErrorMessage(this.token, this.lin, this.col, "Tipo char mal formado");
                    }
                } else if (lookahead == '<') {
                    
                    if ((lookahead = readChar()) == '=') {
                        buffer += lookahead;
                        setToken(buffer, Token.MENOR_IGUAL);
                        lookahead = readChar();
                    } else {
                        setToken(buffer, Token.MENOR);
                    }
                    
                    
                } else if (lookahead == '>') {
                    
                    if ((lookahead = readChar()) == '=') {
                        buffer += lookahead;
                        setToken(buffer, Token.MAIOR_IGUAL);
                        lookahead = readChar();
                    } else {
                        setToken(buffer, Token.MAIOR);
                    }
                    
                } else if (lookahead == '=') {
                    
                    if ((lookahead = readChar()) == '=') {
                        buffer += lookahead;
                        setToken(buffer, Token.IGUAL);
                        lookahead = readChar();
                    } else {
                        setToken(buffer, Token.ATRIBUICAO);
                    }
                    
                } else if (lookahead == '!') {
                    
                    if ((lookahead = readChar()) == '=') {
                        buffer += lookahead;
                        lookahead = readChar();
                        setToken(buffer, Token.DIFERENTE);
                    } else {
                        Utils.showErrorMessage(this.token, this.lin, this.col, "Operador Relacional mal formado");
                    }
                    
                } else if (lookahead == ')') {
                    lookahead = readChar();
                    setToken(buffer, Token.FECHA_PARENTESES);
                } else if (lookahead == '(') {
                    lookahead = readChar();
                    setToken(buffer, Token.ABRE_PARENTESES);
                } else if (lookahead == '}') {
                    lookahead = readChar();
                    setToken(buffer, Token.FECHA_CHAVES);
                } else if (lookahead == '{') {
                    lookahead = readChar();
                    setToken(buffer, Token.ABRE_CHAVES);
                } else if (lookahead == ',') {
                    lookahead = readChar();
                    setToken(buffer, Token.VIRGULA);
                } else if (lookahead == ';') {
                    lookahead = readChar();
                    setToken(buffer, Token.PONTO_VIRGULA);
                } else if (lookahead == '+') {
                    lookahead = readChar();
                    setToken(buffer, Token.SOMA);
                } else if (lookahead == '-') {
                    lookahead = readChar();
                    setToken(buffer, Token.SUBTRACAO);
                } else if (lookahead == '*') {
                    lookahead = readChar();
                    setToken(buffer, Token.MULTIPLICACAO);
                } else if (lookahead == '/') {
                    
                    lookahead = readChar();
                    
                    if (lookahead == '/') {           
                        while(true) {
                            if (Pattern.matches("[\\r\\n]", String.valueOf(lookahead = readChar()))) {
                                lookahead = readChar();
                                return scan();
                            } 
                        }
                    } else if (lookahead == '*') {
                        boolean comentControll = false;
                        while(true) {
                            while((lookahead = readChar()) == '*') {
                                comentControll = true;
                                if (lookahead == Scanner.EOF) {
                                    Utils.showErrorMessage(this.token, this.lin, this.col, "Comentário mal formado");
                                }
                            } 
                            if( comentControll && lookahead  == '/') {
                                lookahead = readChar();
                                return scan();
                            }else if (lookahead == Scanner.EOF) {
                                Utils.showErrorMessage(this.token, this.lin, this.col, "Comentário mal formado");
                            }
                            comentControll = false;
                        }
                    } else {
                        setToken(buffer, Token.DIVISAO);
                    }
                } else  if (lookahead == Scanner.EOF) {
                    return null;
                }else {
                    Utils.showErrorMessage(this.token, this.lin, this.col, "Caracter invalido");
                }
            } else {
                System.out.println("Erro - FileReader not ready");
            }
        } catch (IOException ex) {
            System.out.println("IOException - scan()");
        } 
        return this.token;
    }
}
