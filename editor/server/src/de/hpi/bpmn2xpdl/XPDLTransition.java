package de.hpi.bpmn2xpdl;

import java.util.Arrays;

import org.json.JSONException;
import org.json.JSONObject;
import org.xmappr.Attribute;
import org.xmappr.Element;
import org.xmappr.RootElement;

@RootElement("Transition")
public class XPDLTransition extends XPDLThingConnectorGraphics {

	@Attribute("From")
	protected String from;
	@Attribute("Quantity")
	protected String quantity;
	@Attribute("To")
	protected String to;
	
	@Element("Condition")
	protected XPDLCondition condition;
	
	public static boolean handlesStencil(String stencil) {
		String[] types = {
				"SequenceFlow"};
		return Arrays.asList(types).contains(stencil);
	}
	
	public XPDLCondition getCondition() {
		return condition;
	}
	
	public String getFrom() {
		return from;
	}
	
	public String getQuantity() {
		return quantity;
	}
	
	public String getTo() {
		return to;
	}
	
	public void readJSONconditionexpression(JSONObject modelElement) throws JSONException {
		initializeCondition();
		
		JSONObject passCondition = new JSONObject();
		passCondition.put("conditionexpression", modelElement.optString("conditionexpression"));
		
		getCondition().parse(passCondition);
	}
	
	public void readJSONconditiontype(JSONObject modelElement) throws JSONException {
		String type = modelElement.optString("conditiontype");
		
		if (!type.equals("None")) {
			initializeCondition();
		
			JSONObject passCondition = new JSONObject();
			passCondition.put("conditiontype", modelElement.optString("conditiontype"));
		
			getCondition().parse(passCondition);
		}
	}
	
	public void readJSONconditionunknowns(JSONObject modelElement) throws JSONException {
		String type = modelElement.optString("conditiontype");
		
		if (!type.equals("None")) {
			initializeCondition();
		
			JSONObject passCondition = new JSONObject();
			passCondition.put("conditionunknowns", modelElement.optString("conditionunknowns"));
		
			getCondition().parse(passCondition);
		}
	}
	
	public void readJSONquantity(JSONObject modelElement) {
		setQuantity(modelElement.optString("quantity"));
	}
	
	public void readJSONshowdiamondmarker(JSONObject modelElement) {
		createExtendedAttribute("showdiamondmarker", modelElement.optString("showdiamondmarker"));
	}
	
	public void readJSONsource(JSONObject modelElement) {
		setFrom(modelElement.optString("source"));	
	}
	
	public void readJSONsourceref(JSONObject modelElement) {
		createExtendedAttribute("sourceref", modelElement.optString("sourceref"));
	}
	
	public void readJSONtarget(JSONObject modelElement) throws JSONException {
		JSONObject target = modelElement.getJSONObject("target");
		setTo(target.optString("resourceId"));
	}
	
	public void readJSONtargetref(JSONObject modelElement) {
		createExtendedAttribute("targetref", modelElement.optString("targetref"));
	}
	
	public void setCondition(XPDLCondition conditionValue) {
		condition = conditionValue;
	}
	
	public void setFrom(String source) {
		from = source;
	}
	
	public void setQuantity(String quantityValue) {
		quantity = quantityValue;
	}
	
	public void setTo(String target) {
		to = target;
	}
	
	public void writeJSONcondition(JSONObject modelElement) throws JSONException {
		XPDLCondition conditionObject = getCondition();
		if (conditionObject != null) {
			initializeProperties(modelElement);
			conditionObject.write(getProperties(modelElement));
		}
	}
	
	public void writeJSONquantity(JSONObject modelElement) throws JSONException {
		putProperty(modelElement, "quantity", getQuantity());
	}
	
	public void writeJSONstencil(JSONObject modelElement) throws JSONException {
		writeStencil(modelElement, "SequenceFlow");
	}
	
	protected void initializeCondition() {
		if (getCondition() == null) {
			setCondition(new XPDLCondition());
		}
	}
}
