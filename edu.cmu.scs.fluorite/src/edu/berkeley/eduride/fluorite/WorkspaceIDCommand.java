package edu.berkeley.eduride.fluorite;

import java.util.HashMap;
import java.util.Map;

import org.w3c.dom.Attr;
import org.w3c.dom.Element;

import edu.berkeley.eduride.base_plugin.EduRideBase;
import edu.cmu.scs.fluorite.commands.AnnotateCommand;

public class WorkspaceIDCommand extends AnnotateCommand {
	
	private String mWsid = EduRideBase.getWorkspaceID();
	
	
	
	@Override
	public Map<String, String> getAttributesMap() {
		Map<String, String> attrMap = new HashMap<String, String>();
		attrMap.put("workspaceID", mWsid);
		return attrMap;
	}
	
	
	@Override
	public Map<String, String> getDataMap() {
		// nothing here, just overriding AnnotationCommand default
		Map<String, String> dataMap = new HashMap<String, String>();
		return dataMap;
	}
	
	@Override
	public void createFrom(Element commandElement) {
		super.createFrom(commandElement);
		
		Attr attr = null;
		if ((attr = commandElement.getAttributeNode("workspaceID")) != null) {
			mWsid = attr.getValue();
		}
	}
	
	@Override
	public String getName() {
		return "WorkspaceID (" + mWsid + ")";
	}
	
	@Override
	public String getDescription() {
		return mWsid;
	}
	
}
