/*
 * Decompiled with CFR 0_114.
 */
package net.md_5.bungee.api.chat;

import java.beans.ConstructorProperties;

public final class ClickEvent {
	private final Action action;
	private final String value;

	public Action getAction() {
		return action;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "ClickEvent(action=" + (getAction()) + ", value=" + getValue() + ")";
	}

	@ConstructorProperties(value = { "action", "value" })
	public ClickEvent(Action action, String value) {
		this.action = action;
		this.value = value;
	}

	public static enum Action {
		OPEN_URL, OPEN_FILE, RUN_COMMAND, SUGGEST_COMMAND, CHANGE_PAGE;

		private Action() {}
	}

}
