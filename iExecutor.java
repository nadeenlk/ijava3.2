import com.github.javaparser.ast.body.ConstructorDeclaration;
import com.github.javaparser.ast.body.MethodDeclaration;
import com.github.javaparser.ast.expr.Expression;
import com.github.javaparser.ast.stmt.Statement;

interface iExecutor {
    public iObject exec(MethodDeclaration md, iObject instance, iObject... args);

    public iObject exec(ConstructorDeclaration cd, iObjectVirtual instance, iObject... args);

    public iObject exec(Statement x);

    public iObject exec(Expression x);

    public iObject exec(iExecutable e, iObject obj, iObject[] args);
}
