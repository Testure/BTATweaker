package turing.btatweaker.luapi;

import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import turing.btatweaker.api.IScriptableEvent;
import turing.btatweaker.lua.EventHandler;
import turing.docs.Function;
import turing.docs.LuaClass;
import turing.docs.Description;
import turing.docs.Method;

import java.util.UUID;
import java.util.stream.Collectors;

@LuaClass(value = "EventConnection", constructor = @Function(""))
@Description("A connection to an Event")
public class LuaEventConnection extends turing.btatweaker.luapi.LuaClass {
    private final IScriptableEvent event;
    private final UUID uuid;

    public LuaEventConnection(IScriptableEvent event, UUID uuid) {
        super();

        this.event = event;
        this.uuid = uuid;

        rawset("Disconnect", new Disconnect());
    }

    public IScriptableEvent getEvent() {
        return event;
    }

    @Method("Disconnect")
    @Description("Disables the connection so that the callback is not called again.")
    protected final class Disconnect extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue self) {
            EventHandler.EVENT_CONNECTIONS.removeAll(EventHandler.EVENT_CONNECTIONS.stream().filter((c) -> c.getUuid().compareTo(uuid) == 0).collect(Collectors.toList()));
            return NIL;
        }
    }
}
