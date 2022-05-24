import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;

public class Main {
    iClassLoader main_loader;
    String main_name;
    String[] main_args;

    public void main2(String[] args) throws Throwable {
        LinkedList<String> argz = new LinkedList<>(Arrays.asList(args));
        if (argz.isEmpty())
            help();
        if ("-d".equals(argz.peekFirst())) {
            argz.removeFirst();
            ScopeImpl.debuglog = true;
        }
        if (argz.size() == 1 && "tests".equals(argz.getFirst())) {
            Path tests = Path.of("tests");
            main_loader = new MultiiClassLoader().add(new BuiltinClassLoader()).add(tests);
            for (Path p : Files.list(tests).filter(x -> !Files.isDirectory(x)).toList()) {
                String fn = p.getFileName().toString();
                main_name = "tests." + fn.substring(0, fn.length() - ".java".length());
                if (main_name == "args")
                    main_args = new String[] { "arg1", "arg2", "arg3" };
                else
                    main_args = new String[0];
                try {
                    run();
                } catch (Throwable t) {
                    new Throwable("error @" + p.toString(), t).printStackTrace();
                }
            }
            System.exit(0);
        }
        if (argz.size() < 2)
            help();
        MultiiClassLoader loader = new MultiiClassLoader();
        loader.add(new BuiltinClassLoader());
        for (String s : argz.removeFirst().split(";")) {
            loader.add(Path.of(s).normalize());
        }
        main_loader = loader;
        main_name = argz.removeFirst();
        main_args = argz.toArray(String[]::new);
        run();
    }

    void help() {
        System.out.println();
        System.out.println("ijava: Java source code interpreter");
        System.out.println();
        System.out.println(" ijava [-d] <classpath> <mainclass> [args...]");
        System.out.println("or");
        System.out.println(" ijava [-d] tests                       - run tests");
        System.out.println();
        System.out.println(" -d                                     - enable debug logging");
        System.out.println(" <classpath>                            - class search path of directories/jar files/java source files");
        System.out.println(" <main class>                           - main class name");
        System.out.println();
        System.out.println("eg:");
        System.out.println(" ijava tests ip                         - get your ip");
        System.out.println(" ijava tests hello");
        System.out.println(" ijava tests\\hello.java hello");
        System.out.println(" ijava tests\\hello.class tests.hello");
        System.out.println(" ijava tests\\hello.jar tests.hello");
        System.out.println();
        System.out.println("Nadeen Udantha me@nadeen.lk");
        System.out.println();
        System.exit(0);
    }

    void run() throws Throwable {
        if (ScopeImpl.debuglog)
            System.out.printf("running %s(%s) %s\n", main_name, Arrays.toString(main_args), main_loader);
        /*Scope scope = ScopeImpl.newRootScope(null);
        iClass main_clz = main_loader.findClass(main_name);
        iObjectWrapped argz = new iObjectWrapped(scope, main_args);
        main_clz.getMethod("main", argz.getClazz()).invoke(null, argz);*/
        /*iClass main_clz = main_loader.findClass(main_name);
        String argz = Arrays.stream(main_args).map(x -> "\"" + x + "\"").collect(Collectors.joining(","));
        String exp = String.format("%s.main(new java.lang.String[]{%s})", main_name, argz);
        main_clz.getScope().getExecutor().exec(StaticJavaParser.parseExpression(exp));*/
        iClass main_clz = main_loader.findClass(main_name);
        Scope scope = main_clz.getScope();
        main_clz.getMethod("main", iClassWrapped.from(scope, String[].class)).invoke(new iObjectWrapped(scope, null),
                new iObjectWrapped(scope, main_args));
    }

    public static void main(String[] args) throws Throwable {
        //System.out.printf("Main.main(%s)\n", Arrays.toString(args));
        new Main().main2(args);
    }
}
