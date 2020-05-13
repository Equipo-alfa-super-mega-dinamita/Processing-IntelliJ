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

package org.gamecontrolplus;

/**
 * External constants for passing information into GCP
 * 
 * @author Peter Lager
 */
public class GCP {

	static boolean announced = false;

	/**
	 * @return the pretty version of the library. This will be shown in Processing
	 */
	public static String getPrettyVersion() {
		return "1.2.2";
	}

	/**
	 * @return the version of the library. 
	 */
	public static String getVersion() {
		return "8";
	}

	/**
	 * Display the library version in the ProcessingIDE
	 */
	static void announceGCP(){
		if(!announced){
			System.out.println("=======================================================");
			System.out.println("  GameControlPlus V1.2.2 created by");
			System.out.println("  Christian Riekoff and Peter Lager");
			System.out.println("=======================================================");
			announced = true;
		}
	}

	public static final int UNKNOWN		= 0x00000001;	//     1
	public static final int MOUSE 		= 0x00000002;	//     2
	public static final int KEYBOARD 	= 0x00000004;	//     4
	public static final int FINGERSTICK = 0x00000008;	//     8
	public static final int GAMEPAD 	= 0x00000010;	//    16
	public static final int HEADTRACKER = 0x00000020;	//    32
	public static final int RUDDER 		= 0x00000040;	//    64
	public static final int STICK 		= 0x00000080;	//   128
	public static final int TRACKBALL 	= 0x00000100;	//   256
	public static final int TRACKPAD 	= 0x00000200;	//   512
	public static final int WHEEL 		= 0x00000400;	//  1024
	public static final int ANY 		= 0xFFFFFFFF;	//  Used internally

}
