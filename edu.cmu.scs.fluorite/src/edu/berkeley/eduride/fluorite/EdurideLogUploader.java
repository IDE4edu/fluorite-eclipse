package edu.berkeley.eduride.fluorite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.core.runtime.Path;
import org.json.JSONObject;

import edu.berkeley.eduride.base_plugin.EduRideBase;
import edu.berkeley.eduride.base_plugin.util.Console;
import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.util.Utilities;

public class EdurideLogUploader {


	
	// DUPLICATED FROM LOGIC IN EventRecorder.java
	private static String getCurrentLogFileName() {
		return Utilities.getUniqueLogNameByTimestamp(EventRecorder.getInstance().getStartTimestamp(), false);
	}
	
	// USES FLUORITE FILE NAMING MOJO
	private static boolean hasXMLFileExtension(File f) {
		return f.getName().endsWith(".xml");
	}
	

	// FOR PROCESSING SERVER RESPONSE STATUS MESSAGES
	// must correspond to Log module in eduride-web
	private static final String FAIL_STATUS = "failure";
	private static final String IGNORED_STATUS = "ignored";
	private static final String SUCCESS_STATUS = "success";

	private static final String STATUS_KEY = "status";
	
	
	
	/////////////////
	
	// success means we didn't receive an exc
	public static boolean uploadLogFiles() {
		boolean success = false;

		File logdir = null;
		try {
			logdir = Utilities.getLogLocation();
		} catch (Exception e) {
			Console.err("Failed getting log location to upload logging files", e);
			return success;
		}
		File[] logFiles = logdir.listFiles();
		File currentLogFile = getCurrentLogFile();
		for (File logFile : logFiles) {
			if (!(logFile.equals(currentLogFile)) && hasXMLFileExtension(logFile) ) {
				boolean currentSuccess = deleteOrUpload(logFile);
				success = success && currentSuccess;
			}
		}
		return success;
	}


	
	
	private static File getCurrentLogFile() {
		File currentLogFile = null;
		try {
			File dir = Utilities.getLogLocation();
			String name = getCurrentLogFileName();
			currentLogFile = new File(dir, name);
		} catch (Exception e) {
			Console.err(e);
		}
		return currentLogFile;
	}
	
	
	
	
	
	// tries to upload file; 
	//  if successful, returns (true) without deleting file
	//  if unsuccessful because log file already exists, deletes file (returns true)
	//  if unsuccessful for another reason, sadly does nothing (returns false)
	// Why?  sort of a double check kinda thing
	private static boolean deleteOrUpload(File f) {
		final boolean SUCCESS = true;
		final boolean FAILURE = false;
		
		String uploadSuccess = uploadLogFile(f);
		if (uploadSuccess.equals(FAIL_STATUS)) {
			return FAILURE;
		} else if (uploadSuccess.equals(IGNORED_STATUS)) {
			// it was already uploaded, delete the file
			try {
				Files.delete(f.toPath());
			} catch (IOException e) {
				Console.err("Couldn't delete " + f.getName(), e);
			}
			return SUCCESS;
		} else if (uploadSuccess.equals(SUCCESS_STATUS)) {
			return SUCCESS;
		} else {
			return FAILURE;
		}
	}
	
	
	// obviously EventRecorder.stop() should be called first, yo
	public static String uploadCurrentLogFile() {
		return uploadLogFile(getCurrentLogFile());
	}
	

	
	


	
	private static final String PUSH_TARGET = "/fluorite_xml/";
	
	// TODO
	// don't throw exception here, but print error perhaps?
	public static String uploadLogFile(File f) {
		String success = FAIL_STATUS;
		String jsonMsg = "";
		int responseCode = -1;
		try {

			//String content = readFile(f);

			String wsID = EduRideBase.getWorkspaceID();
			String real_push_target = PUSH_TARGET + wsID + "/";
			
			HttpURLConnection connection = null;
			String domain = EduRideBase.getDomain();
			int port = EduRideBase.getDomainPort();
			URL target = null;

			target = new URL("http", domain, port, real_push_target);
			connection = (HttpURLConnection) target.openConnection();
			connection.setDoOutput(true);
			connection.setDoInput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/xml");
			connection.setRequestProperty("charset", "utf-8");
			connection.setRequestProperty("Content-Length", 
					"" + Long.toString(f.length()));

			OutputStream outStream = connection.getOutputStream();

			// EH, probably work, but might fail for bad XML
			//TransformerFactory tf = TransformerFactory.newInstance();
			//Transformer transformer = tf.newTransformer();
			//FileReader fileReader = new FileReader(f);
			//StreamSource source = new StreamSource(fileReader);
			//StreamResult result = new StreamResult(outStream);
			//transformer.transform(source, result);

			FileInputStream fileInStream = new FileInputStream(f);
			// 10k buffer
			byte[] buffer = new byte[10240];
			int n;
			while ((n = fileInStream.read(buffer)) > 0) {
				outStream.write(buffer, 0, n);
			}
			outStream.flush();
			fileInStream.close();
			
			
			
			InputStream ins = connection.getInputStream();
			InputStreamReader ins_reader = new InputStreamReader(ins);
			BufferedReader in = new BufferedReader(ins_reader);
	        String line;
	        while ((line = in.readLine()) != null) {
	            jsonMsg += line;
	        }
	        in.close();
			
			responseCode = connection.getResponseCode();
			connection.disconnect();
			
		} catch (MalformedURLException e) {
			Console.err("MalformedURL exception while uploading log file " + f.getName());
			Console.err(e);
			return success;
		} catch (FileNotFoundException e) {
			Console.err("File Not Found while attempting to upload file " + f.getName());
			Console.err(e);
			return success;
		} catch (IOException e) {
			// couldn't read the file for some reason...
			Console.err("IOException while uploading log file " + f.getName());
			Console.err(e);
			return success;
//		} catch (TransformerException e) {
//			// transformer.transform broke
//			Console.err("Couldn't upload log file" + f.getName() + ". Transformer.");
//			Console.err(e);
//			return success;
		} finally {
			
		}

		Console.msg("Attempt file " + f.getName() + ".  Response code: " + responseCode + ".  Server msg: " + jsonMsg);
		success = getStatus(jsonMsg);
		return success;
	}


	
	

	

	
	/// messages are like "{'status': 'failure'}"
	
	private static String getStatus(String msg) {
		try {
			String result = null;
			if (msg != null) {
				JSONObject jsonMsg = new JSONObject(msg);
				result = (String) jsonMsg.get(STATUS_KEY);
			}
			return result;
		} catch (Exception e) {
			Console.err("bad json from server.  Message was: " + msg,
					e);
			return FAIL_STATUS;
		}
	}
	
	
	// ignores charset issues
	private static String readFile(File f) throws IOException {
		String content = new String(Files.readAllBytes(f.toPath()));
		return content;
	}
	
}
