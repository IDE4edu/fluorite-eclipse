package edu.berkeley.eduride.fluorite;

import java.io.File;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import org.eclipse.core.runtime.Path;

import edu.berkeley.eduride.base_plugin.util.Console;
import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;

public class EdurideLogUploader {


	
	// DUPLICATED FROM LOGIC IN EventRecorder.java
	private static String getCurrentLogFileName() {
		return Utilities.getUniqueLogNameByTimestamp(EventRecorder.getInstance().getStartTimestamp(), false);
	}
	

	
	/////////////////
	
	// success means we didn't receive an exc
	public static boolean uploadLogFiles() {
		boolean success = false;

		File logdir = null;
		try {
			logdir = Utilities.getLogLocation();
		} catch (Exception e) {
			Console.err("Failed getting log location to upload logging files");
			return success;
		}
		File[] logFiles = logdir.listFiles();
		File currentLogFile = getCurrentLogFile();
		for (File logFile : logFiles) {
			if (!(logFile.equals(currentLogFile))) {
				boolean currentSuccess = deleteOrUpload(logFile);
				success = success && currentSuccess;
			}
		}
		return success;
	}


	// tries to upload file; 
	//  if successful, returns (true) without deleting file
	//  if unsuccessful because log file already exists, deletes file (returns true)
	//  if unsuccessful for another reason, sadly does nothing (returns false)
	// Why?  sort of a double check kinda thing
	private static boolean deleteOrUpload(File f) {
		boolean overallSuccess = true;
		boolean uploadSuccess = false;
		uploadSuccess = uploadLogFile(f);
		
		return overallSuccess;
	}
	
	
	// obviously EventRecorder.stop() should be called first, yo
	public static boolean uploadCurrentLogFile() {
		return uploadLogFile(getCurrentLogFile());
	}
	
	
	
	private static File getCurrentLogFile() {
		File currentLogFile = null;
		try {
			File dir = Utilities.getLogLocation();
			String name = getCurrentLogFileName();
			currentLogFile = new File(dir, name);
		} catch (Exception e) {
		}
		return currentLogFile;
	}
	
	
	
	
	
	// don't throw exception here, but print error perhaps?
	public static boolean uploadLogFile(File f) {
		boolean success = false;
		
		
		return success;
	}
	

	
}
