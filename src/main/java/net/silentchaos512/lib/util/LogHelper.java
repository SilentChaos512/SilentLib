/*
 * SilentLib - LogHelper
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.util;

import lombok.Getter;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.text.WordUtils;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;

// TODO: 1.13: change to a utility class, rely more on Logger directly
// TODO: 1.13: add debug log class with same features as current debug method?
public class LogHelper {
    private static final Map<String, LogHelper> LOGGER_BY_MODNAME = new HashMap<>();

    @Getter
    private final Logger logger;
    @Getter
    private final int buildNumber;
    private String lastDebugOutput = "";

    public LogHelper(String modName, int buildNumber) {
        this.logger = LogManager.getLogger(modName);
        this.buildNumber = buildNumber;
        addLogHelper(modName);
    }

    public LogHelper(Logger providedLogger, int buildNumber) {
        this.logger = providedLogger;
        this.buildNumber = buildNumber;
        addLogHelper(providedLogger.getName());
    }

    private void addLogHelper(String modName) {
        LOGGER_BY_MODNAME.put(modName, this);
        this.debug("Add LogHelper for \"{}\"", modName);
    }

    /**
     * Gets the LogHelper for the mod name (<em>not mod ID</em>), if it exists. Holding a reference
     * to the object should be preferred; this method should not be used in most cases.
     *
     * @param modName The mod name (not ID)
     * @return Optional of LogHelper if one has been registered, empty Optional otherwise
     */
    public static Optional<LogHelper> getRegisteredLogger(String modName) {
        if (!LOGGER_BY_MODNAME.containsKey(modName)) return Optional.empty();
        return Optional.of(LOGGER_BY_MODNAME.get(modName));
    }

    public void catching(Throwable t) {
        this.logger.catching(t);
    }

    public void debug(String msg, Object... params) {
        this.logger.debug(msg, params);

        // Also print to standard output for easy viewing if in dev environment
        // TODO: Is there a better way to show debug-level logs in console?
        if (this.buildNumber == 0) {
            String newOutput = this.logger.getMessageFactory().newMessage(msg, params).getFormattedMessage();
            if (!newOutput.equals(lastDebugOutput)) {
                System.out.println(newOutput);
                this.lastDebugOutput = newOutput;
            }
        }
    }

    public void error(String msg, Object... params) {
        this.logger.error(msg, params);
    }

    public void fatal(String msg, Object... params) {
        this.logger.fatal(msg, params);
    }

    public void info(String msg, Object... params) {
        this.logger.info(msg, params);
    }

    public void log(Level level, String msg, Object... params) {
        this.logger.log(level, msg, params);
    }

    public void trace(String msg, Object... params) {
        this.logger.trace(msg, params);
    }

    public void warn(String msg, Object... params) {
        this.logger.warn(msg, params);
    }

    public void warn(Throwable t, String msg, Object... params) {
        this.logger.warn(msg, params);
        this.logger.catching(t);
    }

    public void noticableWarning(boolean trace, List<String> lines) {
        this.error("********************************************************************************");

        for (final String line : lines) {
            for (final String subline : wrapString(line, 78, false, new ArrayList<>())) {
                this.error("* " + subline);
            }
        }

        if (trace) {
            final StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            for (int i = 2; i < 8 && i < stackTrace.length; i++) {
                this.warn("*  at {}{}", stackTrace[i].toString(), i == 7 ? "..." : "");
            }
        }

        this.error("********************************************************************************");
    }

    private static List<String> wrapString(String string, int lnLength, boolean wrapLongWords, List<String> list) {
        final String lines[] = WordUtils.wrap(string, lnLength, null, wrapLongWords).split(SystemUtils.LINE_SEPARATOR);
        Collections.addAll(list, lines);
        return list;
    }
}
