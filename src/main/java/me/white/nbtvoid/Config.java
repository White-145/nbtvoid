package me.white.nbtvoid;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.StringReader;

import net.fabricmc.api.Environment;
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.text.Text;

@Environment(EnvType.CLIENT)
public class Config {
    public enum CheckType {
        ALL,
        ANY;

        public static Text localized(Enum<CheckType> type) {
            switch ((CheckType)type) {
                case ALL:
                    return Text.translatable(NbtVoid.localized("config", "checkTypeAll"));
                default:
                    return Text.translatable(NbtVoid.localized("config", "checkTypeAny"));
            }
        }
    }

    public enum SortType {
        ALPHABETIC,
        STORE_DATE,
        STORE_DATE_INVERSE;

        public static Text localized(Enum<SortType> type) {
            switch ((SortType)type) {
                case ALPHABETIC:
                    return Text.translatable(NbtVoid.localized("config", "sortTypeAlphabetic"));
                case STORE_DATE:
                    return Text.translatable(NbtVoid.localized("config", "sortTypeStoreDate"));
                default:
                    return Text.translatable(NbtVoid.localized("config", "sortTypeStoreDateInverse"));
            }
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(NbtVoid.MOD_ID + ".json");

    private static Config INSTANCE;

    public static final String DEFAULT_DEFAULT_SEARCH_QUERY = "";
    public static final int DEFAULT_MAX_DISPLAY_ITEMS = 1024;
    public static final int DEFAULT_MAX_STORED_ITEMS = 8192;
    public static final SortType DEFAULT_SORT_TYPE = SortType.ALPHABETIC;
    public static final CheckType DEFAULT_NAME_CHECK = CheckType.ANY;
    public static final CheckType DEFAULT_ID_CHECK = CheckType.ANY;
    public static final CheckType DEFAULT_NBT_CHECK = CheckType.ALL;
    public static final boolean DEFAULT_DO_SAVE = true;
    public static final boolean DEFAULT_IS_ENABLED = true;
    public static final boolean DEFAULT_DO_DYNAMIC_UPDATE = false;
    public static final boolean DEFAULT_DO_ASYNC_SEARCH = false;
    public static final List<String> DEFAULT_IGNORE_NBT = Arrays.asList(new String[] {
		"Enchantment",
        "HideFlags",
        "CustomModelData",
        "BlockEntityTag.id",
        "BlockEntityTag.sherds",
        "SkullOwner.Id"
	});
    public static final List<String> DEFAULT_REMOVE_NBT = Arrays.asList(new String[] {
		"Damage",
        "CustomCreativeLock"
	});
    // TODO: Implement
    // public static final CheckType DEFAULT_IGNORE_ITEMS_CHECK = CheckType.ANY;
    // public static final List<String> DEFAULT_IGNORE_ITEMS = Arrays.asList(new String[] {
    //     "paper{CustomCreativeLock:{}}"
    // });
    // public static final boolean DEFAULT_IGNORE_LIST_ORDER = true;
    // public static final boolean DEFAULT_IGNORE_CUSTOM_NBT = false;

    public static final int MAX_MAX_DISPLAY_ITEMS = 32768;
    public static final int MAX_MAX_STORED_ITEMS = 32768;
	
	private String defaultSearchQuery = DEFAULT_DEFAULT_SEARCH_QUERY;
	private int maxDisplayItems = DEFAULT_MAX_DISPLAY_ITEMS;
	private int maxStoredItems = DEFAULT_MAX_STORED_ITEMS;
    private SortType sortType = DEFAULT_SORT_TYPE;
	private CheckType nameCheck = DEFAULT_NAME_CHECK;
	private CheckType idCheck = DEFAULT_ID_CHECK;
	private CheckType nbtCheck = DEFAULT_NBT_CHECK;
    private boolean doSave = DEFAULT_DO_SAVE;
    private boolean isEnabled = DEFAULT_IS_ENABLED;
    private boolean doDynamicUpdate = DEFAULT_DO_DYNAMIC_UPDATE;
    private boolean doAsyncSearch = DEFAULT_DO_ASYNC_SEARCH;
	private List<String> ignoreNbt = DEFAULT_IGNORE_NBT;
	private List<String> removeNbt = DEFAULT_REMOVE_NBT;

    private Config() {}

    public static Config load() {
        if (!Files.exists(PATH)) {
            INSTANCE = new Config();
            save();
            return INSTANCE;
        } else try {
            INSTANCE = GSON.fromJson(Files.readString(PATH), Config.class);
            return INSTANCE;
        } catch (Exception e) {
            INSTANCE = new Config();
            NbtVoid.LOGGER.error("Couldn't load config file: ", e);
            return INSTANCE;
        }
    }

    public static void save() {
        if (INSTANCE == null) return;
        try {
            Files.write(PATH, Collections.singleton(GSON.toJson(INSTANCE)));
        } catch (Exception e) {
            NbtVoid.LOGGER.error("Couldn't save config file: ", e);
        }
    }

    public static Config getInstance() {
        if (INSTANCE == null) return load();
        return INSTANCE;
    }

    public String getDefaultSearchQuery() {
        return defaultSearchQuery;
    }

    public int getMaxDisplayItems() {
        return maxDisplayItems;
    }

    public int getMaxStoredItems() {
        return maxStoredItems;
    }

    public SortType getSortType() {
        return sortType;
    }

    public CheckType getNameCheck() {
        return nameCheck;
    }

    public CheckType getIdCheck() {
        return idCheck;
    }

    public CheckType getNbtCheck() {
        return nbtCheck;
    }

    public boolean getDoSave() {
        return doSave;
    }

    public boolean getIsEnabled() {
        return isEnabled;
    }

    public boolean getDoDynamicUpdate() {
        return doDynamicUpdate;
    }

    public boolean getDoAsyncSearch() {
        return doAsyncSearch;
    }

    public List<String> getIgnoreNbt() {
        return ignoreNbt;
    }

    public List<String> getRemoveNbt() {
        return removeNbt;
    }

    public void setDefaultSearchQuery(String defaultSearchQuery) {
        this.defaultSearchQuery = defaultSearchQuery;
    }

    public void setMaxDisplayItems(int maxDisplayItems) {
        this.maxDisplayItems = maxDisplayItems;
    }

    public void setMaxStoredItems(int maxStoredItems) {
        this.maxStoredItems = maxStoredItems;
        CompletableFuture.runAsync(VoidController::updateExceptions);
    }

    public void setSortType(SortType sortType) {
        this.sortType = sortType;
    }

    public void setNameCheck(CheckType nameCheck) {
        this.nameCheck = nameCheck;
    }

    public void setIdCheck(CheckType idCheck) {
        this.idCheck = idCheck;
    }

    public void setNbtCheck(CheckType nbtCheck) {
        this.nbtCheck = nbtCheck;
    }

    public void setDoSave(boolean doSave) {
        this.doSave = doSave;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void setDoDynamicUpdate(boolean doDynamicUpdate) {
        this.doDynamicUpdate = doDynamicUpdate;
        if (getDoDynamicUpdate()) CompletableFuture.runAsync(VoidController::updateExceptions);
    }

    public void setDoAsyncSearch(boolean doAsyncSearch) {
        this.doAsyncSearch = doAsyncSearch;
    }

    public void setIgnoreNbt(List<String> ignoreNbt) {
        List<String> checkedList = new ArrayList<>();
        for (String entry : ignoreNbt) {
            try {
                new NbtPathArgumentType().parse(new StringReader(entry));
                checkedList.add(entry);
            } catch (Exception e) {
                NbtVoid.LOGGER.error("Invalid Ignore NBT '" + entry + "': ", e);
            }
        }
        this.ignoreNbt = checkedList;
        if (getDoDynamicUpdate()) CompletableFuture.runAsync(VoidController::updateExceptions);
    }

    public void setRemoveNbt(List<String> removeNbt) {
        List<String> checkedList = new ArrayList<>();
        for (String entry : removeNbt) {
            try {
                new NbtPathArgumentType().parse(new StringReader(entry));
                checkedList.add(entry);
            } catch (Exception e) {
                NbtVoid.LOGGER.error("Invalid Remove NBT '" + entry + "': ", e);
            }
        }
        this.removeNbt = checkedList;
        if (getDoDynamicUpdate()) CompletableFuture.runAsync(VoidController::updateExceptions);
    }
}