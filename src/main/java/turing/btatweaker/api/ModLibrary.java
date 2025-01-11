package turing.btatweaker.api;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.List;

public abstract class ModLibrary extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();

        setupLib(t, env);

        return t;
    }

    public abstract void setupLib(LuaTable libraryTable, LuaValue env);

    public abstract List<String> getValidNames();
}
