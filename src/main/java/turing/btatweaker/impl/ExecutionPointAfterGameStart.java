package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;
import turing.docs.Description;
import turing.docs.Documented;
import turing.docs.ExecutionPoint;

@Documented
@ExecutionPoint("AfterGameStart")
@Description("Runs during AfterGameStart")
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
