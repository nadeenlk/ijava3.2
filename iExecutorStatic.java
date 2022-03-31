import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

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

public class iExecutorStatic {
    public static iObject exec(Scope scope, iExecutor exec, MethodDeclaration md, iObject instance, iObject... args)
            throws Throwable {
        int i = 0;
        for (Parameter p : md.getParameters())
            scope.getVars().declare(scope.findClass(p.getType()), p.getNameAsString()).set(args[i++]);
        return exec.exec(md.getBody().get());
    }

    public static iObject exec(Scope scope, iExecutor exec, ConstructorDeclaration cd, iObjectVirtual instance,
            iObject... args) throws Throwable {
        return instance.getScope().getExecutor().exec(cd.getBody());
    }

    public static iObject exec(Scope scope, iExecutor exec, Statement x) throws Throwable {
        if (x.isBlockStmt()) {
            for (Statement s : x.asBlockStmt().getStatements()) {
                iObject v = exec.exec(s);
                if (v instanceof iObjectReturn)
                    return ((iObjectReturn) v).value;
                if (v instanceof iObjectBreak)
                    return v;
            }
            return null;
        } else if (x.isExpressionStmt()) {
            return exec.exec(x.asExpressionStmt().getExpression());
        } else if (x.isForStmt()) {
            ForStmt xx = x.asForStmt();
            xx.getInitialization().forEach(y -> exec.exec(y));
            Expression c = xx.getCompare().get();
            forloop: while ((boolean) exec.exec(c).asWrapped().x) {
                if (exec.exec(xx.getBody()) instanceof iObjectBreak)
                    break forloop;
                xx.getUpdate().forEach(y -> exec.exec(y));
            }
            return null;
        } else if (x.isForEachStmt()) {
            ForEachStmt xx = x.asForEachStmt();
            if (xx.getVariable().getVariables().size() != 1)
                throw new UnsupportedOperationException();
            exec.exec(xx.getVariable());
            VariableDeclarator ivd = xx.getVariable().getVariable(0);
            iObjectVariable iv = scope.getVars().get(ivd.getName().toString());
            Object iz = exec.exec(xx.getIterable()).asWrapped().x;
            Iterable<?> ii;
            if (iz.getClass().isArray())
                ii = new IterableArray(iz);
            else if (iz instanceof Iterable<?>) {
                ii = (Iterable<?>) iz;
            } else {
                throw new UnsupportedOperationException();
            }
            forloop: for (Object z : ii) {
                iv.set(new iObjectWrapped(scope, z));
                if (exec.exec(xx.getBody()) instanceof iObjectBreak)
                    break forloop;
            }
            return null;
        } else if (x.isReturnStmt()) {
            return new iObjectReturn(scope, x.asReturnStmt().getExpression().map(e -> exec.exec(e)).orElse(null));
        } else if (x.isIfStmt()) {
            IfStmt xx = x.asIfStmt();
            if ((boolean) exec.exec(xx.getCondition()).asWrapped().x)
                return exec.exec(xx.getThenStmt());
            else
                return xx.getElseStmt().map(e -> exec.exec(e)).orElse(null);
        } else if (x.isBreakStmt()) {
            return new iObjectBreak(scope);
        } else if (x.isEmptyStmt()) {
            return null;
        } else {
            throw new Throwable(String.format("unhandled statement %s %s", x.getClass().getSimpleName(), x));
        }
    }

    public static iObject exec(Scope scope, iExecutor exec, Expression x) throws Throwable {
        if (x.isMethodCallExpr()) {
            MethodCallExpr xx = x.asMethodCallExpr();
            List<iObject> args = xx.getArguments().stream().map(arg -> exec.exec(arg)).toList();
            List<iClass> argcs = args.stream().map(y -> y.getClazz()).toList();
            Optional<Expression> ss = xx.getScope();
            if (ss.isEmpty()) {
                iMethod m = scope.findExecutable(xx.getNameAsString(), argcs).asMethod();
                return exec.exec(m, new iObjectWrapped(scope, null), args.toArray(iObject[]::new));
            }
            iObject i = exec.exec(ss.get());
            iMethod m = iExecutableMatcher
                    .getExecutable(scope, i.getClazz(), xx.getNameAsString(), argcs.toArray(iClass[]::new)).asMethod();
            iObject ii = i.asExecArg();
            return exec.exec(m, ii, args.toArray(iObject[]::new));
        } else if (x.isStringLiteralExpr()) {
            return new iObjectWrapped(scope, StringEscapeUtils.unescapeJava(x.asStringLiteralExpr().getValue()));
        } else if (x.isFieldAccessExpr()) {
            FieldAccessExpr xx = x.asFieldAccessExpr();
            iObject obj = exec.exec(xx.getScope());
            iClass c = obj.getClazz();
            if (obj instanceof iClass)
                obj = new iObjectWrapped(scope, null);
            return new iFieldObject(scope, obj, c.getField(xx.getNameAsString()));
        } else if (x.isNameExpr()) {
            String xx = x.asNameExpr().getNameAsString();
            iObjectVariable v = scope.getVars().get(xx);
            if (v != null)
                return v;
            else
                return scope.findClass(xx);
        } else if (x.isObjectCreationExpr()) {
            ObjectCreationExpr xx = x.asObjectCreationExpr();
            if (!xx.getScope().isEmpty())
                throw new UnsupportedOperationException();
            List<iObject> args = xx.getArguments().stream().map(arg -> exec.exec(arg)).toList();
            return iExecutableMatcher
                    .getExecutable(scope, scope.findClass(xx.getType()), null,
                            args.stream().map(arg -> arg.getClazz()).toArray(iClass[]::new))
                    .asConstructor().newInstance(args.stream().map(arg -> arg.asExecArg()).toArray(iObject[]::new));
        } else if (x.isVariableDeclarationExpr()) {
            x.asVariableDeclarationExpr().getVariables().forEach(y -> {
                iClass clz;
                try {
                    clz = scope.findClass(y.getType());
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                iObjectVariable v = scope.getVars().declare(clz, y.getNameAsString());
                Optional<Expression> yy = y.getInitializer();
                if (!yy.isEmpty())
                    v.set(exec.exec(yy.get()));
            });
            return null;
        } else if (x.isIntegerLiteralExpr()) {
            return new iObjectWrapped(scope, x.asIntegerLiteralExpr().asNumber().intValue());
        } else if (x.isLongLiteralExpr()) {
            return new iObjectWrapped(scope, x.asLongLiteralExpr().asNumber().longValue());
        } else if (x.isAssignExpr()) {
            AssignExpr xx = x.asAssignExpr();
            if (xx.getOperator() != AssignExpr.Operator.ASSIGN)
                throw new UnsupportedOperationException();
            iObject v = exec.exec(xx.getValue());
            iObject z = exec.exec(xx.getTarget());
            if (z instanceof iFieldObject) {
                ((iFieldObject) z).set(v);
                return v;
            }
            return z.asVariable().set(v);// return ?????????????????
        } else if (x.isBinaryExpr()) {
            BinaryExpr xx = x.asBinaryExpr();
            iObject l = exec.exec(xx.getLeft());
            iObject r = exec.exec(xx.getRight());
            return new iObjectWrapped(scope, binary_unary_exec.binary_exec(scope, xx.getOperator(), l, r));
        } else if (x.isUnaryExpr()) {
            UnaryExpr xx = x.asUnaryExpr();
            return new iObjectWrapped(scope,
                    binary_unary_exec.unary_exec(scope, xx.getOperator(), exec.exec(xx.getExpression())));
        } else if (x.isEnclosedExpr()) {
            Expression xx = x.asEnclosedExpr().getInner();
            return exec.exec(xx);
        } else if (x.isCastExpr()) {
            CastExpr xx = x.asCastExpr();
            return scope.findClass(xx.getType()).cast(exec.exec(xx.getExpression()));
        } else if (x.isNullLiteralExpr()) {
            return iNull.Null;
        } else if (x.isArrayCreationExpr()) {
            ArrayCreationExpr xx = x.asArrayCreationExpr();
            iClass clz = scope.findClass(xx.getElementType());
            Optional<ArrayInitializerExpr> oi = xx.getInitializer();
            iObject a;
            if (oi.isPresent()) {
                ArrayInitializerExpr i = oi.get();
                List<iObject> vs = i.getValues().stream().map(v -> exec.exec(v)).toList();
                a = clz.newArray(new int[] { vs.size() });
                for (int j = 0; j < vs.size(); j++) {
                    clz.setItem(a, j, vs.get(j));
                }
            } else {
                int[] ds = xx.getLevels().stream().mapToInt(l -> {
                    return (int) exec.exec(l.getDimension().get()).asWrapped().x;
                }).toArray();
                a = clz.newArray(ds);
            }
            return a;
        } else if (x.isArrayAccessExpr()) {
            ArrayAccessExpr xx = x.asArrayAccessExpr();
            iObject a = exec.exec(xx.getName());
            int i = (int) exec.exec(xx.getIndex()).asWrapped().x;
            return new iFieldObject(scope, a, new iFieldArrayElement(scope, a.getClazz(), i));
        } else if (x.isBooleanLiteralExpr()) {
            BooleanLiteralExpr xx = x.asBooleanLiteralExpr();
            return new iObjectWrapped(scope, xx.getValue());
        } else if (x.isClassExpr()) {
            iClass clz = scope.findClass(x.asClassExpr().getType());
            if (!(clz instanceof iClassWrapped))
                throw new UnsupportedOperationException();
            Class<?> c = ((iClassWrapped) clz).x;
            return new iObjectWrapped(scope, c);
        } else {
            throw new Throwable(String.format("unhandled expression %s %s", x.getClass().getSimpleName(), x));
        }
    }

    public static iObject exec(Scope scope, iExecutor exec, iExecutable e, iObject obj, iObject[] args)
            throws Throwable {
        //scope.log("[exec] %s %s %s", obj, e, Arrays.toString(args));
        if (e.isVarArgs()) {
            if (e instanceof iConstructorWrapped || e instanceof iMethodWrapped) {
                int i = e.getParameterCount();
                iObject[] args2 = new iObject[i];
                int z = 0;
                while (z < i - 1)
                    args2[z++] = args[i];
                iClassWrapped clz = (iClassWrapped) e.getParameterTypes()[i - 1];
                List<Object> xs = new ArrayList<>();
                if (args.length > z)
                    while (z < args.length)
                        xs.add(args[z++].asWrapped().x);
                iObject v = null;
                if (xs.size() == -1) {
                    //Object x = xs.get(0);
                    throw new UnsupportedOperationException();
                    /*if (x.getClazz() instanceof iClassArrayWrapped) {
                        if (clz.isAssignableFrom(x.getClazz().getComponentType()))
                            v = x;
                    }*/
                }
                if (v == null) {
                    Object vv = Array.newInstance(clz.x, xs.size());
                    for (z = 0; z < xs.size(); z++) {
                        Array.set(vv, z, xs.get(z));
                    }
                    v = new iObjectWrapped(scope, vv);
                }
                scope.log("[varargs] %s", args, v);
                args2[i - 1] = v;
                args = args2;
            } else
                throw new UnsupportedOperationException();
        }
        scope.log("[executeExecutable] %s.%s(%s)", e, obj, Arrays.toString(args));
        iObject v = obj == null ? e.asConstructor().newInstance(args) : e.asMethod().invoke(obj, args);
        scope.log("[executeExecutable2] %s.%s(%s)=%s", e, obj, Arrays.toString(args), v);
        return v;
    }

}
