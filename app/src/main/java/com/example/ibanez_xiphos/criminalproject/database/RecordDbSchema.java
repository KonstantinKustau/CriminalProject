package com.example.ibanez_xiphos.criminalproject.database;

public class RecordDbSchema {
    public static final class RecordTable{
        public static final String NAME = "records";

        public static final class Cols{
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String DATE = "date";
            public static final String SOLVED = "solved";
            public static final String CONTACT = "contact";
        }
    }
}
