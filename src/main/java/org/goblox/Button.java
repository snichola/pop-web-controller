package org.goblox;

import java.awt.event.KeyEvent;

public enum Button {
	
	LW(KeyEvent.VK_1),
	LY(KeyEvent.VK_2),
	LG(KeyEvent.VK_3),
	LB(KeyEvent.VK_4),
	MR(KeyEvent.VK_5),
	RB(KeyEvent.VK_6),
	RG(KeyEvent.VK_7),
	RY(KeyEvent.VK_8),
	RW(KeyEvent.VK_9);
	
	private int key;

	Button(int key) {
		this.key = key;
	}

	public int getKey() {
		return key;
	}

}
