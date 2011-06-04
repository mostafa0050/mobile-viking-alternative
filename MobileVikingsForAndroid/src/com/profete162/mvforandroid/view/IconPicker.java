/*
 * Copyright (C) 2008 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.profete162.mvforandroid.view;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

/**
 * Presents a list of activities to choose from. This list only contains activities
 * that have ACTION_MAIN, since other types may require data as input.
 */
public class IconPicker extends Activity{
    private static final int IMAGE_PICK = 0;
	@Override
    protected void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK);
	}
	
	 @Override
	    protected void onActivityResult(int requestCode, int resultCode, Intent result) {
	    	
	    	super.onActivityResult(requestCode, resultCode, result); 


	        switch (requestCode) {
	        case IMAGE_PICK:

	            finish();
	            break;
	            

	        
	        }
	    }
	    
}

	


