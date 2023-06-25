package me.white.nbtvoid;

import java.util.List;

import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Pair;

import me.white.nbtvoid.mixin.AdvancementBuilderMixin;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.command.argument.NbtPathArgumentType.NbtPath;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtIo;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtLong;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.text.HoverEvent;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.network.packet.s2c.play.AdvancementUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityEquipmentUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.EntityTrackerUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.GameMessageS2CPacket;
import net.minecraft.network.packet.s2c.play.InventoryS2CPacket;
import net.minecraft.network.packet.s2c.play.ScreenHandlerSlotUpdateS2CPacket;
import net.minecraft.network.packet.s2c.play.SetTradeOffersS2CPacket;
import net.minecraft.village.TradeOffer;
import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.AdvancementDisplay;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.data.DataTracker.SerializedEntry;

public class VoidController {
	public static final Path PATH = FabricLoader.getInstance().getGameDir().resolve("void.nbt");

	public static List<VoidEntry> nbtVoid = new ArrayList<>();

	public static void load() {
		clear();
		if (Files.exists(PATH)) {
			try {
				NbtList nbt = NbtIo.read(PATH.toFile()).getList("entries", NbtElement.COMPOUND_TYPE);
				for (NbtElement entry : nbt) {
					NbtCompound item = ((NbtCompound)entry).getCompound("item");
					long time = ((NbtCompound)entry).getLong("time");
					nbtVoid.add(new VoidEntry(ItemStack.fromNbt((NbtCompound) item), Instant.ofEpochMilli(time)));
				}
			} catch (Exception e) {
				NbtVoid.LOGGER.error("Couldn't load void file: " + e);
			}
		}
		NbtVoid.LOGGER.info("Loaded NBT void");
	}

	public static void save() {
		try {
			NbtCompound nbt = new NbtCompound();
			NbtList entries = new NbtList();
			for (VoidEntry entry : nbtVoid) {
				NbtCompound entryNbt = new NbtCompound();
				entryNbt.put("time", NbtLong.of(entry.getTime().getEpochSecond()));
				NbtCompound itemNbt = new NbtCompound();
				itemNbt.putString("id", Registries.ITEM.getId(entry.getItem().getItem()).toString());
				itemNbt.putByte("Count", (byte) entry.getItem().getCount());
				if (entry.getItem().hasNbt()) {
					itemNbt.put("tag", entry.getItem().getNbt());
				}
				entryNbt.put("item", itemNbt);
				entries.add(entryNbt);
			}
			nbt.put("entries", entries);
			NbtIo.write(nbt, PATH.toFile());
		} catch (Exception e) {
			NbtVoid.LOGGER.error("Couldn't save void file: ", e);
		}
		NbtVoid.LOGGER.info("Saved NBT void");
	}

	public static boolean itemEquals(ItemStack first, ItemStack second) {
		return first.getItem().equals(second.getItem()) && first.getNbt().equals(second.getNbt());
	}

	public static void addItem(ItemStack item) {
		if (!NbtVoid.getConfig().getIsEnabled())
			return;
		if (item.isEmpty())
			return;
		if (isIgnored(item))
			return;

		ItemStack newItem = removeRemoved(item.copy());
		newItem.setCount(1);

		for (VoidEntry entry : nbtVoid) {
			if (itemEquals(newItem, entry.getItem()))
				return;
		}

		if (nbtVoid.size() > NbtVoid.getConfig().getMaxStoredItems() * 9) {
			nbtVoid.remove(nbtVoid.size() - 1);
		}

		nbtVoid.add(0, new VoidEntry(newItem));
	}

	private static boolean isIgnored(ItemStack item) {
		if (!item.hasNbt())
			return true;

		NbtCompound newNbt = item.getNbt().copy();
		for (String ignoreNbt : NbtVoid.getConfig().getIgnoreNbt()) {
			try {
				NbtPath path = new NbtPathArgumentType().parse(new StringReader(ignoreNbt));
				path.remove(newNbt);
			} catch (Exception e) {
				NbtVoid.LOGGER.error("Invalid ignore NBT '" + ignoreNbt + "': " + e);
			}
		}
		return newNbt.isEmpty();
	}

	private static ItemStack removeRemoved(ItemStack item) {
		ItemStack newItem = item.copy();
		for (String removeNbt : NbtVoid.getConfig().getRemoveNbt()) {
			try {
				NbtPath path = new NbtPathArgumentType().parse(new StringReader(removeNbt));
				path.remove(newItem.getNbt());
			} catch (Exception e) {
				NbtVoid.LOGGER.error("Invalid remove NBT '" + removeNbt + "': " + e);
			}
		}
		return newItem;
	}

	public static List<ItemStack> itemsFromText(Text text) {
		List<ItemStack> items = new ArrayList<>();

		for (Text part : text.getWithStyle(Style.EMPTY)) {
			HoverEvent hoverEvent = part.getStyle().getHoverEvent();
			if (hoverEvent == null)
				continue;

			HoverEvent.ItemStackContent item = hoverEvent.getValue(HoverEvent.Action.SHOW_ITEM);
			if (item == null)
				continue;

			items.add(item.asStack());
		}

		return items;
	}

	public static void updateMaxStored() {
		int newMaxStored = NbtVoid.getConfig().getMaxStoredItems();
		if (nbtVoid.size() > newMaxStored) {
			nbtVoid = nbtVoid.subList(0, newMaxStored);
		}
	}

	public static List<VoidEntry> getNbtVoid() {
		return nbtVoid;
	}

	public static void clear() {
		nbtVoid = new ArrayList<>();
	}

	public static void fromPacket(Packet<?> packet) {
		if (packet instanceof InventoryS2CPacket acceptedPacket) {
			for (ItemStack item : acceptedPacket.getContents()) {
				VoidController.addItem(item);
			}
		} else if (packet instanceof ScreenHandlerSlotUpdateS2CPacket acceptedPacket) {
			VoidController.addItem(acceptedPacket.getItemStack());
		} else if (packet instanceof EntityEquipmentUpdateS2CPacket acceptedPacket) {
			for (Pair<EquipmentSlot, ItemStack> pair : acceptedPacket.getEquipmentList()) {
				VoidController.addItem(pair.getSecond());
			}
		} else if (packet instanceof EntityTrackerUpdateS2CPacket acceptedPacket) {
			for (SerializedEntry<?> entry : acceptedPacket.trackedValues()) {
				if (entry.value() instanceof ItemStack itemEntry) {
					VoidController.addItem(itemEntry);
				}
			}
		} else if (packet instanceof SetTradeOffersS2CPacket acceptedPacket) {
			for (TradeOffer offer : acceptedPacket.getOffers()) {
				VoidController.addItem(offer.getOriginalFirstBuyItem());
				VoidController.addItem(offer.getSecondBuyItem());
				VoidController.addItem(offer.getSellItem());
			}
		} else if (packet instanceof GameMessageS2CPacket acceptedPacket) {
			for (ItemStack item : VoidController.itemsFromText(acceptedPacket.content())) {
				VoidController.addItem(item);
			}
		} else if (packet instanceof AdvancementUpdateS2CPacket acceptedPacket) {
			for (Advancement.Builder builder : acceptedPacket.getAdvancementsToEarn().values()) {
				AdvancementDisplay display = ((AdvancementBuilderMixin) builder).getDisplay();
				if (display != null)
					VoidController.addItem(display.getIcon());
			}
		}
	}
}
