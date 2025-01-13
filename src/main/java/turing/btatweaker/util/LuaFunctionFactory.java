package turing.btatweaker.util;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.*;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class LuaFunctionFactory {
    public interface Function2<A1, A2, R> {
        R apply(A1 a1, A2 a2);
    }

    public interface Function3<A1, A2, A3, R> {
        R apply(A1 a1, A2 a2, A3 a3);
    }

    public interface Consumer2<T, T2> {
        void accept(T t, T2 t2);
    }

    public interface Consumer3<T, T2, T3> {
        void accept(T t, T2 t2, T3 t3);
    }

    public static ZeroArgFunction zeroArgFunction(Supplier<LuaValue> supplier) {
        return new ZeroArgFunction() {
            @Override
            public LuaValue call() {
                return supplier.get();
            }
        };
    }

    public static OneArgFunction zeroArgMethod(Function<LuaValue, LuaValue> func) {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return func.apply(self);
            }
        };
    }

    public static OneArgFunction zeroArgBuilderMethod(Consumer<LuaValue> consumer) {
        return new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                consumer.accept(self);
                return self;
            }
        };
    }

    public static OneArgFunction oneArgFunction(Function<LuaValue, LuaValue> func) {
        return zeroArgMethod(func);
    }

    public static TwoArgFunction oneArgMethod(Function2<LuaValue, LuaValue, LuaValue> func) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue arg1) {
                return func.apply(self, arg1);
            }
        };
    }

    public static TwoArgFunction oneArgBuilderMethod(Consumer2<LuaValue, LuaValue> consumer) {
        return new TwoArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue arg) {
                consumer.accept(self, arg);
                return self;
            }
        };
    }

    public static TwoArgFunction twoArgFunction(Function2<LuaValue, LuaValue, LuaValue> func) {
        return oneArgMethod(func);
    }

    public static ThreeArgFunction twoArgMethod(Function3<LuaValue, LuaValue, LuaValue, LuaValue> func) {
        return new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue arg1, LuaValue arg2) {
                return func.apply(self, arg1, arg2);
            }
        };
    }

    public static ThreeArgFunction twoArgBuilderMethod(Consumer3<LuaValue, LuaValue, LuaValue> consumer) {
        return new ThreeArgFunction() {
            @Override
            public LuaValue call(LuaValue self, LuaValue arg1, LuaValue arg2) {
                consumer.accept(self, arg1, arg2);
                return self;
            }
        };
    }

    public static VarArgFunction varArgsFunction(Function<Varargs, LuaValue> func) {
        return new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs args) {
                return func.apply(args);
            }
        };
    }

    public static VarArgFunction varArgsMethod(Function2<LuaValue, Varargs, LuaValue> func) {
        return new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs args) {
                LuaTable self = args.checktable(1);
                return func.apply(self, args.subargs(2));
            }
        };
    }

    public static VarArgFunction varArgsBuilderMethod(Consumer2<LuaValue, Varargs> consumer) {
        return new VarArgFunction() {
            @Override
            public LuaValue invoke(Varargs args) {
                LuaTable self = args.checktable(1);
                consumer.accept(self, args.subargs(2));
                return self;
            }
        };
    }
}
