package turing.btatweaker.lua;

import org.luaj.vm2.Globals;
import org.luaj.vm2.LuaValue;

import java.util.ArrayList;
import java.util.List;

public class ScriptGlobals {
    private final List<LuaValue> libs = new ArrayList<>();

    public void addGlobalLib(LuaValue lib) {
        libs.add(lib);
    }

    public void load(Globals globals) {
        for (LuaValue lib : libs) {
            globals.load(lib);
        }
    }
}
