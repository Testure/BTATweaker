package turing.btatweaker.mixin;

import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import turing.btatweaker.BTATweaker;

@Mixin(value = MinecraftServer.class, remap = false)
public class MinecraftServerMixin {
    @Inject(method = "startServer", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/data/DataLoader;loadDataPacks(Lnet/minecraft/core/MinecraftAccessor;)V", shift = At.Shift.AFTER))
    public void afterRecipesReady(CallbackInfoReturnable<Boolean> ci) {
        BTATweaker.manager.executeScripts(BTATweaker.PROCESS_RECIPES);
    }

    @Inject(method = "startServer", at = @At("TAIL"))
    public void logErrors(CallbackInfoReturnable<Boolean> cir) {
        BTATweaker.manager.log(null);
    }
}
