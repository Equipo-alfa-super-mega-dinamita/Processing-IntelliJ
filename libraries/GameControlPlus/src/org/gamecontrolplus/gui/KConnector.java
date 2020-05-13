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

public class KConnector implements PConstants, KConstants {


	private final PApplet app;
	final KBase owner;
	KConnector conTo = null;
	
	public final int conNo;
	boolean isOver = false;
	
	float size, hsize;
	final int type;
	// Absolute position on screen
	final float px, py;

	public KConnector(PApplet papp, KBase owner, int conNo, float x, float y, float size){
		app = papp;
		this.owner = owner;
		this.conNo = conNo;
		type = (owner instanceof KDescriptor) ? DESC : INPUT;
		px = x;
		py = y;
		this.size = size;
		hsize = size/2;
	}

	public void draw(float deltaY){
		app.pushMatrix();
		app.translate(0, deltaY);
		if(type == INPUT)
			app.scale(-1, 1);
		app.fill(isOver ? HIGHLIGHT : CONNECTOR);
		app.noStroke();
		app.rect(0, 0, hsize, size);
		app.stroke(isOver ? HIGHLIGHT : BORDER);
		app.strokeWeight(isOver ? 3 : 1);
		app.arc(hsize, hsize, size, size, PI+HALF_PI, TWO_PI+HALF_PI, OPEN);
		app.line(0, 0, hsize, 0);
		app.line(0, 0, 0, size);
		app.line(0, size, hsize, size);
		app.popMatrix();	
	}
		
	public boolean isOver(KConfigDeviceUI ccw, float mx, float my){
		isOver = (Math.abs(mx-px) <= hsize && Math.abs(my-py) <= hsize);
		if(isOver) 
			ccw.current(this);
		return isOver;
	}

}
