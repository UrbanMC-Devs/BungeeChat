package net.urbanmc.bungeechat;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.config.Configuration;

public class Groups {

	private Configuration data;
	private Configuration groupsSection;

	private Map<UUID, Group> users;

	public Groups(Configuration data) {
		this.data = data;
		this.groupsSection = data.getSection("groups");
	}

	public void loadGroups() {
		users = new HashMap<UUID, Group>();
		Configuration userSection = data.getSection("users");

		for (String id : userSection.getKeys()) {
			users.put(UUID.fromString(id), getGroupByName(userSection.getString(id)));
		}
	}

	public Group getGroup(UUID id) {
		Group group = users.get(id);

		if (group != null)
			return group;
		else
			return new Group("default", "", "");
	}

	public Group getGroupByName(String name) {
		String prefix = ChatColor.translateAlternateColorCodes('&', groupsSection.getString(name + ".prefix"));
		String suffix = ChatColor.translateAlternateColorCodes('&', groupsSection.getString(name + ".suffix"));

		return new Group(name, prefix, suffix);
	}
}
