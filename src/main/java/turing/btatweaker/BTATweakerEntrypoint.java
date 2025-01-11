package turing.btatweaker;

import turing.btatweaker.lua.LibGatherer;

public interface BTATweakerEntrypoint {
    void addLibs(LibGatherer gatherer);

    void init(IScriptPropertyHolder registry);
}
