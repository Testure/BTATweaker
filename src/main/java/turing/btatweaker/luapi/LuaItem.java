package turing.btatweaker.luapi;

import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.luaj.vm2.LuaTable;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

public class LuaItem extends LuaClass implements IItemConvertible, IIngredient {
    private final Item realItem;
    private final ItemStack stack;

    public LuaItem(ItemStack item) {
        super();

        this.realItem = item.getItem();
        this.stack = item;

        rawset("WithMetadata", new WithMetadata());
        rawset("GetTranslationKey", new GetTranslationKey());
        rawset("WithTag", new WithTag());
        rawset("HasData", new OneArgFunction() {
            @Override
            public LuaValue call(LuaValue self) {
                return LuaValue.valueOf(self.get("Tag") != NIL);
            }
        });

        rawset("Amount", stack.stackSize);
        rawset("Id", realItem.id);
        /*removed until 7.2 is no longer supported
        rawset("Namespace", realItem.namespaceID.namespace);
        rawset("ModId", realItem.namespaceID.namespace);
        rawset("RegistryName", realItem.namespaceID.toString());*/
        rawset("TranslationKey", realItem.getKey());
        rawset("Metadata", stack.getMetadata());
        rawset("Tag", new LuaTag(stack.getData(), stack::setData));
    }

    @Override
    public TwoArgFunction getMulFunction() {
        return new MulFunction();
    }

    @Override
    public TwoArgFunction getEqualFunction() {
        return new EqualFunction();
    }

    @Override
    public TwoArgFunction getGreaterThanEqualFunction() {
        return new LessThanEqualFunction(true);
    }

    @Override
    public TwoArgFunction getGreaterThanFunction() {
        return new LessThanFunction(true);
    }

    @Override
    public TwoArgFunction getLessThanEqualFunction() {
        return new LessThanEqualFunction(false);
    }

    @Override
    public TwoArgFunction getLessThanFunction() {
        return new LessThanFunction(false);
    }

    @Override
    public OneArgFunction getLenFunction() {
        return new LenFunction();
    }

    @Override
    public OneArgFunction getToStringFunction() {
        return new ToStringFunction();
    }

    public LuaItem(Item item) {
        this(item.getDefaultStack());
    }

    public static boolean isLuaItem(LuaValue value) {
        return value.istable() && value instanceof LuaItem;
    }

    public boolean isItemEqual(ItemStack other) {
        return other.isItemEqual(stack);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        LuaItem luaItem = (LuaItem) o;
        return realItem.id == luaItem.realItem.id;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(realItem.id);
    }

    @Override
    public Item asItem() {
        return realItem;
    }

    @Override
    public ItemStack getDefaultStack() {
        return stack;
    }

    @Override
    public int getAmount() {
        return stack.stackSize;
    }

    @Override
    public List<ItemStack> resolve() {
        return Collections.singletonList(stack);
    }

    protected static final class WithTag extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue self, LuaValue tag) {
            LuaTag luaTag;
            if (tag instanceof LuaTag) {
                luaTag = (LuaTag) tag;
            } else {
                luaTag = LuaTag.getLuaTagFromTable(tag);
            }
            ((LuaItem) self).stack.setData(luaTag.getRealTag());
            self.rawset("Tag", luaTag);
            return self;
        }
    }

    protected static final class WithMetadata extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue self, LuaValue arg) {
            int metadata = arg.checkint();
            self.rawset("Metadata", metadata);
            ((LuaItem) self).stack.setMetadata(metadata);
            return self;
        }
    }

    protected static final class GetTranslationKey extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue self) {
            LuaItem luaItem = (LuaItem) self;
            return LuaValue.valueOf(luaItem.realItem.getLanguageKey(luaItem.stack));
        }
    }

    protected static final class MulFunction extends TwoArgFunction {
        @Override
        public LuaValue call(LuaValue self, LuaValue arg) {
            int amount = arg.checkint();
            self.rawset("Amount", amount);
            ((LuaItem) self).stack.stackSize = amount;
            return self;
        }
    }

    protected static class EqualFunction extends TwoArgFunction {
        public LuaValue baseCall(LuaValue self, LuaValue arg) {
            if (arg.istable() && arg instanceof LuaItem) {
                LuaItem other = (LuaItem) arg;
                return LuaValue.valueOf(other.equals(self));
            }
            return FALSE;
        }

        @Override
        public LuaValue call(LuaValue self, LuaValue arg) {
            LuaValue baseValue = baseCall(self, arg);
            if (baseValue.checkboolean()) {
                return LuaValue.valueOf(self.rawget("Amount").checkint() == arg.rawget("Amount").checkint());
            }
            return baseValue;
        }
    }

    protected static final class LessThanFunction extends EqualFunction {
        private final boolean inverse;

        public LessThanFunction(boolean inverse) {
            this.inverse = inverse;
        }

        @Override
        public LuaValue call(LuaValue self, LuaValue arg) {
            LuaValue baseValue = baseCall(self, arg);
            if (baseValue.checkboolean()) {
                boolean v = self.rawget("Amount").checkint() < arg.rawget("Amount").checkint();
                return LuaValue.valueOf(inverse != v);
            }
            return baseValue;
        }
    }

    protected static final class LessThanEqualFunction extends EqualFunction {
        private final boolean inverse;

        public LessThanEqualFunction(boolean inverse) {
            this.inverse = inverse;
        }

        @Override
        public LuaValue call(LuaValue self, LuaValue arg) {
            LuaValue baseValue = baseCall(self, arg);
            if (baseValue.checkboolean()) {
                boolean v = self.rawget("Amount").checkint() <= arg.rawget("Amount").checkint();
                return LuaValue.valueOf(inverse != v);
            }
            return baseValue;
        }
    }

    protected static final class LenFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue self) {
            return self.rawget("Amount");
        }
    }

    protected final class ToStringFunction extends OneArgFunction {
        @Override
        public LuaValue call(LuaValue self) {
            return LuaValue.valueOf(stack.toString() + " [" + stack.getData() + "]");
        }
    }
}
