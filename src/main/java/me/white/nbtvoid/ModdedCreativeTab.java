package me.white.nbtvoid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen.CreativeScreenHandler;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

// Was planning to do more tabs in this mod, maybe in future
public class ModdedCreativeTab {
    public static class AsyncSearcher implements Runnable {
        CreativeScreenHandler handler;
        ModdedCreativeTab moddedTab;
        String query;

        public AsyncSearcher(CreativeScreenHandler handler, ModdedCreativeTab moddedTab, String query) {
            this.handler = handler;
            this.moddedTab = moddedTab;
            this.query = query;
        }

        @Override
        public void run() {
            List<ItemStack> items = moddedTab.searchProvider.apply(query);
            if (moddedTab.searcher != this) return;
            handler.itemList.clear();
            handler.itemList.addAll(items);
            handler.scrollItems(0);
        }
    }

    public static enum Type {
        NORMAL,
        VOID
    }

    public static Map<ItemGroup, ModdedCreativeTab> moddedTabs = new HashMap<>();

    public ItemGroup itemGroup;
    public Type type;
    public Function<String, List<ItemStack>> searchProvider;
    public AsyncSearcher searcher;

    public static class Builder {
        private Type type;
        private Text displayName;
        private ItemStack icon;
        private Function<String, List<ItemStack>> searchProvider;
        private Identifier ident;

        private static List<ItemStack> defaultSearchProvider(String query) {
            return new ArrayList<>();
        }

        public Builder(Identifier ident) {
            this.type = Type.NORMAL;
            this.displayName = Text.literal("");
            this.icon = new ItemStack(Items.DIAMOND);
            this.searchProvider = Builder::defaultSearchProvider;
            this.ident = ident;
        }

        public Builder type(Type type) {
            this.type = type;
            return this;
        }
        
        public Builder displayName(Text displayName) {
            this.displayName = displayName;
            return this;
        }

        public Builder icon(Item icon) {
            this.icon = new ItemStack(icon);
            return this;
        }

        public Builder icon(ItemStack icon) {
            this.icon = icon;
            return this;
        }
        
        public Builder searchProvider(Function<String, List<ItemStack>> searchProvider) {
            this.searchProvider = searchProvider;
            return this;
        }

        private String getTexture(Type type) {
            switch (type) {
                case VOID:
                    return "item_search.png";
                default:
                    return "items.png";
            }
        }

        private ItemGroup.Type getType(Type type) {
            switch (type) {
                case VOID:
                    return ItemGroup.Type.SEARCH;
                default:
                    return ItemGroup.Type.CATEGORY;
            }
        }

        public ModdedCreativeTab build() {
            ModdedCreativeTab tab = new ModdedCreativeTab();
            ItemGroup.Builder builder = FabricItemGroup.builder()
                .displayName(displayName)
                .icon(() -> icon)
                .texture(getTexture(type));
            
            builder.type(getType(type));
            
            tab.itemGroup = builder.build();
            tab.type = type;
            tab.searchProvider = searchProvider;

            moddedTabs.put(tab.itemGroup, tab);
            Registry.register(Registries.ITEM_GROUP, ident, tab.itemGroup);

            return tab;
        }
    }

    public Type getType() {
        return type;
    }

    public ItemGroup getItemGroup() {
        return itemGroup;
    }

    public Function<String, List<ItemStack>> getSearchProvider() {
        return searchProvider;
    }

    public static Builder builder(Identifier ident) {
        return new Builder(ident);
    }

    public static Type getType(ItemGroup group) {
        if (!moddedTabs.containsKey(group)) return Type.NORMAL;
        return moddedTabs.get(group).getType();
    }
}
