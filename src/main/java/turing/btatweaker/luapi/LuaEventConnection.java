package turing.btatweaker.luapi;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import turing.btatweaker.api.IScriptableEvent;
import turing.btatweaker.lua.ScriptManager;

import java.util.UUID;
import java.util.stream.Collectors;

public class LuaEventConnection extends LuaClass {
    private final IScriptableEvent event;
    private final UUID uuid;

    public LuaEventConnection(IScriptableEvent event, UUID uuid) {
        super();

        this.event = event;
        this.uuid = uuid;

        rawset("Disconnect", new Disconnect());
    }

    protected final class Disconnect extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue self) {
            ScriptManager.EVENT_CONNECTIONS.removeAll(ScriptManager.EVENT_CONNECTIONS.stream().filter((c) -> c.getUuid().compareTo(uuid) == 0).collect(Collectors.toList()));
            return NIL;
        }
    }
}
