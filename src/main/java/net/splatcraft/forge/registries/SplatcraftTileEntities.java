package net.splatcraft.forge.registries;

import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.splatcraft.forge.client.renderer.tileentity.InkedBlockTileEntityRenderer;
import net.splatcraft.forge.client.renderer.tileentity.RemotePedestalTileEntityRenderer;
import net.splatcraft.forge.client.renderer.tileentity.StageBarrierTileEntityRenderer;
import net.splatcraft.forge.tileentities.*;
import net.splatcraft.forge.tileentities.container.InkVatContainer;
import net.splatcraft.forge.tileentities.container.WeaponWorkbenchContainer;

import static net.splatcraft.forge.Splatcraft.MODID;
import static net.splatcraft.forge.registries.SplatcraftBlocks.*;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class SplatcraftTileEntities
{
    protected static final DeferredRegister<BlockEntityType<?>> REGISTRY = DeferredRegister.create(ForgeRegistries.BLOCK_ENTITIES, MODID);
    protected static final DeferredRegister<MenuType<?>> CONTAINER_REGISTRY = DeferredRegister.create(ForgeRegistries.CONTAINERS, MODID);

    public static final RegistryObject<BlockEntityType<InkColorTileEntity>> colorTileEntity = registerTileEntity("color", InkColorTileEntity::new, inkedWool, inkedGlass, inkedGlassPane, inkedCarpet, canvas, splatSwitch, inkwell);
    public static final RegistryObject<BlockEntityType<InkedBlockTileEntity>> inkedTileEntity = registerTileEntity("inked_block", InkedBlockTileEntity::new, inkedBlock, glowingInkedBlock, clearInkedBlock);
    public static final RegistryObject<BlockEntityType<CrateTileEntity>> crateTileEntity = registerTileEntity("crate", CrateTileEntity::new, crate, sunkenCrate);
    public static final RegistryObject<BlockEntityType<StageBarrierTileEntity>> stageBarrierTileEntity = registerTileEntity("stage_barrier", StageBarrierTileEntity::new, stageBarrier, stageVoid);
    public static final RegistryObject<BlockEntityType<ColoredBarrierTileEntity>> colorBarrierTileEntity = registerTileEntity("color_barrier", ColoredBarrierTileEntity::new, allowedColorBarrier, deniedColorBarrier);
    public static final RegistryObject<BlockEntityType<InkVatTileEntity>> inkVatTileEntity = registerTileEntity("ink_vat", InkVatTileEntity::new, inkVat);
    public static final RegistryObject<BlockEntityType<RemotePedestalTileEntity>>remotePedestalTileEntity = registerTileEntity("remote_pedestal", RemotePedestalTileEntity::new, remotePedestal);
    public static final RegistryObject<BlockEntityType<SpawnPadTileEntity>> spawnPadTileEntity = registerTileEntity("spawn_pad", SpawnPadTileEntity::new, spawnPad);

    public static final RegistryObject<MenuType<InkVatContainer>> inkVatContainer = CONTAINER_REGISTRY.register("ink_vat", () -> IForgeMenuType.create(InkVatContainer::new));
    public static final RegistryObject<MenuType<WeaponWorkbenchContainer>> weaponWorkbenchContainer = registerContainer("weapon_workbench", WeaponWorkbenchContainer::new);

    @SuppressWarnings("ConstantConditions")
    private static <T extends BlockEntity> RegistryObject<BlockEntityType<T>> registerTileEntity(String name, BlockEntityType.BlockEntitySupplier<T> factoryIn, RegistryObject<? extends Block>... allowedBlocks)
    {
        return REGISTRY.register(name, () ->
        {
            Block[] blocks = new Block[allowedBlocks.length];
            for(int i = 0; i < blocks.length; i++)
                blocks[i] = allowedBlocks[i].get();

            return BlockEntityType.Builder.of(factoryIn, blocks).build(null);
        });
    }

    private static <T extends AbstractContainerMenu> RegistryObject<MenuType<T>> registerContainer(String name, MenuType.MenuSupplier<T> factoryIn)
    {
        return CONTAINER_REGISTRY.register(name, () -> new MenuType<>(factoryIn));
    }

    /*
    private static <T extends Container> ContainerType<T> registerMenu(String name, ContainerType.IFactory<T> factory)
    {
        ContainerType<T> container = new ContainerType<>(factory);
        container.setRegistryName(name);
        c_registry.add(container);
        return container;
    }

    @SubscribeEvent
    public static void containerInit(final RegistryEvent.Register<ContainerType<?>> event)
    {
        IForgeRegistry<ContainerType<?>> registry = event.getRegistry();

        c_registry.forEach(registry::register);
    }
    */
    public static void bindTESR()
    {

        BlockEntityRenderers.register(inkedTileEntity.get(), InkedBlockTileEntityRenderer::new);
        BlockEntityRenderers.register(stageBarrierTileEntity.get(), StageBarrierTileEntityRenderer::new);
        BlockEntityRenderers.register(colorBarrierTileEntity.get(), StageBarrierTileEntityRenderer::new);
        BlockEntityRenderers.register(remotePedestalTileEntity.get(), RemotePedestalTileEntityRenderer::new);
    }
}
