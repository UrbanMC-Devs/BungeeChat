package net.urbanmc.bungeechat.event;

import java.util.List;

import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Cancellable;
import net.md_5.bungee.api.plugin.Event;
import net.urbanmc.bungeechat.Channel;

public class BungeeChatEvent extends Event implements Cancellable {

	private ProxiedPlayer sender;
	private Channel channel;
	private List<ProxiedPlayer> recipients;
	private TextComponent message;

	private boolean cancelled = false;

	public BungeeChatEvent(ProxiedPlayer sender, Channel channel, List<ProxiedPlayer> recipients, TextComponent message) {
		this.sender = sender;
		this.channel = channel;
		this.recipients = recipients;
		this.message = message;
	}

	public ProxiedPlayer getSender() {
		return this.sender;
	}

	public Channel getChannel() {
		return this.channel;
	}

	public List<ProxiedPlayer> getRecipients() {
		return this.recipients;
	}

	public void setRecipients(List<ProxiedPlayer> recipients) {
		this.recipients = recipients;
	}

	public TextComponent getMessage() {
		return this.message;
	}

	public void setMessage(TextComponent message) {
		this.message = message;
	}

	@Override
	public boolean isCancelled() {
		return this.cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}
}
