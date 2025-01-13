package turing.btatweaker.lua;

import net.minecraft.core.util.collection.Pair;
import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.Varargs;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptableEvent;
import turing.btatweaker.luapi.LuaEventConnection;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class EventHandler {
    public static final List<EventConnection> EVENT_CONNECTIONS = new ArrayList<>();

    public static IScriptableEvent getEvent(String eventName) {
        Optional<IScriptableEvent> event = BTATweaker.events.stream().filter((e) -> e.getName().equals(eventName)).findFirst();

        if (event.isPresent()) {
            return event.get();
        }

        throw new NullPointerException("Could not find event with name '" + eventName + "'!");
    }

    public static Pair<EventConnection, LuaEventConnection> connectToEvent(LuaFunction function, String eventName) {
        IScriptableEvent event = getEvent(eventName);
        UUID uuid = UUID.randomUUID();

        EventConnection connection = new EventConnection(uuid, event, function);
        LuaEventConnection luaConnection = new LuaEventConnection(event, uuid);

        EVENT_CONNECTIONS.add(connection);
        return Pair.of(connection, luaConnection);
    }

    public static void cancelEvent(String eventName) {
        IScriptableEvent event = getEvent(eventName);

        if (event.isCancelable()) {
            event.setCanceled(true);
        } else {
            throw new IllegalStateException("Attempt to cancel non-cancelable event '" + eventName + "'!");
        }
    }

    public static void fireEvent(String eventName, Varargs varargs) {
        IScriptableEvent event = getEvent(eventName);

        event.fireEvent(varargs);
    }
}
