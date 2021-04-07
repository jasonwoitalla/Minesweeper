package com.mine.driver;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.Timer;
import java.util.TimerTask;

public class MouseInput implements MouseListener {

	Graphics graphics;
	Timer timer = new Timer();
	public MouseInput(Graphics graphics) {
		this.graphics = graphics;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() == MouseEvent.BUTTON1)
			graphics.leftMouseClick(e.getX(), e.getY());
		else if(e.getButton() == MouseEvent.BUTTON3)
			graphics.rightMouseClick(e.getX(), e.getY());
		
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				graphics.unClick();
			}
		}, 250);
	}

	@Override
	public void mouseReleased(MouseEvent e) {

	}

	@Override
	public void mouseEntered(MouseEvent e) {

	}

	@Override
	public void mouseExited(MouseEvent e) {

	}

}
