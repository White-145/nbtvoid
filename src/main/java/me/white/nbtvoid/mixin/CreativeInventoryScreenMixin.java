package me.white.nbtvoid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.white.nbtvoid.Config;
import me.white.nbtvoid.ModdedCreativeTab;
import me.white.nbtvoid.ModdedCreativeTab.Type;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin {
    @Shadow private TextFieldWidget searchBox;
    @Shadow private static ItemGroup selectedTab;
    @Shadow private float scrollPosition;

    @Inject(method = "setSelectedTab(Lnet/minecraft/item/ItemGroup;)V", at = @At("TAIL"))
    private void setSelectedTab(ItemGroup group, CallbackInfo info) {
        if (ModdedCreativeTab.getType(group) == Type.VOID) {
            searchBox.setText(Config.getInstance().getDefaultSearchQuery());
            searchBox.setMaxLength(256);
        }
    }

    @Inject(method = "search()V", at = @At("HEAD"), cancellable = true)
    private void search(CallbackInfo info) {
        if (ModdedCreativeTab.getType(selectedTab) == Type.VOID) {
            info.cancel();
            
            ModdedCreativeTab moddedTab = ModdedCreativeTab.moddedTabs.get(selectedTab);

            String query = searchBox.getText();
            CreativeScreenHandler handler = ((CreativeInventoryScreen)(Object)this).getScreenHandler();
            DefaultedList<ItemStack> itemList = handler.itemList;

            itemList.clear();
            itemList.addAll(moddedTab.getSearchProvider().apply(query));

            scrollPosition = 0.0f;
            handler.scrollItems(0.0f);
        }
    }
}
