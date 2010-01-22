package de.hpi.bpmn2xpdl;

import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.RootElement;

@RootElement("IntermediateEvent")
public class XPDLIntermediateEvent extends XMLConvertable {
	
	@Attribute("Trigger")
	protected String trigger;
	@Attribute("Implementation")
	protected String implementation;

	public String getImplementation() {
		return implementation;
	}
	
	public String getTrigger() {
		return trigger;
	}
	
	public void readJSONimplementation(JSONObject modelElement) {
		setImplementation(modelElement.optString("implementation"));
	}
	
	public void readJSONtrigger(JSONObject modelElement) {
		setTrigger(modelElement.optString("trigger"));
	}
	
	public void setImplementation(String implementation) {
		this.implementation = implementation;
	}
	
	public void setTrigger(String triggerValue) {
		trigger = triggerValue;
	}

}
