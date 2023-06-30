package me.white.nbtvoid.mixin;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import me.white.nbtvoid.Config;
import me.white.nbtvoid.ModdedCreativeTab;
import me.white.nbtvoid.ModdedCreativeTab.Type;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.text.Text;
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

            ItemStack infoItem = new ItemStack(Items.PAPER, 1);
            infoItem.setCustomName(Text.translatable("itemGroup.nbtvoid.infoItem"));
            infoItem.setSubNbt("CustomCreativeLock", new NbtCompound());

            itemList.clear();
            itemList.add(infoItem);
            new Thread(new ModdedCreativeTab.AsyncSearcher(handler, moddedTab.getSearchProvider(), query)).start();

            scrollPosition = 0.0f;
        }
    }

    @Inject(method = "init()V", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        MinecraftClient instance = MinecraftClient.getInstance();
        if (!instance.interactionManager.hasCreativeInventory()) return;
        if (ModdedCreativeTab.getType(selectedTab) == Type.VOID) {
            searchBox.setText(Config.getInstance().getDefaultSearchQuery());
            searchBox.setMaxLength(256);
        }
    }
}
