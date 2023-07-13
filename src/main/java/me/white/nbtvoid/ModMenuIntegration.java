package me.white.nbtvoid;

import java.util.concurrent.CompletableFuture;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

import dev.isxander.yacl3.api.Binding;
import dev.isxander.yacl3.api.ButtonOption;
import dev.isxander.yacl3.api.ConfigCategory;
import dev.isxander.yacl3.api.ListOption;
import dev.isxander.yacl3.api.Option;
import dev.isxander.yacl3.api.OptionDescription;
import dev.isxander.yacl3.api.YetAnotherConfigLib;
import dev.isxander.yacl3.api.controller.BooleanControllerBuilder;
import dev.isxander.yacl3.api.controller.EnumControllerBuilder;
import dev.isxander.yacl3.api.controller.IntegerFieldControllerBuilder;
import dev.isxander.yacl3.api.controller.StringControllerBuilder;
import me.white.nbtvoid.Config.CheckType;
import me.white.nbtvoid.Config.SortType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenuIntegration implements ModMenuApi {
    private static Option<Boolean> isEnabledOption = Option.<Boolean>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "isEnabled")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "isEnabledDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_IS_ENABLED,
            Config.getInstance()::getIsEnabled,
            Config.getInstance()::setIsEnabled
        ))
        .controller(opt -> BooleanControllerBuilder.create(opt)
            .yesNoFormatter()
        )
        .build();
        
    private static Option<Boolean> doSaveOption = Option.<Boolean>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "doSave")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "doSaveDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_DO_SAVE,
            Config.getInstance()::getDoSave,
            Config.getInstance()::setDoSave
        ))
        .controller(opt -> BooleanControllerBuilder.create(opt)
            .yesNoFormatter()
        )
        .build();

    private static Option<Boolean> doDynamicUpdateOption = Option.<Boolean>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "doDynamicUpdate")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "doDynamicUpdateDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_DO_DYNAMIC_UPDATE,
            Config.getInstance()::getDoDynamicUpdate,
            Config.getInstance()::setDoDynamicUpdate
        ))
        .controller(opt -> BooleanControllerBuilder.create(opt)
            .yesNoFormatter()
        )
        .build();

    private static Option<Boolean> doAsyncSearchOption = Option.<Boolean>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "doAsyncSearch")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "doAsyncSearchDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_DO_ASYNC_SEARCH,
            Config.getInstance()::getDoAsyncSearch,
            Config.getInstance()::setDoAsyncSearch
        ))
        .controller(opt -> BooleanControllerBuilder.create(opt)
            .yesNoFormatter()
        )
        .build();

    private static Option<Integer> maxDisplayItemsOption = Option.<Integer>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "maxDisplayItems")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "maxDisplayItemsDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_MAX_DISPLAY_ITEMS,
            Config.getInstance()::getMaxDisplayItems,
            Config.getInstance()::setMaxDisplayItems
        ))
        .controller(opt -> IntegerFieldControllerBuilder.create(opt)
            .min(0)
            .max(Config.MAX_MAX_DISPLAY_ITEMS)
        )
        .build();
    
    private static Option<Integer> maxStoredItemsOption = Option.<Integer>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "maxStoredItems")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "maxStoredItemsDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_MAX_STORED_ITEMS,
            Config.getInstance()::getMaxStoredItems,
            Config.getInstance()::setMaxStoredItems
        ))
        .controller(opt -> IntegerFieldControllerBuilder.create(opt)
            .min(0)
            .max(Config.MAX_MAX_STORED_ITEMS)
        )
        .build();
    
    private static Option<String> defaultSearchQueryOption = Option.<String>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "defaultSearchQuery")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "defaultSearchQueryDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_DEFAULT_SEARCH_QUERY,
            Config.getInstance()::getDefaultSearchQuery,
            Config.getInstance()::setDefaultSearchQuery
        ))
        .controller(StringControllerBuilder::create)
        .build();
    
    private static Option<SortType> sortTypeOption = Option.<SortType>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "sortType")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "sortTypeDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_SORT_TYPE,
            Config.getInstance()::getSortType,
            Config.getInstance()::setSortType
        ))
        .controller(opt -> EnumControllerBuilder.create(opt)
            .enumClass(SortType.class)
            .valueFormatter(SortType::localized)
        )
        .build();
    
    private static Option<CheckType> nameCheckOption = Option.<CheckType>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "nameCheck")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "nameCheckDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_NAME_CHECK,
            Config.getInstance()::getNameCheck,
            Config.getInstance()::setNameCheck
        ))
        .controller(opt -> EnumControllerBuilder.create(opt)
            .enumClass(CheckType.class)
            .valueFormatter(CheckType::localized)
        )
        .build();
    
    private static Option<CheckType> nbtCheckOption = Option.<CheckType>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "nbtCheck")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "nbtCheckDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_NBT_CHECK,
            Config.getInstance()::getNbtCheck,
            Config.getInstance()::setNbtCheck
        ))
        .controller(opt -> EnumControllerBuilder.create(opt)
            .enumClass(CheckType.class)
            .valueFormatter(CheckType::localized)
        )
        .build();
    
    private static ListOption<String> ignoreNbtOption = ListOption.<String>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "ignoreNbt")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "ignoreNbtDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_IGNORE_NBT,
            Config.getInstance()::getIgnoreNbt,
            Config.getInstance()::setIgnoreNbt
        ))
        .controller(StringControllerBuilder::create)
        .initial("")
        .build();

    private static ListOption<String> removeNbtOption = ListOption.<String>createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "removeNbt")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "removeNbtDescription"))))
        .binding(Binding.generic(
            Config.DEFAULT_REMOVE_NBT,
            Config.getInstance()::getRemoveNbt,
            Config.getInstance()::setRemoveNbt
        ))
        .controller(StringControllerBuilder::create)
        .initial("")
        .build();
    
    private static ButtonOption clearVoidButton = ButtonOption.createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "clearVoid")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "clearVoidDescription"))))
        .action((screen, opt) -> CompletableFuture.runAsync(VoidController::clear))
        .build();

    private static ButtonOption loadVoidButton = ButtonOption.createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "loadVoid")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "loadVoidDescription"))))
        .action((screen, opt) -> CompletableFuture.runAsync(VoidController::load))
        .build();

    private static ButtonOption saveVoidButton = ButtonOption.createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "saveVoid")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "saveVoidDescription"))))
        .action((screen, opt) -> CompletableFuture.runAsync(VoidController::save))
        .build();

    private static ButtonOption updateExceptionsButton = ButtonOption.createBuilder()
        .name(Text.translatable(NbtVoid.localized("config", "updateExceptions")))
        .description(OptionDescription.of(Text.translatable(NbtVoid.localized("config", "updateExceptionsDescription"))))
        .action((screen, opt) -> CompletableFuture.runAsync(VoidController::updateExceptions))
        .build();

    public static Screen getConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
            .title(Text.translatable(NbtVoid.localized("config", "title")))
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable(NbtVoid.localized("config", "category.general")))
                .option(isEnabledOption)
                .option(doSaveOption)
                .option(maxDisplayItemsOption)
                .option(maxStoredItemsOption)
                .option(clearVoidButton)
                .option(loadVoidButton)
                .option(saveVoidButton)
                .build()
            )
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable(NbtVoid.localized("config", "category.search")))
                .option(defaultSearchQueryOption)
                .option(doAsyncSearchOption)
                .option(nameCheckOption)
                .option(nbtCheckOption)
                .option(sortTypeOption)
                .build()
            )
            .category(ConfigCategory.createBuilder()
                .name(Text.translatable(NbtVoid.localized("config", "category.exceptions")))
                .option(doDynamicUpdateOption)
                .option(ignoreNbtOption)
                .option(removeNbtOption)
                .option(updateExceptionsButton)
                .build()
            )
            .save(Config::save)
            .build()
            .generateScreen(parent);
    }

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) return ModMenuIntegration::getConfigScreen;
		return parent -> null;
    }
}
