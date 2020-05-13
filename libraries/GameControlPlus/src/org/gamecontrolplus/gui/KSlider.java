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

import org.gamecontrolplus.ControlSlider;

import processing.core.PApplet;

public class KSlider extends KBaseInput {

	ControlSlider slider;

	protected float tolerance = 0, value = 0, multiplier = 1;

	public KSlider(KConfigDeviceUI ccw, ControlSlider pbutton, float x, float y) {
		super(ccw, x, y, 1, 4);
		uiType = UI_SLIDER;
		inputTypeName = "SLIDER";
		slider = pbutton;
		name = slider.getName();
		namePos = 20 + (int)(ccw.input_UI_length - 20 - app.textWidth(name)) / 2;
		backCol = UI_E_BACK[uiType];
		fontBaseLine = (ccw.desc_UI_height + ccw.fontSize) / 2;
	}

	public float getTolerance(){
		return tolerance;
	}

	public void setTolerance(float t){
		slider.setTolerance(t);
	}

	public float getMultiplier(){
		return multiplier;
	}

	public void setMultiplier(float m){
		slider.setMultiplier(m);
	}


	public void tolerance_set(MTextField source, MEvent event) {
		if(event == MEvent.ENTERED || event == MEvent.LOST_FOCUS){
			float previousValue = tolerance;
			try {
				tolerance = Float.parseFloat(source.getText());
				if(tolerance < 0 || tolerance >= 1.0f)
					tolerance = previousValue;
				slider.setTolerance(tolerance);
			} catch (NumberFormatException e) {
				tolerance = previousValue;
			}
			source.setText("" + tolerance);
		}	  
	}

	public void multiplier_set(MTextField source, MEvent event) {
		if(event == MEvent.ENTERED || event == MEvent.LOST_FOCUS){
			float previousValue = multiplier;
			try {
				multiplier = Float.parseFloat(source.getText());
				slider.setMultiplier(multiplier);
			} catch (NumberFormatException e) {
				multiplier = previousValue;
			}
			source.setText("" + multiplier);
		}
	}

	@Override
	public void update() {
		value = slider.getValue();
		multiplier = slider.getMultiplier();
		tolerance = slider.getTolerance();
	}

	@Override
	public void draw() {
		app.pushMatrix();
		app.pushStyle();
		app.translate(px, py);

		drawBackground();
		app.text("Multiplier  " + multiplier, 10, ccw.input_UI_height + ccw.fontSize);
		app.text("Tolerance   " + tolerance, 10, 2 * ccw.input_UI_height + ccw.fontSize);
		// Slider variables
		float sLeft = 10, sRight = ccw.input_UI_length - 10;
		float sTop = 1 + 3 * ccw.input_UI_height;
		float sWidth = sRight - sLeft, sHeight = ccw.input_UI_height - 8;
		// Draw slider track
		app.noStroke();
		app.fill(255,255,200);
		app.rect(sLeft, sTop, sWidth, sHeight);
		// Draw tolerance
		if(tolerance > 0){
			float tolWidth = tolerance * (sRight - sLeft);
			app.fill(255,200,200);
			app.rect((sRight + sLeft - tolWidth) / 2 , sTop, tolWidth, sHeight);
		}
		// Draw slider value
		String vText = "" + value;
		float vWidth = app.textWidth(vText);
		app.fill(0,0,0,120);
		app.text(vText, (sRight + sLeft - vWidth) / 2, sTop + ccw.fontSize);
		// Draw slider cursor
		float valueX = PApplet.map(value, -multiplier, multiplier, sLeft, sRight);
		app.stroke(SLIDER_CURSOR);
		app.strokeWeight(1);
		app.line(valueX, sTop, valueX, sTop + sHeight);
		app.fill(SLIDER_CURSOR);
		app.noStroke();
		// Top triangle
		app.beginShape();
		app.vertex(valueX, sTop + 3);
		app.vertex(valueX-3, sTop - 3);
		app.vertex(valueX+3, sTop - 3);
		app.endShape();	
		// Bottom triangle
		app.beginShape();
		app.vertex(valueX, sTop+sHeight - 3);
		app.vertex(valueX-3, sTop+sHeight + 3);
		app.vertex(valueX+3, sTop+sHeight + 3);
		app.endShape();	
		
		drawConnectors();
		drawControlHighlight();

		app.popStyle();
		app.popMatrix();
	}


}
