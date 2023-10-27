package net.splatcraft.forge.util;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import net.splatcraft.forge.Splatcraft;
import net.splatcraft.forge.items.weapons.WeaponBaseItem;

public class CommonUtils
{

    private static boolean isValidNamespace(String namespaceIn) {
        for(int i = 0; i < namespaceIn.length(); ++i) {
            if (!validateNamespaceChar(namespaceIn.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean validatePathChar(char charValue) {
        return charValue == '_' || charValue == '-' || charValue >= 'a' && charValue <= 'z' || charValue >= '0' && charValue <= '9' || charValue == '/' || charValue == '.';
    }

    private static boolean validateNamespaceChar(char charValue) {
        return charValue == '_' || charValue == '-' || charValue >= 'a' && charValue <= 'z' || charValue >= '0' && charValue <= '9' || charValue == '.';
    }


    private static boolean isPathValid(String pathIn) {
        for(int i = 0; i < pathIn.length(); ++i) {
            if (!validatePathChar(pathIn.charAt(i))) {
                return false;
            }
        }

        return true;
    }

    public static boolean isResourceNameValid(String resourceName)
    {
        return isResourceNameValid(resourceName, Splatcraft.MODID);
    }

    public static boolean isResourceNameValid(String resourceName, String defaultLoc) {
        String[] astring = decompose(resourceName, ':', defaultLoc);
        return isValidNamespace(org.apache.commons.lang3.StringUtils.isEmpty(astring[0]) ? defaultLoc : astring[0]) && isPathValid(astring[1]);
    }


    protected static String[] decompose(String resourceName, char splitOn, String defaultLoc) {
        String[] astring = new String[]{defaultLoc, resourceName};
        int i = resourceName.indexOf(splitOn);
        if (i >= 0) {
            astring[1] = resourceName.substring(i + 1);
            if (i >= 1) {
                astring[0] = resourceName.substring(0, i);
            }
        }

        return astring;
    }

    public static void blockDrop(Level levelIn, BlockPos pos, ItemStack stack)
    {
        if(levelIn.getGameRules().getBoolean(GameRules.RULE_DOBLOCKDROPS) && !levelIn.captureBlockSnapshots)
            spawnItem(levelIn, pos, stack);
    }

    public static void spawnItem(Level levelIn, BlockPos pos, ItemStack stack) {
        if (!levelIn.isClientSide && !stack.isEmpty())
        {
            double d0 = (double)(levelIn.random.nextFloat() * 0.5F) + 0.25D;
            double d1 = (double)(levelIn.random.nextFloat() * 0.5F) + 0.25D;
            double d2 = (double)(levelIn.random.nextFloat() * 0.5F) + 0.25D;
            ItemEntity itementity = new ItemEntity(levelIn, (double)pos.getX() + d0, (double)pos.getY() + d1, (double)pos.getZ() + d2, stack);
            itementity.setDefaultPickUpDelay();
            levelIn.addFreshEntity(itementity);
        }
    }

    public static ItemStack getItemInInventory(Player entity, Predicate<ItemStack> predicate)
    {
        ItemStack itemstack = ProjectileWeaponItem.getHeldProjectile(entity, predicate);
        if (!itemstack.isEmpty())
            return itemstack;
        else
        {
            for(int i = 0; i < entity.getInventory().getContainerSize(); ++i)
            {
                ItemStack itemstack1 = entity.getInventory().getItem(i);
                if (predicate.test(itemstack1))
                    return itemstack1;
            }

            return ItemStack.EMPTY;
        }
    }

    public static boolean anyWeaponOnCooldown(Player player) {
        boolean isMainOnCooldown = player.getMainHandItem().getItem() instanceof WeaponBaseItem weapon && player.getCooldowns().isOnCooldown(weapon);
        boolean isOffOnCooldown = player.getOffhandItem().getItem() instanceof WeaponBaseItem weapon && player.getCooldowns().isOnCooldown(weapon);
        return isMainOnCooldown || isOffOnCooldown;
    }
}
