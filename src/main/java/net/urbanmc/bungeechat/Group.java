package net.urbanmc.bungeechat;

public class Group {

	private String name;
	private String prefix;
	private String suffix;

	public Group(String name, String prefix, String suffix) {
		this.name = name;
		this.prefix = prefix;
		this.suffix = suffix;
	}

	public String getName() {
		return this.name;
	}

	public String getPrefix() {
		return this.prefix;
	}

	public String getSuffix() {
		return this.suffix;
	}
}
