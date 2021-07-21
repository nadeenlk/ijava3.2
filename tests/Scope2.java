/*import java.util.LinkedHashMap;

import com.github.javaparser.ast.Node;

public class Scope {
    Scope parent;
    Node scope;
    LinkedHashMap<String, iObject> vars = new LinkedHashMap<>();

    public Scope(Scope p, Node s) {
        this.parent = p;
        this.scope = s;
    }

    public Scope child(Node s) {
        return new Scope(this, s);
    }

    public iClass findClass() {
        
    }

    /*public xObject resolveMethod(String n, List<iClass> args) throws Throwable {
        System.out.printf("[resolveMethod] %s(%s) %s\n", n,
                args.stream().map(iClass::getName).collect(Collectors.joining(",")), scope);
        if (scope instanceof ClassOrInterfaceDeclaration) {
            MethodDeclaration md = ((ClassOrInterfaceDeclaration) scope).getMethods().stream().filter(m -> {
                if (!m.getNameAsString().equals(n))
                    return false;
                // m.getParameters().stream().
                return true;
            }).findAny().orElse(null);
            if (md != null)
                return new xMethod(this, md);
        }
        if (parent != null)
            return parent.resolveMethod(n, args);
        throw new NoSuchMethodException(n);
    }

    public xObject resolveClass(String n) throws Throwable {
        System.out.printf("[resolveClass] %s %s\n", scope.getClass().getSimpleName(), n);
        if (scope instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration ct = ((ClassOrInterfaceDeclaration) scope).getMembers().stream()
                    .filter(x -> x.isClassOrInterfaceDeclaration()
                            && x.asClassOrInterfaceDeclaration().getNameAsString().equals(n))
                    .map(x -> x.asClassOrInterfaceDeclaration()).findAny().orElse(null);
            if (ct != null)
                return new xClass(this, ct);
        }
        if (parent != null)
            return parent.resolveClass(n);
        return new xObject(null, null, new iClass(Class.forName("java.lang." + n)), null);
    }

    public xVar declareVar(Type t, String n) {
        Class<?> c = t.toString().equals("int") ? Integer.class : null;
        xVar v = new xVar(n, null, new iClass(c), null);
        vars.put(n, v);
        return v;
    }

    public xVar getVar(String n) {
        xVar v = vars.get(n);
        System.out.printf("[var] get %s=%s\n", n, v);
        if (v == null && parent != null)
            v = parent.getVar(n);
        return v;
    }*
}*/