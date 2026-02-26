package com.fscc.automation.core.constants;

//TODO: Refactor classes to use these constants
public final class FrameworkConstants {

    private FrameworkConstants() {
    }

    // Timeouts
    public static final int DEFAULT_TIMEOUT_SECONDS = 10;
    public static final int IMPLICIT_WAIT_SECONDS = 5;
    public static final int PAGE_LOAD_TIMEOUT_SECONDS = 30;
    public static final int SCRIPT_TIMEOUT_SECONDS = 30;

    // Browser Settings
    public static final String DEFAULT_BROWSER = "chrome";
    public static final boolean HEADLESS_MODE = false;
    public static final String WINDOW_SIZE = "1920x1080";

    // File Paths
    public static final String SCREENSHOTS_PATH = "target/screenshots";
    public static final String REPORTS_PATH = "target/cucumber-reports";
    public static final String DOWNLOADS_PATH = "target/downloads";
}
