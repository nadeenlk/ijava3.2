import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;

public class iClassLoader {
    static int id_counter = 0;
    int id;

    iClassLoader() {
        id = id_counter++;
    }

    iClass findClassImpl(String name) throws Throwable {
        // if (parent != null) return parent.findClass(name);
        throw new ClassNotFoundException(name.toString());
    }

    public void log(String format, Object... args) {
        if (ScopeImpl.debuglog)
            System.out.print(String.format("[ClassLoader:%s#%d] %s\n", toString(), id, String.format(format, args)));
    }

    final iClass findClass(String name) throws ClassNotFoundException {
        log("findClass %s", name);
        // new Throwable().printStackTrace();
        iClass clz;
        try {
            clz = findClassImpl(name);
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        log("findClass2 %s=%s", name, clz);
        return clz;
    }
}

class BaseClassLoader extends iClassLoader {
    BaseClassLoader() {
        super();
    }

    @Override
    public String toString() {
        return "Base";
    }
}

class JavaFileiClassLoader extends iClassLoader {
    MultiiClassLoader parent;
    Path p;
    CompilationUnit cu;
    ScopeImpl scope;

    JavaFileiClassLoader(MultiiClassLoader parent, Path p) throws Throwable {
        super();
        this.parent = parent;
        this.p = p;
        cu = StaticJavaParser.parse(p);
        cu.getImports().addFirst(new ImportDeclaration("java.lang", false, true));
        scope = (ScopeImpl) ScopeImpl.newRootScope(cu);
        scope.loader = this;
    }

    @Override
    iClass findClassImpl(String name) throws Throwable {
        return scope.findClassLocal(name);
    }

    final iClass findClassExt(String name) throws Throwable {
        log("findClassExt %s", name);
        iClass clz = parent.findClassExt(this, name);
        log("findClassExt2 %s=%s", name, clz);
        return clz;
    }

    @Override
    public String toString() {
        return "Java@" + p;
    }
}

class ClassFileiClassLoader extends iClassLoader {
    MultiiClassLoader parent;
    Path p;
    ClassFileClassLoader cl;

    ClassFileiClassLoader(MultiiClassLoader parent, Path p) throws Throwable {
        super();
        this.parent = parent;
        this.p = p;
        cl = new ClassFileClassLoader();
    }

    @Override
    iClass findClassImpl(String name) throws Throwable {
        return iClassWrapped.from(ScopeImpl.newRootScope(null), cl.loadClass(name));
    }

    @Override
    public String toString() {
        return "Class@" + p;
    }

    class ClassFileClassLoader extends ClassLoader {
        ClassFileClassLoader() throws Throwable {
            byte[] b = Files.readAllBytes(p);
            defineClass(null, b, 0, b.length, null);
        }
    }
}

class JarFileiClassLoader extends iClassLoader {
    MultiiClassLoader parent;
    Path p;
    URLClassLoader cl;

    JarFileiClassLoader(MultiiClassLoader parent, Path p) throws Throwable {
        super();
        this.parent = parent;
        this.p = p;
        cl = new URLClassLoader(new URL[] {p.toUri().toURL()});
    }

    @Override
    iClass findClassImpl(String name) throws Throwable {
        return iClassWrapped.from(ScopeImpl.newRootScope(null), cl.loadClass(name));
    }

    @Override
    public String toString() {
        return "Jar@" + p;
    }
}

class MultiiClassLoader extends iClassLoader {
    List<iClassLoader> xs;

    MultiiClassLoader() {
        super();
        // this.parent = parent;
        this.xs = new LinkedList<>();
    }

    MultiiClassLoader add(iClassLoader x) {
        if (ScopeImpl.debuglog)
            System.out.printf("%s add %s\n", this, x);
        xs.add(x);
        return this;
    }

    MultiiClassLoader add(Path p) throws Throwable {
        if (Files.isDirectory(p)) {
            Files.list(p).filter(z -> !Files.isDirectory(z)).forEach(f -> {
                String n = f.getFileName().toString();
                if (n.endsWith(".java") || n.endsWith(".class"))
                    try {
                        add(f);
                    } catch (Throwable t) {
                        throw new RuntimeException(t);
                    }
            });
            return this;
        }
        String n = p.getFileName().toString();
        if (n.endsWith(".java"))
            add(new JavaFileiClassLoader(this, p));
        else if (n.endsWith(".class"))
            add(new ClassFileiClassLoader(this, p));
        else if (n.endsWith(".jar"))
            add(new JarFileiClassLoader(this, p));
        else
            throw new UnsupportedOperationException(String.format("wtf is '%s'?", p));
        return this;
    }

    @Override
    iClass findClassImpl(String name) throws Throwable {
        for (iClassLoader x : xs) {
            try {
                return x.findClass(name);
            } catch (ClassNotFoundException e) {
            }
        }
        return super.findClassImpl(name);
    }

    iClass findClassExtImpl(iClassLoader z, String name) throws Throwable {
        for (iClassLoader x : xs) {
            if (x != z)
                try {
                    return x.findClass(name);
                } catch (ClassNotFoundException e) {
                }
        }
        return super.findClassImpl(name);
    }

    final iClass findClassExt(iClassLoader x, String name) throws Throwable {
        log("findClassExt %s", name);
        iClass clz = findClassExtImpl(x, name);
        log("findClassExt2 %s=%s", name, clz);
        return clz;
    }

    @Override
    public String toString() {
        return "Multi" + xs.toString();
    }
}

class ClassName {
    LinkedList<String> parts;

    ClassName(String name) {
        parts = new LinkedList<>(Arrays.asList(name.split("\\.")));
    }

    String name() {
        return String.join(".", parts);
    }

    @Override
    public String toString() {
        return "ClassName" + parts;
    }
}
