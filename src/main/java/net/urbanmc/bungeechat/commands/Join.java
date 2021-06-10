package net.urbanmc.bungeechat.commands;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.urbanmc.bungeechat.BungeeChat;
import net.urbanmc.bungeechat.Channel;

public class Join extends Command {

	private BaseComponent prefix = new ComponentBuilder("[BungeeChat] ").color(ChatColor.GOLD).create()[0];

	public Join() {
		super("bcjoin", "bungeechat.join");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!(sender instanceof ProxiedPlayer))
			return;

		ProxiedPlayer p = (ProxiedPlayer) sender;

		BaseComponent comp;

		if (args.length == 0) {
			comp = new ComponentBuilder("Please enter a chat to join.").color(ChatColor.RED).create()[0];
			p.sendMessage(new BaseComponent[] { prefix, comp });

			return;
		}

		Channel channel = BungeeChat.getChannelsManager().getChannelByName(args[0]);

		if (channel == null) {
			comp = new ComponentBuilder("Channel not found.").color(ChatColor.RED).create()[0];
			p.sendMessage(new BaseComponent[] { prefix, comp });

			return;
		}

		if (!p.hasPermission(channel.getPermission())) {
			comp = new ComponentBuilder("You do not have permission to use this channel.").color(ChatColor.RED)
					.create()[0];
			p.sendMessage(new BaseComponent[] { prefix, comp });

			return;
		}

		if (channel.isAbsent(p.getUniqueId())) {
			channel.join(p.getUniqueId());

			comp = new ComponentBuilder("You have joined ").color(ChatColor.DARK_GREEN).create()[0];
		} else {
			comp = new ComponentBuilder("You are already in ").color(ChatColor.RED).create()[0];
		}

		BaseComponent comp2 = new ComponentBuilder(channel.getName()).color(ChatColor.WHITE).create()[0];

		p.sendMessage(new BaseComponent[] { prefix, comp, comp2 });
	}
}
