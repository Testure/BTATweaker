package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;

public class ExecutionPointBeforeGameStart implements IScriptExecutionPoint {
    @Override
    public String getName() {
        return "beforeGameStart";
    }

    @Override
    public boolean appliesToFolderName(String folderName) {
        return folderName.equals(getName()) || folderName.equals("beforeStart");
    }
}
