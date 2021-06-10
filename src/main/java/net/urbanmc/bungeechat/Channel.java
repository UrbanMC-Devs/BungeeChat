package net.urbanmc.bungeechat;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;
import net.urbanmc.bungeechat.event.BungeeChatEvent;

public class Channel {

	private static Map<UUID, Long> muted;

	private String name;
	private String tag;
	private List<String> commands;
	private char messageColor;
	private String permission;
	private String format;

	private ChatColor color;

	private String defaultFormat = "%tag%§r [§6%server%§r] %prefix%displayname%§r%suffix%§r: %message%";

	private List<UUID> absent;

	static {
		muted = new HashMap<UUID, Long>();

		loadMuted();
	}

	public Channel(String name, String tag, List<String> commands, char messageColor, String format) {
		this.name = name;
		this.tag = tag;
		this.commands = commands;
		this.messageColor = messageColor;
		this.permission = "bungeechat.channel." + name;
		this.format = format;

		this.color = ChatColor.getByChar(messageColor);
	}

	public String getName() {
		return this.name;
	}

	public String getTag() {
		return this.tag;
	}

	public List<String> getCommands() {
		return this.commands;
	}

	public char getMessageColor() {
		return this.messageColor;
	}

	public ChatColor getColor() {
		return this.color;
	}

	public String getPermission() {
		return this.permission;
	}

	public String getFormat() {
		return this.format;
	}

	public boolean isAbsent(UUID id) {
		if (this.absent == null)
			return false;

		return this.absent.contains(id);
	}

	public void join(UUID id) {
		if (this.absent != null && this.absent.contains(id)) {
			this.absent.remove(id);
		}
	}

	public void leave(UUID id) {
		if (this.absent == null) {
			this.absent = new ArrayList<UUID>();
		}

		if (!this.absent.contains(id)) {
			this.absent.add(id);
		}
	}

	public void chatProcess(ChatEvent e, boolean direct) {
		ProxiedPlayer sender = (ProxiedPlayer) e.getSender();
		UUID id = sender.getUniqueId();

		if (isMuted(id)) {
			long timeout = getMuteTimeout(id);

			if (timeout < System.currentTimeMillis() && timeout != 0) {
				unmute(id);
			} else {
				sender.sendMessage(
						new ComponentBuilder("Your voice has been silenced!").color(ChatColor.GOLD).create());
				return;
			}
		}

		String chat = e.getMessage();

		if (direct) {
			int length = chat.split(" ")[0].length();

			chat = chat.substring(length + 1);
		}

		if (chat.contains("&") && sender.hasPermission("bungeechat.color")) {
			chat = ChatColor.translateAlternateColorCodes('&', chat);
		}

		List<ProxiedPlayer> recipients = new ArrayList<ProxiedPlayer>();

		for (ProxiedPlayer p : ProxyServer.getInstance().getPlayers()) {
			if (!p.hasPermission(this.permission))
				continue;

			if (isAbsent(p.getUniqueId()))
				continue;

			recipients.add(p);
		}

		Group group = BungeeChat.getGroups().getGroup(id);

		String format = this.format;

		if (format == null) {
			format = this.defaultFormat;
		}

		format = format.replace("%tag%", getTag());
		format = format.replace("%server%", sender.getServer().getInfo().getName());
		format = format.replace("%prefix", group.getPrefix());
		format = format.replace("%username%", sender.getName());
		format = format.replace("%displayname%", sender.getDisplayName());
		format = format.replace("%suffix%", group.getSuffix());
		format = format.replace("%message%", getColor() + chat);

		TextComponent message = format(format);

		BungeeChatEvent event = new BungeeChatEvent(sender, this, recipients, message);

		ProxyServer.getInstance().getPluginManager().callEvent(event);

		if (event.isCancelled())
			return;

		for (ProxiedPlayer p : event.getRecipients()) {
			p.sendMessage(event.getMessage());
		}
	}

	private TextComponent format(String message) {
		TextComponent mainComp = new TextComponent("");
		String[] split = message.split("(?=§)");

		for (String arg : split) {
			TextComponent comp = new TextComponent("");

			ChatColor color;

			if (arg.startsWith("§")) {
				color = ChatColor.getByChar(arg.charAt(1));
				arg = arg.substring(2);
			} else {
				color = ChatColor.WHITE;
			}

			comp.setColor(color);

			String[] spaced = arg.split("(?<= )");

			for (String space : spaced) {
				TextComponent builder = new TextComponent(space);

				if (space.contains(".")) {
					String set = space;

					if (!set.toLowerCase().startsWith("http://") && !set.toLowerCase().startsWith("https://")) {
						set = "http://" + set;
					}

					builder.setClickEvent(new ClickEvent(Action.OPEN_URL, set));
				}

				comp.addExtra(builder.duplicate());
			}

			mainComp.addExtra(comp.duplicate());
		}

		return mainComp;
	}

	public static boolean isMuted(UUID id) {
		return muted.containsKey(id);
	}

	public static void mute(UUID id, long millis) {
		muted.put(id, millis);
		saveMuted();
	}

	public static void unmute(UUID id) {
		muted.remove(id);
		saveMuted();
	}

	public static long getMuteTimeout(UUID id) {
		return muted.get(id);
	}

	private static void loadMuted() {
		File file = new File("plugins/BungeeChat/muted.yml");

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

		Configuration sect = data.getSection("muted");

		for (String id : sect.getKeys()) {
			muted.put(UUID.fromString(id), sect.getLong(id));
		}
	}

	private static void saveMuted() {
		File file = new File("plugins/BungeeChat/muted.yml");

		Configuration data;

		try {
			data = ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
		} catch (IOException ex) {
			ex.printStackTrace();
			return;
		}

		Map<String, Long> stringMuted = new HashMap<String, Long>();

		for (Entry<UUID, Long> entry : muted.entrySet()) {
			stringMuted.put(entry.getKey().toString(), entry.getValue());
		}

		data.set("muted", stringMuted);
	}
}
