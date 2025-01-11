package turing.btatweaker;

import turing.btatweaker.api.IScriptExecutionPoint;
import turing.btatweaker.api.IScriptPreprocessor;
import turing.btatweaker.api.IScriptableEvent;
import turing.btatweaker.api.ModLibrary;

public interface IScriptPropertyHolder {
    void addExecutionPoint(IScriptExecutionPoint executionPoint);

    void addPreprocessor(IScriptPreprocessor<?> preprocessor);

    void addScriptEvent(IScriptableEvent event);

    void addModLibrary(ModLibrary library);
}
