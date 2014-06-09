package com.classowl.app.message;

/**
 * Constants related to messages that are passed between Fragments/Activities and Services.
 */
public class Constants {
    /**
     * Messages IDs
     */
    public static final String MSG_TYPE = "msg_type";
    public static final int MSG_GET_SCHOOLS = 1;

    /**
     * Data passed as message arguments.
     */
    public static final String SCHOOLS_DATA = "schools_data";
    public static final String SCHOOLS_ACTION = "com.classowl.app.message.Constants.SCHOOLS_ACTION";
}
