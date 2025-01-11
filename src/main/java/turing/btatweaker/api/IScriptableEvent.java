package turing.btatweaker.api;

import org.luaj.vm2.LuaError;
import org.luaj.vm2.Varargs;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.lua.ScriptManager;

import java.util.stream.Collectors;

public interface IScriptableEvent {
    String getName();

    void setCanceled(boolean canceled);

    boolean isCanceled();

    default boolean isCancelable() {
        return false;
    }

    default void fireEvent(Varargs varargs) {
        ScriptManager.EVENT_CONNECTIONS.removeAll(ScriptManager.EVENT_CONNECTIONS.stream().filter((c) -> c.getEvent().getName().equals(getName())).filter((connection) -> {
            try {
                connection.getFunction().invoke(varargs);
            } catch (LuaError error) {
                BTATweaker.manager.catchError(error);
            }
            return connection.isOneTime();
        }).collect(Collectors.toList()));
    }
}
