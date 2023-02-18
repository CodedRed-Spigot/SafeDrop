package dev.codedred.safedrop.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    private static final Pattern HEX_PATTERN = Pattern.compile("<#([A-Fa-f0-9]){6}>");
    private static final Method MATCH_MATERIAL_METHOD = getMatchMaterialMethod();

    private ChatUtil() {
        throw new IllegalStateException("Utility class");
    }

    public static String format(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        if (isNewerVersion())
            msg = hex(msg);
        return msg;
    }

    private static String hex(String message){
        Matcher matcher = HEX_PATTERN.matcher(message);
        StringBuilder builder = new StringBuilder(message);
        while (matcher.find()) {
            ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
            builder.replace(matcher.start(), matcher.end(), hexColor.toString());
            matcher.reset(builder.toString());
        }
        return ChatColor.translateAlternateColorCodes('&', builder.toString());
    }

    private static boolean isNewerVersion() {
        return MATCH_MATERIAL_METHOD != null;
    }

    private static Method getMatchMaterialMethod() {
        try {
            Class<?> class_Material = Material.class;
            return class_Material.getDeclaredMethod("matchMaterial", String.class, Boolean.TYPE);
        } catch(ReflectiveOperationException ex) {
            return null;
        }
    }
}