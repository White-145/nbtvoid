package me.white.nbtvoid;

import net.fabricmc.api.Environment;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public class NbtVoid implements ClientModInitializer {
	public static final String MOD_ID = "nbtvoid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);
	
	public static final KeyBinding KEYBIND_CLEAR = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		NbtVoid.localized("key", "clear"),
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_UNKNOWN,
		NbtVoid.localized("category", "void")
	));

	public static final KeyBinding KEYBIND_TOGGLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		NbtVoid.localized("key", "toggle"),
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_KP_DECIMAL,
		NbtVoid.localized("category", "void")
	));

	public static final KeyBinding KEYBIND_SAVE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		NbtVoid.localized("key", "save"),
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_UNKNOWN,
		NbtVoid.localized("category", "void")
	));

	public static final KeyBinding KEYBIND_LOAD = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		NbtVoid.localized("key", "load"),
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_UNKNOWN,
		NbtVoid.localized("category", "void")
	));

	public static final KeyBinding KEYBIND_SCAN = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		NbtVoid.localized("key", "scan"),
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_KP_ENTER,
		NbtVoid.localized("category", "void")
	));

	public static final Runnable CLEAR_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			VoidController.clear();
		}
	};

	public static final Runnable SAVE_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			VoidController.save();
		}
	};

	public static final Runnable LOAD_RUNNABLE = new Runnable() {
		@Override
		public void run() {
			VoidController.load();
		}
	};

	public final ModdedCreativeTab VOID_TAB = ModdedCreativeTab.builder(new Identifier(MOD_ID, "void"))
		.displayName(Text.translatable(NbtVoid.localized("itemGroup", "void")))
		.type(ModdedCreativeTab.Type.VOID)
		.icon(Items.ENDER_CHEST)
		.searchProvider(NbtVoid::search)
		.build();

	public static List<ItemStack> search(String query) {
		List<VoidEntry> items;
		if (query.isEmpty()) {
			items = new ArrayList<>(VoidController.getNbtVoid());
		} else {
			items = new ArrayList<>(SearchProvider.search(VoidController.getNbtVoid(), SearchProvider.parseQuery(query)));
		}

		items.sort(VoidEntry.COMPARATOR);
		if (items.size() > Config.getInstance().getMaxDisplayItems() * 9) {
			items = items.subList(0, Config.getInstance().getMaxDisplayItems() * 9);
		}
		
		return VoidEntry.toItems(items);
	}
	
    public static String localized(String start, String end) {
        return start + "." + NbtVoid.MOD_ID + "." + end;
    }

	@Override
	public void onInitializeClient() {
		Config.load();

		if (Config.getInstance().getDoSave()) VoidController.load();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (Config.getInstance().getDoSave()) VoidController.save();
		}));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			// Do actions with void in separate thread so it doesn't lag
			while (KEYBIND_CLEAR.wasPressed()) {
				Thread thread = new Thread(CLEAR_RUNNABLE);
				thread.start();
			}

			while (KEYBIND_TOGGLE.wasPressed()) {
				Config.getInstance().setIsEnabled(!Config.getInstance().getIsEnabled());
			}

			while (KEYBIND_SAVE.wasPressed()) {
				Thread thread = new Thread(SAVE_RUNNABLE);
				thread.start();
			}

			while (KEYBIND_LOAD.wasPressed()) {
				Thread thread = new Thread(LOAD_RUNNABLE);
				thread.start();
			}

			while (KEYBIND_SCAN.wasPressed()) {
				Thread thread = new Thread(VoidController.SCAN_WORLD_RUNNABLE);
				thread.start();
			}
		});

		LOGGER.info("NBT Void initialized");
	}
}
