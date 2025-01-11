package turing.btatweaker.mixin;

import net.minecraft.client.Minecraft;
import net.minecraft.client.world.WorldClient;
import net.minecraft.core.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import turing.btatweaker.BTATweaker;
import turing.btatweaker.lua.ScriptManager;

@Mixin(value = Minecraft.class, remap = false)
public class MinecraftMixin {
    @Inject(method = "startGame", at = @At(value = "INVOKE", target = "Lnet/minecraft/core/data/DataLoader;loadDataPacks(Lnet/minecraft/core/MinecraftAccessor;)V", shift = At.Shift.AFTER))
    public void afterRecipesReady(CallbackInfo ci) {
        BTATweaker.manager.executeScripts(ScriptManager.PROCESS_RECIPES);
    }

    @Inject(method = "changeWorld(Lnet/minecraft/client/world/WorldClient;Ljava/lang/String;Lnet/minecraft/core/entity/player/Player;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/render/LightmapHelper;setup()V", shift = At.Shift.AFTER))
    public void postErrors(WorldClient world, String loadingTitle, Player player, CallbackInfo ci) {
        if (world != null) {
            BTATweaker.manager.log(world);
        }
    }
}
