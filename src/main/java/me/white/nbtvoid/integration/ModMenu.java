package me.white.nbtvoid.integration;

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
import me.white.nbtvoid.Config;
import me.white.nbtvoid.Config.CheckType;
import me.white.nbtvoid.Config.SortType;
import me.white.nbtvoid.NbtVoid;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;

public class ModMenu implements ModMenuApi {
    private static Option<Boolean> isEnabledOption = Option.<Boolean>createBuilder()
            .name(Text.translatable("config.nbtvoid.isenabled"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.isenableddescription")))
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
            .name(Text.translatable("config.nbtvoid.dosave"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.dosavedescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_DO_SAVE,
                    Config.getInstance()::getDoSave,
                    Config.getInstance()::setDoSave
            ))
            .controller(opt -> BooleanControllerBuilder.create(opt)
                    .yesNoFormatter()
            )
            .build();

    private static Option<Integer> maxDisplayItemRowsOption = Option.<Integer>createBuilder()
            .name(Text.translatable("config.nbtvoid.maxdisplayitemrows"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.maxdisplayitemrowsdescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_MAX_DISPLAY_ITEM_ROWS,
                    Config.getInstance()::getMaxDisplayItemRows,
                    Config.getInstance()::setMaxDisplayItemRows
            ))
            .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                    .min(0)
                    .max(Config.MAX_MAX_DISPLAY_ITEM_ROWS)
            )
            .build();

    private static Option<Integer> maxStoredItemRowsOption = Option.<Integer>createBuilder()
            .name(Text.translatable("config.nbtvoid.maxstoreditemrows"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.maxstoreditemrowsdescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_MAX_STORED_ITEM_ROWS,
                    Config.getInstance()::getMaxStoredItemRows,
                    Config.getInstance()::setMaxStoredItemRows
            ))
            .controller(opt -> IntegerFieldControllerBuilder.create(opt)
                    .min(0)
                    .max(Config.MAX_MAX_STORED_ITEM_ROWS)
            )
            .build();

    private static Option<String> defaultSearchQueryOption = Option.<String>createBuilder()
            .name(Text.translatable("config.nbtvoid.defaultsearchquery"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.defaultsearchquerydescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_DEFAULT_SEARCH_QUERY,
                    Config.getInstance()::getDefaultSearchQuery,
                    Config.getInstance()::setDefaultSearchQuery
            ))
            .controller(StringControllerBuilder::create)
            .build();

    private static Option<SortType> sortTypeOption = Option.<SortType>createBuilder()
            .name(Text.translatable("config.nbtvoid.sorttype"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.sorttypedescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_SORT_TYPE,
                    Config.getInstance()::getSortType,
                    Config.getInstance()::setSortType
            ))
            .controller(opt -> EnumControllerBuilder.create(opt)
                    .enumClass(SortType.class)
                    .formatValue(SortType::localized)
            )
            .build();

    private static Option<CheckType> nameCheckOption = Option.<CheckType>createBuilder()
            .name(Text.translatable("config.nbtvoid.namecheck"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.namecheckdescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_NAME_CHECK,
                    Config.getInstance()::getNameCheck,
                    Config.getInstance()::setNameCheck
            ))
            .controller(opt -> EnumControllerBuilder.create(opt)
                    .enumClass(CheckType.class)
                    .formatValue(CheckType::localized)
            )
            .build();

    private static Option<CheckType> nbtCheckOption = Option.<CheckType>createBuilder()
            .name(Text.translatable("config.nbtvoid.nbtcheck"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.nbtcheckdescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_NBT_CHECK,
                    Config.getInstance()::getNbtCheck,
                    Config.getInstance()::setNbtCheck
            ))
            .controller(opt -> EnumControllerBuilder.create(opt)
                    .enumClass(CheckType.class)
                    .formatValue(CheckType::localized)
            )
            .build();

    private static Option<Boolean> doCheckTooltip = Option.<Boolean>createBuilder()
            .name(Text.translatable("config.nbtvoid.dochecktooltip"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.dochecktooltipdescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_DO_CHECK_TOOLTIP,
                    Config.getInstance()::getDoCheckTooltip,
                    Config.getInstance()::setDoCheckTooltip
            ))
            .controller(opt -> BooleanControllerBuilder.create(opt)
                    .yesNoFormatter()
            )
            .build();

    private static ListOption<String> ignoreNbtOption = ListOption.<String>createBuilder()
            .name(Text.translatable("config.nbtvoid.ignorenbt"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.ignorenbtdescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_IGNORE_NBT,
                    Config.getInstance()::getIgnoreNbt,
                    Config.getInstance()::setIgnoreNbt
            ))
            .controller(StringControllerBuilder::create)
            .initial("")
            .build();

    private static ListOption<String> removeNbtOption = ListOption.<String>createBuilder()
            .name(Text.translatable("config.nbtvoid.removenbt"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.removenbtdescription")))
            .binding(Binding.generic(
                    Config.DEFAULT_REMOVE_NBT,
                    Config.getInstance()::getRemoveNbt,
                    Config.getInstance()::setRemoveNbt
            ))
            .controller(StringControllerBuilder::create)
            .initial("")
            .build();

    private static ButtonOption clearVoidButton = ButtonOption.createBuilder()
            .name(Text.translatable("config.nbtvoid.clearvoid"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.clearvoiddescription")))
            .action((screen, opt) -> NbtVoid.VOID.clear())
            .build();

    private static ButtonOption loadVoidButton = ButtonOption.createBuilder()
            .name(Text.translatable("config.nbtvoid.loadvoid"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.loadvoiddescription")))
            .action((screen, opt) -> NbtVoid.load())
            .build();

    private static ButtonOption saveVoidButton = ButtonOption.createBuilder()
            .name(Text.translatable("config.nbtvoid.savevoid"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.savevoiddescription")))
            .action((screen, opt) -> NbtVoid.save())
            .build();

    private static ButtonOption updateExceptionsButton = ButtonOption.createBuilder()
            .name(Text.translatable("config.nbtvoid.update"))
            .description(OptionDescription.of(Text.translatable("config.nbtvoid.updatedescription")))
            .action((screen, opt) -> NbtVoid.update())
            .build();

    public static Screen getConfigScreen(Screen parent) {
        return YetAnotherConfigLib.createBuilder()
                .title(Text.translatable("config.nbtvoid.title"))
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("config.nbtvoid.category.general"))
                        .option(isEnabledOption)
                        .option(doSaveOption)
                        .option(maxDisplayItemRowsOption)
                        .option(maxStoredItemRowsOption)
                        .option(clearVoidButton)
                        .option(loadVoidButton)
                        .option(saveVoidButton)
                        .build()
                )
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("config.nbtvoid.category.search"))
                        .option(defaultSearchQueryOption)
                        .option(nameCheckOption)
                        .option(nbtCheckOption)
                        .option(sortTypeOption)
                        .option(doCheckTooltip)
                        .build()
                )
                .category(ConfigCategory.createBuilder()
                        .name(Text.translatable("config.nbtvoid.category.exceptions"))
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
//        if (FabricLoader.getInstance().isModLoaded("yet_another_config_lib_v3")) return ModMenu::getConfigScreen;
//        return parent -> null;
        return ModMenu::getConfigScreen;
    }
}
