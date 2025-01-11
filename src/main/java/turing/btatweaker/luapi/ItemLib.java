package turing.btatweaker.luapi;

import net.minecraft.core.item.Item;
import net.minecraft.core.item.ItemStack;
import org.luaj.vm2.LuaError;
import org.luaj.vm2.LuaValue;
import org.luaj.vm2.Varargs;
import org.luaj.vm2.lib.TwoArgFunction;
import org.luaj.vm2.lib.VarArgFunction;

public class ItemLib extends TwoArgFunction {
    @Override
    public LuaValue call(LuaValue modname, LuaValue env) {
        ItemFunc itemFunction = new ItemFunc();
        env.set("item", itemFunction);
        env.get("package").get("loaded").set("item", itemFunction);
        return itemFunction;
    }

    protected static final class ItemFunc extends VarArgFunction {
        @Override
        public LuaValue invoke(Varargs arg) {
            int itemId = -1;
            if (arg.isnumber(1)) {
                itemId = arg.checkint(1);
            } else if (arg.isstring(1)) {
                String name = arg.checkjstring(1);
                for (Item item : Item.itemsList) {
                    if (item != null && (item.getKey().equals(name) || item.namespaceID.toString().equals(name))) {
                        itemId = item.id;
                        break;
                    }
                }
            }
            int meta = -1;
            if (arg.isnumber(2)) {
                meta = arg.checkint(2);
            }
            if (itemId >= 0) {
                Item item = Item.itemsList[itemId];
                if (item == null) {
                    throw new LuaError("could not find item with id '" + itemId + "'");
                }
                ItemStack stack = item.getDefaultStack();
                if (meta >= 0) stack.setMetadata(meta);
                return new LuaItem(stack);
            } else {
                throw new LuaError("could not find item with id '" + itemId + "'");
            }
        }
    }
}
