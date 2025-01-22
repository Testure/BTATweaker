package turing.btatweaker.luapi;

import net.fabricmc.loader.api.FabricLoader;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.ModLibrary;
import turing.docs.*;

@Documented
@Library(value = "mods", className = "Mods")
@Description("Provides access to addon libraries.")
public class ModLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();

        t.set("isModLoaded", new IsModLoaded());

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

    @Function(value = "isModLoaded", returnType = "boolean", arguments = @Argument(value = "string", name = "modId"))
    @Description({"Checks if a mod with the given id is currently loaded.", "This check is done on whatever side the script is currently being run on,", "so a client side mod will only be detected on the client."})
    private static final class IsModLoaded extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue modName) {
            return LuaValue.valueOf(FabricLoader.getInstance().isModLoaded(modName.checkjstring()));
        }
    }
}
