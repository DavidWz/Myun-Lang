package myun.compiler;

import myun.AST.*;
import myun.scope.MyunCoreScope;
import myun.scope.ScopeInitializer;
import myun.type.inference.TypeInferrer;

import java.io.*;
import java.util.stream.Collectors;

/**
 * Compiles a myun source file to LLVM code.
 * @noinspection UseOfSystemOutOrSystemErr
 */
final public class MyunCompiler {
    private static final String DEFAULT_LLVM_COMPILER = "llc";
    private static final String DEFAULT_ASSEMBLY_COMPILER = "gcc";
    private static final int DEFAULT_OPT_LEVEL = 0;

    private final String llvmCompiler;
    private final String assemblyCompiler;
    private final int optimizationLevel;
    private final boolean debug;

    private MyunCompiler(String llvmCompiler, String assemblyCompiler, int optimizationLevel, boolean debug) {
        this.llvmCompiler = llvmCompiler;
        this.assemblyCompiler = assemblyCompiler;
        this.optimizationLevel = optimizationLevel;
        this.debug = debug;
    }

    public static MyunCompiler getDefaultMyunCompiler() {
        return new MyunCompiler(DEFAULT_LLVM_COMPILER, DEFAULT_ASSEMBLY_COMPILER, DEFAULT_OPT_LEVEL, false);
    }

    public static void main(String... args) throws IOException, InterruptedException {
        if (args.length == 0) {
            System.out.println("Missing input file.");
            System.exit(1);
        }
        String inputFile = args[0];

        MyunCompiler compiler;
        if (args.length >= 4) {
            String llvmCompiler = args[1];
            String assemblyCompiler = args[2];
            int optimizationLevel = Integer.parseInt(args[3]);
            boolean debug = false;
            if ((args.length == 5) && "-debug".equals(args[4])) {
                debug = true;
            }
            compiler = new MyunCompiler(llvmCompiler, assemblyCompiler, optimizationLevel, debug);
        }
        else {
            compiler = getDefaultMyunCompiler();
        }

        compiler.compileFromFile(inputFile);
    }

    /**
     * Compiles myun code from a file and writes the resulting llvm ir code to an output file.
     *
     * @param inputFile the path to the input file
     * @return the output file path
     * @throws IOException thrown when the file could not be loaded or written to
     */
    public String compileFromFile(String inputFile) throws IOException, InterruptedException {
        // generate the AST
        ASTGenerator astGen = new ASTGenerator();
        ASTCompileUnit program = astGen.parseFile(inputFile);
        String fileName;
        if (inputFile.endsWith(".myun")) {
            fileName = inputFile.substring(0, inputFile.length() - 1 - "myun".length());
        } else {
            fileName = inputFile;
        }

        // init the scopes
        ScopeInitializer.initScopes(program, MyunCoreScope.getInstance());

        // infer the types
        TypeInferrer typeInferrer = new TypeInferrer();
        typeInferrer.inferTypes(program);

        // print the source code
        if (debug) {
            MyunPrettyPrinter prettyPrinter = new MyunPrettyPrinter();
            System.out.println(prettyPrinter.toString(program));
        }

        // compile the code
        MyunToLLVMTranslator llvmTranslator = new MyunToLLVMTranslator();
        String llvmCode = llvmTranslator.translateToLLVM(program);
        if (debug) {
            System.out.println("### Compiled to: ");
            System.out.println(llvmCode);
        }

        // write it to the output file
        String outputFile = fileName+".ll";
        Writer writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), "utf-8"));
        writer.write(llvmCode);
        writer.close();

        // compile the llvm code
        runCompileChain(fileName);
        return fileName+".out";
    }

    private void runCompileChain(String fileName) throws IOException, InterruptedException {
        Runtime rt = Runtime.getRuntime();
        String[] execCodes = {llvmCompiler+" -O"+optimizationLevel+ ' ' +fileName+".ll",
                assemblyCompiler+" -c "+fileName+".s -o "+fileName+".o",
                assemblyCompiler+ ' ' +fileName+".o -o "+fileName+".out"};
        for (String execCode : execCodes) {
            Process compileProcess = rt.exec(execCode);
            int compileRet = compileProcess.waitFor();
            if (compileRet != 0) {
                String error = new BufferedReader(new InputStreamReader(compileProcess.getErrorStream()))
                        .lines().collect(Collectors.joining("\n"));
                throw new UnsuccessfulCompilationException(error);
            }
        }
    }
}
