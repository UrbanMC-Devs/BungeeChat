package net.urbanmc.bungeechat.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.urbanmc.bungeechat.BungeeChat;
import net.urbanmc.bungeechat.event.BungeePMEvent;

public class Message extends Command {

	public Message() {
		super("bcmessage", "bungeechat.message", "bcm", "gpm", "nwpm");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new ComponentBuilder("Please specify a player to message.").color(ChatColor.RED).create());
			return;
		}

		CommandSender receiver;

		if (args[0].equalsIgnoreCase("console")) {
			receiver = ProxyServer.getInstance().getConsole();
		} else {
			receiver = ProxyServer.getInstance().getPlayer(args[0]);
		}

		if (receiver == null) {
			sender.sendMessage(new ComponentBuilder("Player not found.").color(ChatColor.RED).create());
			return;
		}

		if (args.length == 1) {
			sender.sendMessage(new ComponentBuilder("Please specify a message to send.").color(ChatColor.RED).create());
			return;
		}

		StringBuilder message = new StringBuilder();

		for (int i = 1; i < args.length; i++) {
			message.append(args[i] + " ");
		}

		BungeePMEvent event = new BungeePMEvent(sender, receiver, message.toString().trim());

		if (event.isCancelled())
			return;

		BungeeChat.getReplyMap().put(sender.getName(), receiver.getName());
		BungeeChat.getReplyMap().put(receiver.getName(), sender.getName());

		sender.sendMessage(event.getSenderLine());
		receiver.sendMessage(event.getReceiverLine());
	}
}
