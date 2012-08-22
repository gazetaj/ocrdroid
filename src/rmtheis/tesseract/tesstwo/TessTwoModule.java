/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package rmtheis.tesseract.tesstwo;

import java.io.IOException;
import java.util.HashMap;

import org.appcelerator.kroll.KrollModule;
import org.appcelerator.kroll.annotations.Kroll;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.common.TiConfig;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;

import com.googlecode.tesseract.android.TessBaseAPI;


@Kroll.module(name="Tesseract", id="rmtheis.tesseract.tesstwo")
public class TessTwoModule extends KrollModule
{

	// Standard Debugging variables
	private static final String LCAT = "TesseractModule";
	private static final boolean DBG = TiConfig.LOGD;
	
	private static final String DATA_PATH = "file:///android_asset/";
	
	private String exampleProperty = "";

	// You can define constants with @Kroll.constant, for example:
	// @Kroll.constant public static final String EXTERNAL_NAME = value;
	
	public TessTwoModule()
	{
		super();
	}

	@Kroll.onAppCreate
	public static void onAppCreate(TiApplication app)
	{
		Log.d(LCAT, "inside onAppCreate");
		// put module init code that needs to run when the application is created
	}

	// Methods
	@Kroll.method
	public String example()
	{
		Log.d(LCAT, "example called");
		return "exampleProperty: PLACEHOLDER".replaceAll("PLACEHOLDER", exampleProperty);
	}
	
	// Properties
	@Kroll.getProperty
	public String getExampleProp()
	{
		Log.d(LCAT, "get example property");
		return exampleProperty;
	}
	
	
	@Kroll.setProperty
	public void setExampleProp(String value) {
		Log.d(LCAT, "set example property: " + value);
		exampleProperty = value;
	}

	@Kroll.method
	public String ocr(HashMap _config)
	{
		assert(_config != null);
		String imagePath = (String) _config.get("path");
		String language = (String) _config.get("lang");
		
		Log.d(LCAT, "ocr called");
		
		BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 2;
        Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);
 
        try {
            ExifInterface exif = new ExifInterface(imagePath);
            int exifOrientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
 
            Log.v(LCAT, "Orient: " + exifOrientation);
 
            int rotate = 0;
            switch (exifOrientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
            }
 
            Log.v(LCAT, "Rotation: " + rotate);
 
            if (rotate != 0) {
 
                // Getting width & height of the given image.
                int w = bitmap.getWidth();
                int h = bitmap.getHeight();
 
                // Setting pre rotate
                Matrix mtx = new Matrix();
                mtx.preRotate(rotate);
 
                // Rotating Bitmap
                bitmap = Bitmap.createBitmap(bitmap, 0, 0, w, h, mtx, false);
                // tesseract req. ARGB_8888
                bitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true);
            }
 
        } catch (IOException e) {
            Log.e(LCAT, "Rotate or coversion failed: " + e.toString());
        }
 
//        ImageView iv = (ImageView) findViewById(R.id.image);
//        iv.setImageBitmap(bitmap);
//        iv.setVisibility(View.VISIBLE);
 
        Log.v(LCAT, "Before baseApi");
 
        TessBaseAPI baseApi = new TessBaseAPI();
        baseApi.setDebug(true);
        baseApi.init(DATA_PATH, language);
        baseApi.setImage(bitmap);
        //baseApi.get
        String recognizedText = baseApi.getUTF8Text();
        baseApi.end();
 
        Log.v(LCAT, "OCR Result: " + recognizedText);
 
        // clean up and show
        if (language.equalsIgnoreCase("eng")) {
            recognizedText = recognizedText.replaceAll("[^a-zA-Z0-9]+", " ");
        }
		
		return recognizedText.trim();
	}


}

