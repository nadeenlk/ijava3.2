import java.util.List;

import com.github.javaparser.ast.Node;

public interface Scope {
    public void log(String format, Object... args);

    public void setClz(iClassVirtual clz);

    public Scope getChild(Node node);

    public boolean hasParent();

    public Scope getParent();

    public iExecutor getExecutor();

    public iClass findClass(String name) throws ClassNotFoundException;

    public iExecutable findExecutable(String name, List<iClass> types) throws NoSuchMethodException;

    public VarBlock getVars();
}
