/**
 * Copyright (c) 2010
 * Tobias Metzke
 *
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 **/
 
if(!ORYX.Plugins) {
	ORYX.Plugins = {};
}

//TODO language support
ORYX.I18N.PictureSupport = {
	imp: {
		name: "Import",
		description: "Import ...",
		group: "import"
		// ...
	}
}

// just for logging
ORYX_LOGLEVEL = 3;
 
ORYX.Plugins.PictureSupport = ORYX.Plugins.AbstractPlugin.extend({
 
	 construct: function(){
        // Call super class constructor
        arguments.callee.$.construct.apply(this, arguments);
        
        // build a button in the tool bar
        this.facade.offer({
            'name': ORYX.I18N.PictureSupport.imp.name,
            'functionality': this.importPicture.bind(this),
            'group': ORYX.I18N.PictureSupport.imp.group,
            'dropDownGroupIcon': ORYX.PATH + "images/import.png",
			'icon': ORYX.PATH + "images/page_white_javascript.png",
            'description': ORYX.I18N.PictureSupport.imp.description,
            'index': 0,
            'minShape': 0,
            'maxShape': 0
        });
        
        // change the shape menu's alignment
        ORYX.CONFIG.SHAPEMENU_RIGHT = ORYX.CONFIG.SHAPEMENU_BOTTOM;
        ORYX.CONFIG.SHAPEMENU_BUTTONS_PER_LEVEL_BOTTOM = 6;
        
        // catch occurring events
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_LOADED, this.pictureInstantiation.bind(this));
		this.facade.registerOnEvent(ORYX.CONFIG.EVENT_PROPWINDOW_PROP_CHANGED, this.handleProperties.bind(this));
		
 	},
	
	handleProperties: function(event){
		var shape = event.elements.first();
		var properties = shape._svgShapes.find(function(element) { return element.element.id == (shape.id + "properties_frame") });
		var image_frame = shape._svgShapes.find(function(element) { return element.element.id == (shape.id + "image_frame") });
		var text_frame = shape._svgShapes.find(function(element) { return element.element.id == (shape.id + "text_frame_title") });
		
		// if properties shall be hidden, delete them from HTML  
		if(event["key"]=="oryx-showproperties" && event["value"]==false){
			//TODO replace "lastElementChild" by specific HTML tag id
			text_frame.element.parentNode.removeChild(text_frame.element.parentNode.lastElementChild);
			//bounds AND _oldBounds need to be set, otherwise bounds are reset to _oldBounds, if node is moved
			shape._oldBounds.set(
				shape.bounds.a.x, 
				shape.bounds.a.y, 
				shape.bounds.b.x, 
				shape.bounds.a.y + 60);
			shape.bounds.set(
				shape.bounds.a.x, 
				shape.bounds.a.y, 
				shape.bounds.b.x, 
				shape.bounds.a.y + 60);	
		};
		
		if(event["key"]=="oryx-showproperties" && event["value"]==true){
			//TODO resize properties and its HTML rectangle (@properties.element) and children according to content
			//TODO insert properties HTML rectangle (@properties.element) back into DOM 
			
			//bounds AND _oldBounds need to be set, otherwise bounds are reset to _oldBounds, if node is moved
			shape.bounds.set(
				shape.bounds.a.x, 
				shape.bounds.a.y, 
				shape.bounds.b.x, 
				shape.bounds.a.y + 60 + properties.height);
			shape._oldBounds.set(
				shape.bounds.a.x, 
				shape.bounds.a.y, 
				shape.bounds.b.x, 
				shape.bounds.a.y + 60 + properties.height);
		};
	},
	
	importPicture: function(){},
 	
 	onSelectionChange: function(){},
 	
 	pictureInstantiation: function(event){
 		//create a process lane if the canvas is empty
		if(this.facade.getCanvas().children.length == 0){		
			var mynamespace = "http://b3mn.org/stencilset/picture#";
			var mytype = "http://b3mn.org/stencilset/picture#process";
		
			this.facade.createShape({
				type: mytype,
				namespace: mynamespace,
				position: {x: 0, y: 0}
			}).refresh();
		}
	}
 	
 });
