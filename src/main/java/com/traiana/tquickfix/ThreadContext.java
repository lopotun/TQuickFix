package com.traiana.tquickfix;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
* Created with IntelliJ IDEA.
* User: EvgenyK
* Date: 12/3/14
* Time: 11:39 AM
*/
public class ThreadContext {
    /**
     * Parsing/validation error types.
     */
    public static enum ErrorType {
        /**
         * A field contains illegal value. E.g. a numeric field contains a word or character field contains a multicharacter string.
         */
        ILLEGAL_VALUE,
        /**
         * A filed that declared as mandatory is missing.
         */
        MISSING_MANDATORY_VALUE,
        /**
         * Value of a field is out the predefined values that this field can have.
         */
        OUT_OF_PREDEFINED_SCOPE,
        /**
         * Severe parsing error has occurred. In most cases that meas that entire message is invalid or seriously corrupted.
         */
        PARSING,
        /**
         * //TODO Not in use?
         */
        WARNING
    }

    private DateFormat date;
    private DateFormat time;
    private DateFormat dateTime;
    private DateFormat monthYear;
    private Map<ErrorType, List<CharSequence>> mapping;
    private static final ThreadContext INSTANCE = new ThreadContext();

    /**
     * Returns {@linkplain ThreadContext} singleton.
     * @return
     */
    public static ThreadContext getInstance() {
        return INSTANCE;
    }

    private ThreadContext() {
        mapping = new HashMap<ErrorType, List<CharSequence>>(ErrorType.values().length);
        mapping.put(ErrorType.ILLEGAL_VALUE, new LinkedList<CharSequence>());
        mapping.put(ErrorType.MISSING_MANDATORY_VALUE, new LinkedList<CharSequence>());
        mapping.put(ErrorType.OUT_OF_PREDEFINED_SCOPE, new LinkedList<CharSequence>());
        mapping.put(ErrorType.PARSING, new LinkedList<CharSequence>());
        mapping.put(ErrorType.WARNING, new LinkedList<CharSequence>());
        date = new SimpleDateFormat("yyyyMMdd");
        time = new SimpleDateFormat("HH:mm:ss");//:S
        dateTime = new SimpleDateFormat("yyyyMMdd-HH:mm:ss");//:S
        monthYear = new SimpleDateFormat("yyyyMM");
        System.out.println("CREATING");
    }

    /**
     * Clears errors state. This method is called internally by the framework every time before it starts to parse a message.
     * End user can call this method after message parsing and before message validation.
     */
    public void reset() {
        System.out.println("RESETTING");
        for(List<CharSequence> o : mapping.values()) {
            if(!o.isEmpty()) {
                o.clear();
            }
        }
    }

    /**
     * Adds an error of the given type to list of errors. This method is called internally by the framework
     * and usually should not be called by an end user.
     * @param errorType
     * @param error
     */
    public void addError(ErrorType errorType, CharSequence error) {
        mapping.get(errorType).add(error);
    }

    /**
     * Returns error list of the given error type.
     * @param errorType
     * @return
     */
    public List<CharSequence> getErrors(ErrorType errorType) {
        return mapping.get(errorType);
    }

    /**
     * Returns all detected errors as list.
     * @return
     */
    public List<CharSequence> getErrors() {
        List<CharSequence> zzz = new ArrayList<CharSequence>();
        for(List<CharSequence> o : mapping.values()) {
            if(!o.isEmpty()) {
                zzz.addAll(o);
            }
        }
        return zzz;
    }

    /**
     * Returns all detected errors separated by error type.
     * @return
     */
    public Map<ErrorType, List<CharSequence>> getAllErrors() {
        return mapping;
    }

    /**
     * Checks whether an error had occurred while message parsing/validation.
     * @return <em>true</em> if an error had occurred while message parsing/validation.
     */
    public boolean hasErrors() {
        for(List<CharSequence> o : mapping.values()) {
            if(!o.isEmpty()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks whether a error of the given error type had occurred while message parsing/validation.
     * @param errorType
     * @return
     */
    public boolean hasErrors(ErrorType errorType) {
        return !mapping.get(errorType).isEmpty();
    }

    /**
     * This method is called internally by the framework and usually should not be called by an end user.
     * @return
     */
    public DateFormat getDateFormatter() {
        return date;
    }

    /**
     * This method is called internally by the framework and usually should not be called by an end user.
     * @return
     */
    public DateFormat getTimeFormatter() {
        return time;
    }

    /**
     * This method is called internally by the framework and usually should not be called by an end user.
     * @return
     */
    public DateFormat getDateTimeFormatter() {
        return dateTime;
    }

    /**
     * This method is called internally by the framework and usually should not be called by an end user.
     * @return
     */
    public DateFormat getMonthYearFormatter() {
        return monthYear;
    }
}