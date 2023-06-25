package me.white.nbtvoid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;

@Mixin(Advancement.Builder.class)
public interface AdvancementBuilderMixin {
    @Accessor
    public AdvancementDisplay getDisplay();
}
