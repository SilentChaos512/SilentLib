package net.silentchaos512.lib.tile;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

import javax.activation.UnsupportedDataTypeException;
import java.lang.annotation.Annotation;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

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

  /**
   * Reads/writes sync variables for any object. Used by TileEntitySL in Lib. Gems uses this in PlayerDataHandler.
   *
   * @author SilentChaos512
   * @since 2.1.1
   */
  public static class Helper {

    /**
     * Reads sync variables for the object. This method will attempt to read a value from NBT and assign that value for
     * any field marked with the SyncVariable annotation.
     *
     * @param obj
     *          The object with SyncVariable fields.
     * @param tags
     *          The NBT to read values from.
     * @throws UnsupportedDataTypeException
     *           If the method does not know how to handle the type of the field.
     */
    public static void readSyncVars(Object obj, NBTTagCompound tags) {

      // Try to read from NBT for fields marked with SyncVariable.
      for (Field field : obj.getClass().getDeclaredFields()) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
          if (annotation instanceof SyncVariable) {
            SyncVariable sync = (SyncVariable) annotation;

            try {
              // Set fields accessible if necessary.
              if (!field.isAccessible())
                field.setAccessible(true);
              String name = sync.name();

              if (field.getType() == int.class)
                field.setInt(obj, tags.getInteger(name));
              else if (field.getType() == float.class)
                field.setFloat(obj, tags.getFloat(name));
              else if (field.getType() == String.class)
                field.set(obj, tags.getString(name));
              else if (field.getType() == boolean.class)
                field.setBoolean(obj, tags.getBoolean(name));
              else if (field.getType() == double.class)
                field.setDouble(obj, tags.getDouble(name));
              else if (field.getType() == long.class)
                field.setLong(obj, tags.getLong(name));
              else if (field.getType() == short.class)
                field.setShort(obj, tags.getShort(name));
              else if (field.getType() == byte.class)
                field.setByte(obj, tags.getByte(name));
              else
                throw new UnsupportedDataTypeException(
                    "Don't know how to read type " + field.getType() + " from NBT!");
            } catch (IllegalAccessException | UnsupportedDataTypeException ex) {
              ex.printStackTrace();
            }
          }
        }
      }
    }

    /**
     * Writes sync variables for the object. This method will take the values in all fields marked with the SyncVariable
     * annotation and save them to NBT.
     *
     * @param obj
     *          The object with SyncVariable fields.
     * @param tags
     *          The NBT to save values to.
     * @param syncType
     *          The sync type (WRITE or PACKET).
     * @return The modified tags.
     * @throws UnsupportedDataTypeException
     *           If the method does not know how to handle the type of the field.
     */
    public static NBTTagCompound writeSyncVars(Object obj, NBTTagCompound tags, Type syncType) {

      // Try to write to NBT for fields marked with SyncVariable.
      for (Field field : obj.getClass().getDeclaredFields()) {
        for (Annotation annotation : field.getDeclaredAnnotations()) {
          if (annotation instanceof SyncVariable) {
            SyncVariable sync = (SyncVariable) annotation;

            // Does variable allow writing in this case?
            if (syncType == SyncVariable.Type.WRITE && sync.onWrite()
                || syncType == SyncVariable.Type.PACKET && sync.onPacket()) {
              try {
                // Set fields accessible if necessary.
                if (!field.isAccessible())
                  field.setAccessible(true);
                String name = sync.name();

                if (field.getType() == int.class)
                  tags.setInteger(name, field.getInt(obj));
                else if (field.getType() == float.class)
                  tags.setFloat(name, field.getFloat(obj));
                else if (field.getType() == String.class)
                  tags.setString(name, (String) field.get(obj));
                else if (field.getType() == boolean.class)
                  tags.setBoolean(name, field.getBoolean(obj));
                else if (field.getType() == double.class)
                  tags.setDouble(name, field.getDouble(obj));
                else if (field.getType() == long.class)
                  tags.setLong(name, field.getLong(obj));
                else if (field.getType() == short.class)
                  tags.setShort(name, field.getShort(obj));
                else if (field.getType() == byte.class)
                  tags.setByte(name, field.getByte(obj));
                else
                  throw new UnsupportedDataTypeException(
                      "Don't know how to write type " + field.getType() + " to NBT!");
              } catch (IllegalAccessException | UnsupportedDataTypeException ex) {
                ex.printStackTrace();
              }
            }
          }
        }
      }

      return tags;
    }
  }
}
