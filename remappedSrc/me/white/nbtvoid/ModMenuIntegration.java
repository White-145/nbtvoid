package me.white.nbtvoid;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("cloth-config2")) {
			return (parent) -> {
                ConfigBuilder builder = ConfigBuilder.create()
                    .setParentScreen(parent)
                    .setTitle(Text.translatable(NbtVoid.localized("config", "title")))
                    .setSavingRunnable(Config::save);
                ConfigEntryBuilder entryBuilder = builder.entryBuilder();
        
                builder.getOrCreateCategory(Text.translatable(NbtVoid.localized("config", "nbtvoid.title")))
                    .addEntry(entryBuilder
                        .startBooleanToggle(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.isEnabled")),
                            NbtVoid.getConfig().getIsEnabled()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.isEnabledTooltip")))
                        .setDefaultValue(Config.DEFAULT_IS_ENABLED)
                        .setSaveConsumer(NbtVoid.getConfig()::setIsEnabled)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startBooleanToggle(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.doSave")),
                            NbtVoid.getConfig().getDoSave()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.doSaveTooltip")))
                        .setDefaultValue(Config.DEFAULT_DO_SAVE)
                        .setSaveConsumer(NbtVoid.getConfig()::setDoSave)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startEnumSelector(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.sortType")),
                            Config.SortType.class,
                            NbtVoid.getConfig().getSortType()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.sortTypeTooltip")))
                        .setDefaultValue(Config.DEFAULT_SORT_TYPE)
                        .setEnumNameProvider(Config.SortType::localized)
                        .setSaveConsumer(NbtVoid.getConfig()::setSortType)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startIntField(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.maxDisplayItems")),
                            NbtVoid.getConfig().getMaxDisplayItems()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.maxDisplayItemsTooltip")))
                        .setDefaultValue(Config.DEFAULT_MAX_DISPLAY_ITEMS)
                        .setMin(0)
                        .setMax(Config.MAX_MAX_DISPLAY_ITEMS)
                        .setSaveConsumer(NbtVoid.getConfig()::setMaxDisplayItems)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startIntField(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.maxStoredItems")),
                            NbtVoid.getConfig().getMaxStoredItems()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.maxStoredItemsTooltip")))
                        .setDefaultValue(Config.DEFAULT_MAX_STORED_ITEMS)
                        .setMin(0)
                        .setMax(Config.MAX_MAX_STORED_ITEMS)
                        .setSaveConsumer(value -> {
                            NbtVoid.getConfig().setMaxStoredItems(value);
                            VoidController.updateMaxStored();
                        })
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startStrField(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.defaultSearchQuery")),
                            NbtVoid.getConfig().getDefaultSearchQuery()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.defaultSearchQueryTooltip")))
                        .setDefaultValue(Config.DEFAULT_DEFAULT_SEARCH_QUERY)
                        .setSaveConsumer(NbtVoid.getConfig()::setDefaultSearchQuery)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startEnumSelector(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.nameCheck")),
                            Config.CheckType.class,
                            NbtVoid.getConfig().getNameCheck()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.nameCheckTooltip")))
                        .setEnumNameProvider(Config.CheckType::localized)
                        .setDefaultValue(Config.DEFAULT_NAME_CHECK)
                        .setSaveConsumer(NbtVoid.getConfig()::setNameCheck)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startEnumSelector(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.nbtCheck")),
                            Config.CheckType.class,
                            NbtVoid.getConfig().getNbtCheck()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.nbtCheckTooltip")))
                        .setEnumNameProvider(Config.CheckType::localized)
                        .setDefaultValue(Config.DEFAULT_NBT_CHECK)
                        .setSaveConsumer(NbtVoid.getConfig()::setNbtCheck)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startStrList(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.ignoreNbt")),
                            NbtVoid.getConfig().getIgnoreNbt()
                        )
                        .setDefaultValue(Config.DEFAULT_IGNORE_NBT)
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.ignoreNbtTooltip")))
                        .setSaveConsumer(NbtVoid.getConfig()::setIgnoreNbt)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startStrList(
                            Text.translatable(NbtVoid.localized("config", "nbtvoid.removeNbt")),
                            NbtVoid.getConfig().getRemoveNbt()
                        )
                        .setDefaultValue(Config.DEFAULT_REMOVE_NBT)
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtvoid.removeNbtTooltip")))
                        .setSaveConsumer(NbtVoid.getConfig()::setRemoveNbt)
                        .build()
                    );
                    
                return builder.build();
            };
		} else {
			return parent -> null;
		}
    }
}
