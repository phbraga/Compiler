package compilador.util;

import compilador.scanner.Token;
import java.util.ArrayList;

public class Utils {
    
    public static void showErrorMessage ( Token token, int lin, int col, String message ) {
        if (token != null && token.getCode() == -1)
            System.out.println("ERRO na linha " + lin +  ", coluna " + col + ", nenhum token foi formado: " +  message);
        else {          
            if(token == null)
                System.out.println("ERRO na linha " + lin +  ", coluna " + col + ", final de arquivo: " +  message);
            else 
                System.out.println("ERRO na linha " + lin +  ", coluna " + col + ", ultimo token lido " + token + ": " +  message);
        }
        System.exit(lin);
    }
    
    public static ArrayList<String> createListAllOperators() {
        ArrayList<String> listAllOperators = new ArrayList<>();
        
        listAllOperators.add("+");
        listAllOperators.add("-");
        listAllOperators.add("*");
        listAllOperators.add("/");
        listAllOperators.add("==");
        listAllOperators.add("!=");
        listAllOperators.add("<");
        listAllOperators.add(">");
        listAllOperators.add(">=");
        listAllOperators.add("<=");
        
        return listAllOperators;
    }
    
    public static ArrayList<Integer> createListRelationalOpTypes() {
        ArrayList<Integer> listRelationalOpTypes = new ArrayList<>();
        
        listRelationalOpTypes.add(Token.IGUAL);
        listRelationalOpTypes.add(Token.DIFERENTE);
        listRelationalOpTypes.add(Token.MAIOR);
        listRelationalOpTypes.add(Token.MAIOR_IGUAL);
        listRelationalOpTypes.add(Token.MENOR);
        listRelationalOpTypes.add(Token.MENOR_IGUAL);

        return listRelationalOpTypes;
    }
    
    public static ArrayList<Integer> createListFirstType() {
        ArrayList<Integer> firstType = new ArrayList<>();
        
        firstType.add(Token.TIPO_CHAR);
        firstType.add(Token.TIPO_FLOAT);
        firstType.add(Token.TIPO_INT);
        
        return firstType;
    }
    
    public static ArrayList<Integer> createListFirstDeclaration() {
        ArrayList<Integer> firstDeclaration = new ArrayList<>();
        
        firstDeclaration.add(Token.CHAR);
        firstDeclaration.add(Token.FLOAT);
        firstDeclaration.add(Token.INT);
        
        return firstDeclaration;
    }
    
    public static ArrayList<Integer> createListFirstCommand() {
        ArrayList<Integer> firstCommand = new ArrayList<>();
        
        firstCommand.add(Token.IF);
        firstCommand.addAll(createListFirstBasicCommand());
        firstCommand.addAll(createListFirstIteration());
        
        return firstCommand;
    }
    
    public static ArrayList<Integer> createListFirstBasicCommand() {
        ArrayList<Integer> firstBasicCommand = new ArrayList<>();
        
        firstBasicCommand.addAll(createListFirstBlock());
        firstBasicCommand.addAll(createListFirstAssing());
        
        return firstBasicCommand;
    }
    
    public static ArrayList<Integer> createListFirstIteration() {
        ArrayList<Integer> firstIteration = new ArrayList<>();
        
        firstIteration.add(Token.DO);
        firstIteration.add(Token.WHILE);
        
        return firstIteration;
    }
    
    public static ArrayList<Integer> createListFirstBlock() {
        ArrayList<Integer> firstBlock = new ArrayList<>();
        
        firstBlock.add(Token.ABRE_CHAVES);
        
        return firstBlock;
    }

    public static ArrayList<Integer> createListFirstAssing() {
        ArrayList<Integer> firstAsing = new ArrayList<>();
        
        firstAsing.add(Token.IDENTIFICADOR);
        
        return firstAsing;
    }
    
}
