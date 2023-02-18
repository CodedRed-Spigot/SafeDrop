package dev.codedred.safedrop.utils;

import net.md_5.bungee.api.ChatColor;
import org.bukkit.Material;

import java.lang.reflect.Method;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatUtil {

    private static final Pattern hexPattern = Pattern.compile("<#([A-Fa-f0-9]){6}>");

    public static String format(String msg) {
        msg = ChatColor.translateAlternateColorCodes('&', msg);
        if (isNewerVersion())
            msg = hex(msg);
        return msg;
    }

    private static String hex(String message){
        Matcher matcher = hexPattern.matcher(message);
        while (matcher.find()) {
            ChatColor hexColor = ChatColor.of(matcher.group().substring(1, matcher.group().length() - 1));
            String before = message.substring(0, matcher.start());
            String after = message.substring(matcher.end());
            message = before + hexColor + after;
            matcher = hexPattern.matcher(message);
        }
        return ChatColor.translateAlternateColorCodes('&', message);
    }

    private static boolean isNewerVersion() {
        try {
            Class<?> class_Material = Material.class;
            Method method = class_Material.getDeclaredMethod("matchMaterial", String.class, Boolean.TYPE);
            return (method != null);
        } catch(ReflectiveOperationException ex) {
            return false;
        }
    }
}
