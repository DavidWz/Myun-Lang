package myun;

import myun.AST.ASTCompileUnit;
import myun.AST.ASTGenerator;
import myun.AST.MyunPrettyPrinter;
import myun.AST.constraints.ViolatedConstraintException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ViolatedConstraintException {
        String inputFile = args[0];
        ASTGenerator astGen = new ASTGenerator();
        ASTCompileUnit program = astGen.parseFile(inputFile);

        MyunPrettyPrinter prettyPrinter = new MyunPrettyPrinter();
        System.out.println(prettyPrinter.toString(program));
    }
}
