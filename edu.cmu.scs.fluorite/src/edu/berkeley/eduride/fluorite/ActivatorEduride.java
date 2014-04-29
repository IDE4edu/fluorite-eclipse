package edu.berkeley.eduride.fluorite;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.ui.progress.UIJob;
import org.osgi.framework.BundleContext;

import edu.cmu.scs.fluorite.model.EventRecorder;
import edu.cmu.scs.fluorite.preferences.Initializer;

/**
 * The activator class controls the plug-in life cycle
 */
public class ActivatorEduride extends edu.cmu.scs.fluorite.plugin.Activator {


	
	/**
	 * The constructor
	 */
	public ActivatorEduride() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	@Override
	public void start(BundleContext context) throws Exception {

		// set preferences, etc here?


		
		super.start(context);
		
		System.out.println("STARTING FLUORITE");
		// add workspaceID to the log, when it starts
		EventRecorder.getInstance().scheduleTask(new Runnable() {
			@Override
			public void run() {
				WorkspaceIDCommand wsidCommand = new WorkspaceIDCommand();
				EventRecorder.getInstance().recordCommand(wsidCommand);
			}
		});

		// grab the old logs and push them up, in a separate thread?  
		Job upload = new Job("Uploading logs") {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				
				EdurideLogUploader.uploadLogFiles();	
				
				return Status.OK_STATUS;
			}

		};
		upload.setSystem(true);
		upload.setUser(false);
		upload.schedule();
		
	}
	
	
	
	
	
	@Override
	public void stop(BundleContext context) throws Exception {
		// uh oh?  This gets called in super...  ok to call twice?
		EventRecorder.getInstance().stop();
		EdurideLogUploader.uploadCurrentLogFile();
		
		for (File f : getFilesToUpload()) {
			EdurideLogUploader.uploadLogFile(f);
		}
		
		super.stop(context);
		
	}

	
	
	private ArrayList<File> getFilesToUpload() {
		ArrayList<File> files = new ArrayList<File>();
		
		// TODO remove this abomination when .isa files can handle this
		// CSE21 LAB 13 SURVEY, yoyo
		IProject lab13 = ResourcesPlugin.getWorkspace().getRoot().getProject("Lab 21_13");
		IResource survey = lab13.findMember("doc/survey.txt");
		if (survey.exists() && (survey.getType() == IResource.FILE)) {
			files.add(survey.getLocation().toFile());
		}
		
		
		return files;
	}


	

	
	// TODO -- upload logs?

}
