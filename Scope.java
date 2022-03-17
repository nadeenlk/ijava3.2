import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.body.VariableDeclarator;
import com.github.javaparser.ast.expr.ArrayAccessExpr;
import com.github.javaparser.ast.expr.ArrayCreationExpr;
import com.github.javaparser.ast.expr.ArrayInitializerExpr;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.BooleanLiteralExpr;
import com.github.javaparser.ast.expr.CastExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.ForEachStmt;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.IfStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.utils.StringEscapeUtils;

public class Scope {
    Scope parent;
    iObject obj;
    Node node;
    LinkedHashMap<String, iObjectVariable> vars = new LinkedHashMap<>();

    public Scope(Scope parent, Node node) {
        this.parent = parent;
        this.node = node;
    }

    static boolean debuglog = false;

    public void log(String format, Object... args) {
        if (debuglog)
            System.out.printf(format, args);
    }

    public iObject exec(MethodDeclaration md, iObject instance, iObject... args) throws Throwable {
        int i = 0;
        for (Parameter p : md.getParameters())
            declareVar(findClass(p.getType().toString()), p.getNameAsString()).set(args[i++]);
        return exec2(md.getBody().get());
    }

    public iObject exec(ConstructorDeclaration cd, iObjectVirtual instance, iObject... args) throws Throwable {
        return instance.scope.exec(cd.getBody());
    }

    public iObject exec(Statement x) throws Throwable {
        if (x.isBlockStmt()) {
            Scope ss = newChild(x);
            for (Statement s : x.asBlockStmt().getStatements()) {
                iObject v = ss.exec2(s);
                if (v instanceof iObjectReturn)
                    return ((iObjectReturn) v).value;
                if (v instanceof iObjectBreak)
                    return v;
            }
            return null;
        } else if (x.isExpressionStmt()) {
            return exec2(x.asExpressionStmt().getExpression());
        } else if (x.isForStmt()) {
            ForStmt xx = x.asForStmt();
            Scope ss = newChild(x);
            xx.getInitialization().forEach(y -> ss.exec2(y));
            Expression c = xx.getCompare().get();
            forloop: while ((boolean) ss.exec2(c).asWrapped().x) {
                if (ss.exec2(xx.getBody()) instanceof iObjectBreak)
                    break forloop;
                xx.getUpdate().forEach(y -> ss.exec2(y));
            }
            return null;
        } else if (x.isForEachStmt()) {
            ForEachStmt xx = x.asForEachStmt();
            Scope ss = newChild(x);
            if (xx.getVariable().getVariables().size() != 1)
                throw new UnsupportedOperationException();
            ss.exec2(xx.getVariable());
            VariableDeclarator ivd = xx.getVariable().getVariable(0);
            iObjectVariable iv = ss.getVar(ivd.getName().toString());
            Object iz = exec2(xx.getIterable()).asWrapped().x;
            Iterable ii;
            if (iz.getClass().isArray())
                ii = new IterableArray(iz);
            else
                ii = (Iterable) iz;
            forloop: for (Object z : ii) {
                iv.set(new iObjectWrapped(z));
                if (ss.exec2(xx.getBody()) instanceof iObjectBreak)
                    break forloop;
            }
            return null;

        } else if (x.isReturnStmt()) {
            return new iObjectReturn(x.asReturnStmt().getExpression().map(e -> exec2(e)).orElse(null));
        } else if (x.isIfStmt()) {
            IfStmt xx = x.asIfStmt();
            if ((boolean) exec2(xx.getCondition()).asWrapped().x)
                return exec2(xx.getThenStmt());
            else
                return xx.getElseStmt().map(e -> exec2(e)).orElse(null);
        } else if (x.isBreakStmt()) {
            return new iObjectBreak();
        } else {
            throw new Throwable(String.format("unhandled statement %s %s", x.getClass().getSimpleName(), x));
        }
    }

    public iObject exec(Expression x) throws Throwable {
        if (x.isMethodCallExpr()) {
            MethodCallExpr xx = x.asMethodCallExpr();
            List<iObject> args = xx.getArguments().stream().map(arg -> exec2(arg)).toList();
            Optional<Expression> ss = xx.getScope();
            if (ss.isEmpty()) {
                iMethod m = findExecutable(xx.getNameAsString(),
                        Arrays.asList(args.stream().map(y -> y.getClazz()).toArray(iClass[]::new))).asMethod();
                return executeExecutable(m, new iObjectWrapped(null), args.toArray(iObject[]::new));
            }
            iObject i = exec2(ss.get());
            iMethod m = getExecutable(i.getClazz(), xx.getNameAsString(),
                    args.stream().map(arg -> arg.getClazz()).toArray(iClass[]::new)).asMethod();
            iObject ii = i.asExecArg();
            // System.out.printf("\n\n\n[boi] i=%s ii=%s\n\n\n\n", i, ii);
            return executeExecutable(m, ii, args.toArray(iObject[]::new));
        } else if (x.isStringLiteralExpr()) {
            return new iObjectWrapped(StringEscapeUtils.unescapeJava(x.asStringLiteralExpr().getValue()));
        } else if (x.isFieldAccessExpr()) {
            FieldAccessExpr xx = x.asFieldAccessExpr();
            iObject obj = exec2(xx.getScope());
            iClass c = obj.getClazz();
            if (obj instanceof iClass)
                obj = new iObjectWrapped(null);
            return new iFieldObject(obj, c.getField(xx.getNameAsString()));
        } else if (x.isNameExpr()) {
            String xx = x.asNameExpr().getNameAsString();
            iObjectVariable v = getVar(xx);
            if (v != null)
                return v;
            else
                return findClass(xx);
        } else if (x.isObjectCreationExpr()) {
            ObjectCreationExpr xx = x.asObjectCreationExpr();
            if (!xx.getScope().isEmpty())
                throw null;
            List<iObject> args = xx.getArguments().stream().map(arg -> exec2(arg)).toList();
            return getExecutable(exec2(xx.getType().getNameAsExpression()).getClazz(), null,
                    args.stream().map(arg -> arg.getClazz()).toArray(iClass[]::new)).asConstructor()
                            .newInstance(args.stream().map(arg -> arg.asExecArg()).toArray(iObject[]::new));
        } else if (x.isVariableDeclarationExpr()) {
            x.asVariableDeclarationExpr().getVariables().forEach(y -> {
                iObjectVariable v = declareVar(findClass2(y.getType().toString()), y.getNameAsString());
                Optional<Expression> yy = y.getInitializer();
                if (!yy.isEmpty())
                    v.set(exec2(yy.get()));
            });
            return null;
        } else if (x.isIntegerLiteralExpr()) {
            return new iObjectWrapped(x.asIntegerLiteralExpr().asNumber().intValue());
        } else if (x.isLongLiteralExpr()) {
            return new iObjectWrapped(x.asLongLiteralExpr().asNumber().longValue());
        } else if (x.isAssignExpr()) {
            AssignExpr xx = x.asAssignExpr();
            if (xx.getOperator() != AssignExpr.Operator.ASSIGN)
                throw new UnsupportedOperationException();
            iObject v = exec2(xx.getValue());
            iObject z = exec2(xx.getTarget());
            if (z instanceof iFieldObject) {
                ((iFieldObject) z).set(v);
                return v;
            }
            return z.asVariable().set(v);/// return ?????????????????
        } else if (x.isBinaryExpr()) {
            BinaryExpr xx = x.asBinaryExpr();
            iObject l = exec2(xx.getLeft());
            iObject r = exec2(xx.getRight());
            return new iObjectWrapped(binary_unary_exec.binary_exec(this, xx.getOperator(), l, r));
        } else if (x.isUnaryExpr()) {
            UnaryExpr xx = x.asUnaryExpr();
            return new iObjectWrapped(binary_unary_exec.unary_exec(this, xx.getOperator(), exec2(xx.getExpression())));
        } else if (x.isEnclosedExpr()) {
            Expression xx = x.asEnclosedExpr().getInner();
            return newChild(xx).exec2(xx);
        } else if (x.isCastExpr()) {
            CastExpr xx = x.asCastExpr();
            return findClass(xx.getType().toString()).cast(exec2(xx.getExpression()));
        } else if (x.isNullLiteralExpr()) {
            return iNull.Null;
        } else if (x.isArrayCreationExpr()) {
            ArrayCreationExpr xx = x.asArrayCreationExpr();
            iClass clz = findClass(xx.getElementType().toString());
            Optional<ArrayInitializerExpr> oi = xx.getInitializer();
            iObject a;
            if (oi.isPresent()) {
                ArrayInitializerExpr i = oi.get();
                List<iObject> vs = i.getValues().stream().map(v -> exec2(v)).toList();
                a = clz.newArray(new int[] { vs.size() });
                for (int j = 0; j < vs.size(); j++) {
                    clz.setItem(a, j, vs.get(j));
                }
            } else {
                int[] ds = xx.getLevels().stream().mapToInt(l -> {
                    return (int) exec2(l.getDimension().get()).asWrapped().x;
                }).toArray();
                a = clz.newArray(ds);
            }
            return a;
        } else if (x.isArrayAccessExpr()) {
            ArrayAccessExpr xx = x.asArrayAccessExpr();
            iObject a = exec2(xx.getName());
            int i = (int) exec2(xx.getIndex()).asWrapped().x;
            return new iFieldObject(a, new iFieldArrayElement(a.getClazz(), i));
        } else if (x.isBooleanLiteralExpr()) {
            BooleanLiteralExpr xx = x.asBooleanLiteralExpr();
            return new iObjectWrapped(xx.getValue());
        } else {
            throw new Throwable(String.format("unhandled expression %s %s", x.getClass().getSimpleName(), x));
        }
    }

    public iObjectVariable declareVar(iClass type, String name) {
        iObjectVariable obj = new iObjectVariable(this, type, name);
        vars.put(name, obj);
        return obj;
    }

    public iObjectVariable getVar(String n) {
        iObjectVariable v = vars.get(n);
        log("[scope] getVar %s=%s\n", n, v);
        if (v == null && parent != null)
            v = parent.getVar(n);
        return v;
    }

    public iObject executeExecutable(iExecutable e, iObject obj, iObject[] args) throws Throwable {
        if (e.isVarArgs()) {
            if (e instanceof iConstructorWrapped || e instanceof iMethodWrapped) {
                int i = e.getParameterCount();
                iObject[] args2 = Arrays.copyOf(args, i);
                Object[] args3 = Stream.of(Arrays.copyOfRange(args, i - 1, args.length - i + 2))
                        .map(p -> p.asWrapped().x).toArray(Object[]::new);
                args2[i - 1] = new iObjectWrapped(args3);
                args = args2;
            } else
                throw new UnsupportedOperationException();
        }
        log("[executeExecutable] %s.%s(%s)\n", e, obj, Arrays.toString(args));
        iObject v = obj == null ? e.asConstructor().newInstance(args) : e.asMethod().invoke(obj, args);
        log("[executeExecutable2] %s.%s(%s)=%s\n", e, obj, Arrays.toString(args), v);
        return v;
    }

    public iExecutable getExecutable(iClass c, String n, iClass[] cx) throws Throwable {
        log("[findExecutable] %s.%s(%s)\n", c, n, Arrays.toString(cx));
        boolean isMethod = n != null;
        try {
            if (isMethod)
                return c.getMethod(n, cx);
            else
                return c.getConstructor(cx);
        } catch (NoSuchMethodException e) {
            log("[err] %s", e);
        }
        for (iExecutable e : (isMethod ? c.getMethods() : c.getConstructors())) {
            if (!isMethod || e.asMethod().getName().equals(n)) {
                if (getExecutableMatches(e.isVarArgs(), e.getParameterTypes(), cx))
                    return e;
            }
        }
        throw new NoSuchMethodException(n);
    }

    public boolean isAssignableFrom(iClass dst, iClass src) {
        return Primitives.isAssignable(dst, src) || dst.isAssignableFrom(src);
    }

    public boolean getExecutableMatches(boolean isVarArgs, iClass[] dst, iClass[] src) {
        log("[findExecutableMatches] isVarArgs=%b (%s)<--(%s)\n", isVarArgs, Arrays.toString(dst),
                Arrays.toString(src));
        if (isVarArgs) {
            // if (src.length < dst.length - 1)return false;
            for (int i = 0; i < dst.length - 1; i++) {
                if (!isAssignableFrom(dst[i], src[i]))
                    return false;
            }
            iClass t = dst[dst.length - 1].getComponentType();
            for (int i = dst.length; i < src.length; i++) {
                if (!isAssignableFrom(t, src[i]))
                    return false;
            }
            return true;
        } else {
            if (src.length != dst.length)
                return false;
            for (int i = 0; i < src.length; i++)
                if (!isAssignableFrom(dst[i], src[i]))
                    return false;
            return true;
        }
    }

    public iExecutable findExecutable(String name, List<iClass> types) throws Throwable {
        if (node instanceof ClassOrInterfaceDeclaration) {
            if (name != null)
                try {
                    return obj.asClass().getMethod(name, types.toArray(iClass[]::new));
                } catch (NoSuchMethodException e) {
                    log("[err] %s", e);
                }
        }
        if (parent != null)
            return parent.findExecutable(name, types);
        throw new NoSuchMethodException(name);
    }

    public iClass findClass(String name) throws Throwable {
        log("[findClass] %s %s\n", name, node.getClass().getSimpleName());
        {
            Class<?> clz = Primitives.findClass(name);
            if (clz != null)
                return new iClassWrapped(clz);
        }
        if (name.endsWith("[]")) {
            return new iClassArrayWrapped((iClassWrapped) findClass(name.substring(0, name.length() - 2)));
        }
        if (node instanceof CompilationUnit || node instanceof ClassOrInterfaceDeclaration) {
            ClassOrInterfaceDeclaration cid = node.stream()
                    .filter(n -> n instanceof ClassOrInterfaceDeclaration
                            && ((ClassOrInterfaceDeclaration) n).getNameAsString().equals(name))
                    .map(n -> (ClassOrInterfaceDeclaration) n).findAny().orElse(null);
            if (cid != null)
                return new iClassVirtual(newChild(cid), cid);
        }
        if (node instanceof CompilationUnit) {
            CompilationUnit cu = ((CompilationUnit) node);
            ImportDeclaration id = cu.getImports().stream().filter(i -> {
                if (i.isStatic())
                    throw new UnsupportedOperationException();
                try {
                    String n = i.getNameAsString();
                    log("[import] (%s) name=%s\n", i.toString().trim(), name);
                    if (i.isAsterisk())
                        forName(n + "." + name);
                    else if (n.endsWith("." + name))
                        forName(n);
                    else
                        return false;
                    return true;
                } catch (ClassNotFoundException e) {
                    return false;
                }
            }).findFirst().orElse(null);
            if (id != null) {
                if (id.isAsterisk())
                    return new iClassWrapped(forName(id.getNameAsString() + "." + name));
                else
                    return new iClassWrapped(forName(id.getNameAsString()));
            }
        }
        if (parent != null)
            return parent.findClass(name);
        return new iClassWrapped(forName(name));
    }

    public Class<?> forName(String name) throws ClassNotFoundException {
        log("[class] forName %s\n", name);
        Class<?> clz = Class.forName(name);
        log("[class] forName %s=%s\n", name, clz);
        return clz;
    }

    public Scope newChild(Node node) {
        // if (!this.node.isAncestorOf(node))throw new UnsupportedOperationException();
        return new Scope(this, node);
    }

    public iClass findClass2(String name) {
        try {
            return findClass(name);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public RuntimeException throw_err(Object x, Throwable t) {
        return new RuntimeException("error while running " + x.getClass().getSimpleName() + " " + x, t);
    }

    public iObject exec2(Expression x) {
        try {
            log("[exec] %s(%s)\n", x.getClass().getSimpleName(), x);
            iObject v = exec(x);
            log("[exec2] %s(%s)=%s\n", x.getClass().getSimpleName(), x, v);
            return v;
        } catch (Throwable t) {
            throw throw_err(x, t);
        }
    }

    public iObject exec2(Statement x) {
        try {
            log("[exec] %s(%s)\n", x.getClass().getSimpleName(), x);
            iObject v = exec(x);
            log("[exec2] %s(%s)=%s\n", x.getClass().getSimpleName(), x, v);
            return v;
        } catch (Throwable t) {
            throw throw_err(x, t);
        }
    }
}
