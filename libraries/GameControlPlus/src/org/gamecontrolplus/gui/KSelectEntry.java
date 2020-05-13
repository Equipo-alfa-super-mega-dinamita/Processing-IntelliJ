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

import org.gamecontrolplus.ControlDevice;

import processing.core.PApplet;

/**
 * A single device entry option used in the device select window
 * 
 * @author Peter Lager
 *
 */
public class KSelectEntry implements Comparable<KSelectEntry> {
	// Parent
	private KSelectDeviceUI selectDeviceUI;
	
	private final ControlDevice device;
	
	// GUI stuff
	private MLabel displayName;
	private MButton btnGoConfig;

	public KSelectEntry(KSelectDeviceUI selectDeviceUI, ControlDevice dev, int index){
		this.selectDeviceUI = selectDeviceUI;
		this.device = dev;
		createGUI(selectDeviceUI.getWindow(), index);
	}

	private void createGUI(PApplet window, int index) {
		displayName = new MLabel(window, 36, 20 + index * 20, window.width-36, 20);
		displayName.setText(device.getName() + "  [" + device.getTypeName() + "]");
		displayName.setTextAlign(MAlign.LEFT, null);
		btnGoConfig = new MButton(window, 4, 24 + index * 20, 24, 14);
		btnGoConfig.addEventHandler(this, "configClick");					
	}
	
	// Remember the device selected
	public void configClick(MButton source, MEvent event) {
		selectDeviceUI.setSelectedDevice(device);
	}

	@Override
	public int compareTo(KSelectEntry entry) {
		return device.compareTo(entry.device);
	}

}
