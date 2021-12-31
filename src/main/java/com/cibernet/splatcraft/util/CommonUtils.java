package com.cibernet.splatcraft.util;

import com.cibernet.splatcraft.Splatcraft;

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
            astring[1] = resourceName.substring(i + 1, resourceName.length());
            if (i >= 1) {
                astring[0] = resourceName.substring(0, i);
            }
        }

        return astring;
    }
}
