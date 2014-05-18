package com.stealthyone.mcb.simpleslap.messages;

import com.stealthyone.mcb.simpleslap.SimpleSlap;
import org.bukkit.command.CommandSender;

public enum NoticeMessage {

    SLAP_BYPASS_TOGGLED,
    SLAP_COOLDOWN_ENDED,
    SLAP_MOVEMENT_TOGGLED,

    PLUGIN_RELOADED,
    PLUGIN_SAVED;

    private String path;

    private NoticeMessage() {
        this.path = "notices." + toString().toLowerCase();
    }

    public String getMessagePath() {
        return path;
    }

    public String getMessage() {
        return SimpleSlap.getInstance().getMessageManager().getMessage(path);
    }

    public String getMessage(String... replacements) {
        return SimpleSlap.getInstance().getMessageManager().getMessage(path, replacements);
    }

    public void sendTo(CommandSender sender) {
        sender.sendMessage(getMessage().split("\n"));
    }

    public void sendTo(CommandSender sender, String... replacements) {
        sender.sendMessage(getMessage(replacements).split("\n"));
    }

}