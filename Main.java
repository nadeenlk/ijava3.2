import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

public class Main {
    public void main2(String[] args) throws Throwable {
        if (args.length >= 1) {
            if (args.length >= 2) {
                if (args[0].equals("-d")) {
                    ScopeImpl.debuglog = true;
                    args = Arrays.copyOfRange(args, 1, args.length);
                }
            }
            if (args.length == 1 && args[0].equals("tests")) {
                for (Path p : Files.list(Path.of("tests")).toList()) {
                    String fn = p.getFileName().toString();
                    if (fn.endsWith(".java")) {
                        try {
                            if (fn == "args.java")
                                run_file(p, new String[] { "arg1", "arg2", "arg3" });
                            else
                                run_file(p, new String[0]);
                        } catch (Throwable t) {
                            new Throwable("error @" + p.toString(), t).printStackTrace();
                        }
                    }
                }
                return;
            }
            if (args.length >= 1) {
                if (args[0].endsWith(".java")) {
                    String[] args2 = Arrays.copyOfRange(args, 1, args.length);
                    run_file(Path.of(args[0]), args2);
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
        System.out.println(" [-d]                    - enable debug logging");
        System.out.println();
        System.out.println("eg: ijava tests\\ip.java - get your ip");
        System.out.println("Nadeen Udantha udanthan@gmail.com");
        System.out.println();
    }

    void run_file(Path path, String[] args) throws Throwable {
        String n = path.getFileName().toString();
        n = n.substring(0, n.length() - ".java".length());
        CompilationUnit cu = StaticJavaParser.parse(path);
        cu.getImports().addFirst(new ImportDeclaration("java.lang", false, true));
        Scope scope = ScopeImpl.newRootScope(cu);
        iObjectWrapped argz = new iObjectWrapped(scope, args);
        scope.findClass(n).getMethod("main", new iClassWrapped(scope, String[].class)).invoke(null, argz);
    }

    public static void main(String[] args) throws Throwable {
        new Main().main2(args);
    }
}
