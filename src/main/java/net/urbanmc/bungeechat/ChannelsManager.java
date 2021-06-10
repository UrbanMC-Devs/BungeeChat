package net.urbanmc.bungeechat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class ChannelsManager {

	private Configuration data;

	private Map<String, Channel> channels;
	private Map<UUID, Channel> auto;

	public ChannelsManager(Configuration data) {
		this.data = data;

		this.channels = new HashMap<String, Channel>();
		this.auto = new HashMap<UUID, Channel>();
	}

	public void loadChannels() {
		Configuration section = data.getSection("channels");

		for (String name : section.getKeys()) {
			String tag = ChatColor.translateAlternateColorCodes('&', section.getString(name + ".tag"));

			List<String> commands = new ArrayList<String>();

			for (String command : section.getStringList(name + ".commands")) {
				commands.add(command.toLowerCase());
			}

			char messageColor = section.getString(name + ".color").charAt(0);

			String format = section.getString(name + ".format");

			if (format != null && format.length() == 0) {
				format = null;
			}

			if (format != null) {
				format = ChatColor.translateAlternateColorCodes('&', format);
			}

			Channel channel = new Channel(name, tag, commands, messageColor, format);

			this.channels.put(name, channel);
		}
	}

	public void addChannel(Channel channel) {
		this.channels.put(channel.getName(), channel);
	}

	public void removeChannel(Channel channel) {
		this.channels.remove(channel.getName());
	}

	public boolean isChannelRegistered(Channel channel) {
		return this.channels.containsKey(channel.getName());
	}

	public Channel getChannelByName(String name) {
		return this.channels.get(name);
	}

	public Channel getChannelByCommand(String command) {
		for (Channel channel : getChannels().values()) {
			for (String channelCommand : channel.getCommands()) {
				if (channelCommand.equals(command))
					return channel;
			}
		}

		return null;
	}

	public Map<String, Channel> getChannels() {
		return this.channels;
	}

	public boolean inAuto(UUID id) {
		return this.auto.containsKey(id);
	}

	public Channel getAuto(UUID id) {
		return this.auto.get(id);
	}

	public void putAuto(UUID id, Channel channel) {
		this.auto.put(id, channel);
	}

	public void removeAuto(UUID id) {
		this.auto.remove(id);
	}
}
