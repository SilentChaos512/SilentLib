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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LogHelper {

    @Getter
    private Logger logger;
    @Getter
    private int buildNumber = 0;
    private String lastDebugOutput = "";

    /**
     * @deprecated Use LogHelper(String, int)
     */
    @Deprecated
    public LogHelper(String modName) {
        this(modName, 0);
    }

    public LogHelper(String modName, int buildNumber) {
        this.logger = LogManager.getLogger(modName);
        this.buildNumber = buildNumber;
    }

    public void catching(Throwable t) {
        this.logger.catching(t);
    }

    public void debug(String msg, Object... params) {
        this.logger.debug(msg, params);
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
            for (final String subline : wrapString(line, 78, false, new ArrayList<String>())) {

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

    public static List<String> wrapString(String string, int lnLength, boolean wrapLongWords, List<String> list) {

        final String lines[] = WordUtils.wrap(string, lnLength, null, wrapLongWords).split(SystemUtils.LINE_SEPARATOR);
        Collections.addAll(list, lines);
        return list;
    }

    @Deprecated
    public void debug(Object... objects) {

        if (buildNumber == 0) {
            String line = lineFromList(objects);

            if (!line.equals(lastDebugOutput)) {
                logger.debug(line);
                System.out.println(line);
                lastDebugOutput = line;
            }
        }
    }

    @Deprecated
    public void info(Object obj) {
        logger.info(obj);
    }

    @Deprecated
    public void info(Object... objects) {
        logger.info(lineFromList(objects));
    }

    @Deprecated
    public void warning(Object obj) {
        logger.warn(obj);
    }

    @Deprecated
    public void warning(Object... objects) {
        logger.warn(lineFromList(objects));
    }

    @Deprecated
    public void severe(Object obj) {
        logger.error(obj);
    }

    @Deprecated
    public void severe(Object... objects) {
        logger.error(lineFromList(objects));
    }

    public void derp() {
        debug("Derp!");
    }

    public String lineFromList(Object... objects) {
        String str = "";
        for (int i = 0; i < objects.length; ++i) {
            if (i != 0) {
                str += ", ";
            }
            str += objects[i];
        }
        return str;
    }
}
