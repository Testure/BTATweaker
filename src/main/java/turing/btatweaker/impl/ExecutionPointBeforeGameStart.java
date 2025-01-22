package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;
import turing.docs.Description;
import turing.docs.Documented;
import turing.docs.ExecutionPoint;

@Documented
@ExecutionPoint("BeforeGameStart")
@Description("Runs during BeforeGameStart")
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
