package net.urbanmc.bungeechat.commands;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.urbanmc.bungeechat.Channel;

public class Mute extends Command {

	public Mute() {
		super("bcmute", "bungeechat.mute", "gmute");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(new ComponentBuilder("Please specify a player to mute.").color(ChatColor.RED).create());
			return;
		}

		ProxiedPlayer p = ProxyServer.getInstance().getPlayer(args[0]);

		BaseComponent[] notFound = new ComponentBuilder("Player not found!").color(ChatColor.RED).create();

		if (p == null) {
			sender.sendMessage(notFound);
			return;
		}

		if (p.hasPermission("bungeechat.mute.exempt") && sender instanceof ProxiedPlayer) {
			BaseComponent comp1 = new ComponentBuilder("Error: ").color(ChatColor.RED).create()[0];
			BaseComponent comp2 = new ComponentBuilder("You may not mute that player!").color(ChatColor.DARK_RED)
					.create()[0];
			sender.sendMessage(new BaseComponent[] { comp1, comp2 });
			return;
		}

		UUID id = p.getUniqueId();

		long time = 0;

		if (args.length > 1) {
			try {
				time = System.currentTimeMillis() + getTime(args[1]);
			} catch (ScriptException e) {
				sender.sendMessage(
						new ComponentBuilder("You have entered an invalid time!").color(ChatColor.RED).create());
				return;
			}
		}

		if (time == 0 && Channel.isMuted(id)) {
			Channel.unmute(id);
		} else {
			Channel.mute(id, time);
		}
		ComponentBuilder muted = new ComponentBuilder(
				p.getName() + " is " + (Channel.isMuted(id) ? "now" : "no longer") + " muted.").color(ChatColor.GOLD);

		sender.sendMessage(muted.create());
	}

	private long getTime(String time) throws ScriptException {
		time = time.replace("sec", "s");
		time = time.replace("seconds", "s");
		time = time.replace("min", "m");
		time = time.replace("minutes", "m");
		time = time.replace("hr", "h");
		time = time.replace("hours", "h");
		time = time.replace("days", "d");

		time = time.replace("s", "+");
		time = time.replace("m", "*60+");
		time = time.replace("h", "*60*60+");
		time = time.replace("d", "*60*60*24+");
		time = time.replace("w", "*60*60*24*7+");
		time = time.replace("y", "*60*60*24*365+");

		ScriptEngineManager manager = new ScriptEngineManager();
		ScriptEngine engine = manager.getEngineByName("js");

		String resultString = "";

		try {
			double result = (double) engine.eval(removeFinalChar(time));

			resultString = Double.toString((int) result);
			int index = resultString.indexOf(".");

			resultString = resultString.substring(0, index);
		} catch (ClassCastException cce) {
			int result = (int) engine.eval(removeFinalChar(time));

			resultString = Integer.toString(result);
		}

		long newTime = (long) TimeUnit.SECONDS.toMillis(Long.parseLong(resultString));
		return newTime;
	}

	private String removeFinalChar(String str) {
		if (str.length() > 0 && str.charAt(str.length() - 1) == '+') {
			str = str.substring(0, str.length() - 1);
		}

		return str;
	}
}
