package me.white.nbtvoid.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.mojang.brigadier.StringReader;
import me.white.nbtvoid.Config;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;

public class SearchProvider {
    List<String> nameQueries;
    List<String> identifierQueries;
    List<String> nbtQueries;

    public SearchProvider(String query) {
        ArrayList<String> nameQueries = new ArrayList<>();
        ArrayList<String> identifierQueries = new ArrayList<>();
        ArrayList<String> nbtQueries = new ArrayList<>();

        String[] querySplit = query.split("\\s+");
        for (String queryPart : querySplit) {
            if (!queryPart.isEmpty()) {
                char prefix = queryPart.charAt(0);
                ArrayList<String> queries = switch (prefix) {
                    case '$' -> nbtQueries;
                    case '&' -> identifierQueries;
                    default -> nameQueries;
                };
                if (prefix == '$' || prefix == '&' || prefix == '\\') {
                    queryPart = queryPart.substring(1);
                }
                if (!queryPart.isEmpty()) {
                    queries.add(queryPart);
                }
            }
        }

        this.nameQueries = nameQueries;
        this.identifierQueries = identifierQueries;
        this.nbtQueries = nbtQueries;
    }

    private static boolean matchesNbt(ItemStack stack, String query) {
        try {
            NbtPath path = new NbtPathArgumentType().parse(new StringReader(query));
            return path.count(stack.getNbt()) != 0;
        } catch (Exception e) {
            return false;
        }
    }

    private static boolean matchesName(ItemStack stack, String query) {
        query = query.toLowerCase(Locale.ROOT);
        if (Config.getInstance().getDoCheckTooltip()) {
            for (Text tooltip : stack.getTooltip(null, TooltipContext.Default.BASIC.withCreative())) {
                if (tooltip.getString().toLowerCase(Locale.ROOT).contains(query)) return true;
            }
        } else {
            if (!stack.hasNbt()) return false;
            NbtCompound nbt = stack.getNbt();
            if (!nbt.contains("display", NbtElement.COMPOUND_TYPE)) return false;
            NbtCompound display = nbt.getCompound("display");
            if (display.contains("Name", NbtElement.STRING_TYPE)) {
                return Text.Serialization.fromJson(display.getString("Name")).getString().toLowerCase(Locale.ROOT).contains(query);
            }
        }
        return false;
    }

    private static boolean matchesIdentifier(ItemStack stack, String query) {
        return Registries.ITEM.getId(stack.getItem()).toString().contains(query);
    }

    private boolean matches(ItemStack stack) {
        nameCheck: if (Config.getInstance().getNameCheck() == Config.CheckType.ANY) {
            if (!nameQueries.isEmpty()) {
                for (String query : nameQueries) {
                    if (matchesName(stack, query)) break nameCheck;
                }
                return false;
            }
        } else {
            for (String query : nameQueries) {
                if (!matchesName(stack, query)) return false;
            }
        }
        identifierCheck: if (Config.getInstance().getIdCheck() == Config.CheckType.ANY) {
            if (!identifierQueries.isEmpty()) {
                for (String query : identifierQueries) {
                    if (matchesIdentifier(stack, query)) break identifierCheck;
                }
                return false;
            }
        } else {
            for (String query : identifierQueries) {
                if (!matchesIdentifier(stack, query)) return false;
            }
        }
        nbtCheck: if (Config.getInstance().getNbtCheck() == Config.CheckType.ANY) {
            if (!nbtQueries.isEmpty()) {
                for (String query : nbtQueries) {
                    if (matchesNbt(stack, query)) break nbtCheck;
                }
                return false;
            }
        } else {
            for (String query : nbtQueries) {
                if (!matchesNbt(stack, query)) return false;
            }
        }
        return true;
    }

    public List<ItemStack> findAll(List<ItemStack> items) {
        return items.stream().filter(this::matches).toList();
    }
}