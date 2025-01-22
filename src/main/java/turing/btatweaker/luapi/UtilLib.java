package turing.btatweaker.luapi;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.docs.Description;
import turing.docs.Documented;
import turing.docs.Library;

@Documented
@Library(value = "util", className = "Util")
@Description("Provides various utility functions.")
public class UtilLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();

        env.set("util", t);
        env.get("package").get("loaded").set("util", t);

        return t;
    }
}
