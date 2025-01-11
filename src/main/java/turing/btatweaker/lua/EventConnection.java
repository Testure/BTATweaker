package turing.btatweaker.lua;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaValue;
import turing.btatweaker.api.IScriptableEvent;

import java.util.UUID;

public class EventConnection {
    private final IScriptableEvent event;
    private final LuaFunction function;
    private final boolean isOneTime;
    private final UUID uuid;

    public EventConnection(UUID uuid, IScriptableEvent event, LuaValue value, boolean isOneTime) {
        this.event = event;
        this.function = value.checkfunction();
        this.isOneTime = isOneTime;
        this.uuid = uuid;
    }

    public EventConnection(UUID uuid, IScriptableEvent event, LuaValue value) {
        this(uuid, event, value, false);
    }

    public UUID getUuid() {
        return uuid;
    }

    public IScriptableEvent getEvent() {
        return event;
    }

    public LuaFunction getFunction() {
        return function;
    }

    public boolean isOneTime() {
        return isOneTime;
    }
}
