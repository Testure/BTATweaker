package turing.btatweaker.luapi;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptableEvent;
import turing.btatweaker.lua.EventHandler;
import turing.docs.*;

@Documented
@Library(value = "events", className = "Events")
@Description("Provides functions for connecting to and dealing with events.")
public class EventLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        LuaTable t = new LuaTable();

        t.set("connect", new Connect());
        t.set("eventExists", new EventExists());

        env.set("events", t);
        env.get("package").get("loaded").set("events", t);

        return t;
    }

    @Function(value = "eventExists", returnType = "boolean", arguments = @Argument(value = "string", name = "eventName"), examples = @FunctionExample(value = "\"onGameLoad\""))
    @Description("Checks if an event with the given name exists or not.")
    private static final class EventExists extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue arg) {
            for (IScriptableEvent event : BTATweaker.events) {
                if (event.getName().equals(arg.checkjstring())) {
                    return TRUE;
                }
            }
            return FALSE;
        }
    }

    @Function(value = "connect", returnType = "EventConnection", arguments = {
            @Argument(value = "string", name = "eventName", description = "The name of the event to connect to"),
            @Argument(value = "function", name = "callback", description = "The function to be called when the event is fired.")
    }, examples = @FunctionExample(value = {
            "\"onGameLoad\"",
            "function(argument)\n\tprint(argument)\nend"
    }, returnValues = "connection"))
    @Description("Connects the given callback to an event.")
    private static final class Connect extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue eventName, LuaValue function) {
            LuaFunction callback = function.checkfunction();
            String name = eventName.checkjstring();
            return EventHandler.connectToEvent(callback, name).getRight();
        }
    }
}
