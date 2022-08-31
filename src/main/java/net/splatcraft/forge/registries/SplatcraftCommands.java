package net.splatcraft.forge.registries;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.ArgumentSerializer;
import net.minecraft.command.arguments.ArgumentTypes;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.commands.ClearInkCommand;
import net.splatcraft.forge.commands.ColorScoresCommand;
import net.splatcraft.forge.commands.InkColorCommand;
import net.splatcraft.forge.commands.ReplaceColorCommand;
import net.splatcraft.forge.commands.ScanTurfCommand;
import net.splatcraft.forge.commands.arguments.ColorCriterionArgument;
import net.splatcraft.forge.commands.arguments.InkColorArgument;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SplatcraftCommands
{
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSource> dispatcher = event.getDispatcher();

        InkColorCommand.register(dispatcher);
        ScanTurfCommand.register(dispatcher);
        ClearInkCommand.register(dispatcher);
        ReplaceColorCommand.register(dispatcher);
        ColorScoresCommand.register(dispatcher);
    }

    public static void registerArguments()
    {
        ArgumentTypes.register(Splatcraft.MODID + ":ink_color", InkColorArgument.class, new ArgumentSerializer<>(InkColorArgument::inkColor));
        ArgumentTypes.register(Splatcraft.MODID + ":color_criterion", ColorCriterionArgument.class, new ArgumentSerializer<>(ColorCriterionArgument::colorCriterion));
    }
}
