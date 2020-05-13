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

import org.gamecontrolplus.Configuration;
import org.gamecontrolplus.ControlDevice;

import processing.core.PApplet;

public class KManageConfiguration implements KConstants {
	
	
	public static int selectDevice(final PApplet parent, final Configuration config) {
		KSelectDeviceUI kSelectDevice = new KSelectDeviceUI(parent, config);
		while(kSelectDevice.status() == WORKING) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		kSelectDevice.finishedWith();
		return kSelectDevice.status();
	}
	
	public static int configDevice(final PApplet parent, final Configuration config, final ControlDevice device) {
		KConfigDeviceUI kConfigDevice = new KConfigDeviceUI(parent, config, device);
		while(kConfigDevice.status() == WORKING) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}			
		}
		kConfigDevice.finishedWith();
		return kConfigDevice.status();
	}
}
