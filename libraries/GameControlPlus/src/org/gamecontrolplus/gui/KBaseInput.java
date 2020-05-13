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

import org.gamecontrolplus.ControlButton;
import org.gamecontrolplus.ControlHat;
import org.gamecontrolplus.ControlInput;
import org.gamecontrolplus.ControlSlider;

public abstract class KBaseInput extends KBase{
	
	public static KBaseInput makeInputUI(KConfigDeviceUI ccw, ControlInput input, float x, float y) {
		KBaseInput obj = null;
		if(input instanceof ControlHat)
			obj = new KCoolieHat(ccw, (ControlHat) input, x, y);
		else  if(input instanceof ControlButton)
			obj = new KButton(ccw, (ControlButton) input, x, y);
		else  if(input instanceof ControlSlider)
			obj = new KSlider(ccw, (ControlSlider) input, x, y);
		return obj;
	}

	int namePos;
	// BUTTON, HAT, SLIDER
	String inputTypeName = "";

	
	public float getValue(){
		return 0;
	}

	public float getMultiplier(){
		return 0;
	}
	
	public void setTolerance(float t){
	}

	public void setMultiplier(float m){
	}

	public float getTolerance(){
		return 0;
	}

	public KBaseInput(KConfigDeviceUI ccw, float x, float y, int nbr_connects, int nbrLines){
		super(ccw, x, y, nbr_connects);
		for(int cn = 0; cn < connectors.length; cn++)
			connectors[cn]= new KConnector(app, this, cn, 
					px - ccw.connector_size_r, // x
					py + (cn + 0/5f) * ccw.input_UI_height + ccw.connector_size_r, //y
					ccw.connector_size_d); // size
		UI_HEIGHT = nbrLines * ccw.input_UI_height;
		fontBaseLine = (ccw.input_UI_height + ccw.fontSize) / 2;
	}

	protected void drawBackground(){
		// Background
		app.noStroke();
		app.fill(backCol);
		app.rect(0, 0, ccw.input_UI_length,  UI_HEIGHT);
		app.fill(TEXTFILL);
		app.text(name, namePos, fontBaseLine);
	}

	protected void drawConnectors(){
		for(int cn = 0; cn < connectors.length; cn++)
			connectors[cn].draw(cn * ccw.input_UI_height);
	}

	protected void drawControlHighlight(){
		// Highlighter when mouse over
		app.stroke(isOver ? HIGHLIGHT : BORDER);
		app.strokeWeight(isOver ? 4 : 1);
		app.noFill();
		app.rect(0, 0, ccw.input_UI_length,  UI_HEIGHT);
	}

}
