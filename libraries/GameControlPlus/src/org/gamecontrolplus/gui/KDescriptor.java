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

import org.gamecontrolplus.Configuration.InputConfig;

public class KDescriptor extends KBase {
	
	InputConfig iconfig;
	KBaseInput source = null;
	int conID = 0;

	public KDescriptor(KConfigDeviceUI ccw, float x, float y, InputConfig iconfig) {
		super(ccw, x ,y, 1);
		this.iconfig = iconfig;
		name = iconfig.key;
		for(int cn = 0; cn < connectors.length; cn++)
			connectors[cn]= new KConnector(app, this, cn, 
					px + ccw.desc_UI_length + ccw.connector_size_r, // x
					py + (cn + 0/5f) * ccw.desc_UI_height + ccw.connector_size_r, //y
					ccw.connector_size_d); // size

		uiType = UI_DESCRIPTOR;
		UI_HEIGHT = ccw.desc_UI_height;
		backCol = UI_E_BACK[iconfig.type];
		fontBaseLine = (ccw.desc_UI_height + ccw.fontSize) / 2;
	}


	public void draw(){
		app.pushMatrix();
		//	app.pushStyle();
		app.translate(px, py);
		// Descriptor area
		app.noStroke();
		app.fill(backCol );
		app.rect(0, 0, ccw.desc_UI_length, UI_HEIGHT);

		// Text
		app.fill(0);
		app.text(iconfig.description, 10, fontBaseLine);

		// Highlight
		app.stroke(isOver ? HIGHLIGHT : BORDER);
		app.strokeWeight(isOver ? 4 : 1);
		app.noFill();
		app.rect(0, 0, ccw.desc_UI_length,  UI_HEIGHT);

		// Connectors 
		//	app.pushMatrix();
		app.translate(ccw.desc_UI_length,0);
		drawConnectors();
		//	app.popMatrix();

		app.popMatrix();
	}

	protected void drawConnectors(){
		for(int cn = 0; cn < connectors.length; cn++)
			connectors[cn].draw(cn * ccw.desc_UI_height);
	}

	//public String getName(){
	//	return txfKey.getText();
	//}

}
