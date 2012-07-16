//// APOD ////
//
// This project is an Android client to visualize the Astronomy Picture Of The
// Day (APOD) published by NASA at http://apod.nasa.gov/apod/. This project is
// free of charges and have been developed with the objective to learn Android
// programmation.
//
// Copyright (C) 2012  Philippe Chretien
//
// This program is free software; you can redistribute it and/or
// modify it under the terms of the GNU General Public License Version 2
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program; if not, write to the Free Software
// Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301, USA.
//
// You will find the latest version of this code at the following address:
// http://github.com/pchretien
//
// You can contact me at the following email address:
// philippe.chretien at gmail.com

package com.basbrun;

import android.app.Application;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

// The APODApplication class contains a reference to a unique data provider 
// All the communications to the web server are made trough this data provider
// singleton.
//
// One must change the AndroidManifest.xml file to set this class as the main
// application file. android:name="com.basbrun.APODApplication"
public class APODApplication extends Application
{
	private static APODDataProvider dataProvider = null;

	@Override
	public void onCreate()
	{
		super.onCreate();
		
		SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
		dataProvider = new APODDataProvider(preferences);
	}

	public APODDataProvider getDataProvider() {
		return dataProvider;
	}
}
