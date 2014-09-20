package compilador.parser;

import compilador.gci.TokenGCI;
import compilador.scanner.Scanner;
import compilador.scanner.Token;
import compilador.util.Utils;
import java.util.ArrayList;

public class Parser {
    /*
     * <decl_var> ::= <tipo> <id> {,<id>}* ;
     * <tipo> ::= int | float | char
     * <programa> ::= int main"("")" <bloco>
     * <bloco> ::= “{“ {<decl_var>}* {<comando>}* “}”
     * <comando> ::= <comando_básico> | <iteração> | if "("<expr_relacional>")" <comando> {else <comando>}?
     * <comando_básico> ::= <atribuição> | <bloco>
     * <iteração> ::= while "("<expr_relacional>")" <comando> | do <comando> while "("<expr_relacional>")"";"
     * <atribuição> ::= <id> "=" <expr_arit> ";"
     * <expr_relacional> ::= <expr_arit> <op_relacional> <expr_arit>
     * <expr_arit> ::= <expr_arit> "+" <termo>   | <expr_arit> "-" <termo> | <termo>
     * <termo> ::= <termo> "*" <fator> | <termo> “/” <fator> | <fator>
     * <fator> ::= “(“ <expr_arit> “)” | <id> | <real> | <inteiro> | <char>
     */
    
    public static Parser parser;
    private Scanner scanner = Scanner.getInstance();
    private SymbolTable symbolTable = SymbolTable.getInstance();
    private Token lookahead;
    private boolean isDiv = false;
    private int counterTemps = 0;
    private int counterLabels = 0;
    
    public static final int GENERAL = 0;
    public static final int ASSIGN = 1;
    
    private final ArrayList<Integer> firstDeclaration;
    private final ArrayList<Integer> firstCommand;
    private final ArrayList<Integer> firstBasicCommand;
    private final ArrayList<Integer> firstIteration;
    private final ArrayList<Integer> firstBlock;
    private final ArrayList<Integer> firstAsing;
    private final ArrayList<Integer> firstType;
    private final ArrayList<Integer> listRelationalOpTypes;  
    private final ArrayList<String> listAllOperators;

    
    private Parser() {
        firstDeclaration = Utils.createListFirstDeclaration();
        firstCommand = Utils.createListFirstCommand();
        firstBasicCommand = Utils.createListFirstBasicCommand();
        firstIteration = Utils.createListFirstIteration();
        firstBlock = Utils.createListFirstBlock();
        firstAsing = Utils.createListFirstAssing();
        firstType = Utils.createListFirstType();
        listRelationalOpTypes = Utils.createListRelationalOpTypes();
        listAllOperators = Utils.createListAllOperators();
    }
    
    public static Parser getInstance() {
        if( parser == null) {
            parser = new Parser();
        }
        return parser;
    }

    public Token getLookahead() {
        return lookahead;
    }

    public void init() {
        lookahead = scanner.scan();
        readProgram();
    }
    
    private void readProgram() {
        if(lookahead == null || lookahead.getCode() != Token.INT) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O programa deve comecar com 'int main()'. Esperado: 'int', Atual: nao encontrado.");
        }
        
        lookahead = scanner.scan();
        if (lookahead == null || lookahead.getCode() != Token.MAIN) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O programa deve comecar com 'int main()'. Esperado: 'main' apos 'int', Atual: nao encontrado.");
        }
        
        lookahead = scanner.scan();
        if (lookahead == null || lookahead.getCode() != Token.ABRE_PARENTESES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O programa deve comecar com 'int main()'. Esperado: '(' apos 'main', Atual: nao encontrado.");
        }
        
        lookahead = scanner.scan();        
        if (lookahead == null || lookahead.getCode() != Token.FECHA_PARENTESES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O programa deve comecar com 'int main()'. Esperado: ')' apos '(', Atual: nao encontrado.");
        } 
        
        lookahead = scanner.scan();
        readBlock();
        
        if(lookahead != null) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(),
                                    "Apos o programa principal 'int main () { ... }' nao deve existir mais comandos.");
        }
    }
    
    private void readBlock() {
        symbolTable.incrementScope();
        
        if (lookahead == null || lookahead.getCode() != Token.ABRE_CHAVES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O bloco deve comecar com '{ ... }'. Esperado: '{', Atual: nao encontrado.");
        }
        
        lookahead = scanner.scan();
        while(lookahead != null && firstDeclaration.contains(lookahead.getCode())) {
            readDeclaration();
        }
        
        while(lookahead != null && firstCommand.contains(lookahead.getCode())) {
            readCommand();
        }
         
        if (lookahead == null || lookahead.getCode() != Token.FECHA_CHAVES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O bloco deve comecar com '{ ... }'. Esperado: '}' no final do bloco, Atual: nao encontrado.");
        }
        
        symbolTable.decrementScope();
        lookahead = scanner.scan();
    }
    
    private void readDeclaration() {
        if (lookahead == null || !firstDeclaration.contains(lookahead.getCode())) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Uma declaracao deve comecar com um tipo int, float ou char.");
        }
        
        int firstCode = lookahead.getCode();
        
        while(true) {
            if ((lookahead = scanner.scan()) == null || lookahead.getCode() != Token.IDENTIFICADOR) {
                Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                        "Uma declaracao deve conter um identificador depois da declaracao do tipo da variavel ou de uma virgula.");
            }
            
            if(symbolTable.lookup(lookahead.getLexema(), symbolTable.getScope()) != null) {
                Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                        "Duplicidade na declaracao do identificador '" + lookahead.getLexema() + "' num mesmo bloco");
            }
            
            symbolTable.push(new SymbolTableElement(lookahead.getLexema(), symbolTable.getScope(), getTypeByFirstCode(firstCode)));
            
            if ((lookahead = scanner.scan()) == null || lookahead.getCode() != Token.VIRGULA) {
                break;
            } 
        }
        
        if(lookahead != null && lookahead.getCode() != Token.PONTO_VIRGULA) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Uma declaracao deve terminar com um ';'. Esperado: ';', Atual: nao encontrado.");
        }
        
        lookahead = scanner.scan();
    }
    
    private void readCommand() {
        if (lookahead != null && firstBasicCommand.contains(lookahead.getCode())) {
            readBasicCommand();
        } else if (lookahead != null && firstIteration.contains(lookahead.getCode())) {
            readIteration();
        } else if (lookahead != null && lookahead.getCode() == Token.IF) {
            readIfElse();
        } else {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Um comando deve inciar com um comando basico, iteracao ou if-else.");
        }
    }
    
    private void readBasicCommand() {
        if (lookahead != null && firstAsing.contains(lookahead.getCode())) {
            readAssign();
        } else if (lookahead != null && firstBlock.contains(lookahead.getCode())) {
            readBlock();
        } else {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Um comando basico deve inciar com uma atribuicao ou um bloco.");
        }
    }
    
    private void readIteration() {
        if (lookahead == null || lookahead.getCode() == Token.WHILE) {
            readWhile();
        } else if (lookahead == null || lookahead.getCode() == Token.DO) {
            readDoWhile();
        } else {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Uma iteracao deve comecar com um while ou do-while.");
        }
    }
    
    private void readAssign() {
        TokenGCI lValue, rValue;
        if (lookahead == null || lookahead.getCode() != Token.IDENTIFICADOR) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Uma atribuicao deve comecar com um identificador. Esperado: Identificador, Atual: nao encontrado.");
        }
        
        if((lValue = symbolTable.lookup(lookahead.getLexema())) == null) {
                Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                        "A variavel '" + lookahead.getLexema() + "' nao foi declarada");
        }
        
        lookahead = scanner.scan(); 
        if (lookahead == null || lookahead.getCode() != Token.ATRIBUICAO) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Uma atribuicao deve conter um '=' apos o identificador. Esperado: '=', Atual: nao encontrado.");
        }
        
        lookahead = scanner.scan();
        rValue = readArithmeticExp();
        
        verifyAssign(lValue, rValue);
        
        if (lookahead == null || lookahead.getCode() != Token.PONTO_VIRGULA) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Uma atribuicao deve terminar com ';'. Esperado: ';', Atual: nao encontrado.");
        }
        
        lookahead = scanner.scan();
    }
    
    private TokenGCI readRelationalExp() {
        TokenGCI leftSide = readArithmeticExp();
        
        if(lookahead != null && !listRelationalOpTypes.contains(lookahead.getCode())) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Uma expressao relacional precisa conter um operador relacional Esperado: '==' ,'!=', '>', '>=', '<' ou '<=', Atual: nao encontrado.");
        }
        String operator = lookahead.getLexema();
        
        lookahead = scanner.scan();
        TokenGCI rightSide = readArithmeticExp();
        
        if(rightSide != null) {
            rightSide.setLexema(operator + rightSide.getLexema());
        }
        TokenGCI fValue = semanticRules(leftSide, rightSide, Parser.GENERAL);
        
        return fValue;
    }
    
    private TokenGCI readArithmeticExp() {
        TokenGCI lValue = readTerm();
        TokenGCI rValue = readArithmeticExpLine();
        
        return semanticRules(lValue, rValue, Parser.GENERAL);
    }
    
    private TokenGCI readArithmeticExpLine() {
        TokenGCI lValue = null, rValue = null, fValue = null;
        String lexeme = "";
        if(lookahead != null && (lookahead.getCode() == Token.SOMA || lookahead.getCode() == Token.SUBTRACAO)) {
            lexeme = lookahead.getLexema();
            lookahead = scanner.scan();
            lValue = readTerm();
            rValue = readArithmeticExpLine();
            
            fValue = semanticRules(lValue, rValue, Parser.GENERAL);
            fValue.setLexema(lexeme + fValue.getLexema());
        }
        
        return fValue;
    }
    
    private TokenGCI readTerm() {
        TokenGCI lValue = readFactor();
        TokenGCI rValue = readTermLine();
        
        return semanticRules(lValue, rValue, Parser.GENERAL);
    }
    
    private TokenGCI readTermLine() {
        TokenGCI lValue = null, rValue = null, fValue = null;
        String lexeme = "";
        if(lookahead != null && (lookahead.getCode() == Token.MULTIPLICACAO || lookahead.getCode() == Token.DIVISAO)) {
            lexeme = lookahead.getLexema();
            isDiv = lookahead.getCode() == Token.DIVISAO ? true : false;
            lookahead = scanner.scan();
            lValue = readFactor();
            rValue = readTermLine();
            
            fValue = semanticRules(lValue, rValue, Parser.GENERAL);
            fValue.setLexema(lexeme + fValue.getLexema());
            
        } 
        
        return fValue;
    }
    
    private TokenGCI readFactor() {
        TokenGCI lValue = null;
        if(lookahead != null && lookahead.getCode() == Token.ABRE_PARENTESES) {
            lookahead = scanner.scan();
            lValue = readArithmeticExp();
            
            if (lookahead != null && lookahead.getCode() != Token.FECHA_PARENTESES) {
                Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Um fator deve comecar com '('. Esperado: '(', Atual: nao encontrado");
            }
            
            lookahead = scanner.scan();
        } else if (lookahead != null && lookahead.getCode() == Token.IDENTIFICADOR){
            if((lValue = symbolTable.lookup(lookahead.getLexema())) == null) {
                Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                        "A variavel '" + lookahead.getLexema() + "' nao foi declarada");
            }
            lookahead = scanner.scan();
        } else if (lookahead != null && firstType.contains(lookahead.getCode())) {
            lValue = new TokenGCI(lookahead.getLexema(), lookahead.getCode());
            lookahead = scanner.scan();            
        } else {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                        "Um fator deve comecar com '( exp aritmetica )', identificador, float, inteiro ou char.");
        }
        
        return lValue;
    }
    
    private void readIfElse() {
        if (lookahead == null || lookahead.getCode() != Token.IF) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando if deve comecar com 'if'. Esperado: 'if', Atual: nao encontrado");
        }
        
        lookahead = scanner.scan();
        if (lookahead == null || lookahead.getCode() != Token.ABRE_PARENTESES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando if deve conter '( ... )'apos 'if'. Esperado: '(', Atual: nao encontrado");
        }
        
        lookahead = scanner.scan();
        TokenGCI value = readRelationalExp();
        String labelElse = newLabel();
        String labelExitIf = newLabel();
        
        if (lookahead == null || lookahead.getCode() != Token.FECHA_PARENTESES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando if deve conter '( ... )' apos 'if'. Esperado: ')', Atual: nao encontrado");
        }
        System.out.println("if " + value.getLexema() + " == 0 goto " + labelElse);
        
        lookahead = scanner.scan();
        readCommand();
        
        System.out.println("goto " + labelExitIf);
        System.out.println(labelElse + ":");
        
        if (lookahead != null && lookahead.getCode() == Token.ELSE) {
            lookahead = scanner.scan();
            readCommand();
        }
        
        System.out.println(labelExitIf + ":");
    }
    
    private void readWhile() {
        if (lookahead == null || lookahead.getCode() != Token.WHILE) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando while deve iniciar com 'while'. Esperado: 'while', Atual: nao encontrado");
        }
        
        String labelInitWhile = newLabel();
        String lavelExitWhile = newLabel();
        System.out.println(labelInitWhile + ":");
        
        lookahead = scanner.scan();
        if (lookahead == null || lookahead.getCode() != Token.ABRE_PARENTESES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando while deve conter '( ... )'apos 'while'. Esperado: '(', Atual: nao encontrado");
        }
        
        lookahead = scanner.scan();
        TokenGCI value = readRelationalExp();
        
        System.out.println("if " + value.getLexema() + " == 0 goto " + lavelExitWhile);
        
        if (lookahead == null || lookahead.getCode() != Token.FECHA_PARENTESES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando while deve conter '( ... )' apos 'while'. Esperado: ')', Atual: nao encontrado");
        }
        
        lookahead = scanner.scan();
        readCommand();
        
        System.out.println("goto " + labelInitWhile + "\n");
        System.out.println(lavelExitWhile + ":");
    }
    
    private void readDoWhile() {
        if (lookahead == null || lookahead.getCode() != Token.DO) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando do-while deve comecar com 'do'. Esperado: 'do', Atual: nao encontrado");
        }
        
        String label = newLabel();
        System.out.println(label + ":");
        lookahead = scanner.scan();
        readCommand();
         
        if (lookahead == null || lookahead.getCode() != Token.WHILE) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando do-while deve conter 'while'. Esperado: 'while', Atual: nao encontrado");
        }
        
        lookahead = scanner.scan();
        if (lookahead == null || lookahead.getCode() != Token.ABRE_PARENTESES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando do-while deve conter '( ... )'apos 'while'. Esperado: '(', Atual: nao encontrado");
        }
        
        lookahead = scanner.scan();
        TokenGCI value = readRelationalExp();
        
        if (lookahead == null || lookahead.getCode() != Token.FECHA_PARENTESES) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando do-while deve conter '( ... )' apos 'while'. Esperado: ')', Atual: nao encontrado");
        }
        
        lookahead = scanner.scan();
        if (lookahead == null || lookahead.getCode() != Token.PONTO_VIRGULA) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "O comando do-while deve conter ';' apos 'while( ... )'. Esperado: ';', Atual: nao encontrado");
        }
        
        System.out.println("if " + value.getLexema() + " != 0 goto " + label);
        
        lookahead = scanner.scan();
    }
    
    public int getTypeByFirstCode (int firstCode) {
        int type;
        switch(firstCode) {
            case Token.INT:
                type = Token.TIPO_INT;
                break;
            case Token.FLOAT:
                type = Token.TIPO_FLOAT;
                break;
            case Token.CHAR:
                type = Token.TIPO_CHAR;
                break;
            default:
                type = firstCode;
        }
        return type;
    }
      
    public TokenGCI semanticRules(TokenGCI lValue, TokenGCI rValue, int code) {
        TokenGCI resultantToken = null;
        if(lValue == null && rValue == null) {
            return null;
        } else if (rValue == null) {
            resultantToken = lValue;
        } else if (lValue == null) {
            resultantToken = rValue;
        } else if ((lValue.getCode() == Token.TIPO_CHAR && rValue.getCode() != Token.TIPO_CHAR) 
                    || (lValue.getCode() != Token.TIPO_CHAR && rValue.getCode() == Token.TIPO_CHAR) ) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(), 
                                    "Tipo CHAR so e compativel com o tipo CHAR");
        }  else {                    
            int opCode;
            if(isDiv) {
                if(lValue.getCode() == Token.TIPO_CHAR && rValue.getCode() == Token.TIPO_CHAR) {
                    opCode = Token.TIPO_CHAR;
                } else {
                    opCode = Token.TIPO_FLOAT;
                }
                isDiv = false;
            } else {
                if(lValue.getCode() == Token.TIPO_CHAR && rValue.getCode() == Token.TIPO_CHAR) {
                    opCode = Token.TIPO_CHAR;
                }else if(lValue.getCode() == Token.TIPO_FLOAT || rValue.getCode() == Token.TIPO_FLOAT) {
                    opCode = Token.TIPO_FLOAT;
                } else {
                    opCode = Token.TIPO_INT;
                }
            }
            
            if(code == Parser.ASSIGN) {
                GCIAssign(lValue, rValue);
            } else {
                resultantToken = GCI(lValue, rValue);
            }
            
            if(resultantToken != null)
                resultantToken.setCode(opCode);
        }
        
        return resultantToken;
        
    }
    
    private void verifyAssign(TokenGCI lValue, TokenGCI rValue) {
        if( lValue.getCode() != rValue.getCode() && (lValue.getCode() == Token.TIPO_INT && rValue.getCode() == Token.TIPO_FLOAT)) {
            Utils.showErrorMessage(lookahead, scanner.getLinha(), scanner.getColuna(),
                    "Não pode ser atribuido um valor do tipo FLOAT a um identificador do tipo INT");
        }
        semanticRules(lValue, rValue, Parser.ASSIGN);
    }
   
    private String newTemp () {
        return "T" + counterTemps++;
    }
    
    private String newLabel () {
        return "L" + counterLabels++;
    }
    
    private String getsignal (TokenGCI rValue) {
        String signal = "";
        
        for(int i = 0 ; i < listAllOperators.size() ; ++i) {
            if(rValue.getLexema().contains(listAllOperators.get(i))) {
                signal = listAllOperators.get(i);
                break;
            }
        }
        
        return signal;
    }
    
    private TokenGCI GCI (TokenGCI lValue, TokenGCI rValue) {
        TokenGCI gci = new TokenGCI();
        String temp = newTemp();
        
        String signal = getsignal(rValue);
        String rValueLexeme = rValue.getLexema().substring(signal.length(), rValue.getLexema().length());
        
        System.out.print(temp + " = ");
        
        if (lValue.getCode() != Token.TIPO_FLOAT && rValue.getCode() == Token.TIPO_FLOAT) {
            System.out.println("(float) " + lValue.getLexema());
            String previousTemp = temp;
            temp = newTemp();
            System.out.println(temp + " = " + previousTemp + rValue.getLexema());
            gci.setLexema(temp);
            
            return gci;
            
        } else if (lValue.getCode() == rValue.getCode()) {
            System.out.print(lValue.getLexema());
        }
        
        if(lValue.getCode() == Token.TIPO_FLOAT && rValue.getCode() != Token.TIPO_FLOAT) {
            System.out.println("(float) " + rValueLexeme);
            String previousTemp = temp;
            temp = newTemp();
            System.out.println(temp + " = " + lValue.getLexema() + signal + previousTemp);
            gci.setLexema(temp);

            return gci;
            
        } else {
            System.out.println(rValue.getLexema());
        }
        
        gci.setLexema(temp);
        
        return gci;
    }

    private void GCIAssign (TokenGCI lValue, TokenGCI rValue) {
        if(lValue.getCode() == rValue.getCode()) {
            System.out.println(lValue.getLexema() + " = " + rValue.getLexema());
        } else {
            String temp = newTemp();
            System.out.println(temp + " = " + "(float) " + rValue.getLexema());
            System.out.println(lValue.getLexema() + " = " + temp);
        }
    }

}