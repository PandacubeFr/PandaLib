/*
 * Decompiled with CFR 0_114.
 */
package net.md_5.bungee.api.chat;

import java.beans.ConstructorProperties;
import java.util.Arrays;

public final class HoverEvent {
	private final Action action;
	private final BaseComponent[] value;

	public Action getAction() {
		return action;
	}

	public BaseComponent[] getValue() {
		return value;
	}

	@Override
	public String toString() {
		return "HoverEvent(action=" + (getAction()) + ", value=" + Arrays.deepToString(getValue()) + ")";
	}

	@ConstructorProperties(value = { "action", "value" })
	public HoverEvent(Action action, BaseComponent[] value) {
		this.action = action;
		this.value = value;
	}

	public static enum Action {
		SHOW_TEXT, SHOW_ACHIEVEMENT, SHOW_ITEM, SHOW_ENTITY;

		private Action() {}
	}

}
