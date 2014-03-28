package edu.berkeley.eduride.fluorite;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
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

		// TODO grab the old logs and push them up, in a separate thread?  
		
		super.start(context);
		
		
		// add workspaceID to the log, when it starts
		EventRecorder.getInstance().scheduleTask(new Runnable() {
			@Override
			public void run() {
				WorkspaceIDCommand wsidCommand = new WorkspaceIDCommand();
				EventRecorder.getInstance().recordCommand(wsidCommand);
			}
		});


		


	}



	
	// TODO -- upload logs?

}
