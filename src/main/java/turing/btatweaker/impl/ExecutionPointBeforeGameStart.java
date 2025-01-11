package turing.btatweaker.impl;

import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptExecutionPoint;

public class ExecutionPointBeforeGameStart implements IScriptExecutionPoint {
    public ExecutionPointBeforeGameStart() {
        BTATweaker.executionPoints.add(this);
    }

    @Override
    public String getName() {
        return "beforeGameStart";
    }

    @Override
    public boolean appliesToFolderName(String folderName) {
        return folderName.equals(getName()) || folderName.equals("beforeStart");
    }
}
