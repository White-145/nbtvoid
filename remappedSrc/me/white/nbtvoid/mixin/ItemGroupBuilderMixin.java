package me.white.nbtvoid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.item.ItemGroup.Builder;
import net.minecraft.item.ItemGroup.Type;

@Mixin(Builder.class)
public interface ItemGroupBuilderMixin {
    @Accessor("type")
    public void setType(Type type);
}