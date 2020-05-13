/*
 * Part of the Game Control Plus library - http://www.lagers.org.uk/gamecontrol
 * 
 * Copyright (c) 2020 Peter Lager
 * <quark(a)lagers.org.uk> http:www.lagers.org.uk
 * 
 * This software is provided 'as-is', without any express or implied warranty.
 * In no event will the authors be held liable for any damages arising from
 * the use of this software.
 * 
 * Permission is granted to anyone to use this software for any purpose,
 * including commercial applications, and to alter it and redistribute it freely,
 * subject to the following restrictions:
 * 
 * 1. The origin of this software must not be misrepresented;
 * you must not claim that you wrote the original software.
 * If you use this software in a product, an acknowledgment in the product
 * documentation would be appreciated but is not required.
 * 
 * 2. Altered source versions must be plainly marked as such,
 * and must not be misrepresented as being the original software.
 * 
 * 3. This notice may not be removed or altered from any source distribution.
 * 
 */

package org.gamecontrolplus.gui;

import java.util.List;

import org.gamecontrolplus.Configuration;
import org.gamecontrolplus.ControlDevice;
import org.gamecontrolplus.ControlIO;

import processing.core.PApplet;
import processing.core.PConstants;

public class KSelectDeviceUI implements PConstants, KConstants {

	private ControlDevice selectedDevice = null;

	private ControlIO controlIO;
	
	private MWindow window;

	private int status = WORKING;

	public KSelectDeviceUI(PApplet app, Configuration config) {
		M4P.messagesEnabled(false);
		controlIO = ControlIO.getInstance(app);
		createGUI(app, controlIO.getDevices(), config);
	}

	private void createGUI(PApplet app, List<ControlDevice> devices, Configuration config){
		String title = "Select device for " + config.getUsage();
		window = MWindow.getWindow(app, title, 80, 40, 500, 20*(devices.size() + 3), PApplet.JAVA2D);
		MLabel lblControls = new MLabel(window, 0, 0, window.width, 20);
		lblControls.setText("Control devices");
		lblControls.setOpaque(true);
		lblControls.setTextBold();
		MButton btnExit = new MButton(window, window.width - 105, window.height-28, 100, 24);
		btnExit.setText("Exit Game");
		btnExit.addEventHandler(this, "exitClick");
		// Now add device selectors
		for(int i = 0; i < devices.size(); i++) {
			new KSelectEntry(this, devices.get(i), i);
		}
		window.addDrawHandler(this, "draw");
	}

	public void exitClick(MButton source, MEvent event) {
		window.noLoop();
		controlIO.setConfiguredDevice(null);
		status = CANCELLED;
	}

	synchronized public void draw(PApplet appc, MWinData data) {
		appc.background(255, 255, 220);
		appc.stroke(230, 230, 200);
		appc.fill(240, 240, 210);
		int y =0;
		while(y < appc.height){
			appc.rect(0,y,appc.width,20);
			y += 40;
		}
		appc.fill(200,255,200);
		appc.rect(0,appc.height-30,appc.width,30);
	}

	// Called when a device has been selected
	void setSelectedDevice(ControlDevice device) {
		window.noLoop();
		controlIO.setConfiguredDevice(device);
		status = FINISHED;
	}

	public MWindow getWindow() {
		return window;
	}
	
	// Poll this to see if it has been selected
	public ControlDevice getSelectedDevice() {
		return selectedDevice;
	}

	public int status() {
		return status;
	}

	// Will be called once a device has been selected
	public void finishedWith(){
		window.noLoop();
		window.forceClose();
	}

}
