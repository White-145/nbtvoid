package me.white.nbtvoid;

import java.util.List;

import com.mojang.brigadier.StringReader;
import com.mojang.datafixers.util.Pair;

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
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EquipmentSlot;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.data.DataTracker.SerializedEntry;
import net.minecraft.entity.decoration.ItemFrameEntity;
import net.minecraft.entity.decoration.DisplayEntity.ItemDisplayEntity;
import net.minecraft.entity.player.PlayerInventory;

public class VoidController {
	public static final Path PATH = FabricLoader.getInstance().getGameDir().resolve("void.nbt");

	public static List<VoidEntry> nbtVoid = new ArrayList<>();
	public static boolean updating = false;

	public static void updateExceptions() {
		// prevent second run from config menu
		if (updating) return;
		updating = true;
		List<VoidEntry> oldVoid = new ArrayList<>(nbtVoid);
		clear();
		for (VoidEntry entry : oldVoid) addEntry(entry);
		updating = false;
		int overflow = nbtVoid.size() - Config.getInstance().getMaxStoredItems() * 9;
		for (int i = 0; i < overflow; ++i) {
			removeOldest();
		}
		scan();
	}
	
	public static void scan() {
		MinecraftClient client = MinecraftClient.getInstance();
		if (client == null) return;
		ClientPlayerEntity player = client.player;
		if (player != null) {
			PlayerInventory inventory = client.player.getInventory();
			addItems(player.getArmorItems());
			addItems(inventory.offHand);
			addItems(inventory.main);
			addItems(player.getEnderChestInventory().stacks);
		}
		if (client.getNetworkHandler() == null || client.getNetworkHandler().getWorld() == null) return;
		for (Entity entity : client.getNetworkHandler().getWorld().getEntities()) {
			addItems(entity.getArmorItems());
			addItems(entity.getHandItems());
			addItems(entity.getItemsEquipped());
			if (entity instanceof ItemFrameEntity itemFrameEntity) {
				ItemStack item = itemFrameEntity.getHeldItemStack();
				if (item != null)
					addItem(item);
			}
			if (entity instanceof ItemEntity itemEntity) {
				ItemStack item = itemEntity.getStack();
				if (item != null)
					addItem(item);
			}
			if (entity instanceof ItemDisplayEntity itemDisplayEntity) {
				addItem(itemDisplayEntity.getStackReference(0).get());
			}
		}
	}

	public static void load() {
		clear();
		if (Files.exists(PATH)) {
			try {
				NbtList nbt = NbtIo.read(PATH.toFile()).getList("entries", NbtElement.COMPOUND_TYPE);
				for (NbtElement entry : nbt) {
					NbtCompound item = ((NbtCompound)entry).getCompound("item");
					long time = ((NbtCompound)entry).getLong("time");
					addEntry(new VoidEntry(ItemStack.fromNbt((NbtCompound) item), Instant.ofEpochSecond(time)));
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

	public static void addItems(Iterable<ItemStack> items) {
		if (items == null) return;
		for (ItemStack item : items) {
			addItem(item);
		}
	}

	public static void addItem(ItemStack item) {
		if (item.isEmpty()) return;
		if (!item.hasNbt()) return;

		NbtCompound newNbt = removeRemoved(item.getNbt().copy());
		if (isIgnored(newNbt)) return;
		ItemStack newItem = item.copy();
		newItem.setNbt(newNbt);
		newItem.setCount(1);

		for (VoidEntry entry : nbtVoid) {
			if (itemEquals(newItem, entry.getItem())) return;
		}

		nbtVoid.add(0, new VoidEntry(newItem));

		if (nbtVoid.size() > Config.getInstance().getMaxStoredItems() * 9) {
			removeOldest();
		}
	}

	public static void addEntry(VoidEntry entry) {
		if (entry.getItem().isEmpty()) return;
		if (!entry.getItem().hasNbt()) return;

		NbtCompound newNbt = removeRemoved(entry.getItem().getNbt().copy());
		if (isIgnored(newNbt)) return;
		ItemStack newItem = entry.getItem().copy();
		newItem.setNbt(newNbt);

		for (VoidEntry voidEntry : nbtVoid) {
			if (itemEquals(newItem, voidEntry.getItem())) return;
		}

		nbtVoid.add(new VoidEntry(newItem, entry.getTime()));
	}

	private static void removeOldest() {
		int oldest = -1;
		Instant oldestTime = null;
		for (int i = 0; i < nbtVoid.size(); ++i) {
			if (oldestTime == null || nbtVoid.get(i).getTime().compareTo(oldestTime) < 0) {
				oldest = i;
				oldestTime = nbtVoid.get(i).getTime();
			}
		}
		nbtVoid.remove(oldest);
	}

	private static boolean isIgnored(NbtCompound nbt) {
		if (nbt == null)
			return true;
		if (nbt.isEmpty())
			return true;

		NbtCompound newNbt = nbt.copy();
		for (String ignoreNbt : Config.getInstance().getIgnoreNbt()) {
			try {
				NbtPath path = new NbtPathArgumentType().parse(new StringReader(ignoreNbt));
				path.remove(newNbt);
			} catch (Exception e) {
				NbtVoid.LOGGER.error("Invalid ignore NBT '" + ignoreNbt + "': " + e);
			}
		}
		return newNbt.isEmpty();
	}

	private static NbtCompound removeRemoved(NbtCompound nbt) {
		NbtCompound newNbt = nbt.copy();
		for (String removeNbt : Config.getInstance().getRemoveNbt()) {
			try {
				NbtPath path = new NbtPathArgumentType().parse(new StringReader(removeNbt));
				path.remove(newNbt);
			} catch (Exception e) {
				NbtVoid.LOGGER.error("Invalid remove NBT '" + removeNbt + "': " + e);
			}
		}
		return newNbt;
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

	public static List<VoidEntry> getNbtVoid() {
		return nbtVoid;
	}

	public static void clear() {
		nbtVoid.clear();
	}

	public static void fromPacket(Packet<?> packet) {
		if (!Config.getInstance().getIsEnabled()) return;
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
				AdvancementDisplay display = builder.display;
				if (display != null)
					VoidController.addItem(display.getIcon());
			}
		}
	}
}
