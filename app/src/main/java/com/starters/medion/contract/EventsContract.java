package com.starters.medion.contract;

import android.provider.BaseColumns;


public final class EventsContract {
    private EventsContract(){}

    public static class EventsEntry implements BaseColumns{
        public static final String TABLE_NAME = "eventsdata";
        public static final String COLUMN_NAME_EVENTID = "eventid";
        public static final String COLUMN_NAME_EVENTNAME = "name";
        public static final String COLUMN_NAME_DATE = "date";
        public static final String COLUMN_NAME_TIME = "time";
        public static final String COLUMN_NAME_MEMBERS = "members";
        public static final String COLUMN_NAME_LOCATION = "location";
        public static final String COLUMN_NAME_ADMIN = "admin";

    }


}
