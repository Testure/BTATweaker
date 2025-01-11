package turing.btatweaker.impl;

import turing.btatweaker.api.IScriptExecutionPoint;
import turing.btatweaker.api.IScriptPreprocessor;
import turing.btatweaker.lua.LuaScript;

public class ExecutionPointPreprocessor implements IScriptPreprocessor<Void> {
    private final IScriptExecutionPoint executionPoint;

    public ExecutionPointPreprocessor(IScriptExecutionPoint executionPoint) {
        this.executionPoint = executionPoint;
    }

    @Override
    public String getName() {
        return executionPoint.getName();
    }

    @Override
    public boolean handle(LuaScript script, Void argument) {
        script.executionPoint = executionPoint;
        return true;
    }
}
