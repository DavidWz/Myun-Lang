package myun;

import myun.compiler.MyunCompiler;
import java.io.IOException;

final class Main {
    private Main() {
        super();
    }

    public static void main(String[] args) throws IOException {
        String inputFile = args[0];
        MyunCompiler compiler = new MyunCompiler();

        compiler.compileFromFile(inputFile);
    }
}
