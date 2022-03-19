import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.NodeList;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.stmt.BlockStmt;
import com.github.javaparser.ast.stmt.EmptyStmt;
import com.github.javaparser.ast.type.ClassOrInterfaceType;
import com.github.javaparser.ast.type.TypeParameter;

public class ScopeImpl implements Scope {
    Scope parent;
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
            // System.out.printf(format + "\n", args);
            System.out.println(String.format(format, args) + ": " + this);
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

    public Class<?> clzForName(String name) throws ClassNotFoundException {
        log("[class] forName %s", name);
        Class<?> clz = Class.forName(name);
        log("[class] forName %s=%s", name, clz);
        return clz;
    }

    public iClass findClass2(String name) throws Throwable {
        log("[findClass] %s %s", name, node.getClass().getSimpleName());
        {
            Class<?> clz = Primitives.findClass(name);
            if (clz != null)
                return new iClassWrapped(this, clz);
        }
        if (name.endsWith("[]")) {
            return new iClassArrayWrapped(this, (iClassWrapped) findClass(name.substring(0, name.length() - 2)));
        }
        if (name.endsWith(">")) {
            return findClass(name.substring(0, name.lastIndexOf('<')));
        }
        // if
        // (!Pattern.matches("([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*",
        // name))throw new ClassNotFoundException(name);
        if (node instanceof CompilationUnit || node instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration cid = node.stream()
                    .filter(n -> n instanceof ClassOrInterfaceDeclaration
                            && ((ClassOrInterfaceDeclaration) n).getNameAsString().equals(name))
                    .map(n -> (ClassOrInterfaceDeclaration) n).findAny().orElse(null);
            if (cid != null)
                return new iClassVirtual(getChild(cid), cid);
        }
        if (node instanceof CompilationUnit) {
            CompilationUnit cu = ((CompilationUnit) node);
            ImportDeclaration id = cu.getImports().stream().filter(i -> {
                if (i.isStatic())
                    throw new UnsupportedOperationException();
                try {
                    String n = i.getNameAsString();
                    log("[import] (%s) name=%s", i.toString().trim(), name);
                    if (i.isAsterisk())
                        clzForName(n + "." + name);
                    else if (n.endsWith("." + name))
                        clzForName(n);
                    else
                        return false;
                    return true;
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }).findFirst().orElse(null);
            if (id != null) {
                if (id.isAsterisk())
                    return new iClassWrapped(this, clzForName(id.getNameAsString() + "." + name));
                else
                    return new iClassWrapped(this, clzForName(id.getNameAsString()));
            }
        }
        if (node instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration d = ((ClassOrInterfaceDeclaration) node);
            Optional<TypeParameter> tp = d.getTypeParameters().stream().filter(t -> t.getNameAsString().equals(name))
                    .findFirst();
            if (tp.isPresent()) {
                NodeList<ClassOrInterfaceType> tb = tp.get().getTypeBound();
                if (tb.size() != 1)
                    throw new UnsupportedOperationException();
                ClassOrInterfaceType t = tb.get(0);
                return findClass(t.getNameWithScope());
            }
        }
        if (hasParent())
            return getParent().findClass(name);
        return new iClassWrapped(this, clzForName(name));
    }

    public iClass findClass(String name) throws ClassNotFoundException {
        try {
            return findClass2(name);
        } catch (ClassNotFoundException e) {
            throw e;
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }
}
