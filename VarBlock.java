import java.util.LinkedHashMap;

public class VarBlock {
    LinkedHashMap<String, iObjectVariable> vars = new LinkedHashMap<>();
    LinkedHashMap<Scope, VarBlock> childs = new LinkedHashMap<>();
    VarBlock parent;
    Scope scope;

    public VarBlock(VarBlock parent, Scope scope) {
        this.parent = parent;
        this.scope = scope;
    }

    public VarBlock getChild(Scope scope) {
        return childs.computeIfAbsent(scope, k -> new VarBlock(this, k));
    }

    public iObjectVariable declare(iClass type, String name) {
        iObjectVariable obj = new iObjectVariable(scope, type, name);
        vars.put(name, obj);
        return obj;
    }

    public iObjectVariable get(String n) {
        iObjectVariable v = vars.get(n);
        scope.log("[varblock] getVar %s=%s", n, v);
        if (v == null && parent != null)
            v = parent.get(n);
        return v;
    }

    @Override
    public String toString() {
        return "VarBlock" + vars;
    }
}
