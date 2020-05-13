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

public class KButton extends KBaseInput {

	ControlButton button;
	
	boolean isPressed;
	
	public KButton(KConfigDeviceUI ccw, ControlButton pbutton, float x, float y){
		super(ccw, x, y, 1, 1);
		uiType = UI_BUTTON;
		inputTypeName = "BUTTON";
		button = pbutton;
		name = button.getName();
		namePos = 20 + (int)(ccw.input_UI_length - 20 - app.textWidth(name)) / 2;
		backCol = UI_E_BACK[uiType];
		fontBaseLine = (ccw.desc_UI_height + ccw.fontSize) / 2;
	}
	
	public void update(){
		isPressed = button.pressed();
	}
	
	public void draw(){
		app.pushMatrix();
//		app.pushStyle();
		app.translate(px, py);
		
		drawBackground();
	
		// Pressed ?
		app.stroke(BORDER);
		app.strokeWeight(1);
		app.fill(isPressed ? PRESSED : RELEASED);
		app.ellipse(ccw.input_UI_height/2, ccw.input_UI_height/2, ccw.indicator_d, ccw.indicator_d);
		
		drawConnectors();
		drawControlHighlight();
		
//		app.popStyle();
		app.popMatrix();
	}
}

