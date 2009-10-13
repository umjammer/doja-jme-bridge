/*
 * @(#)InvalidRecordIDException.java	1.13 02/07/24 @(#)
 *
 * Portiona Copyright (c) 2000-2002 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Copyright 2000 Motorola, Inc. All Rights Reserved.
 * This notice does not imply publication.
 */

package vavi.microedition.rms; 


/**
 * Thrown to indicate an operation could not be completed because the
 * record ID was invalid.
 *
 * @since MIDP 1.0
 */
public class InvalidRecordIDException extends RecordStoreException { 

    /**
     * Constructs a new <code>InvalidRecordIDException</code> with the
     * specified detail message.
     *
     * @param message the detail message
     */
    public InvalidRecordIDException(String message) {
	super(message);
    }
    
    /** 
     * Constructs a new <code>InvalidRecordIDException</code> with no detail 
     * message. 
     */
    public InvalidRecordIDException() {
    } 
} 

/* */
