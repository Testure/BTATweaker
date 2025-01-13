package turing.btatweaker.luapi;

import net.fabricmc.loader.api.FabricLoader;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.ModLibrary;
import turing.btatweaker.util.LuaFunctionFactory;

public class ModLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();

        t.set("isModLoaded", LuaFunctionFactory.oneArgFunction((modName) ->
                LuaValue.valueOf(FabricLoader.getInstance().isModLoaded(modName.checkjstring()))
        ));

        for (ModLibrary lib : BTATweaker.modLibs) {
            LuaValue library = lib.call(modname, env);
            lib.getAliases().forEach((name) -> {
                t.set(name, library);
            });
        }

        env.set("mods", t);
        env.get("package").get("loaded").set("mods", t);

        return t;
    }
}
