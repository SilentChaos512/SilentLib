package net.silentchaos512.lib.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.MinecraftForge;

public class LogHelper {

  private Logger logger;
  private int buildNumber = 0;

  @Deprecated
  /** @deprecated Use LogHelper(String, int) */
  public LogHelper(String modName) {

    logger = LogManager.getLogger(modName);
  }

  public LogHelper(String modName, int buildNumber) {

    logger = LogManager.getLogger(modName);
    this.buildNumber = buildNumber;
  }

  public void debug(Object... objects) {

    if (buildNumber == 0) {
      String line = lineFromList(objects);
      logger.debug(line);
      System.out.println(line);
    }
  }

  public void info(Object obj) {

    logger.info(obj);
  }

  public void warning(Object obj) {

    logger.warn(obj);
  }

  public void severe(Object obj) {

    logger.error(obj);
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
