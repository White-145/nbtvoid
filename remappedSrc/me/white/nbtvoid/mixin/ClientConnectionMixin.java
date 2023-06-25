package me.white.nbtvoid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.white.nbtvoid.Config;
import me.white.nbtvoid.VoidController;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.listener.PacketListener;
import net.minecraft.network.packet.Packet;

@Mixin(ClientConnection.class)
public class ClientConnectionMixin {
    @Inject(at = @At("HEAD"), method = "handlePacket")
    private static void handlePacket(Packet<?> packet, PacketListener listener, CallbackInfo ci) {
        if (Config.getInstance().getIsEnabled()) VoidController.fromPacket(packet);
    }
}