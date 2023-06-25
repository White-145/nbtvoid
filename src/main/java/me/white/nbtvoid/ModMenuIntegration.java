package me.white.nbtvoid;

import java.util.ArrayList;
import java.util.List;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.command.argument.NbtPathArgumentType;
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
        
                builder.getOrCreateCategory(Text.translatable(NbtVoid.localized("config", "title")))
                    .addEntry(entryBuilder
                        .startBooleanToggle(
                            Text.translatable(NbtVoid.localized("config", "isEnabled")),
                            Config.getInstance().getIsEnabled()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "isEnabledTooltip")))
                        .setDefaultValue(Config.DEFAULT_IS_ENABLED)
                        .setSaveConsumer(Config.getInstance()::setIsEnabled)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startBooleanToggle(
                            Text.translatable(NbtVoid.localized("config", "doSave")),
                            Config.getInstance().getDoSave()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "doSaveTooltip")))
                        .setDefaultValue(Config.DEFAULT_DO_SAVE)
                        .setSaveConsumer(Config.getInstance()::setDoSave)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startBooleanToggle(
                            Text.translatable(NbtVoid.localized("config", "doDynamicUpdate")),
                            Config.getInstance().getDoDynamicUpdate()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "doDynamicUpdateTooltip")))
                        .setDefaultValue(Config.DEFAULT_DO_DYNAMIC_UPDATE)
                        .setSaveConsumer(value -> {
                            Config.getInstance().setDoDynamicUpdate(value);
                            if (value) {
                                Thread thread = new Thread(VoidController.UPDATE_RUNNABLE);
                                thread.start();
                            }
                        })
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startEnumSelector(
                            Text.translatable(NbtVoid.localized("config", "sortType")),
                            Config.SortType.class,
                            Config.getInstance().getSortType()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "sortTypeTooltip")))
                        .setDefaultValue(Config.DEFAULT_SORT_TYPE)
                        .setEnumNameProvider(Config.SortType::localized)
                        .setSaveConsumer(Config.getInstance()::setSortType)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startIntField(
                            Text.translatable(NbtVoid.localized("config", "maxDisplayItems")),
                            Config.getInstance().getMaxDisplayItems()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "maxDisplayItemsTooltip")))
                        .setDefaultValue(Config.DEFAULT_MAX_DISPLAY_ITEMS)
                        .setMin(0)
                        .setMax(Config.MAX_MAX_DISPLAY_ITEMS)
                        .setSaveConsumer(Config.getInstance()::setMaxDisplayItems)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startIntField(
                            Text.translatable(NbtVoid.localized("config", "maxStoredItems")),
                            Config.getInstance().getMaxStoredItems()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "maxStoredItemsTooltip")))
                        .setDefaultValue(Config.DEFAULT_MAX_STORED_ITEMS)
                        .setMin(0)
                        .setMax(Config.MAX_MAX_STORED_ITEMS)
                        .setSaveConsumer(value -> {
                            Config.getInstance().setMaxStoredItems(value);
                            VoidController.UPDATE_MAX_STORED_ITEMS_RUNNABLE.run();
                        })
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startStrField(
                            Text.translatable(NbtVoid.localized("config", "defaultSearchQuery")),
                            Config.getInstance().getDefaultSearchQuery()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "defaultSearchQueryTooltip")))
                        .setDefaultValue(Config.DEFAULT_DEFAULT_SEARCH_QUERY)
                        .setSaveConsumer(Config.getInstance()::setDefaultSearchQuery)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startEnumSelector(
                            Text.translatable(NbtVoid.localized("config", "nameCheck")),
                            Config.CheckType.class,
                            Config.getInstance().getNameCheck()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nameCheckTooltip")))
                        .setEnumNameProvider(Config.CheckType::localized)
                        .setDefaultValue(Config.DEFAULT_NAME_CHECK)
                        .setSaveConsumer(Config.getInstance()::setNameCheck)
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startEnumSelector(
                            Text.translatable(NbtVoid.localized("config", "nbtCheck")),
                            Config.CheckType.class,
                            Config.getInstance().getNbtCheck()
                        )
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "nbtCheckTooltip")))
                        .setEnumNameProvider(Config.CheckType::localized)
                        .setDefaultValue(Config.DEFAULT_NBT_CHECK)
                        .setSaveConsumer(Config.getInstance()::setNbtCheck)
                        .build()
                    )
                    // .addEntry(entryBuilder
                    //     .startEnumSelector(
                    //         Text.translatable(NbtVoid.localized("config", "ignoreItemsCheck")),
                    //         Config.CheckType.class,
                    //         Config.getInstance().getIgnoreItemsCheck()
                    //     )
                    //     .setTooltip(Text.translatable(NbtVoid.localized("config", "ignoreItemsCheckTooltip")))
                    //     .setEnumNameProvider(Config.CheckType::localized)
                    //     .setDefaultValue(Config.DEFAULT_IGNORE_ITEMS_CHECK)
                    //     .setSaveConsumer(Config.getInstance()::setIgnoreItemsCheck)
                    //     .build()
                    // )
                    .addEntry(entryBuilder
                        .startStrList(
                            Text.translatable(NbtVoid.localized("config", "ignoreNbt")),
                            Config.getInstance().getIgnoreNbt()
                        )
                        .setDefaultValue(Config.DEFAULT_IGNORE_NBT)
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "ignoreNbtTooltip")))
                        .setSaveConsumer(value -> {
                            List<String> newValue = new ArrayList<>();
                            for (String ignoreNbt : value) {
                                try {
                                    new NbtPathArgumentType().parse(new com.mojang.brigadier.StringReader(ignoreNbt));
                                    newValue.add(ignoreNbt);
                                } catch (Exception E) {
                                    NbtVoid.LOGGER.info("Invalid ignore NBT: " + ignoreNbt);
                                }
                            }
                            Config.getInstance().setIgnoreNbt(newValue);
                            if (Config.getInstance().getDoDynamicUpdate()) {
                                Thread thread = new Thread(VoidController.UPDATE_RUNNABLE);
                                thread.start();
                            }
                        })
                        .build()
                    )
                    .addEntry(entryBuilder
                        .startStrList(
                            Text.translatable(NbtVoid.localized("config", "removeNbt")),
                            Config.getInstance().getRemoveNbt()
                        )
                        .setDefaultValue(Config.DEFAULT_REMOVE_NBT)
                        .setTooltip(Text.translatable(NbtVoid.localized("config", "removeNbtTooltip")))
                        .setSaveConsumer(value -> {
                            List<String> newValue = new ArrayList<>();
                            for (String removeNbt : value) {
                                try {
                                    new NbtPathArgumentType().parse(new com.mojang.brigadier.StringReader(removeNbt));
                                    newValue.add(removeNbt);
                                } catch (Exception E) {
                                    NbtVoid.LOGGER.info("Invalid remove NBT: " + removeNbt);
                                }
                            }
                            Config.getInstance().setRemoveNbt(newValue);
                            if (Config.getInstance().getDoDynamicUpdate()) {
                                Thread thread = new Thread(VoidController.UPDATE_RUNNABLE);
                                thread.start();
                            }
                        })
                        .build()
                    // )
                    // .addEntry(entryBuilder
                    //     .startStrList(
                    //         Text.translatable(NbtVoid.localized("config", "ignoreItems")),
                    //         Config.getInstance().getIgnoreItems()
                    //     )
                    //     .setDefaultValue(Config.DEFAULT_IGNORE_ITEMS)
                    //     .setTooltip(Text.translatable(NbtVoid.localized("config", "ignoreItemsTooltip")))
                    //     .setSaveConsumer(value -> {
                    //         Config.getInstance().setIgnoreItems(value);
                    //         Thread thread = new Thread(VoidController.UPDATE_RUNNABLE);
				    //         thread.start();
                    //     })
                    //     .build()
                    );
                    
                return builder.build();
            };
		} else {
			return parent -> null;
		}
    }
}
