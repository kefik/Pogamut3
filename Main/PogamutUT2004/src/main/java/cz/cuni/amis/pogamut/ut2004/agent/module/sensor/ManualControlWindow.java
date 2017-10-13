package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

public class ManualControlWindow extends JFrame {
	
	public static interface IManualControlCallback {
		
		public void jump();
		public void raycast();
		public void drawLevelGeometry();
		public void drawLevelGeometryBSP();
		public void drawClear();
		public void drawNavPointVisibility();
		public void drawNavPointVisibilityWorldView();
		
	}
	
	public boolean forward;
	public boolean backward;
	public boolean left;
	public boolean right;
	
	public IManualControlCallback callback;
	
	public ManualControlWindow() {
		setBounds(10,10,200,30);
    	setVisible(true);
    	addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'w': forward = true; break;
				case 'a': left = true; break;
				case 's': backward = true; break;
				case 'd': right = true; break;
				case 'j': jump(); break;
				case ' ': jump(); break;
				case 'r': raycast(); break;
				case 'l': drawLevelGeometry(); break;
				case 'b': drawLevelGeometryBSP(); break;
				case 'n': drawNavPointVisibility(); break;
				case 'm': drawNavPointVisibilityWorldView(); break;
				case 'c': drawClear(); break;
				}
			}

			@Override
			public void keyReleased(KeyEvent e) {
				switch (e.getKeyChar()) {
				case 'w': forward = false; break;
				case 'a': left = false; break;
				case 's': backward = false; break;
				case 'd': right = false; break;
				}
			}    		
    	});
	}
	
	protected void drawClear() {
		if (callback == null) return;
		callback.drawClear();
	}

	protected void drawLevelGeometryBSP() {
		if (callback == null) return;
		callback.drawLevelGeometryBSP();
	}

	protected void drawLevelGeometry() {
		if (callback == null) return;
		callback.drawLevelGeometry();
	}

	private void raycast() {
		if (callback == null) return;
		callback.raycast();
	}

	private void jump() {
		if (callback == null) return;
		callback.jump();
	}
	
	protected void drawNavPointVisibilityWorldView() {
		if (callback == null) return;
		callback.drawNavPointVisibilityWorldView();
	}

	protected void drawNavPointVisibility() {
		if (callback == null) return;
		callback.drawNavPointVisibility();
	}

}
