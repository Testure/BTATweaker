package turing.btatweaker;

import turing.btatweaker.lua.ScriptGlobals;

public interface BTATweakerEntrypoint {
    default void addGlobals(ScriptGlobals globals) {

    }

    default void init(IBTATweaker registry) {

    }
}
