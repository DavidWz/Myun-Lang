package myun;

import myun.AST.ASTCompileUnit;
import myun.AST.ASTGenerator;
import myun.AST.MyunPrettyPrinter;
import myun.AST.constraints.ViolatedConstraintException;
import myun.scope.PredefinedScope;
import myun.scope.Scope;
import myun.scope.ScopeInitializer;
import myun.type.TypeInferrer;
import myun.type.TypeMismatchException;

import java.io.IOException;

public class Main {

    public static void main(String[] args) throws IOException, ViolatedConstraintException, TypeMismatchException {
        String inputFile = args[0];
        ASTGenerator astGen = new ASTGenerator();
        ASTCompileUnit program = astGen.parseFile(inputFile);

        Scope coreLang = PredefinedScope.getPredefinedScope();

        ScopeInitializer scopeInitializer = new ScopeInitializer();
        scopeInitializer.initScope(program, coreLang);

        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        MyunPrettyPrinter prettyPrinter = new MyunPrettyPrinter();
        System.out.println(prettyPrinter.toString(program));
    }
}
