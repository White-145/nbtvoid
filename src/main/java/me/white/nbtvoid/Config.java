package me.white.nbtvoid;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mojang.brigadier.StringReader;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.NbtPathArgumentType;
import net.minecraft.text.Text;

public class Config {
    public enum CheckType {
        ALL,
        ANY;

        public static Text localized(Enum<CheckType> type) {
            return switch ((CheckType) type) {
                case ALL -> Text.translatable("config.nbtvoid.checktypeall");
                case ANY -> Text.translatable("config.nbtvoid.checktypeany");
            };
        }
    }

    public enum SortType {
        ALPHABETIC,
        STORE_DATE,
        STORE_DATE_INVERSE;

        public static Text localized(Enum<SortType> type) {
            return switch ((SortType) type) {
                case ALPHABETIC -> Text.translatable("config.nbtvoid.sorttypealphabetic");
                case STORE_DATE -> Text.translatable("config.nbtvoid.sorttypestoredate");
                case STORE_DATE_INVERSE -> Text.translatable("config.nbtvoid.sorttypestoredateinverse");
            };
        }
    }

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final Path PATH = FabricLoader.getInstance().getConfigDir().resolve(NbtVoid.MOD_ID + ".json");

    private static Config INSTANCE;

    public static final String DEFAULT_DEFAULT_SEARCH_QUERY = "";
    public static final int DEFAULT_MAX_DISPLAY_ITEM_ROWS = 128;
    public static final int DEFAULT_MAX_STORED_ITEM_ROWS = 1024;
    public static final SortType DEFAULT_SORT_TYPE = SortType.ALPHABETIC;
    public static final CheckType DEFAULT_NAME_CHECK = CheckType.ANY;
    public static final CheckType DEFAULT_ID_CHECK = CheckType.ANY;
    public static final CheckType DEFAULT_NBT_CHECK = CheckType.ALL;
    public static final boolean DEFAULT_DO_CHECK_TOOLTIP = false;
    public static final boolean DEFAULT_DO_SAVE = true;
    public static final boolean DEFAULT_IS_ENABLED = true;
    public static final boolean DEFAULT_DO_DYNAMIC_UPDATE = false;
    public static final List<String> DEFAULT_IGNORE_NBT = Arrays.asList(
            "Enchantments",
            "HideFlags",
            "CustomModelData",
            "BlockEntityTag.id",
            "BlockEntityTag.sherds",
            "SkullOwner.Id"
    );
    public static final List<String> DEFAULT_REMOVE_NBT = Arrays.asList(
            "Damage",
            "CustomCreativeLock"
    );

    public static final int MAX_MAX_DISPLAY_ITEM_ROWS = 4096;
    public static final int MAX_MAX_STORED_ITEM_ROWS = 4096;
	
	private String defaultSearchQuery = DEFAULT_DEFAULT_SEARCH_QUERY;
	private int maxDisplayItemRows = DEFAULT_MAX_DISPLAY_ITEM_ROWS;
	private int maxStoredItemRows = DEFAULT_MAX_STORED_ITEM_ROWS;
    private SortType sortType = DEFAULT_SORT_TYPE;
	private CheckType nameCheck = DEFAULT_NAME_CHECK;
	private CheckType idCheck = DEFAULT_ID_CHECK;
	private CheckType nbtCheck = DEFAULT_NBT_CHECK;
    private boolean doCheckTooltip = DEFAULT_DO_CHECK_TOOLTIP;
    private boolean doSave = DEFAULT_DO_SAVE;
    private boolean isEnabled = DEFAULT_IS_ENABLED;
    private boolean doDynamicUpdate = DEFAULT_DO_DYNAMIC_UPDATE;
	private List<String> ignoreNbt = DEFAULT_IGNORE_NBT;
	private List<String> removeNbt = DEFAULT_REMOVE_NBT;

    public static Config load() {
        if (!Files.exists(PATH)) {
            INSTANCE = new Config();
            save();
            return INSTANCE;
        } else try {
            INSTANCE = GSON.fromJson(Files.readString(PATH), Config.class);
            return INSTANCE;
        } catch (Exception e) {
            NbtVoid.LOGGER.error("Could not load config file: ", e);
            INSTANCE = new Config();
            save();
            return INSTANCE;
        }
    }

    public static void save() {
        if (INSTANCE == null) return;
        try {
            Files.write(PATH, Collections.singleton(GSON.toJson(INSTANCE)));
        } catch (Exception e) {
            NbtVoid.LOGGER.error("Could not save config file: ", e);
        }
    }

    public static Config getInstance() {
        return INSTANCE == null ? load() : INSTANCE;
    }

    public String getDefaultSearchQuery() {
        return defaultSearchQuery;
    }

    public int getMaxDisplayItemRows() {
        return maxDisplayItemRows;
    }

    public int getMaxStoredItemRows() {
        return maxStoredItemRows;
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

    public boolean getDoCheckTooltip() {
        return doCheckTooltip;
    }

    public boolean getDoSave() {
        return doSave;
    }

    public boolean getIsEnabled() {
        return isEnabled;
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

    public void setMaxDisplayItemRows(int maxDisplayItemRows) {
        this.maxDisplayItemRows = maxDisplayItemRows;
    }

    public void setMaxStoredItemRows(int maxStoredItemRows) {
        this.maxStoredItemRows = maxStoredItemRows;
        NbtVoid.VOID.setMaxSize(maxStoredItemRows * 9);
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

    public void setDoCheckTooltip(boolean doCheckTooltip) {
        this.doCheckTooltip = doCheckTooltip;
    }

    public void setDoSave(boolean doSave) {
        this.doSave = doSave;
    }

    public void setIsEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    public void setIgnoreNbt(List<String> ignoreNbt) {
        NbtPathArgumentType parser = NbtPathArgumentType.nbtPath();
        this.ignoreNbt = ignoreNbt.stream().filter(entry -> {
            try {
                parser.parse(new StringReader(entry));
                return true;
            } catch (CommandSyntaxException e) {
                NbtVoid.LOGGER.warn("Invalid Remove NBT '" + entry + "': ", e);
            }
            return false;
        }).toList();
    }

    public void setRemoveNbt(List<String> removeNbt) {
        NbtPathArgumentType parser = NbtPathArgumentType.nbtPath();
        this.removeNbt = removeNbt.stream().filter(entry -> {
            try {
                parser.parse(new StringReader(entry));
                return true;
            } catch (CommandSyntaxException e) {
                NbtVoid.LOGGER.warn("Invalid Remove NBT '" + entry + "': ", e);
            }
            return false;
        }).toList();
    }
}