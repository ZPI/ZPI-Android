package com.pwr.zpi.files;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;

import org.xmlpull.v1.XmlSerializer;

import android.location.Location;
import android.os.Environment;
import android.util.Log;
import android.util.Xml;

import com.pwr.zpi.database.entity.SingleRun;
import com.pwr.zpi.utils.Pair;

public class GPXParser {
	
	private static final String GPX_TAG = "gpx";
	private static final String XMLNS_ATTR = "xmlns";
	private static final String XMLNS_ATTR_VALUE = "http://www.topografix.com/GPX/1/1";
	private static final String XMLNS_XSI_ATTR = "xmlns:xsi";
	private static final String XMLNS_XSI_ATTR_VALUE = "http://www.w3.org/2001/XMLSchema-instance";
	private static final String XSI_ATTR = "xsi:schemaLocation";
	private static final String XSI_ATTR_VALUE = "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd";
	
	private static final String VERSION_TAG = "version";
	private static final String CREATOR_TAG = "creator";
	private static final String WPT_TAG = "wpt";
	private static final String LAT_TAG = "lat";
	private static final String LON_TAG = "lon";
	private static final String ELE_TAG = "ele";
	private static final String TIME_TAG = "time";
	private static final String CDATA_START = "<![CDATA[";
	private static final String CDATA_END = "]]>";
	private static final String TRK_TAG = "trk";
	private static final String TRKSEG_TAG = "trkseg";
	private static final String TRKPT_TAG = "trkpt";
	private static final String CREATOR_NAME = "Marek Kreœnicki and Jakub Skudlarski";
	private static final String VERSION_NAME = "1.1";
	
	/**
	 * Date format for a point timestamp.
	 */
	private static final SimpleDateFormat POINT_DATE_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'");
	
	public static String saveGPX(SingleRun run) {
		
		//create a new file called "new.xml" in the SD card
		File newxmlfile = new File(Environment.getExternalStorageDirectory() + "/" + run.getName() + ".gpx");
		try {
			newxmlfile.createNewFile();
		}
		catch (IOException e) {
			Log.e("IOException", "exception in createNewFile() method");
			e.printStackTrace();
		}
		//we have to bind the new file with a FileOutputStream
		FileOutputStream fileos = null;
		try {
			fileos = new FileOutputStream(newxmlfile);
		}
		catch (FileNotFoundException e) {
			Log.e("FileNotFoundException", "can't create FileOutputStream");
		}
		//we create a XmlSerializer in order to write xml data
		XmlSerializer serializer = Xml.newSerializer();
		try {
			//we set the FileOutputStream as output for the serializer, using UTF-8 encoding
			serializer.setOutput(fileos, "UTF-8");
			//Write <?xml declaration with encoding (if encoding not null) and standalone flag (if standalone not null) 
			serializer.startDocument(null, Boolean.valueOf(true));
			//set indentation option
			serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
			//start a tag called "root"
			serializer.startTag(null, GPX_TAG);
			serializer.attribute(null, XMLNS_ATTR, XMLNS_ATTR_VALUE);
			serializer.attribute(null, XMLNS_XSI_ATTR, XMLNS_XSI_ATTR_VALUE);
			serializer.attribute(null, XSI_ATTR, XSI_ATTR_VALUE);
			serializer.attribute(null, VERSION_TAG, VERSION_NAME);
			serializer.attribute(null, CREATOR_TAG, CREATOR_NAME);
			
			serializer.startTag(null, TRK_TAG);
			for (LinkedList<Pair<Location, Long>> singleSegment : run.getTraceWithTime())
			{
				serializer.startTag(null, TRKSEG_TAG);
				for (Pair<Location, Long> singlePoint : singleSegment)
				{
					serializer.startTag(null, TRKPT_TAG);
					serializer.attribute(null, LAT_TAG, singlePoint.first.getLatitude() + "");
					serializer.attribute(null, LON_TAG, singlePoint.first.getLongitude() + "");
					serializer.startTag(null, ELE_TAG);
					serializer.text(singlePoint.first.getAltitude() + "");
					serializer.endTag(null, ELE_TAG);
					serializer.startTag(null, TIME_TAG);
					serializer.text(POINT_DATE_FORMATTER.format(new Date(run.getStartDate().getTime()
						+ singlePoint.second)));
					serializer.endTag(null, TIME_TAG);
					serializer.endTag(null, TRKPT_TAG);
				}
				serializer.endTag(null, TRKSEG_TAG);
			}
			serializer.endTag(null, TRK_TAG);
			
			serializer.endTag(null, GPX_TAG);
			serializer.endDocument();
			//write xml data into the FileOutputStream
			serializer.flush();
			//finally we close the file stream
			fileos.close();
			return Environment.getExternalStorageDirectory() + "/" + run.getName()
				+ ".gpx";
		}
		catch (Exception e) {
			Log.e("Exception", "error occurred while creating xml file");
			e.printStackTrace();
		}
		return null;
	}
}
