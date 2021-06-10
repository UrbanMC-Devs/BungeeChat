package net.urbanmc.bungeechat.commands;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.urbanmc.bungeechat.BungeeChat;

public class Spy extends Command {

	public Spy() {
		super("bcspy", "bungeechat.message.spy");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer)) {
			sender.sendMessage(new ComponentBuilder("You must be a player to run this command.").create());
			return;
		}

		UUID id = ((ProxiedPlayer) sender).getUniqueId();

		boolean spying = BungeeChat.isSpying(id);

		if (spying) {
			BungeeChat.removeSpying(id);
		} else {
			BungeeChat.addSpying(id);
		}

		BaseComponent[] comp = new ComponentBuilder(
				"You are " + (!spying ? "now" : "no longer") + " spying on BungeeChat private messages")
						.color(ChatColor.GOLD).create();

		sender.sendMessage(comp);
	}
}
