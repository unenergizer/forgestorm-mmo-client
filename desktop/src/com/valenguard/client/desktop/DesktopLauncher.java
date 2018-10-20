package com.valenguard.client.desktop;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Files;
import com.badlogic.gdx.backends.lwjgl.LwjglApplication;
import com.badlogic.gdx.backends.lwjgl.LwjglApplicationConfiguration;
import com.valenguard.client.ClientConstants;
import com.valenguard.client.Valenguard;

/********************************************************
 * Valenguard MMO Client and Valenguard MMO Server Info
 *
 * Owned by Robert A Brown & Joseph Rugh
 * Created by Robert A Brown & Joseph Rugh
 *
 * Project Title: valenguard-client
 * Original File Date: 12/20/2017 @ 12:14 AM
 * ______________________________________________________
 *
 * Copyright © 2017 Valenguard.com. All Rights Reserved.
 *
 * No part of this project and/or code and/or source code
 * and/or source may be reproduced, distributed, or
 * transmitted in any form or by any means, including
 * photocopying, recording, or other electronic or
 * mechanical methods, without the prior written
 * permission of the owner.
 *******************************************************/

public class DesktopLauncher {
	public static void main(String[] arg) {
		LwjglApplicationConfiguration config = new LwjglApplicationConfiguration();
		config.vSyncEnabled = true;
		config.title = "Valenguard - A Retro MMO";
		config.width = ClientConstants.SCREEN_WIDTH;
		config.height = ClientConstants.SCREEN_HEIGHT;

		config.addIcon("icon-128.png", Files.FileType.Internal);
		config.addIcon("icon-32.png", Files.FileType.Internal);
		config.addIcon("icon-16.png", Files.FileType.Internal);

		Application application = new LwjglApplication(Valenguard.getInstance(), config);
		application.setLogLevel(Application.LOG_DEBUG);
	}
}
