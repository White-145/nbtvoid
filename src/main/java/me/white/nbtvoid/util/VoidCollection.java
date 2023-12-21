package me.white.nbtvoid.util;

import me.white.nbtvoid.Config;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemStackSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class VoidCollection {
    private List<ItemStack> items = new ArrayList<>();
    private Set<ItemStack> unique = ItemStackSet.create();
    private int maxSize;
    // flag to avoid recursion in ItemStack mixin
    public boolean isLocked = false;

    public VoidCollection() {
        this.maxSize = -1;
    }

    public VoidCollection(int maxSize) {
        this.maxSize = maxSize;
    }

    public ItemStack remove() {
        ItemStack stack = items.remove(0);
        ItemStack ignored = Util.removeNbt(stack, Config.getInstance().getIgnoreNbt());
        unique.remove(ignored);
        assert items.size() == unique.size();
        return stack;
    }

    public boolean add(ItemStack stack) {
        if (stack == null || !stack.hasNbt()) return false;

        isLocked = true;
        stack = stack.copyWithCount(1);
        ItemStack removed = Util.removeNbt(stack, Config.getInstance().getRemoveNbt());
        ItemStack ignored = Util.removeNbt(removed, Config.getInstance().getIgnoreNbt());
        isLocked = false;

        if (!ignored.hasNbt()) return false;
        if (unique.contains(ignored)) return false;

        if (maxSize >= 0) {
            while (items.size() >= maxSize) {
                remove();
            }
        }

        unique.add(ignored);
        return items.add(removed);
    }

    public void clear() {
        items.clear();
        unique.clear();
    }

    public int getMaxSize() {
        return maxSize;
    }

    public void setMaxSize(int maxSize) {
        this.maxSize = maxSize;
        if (maxSize >= 0) {
            while (items.size() > maxSize) {
                remove();
            }
        }
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
