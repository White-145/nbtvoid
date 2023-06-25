package me.white.nbtvoid;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import me.white.nbtvoid.Config.SortType;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class VoidEntry {
    public static final Comparator<VoidEntry> COMPARATOR = new Comparator<VoidEntry>() {
		@Override
		public int compare(VoidEntry first, VoidEntry second) {
            SortType sortType = Config.getInstance().getSortType();
            if (sortType == SortType.ALPHABETIC) {
                String firstId = Registries.ITEM.getId(first.getItem().getItem()).toString();
                String secondId = Registries.ITEM.getId(second.getItem().getItem()).toString();
                int idCompared = firstId.compareTo(secondId);
                if (idCompared != 0)
                    return idCompared;
                return first.getItem().getName().toString().compareTo(second.getItem().getName().toString());
            } else if (sortType == SortType.STORE_DATE) {
                return first.getTime().compareTo(second.getTime());
            } else if (sortType == SortType.STORE_DATE_INVERSE) {
                return second.getTime().compareTo(first.getTime());
            }

            return 0;
		}
	};

    private ItemStack item;
    private Instant time;

    public VoidEntry(ItemStack item, Instant time) {
        this.item = item;
        this.time = time;
    }

    public VoidEntry(ItemStack item) {
        this.item = item;
        this.time = Instant.now();
    }

    public ItemStack getItem() {
        return item;
    }

    public Instant getTime() {
        return time;
    }

    public void setItem(ItemStack item) {
        this.item = item;
    }

    public void setTime(Instant time) {
        this.time = time;
    }

    public static List<ItemStack> toItems(List<VoidEntry> entries) {
        List<ItemStack> items = new ArrayList<>();
        for (VoidEntry entry : entries) {
            items.add(entry.item);
        }

        return items;
    }
}
