package net.urbanmc.bungeechat.listener;

import java.util.List;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.urbanmc.bungeechat.BungeeChat;
import net.urbanmc.bungeechat.Channel;

public class TabCompletor implements Listener {

	@EventHandler
	public void onTabCompleteChat(TabCompleteEvent e) {
		CommandSender sender = (CommandSender) e.getSender();

		String msg = e.getCursor().toLowerCase();
		String[] args = msg.split(" ");

		if (!msg.startsWith("/"))
			return;

		Channel channel = BungeeChat.getChannelsManager().getChannelByCommand(args[0].substring(1));

		if (channel == null)
			return;

		if (!sender.hasPermission(channel.getPermission()))
			return;

		int length = 0;

		for (String arg : args) {
			length += arg.length();
		}

		if (msg.length() - length == args.length) {
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				e.getSuggestions().add(p.getName());
			}
		} else {
			for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
				if (p.getName().toLowerCase().startsWith(args[args.length - 1])) {
					e.getSuggestions().add(p.getName());
				}
			}
		}
	}

	@EventHandler
	public void onTabCompletePM(TabCompleteEvent e) {
		CommandSender sender = (CommandSender) e.getSender();

		if (!sender.hasPermission("bungeechat.message") && !sender.hasPermission("bungeechat.reply"))
			return;

		String msg = e.getCursor().toLowerCase();
		String[] args = msg.split(" ");

		List<String> commands = BungeeChat.getMessageCommands();
		commands.addAll(BungeeChat.getReplyCommands());

		int length = 0;

		for (String arg : args) {
			length += arg.length();
		}

		for (String command : commands) {
			if (msg.startsWith("/" + command)) {
				if (msg.length() - length == args.length) {
					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						e.getSuggestions().add(p.getName());
					}
				} else {
					for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
						if (p.getName().toLowerCase().startsWith(args[args.length - 1])) {
							e.getSuggestions().add(p.getName());
						}
					}
				}
			}
		}
	}
}
