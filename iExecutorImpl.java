import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;

public class iExecutorImpl implements iExecutor {
    Scope current_scope;

    public iExecutorImpl(Scope scope) {
        current_scope = scope;
    }

    void log(Scope scope, Object x) {
        scope.log("[exec] %s(%s)", x.getClass().getSimpleName(), x);
    }

    void log(Scope scope, Object x, Object v) {
        scope.log("[exec2] %s(%s)=%s", x.getClass().getSimpleName(), x, v);
    }

    public iObject exec(MethodDeclaration x, iObject instance, iObject... args) {
        Scope scope = current_scope.getChild(x);
        iExecutor exec = scope.getExecutor();
        try {
            log(scope, x);
            iObject v = iExecutorStatic.exec(scope, exec, x, instance, args);
            log(scope, x, v);
            return v;
        } catch (Throwable t) {
            throw throw_err(x, t);
        }
    }

    public iObject exec(ConstructorDeclaration x, iObjectVirtual instance, iObject... args) {
        Scope scope = current_scope.getChild(x);
        iExecutor exec = scope.getExecutor();
        try {
            log(scope, x);
            iObject v = iExecutorStatic.exec(scope, exec, x, instance, args);
            log(scope, x, v);
            return v;
        } catch (Throwable t) {
            throw throw_err(x, t);
        }
    }

    public iObject exec(Statement x) {
        Scope scope = current_scope.getChild(x);
        iExecutor exec = scope.getExecutor();
        try {
            log(scope, x);
            iObject v = iExecutorStatic.exec(scope, exec, x);
            log(scope, x, v);
            return v;
        } catch (Throwable t) {
            throw throw_err(x, t);
        }
    }

    public iObject exec(Expression x) {
        Scope scope = current_scope.getChild(x);
        iExecutor exec = scope.getExecutor();
        try {
            log(scope, x);
            iObject v = iExecutorStatic.exec(scope, exec, x);
            log(scope, x, v);
            return v;
        } catch (Throwable t) {
            throw throw_err(x, t);
        }
    }

    public iObject exec(iExecutable x, iObject obj, iObject[] args) {
        Scope scope = current_scope;// ????????
        iExecutor exec = scope.getExecutor();
        try {
            log(scope, x);
            iObject v = iExecutorStatic.exec(scope, exec, x, obj, args);
            log(scope, x, v);
            return v;
        } catch (Throwable t) {
            throw throw_err(x, t);
        }
    }

    public static RuntimeException throw_err(Object x, Throwable t) {
        return new RuntimeException("error while running " + x.getClass().getSimpleName() + " " + x, t);
    }
}
