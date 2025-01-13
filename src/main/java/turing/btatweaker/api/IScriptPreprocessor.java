package turing.btatweaker.api;

import turing.btatweaker.lua.LuaScript;

public interface IScriptPreprocessor<T> {
    String getName();

    default T intrepretArgument(String arg) {
        return null;
    }

    default boolean handle(LuaScript script, T argument) {
        return true;
    }
}
