package me.white.nbtvoid.util;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;

import java.util.List;

public class Util {
    public static ItemStack removeNbt(ItemStack stack, List<String> paths) {
        stack = stack.copy();
        NbtCompound nbt = stack.getNbt();
        NbtPathArgumentType parser = NbtPathArgumentType.nbtPath();
        for (String path : paths) {
            try {
                NbtPathArgumentType.NbtPath nbtPath = parser.parse(new StringReader(path));
                nbtPath.remove(nbt);
            } catch (CommandSyntaxException ignored) { }
        }
        return stack;
    }
}
