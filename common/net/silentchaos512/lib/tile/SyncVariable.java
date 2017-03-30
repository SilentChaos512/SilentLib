package net.silentchaos512.lib.tile;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * Marks variables that should be automatically synced with the client. Currently, this is used just for tile entities,
 * but could have other uses I guess? See {@link TileEntitySL} for usage.
 * 
 * @author SilentChaos512
 * @since 2.0.6
 */
@Retention(RUNTIME)
@Target(FIELD)
public @interface SyncVariable {

  /**
   * The name to read/write to NBT.
   */
  String name();

  /**
   * Should the variable be loaded in {@link TileEntity#readFromNBT}?
   */
  boolean onRead() default true;

  /**
   * Should the variable be saved in {@link TileEntity#writeToNBT}?
   */
  boolean onWrite() default true;

  /**
   * Should the variable be saved in {@link TileEntity#getUpdatePacket} and {@link TileEntity#getUpdateTag}?
   */
  boolean onPacket() default true;

  /**
   * Used together with onRead, onWrite, and onPacket to determine when a variable should be saved/loaded. In most
   * cases, you should probably just sync everything at all times.
   */
  public static enum Type {
    READ, WRITE, PACKET;
  }
}
