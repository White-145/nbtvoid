package me.white.nbtvoid.mixin;

import me.white.nbtvoid.NbtVoid;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin {
    @Inject(at = @At("TAIL"), method = "setNbt")
    private void setNbt(NbtCompound nbt, CallbackInfo ci) {
        ItemStack stack = (ItemStack)(Object)this;
        if (!NbtVoid.VOID.isLocked && stack.hasNbt()) {
            NbtVoid.VOID.add(stack);
        }
    }
}
