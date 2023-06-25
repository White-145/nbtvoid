package me.white.nbtvoid;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;

import java.util.List;

import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Environment(value=EnvType.CLIENT)
public class NbtVoid implements ModInitializer {
	public static final String MOD_ID = "nbtvoid";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	private static Config CONFIG;
	public static final NbtVoid NBT_VOID = new NbtVoid();

	public static Config getConfig() {
		return CONFIG;
	}
	
	public static final KeyBinding KEYBIND_CLEAR = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		NbtVoid.localized("key", "nbtvoid.clear"),
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_UNKNOWN,
		NbtVoid.localized("category", "nbtvoid.void")
	));

	public static final KeyBinding KEYBIND_TOGGLE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		NbtVoid.localized("key", "nbtvoid.toggle"),
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_KP_DECIMAL,
		NbtVoid.localized("category", "nbtvoid.void")
	));

	public static final KeyBinding KEYBIND_SAVE = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		NbtVoid.localized("key", "nbtvoid.save"),
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_UNKNOWN,
		NbtVoid.localized("category", "nbtvoid.void")
	));

	public static final KeyBinding KEYBIND_LOAD = KeyBindingHelper.registerKeyBinding(new KeyBinding(
		NbtVoid.localized("key", "nbtvoid.load"),
		InputUtil.Type.KEYSYM,
		GLFW.GLFW_KEY_UNKNOWN,
		NbtVoid.localized("category", "nbtvoid.void")
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

	public final ModdedCreativeTab VOID_TAB = ModdedCreativeTab.builder()
		.displayName(Text.translatable(NbtVoid.localized("itemGroup", "nbtvoid.void")))
		.type(ModdedCreativeTab.Type.VOID)
		.icon(Items.ENDER_CHEST)
		.searchProvider(NbtVoid::search)
		.build();
	
	public static List<ItemStack> search(String query) {
		List<VoidEntry> items;
		if (query.isEmpty()) {
			items = VoidController.getNbtVoid();
		} else {
			items = SearchProvider.search(VoidController.getNbtVoid(), SearchProvider.parseQuery(query));
		}
		items = items.subList(0, Math.min(items.size(), NbtVoid.getConfig().getMaxDisplayItems() * 9));
		items.sort(VoidEntry.COMPARATOR);
		
		return VoidEntry.toItems(items);
	}
	
    public static String localized(String start, String end) {
        return start + "." + NbtVoid.MOD_ID + "." + end;
    }

	@Override
	public void onInitialize() {
		LOGGER.info("NBT Void initialized");
		if (NbtVoid.getConfig().getDoSave()) VoidController.load();
		Runtime.getRuntime().addShutdownHook(new Thread(() -> {
			if (NbtVoid.getConfig().getDoSave()) VoidController.save();
		}));

		ClientTickEvents.END_CLIENT_TICK.register(client -> {
			while (KEYBIND_CLEAR.wasPressed()) {
				Thread thread = new Thread(CLEAR_RUNNABLE);
				thread.start();
			}

			while (KEYBIND_TOGGLE.wasPressed()) {
				NbtVoid.getConfig().setIsEnabled(!Config.getInstance().getIsEnabled());
			}

			while (KEYBIND_SAVE.wasPressed()) {
				Thread thread = new Thread(SAVE_RUNNABLE);
				thread.start();
			}

			while (KEYBIND_LOAD.wasPressed()) {
				Thread thread = new Thread(LOAD_RUNNABLE);
				thread.start();
			}
		});
	}
}
