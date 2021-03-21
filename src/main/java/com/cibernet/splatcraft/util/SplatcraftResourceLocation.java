package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ResourceLocationException;
import net.minecraft.util.text.TranslationTextComponent;

/*
    Namespace defaults to Splatcraft's ModId
 */
public class SplatcraftResourceLocation extends ResourceLocation {
    protected SplatcraftResourceLocation(String[] resourceParts) {
        super(resourceParts);
    }

    public SplatcraftResourceLocation(String resourceName) {
        this(decompose(resourceName, ':'));
    }

    public SplatcraftResourceLocation(String namespaceIn, String pathIn) {
        super(namespaceIn, pathIn);
    }


    public static ResourceLocation read(StringReader reader) throws CommandSyntaxException {
        int i = reader.getCursor();

        while (reader.canRead() && ResourceLocation.isValidPathCharacter(reader.peek())) {
            reader.skip();
        }

        String s = reader.getString().substring(i, reader.getCursor());

        try {
            return new SplatcraftResourceLocation(s);
        } catch (ResourceLocationException resourcelocationexception) {
            reader.setCursor(i);
            throw new SimpleCommandExceptionType(new TranslationTextComponent("argument.id.invalid")).createWithContext(reader);
        }
    }

    protected static String[] decompose(String resourceName, char splitOn) {
        String[] astring = new String[]{Splatcraft.MODID, resourceName};
        int i = resourceName.indexOf(splitOn);
        if (i >= 0) {
            astring[1] = resourceName.substring(i + 1);
            if (i >= 1) {
                astring[0] = resourceName.substring(0, i);
            }
        }

        return astring;
    }
}
