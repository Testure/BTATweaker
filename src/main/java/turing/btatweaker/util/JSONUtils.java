package turing.btatweaker.util;

import com.google.gson.*;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class JSONUtils {
    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(LuaValue.class, LuaValueAdapter.INSTANCE).registerTypeAdapter(LuaTable.class, LuaTableAdapter.INSTANCE).setPrettyPrinting().create();

    public static JsonElement convertLuaToJSON(LuaValue value) {
        return GSON.toJsonTree(value, LuaValue.class);
    }

    public static LuaTable convertJSONToLua(JsonObject json) {
        return GSON.fromJson(json, LuaTable.class);
    }

    public static final class LuaTableAdapter implements JsonDeserializer<LuaTable>, JsonSerializer<LuaTable> {
        public static final LuaTableAdapter INSTANCE = new LuaTableAdapter();

        @Override
        public LuaTable deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {

            if (json.isJsonArray()) {
                JsonArray array = json.getAsJsonArray();
                LuaTable t = new LuaTable();

                for (JsonElement element : array) {
                    t.add(LuaValueAdapter.INSTANCE.deserialize(element, LuaValue.class, context));
                }

                return t;
            } else if (json.isJsonObject()) {
                JsonObject object = json.getAsJsonObject();
                LuaTable t = new LuaTable();

                for (Map.Entry<String, JsonElement> entry : object.entrySet()) {
                    t.set(entry.getKey(), LuaValueAdapter.INSTANCE.deserialize(entry.getValue(), LuaValue.class, context));
                }

                return t;
            }

            throw new IllegalArgumentException("Could not convert JSON value to a lua table!");
        }

        @Override
        public JsonElement serialize(LuaTable src, Type typeOfSrc, JsonSerializationContext context) {
            boolean hasStringKeys = false;
            Map<String, JsonElement> elements = new HashMap<>();

            Varargs args = src.next(LuaValue.NIL);
            while (args != LuaValue.NIL) {
                LuaValue key = args.arg(1);
                LuaValue value = args.arg(2);

                if (!key.isnumber()) {
                    hasStringKeys = true;
                }

                String mapKey = key.isstring() ? key.checkjstring() : key.tostring().checkjstring();
                elements.put(mapKey, LuaValueAdapter.INSTANCE.serialize(value, LuaValue.class, context));

                args = src.next(key);
            }

            if (hasStringKeys) {
                JsonObject object = new JsonObject();

                for (Map.Entry<String, JsonElement> entry : elements.entrySet()) {
                    object.add(entry.getKey(), entry.getValue());
                }

                return object;
            } else {
                JsonArray array = new JsonArray();

                for (Map.Entry<String, JsonElement> entry : elements.entrySet()) {
                    array.add(entry.getValue());
                }

                return array;
            }
        }
    }

    public static final class LuaValueAdapter implements JsonDeserializer<LuaValue>, JsonSerializer<LuaValue> {
        public static final LuaValueAdapter INSTANCE = new LuaValueAdapter();

        @Override
        public LuaValue deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            if (json.isJsonArray() || json.isJsonObject()) {
                return LuaTableAdapter.INSTANCE.deserialize(json, LuaTable.class, context);
            }

            if (json.isJsonPrimitive()) {
                JsonPrimitive primitive = json.getAsJsonPrimitive();

                if (primitive.isJsonNull()) {
                    return LuaValue.NIL;
                } else if (primitive.isBoolean()) {
                    return LuaValue.valueOf(primitive.getAsBoolean());
                } else if (primitive.isString()) {
                    return LuaValue.valueOf(primitive.getAsString());
                } else if (primitive.isNumber()) {
                    Number number = primitive.getAsNumber();
                    if (number.doubleValue() != number.longValue()) {
                        return LuaValue.valueOf(number.doubleValue());
                    } else if (number.longValue() > Integer.MAX_VALUE || number.longValue() < Integer.MIN_VALUE) {
                        return LuaValue.valueOf(number.longValue());
                    } else {
                        return LuaValue.valueOf(number.intValue());
                    }
                }
            }

            throw new IllegalArgumentException("Could not convert JSON value to lua value!");
        }

        @Override
        public JsonElement serialize(LuaValue src, Type typeOfSrc, JsonSerializationContext context) {
            if (src.istable()) {
                return LuaTableAdapter.INSTANCE.serialize(src.checktable(), LuaTable.class, context);
            }

            if (src.isnil()) {
                return JsonNull.INSTANCE;
            } else if (src.isboolean()) {
                return new JsonPrimitive(src.checkboolean());
            } else if (src.isstring()) {
                return new JsonPrimitive(src.checkjstring());
            } else if (src.islong()) {
                return new JsonPrimitive(src.checklong());
            } else if (src.isint()) {
                return new JsonPrimitive(src.checkint());
            } else if (src.isnumber()) {
                return new JsonPrimitive(src.checkdouble());
            }

            throw new IllegalArgumentException("Could not convert lua value into JSON value!");
        }
    }
}
