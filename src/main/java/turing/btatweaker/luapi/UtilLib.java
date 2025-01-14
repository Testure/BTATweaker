package turing.btatweaker.luapi;

import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.util.LuaFunctionFactory;

public class UtilLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();

        env.set("util", t);
        env.get("package").get("loaded").set("util", t);

        /*LuaTable luaTag = new LuaTable();
        luaTag.set("new", LuaFunctionFactory.zeroArgFunction(LuaTag::new));

        env.set("LuaTag", luaTag);
        env.get("package").get("loaded").set("LuaTag", luaTag);*/

        return t;
    }
}
