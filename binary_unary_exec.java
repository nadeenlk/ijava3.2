import java.util.Objects;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;

public class binary_unary_exec {
    static iClass iDouble, iFloat, iLong, iInteger, iString, iBool;
    static {
        Scope scope = ScopeImpl.newRootScope(null);
        iDouble = iClassWrapped.from(scope, Double.class);
        iFloat = iClassWrapped.from(scope, Float.class);
        iLong = iClassWrapped.from(scope, Long.class);
        iInteger = iClassWrapped.from(scope, Integer.class);
        iString = iClassWrapped.from(scope, String.class);
        iBool = iClassWrapped.from(scope, Boolean.class);
    }

    @SuppressWarnings("all")
    public static Object binary_exec(Scope scope, BinaryExpr.Operator op, iObject l, iObject r) throws Throwable {
        scope.log("binary_exec(op=%s,l=(%s),r=(%s))", op, l, r);
        if (l instanceof iObjectVariable)
            return binary_exec(scope, op, l.asVariable().get(), r);
        if (r instanceof iObjectVariable)
            return binary_exec(scope, op, l, r.asVariable().get());
        iClass lc = l.getClazz();
        iClass rc = r.getClazz();
        /*
         * OR("||"), AND("&&"), BINARY_OR("|"), BINARY_AND("&"), XOR("^"), EQUALS("=="),
         * NOT_EQUALS("!="), LESS("<"), GREATER(">"), LESS_EQUALS("<="),
         * GREATER_EQUALS(">="), LEFT_SHIFT("<<"), SIGNED_RIGHT_SHIFT(">>"),
         * UNSIGNED_RIGHT_SHIFT(">>>"), PLUS("+"), MINUS("-"), MULTIPLY("*"),
         * DIVIDE("/"), REMAINDER("%");
         */
        if (op == BinaryExpr.Operator.PLUS && (lc.equals(iString) || rc.equals(iString)))
            return Objects.toString(l.asWrapped().x) + Objects.toString(r.asWrapped().x);
        if (lc.equals(iDouble) || rc.equals(iDouble)) {
            double ll = ((Number) l.asWrapped().x).doubleValue();
            double rr = ((Number) r.asWrapped().x).doubleValue();
            switch (op) {
            case EQUALS:
                return ll == rr;
            case NOT_EQUALS:
                return ll != rr;
            case LESS:
                return ll < rr;
            case GREATER:
                return ll > rr;
            case LESS_EQUALS:
                return ll <= rr;
            case GREATER_EQUALS:
                return ll >= rr;
            case PLUS:
                return ll + rr;
            case MINUS:
                return ll - rr;
            case MULTIPLY:
                return ll * rr;
            case DIVIDE:
                return ll / rr;
            case REMAINDER:
                return ll % rr;
            }
        } else if (lc.equals(iFloat) || rc.equals(iFloat)) {
            float ll = ((Number) l.asWrapped().x).floatValue();
            float rr = ((Number) r.asWrapped().x).floatValue();
            switch (op) {
            case EQUALS:
                return ll == rr;
            case NOT_EQUALS:
                return ll != rr;
            case LESS:
                return ll < rr;
            case GREATER:
                return ll > rr;
            case LESS_EQUALS:
                return ll <= rr;
            case GREATER_EQUALS:
                return ll >= rr;
            case PLUS:
                return ll + rr;
            case MINUS:
                return ll - rr;
            case MULTIPLY:
                return ll * rr;
            case DIVIDE:
                return ll / rr;
            case REMAINDER:
                return ll % rr;
            }
        } else if (lc.equals(iLong) || rc.equals(iLong)) {
            long ll = ((Number) l.asWrapped().x).longValue();
            long rr = ((Number) r.asWrapped().x).longValue();
            switch (op) {
            case BINARY_OR:
                return ll | rr;
            case BINARY_AND:
                return ll & rr;
            case XOR:
                return ll ^ rr;
            case EQUALS:
                return ll == rr;
            case NOT_EQUALS:
                return ll != rr;
            case LESS:
                return ll < rr;
            case GREATER:
                return ll > rr;
            case LESS_EQUALS:
                return ll <= rr;
            case GREATER_EQUALS:
                return ll >= rr;
            case LEFT_SHIFT:
                return ll << rr;
            case SIGNED_RIGHT_SHIFT:
                return ll >> rr;
            case UNSIGNED_RIGHT_SHIFT:
                return ll >>> rr;
            case PLUS:
                return ll + rr;
            case MINUS:
                return ll - rr;
            case MULTIPLY:
                return ll * rr;
            case DIVIDE:
                return ll / rr;
            case REMAINDER:
                return ll % rr;
            }
        } else if (lc.equals(iInteger) || rc.equals(iInteger)) {
            int ll = ((Number) l.asWrapped().x).intValue();
            int rr = ((Number) r.asWrapped().x).intValue();
            // System.out.printf("[bin] ll=%s rr=%s\n", ll, rr);
            switch (op) {
            case BINARY_OR:
                return ll | rr;
            case BINARY_AND:
                return ll & rr;
            case XOR:
                return ll ^ rr;
            case EQUALS:
                return ll == rr;
            case NOT_EQUALS:
                return ll != rr;
            case LESS:
                return ll < rr;
            case GREATER:
                return ll > rr;
            case LESS_EQUALS:
                return ll <= rr;
            case GREATER_EQUALS:
                return ll >= rr;
            case LEFT_SHIFT:
                return ll << rr;
            case SIGNED_RIGHT_SHIFT:
                return ll >> rr;
            case UNSIGNED_RIGHT_SHIFT:
                return ll >>> rr;
            case PLUS:
                return ll + rr;
            case MINUS:
                return ll - rr;
            case MULTIPLY:
                return ll * rr;
            case DIVIDE:
                return ll / rr;
            case REMAINDER:
                return ll % rr;
            }
        }
        if (op == BinaryExpr.Operator.EQUALS)
            return Objects.equals(l, r);
        if (lc.equals(iBool) || rc.equals(iBool)) {
            boolean ll = (boolean) l.asWrapped().x;
            boolean rr = (boolean) r.asWrapped().x;
            switch (op) {
            case AND:
                return ll & rr;
            }
        }
        throw new UnsupportedOperationException();
    }

    public static Object unary_exec(Scope scope, UnaryExpr.Operator op, iObject x) throws Throwable {
        scope.log("binary_exec(op=%s,x=(%s))", op, x);
        iObjectVariable xv = x.asVariable();
        iClass c = xv.getClazz();
        iObjectWrapped p = x.asVariable().get().asWrapped();
        /*
         * 
         * PLUS("+", false), MINUS("-", false), PREFIX_INCREMENT("++", false),
         * PREFIX_DECREMENT("--", false), LOGICAL_COMPLEMENT("!", false),
         * BITWISE_COMPLEMENT("~", false), POSTFIX_INCREMENT("++", true),
         * POSTFIX_DECREMENT("--", true);
         * 
         */
        if (c.equals(iInteger)) {
            int v = (int) p.x;
            if (op == UnaryExpr.Operator.PREFIX_INCREMENT) {
                return xv.set(new iObjectWrapped(scope, v + 1));
            } else if (op == UnaryExpr.Operator.POSTFIX_INCREMENT) {
                xv.set(new iObjectWrapped(scope, v + 1));
                return p;
            } else if (op == UnaryExpr.Operator.PREFIX_DECREMENT) {
                return xv.set(new iObjectWrapped(scope, v - 1));
            } else if (op == UnaryExpr.Operator.POSTFIX_DECREMENT) {
                xv.set(new iObjectWrapped(scope, v - 1));
                return p;
            }
        }
        throw new UnsupportedOperationException();
    }

}
