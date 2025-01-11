package turing.btatweaker.luapi;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

public class LuaClass extends LuaTable {
    public LuaClass() {
        m_metatable = setupMetatable();
    }

    public LuaTable setupMetatable() {
        LuaTable metatable = new LuaTable();
        addToMetatable(metatable, "__add", getAddFunction());
        addToMetatable(metatable, "__sub", getSubFunction());
        addToMetatable(metatable, "__mul", getMulFunction());
        addToMetatable(metatable, "__div", getDivFunction());
        addToMetatable(metatable, "__mod", getModFunction());
        addToMetatable(metatable, "__pow", getPowFunction());
        addToMetatable(metatable, "__index", getIndexFunction());
        addToMetatable(metatable, "__len", getLenFunction());
        addToMetatable(metatable, "__tostring", getToStringFunction());
        addToMetatable(metatable, "__eq", getEqualFunction());
        addToMetatable(metatable, "__le", getLessThanEqualFunction());
        addToMetatable(metatable, "__ge", getGreaterThanEqualFunction());
        addToMetatable(metatable, "__lt", getLessThanFunction());
        addToMetatable(metatable, "__gt", getGreaterThanFunction());
        addToMetatable(metatable, "__newindex", getNewIndexFunction());
        return metatable;
    }

    public void addToMetatable(LuaTable metatable, String name, LuaValue value) {
        if (value != null) {
            metatable.rawset(name, value);
        }
    }

    public TwoArgFunction getMulFunction() {
        return null;
    }

    public TwoArgFunction getEqualFunction() {
        return null;
    }

    public TwoArgFunction getLessThanEqualFunction() {
        return null;
    }

    public TwoArgFunction getLessThanFunction() {
        return null;
    }

    public TwoArgFunction getGreaterThanEqualFunction() {
        return null;
    }

    public TwoArgFunction getGreaterThanFunction() {
        return null;
    }

    public TwoArgFunction getSubFunction() {
        return null;
    }

    public TwoArgFunction getAddFunction() {
        return null;
    }

    public TwoArgFunction getIndexFunction() {
        return null;
    }

    public ThreeArgFunction getNewIndexFunction() {
        return new SetFunction();
    }

    public OneArgFunction getToStringFunction() {
        return null;
    }

    public OneArgFunction getLenFunction() {
        return null;
    }

    public TwoArgFunction getDivFunction() {
        return null;
    }

    public TwoArgFunction getModFunction() {
        return null;
    }

    public TwoArgFunction getPowFunction() {
        return null;
    }

    protected static final class SetFunction extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue self, LuaValue arg, LuaValue arg2) {
            return self;
        }
    }
}
