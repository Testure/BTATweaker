package turing.btatweaker.luapi;

import org.luaj.vm2.LuaFunction;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.api.IScriptableEvent;
import turing.btatweaker.lua.EventHandler;

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

    private static final class Connect extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue eventName, LuaValue function) {
            LuaFunction callback = function.checkfunction();
            String name = eventName.checkjstring();
            return EventHandler.connectToEvent(callback, name).getRight();
        }
    }
}
