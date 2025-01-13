package turing.btatweaker.mixin;

import net.minecraft.client.Minecraft;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.btatweaker.BTATweaker;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/data/DataLoader;loadDataPacks(Lnet/minecraft/core/MinecraftAccessor;)V", shift = At.Shift.AFTER))
    public void afterRecipesReady(CallbackInfo ci) {
        BTATweaker.manager.executeScripts(BTATweaker.PROCESS_RECIPES);
    }
}
