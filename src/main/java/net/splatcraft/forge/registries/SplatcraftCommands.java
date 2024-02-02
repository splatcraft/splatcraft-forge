package net.splatcraft.forge.registries;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.synchronization.ArgumentTypes;
import net.minecraft.commands.synchronization.EmptyArgumentSerializer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.commands.*;
import net.splatcraft.forge.commands.arguments.ColorCriterionArgument;
import net.splatcraft.forge.commands.arguments.InkColorArgument;

@Mod.EventBusSubscriber(modid = Splatcraft.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class SplatcraftCommands
{
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event)
    {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        InkColorCommand.register(dispatcher);
        ScanTurfCommand.register(dispatcher);
        ClearInkCommand.register(dispatcher);
        ReplaceColorCommand.register(dispatcher);
        ColorScoresCommand.register(dispatcher);
        StageCommand.register(dispatcher);

        SuperJumpCommand.register(dispatcher);
    }

    public static void registerArguments()
    {
        ArgumentTypes.register(Splatcraft.MODID + ":ink_color", InkColorArgument.class, new EmptyArgumentSerializer<>(InkColorArgument::inkColor));
        ArgumentTypes.register(Splatcraft.MODID + ":color_criterion", ColorCriterionArgument.class, new EmptyArgumentSerializer<>(ColorCriterionArgument::colorCriterion));
    }
}
