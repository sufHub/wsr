package com.suf.wsr.daily_report.intf;

public class DailyReportException extends Exception{

	private static final long serialVersionUID = 1L;
	
	  /**
     * Constructor.
     */
    public DailyReportException() {
    }

    /**
     * Constructor.
     * @param message the message
     */
    public DailyReportException(String message) {
        super(message);
    }

    /**
     * Constructor.
     * @param message the message
     * @param param the error parameter
     */
    public DailyReportException(String message, String param) {
        super(message);
    }

    /**
     * Constructor.
     * @param cause the cause
     */
    public DailyReportException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructor.
     * @param message the message
     * @param cause the cause
     */
    public DailyReportException(String message, Throwable cause) {
        super(message, cause);
    }

}
