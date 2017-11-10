package com.googleSearch.test_googleSearch;

import java.util.EventObject;

public class JImagePanelEvent extends EventObject {

    public static final Integer EVENT_SAVE = 1;
	    
    
    private Integer eventType = null;
    
    public JImagePanelEvent(Object source, Integer eventType) {
        super(source);
        this.eventType = eventType;
    }
    
    public Object getSource() {
    	return source;
    }
    
    public Object getEventType() {
    	return eventType;
    }

}
