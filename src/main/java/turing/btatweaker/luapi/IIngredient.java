package turing.btatweaker.luapi;

import net.minecraft.core.item.ItemStack;

import java.util.List;

public interface IIngredient {
    int getAmount();

    List<ItemStack> resolve();
}
