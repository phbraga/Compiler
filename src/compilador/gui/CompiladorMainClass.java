package compilador.gui;

import compilador.scanner.Scanner;
import java.io.FileNotFoundException;
import java.io.FileReader;
import compilador.parser.Parser;
import compilador.scanner.Token;


public class CompiladorMainClass {

    public static void main(String[] args) {
        /**
         Example: java -jar "C:\Users\Pedro\Dropbox\Faculdade\5° Periodo\Compiladores\Compilador_PHMB\dist\Compilador_PHMB.jar" "C:\Users\Pedro\Dropbox\Faculdade\5° Periodo\Compiladores\Compilador_PHMB\teste.c"
         */
        try {
            Scanner scanner = Scanner.getInstance();
            FileReader fileReader = new FileReader(args[0]);
            scanner.setFileReader(fileReader);
            
            Parser parser = Parser.getInstance();
            parser.init();
            
        } catch (FileNotFoundException ex) {
            System.out.println("Arquivo '" + args[0] + "' não foi encontrado \n\n" );
        } 
        System.out.println("Passou!");
    }
}
