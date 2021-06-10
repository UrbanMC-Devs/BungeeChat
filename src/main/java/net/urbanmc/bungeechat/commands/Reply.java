package net.urbanmc.bungeechat.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.plugin.Command;
import net.urbanmc.bungeechat.BungeeChat;
import net.urbanmc.bungeechat.event.BungeePMEvent;

public class Reply extends Command {

	public Reply() {
		super("bcreply", "bungeechat.reply", "bcr", "gr", "nwr");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!BungeeChat.getReplyMap().containsKey(sender.getName())) {
			sender.sendMessage(new ComponentBuilder("You have nobody to reply to.").color(ChatColor.RED).create());
			return;
		}

		String replyPlayer = BungeeChat.getReplyMap().get(sender.getName());

		CommandSender receiver;

		if (replyPlayer.equals("CONSOLE")) {
			receiver = ProxyServer.getInstance().getConsole();
		} else {
			receiver = ProxyServer.getInstance().getPlayer(replyPlayer);
		}

		if (receiver == null) {
			sender.sendMessage(new ComponentBuilder("That player logged off.").color(ChatColor.RED).create());
			BungeeChat.getReplyMap().remove(sender);
			BungeeChat.getReplyMap().remove(replyPlayer);
			return;
		}

		if (args.length == 0) {
			sender.sendMessage(new ComponentBuilder("Please specify a message to send.").color(ChatColor.RED).create());
			return;
		}

		StringBuilder message = new StringBuilder();

		for (int i = 0; i < args.length; i++) {
			message.append(args[i] + " ");
		}

		BungeePMEvent event = new BungeePMEvent(sender, receiver, message.toString().trim());

		if (event.isCancelled())
			return;

		sender.sendMessage(event.getSenderLine());
		receiver.sendMessage(event.getReceiverLine());
	}
}
