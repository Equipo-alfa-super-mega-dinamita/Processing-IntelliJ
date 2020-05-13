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

import java.awt.Font;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gamecontrolplus.Configuration;
import org.gamecontrolplus.ControlButton;
import org.gamecontrolplus.ControlDevice;
import org.gamecontrolplus.ControlHat;
import org.gamecontrolplus.ControlIO;
import org.gamecontrolplus.ControlInput;
import org.gamecontrolplus.ControlSlider;

import processing.core.PApplet;
import processing.core.PConstants;
import processing.event.MouseEvent;

public class KConfigDeviceUI implements PConstants, KConstants {

	private final PApplet app;
	private final ControlIO controlIO;
	
	private MWindow window;
	private MTextArea txaStatus;

	// Status
	private int status = WORKING;
	
	private final ControlDevice device;
	private Configuration config;

	private KConnector start = null;
	private KConnector end = null;
	private KConnector current = null;

	float scale;
	float input_UI_height;
	float desc_UI_height;
	float element_UI_gap;
	float input_UI_length;
	float desc_UI_length;
	float textfield_gap;
	float indicator_d;
	float connector_size_r;  // radius
	float connector_size_d;  // diameter
	float fontSize;
	Font font;

	float spaceNeeded;
	float spaceForDescs;
	float spaceForInputs;
	private int winHeight;

	private boolean dragging = false;

	private List<KBase> uiElements = new ArrayList<KBase>();
	private List<KConnector> uiConnections = new ArrayList<KConnector>();

	private Map<String, KBaseInput> devInpKeys = new HashMap<String, KBaseInput>();
	private Map<String, KDescriptor> descriptors = new HashMap<String, KDescriptor>();

	private StringBuffer report;
	private int errCount = 0;


	public KConfigDeviceUI(PApplet app, Configuration config, ControlDevice device) {
		this.app = app;
		this.controlIO = ControlIO.getInstance(app);
		this.device = device;
		device.open();
		this.config = config;

		createMetrics();
		createGUI(app);
	}

	private void createGUI(PApplet app) {
		M4P.messagesEnabled(false);
		// CREATE THE WINDOW
		String title = "'" + device.getName() + "'  [" + device.getTypeName() + " on " + device.getPortTypeName() + "]"; 
		window = MWindow.getWindow(app, title, 80, 100, 1020, winHeight, M4P.JAVA2D);
		//window.setResizable(false);
		
		M4P.setCursor(CROSS);	

		// Create the control panel
		float px = window.width - PANEL_WIDTH + 10;
		float pw = PANEL_WIDTH - 20;
		float py = 10;
		MLabel lblFilenamePrompt = new MLabel(window, px, py, pw, 20, "Config. for: " + config.getUsage());
		lblFilenamePrompt.setTextAlign(MAlign.LEFT, null);
		lblFilenamePrompt.setLocalColorScheme(M4P.GREEN_SCHEME);
		lblFilenamePrompt.setTextBold();
		lblFilenamePrompt.setOpaque(true);
		py += 26;
		float bw = (pw - 20)/3;
		MButton btnClearStatus = new MButton(window, px, py, bw, 20);
		btnClearStatus.setLocalColorScheme(M4P.GREEN_SCHEME);
		btnClearStatus.setText("Clear Status");
		btnClearStatus.addEventHandler(this, "clear_click");
		MButton btnVerify = new MButton(window, px + (pw - bw)/2, py, bw, 20);
		btnVerify.setLocalColorScheme(M4P.GREEN_SCHEME);
		btnVerify.setText("Verify");
		btnVerify.addEventHandler(this, "verify_click");
		MButton btnSave = new MButton(window, px + pw - bw, py, bw, 20);
		btnSave.setLocalColorScheme(M4P.GREEN_SCHEME);
		btnSave.setText("USE");
		btnSave.addEventHandler(this, "use_device_click");
		py += 26;
		MLabel lblStatus = new MLabel(window, px, py, pw, 20, "VERIFY / SAVE STATUS REPORT");
		lblStatus.setLocalColorScheme(M4P.GREEN_SCHEME);
		lblStatus.setTextBold();
		lblStatus.setOpaque(true);
		py += 22;
		txaStatus = new MTextArea(window, px, py, pw, 140, M4P.SCROLLBARS_VERTICAL_ONLY);
		txaStatus.setLocalColorScheme(M4P.GREEN_SCHEME);
		txaStatus.setPromptText("Verify / save status report");
		py += txaStatus.getHeight() + 4;
		MButton btnQuit = new MButton(window, px, py, pw, 20);
		btnQuit.setLocalColorScheme(M4P.RED_SCHEME);
		btnQuit.setText("CANCEL CONFIGURATION AND EXIT");
		btnQuit.addEventHandler(this, "quit_click");

		window.textSize(fontSize);

		addConfigToGUI(spaceNeeded, spaceForDescs, spaceForInputs);
		makeExistingConnections();

		window.addDrawHandler(this, "draw");
		window.addMouseHandler(this, "mouse");
		window.addPreHandler(this, "pre");
		window.loop();
	}
	
	private void createMetrics() {		
		spaceForInputs = ELEMENT_UI_GAP;
		// Scan through controls to calculate the window height needed
		for(ControlInput input : device.getInputs()){
			if(input instanceof ControlHat){
				spaceForInputs += 5 * INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
			}
			else  if(input instanceof ControlButton){
				spaceForInputs += INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
			}
			else  if(input instanceof ControlSlider){
				spaceForInputs += 4 * INPUT_UI_HEIGHT + ELEMENT_UI_GAP + 2;
			}
			else
				System.out.println("Unknown input " + input);	
		}
		spaceForDescs = config.nbrInputs() * (DESC_UI_HEIGHT + ELEMENT_UI_GAP + 2);
		spaceNeeded = Math.max(spaceForInputs, spaceForDescs);
		spaceNeeded = Math.max(spaceNeeded, PANEL_HEIGHT);
		// Now calculate window scaling and height
		if(app.displayHeight < spaceNeeded + 40)
			scale = app.displayHeight / (spaceNeeded + 40);	
		else
			scale = 1.0f;
		winHeight = Math.round(spaceNeeded  * scale);

		// Apply scaling
		input_UI_height = INPUT_UI_HEIGHT * scale;
		desc_UI_height = DESC_UI_HEIGHT * scale;
		element_UI_gap  = ELEMENT_UI_GAP * scale;
		input_UI_length = INPUT_UI_LENGTH;
		desc_UI_length = DESC_UI_LENGTH;
		textfield_gap = TEXTFIELD_GAP * scale;
		indicator_d = INICATOR_D * scale;
		connector_size_r = CONNECTOR_SIZE_R * scale;		
		connector_size_d = 2 * connector_size_r;
		fontSize = FONT_SIZE * scale;
		font = new Font("Dialog", Font.PLAIN, (int)fontSize);
	}
	
	private void addConfigToGUI(float spaceNeeded, float spaceForDescs, float spaceForInputs){
		float px, py;
		// Create and add device inputs to UI 
		px = window.width - 10 - INPUT_UI_LENGTH - PANEL_WIDTH;
		py = ELEMENT_UI_GAP + (spaceNeeded - spaceForInputs) / 2; 

		for(ControlInput input : device.getInputs()){
			KBaseInput ui = KBaseInput.makeInputUI(this, input, px, py);
			if(ui != null){
				uiElements.add(ui);
				py += ui.UI_HEIGHT + ELEMENT_UI_GAP;
				devInpKeys.put(ui.name, ui);
			}
		}
		// Create and add descriptors to UI 
		px = 10;
		py = ELEMENT_UI_GAP + (spaceNeeded - spaceForDescs) / 2; 
		for(Configuration.InputConfig iconfig : config.getGameInputs()){
			KDescriptor ui = new KDescriptor(this, px, py, iconfig);
			uiElements.add(ui);
			descriptors.put(ui.name, ui);
			py += ui.UI_HEIGHT + ELEMENT_UI_GAP;
		}
		// Now create list of connectors
		for(KBase ui : uiElements)
			for(KConnector c : ui.connectors)
				uiConnections.add(c);
	}

	/**
	 * Make existing connections between game inputs and device inputs based on type and name.
	 */
	private void makeExistingConnections(){
		for(Configuration.InputConfig iconfig : config.getGameInputs()){
			KBaseInput di = devInpKeys.get(iconfig.deviceInputName);
			if(di != null && iconfig.type == di.uiType && iconfig.inputConNo < di.getNbrOfConnectors()){
				KDescriptor descUI = descriptors.get(iconfig.key);
				descUI.connectors[0].conTo = di.connectors[iconfig.inputConNo];
				di.connectors[iconfig.inputConNo].conTo = descUI.connectors[0];
				di.setMultiplier(iconfig.multiplier);
				di.setTolerance(iconfig.tolerance);
			}
		}
	}
	
	private void addToReport(String line, boolean isError){
		report.append(line);
		if(isError) errCount++;
	}

	/**
	 * Verify the configuration
	 * @param chain
	 * @return
	 */
	private boolean verifyConfig(boolean chain){
		report = new StringBuffer();
		for(Configuration.InputConfig iconfig : config.getGameInputs()){
			KDescriptor descUI = descriptors.get(iconfig.key);
			KConnector con = descUI.connectors[0].conTo;
			if(con != null){
				iconfig.deviceInputName = con.owner.name;
				iconfig.inputConNo = con.conNo;
				iconfig.multiplier = ((KBaseInput)con.owner).getMultiplier();
				iconfig.tolerance = ((KBaseInput)con.owner).getTolerance();
			}
			else {
				addToReport("No input assigned to: " + descUI.name + "\n", true);
			}	
		}
		if(errCount > 0)
			addToReport("VERIFY - " + errCount + " errors found\n", false);
		else
			addToReport("VERIFY - successful\n", false);
		if(!chain)
			txaStatus.setText(report.toString());
		return errCount == 0;
	}

	private boolean saveConfig(){
		if(!verifyConfig(true)){
			addToReport("SAVE - abandoned\n", false);
			txaStatus.setText(report.toString());
			return false;
		}
		Configuration.saveConfiguration(app, config);
		return true;
	}


	public void verify_click(MButton button, MEvent event) { 
		verifyConfig(false);
	}

	// What to do when we select USE button
	public void use_device_click(MButton button, MEvent event) { 
		if(saveConfig()){
			device.matches(config);
			controlIO.finishedConfig(device);
			status = FINISHED;
		}
	}

	public void quit_click(MButton button, MEvent event) { 
		controlIO.finishedConfig(null);
		status = CANCELLED;
	}


	public void clear_click(MButton button, MEvent event) {
		txaStatus.setText("");
	}

	synchronized public void pre(PApplet appc, MWinData data) {
		current = null;
		for(KBase ui : uiElements){
			ui.update();
			ui.overWhat(appc.mouseX, appc.mouseY);
		}
		if(!dragging && current != null && current.conTo != null){
			current.conTo.isOver = true;
		}
	}

	synchronized public void mouse(PApplet appc, MWinData data, MouseEvent mevent) {
		switch(mevent.getAction()){
		case MouseEvent.PRESS:
			if(current != null){
				start = current;
				dragging = true;
			}
			break;
		case MouseEvent.RELEASE:
			if(current != null && start != null && current.type != start.type){
				KConnector descCon = (current.type == DESC) ? current : start;
				KDescriptor descUI = ((KDescriptor)descCon.owner);
				KConnector inputCon = (start.type == INPUT) ? start : current;
				KBaseInput inputUI = ((KBaseInput)inputCon.owner);
				// Make sure the device input is the right type for the descriptor
				int type0 = ((KDescriptor)descCon.owner).iconfig.type;
				int type1 = inputCon.owner.uiType;
				if(type0 == type1){
					// Remove any existing connection
					end = current;
					current = null;
					if(start.conTo != null)
						start.conTo.conTo = null;
					if(end.conTo != null)
						end.conTo.conTo = null;
					// Add new connection
					start.conTo = end;
					end.conTo = start;
					descUI.iconfig.deviceInputName = inputUI.name;		// Not sure if needed look at makeConfigLines
   					inputUI.setMultiplier(descUI.iconfig.multiplier);
					inputUI.setTolerance(descUI.iconfig.tolerance);
				}
			}
			current = start = null;
			dragging = false;
			break;
		case MouseEvent.DRAG:

			break;
		}
	}

	void current(KConnector connector) {
		current = connector;
	}
	
	synchronized public void draw(PApplet appc, MWinData data) {
		appc.background(BACKGROUND);
		// Draw control panel at bottom
		appc.noStroke();
		appc.fill(PANEL);;
		appc.rect(appc.width - PANEL_WIDTH, 0, PANEL_WIDTH, appc.height);
		// Draw connections
		appc.strokeWeight(3.5f);
		for(KConnector c : uiConnections){
			if(c.conTo != null && c.type == KConnector.DESC){
				appc.stroke(c.isOver ? HIGHLIGHT : CONNECTION);
				appc.line(c.px,  c.py,  c.conTo.px,  c.conTo.py);
			}
		}
		// Connection in the making
		if(dragging && start != null){
			appc.stroke(CONNECTION);
			appc.line(start.px, start.py, appc.mouseX, appc.mouseY);
		}
		// Draw descriptors and inputs
		for(KBase ui : uiElements)
			ui.draw();
	}
	
	public MWindow getWindow() {
		return window;
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
