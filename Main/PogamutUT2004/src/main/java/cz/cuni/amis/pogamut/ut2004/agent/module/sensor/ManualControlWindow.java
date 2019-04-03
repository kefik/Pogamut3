package cz.cuni.amis.pogamut.ut2004.agent.module.sensor;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JFrame;
import javax.swing.JLabel;

public class ManualControlWindow extends JFrame {
	
	public static interface IManualControlCallback {
		
		public void jump();
		public void raycast();
		public void raycastNavMesh();
		public void drawLevelGeometry();
		public void drawLevelGeometryBSP();
		public void drawClear();
		public void drawNavPointVisibility();
		public void drawNavPointVisibilityWorldView();
		public void drawItemsVisibility();
		public void drawNavMesh();
		public void drawNavMeshWithLinks();		
		public void sendGlobalMessage(String msg);
		
		public boolean isActive();
		public void toggleActive();
		
	}
	
	public boolean forward;
	public boolean backward;
	public boolean left;
	public boolean right;
	
	public IManualControlCallback callback;
	
	private JLabel label1;
	private JLabel label2;
	
	public ManualControlWindow() {
		setBounds(10,10,370,90);
    	setVisible(true);
    	
    	setLayout(null);
    	
    	label1 = new JLabel("Find bot in UT2004; focus this window and press 'h' for help.");
    	label1.setBounds(5, 5, 400, 20);
    	add(label1);
    	
    	label2 = new JLabel("ACTIVE");
    	label2.setBounds(5, 25, 400, 20);
    	add(label2);
    	
    	
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
				case ' ': jump(); break;
				case 'r': raycast(); break;
				case 't': raycastNavmesh(); break;
				case 'l': drawLevelGeometry(); break;
				case 'b': drawLevelGeometryBSP(); break;
				case 'v': drawNavPointVisibility(); break;
				case 'g': drawNavPointVisibilityWorldView(); break;
				case 'i': drawItemsVisibility(); break;
				case 'c': drawClear(); break;
				case 'n': drawNavMesh(); break;
				case 'm': drawNavMeshWithLinks(); break;
				case 'h': help(); break;
				case 'e': toggleActive(); break;
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
	
	protected void raycastNavmesh() {
		if (callback == null) return;
		callback.raycastNavMesh();
	}

	protected void toggleActive() {
		if (callback == null) return;
		callback.toggleActive();
		if (callback.isActive()) {
			callback.sendGlobalMessage("MANUAL CONTROL ACTIVATED");
			label2.setText("ACTIVE");
		}
		else {
			callback.sendGlobalMessage("MANUAL CONTROL DEACTIVATED");
			label2.setText("DEACTIVATED");
		}
	}

	protected void help() {
		if (callback == null) return;
		callback.sendGlobalMessage("Control: WSAD+Space, E - De/Activate");
		callback.sendGlobalMessage("Level geom - L; Level geom BSP - B; Raycast - R; I - items visibility");
		callback.sendGlobalMessage("NavMesh - N; NavMesh+Links - M; Raycast NavMesh - T; Clear - C");
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
	
	protected void drawNavMeshWithLinks() {
		if (callback == null) return;
		callback.drawNavMeshWithLinks();
	}
	
	protected void drawItemsVisibility() {
		if (callback == null) return;
		callback.drawItemsVisibility();
	}

	protected void drawNavMesh() {
		callback.drawNavMesh();		
	}

}
