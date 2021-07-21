import java.nio.file.Path;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

public class Main {
    public void main() throws Throwable {
        String n = "test_ip";
        CompilationUnit cu = StaticJavaParser.parse(Path.of("tests/" + n + ".java"));
        cu.getImports().addFirst(new ImportDeclaration("java.lang", false, true));
        new Scope(null, cu).findClass(n).getMethod("main", new iClassWrapped(String[].class)).invoke(null,
                (iObject) null);
    }

    public static void main(String[] args) throws Throwable {
        new Main().main();
    }
}