package turing.btatweaker.luapi;

import net.fabricmc.loader.api.FabricLoader;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.ModLibrary;

public class ModLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();

        t.set("isModLoaded", new IsModLoaded());

        for (ModLibrary lib : BTATweaker.modLibs) {
            LuaValue library = lib.call(modname, env);
            lib.getValidNames().forEach((name) -> {
                t.set(name, library);
            });
        }

        env.set("mods", t);
        env.get("package").get("loaded").set("mods", t);

        return t;
    }

    protected static final class IsModLoaded extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            return LuaValue.valueOf(FabricLoader.getInstance().isModLoaded(arg.checkjstring()));
        }
    }
}
