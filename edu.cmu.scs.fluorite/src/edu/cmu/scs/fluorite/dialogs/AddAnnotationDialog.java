package edu.cmu.scs.fluorite.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class AddAnnotationDialog extends Dialog {

	// EDURIDE CHANGES
	
	// log version > 0.2.0
	public static final int OTHER = Window.OK;
	public static final int CANCEL = Window.CANCEL;
	public static final int BACKTRACKING = 2;
	public static final int WRITING_NEW_CODE = 3;
	public static final int FIXING_CODE = 4;
	public static final int WRITING_TEST_CASES = 5;
	public static final int COMPLETELY_LOST = 6;
	public static final int CORRECTING_LOGIC = 7;
	public static final int TRYING_OUT_DIFFERENT_ALGORITHMS = 8;
	public static final int DEBUGGING = 9;
	
	public static final String[] BUTTON_NAMES = {
		"Other",
		"Cancel",
		"Backtracking",
		"Writing new code",
		"Fixing existing code",
		"Writing test cases",
		"I'm completely lost",
		"Correcting Logic",
		"Trying out different algorithms",
		"Debugging",
	};

	private Text textComment;
	private String comment;

	public AddAnnotationDialog(Shell parentShell) {
		super(parentShell);

		comment = "";
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		// createButton(parent, BACKTRACKING, "Backtracking", true);
		createButton(parent, WRITING_NEW_CODE, "Writing new code", false);
		createButton(parent, FIXING_CODE, "Fixing existing code", false);
		createButton(parent, WRITING_TEST_CASES, "Writing test cases", false);
		createButton(parent, DEBUGGING, "Debugging", false);
		createButton(parent, COMPLETELY_LOST, "I'm completely lost", false);
		//createButton(parent, CORRECTING_LOGIC, "Correcting logic", false);
		//createButton(parent, TRYING_OUT_DIFFERENT_ALGORITHMS, "Trying another algorithm", false);

		createButton(parent, OTHER, "Other (comment)", true);
		createButton(parent, CANCEL, "Cancel", false);
		
		// Modify the parent's layout
		GridLayout gridLayout = new GridLayout(3, true);
		parent.setLayout(gridLayout);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		getShell().setText("Add Annotation to the Log File");

		Composite comp = new Composite(parent, SWT.NONE);
		comp.setLayout(new GridLayout());
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Label label = new Label(comp, SWT.NONE);
		StringBuffer msg = new StringBuffer();
		msg.append("\nPlease describe what you are doing or thinking.\n\n");
		label.setText(msg.toString());

		this.textComment = new Text(comp, SWT.MULTI | SWT.BORDER);
		this.textComment.setSize(200, 100);
		this.textComment.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				false));

		return comp;
	}

	@Override
	protected void buttonPressed(int buttonId) {
		setReturnCode(buttonId);
		this.comment = this.textComment.getText();
		close();
	}

	@Override
	protected void cancelPressed() {
		setReturnCode(CANCEL);
		close();
	}

	public String getComment() {
		return this.comment;
	}

}
