package com.profete162.mvforandroid.util;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;

import com.profete162.mvforandroid.R;


    public class Codes 
    { 
    	
    
		public static void WhatsNew(Activity mContext, Activity monthis) {
			//.setMessage(monthis.getString(R.string.whatsnew))
/*
			LayoutInflater inflater = (LayoutInflater)mContext.getSystemService(mContext.LAYOUT_INFLATER_SERVICE);
			
			   new AlertDialog.Builder(mContext)
			      .setView(inflater.inflate(R.layout.image_dialog, null))
			      .setMessage(monthis.getString(R.string.whatsnew))
			      .setTitle("BETrains PRO")
			      .setCancelable(true)
			      .setNeutralButton(android.R.string.ok,
			         new DialogInterface.OnClickListener() {
			         public void onClick(DialogInterface dialog, int whichButton){}
			         })
			      .show();
			      */
			
			   new AlertDialog.Builder(mContext)
			   		.setMessage(monthis.getString(R.string.whatsnew))
			      .setTitle(monthis.getString(R.string.whatsnewtitle))
			      .setCancelable(true)
			      .setNeutralButton(android.R.string.ok,
			         new DialogInterface.OnClickListener() {
			         public void onClick(DialogInterface dialog, int whichButton){}
			         })
			      .show();

		        
		    }

	
 }
