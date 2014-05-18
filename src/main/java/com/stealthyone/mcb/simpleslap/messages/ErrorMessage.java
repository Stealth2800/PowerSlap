package com.stealthyone.mcb.simpleslap.messages;

import com.stealthyone.mcb.simpleslap.SimpleSlap;
import org.bukkit.command.CommandSender;

public enum ErrorMessage {

    SLAP_CANNOT_SLAP_SELF,
    SLAP_COOLING_DOWN,
    SLAP_INVALID_POWER,
    SLAP_POWER_MUST_BE_INT,
    MUST_BE_PLAYER,
    NO_PERMISSION,
    UNABLE_TO_FIND_PLAYER,
    UNKNOWN_COMMAND;

    private String path;

    private ErrorMessage() {
        this.path = "errors." + toString().toLowerCase();
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
        sender.sendMessage(getMessage());
    }

    public void sendTo(CommandSender sender, String... replacements) {
        sender.sendMessage(getMessage(replacements));
    }

}