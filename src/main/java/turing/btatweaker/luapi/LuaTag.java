package turing.btatweaker.luapi;

import com.mojang.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.ThreeArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.util.LuaFunctionFactory;

import java.util.function.Consumer;

public class LuaTag extends LuaClass {
    private final CompoundTag tag;
    private final Consumer<CompoundTag> changeCallback;

    public LuaTag(CompoundTag tag, @Nullable Consumer<CompoundTag> changeCallback) {
        super();
        this.tag = tag;
        this.changeCallback = changeCallback;
        rawset("PutBoolean", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                boolean val = value.checkboolean();
                tag.putBoolean(key, val);
            }
        });
        rawset("PutByte", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                byte val = (byte) value.checkint();
                tag.putByte(key, val);
            }
        });
        rawset("PutShort", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                short val = (short) value.checkint();
                tag.putShort(key, val);
            }
        });
        rawset("PutInteger", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                int val = value.checkint();
                tag.putInt(key, val);
            }
        });
        rawset("PutLong", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                long val = value.checklong();
                tag.putLong(key, val);
            }
        });
        rawset("PutFloat", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                float val = (float) value.checkdouble();
                tag.putFloat(key, val);
            }
        });
        rawset("PutDouble", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                double val = value.checkdouble();
                tag.putDouble(key, val);
            }
        });
        rawset("PutString", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                String val = value.checkjstring();
                tag.putString(key, val);
            }
        });
        rawset("PutTag", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                LuaTable v = value.checktable();
                LuaTag other;
                if (!(v instanceof LuaTag)) {
                    throw new LuaError("Attempt to pass non-tag value to PutTag");
                } else other = (LuaTag) v;
                tag.put(key, other.getRealTag());
            }
        });
        rawset("PutByteArray", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                LuaTable t = value.checktable();
                byte[] array = new byte[t.length()];
                for (int i = 1; i <= t.length(); i++) {
                    array[i - 1] = (byte) t.checkint(i);
                }
                tag.putByteArray(key, array);
            }
        });
        rawset("PutShortArray", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                LuaTable t = value.checktable();
                short[] array = new short[t.length()];
                for (int i = 1; i <= t.length(); i++) {
                    array[i - 1] = (short) t.checkint(i);
                }
                tag.putShortArray(key, array);
            }
        });
        rawset("PutDoubleArray", new PutFunction() {
            @Override
            protected void putData(String key, LuaValue value) {
                LuaTable t = value.checktable();
                double[] array = new double[t.length()];
                for (int i = 1; i <= t.length(); i++) {
                    array[i - 1] = t.checkdouble(i);
                }
                tag.putDoubleArray(key, array);
            }
        });

        rawset("GetBoolean", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                return LuaValue.valueOf(tag.getBoolean(key));
            }
        });
        rawset("GetByte", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                return LuaValue.valueOf(tag.getByte(key));
            }
        });
        rawset("GetShort", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                return LuaValue.valueOf(tag.getShort(key));
            }
        });
        rawset("GetInteger", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                return LuaValue.valueOf(tag.getInteger(key));
            }
        });
        rawset("GetFloat", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                return LuaValue.valueOf(tag.getFloat(key));
            }
        });
        rawset("GetDouble", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                return LuaValue.valueOf(tag.getDouble(key));
            }
        });
        rawset("GetString", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                return LuaValue.valueOf(tag.getString(key));
            }
        });
        rawset("GetByteArray", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                return LuaValue.valueOf(tag.getByteArray(key));
            }
        });
        rawset("GetShortArray", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                short[] array = tag.getShortArray(key);
                LuaTable t = new LuaTable();
                for (short s : array) {
                    t.add(s);
                }
                return t;
            }
        });
        rawset("GetDoubleArray", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                double[] array = tag.getDoubleArray(key);
                LuaTable t = new LuaTable();
                for (double d : array) {
                    t.add(d);
                }
                return t;
            }
        });
        rawset("GetTag", new GetFunction() {
            @Override
            protected LuaValue getData(String key) {
                CompoundTag compoundTag = tag.getCompound(key);
                return new LuaTag(compoundTag);
            }
        });

        rawset("ContainsKey", LuaFunctionFactory.oneArgMethod((self, key) ->
                LuaValue.valueOf(tag.containsKey(key.checkjstring())))
        );
    }

    public LuaTag(CompoundTag tag) {
        this(tag, null);
    }

    public LuaTag() {
        this(new CompoundTag());
    }

    public CompoundTag getRealTag() {
        return tag;
    }

    @Override
    public OneArgFunction getLenFunction() {
        return LuaFunctionFactory.zeroArgMethod((self) ->
                LuaValue.valueOf(getRealTag().getValues().size())
        );
    }

    @Override
    public OneArgFunction getToStringFunction() {
        return LuaFunctionFactory.zeroArgMethod((self) ->
                LuaValue.valueOf(getRealTag().toString())
        );
    }

    public static LuaTag getLuaTagFromTable(LuaValue table) {
        LuaTable t = table.checktable();
        LuaTag tag = new LuaTag();

        Varargs v = t.next(LuaValue.NIL);
        while (v != NIL) {
            LuaValue key = v.arg(1);
            LuaValue value = v.arg(2);
            key.checkjstring();

            String functionName = "";

            if (value.istable() && value instanceof LuaTag) {
                functionName = "PutTag";
            } else if (value.isstring()) {
                functionName = "PutString";
            } else if (value.isint()) {
                int num = value.checkint();
                if (num <= Byte.MAX_VALUE && num >= Byte.MIN_VALUE) {
                    functionName = "PutByte";
                } else if (num <= Short.MAX_VALUE && num >= Short.MIN_VALUE) {
                    functionName = "PutShort";
                } else {
                    functionName = "PutInteger";
                }
            } else if (value.islong()) {
                functionName = "PutLong";
            } else if (value.isnumber()) {
                double num = value.checkdouble();
                if (num <= Float.MAX_VALUE && num >= Float.MIN_VALUE) {
                    functionName = "PutFloat";
                } else {
                    functionName = "PutDouble";
                }
            } else if (value.istable()) {
                LuaTable tbl = value.checktable();
                LuaValue firstVal = tbl.get(1);

                if (firstVal.isint()) {
                    int num = firstVal.checkint();
                    if (num <= Byte.MAX_VALUE && num >= Byte.MIN_VALUE) {
                        functionName = "PutByteArray";
                    } else {
                        functionName = "PutShortArray";
                    }
                } else if (firstVal.isnumber()) {
                    functionName = "PutDoubleArray";
                }
            }

            if (!functionName.isEmpty()) {
                tag.rawget(functionName).call(tag, key, value);
            }

            v = t.next(key);
        }

        return tag;
    }

    protected abstract class GetFunction extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue self, LuaValue key) {
            String keyName = key.checkjstring();
            if (!(self.checktable() instanceof LuaTag)) {
                throw new LuaError("Attempt to call a method as a function!");
            }
            if (!tag.containsKey(keyName)) {
                return NIL;
            }
            return getData(keyName);
        }

        protected abstract LuaValue getData(String key);
    }

    protected abstract class PutFunction extends ThreeArgFunction {
        @Override
        public LuaValue call(LuaValue self, LuaValue key, LuaValue value) {
            String keyName = key.checkjstring();
            if (!(self.checktable() instanceof LuaTag)) {
                throw new LuaError("Attempt to call a method as a function!");
            }
            if (tag.containsKey(keyName)) {
                throw new LuaError("Tag already contains key '" + keyName + "'!");
            }
            putData(keyName, value);
            if (changeCallback != null) {
                changeCallback.accept(tag);
            }
            return self;
        }

        protected abstract void putData(String key, LuaValue value);
    }
}
