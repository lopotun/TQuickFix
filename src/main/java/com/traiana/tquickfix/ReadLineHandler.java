package com.traiana.tquickfix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Processes a string that was read from a file or any other data source.
 * User: EvgenyK
 * Date: 11/24/14
 * Time: 10:35 AM
 */
public abstract class ReadLineHandler<T> {
    protected abstract boolean isSpecialSymbolsAware();
    protected abstract void handleData(String line);
    public abstract T getResult();

    protected void lineRead(String line) {
        if(line == null) {
            return;
        }
        line = line.trim();
        if(line.length() == 0) {
            return;
        }

        if(isSpecialSymbolsAware()) {
            char firstChar = line.charAt(0);
            switch(firstChar) {
                case '#':
                case ';':
                    comment(line);
                    break;
                case '!':
                    command(line);
                    break;
                default: handleData(line);
            }
        } else {
            handleData(line);
        }
    }

    @SuppressWarnings("unused")
    protected void command(String line) {
        //Do nothing.
    }

    @SuppressWarnings("unused")
    protected void comment(String line) {
        //Do nothing.
    }

    protected void eof() {
        //Do nothing.
    }
}





class LinesCollector extends ReadLineHandler<String> {
    private StringBuilder sb = new StringBuilder();

    @Override
    protected void handleData(String line) {
        sb.append(line).append('\n');
    }

    @Override
    public String getResult() {
        return sb.toString();
    }

    @Override
    protected boolean isSpecialSymbolsAware() {
        return false;
    }

    /**
     * Gets rid of the last new-line symbol.
     */
    @Override
    protected void eof() {
        if(sb.length() > 0) {
            sb.setLength(sb.length() - 1);
        }
    }
}


/**
 * @deprecated Not in use anymore
 */
class ConfigParser extends ReadLineHandler<Map<ConfigParser.Commands, Map<String, Set<String>>>> {
    public static enum Commands {ALLOWED_PARSE_VALIDATION, SUPPRESSED_VALIDATION}
    private Map<ConfigParser.Commands, Map<String, Set<String>>> commangs = new HashMap<Commands, Map<String, Set<String>>>(3);
    private Map<String, Set<String>> allowParseValidation;
    private Map<String, Set<String>> suppressValidation;
    private boolean allowParseValidationActive = false, suppressValidationActive = false;

    @Override
    protected void handleData(String line) {
        if(allowParseValidationActive || suppressValidationActive) {
            int lastDotPosition = line.lastIndexOf('.');
            String className = line.substring(0, lastDotPosition);
            String variableName = line.substring(lastDotPosition + 1);

            if(allowParseValidationActive) {
                addMapping(allowParseValidation, className, variableName);
            } else {
                addMapping(suppressValidation, className, variableName);
            }
        }
    }

    private void addMapping(Map<String, Set<String>> mapping, String className, String variableName) {
        Set<String> affectedVars = mapping.get(className);
        if(affectedVars == null) {
            affectedVars = new HashSet<String>();
            mapping.put(className, affectedVars);
        }
        affectedVars.add(variableName);
    }

    @Override
    protected void command(String line) {
        if(line.equalsIgnoreCase("!force_parse_validation_begin")) {
            allowParseValidation = new HashMap<String, Set<String>>();
            allowParseValidationActive = true;
            commangs.put(Commands.ALLOWED_PARSE_VALIDATION, allowParseValidation);
            return;
        }
        if(line.equalsIgnoreCase("!force_parse_validation_end")) {
            allowParseValidationActive = false;
        }

        if(line.equalsIgnoreCase("!suppress_validation_begin")) {
            suppressValidation = new HashMap<String, Set<String>>();
            suppressValidationActive = true;
            commangs.put(Commands.SUPPRESSED_VALIDATION, suppressValidation);
            return;
        }
        if(line.equalsIgnoreCase("!suppress_validation_end")) {
            suppressValidationActive = false;
        }
    }

    @Override
    public Map<ConfigParser.Commands, Map<String, Set<String>>> getResult() {
        return commangs;
    }

    @Override
    protected boolean isSpecialSymbolsAware() {
        return true;
    }
}