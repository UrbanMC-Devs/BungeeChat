package net.urbanmc.bungeechat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.api.plugin.PluginManager;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.urbanmc.bungeechat.commands.Join;
import net.urbanmc.bungeechat.commands.Leave;
import net.urbanmc.bungeechat.commands.Message;
import net.urbanmc.bungeechat.commands.Mute;
import net.urbanmc.bungeechat.commands.Reply;
import net.urbanmc.bungeechat.commands.Spy;
import net.urbanmc.bungeechat.listener.ChatListener;
import net.urbanmc.bungeechat.listener.TabCompletor;
import net.urbanmc.bungeechat.listener.SpyListener;

public class BungeeChat extends Plugin {

	private static ChannelsManager manager;
	private static Groups groups;

	private static Map<String, String> replyMap;
	private static List<UUID> spyList;

	private static Message message;
	private static Reply reply;

	@Override
	public void onEnable() {
		setupChannelManager();
		setupGroups();

		replyMap = new HashMap<String, String>();
		spyList = new ArrayList<UUID>();

		loadSpy();

		message = new Message();
		reply = new Reply();

		PluginManager pm = getProxy().getPluginManager();

		pm.registerListener(this, new ChatListener());
		pm.registerListener(this, new TabCompletor());
		pm.registerListener(this, new SpyListener());

		pm.registerCommand(this, new Join());
		pm.registerCommand(this, new Leave());
		pm.registerCommand(this, message);
		pm.registerCommand(this, new Mute());
		pm.registerCommand(this, reply);
		pm.registerCommand(this, new Spy());
	}

	private void setupChannelManager() {
		File file = new File("plugins/BungeeChat/channels.yml");

		if (!file.getParentFile().isDirectory()) {
			file.getParentFile().mkdir();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();

				InputStream input = this.getClass().getClassLoader().getResourceAsStream("channels.yml");
				OutputStream output = new FileOutputStream(file);

				copy(input, output);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		Configuration data = null;

		try {
			data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		if (data != null) {
			manager = new ChannelsManager(data);
			manager.loadChannels();
		} else {
			getLogger().log(Level.SEVERE, "Error enabling BungeeChat");
		}
	}

	private void setupGroups() {
		File file = new File("plugins/BungeeChat/groups.yml");

		if (!file.getParentFile().isDirectory()) {
			file.getParentFile().mkdir();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();

				InputStream input = this.getClass().getClassLoader().getResourceAsStream("groups.yml");
				OutputStream output = new FileOutputStream(file);

				copy(input, output);
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		Configuration data = null;

		try {
			data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}

		groups = new Groups(data);
		groups.loadGroups();
	}

	private static void loadSpy() {
		File file = new File("plugins/BungeeChat/spy.yml");

		if (!file.getParentFile().isDirectory()) {
			file.getParentFile().mkdir();
		}

		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		Configuration data;

		try {
			data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}

		for (String id : data.getStringList("spy")) {
			spyList.add(UUID.fromString(id));
		}
	}

	public static ChannelsManager getChannelsManager() {
		return BungeeChat.manager;
	}

	public static Groups getGroups() {
		return BungeeChat.groups;
	}

	public static Map<String, String> getReplyMap() {
		return BungeeChat.replyMap;
	}

	public static List<UUID> getSpying() {
		return BungeeChat.spyList;
	}

	public static boolean isSpying(UUID id) {
		return spyList.contains(id);
	}

	public static void addSpying(UUID id) {
		spyList.add(id);
		saveSpying();
	}

	public static void removeSpying(UUID id) {
		spyList.remove(id);
		saveSpying();
	}

	private static void saveSpying() {
		File file = new File("plugins/BungeeChat/spy.yml");

		Configuration data;

		try {
			data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}

		List<String> spying = new ArrayList<String>();

		for (UUID id : spyList) {
			spying.add(id.toString());
		}

		data.set("spy", spying);

		try {
			ConfigurationProvider.getProvider(YamlConfiguration.class).save(data, file);
		} catch (IOException ex) {
			ex.printStackTrace();
		}
	}

	public static List<String> getMessageCommands() {
		List<String> commands = new ArrayList<String>();

		commands.add(message.getName());

		for (String alias : message.getAliases()) {
			commands.add(alias);
		}

		return commands;
	}

	public static List<String> getReplyCommands() {
		List<String> commands = new ArrayList<String>();

		commands.add(reply.getName());

		for (String alias : reply.getAliases()) {
			commands.add(alias);
		}

		return commands;
	}

	private void copy(InputStream input, OutputStream output) throws IOException {
		byte[] buffer = new byte[4096];

		int n;
		while (-1 != (n = input.read(buffer))) {
			output.write(buffer, 0, n);
		}
	}
}
