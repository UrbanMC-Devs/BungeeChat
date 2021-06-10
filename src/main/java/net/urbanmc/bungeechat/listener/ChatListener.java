package net.urbanmc.bungeechat.listener;

import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.urbanmc.bungeechat.BungeeChat;
import net.urbanmc.bungeechat.Channel;

public class ChatListener implements Listener {

	private BaseComponent prefix = new ComponentBuilder("[BungeeChat] ").color(ChatColor.GOLD).create()[0];

	@EventHandler
	public void onChat(ChatEvent e) {
		if (!(e.getSender() instanceof ProxiedPlayer))
			return;

		ProxiedPlayer p = (ProxiedPlayer) e.getSender();

		UUID id = p.getUniqueId();

		if (BungeeChat.getChannelsManager().inAuto(id) && !e.getMessage().startsWith("/")) {
			e.setCancelled(true);

			BungeeChat.getChannelsManager().getAuto(id).chatProcess(e, false);
		} else {
			String message = e.getMessage();

			if (!message.startsWith("/"))
				return;

			String[] messageSplit = message.split(" ");

			Channel channel = BungeeChat.getChannelsManager()
					.getChannelByCommand(messageSplit[0].substring(1).toLowerCase());

			if (channel == null)
				return;

			if (!p.hasPermission(channel.getPermission()))
				return;

			e.setCancelled(true);

			if (messageSplit.length == 1) {
				processSwap(p, channel);
			} else {
				channel.chatProcess(e, true);
			}
		}
	}

	private void processSwap(ProxiedPlayer p, Channel channel) {
		UUID id = p.getUniqueId();

		Channel auto = BungeeChat.getChannelsManager().getAuto(id);

		BaseComponent comp = new ComponentBuilder("Set to ").color(ChatColor.DARK_GREEN).create()[0];
		BaseComponent comp2;

		if (auto == null || !auto.getName().equals(channel.getName())) {
			BungeeChat.getChannelsManager().putAuto(id, channel);

			comp2 = new ComponentBuilder(channel.getName()).color(ChatColor.WHITE).create()[0];
		} else {
			BungeeChat.getChannelsManager().removeAuto(id);

			comp2 = new ComponentBuilder("none").color(ChatColor.WHITE).create()[0];
		}

		p.sendMessage(new BaseComponent[] { prefix, comp, comp2 });
	}
}
