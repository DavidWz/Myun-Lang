package myun;

import myun.compiler.MyunCompiler;
import myun.AST.constraints.ViolatedConstraintException;
import myun.type.TypeMismatchException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ViolatedConstraintException, TypeMismatchException {
        String inputFile = args[0];
        MyunCompiler compiler = new MyunCompiler();
        compiler.compileFromFile(inputFile);
    }
}
