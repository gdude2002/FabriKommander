package me.gserv.fabrikommander.mixin;

import me.gserv.fabrikommander.data.PlayerDataManager;
import net.minecraft.network.ClientConnection;
import net.minecraft.server.PlayerManager;
import net.minecraft.server.network.ServerPlayerEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerManager.class)
public class PlayerManagerMixin {
    @Inject(at = @At("RETURN"), method = "onPlayerConnect")
    private void onPlayerConnect(ClientConnection connection, ServerPlayerEntity player, CallbackInfo ci) {
        PlayerDataManager.INSTANCE.playerJoined(player);
    }

    @Inject(at = @At("RETURN"), method = "remove")
    private void remove(ServerPlayerEntity player, CallbackInfo ci) {
        PlayerDataManager.INSTANCE.playerLeft(player);
    }
}
