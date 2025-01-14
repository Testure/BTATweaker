package turing.btatweaker.luapi;

import net.minecraft.core.item.IItemConvertible;
import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.lib.OneArgFunction;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.util.LuaFunctionFactory;

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

        rawset("WithMetadata", LuaFunctionFactory.oneArgBuilderMethod((self, arg) -> {
            int metadata = arg.checkint();

            self.rawset("Metadata", metadata);
            ((LuaItem) self).stack.setMetadata(metadata);
        }));
        rawset("GetTranslationKey", LuaFunctionFactory.zeroArgMethod((self) -> {
            LuaItem luaItem = (LuaItem) self;

            return LuaValue.valueOf(luaItem.realItem.getLanguageKey(luaItem.stack));
        }));
        /*rawset("WithTag", new WithTag());
        rawset("HasData", LuaFunctionFactory.zeroArgMethod((self) ->
                LuaValue.valueOf(self.get("Tag") != NIL)
        ));*/

        rawset("Amount", stack.stackSize);
        rawset("Id", realItem.id);
        /*removed until 7.2 is no longer supported
        rawset("Namespace", realItem.namespaceID.namespace);
        rawset("ModId", realItem.namespaceID.namespace);
        rawset("RegistryName", realItem.namespaceID.toString());*/
        rawset("TranslationKey", realItem.getKey());
        rawset("Metadata", stack.getMetadata());
        //rawset("Tag", new LuaTag(stack.getData(), stack::setData));
    }

    public LuaItem(Item item) {
        this(item.getDefaultStack());
    }

    @Override
    public TwoArgFunction getMulFunction() {
        return LuaFunctionFactory.oneArgBuilderMethod((self, arg) -> {
            int amount = arg.checkint();

            self.rawset("Amount", amount);
            ((LuaItem) self).stack.stackSize = amount;
        });
    }

    @Override
    public TwoArgFunction getEqualFunction() {
        return LuaFunctionFactory.oneArgMethod((self, arg) -> {
            if (arg.istable() && arg instanceof LuaItem) {
                LuaItem other = (LuaItem) arg;
                return LuaValue.valueOf(other.getStack().isStackEqual(((LuaItem) self).getStack()));
            }
            return FALSE;
        });
    }

    @Override
    public OneArgFunction getLenFunction() {
        return LuaFunctionFactory.zeroArgMethod((self) ->
                self.rawget("Amount")
        );
    }

    @Override
    public OneArgFunction getToStringFunction() {
        return LuaFunctionFactory.zeroArgMethod((self) ->
                LuaValue.valueOf(stack.toString() + " [" + stack.getData() + "]")
        );
    }

    public static boolean isLuaItem(LuaValue value) {
        return value.istable() && value instanceof LuaItem;
    }

    public boolean isItemEqual(ItemStack other) {
        return other.isItemEqual(stack);
    }

    public ItemStack getStack() {
        return stack;
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
        return getStack();
    }

    @Override
    public int getAmount() {
        return stack.stackSize;
    }

    @Override
    public List<ItemStack> resolve() {
        return Collections.singletonList(stack);
    }

    /*protected static final class WithTag extends TwoArgFunction {
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
    }*/
}
