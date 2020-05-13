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

import processing.core.PApplet;
import processing.core.PConstants;

public abstract class KBase implements  PConstants, KConstants {

	protected KConfigDeviceUI ccw;
	protected final PApplet app;

	String name;

	// UI_BUTTON, UI_COOLIEHAT, UI_SLIDER or UI_DESCRIPTOR	
	int uiType;
	
	final float px, py;
	
	float UI_HEIGHT;
	float fontBaseLine;
	int backCol;

	final KConnector[] connectors;
	
	boolean isOver = false;
	
	KBase(KConfigDeviceUI ccw, float x, float y, int nbr_connects){
		this.ccw = ccw;
		app = ccw.getWindow();
		UI_HEIGHT = ccw.desc_UI_height;
		px = x;
		py = y;
		connectors = new KConnector[nbr_connects];
	}
	
	public int getNbrOfConnectors(){
		return connectors.length;
	}
	
	protected abstract void drawConnectors();

	public void update(){ }
	
	public void overWhat(float mx, float my){
		for(int cn = 0; cn < connectors.length; cn++){
			isOver = connectors[cn].isOver(ccw, mx , my);
			if(isOver) {
				break;
			}
		}
	}
	
	public abstract void draw();

}