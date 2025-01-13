package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;

public class ExecutionPointAfterGameStart implements IScriptExecutionPoint {
    @Override
    public String getName() {
        return "afterGameStart";
    }

    @Override
    public boolean appliesToFolderName(String folderName) {
        return folderName.equals(getName()) || folderName.equals("afterStart");
    }

    @Override
    public boolean isReloadable() {
        return true;
    }
}
