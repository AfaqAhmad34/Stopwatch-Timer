package com.example.stopwatch;

public class Constants {
    // Channel ID for notifications
    public static final String CHANNEL_ID = "Stopwatch_Notifications";

    // Service Actions
    public static final String START = "START";
    public static final String PAUSE = "PAUSE";
    public static final String RESET = "RESET";
    public static final String GET_STATUS = "GET_STATUS";
    public static final String MOVE_TO_FOREGROUND = "MOVE_TO_FOREGROUND";
    public static final String MOVE_TO_BACKGROUND = "MOVE_TO_BACKGROUND";

    // Intent Extras
    public static final String STOPWATCH_ACTION = "STOPWATCH_ACTION";
    public static final String TIME_ELAPSED = "TIME_ELAPSED";
    public static final String IS_STOPWATCH_RUNNING = "IS_STOPWATCH_RUNNING";

    // Intent Actions
    public static final String STOPWATCH_TICK = "STOPWATCH_TICK";
    public static final String STOPWATCH_STATUS = "STOPWATCH_STATUS";
}
