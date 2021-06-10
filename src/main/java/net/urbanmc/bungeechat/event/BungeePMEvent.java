package net.urbanmc.bungeechat.event;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;

public class BungeePMEvent extends Event implements Cancellable {

	private CommandSender sender;
	private CommandSender receiver;
	private String message;

	private boolean cancelled = false;

	public BungeePMEvent(CommandSender sender, CommandSender receiver, String message) {
		this.sender = sender;
		this.receiver = receiver;
		this.message = message;
	}

	public CommandSender getSender() {
		return this.sender;
	}

	public CommandSender getReceiver() {
		return this.receiver;
	}

	public String getMessage() {
		return this.message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	public BaseComponent[] getSenderLine() {
		String server;

		if (receiver instanceof ProxiedPlayer) {
			server = ((ProxiedPlayer) receiver).getServer().getInfo().getName();
		} else {
			server = "OP";
		}

		BaseComponent comp = new ComponentBuilder("[").color(ChatColor.GOLD).create()[0];
		BaseComponent comp2 = new ComponentBuilder("me ").color(ChatColor.RED).create()[0];
		BaseComponent comp3 = new ComponentBuilder("-> ").color(ChatColor.GOLD).create()[0];
		BaseComponent comp4 = new ComponentBuilder("[" + server + "] ").color(ChatColor.GOLD).create()[0];
		BaseComponent comp5 = new ComponentBuilder(receiver.getName()).color(ChatColor.RED).create()[0];
		BaseComponent comp6 = new ComponentBuilder("] ").color(ChatColor.GOLD).create()[0];
		BaseComponent comp7 = new ComponentBuilder(message).color(ChatColor.WHITE).create()[0];

		return new BaseComponent[] { comp, comp2, comp3, comp4, comp5, comp6, comp7 };
	}

	public BaseComponent[] getReceiverLine() {
		String server;

		if (sender instanceof ProxiedPlayer) {
			server = ((ProxiedPlayer) sender).getServer().getInfo().getName();
		} else {
			server = "OP";
		}

		BaseComponent comp = new ComponentBuilder("[" + server + "] ").color(ChatColor.GOLD).create()[0];
		BaseComponent comp2 = new ComponentBuilder("[").color(ChatColor.GOLD).create()[0];
		BaseComponent comp3 = new ComponentBuilder(sender.getName()).color(ChatColor.RED).create()[0];
		BaseComponent comp4 = new ComponentBuilder(" -> ").color(ChatColor.GOLD).create()[0];
		BaseComponent comp5 = new ComponentBuilder("me").color(ChatColor.RED).create()[0];
		BaseComponent comp6 = new ComponentBuilder("] ").color(ChatColor.GOLD).create()[0];
		BaseComponent comp7 = new ComponentBuilder(message).color(ChatColor.WHITE).create()[0];

		return new BaseComponent[] { comp, comp2, comp3, comp4, comp5, comp6, comp7 };
	}
}
