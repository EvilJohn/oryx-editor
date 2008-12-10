
package org.oryxeditor.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;



/**
 * Copyright (c) 2008 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
public class BPEL4ChorExporter extends HttpServlet {

	
    
	private static final long serialVersionUID = 3551528829474652693L;

	/**
     * The POST request.
     */
    protected void doPost(HttpServletRequest req, HttpServletResponse res) throws ServletException {
    	
    	String rdfString = req.getParameter("data");
    	
    	String resultString = transformTopology (rdfString, res) + transformGrounding (rdfString, res)
    							+ transformProcesses (rdfString, res);

    	if (resultString != null){
    		try {
    		       printResponse (res, resultString);
    		       return;
    		} catch (Exception e){
    		       handleException(res, e); 
    		}
    	}
    }
  
    
    private String transformTopology (String rdfString, HttpServletResponse res){
  	   /*
	   	// XSLT source
	   	final String xsltFilename = System.getProperty("catalina.home") + "/webapps/oryx/xslt/RDF2BPEL.xslt";
	   	final File xsltFile = new File(xsltFilename);
	   	final Source xsltSource = new StreamSource(xsltFile);	
	   	
	   	// Transformer Factory
	   	final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
	   	// Get the rdf source
	   	final Source rdfSource;
	   	InputStream rdf = new ByteArrayInputStream(rdfString.getBytes());
	   	rdfSource = new StreamSource(rdf);
	 
	   	// Get the result string
	   	String resultString = null;
	   	try {
	   		Transformer transformer = transformerFactory.newTransformer(xsltSource);
	   		StringWriter writer = new StringWriter();
	   		transformer.transform(rdfSource, new StreamResult(writer));
	   		resultString = writer.toString();
	   	} catch (Exception e){
	   		handleException(res, e); 
	   		return null;
	   	}
	
	   	return resultString;
	   	*/ 
    	return "TODO : transform topology";

   }
    
    private String transformGrounding (String rdfString, HttpServletResponse res){
  	   /*
	   	// XSLT source
	   	final String xsltFilename = System.getProperty("catalina.home") + "/webapps/oryx/xslt/RDF2BPEL.xslt";
	   	final File xsltFile = new File(xsltFilename);
	   	final Source xsltSource = new StreamSource(xsltFile);	
	   	
	   	// Transformer Factory
	   	final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
	   	// Get the rdf source
	   	final Source rdfSource;
	   	InputStream rdf = new ByteArrayInputStream(rdfString.getBytes());
	   	rdfSource = new StreamSource(rdf);
	 
	   	// Get the result string
	   	String resultString = null;
	   	try {
	   		Transformer transformer = transformerFactory.newTransformer(xsltSource);
	   		StringWriter writer = new StringWriter();
	   		transformer.transform(rdfSource, new StreamResult(writer));
	   		resultString = writer.toString();
	   	} catch (Exception e){
	   		handleException(res, e); 
	   		return null;
	   	}
	
	   	return resultString;
	   	*/
	   	return "TODO : transform topology";

   }
    

    private String transformProcesses (String rdfString, HttpServletResponse res){
	   
	   	// RDF2BPEL XSLT source
	   	final String xsltFilename = System.getProperty("catalina.home") + "/webapps/oryx/xslt/RDF2BPEL.xslt";
	   	final File xsltFile = new File(xsltFilename);
	   	final Source xsltSource = new StreamSource(xsltFile);	
	   	
	   	// Transformer Factory
	   	final TransformerFactory transformerFactory = TransformerFactory.newInstance();
	
	   	// Get the rdf source
	   	final Source rdfSource;
	   	InputStream rdf = new ByteArrayInputStream(rdfString.getBytes());
	   	rdfSource = new StreamSource(rdf);
	 
	   	// Get the result string
	   	String resultString = null;
	   	try {
	   		Transformer transformer = transformerFactory.newTransformer(xsltSource);
	   		StringWriter writer = new StringWriter();
	   		transformer.transform(rdfSource, new StreamResult(writer));
	   		resultString = writer.toString();
	   	} catch (Exception e){
	   		handleException(res, e); 
	   		return null;
	   	}
	   	
	   	//resultString = seperateProcesses (resultString);
	
	   	return resultString;

   }
    
   private void printResponse(HttpServletResponse res, String text){
    	if (res != null){
 
        	// Get the PrintWriter
        	res.setContentType("text/plain");
        	
        	PrintWriter out = null;
        	try {
        	    out = res.getWriter();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
    		out.print(text);
    	}
    }
    
    
    private void printError(HttpServletResponse res, String err){
    	if (res != null){
 
        	// Get the PrintWriter
        	res.setContentType("text/html");
        	
        	PrintWriter out = null;
        	try {
        	    out = res.getWriter();
        	} catch (IOException e) {
        	    e.printStackTrace();
        	}
        	
    		out.print("{success:false, content:'"+err+"'}");
    	}
    }
    
	private void handleException(HttpServletResponse res, Exception e) {
		e.printStackTrace();
		printError(res, e.getLocalizedMessage());
	}
    
}
