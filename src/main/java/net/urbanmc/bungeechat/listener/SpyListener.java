package net.urbanmc.bungeechat.listener;

import java.util.List;
import java.util.UUID;

import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.urbanmc.bungeechat.BungeeChat;

public class SpyListener implements Listener {

	@EventHandler
	public void onBungeePM(ChatEvent e) {
		CommandSender sender = (CommandSender) e.getSender();
		String cmd = e.getMessage();

		if (!cmd.startsWith("/"))
			return;

		List<String> commands = BungeeChat.getMessageCommands();
		commands.addAll(BungeeChat.getReplyCommands());

		boolean msgCommand = false;

		for (String command : commands) {
			if (cmd.toLowerCase().startsWith("/" + command)) {
				msgCommand = true;
			}
		}

		if (!msgCommand)
			return;

		BaseComponent[] comp = new ComponentBuilder(sender.getName() + " : " + cmd).create();

		for (UUID id : BungeeChat.getSpying()) {
			ProxiedPlayer p = ProxyServer.getInstance().getPlayer(id);

			if (p != null && !sender.getName().equals(p.getName())) {
				p.sendMessage(comp);
			}
		}
	}
}
