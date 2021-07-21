import java.util.Objects;

import com.github.javaparser.ast.expr.BinaryExpr;
import com.github.javaparser.ast.expr.UnaryExpr;

public class binary_unary_exec {
    static iClass iDouble = new iClassWrapped(Double.class);
    static iClass iFloat = new iClassWrapped(Float.class);
    static iClass iLong = new iClassWrapped(Long.class);
    static iClass iInteger = new iClassWrapped(Integer.class);
    static iClass iString = new iClassWrapped(String.class);

    public static Object binary_exec(Scope scope, BinaryExpr.Operator op, iObject l, iObject r) throws Throwable {
        scope.log("binary_exec(op=%s,l=(%s),r=(%s))\n", op, l, r);
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
            if (op == BinaryExpr.Operator.EQUALS)
                return ll == rr;
            else if (op == BinaryExpr.Operator.NOT_EQUALS)
                return ll != rr;
            else if (op == BinaryExpr.Operator.LESS)
                return ll < rr;
            else if (op == BinaryExpr.Operator.GREATER)
                return ll > rr;
            else if (op == BinaryExpr.Operator.LESS_EQUALS)
                return ll <= rr;
            else if (op == BinaryExpr.Operator.GREATER_EQUALS)
                return ll >= rr;
            else if (op == BinaryExpr.Operator.PLUS)
                return ll + rr;
            else if (op == BinaryExpr.Operator.MINUS)
                return ll - rr;
            else if (op == BinaryExpr.Operator.MULTIPLY)
                return ll * rr;
            else if (op == BinaryExpr.Operator.DIVIDE)
                return ll / rr;
            else if (op == BinaryExpr.Operator.REMAINDER)
                return ll % rr;
        } else if (lc.equals(iFloat) || rc.equals(iFloat)) {
            float ll = ((Number) l.asWrapped().x).floatValue();
            float rr = ((Number) r.asWrapped().x).floatValue();
            if (op == BinaryExpr.Operator.EQUALS)
                return ll == rr;
            else if (op == BinaryExpr.Operator.NOT_EQUALS)
                return ll != rr;
            else if (op == BinaryExpr.Operator.LESS)
                return ll < rr;
            else if (op == BinaryExpr.Operator.GREATER)
                return ll > rr;
            else if (op == BinaryExpr.Operator.LESS_EQUALS)
                return ll <= rr;
            else if (op == BinaryExpr.Operator.GREATER_EQUALS)
                return ll >= rr;
            else if (op == BinaryExpr.Operator.PLUS)
                return ll + rr;
            else if (op == BinaryExpr.Operator.MINUS)
                return ll - rr;
            else if (op == BinaryExpr.Operator.MULTIPLY)
                return ll * rr;
            else if (op == BinaryExpr.Operator.DIVIDE)
                return ll / rr;
            else if (op == BinaryExpr.Operator.REMAINDER)
                return ll % rr;
        } else if (lc.equals(iLong) || rc.equals(iLong)) {
            long ll = ((Number) l.asWrapped().x).longValue();
            long rr = ((Number) r.asWrapped().x).longValue();
            if (op == BinaryExpr.Operator.BINARY_OR)
                return ll | rr;
            else if (op == BinaryExpr.Operator.BINARY_AND)
                return ll & rr;
            else if (op == BinaryExpr.Operator.XOR)
                return ll ^ rr;
            else if (op == BinaryExpr.Operator.EQUALS)
                return ll == rr;
            else if (op == BinaryExpr.Operator.NOT_EQUALS)
                return ll != rr;
            else if (op == BinaryExpr.Operator.LESS)
                return ll < rr;
            else if (op == BinaryExpr.Operator.GREATER)
                return ll > rr;
            else if (op == BinaryExpr.Operator.LESS_EQUALS)
                return ll <= rr;
            else if (op == BinaryExpr.Operator.GREATER_EQUALS)
                return ll >= rr;
            else if (op == BinaryExpr.Operator.LEFT_SHIFT)
                return ll << rr;
            else if (op == BinaryExpr.Operator.SIGNED_RIGHT_SHIFT)
                return ll >> rr;
            else if (op == BinaryExpr.Operator.UNSIGNED_RIGHT_SHIFT)
                return ll >>> rr;
            else if (op == BinaryExpr.Operator.PLUS)
                return ll + rr;
            else if (op == BinaryExpr.Operator.MINUS)
                return ll - rr;
            else if (op == BinaryExpr.Operator.MULTIPLY)
                return ll * rr;
            else if (op == BinaryExpr.Operator.DIVIDE)
                return ll / rr;
            else if (op == BinaryExpr.Operator.REMAINDER)
                return ll % rr;
        } else if (lc.equals(iInteger) || rc.equals(iInteger)) {
            int ll = ((Number) l.asWrapped().x).intValue();
            int rr = ((Number) r.asWrapped().x).intValue();
            // System.out.printf("[bin] ll=%s rr=%s\n", ll, rr);
            if (op == BinaryExpr.Operator.BINARY_OR)
                return ll | rr;
            else if (op == BinaryExpr.Operator.BINARY_AND)
                return ll & rr;
            else if (op == BinaryExpr.Operator.XOR)
                return ll ^ rr;
            else if (op == BinaryExpr.Operator.EQUALS)
                return ll == rr;
            else if (op == BinaryExpr.Operator.NOT_EQUALS)
                return ll != rr;
            else if (op == BinaryExpr.Operator.LESS)
                return ll < rr;
            else if (op == BinaryExpr.Operator.GREATER)
                return ll > rr;
            else if (op == BinaryExpr.Operator.LESS_EQUALS)
                return ll <= rr;
            else if (op == BinaryExpr.Operator.GREATER_EQUALS)
                return ll >= rr;
            else if (op == BinaryExpr.Operator.LEFT_SHIFT)
                return ll << rr;
            else if (op == BinaryExpr.Operator.SIGNED_RIGHT_SHIFT)
                return ll >> rr;
            else if (op == BinaryExpr.Operator.UNSIGNED_RIGHT_SHIFT)
                return ll >>> rr;
            else if (op == BinaryExpr.Operator.PLUS)
                return ll + rr;
            else if (op == BinaryExpr.Operator.MINUS)
                return ll - rr;
            else if (op == BinaryExpr.Operator.MULTIPLY)
                return ll * rr;
            else if (op == BinaryExpr.Operator.DIVIDE)
                return ll / rr;
            else if (op == BinaryExpr.Operator.REMAINDER)
                return ll % rr;
        }
        if (op == BinaryExpr.Operator.EQUALS)
            return Objects.equals(l, r);
        throw new UnsupportedOperationException();
    }

    public static Object unary_exec(Scope scope, UnaryExpr.Operator op, iObject x) throws Throwable {
        scope.log("binary_exec(op=%s,x=(%s))\n", op, x);
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
                return xv.set(new iObjectWrapped(v + 1));
            } else if (op == UnaryExpr.Operator.POSTFIX_INCREMENT) {
                xv.set(new iObjectWrapped(v + 1));
                return p;
            } else if (op == UnaryExpr.Operator.PREFIX_DECREMENT) {
                return xv.set(new iObjectWrapped(v - 1));
            } else if (op == UnaryExpr.Operator.POSTFIX_DECREMENT) {
                xv.set(new iObjectWrapped(v - 1));
                return p;
            }
        }
        throw new UnsupportedOperationException();
    }

}
