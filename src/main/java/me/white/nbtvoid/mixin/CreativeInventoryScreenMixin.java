package me.white.nbtvoid.mixin;

import me.white.nbtvoid.Config;
import me.white.nbtvoid.NbtVoid;
import me.white.nbtvoid.util.SearchProvider;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import org.spongepowered.asm.mixin.injection.At;

import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.client.gui.widget.TextFieldWidget;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.util.collection.DefaultedList;

import java.util.*;

@Mixin(CreativeInventoryScreen.class)
public abstract class CreativeInventoryScreenMixin {
    @Shadow private TextFieldWidget searchBox;
    @Shadow private static ItemGroup selectedTab;
    @Shadow private float scrollPosition;

    @Inject(method = "init()V", at = @At("TAIL"))
    private void init(CallbackInfo info) {
        if (selectedTab == NbtVoid.VOID_GROUP) {
            searchBox.setMaxLength(256);
            searchBox.setText(Config.getInstance().getDefaultSearchQuery());
        }
    }

    @Inject(method = "setSelectedTab(Lnet/minecraft/item/ItemGroup;)V", at = @At("TAIL"))
    private void setSelectedTab(ItemGroup group, CallbackInfo info) {
        if (group == NbtVoid.VOID_GROUP) {
            searchBox.setMaxLength(256);
            searchBox.setText(Config.getInstance().getDefaultSearchQuery());
        }
    }

    @Inject(method = "search()V", at = @At("HEAD"), cancellable = true)
    private void search(CallbackInfo info) {
        if (selectedTab == NbtVoid.VOID_GROUP) {
            info.cancel();

            String query = searchBox.getText();
            CreativeScreenHandler handler = ((CreativeInventoryScreen)(Object)this).getScreenHandler();
            DefaultedList<ItemStack> itemList = handler.itemList;

            itemList.clear();

            ArrayList<ItemStack> items = new ArrayList<>(new SearchProvider(query).findAll(NbtVoid.VOID.getItems()));

            switch (Config.getInstance().getSortType()) {
                case ALPHABETIC -> {
                    items.sort(Comparator.comparing((stack) -> stack.getName().getString()));
                }
                case STORE_DATE -> {
                    Collections.reverse(items);
                }
            }
            itemList.addAll(items);

            handler.scrollItems(0);
            scrollPosition = 0.0f;
        }
    }
}
