import java.nio.file.Files;
import java.nio.file.Path;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

public class Main {
    public void main2(String[] args) throws Throwable {
        if (args.length == 1 || args.length == 2) {
            if (args.length == 2) {
                if (args[0].equals("-d")) {
                    Scope.debuglog = true;
                    args = new String[] { args[1] };
                }
            }
            if (args.length == 1) {
                if (args[0].equals("tests")) {
                    for (Path p : Files.list(Path.of("tests")).toList()) {
                        String fn = p.getFileName().toString();
                        if (fn.startsWith("test_") && fn.endsWith(".java"))
                            run_file(p);
                    }
                    return;
                }
                if (args[0].endsWith(".java")) {
                    run_file(Path.of(args[0]));
                    return;
                }
            }
        }
        System.out.println();
        System.out.println("ijava: Java source code interpreter");
        System.out.println();
        System.out.println(" ijava                   - help");
        System.out.println(" ijava [-d] <file path>  - run java source file");
        System.out.println(" ijava [-d] tests        - run tests");
        System.out.println();
        System.out.println("flags:");
        System.out.println(" [-d]               - enable debug logging");
        System.out.println();
        System.out.println("eg: ijava tests\\test_ip.java - get your ip");
        System.out.println("Nadeen Udantha udanthan@gmail.com");
        System.out.println();
    }

    void run_file(Path path) throws Throwable {
        String n = path.getFileName().toString();
        n = n.substring(0, n.length() - ".java".length());
        CompilationUnit cu = StaticJavaParser.parse(path);
        cu.getImports().addFirst(new ImportDeclaration("java.lang", false, true));
        new Scope(null, cu).findClass(n).getMethod("main", new iClassWrapped(String[].class)).invoke(null,
                (iObject) null);
    }

    public static void main(String[] args) throws Throwable {
        new Main().main2(args);
    }
}