import java.lang.reflect.Array;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.ast.type.TypeParameter;

public class ScopeImpl implements Scope {
    JavaFileiClassLoader loader;
    ScopeImpl parent;
    Node node;
    iClassVirtual iclzv;
    VarBlock vars;

    public ScopeImpl(ScopeImpl parent, Node node, boolean new_var_block) {
        this.parent = parent;
        if (node == null)
            throw new UnsupportedOperationException();
        this.node = node;
        vars = new_var_block ? (parent == null ? new VarBlock(null, this) : parent.vars.getChild(this)) : parent.vars;
    }

    public JavaFileiClassLoader getClassLoader() {
        if (loader != null)
            return loader;
        if (parent == null)
            throw new RuntimeException("wtf?");
        return parent.getClassLoader();
    }

    public void setClz(iClassVirtual clz) {
        iclzv = clz;
    }

    public static Scope newRootScope(Node node) {
        if (node == null) {
            node = new EmptyStmt();
        }
        return new ScopeImpl(null, node, true);
    }

    @Override
    public VarBlock getVars() {
        return vars;
    }

    LinkedHashMap<Node, Scope> childs = new LinkedHashMap<>();

    public Scope getChild(Node node) {
        if (node == this.node) {
            return this;
        }
        if (node == null) {
            node = new EmptyStmt();
        }
        boolean new_var_block = node instanceof BlockStmt;
        // if (node instanceof Node)System.out.println(node.getClass().getSimpleName());
        // if (!this.node.isAncestorOf(node))throw new UnsupportedOperationException();
        return childs.computeIfAbsent(node, k -> new ScopeImpl(this, k, new_var_block));
    }

    public boolean hasParent() {
        return parent != null;
    }

    public Scope getParent() {
        return parent;
    }

    public iExecutor getExecutor() {
        return new iExecutorImpl(this);
    }

    static boolean debuglog = false;

    public void log(String format, Object... args) {
        if (debuglog) {
            /*
             * String p = " ".repeat(depth);
             * System.out.println(Arrays.stream(String.format(format,
             * args).split("\n")).map(s -> p + s) .collect(Collectors.joining("\n")));
             */
            System.out.printf(format + "\n", args);
            // System.out.println(String.format(format, args) + ": " + this);
        }
    }

    public String toString() {
        return String.format("Scope(%s)%s", node, vars);
    }

    public iExecutable findExecutable(String name, List<iClass> types) throws NoSuchMethodException {
        if (node instanceof ClassOrInterfaceDeclaration) {
            if (name != null && iclzv != null)
                try {
                    return iclzv.getMethod(name, types.toArray(iClass[]::new));
                } catch (NoSuchMethodException t) {
                    log("[err] %s", t);
                } catch (Throwable t) {
                    throw new RuntimeException(t);
                }
        }
        if (hasParent())
            return getParent().findExecutable(name, types);
        throw new NoSuchMethodException(name);
    }

    @FunctionalInterface
    public interface FindClassWTFZ {
        iClass apply(String name) throws Throwable;
    }

    iClass findClassWTF2(String name, FindClassWTFZ f) throws Throwable {
        Class<?> clz = Primitives.findClass(name);
        if (clz != null)
            return iClassWrapped.from(this, clz);
        if (name.endsWith("[]")) {
            clz = ((iClassWrapped) f.apply(name.substring(0, name.length() - 2))).x;
            clz = Array.newInstance(clz, 0).getClass();
            return new iClassArrayWrapped(this, iClassWrapped.from(this, clz));
        }
        if (name.endsWith(">")) {
            return f.apply(name.substring(0, name.lastIndexOf('<')));
        }
        throw new ClassNotFoundException(name);
    }

    iClass findClassWTF(String name, FindClassWTFZ f) throws Throwable {
        log("[findClassWTF] %s", name);
        iClass c = findClassWTF2(name, f);
        log("[findClassWTF2] %s=%s", name, f);
        return c;
    }

    iClass findClassImports(String name) throws Throwable {
        log("[findClassImports] %s", name);
        if (!(node instanceof CompilationUnit))
            return parent.findClassImports(name);
        try {
            return findClassWTF(name, this::findClassImports);
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        CompilationUnit cu = (CompilationUnit) node;
        iClass clz = cu.getImports().stream().map(i -> {
            if (i.isStatic())
                throw new UnsupportedOperationException();
            try {
                String n = i.getNameAsString();
                log("[import] (%s) name=%s", i.toString().trim(), name);
                if (i.isAsterisk()) {
                    //System.out.printf("(%s).startsWith(%s)=%s\n", name, i.getNameAsString(),name.startsWith(i.getNameAsString()));
                    //if (!name.startsWith(i.getNameAsString()))
                    if (cu.getImports().stream().allMatch(ii -> !name.startsWith(ii.getNameAsString())))
                        return findClassExt(n + "." + name);
                } else if (n.endsWith("." + name))
                    return findClassExt(n);
            } catch (ClassNotFoundException e) {
            } catch (Throwable t) {
                throw new RuntimeException(t);
            }
            return null;
        }).filter(c -> c != null).findFirst().orElseThrow(ClassNotFoundException::new);
        log("[findClassImports2] %s=%s", name, clz);
        return clz;
    }

    public iClass findClassExt(String name) throws ClassNotFoundException {
        try {
            return findClassWTF(name, this::findClassExt);
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        try {
            return findClassImports(name);
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        try {
            return getClassLoader().findClassExt(name);
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public iClass findClassLocal(String name) throws Throwable {
        try {
            return findClassWTF(name, this::findClassLocal);
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        log("[findClassLocal] %s %s", name, node.getClass().getSimpleName());
        if (node instanceof CompilationUnit || node instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration cid = node.stream()
                    .filter(n -> n instanceof ClassOrInterfaceDeclaration
                            && ((ClassOrInterfaceDeclaration) n).getNameAsString().equals(name))
                    .map(n -> (ClassOrInterfaceDeclaration) n).findAny().orElse(null);
            if (cid != null)
                return new iClassVirtual(getChild(cid), cid);
            //node.stream().filter(n -> n instanceof ClassOrInterfaceDeclaration).forEach(n -> System.out.println(n));
            //throw new UnsupportedOperationException();
        }
        //JavaFileiClassLoader loader = getClassLoader();
        if (node instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration d = ((ClassOrInterfaceDeclaration) node);
            Optional<TypeParameter> tp = d.getTypeParameters().stream().filter(t -> t.getNameAsString().equals(name))
                    .findFirst();
            if (tp.isPresent()) {
                NodeList<ClassOrInterfaceType> tb = tp.get().getTypeBound();
                if (tb.size() != 1)
                    throw new UnsupportedOperationException();
                ClassOrInterfaceType t = tb.get(0);
                return findClassLocal(t.getNameWithScope());
            }
        }
        if (hasParent())
            return getParent().findClass(name);
        throw new ClassNotFoundException(name);
    }

    public iClass findClass(Type name) throws ClassNotFoundException {
        return findClass(name.toString());
    }

    public iClass findClass(String name) throws ClassNotFoundException {
        log("[findClass] %s", name);
        try {
            return findClassWTF(name, this::findClass);
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        try {
            return findClassLocal(name);
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        try {
            return findClassExt(name);
        } catch (ClassNotFoundException e) {
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
        throw new ClassNotFoundException(name);
    }
}
