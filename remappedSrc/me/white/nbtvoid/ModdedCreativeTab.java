package me.white.nbtvoid;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.google.common.base.Function;

import me.white.nbtvoid.mixin.ItemGroupBuilderMixin;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

public class ModdedCreativeTab {
    public static enum Type {
        NORMAL,
        VOID,
        VAULT
    }

    public static Map<ItemGroup, ModdedCreativeTab> moddedTabs = new HashMap<>();

    public ItemGroup itemGroup;
    public Type type;
    public Function<String, List<ItemStack>> searchProvider;

    public static class Builder {
        private Type type;
        private Text displayName;
        private ItemStack icon;
        private Function<String, List<ItemStack>> searchProvider;

        private static List<ItemStack> defaultSearchProvider(String query) {
            return new ArrayList<>();
        }

        public Builder() {
            this.type = Type.NORMAL;
            this.displayName = Text.literal("");
            this.icon = new ItemStack(Items.DIAMOND);
            this.searchProvider = Builder::defaultSearchProvider;
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
                case VAULT:
                    return ItemGroup.Type.HOTBAR;
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
                
            ((ItemGroupBuilderMixin)builder).setType(getType(type));
            
            tab.itemGroup = builder.build();
            tab.type = type;
            tab.searchProvider = searchProvider;

            moddedTabs.put(tab.itemGroup, tab);

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

    public static Builder builder() {
        return new Builder();
    }

    public static Type getType(ItemGroup group) {
        if (!moddedTabs.containsKey(group)) return Type.NORMAL;
        return moddedTabs.get(group).getType();
    }
}