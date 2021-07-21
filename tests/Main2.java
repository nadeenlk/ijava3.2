/*import java.lang.reflect.Constructor;
import java.lang.reflect.Executable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.github.javaparser.StaticJavaParser;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.Node;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.body.Parameter;
import com.github.javaparser.ast.expr.AssignExpr;
import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.expr.FieldAccessExpr;
import com.github.javaparser.ast.expr.MethodCallExpr;
import com.github.javaparser.ast.expr.ObjectCreationExpr;
import com.github.javaparser.ast.expr.UnaryExpr;
import com.github.javaparser.ast.stmt.ForStmt;
import com.github.javaparser.ast.stmt.Statement;
import com.github.javaparser.ast.type.Type;
import com.github.javaparser.utils.StringEscapeUtils;

public class Main2 {
    public void main() throws Throwable {
        CompilationUnit ast = StaticJavaParser.parse(Path.of("test_method.java"));
        // System.out.println(ast.getImports().);
        ClassOrInterfaceDeclaration mainclz = ast.getType(0).asClassOrInterfaceDeclaration();
        xScope s = new xScope(null, mainclz);
        new xMethod(s, mainclz.getMethodsByName("main").get(0)).invoke("main",
                Arrays.asList(new xObject(null, null, new iClass(String[].class), null)));
    }

    public xObject exec(xScope s, MethodDeclaration x, List<xObject> args) throws Throwable {
        System.out.printf("[exec] (%s,%s,%s)\n", s, x, args);
        xScope ss = s.newChild(x);
        int i = 0;
        for (Parameter p : x.getParameters())
            ss.declareVar(p.getType(), p.getNameAsString()).setVar(args.get(i++));
        exec(ss, x.getBody().get());
        return null;
    }

    public void exec(xScope s, Statement x) throws Throwable {
        if (x.isBlockStmt()) {
            xScope ss = s.newChild(x);
            x.asBlockStmt().getStatements().forEach(xx -> exec2(ss, xx));
        } else if (x.isExpressionStmt()) {
            exec(s, x.asExpressionStmt().getExpression());
        } else if (x.isForStmt()) {
            ForStmt xx = x.asForStmt();
            xScope ss = s.newChild(x);
            xx.getInitialization().forEach(y -> exec2(ss, y));
            Expression c = xx.getCompare().get();
            while ((boolean) exec(ss, c).i == true) {
                exec(ss, xx.getBody());
                xx.getUpdate().forEach(y -> exec2(ss, y));
            }
        } else {
            throw new Throwable(String.format("unhandled statement %s %s", x.getClass().getSimpleName(), x));
        }
    }

    public xObject exec(xScope s, Expression x) throws Throwable {
        System.out.printf("[exec] %s(%s)\n", x.getClass().getSimpleName(), x);
        if (x.isMethodCallExpr()) {
            MethodCallExpr xx = x.asMethodCallExpr();
            List<xObject> args = xx.getArguments().stream().map(y -> exec2(s, y)).toList();
            Optional<Expression> ss = xx.getScope();
            xObject m;
            if (ss.isEmpty())
                m = s.resolveMethod(xx.getNameAsString(),
                        Arrays.asList(args.stream().map(y -> y.c).toArray(iClass[]::new)));
            else
                m = exec(s, ss.get());
            return m.invoke(xx.getNameAsString(), args);
        } else if (x.isFieldAccessExpr()) {
            FieldAccessExpr xx = x.asFieldAccessExpr();
            return exec(s, xx.getScope()).getField(xx.getNameAsString());
        } else if (x.isNameExpr()) {
            String xx = x.asNameExpr().getNameAsString();
            xVar v = s.getVar(xx);
            if (v != null)
                return v;
            else
                return s.resolveClass(xx);
        } else if (x.isStringLiteralExpr()) {
            String xx = StringEscapeUtils.unescapeJava(x.asStringLiteralExpr().getValue());
            return new xObject(null, null, new iClass(String.class), xx);
        } else if (x.isIntegerLiteralExpr()) {
            int xx = x.asIntegerLiteralExpr().asNumber().intValue();
            return new xObject(null, null, new iClass(Integer.class), xx);
        } else if (x.isVariableDeclarationExpr()) {
            x.asVariableDeclarationExpr().getVariables().forEach(y -> {
                xVar v = s.declareVar(y.getType(), y.getNameAsString());
                Optional<Expression> yy = y.getInitializer();
                if (!yy.isEmpty())
                    v.setVar(exec2(s, yy.get()));
            });
            return null;
        } else if (x.isAssignExpr()) {
            AssignExpr xx = x.asAssignExpr();
            if (xx.getOperator() != AssignExpr.Operator.ASSIGN)
                throw new UnsupportedOperationException();
            ((xVar) exec(s, xx.getTarget())).i = exec(s, xx.getValue()).i;
            return null;
        } else if (x.isBinaryExpr()) {
            BinaryExpr xx = x.asBinaryExpr();
            xObject l = exec(s, xx.getLeft());
            xObject r = exec(s, xx.getRight());
            return binary_exec(xx.getOperator(), l, r);
        } else if (x.isUnaryExpr()) {
            UnaryExpr xx = x.asUnaryExpr();
            return unary_exec(xx.getOperator(), exec(s, xx.getExpression()));
        } else if (x.isObjectCreationExpr()) {
            ObjectCreationExpr xx = x.asObjectCreationExpr();
            if (!xx.getScope().isEmpty())
                throw null;
            return exec(s, xx.getType().getNameAsExpression())
                    .create(xx.getArguments().stream().map(y -> exec2(s, y)).toList());
        } else {
            throw new Throwable(String.format("unhandled expression %s %s", x.getClass().getSimpleName(), x));
        }
    }

    public xObject binary_exec(BinaryExpr.Operator op, xObject l, xObject r) throws Throwable {
        System.out.printf("binary_exec(op=%s,l=(%s),r=(%s))\n", op, l, r);
        if (l.c.equals(Integer.class) && r.c.equals(Integer.class)) {
            if (op == BinaryExpr.Operator.LESS_EQUALS)
                return new xObject(null, null, new iClass(Boolean.class), ((int) l.i) <= ((int) r.i));
            else if (op == BinaryExpr.Operator.MULTIPLY)
                return new xObject(null, null, new iClass(Integer.class), ((int) l.i) * ((int) r.i));
        } else if (op == BinaryExpr.Operator.PLUS && (l.c.equals(String.class) || r.c.equals(String.class)))
            return new xObject(null, null, new iClass(String.class), l.i.toString() + r.i.toString());
        throw new UnsupportedOperationException();
    }

    public xObject unary_exec(UnaryExpr.Operator op, xObject x) throws Throwable {
        System.out.printf("binary_exec(op=%s,x=(%s))\n", op, x);
        if (op == UnaryExpr.Operator.POSTFIX_INCREMENT && x.c.equals(Integer.class)) {
            x.i = (int) x.i + 1;
            return x;
        }
        throw new UnsupportedOperationException();
    }

    class xVar extends xObject {
        public xVar(String n, xObject p, iClass c, Object i) {
            super(n, p, c, i);
        }

        public void setVar(xObject v) {
            System.out.printf("[var] set %s %s\n", n, v);
            this.i = v.i;
        }
    }

    public iExecutable findExecutable(iClass c, String n, iClass[] cx) throws Throwable {
        System.out.printf("[findExecutable] %s.%s(%s)\n", c, n, Arrays.toString(cx));
        boolean ct = n == null;
        try {
            if (!ct)
                return c.getMethod(n, cx);
            else
                return c.getConstructor(cx);
        } catch (Exception e) {
            System.out.println(e);
        }
        /*
         * next_exec: for (Executable e : (ct ? c.getConstructors() : c.getMethods())) {
         * if (!ct && e.getName().equals(n)) { if (e.isVarArgs()) { iClass[] ccx =
         * e.getParameterTypes(); for (int i = 0; i < ccx.length - 1; i++) { if
         * (cx[i].equals(ccx[i])) continue next_exec; } iClass t = ccx[ccx.length -
         * 1].getComponentType(); for (int i = ccx.length; i < cx.length; i++) { if
         * (!t.isAssignableFrom(cx[i])) continue next_exec; } return e; } } }
         *
        throw new NoSuchMethodException(n);
    }

    class xClass extends xObject {
        xScope s;
        ClassOrInterfaceDeclaration d;

        public xClass(xScope s, ClassOrInterfaceDeclaration d) {
            super(d.getNameAsString(), null, null, null);
            this.s = s;
            this.d = d;
        }

        @Override
        public xObject create(List<xObject> args) throws Throwable {
            xScope ss = s.newChild(d);
            exec(ss, d.getConstructorByParameterTypes(args.stream().map(x -> x.c).toArray(Class[]::new)).get()
                    .getBody());
            return new xObject(n, this, new iClassVirtual(d), ss);
        }
    }

    class xMethod extends xObject {
        xScope s;
        MethodDeclaration md;

        public xMethod(xScope s, MethodDeclaration md) {
            super(md.getNameAsString(), null, null, null);
            this.s = s;
            this.md = md;
        }

        @Override
        public xObject invoke(String n, List<xObject> args) throws Throwable {
            System.out.printf("[invoke] xMethod (%s)%s(%s)(%s)\n", s, n,
                    args.stream().map(x -> x.c.getName()).collect(Collectors.joining(",")),
                    args.stream().map(x -> Objects.toString(x.i)).collect(Collectors.joining(",")));
            return exec(s, md, args);
        }
    }

    class xObject {
        String n;
        xObject p;
        iClass c;
        Object i;

        public xObject(String n, xObject p, iClass c, Object i) {
            this.n = n;
            this.p = p;
            this.c = c;
            this.i = i;
            System.out.println(this);
        }

        public xObject getField(String x) throws Throwable {
            iField f = c.getField(x);
            return new xObject(x, this, f.getType(), f.get(i));
        }

        public xObject create(List<xObject> args) throws Throwable {
            return execute(null, args);
        }

        public xObject invoke(String n, List<xObject> args) throws Throwable {
            return execute(n, args);
        }

        public xObject execute(String n, List<xObject> args) throws Throwable {
            System.out.printf("[execute] %s.%s(%s)(%s)\n", c.getName(), n,
                    args.stream().map(x -> x.c == null ? null : x.c.getName()).collect(Collectors.joining(",")),
                    args.stream().map(x -> Objects.toString(x.i)).collect(Collectors.joining(",")));
            iExecutable e = findExecutable(c, n, args.stream().map(x -> x.c).toArray(iClass[]::new));
            Object v;
            if (e.isVarArgs()) {
                int ii = e.getParameterCount() - 1;
                Object[] xs = Stream.concat(args.stream().limit(ii).map(x -> x.i), Stream.of((Object) null)).toArray();
                xs[ii] = args.stream().skip(ii).map(x -> x.i).toArray();
                v = execute2(e, i, xs);
            } else
                v = execute2(e, i, args.stream().map(x -> x.i).toArray());
            iClass t;
            if (e instanceof iMethod)
                t = ((iMethod) e).getReturnType();
            else if (e instanceof iConstructor)
                t = ((iConstructor) e).getDeclaringClass();
            else
                throw new UnsupportedOperationException();
            return new xObject(null, null, t, v);
        }

        public Object execute2(iExecutable e, Object i, Object[] args) throws Throwable {
            System.out.printf("[execute2] %s[%s](%s)\n", e, i, Arrays.toString(args));
            if (e instanceof iMethod)
                return ((iMethod) e).invoke(i, args);
            else if (e instanceof iConstructor)
                return ((iConstructor) e).newInstance(args);
            throw new UnsupportedOperationException();
        }

        @Override
        public String toString() {
            return String.format("xObject(n=%s,p=%s,c=%s,i=%s)", n, p, c, i);
        }
    }

    public xObject exec2(xScope s, Expression x) {
        try {
            return exec(s, x);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public void exec2(xScope s, Statement x) {
        try {
            exec(s, x);
        } catch (Throwable t) {
            throw new RuntimeException(t);
        }
    }

    public static void main(String[] args) throws Throwable {
        new Main2().main();
    }
}*/