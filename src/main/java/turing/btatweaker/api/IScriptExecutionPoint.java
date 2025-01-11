package turing.btatweaker.api;

import turing.btatweaker.lua.LuaScript;

public interface IScriptExecutionPoint {
    String getName();

    default boolean shouldExecute(LuaScript script) {
        return script.executionPoint.getName().equals(getName());
    }

    default boolean appliesToFolderName(String folderName) {
        return folderName.equals(getName());
    }

    default boolean isReloadable() {
        return false;
    }
}
