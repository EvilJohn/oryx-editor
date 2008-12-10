package de.hpi.xforms.serialization;

import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import de.hpi.xforms.*;

/**
 * 
 * @author jan-felix.schwarz@student.hpi.uni-potsdam.de
 *
 */
public class XFormsXHTMLExporter {
	
	private XForm form;
	private Document doc;

	public XFormsXHTMLExporter(XForm form) {
		super();
		this.form = form;
	}
	
	/**
	 * Generate XForms+XHTML document
	 * @return XForms+XHTML document
	 */
	public Document getXHTMLDocument(String cssUrl) {

		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			doc = builder.newDocument();
			
			Element html = doc.createElementNS("http://www.w3.org/1999/xhtml", "html");
			html.setAttribute("xmlns", "http://www.w3.org/1999/xhtml");
			html.setAttribute("xmlns:xf", "http://www.w3.org/2002/xforms");
			html.setAttribute("xmlns:ev", "http://www.w3.org/2001/xml-events"  );
			html.setAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
			html.setAttribute("xmlns:xsd", "http://www.w3.org/2001/XMLSchema");
			doc.appendChild(html);
			addHead(html, cssUrl);
			addBody(html);
			
			/* 
			 * TODO: (for integration in process execution environment) adjust submissions to submit 
			 * instance data to following task fitting execution engine's requirements
			 */
			
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		
		return doc;
	}
	
	/**
	 * Generate XForms+XHTML document with additional markup for a button to submit
	 * data to the instance inspector 
	 * @param instanceInspectorUrl destination URI for submitting instance data for inspection
	 * @return XForms+XHTML document with markup for an instance inspector button
	 */
	public Document getXHTMLDocumentForInspection(String instanceInspectorUrl, String cssUrl) {
		if(doc==null) getXHTMLDocument(cssUrl);
		
		Element model = (Element) doc.getElementsByTagName("xf:model").item(0);
		Element instance = (Element) model.appendChild(
				doc.createElementNS("http://www.w3.org/2002/xforms", "xf:submission"));
		instance.setAttribute("id", "oryx_xforms_instance_inspection_submission");
		instance.setAttribute("resource", instanceInspectorUrl);
		instance.setAttribute("method", "post");
		instance.setAttribute("replace", "all");
		
		// add submit button
		Element body = (Element) doc.getElementsByTagName("body").item(0);
		Element rowDiv = (Element) body.appendChild(
				doc.createElementNS("http://www.w3.org/1999/xhtml", "div"));
		rowDiv.setAttribute("class", "form_row");
		Element submit = (Element) rowDiv.appendChild(
				doc.createElementNS("http://www.w3.org/2002/xforms", "xf:submit"));
		submit.setAttribute("id", "oryx_xforms_instance_inspection_submit");
		submit.setAttribute("submission", "oryx_xforms_instance_inspection_submission");
		Element label = (Element) submit.appendChild(
				doc.createElementNS("http://www.w3.org/2002/xforms", "xf:label"));
		label.appendChild(doc.createCDATASection("SHOW INSTANCE DATA"));
		
		return doc;
	}
	
	private void addHead(Element html, String cssUrl) {
		if(form.getHead()!=null) {
			form.setHead((Element) doc.importNode(form.getHead(), true));
			html.appendChild(form.getHead());
			modifyHead(form.getHead());
		} else {
			Element head = (Element) html.appendChild(
					doc.createElementNS("http://www.w3.org/1999/xhtml", "head"));
			generateHead(cssUrl, head);
		}
	}
	
	private void modifyHead(Element head) {
		if(head.getElementsByTagName("title").getLength()>0) {
			head.getElementsByTagName("title").item(0).setTextContent(form.getAttributes().get("name"));
		} else {
			Element title = (Element) head.appendChild(doc.createElement("title"));
			title.appendChild(doc.createCDATASection(form.getAttributes().get("name")));
			head.appendChild(title);
		}
		
		Element model;
		if(head.getElementsByTagNameNS("http://www.w3.org/2002/xforms", "xf:model").getLength()>0) {
			model = (Element) head.getElementsByTagNameNS("http://www.w3.org/2002/xforms", "xf:model").item(0);
		} else {
			model = (Element) head.appendChild(
					doc.createElementNS("http://www.w3.org/2002/xforms", "xf:model"));
			addAttributes(model, form.getModel());
		}
		
		for(Bind bind : form.getModel().getBinds()) {
			NodeList bindNodes = model.getElementsByTagNameNS("http://www.w3.org/2002/xforms", "xf:bind");
			for(int i=0; i<bindNodes.getLength(); i++) {
				boolean replaced = false;
				String nodeset = ((Element) bindNodes.item(i)).getAttribute("nodeset");
				if((nodeset!=null) && nodeset.equals(bind.getAttributes().get("nodeset"))) {
					model.replaceChild(getElement(bind), bindNodes.item(i));
					replaced = true;
				}
				if(!replaced) 
					addElementsRecursive(model, bind);
			}
		}
		
		for(Submission submission : form.getModel().getSubmissions()) {
			boolean replaced = false;
			NodeList submissionNodes = model.getElementsByTagNameNS("http://www.w3.org/2002/xforms", "xf:submission");
			for(int i=0; i<submissionNodes.getLength(); i++) {
				String id = ((Element) submissionNodes.item(i)).getAttribute("id");
				if((id!=null) && id.equals(submission.getAttributes().get("id"))) {
					model.replaceChild(getElement(submission), submissionNodes.item(i));
					replaced = true;
				}
			}
			if(!replaced) 
				addElementsRecursive(model, submission);
		}
			
	}

	private void generateHead(String cssUrl, Element head) {
		Element title = (Element) head.appendChild(doc.createElement("title"));
		title.appendChild(doc.createCDATASection(form.getAttributes().get("name")));
		
		Element link = (Element) head.appendChild(
				doc.createElementNS("http://www.w3.org/1999/xhtml", "link"));
		link.setAttribute("rel", "stylesheet");
		link.setAttribute("media", "screen");
		link.setAttribute("href", cssUrl);
		
		Element model = (Element) head.appendChild(
				doc.createElementNS("http://www.w3.org/2002/xforms", "xf:model"));
		addAttributes(model, form.getModel());
		
		Element instance = (Element) model.appendChild(
				doc.createElementNS("http://www.w3.org/2002/xforms", "xf:instance"));
		Node instanceChild = form.getModel().getInstance().getContent().getFirstChild();
		while(instanceChild!=null) {
			instance.appendChild(doc.importNode(instanceChild, true));
			instanceChild = instanceChild.getNextSibling();
		}
		
		for(Bind bind : form.getModel().getBinds())
			addElementsRecursive(model, bind);
		
		for(Submission submission : form.getModel().getSubmissions())
			addElementsRecursive(model, submission);
		
	}
	
	private void addBody(Element html) {
		Element body = (Element) html.appendChild(
			doc.createElementNS("http://www.w3.org/1999/xhtml", "body"));
		String formName = form.getAttributes().get("name");
		if(formName!=null) {
			Element headline = (Element) body.appendChild(
					doc.createElementNS("http://www.w3.org/1999/xhtml", "h1"));
			headline.appendChild(doc.createCDATASection(form.getAttributes().get("name")));
		}
		
		// realize row layouting using divs
		int lastYPosition = 0;
		Element rowDiv = null;
		for(XFormsUIElement element : form.getChildElements()) {
			if(element.getYPosition() > lastYPosition) {
				// next row
				rowDiv = (Element) doc.createElementNS("http://www.w3.org/1999/xhtml", "div");
				rowDiv.setAttribute("class", "form_row");
				body.appendChild(rowDiv);
				lastYPosition = element.getYPosition();
			}
			addElementsRecursive(rowDiv, element);
		}
	}
	
	private void addElementsRecursive(Element xmlElement, XFormsElement xfElement) {
		
		if(xfElement==null) return;
		Element newXmlElement = (Element) xmlElement.appendChild(getElement(xfElement));
		
		if(xfElement instanceof LabelContainer) {
			addElementsRecursive(newXmlElement, ((LabelContainer) xfElement).getLabel());
		}
		
		if(xfElement instanceof Switch) {
			for(Case xfChild : ((Switch) xfElement).getCases()) {
				addElementsRecursive(newXmlElement, xfChild);
			}
		}
		
		if(xfElement instanceof ListUICommonContainer) {
			for(ListUICommon xfChild : ((ListUICommonContainer) xfElement).getListUICommons()) {
				addElementsRecursive(newXmlElement, xfChild);
			}
		}
		
		if(xfElement instanceof UIElementContainer) {
			// realize row layouting using divs
			int lastYPosition = 0;
			Element rowDiv = null;
			for(XFormsUIElement xfChild : ((UIElementContainer) xfElement).getChildElements()) {
				if(xfChild.getYPosition() > lastYPosition) {
					// next row
					rowDiv = (Element) doc.createElementNS("http://www.w3.org/1999/xhtml", "div");
					rowDiv.setAttribute("class", "form_row");
					newXmlElement.appendChild(rowDiv);
					lastYPosition = xfChild.getYPosition();
				}
				addElementsRecursive(rowDiv, xfChild);
			}
		}
		
		if(xfElement instanceof Item) {
			addElementsRecursive(newXmlElement, ((Item) xfElement).getValue());
		}
		
		if(xfElement instanceof Itemset) {
			addElementsRecursive(newXmlElement, ((Itemset) xfElement).getValue());
			addElementsRecursive(newXmlElement, ((Itemset) xfElement).getCopy());
		}
		
		if(xfElement instanceof UICommonContainer) {
			addElementsRecursive(newXmlElement, ((UICommonContainer) xfElement).getHelp());
			addElementsRecursive(newXmlElement, ((UICommonContainer) xfElement).getHint());
			addElementsRecursive(newXmlElement, ((UICommonContainer) xfElement).getAlert());
		}
		
		if(xfElement instanceof ActionContainer) {
			for(AbstractAction xfChild : ((ActionContainer) xfElement).getActions()) {
				addElementsRecursive(newXmlElement, xfChild);
			}
		}
		
	}
	
	private Element getElement(XFormsElement xfElement) {
		Element element = doc.createElementNS(
				"http://www.w3.org/2002/xforms", "xf:" + xfElement.getTagName());
		addAttributes(element, xfElement);
		return element;
	}
	
	private void addAttributes(Element xmlElement, XFormsElement xfElement) {
		for(Entry<String, String> attribute : xfElement.getAttributes().entrySet()) {
			if((attribute.getValue()!=null) && !attribute.getValue().equals("/") && !attribute.getValue().equals("")) {
				String namespace = "http://www.w3.org/2002/xforms";
				if(attribute.getKey().startsWith("ev:"))
					namespace = "http://www.w3.org/2001/xml-events";
				xmlElement.setAttributeNS(namespace, attribute.getKey(), attribute.getValue());
			}
				
		}
		if(xfElement instanceof PCDataContainer) {
			PCDataContainer pcDataContainer = (PCDataContainer) xfElement;
			xmlElement.appendChild(doc.createCDATASection(pcDataContainer.getContent()));
		}
	}
	
}
