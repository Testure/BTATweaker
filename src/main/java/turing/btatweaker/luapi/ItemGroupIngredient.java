package turing.btatweaker.luapi;

import net.minecraft.core.data.registry.Registries;
import net.minecraft.core.item.ItemStack;
import org.luaj.vm2.lib.TwoArgFunction;
import turing.btatweaker.util.LuaFunctionFactory;
import turing.docs.*;

import java.util.List;
import java.util.stream.Collectors;

@turing.docs.LuaClass(value = "ItemGroup", extend = "Ingredient", constructor = @Function(value = "itemgroup", returnType = "ItemGroup", arguments = @Argument(value = "string", name = "itemGroupName"), examples = @FunctionExample(returnValues = "ironOres", value = "\"minecraft:iron_ores\"")))
@Property(name = "Amount", value = "number")
@Property(name = "ItemGroup", value = "string")
@Description("Represents an ItemGroup.")
public class ItemGroupIngredient extends LuaClass implements IIngredient {
    private int amount;
    protected final String itemGroup;

    public ItemGroupIngredient(String itemGroup, int amount) {
        super();
        this.itemGroup = itemGroup;
        this.amount = amount;

        rawset("Amount", amount);
        rawset("ItemGroup", itemGroup);
    }

    public ItemGroupIngredient(String itemGroup) {
        this(itemGroup, 1);
    }

    @Override
    public int getAmount() {
        return amount;
    }

    @Override
    public List<ItemStack> resolve() {
        return Registries.ITEM_GROUPS.getItem(itemGroup).stream().peek((s) -> {
            ItemStack copy = s.copy();
            copy.stackSize *= getAmount();
        }).collect(Collectors.toList());
    }

    @Override
    public TwoArgFunction getMulFunction() {
        return LuaFunctionFactory.oneArgBuilderMethod((self, arg) -> {
                    rawset("Amount", arg.checkint());
                    amount = arg.checkint();
        });
    }
}
