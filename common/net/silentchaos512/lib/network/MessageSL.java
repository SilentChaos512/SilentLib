package net.silentchaos512.lib.network;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.lang3.tuple.Pair;

import io.netty.buffer.ByteBuf;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

/**
 * Convenient base class for packets. Copied from Silent's Gems, inspired by Psi.
 * 
 * @author SilentChaos512
 * @since 2.1.3
 * @param <REQ>
 */
public class MessageSL<REQ extends MessageSL> implements Serializable, IMessage, IMessageHandler<REQ, IMessage> {

  private static final HashMap<Class, Pair<Reader, Writer>> handlers = new HashMap();
  private static final HashMap<Class, Field[]> fieldCache = new HashMap();

  static {
    map(byte.class, MessageSL::readByte, MessageSL::writeByte);
    map(short.class, MessageSL::readShort, MessageSL::writeShort);
    map(int.class, MessageSL::readInt, MessageSL::writeInt);
    map(long.class, MessageSL::readLong, MessageSL::writeLong);
    map(float.class, MessageSL::readFloat, MessageSL::writeFloat);
    map(double.class, MessageSL::readDouble, MessageSL::writeDouble);
    map(boolean.class, MessageSL::readBoolean, MessageSL::writeBoolean);
    map(char.class, MessageSL::readChar, MessageSL::writeChar);
    map(String.class, MessageSL::readString, MessageSL::writeString);
    map(NBTTagCompound.class, MessageSL::readNBT, MessageSL::writeNBT);
    map(ItemStack.class, MessageSL::readItemStack, MessageSL::writeItemStack);
    map(BlockPos.class, MessageSL::readBlockPos, MessageSL::writeBlockPos);
  }

  public IMessage handleMessage(MessageContext context) {

    return null;
  }

  @Override
  public final IMessage onMessage(REQ message, MessageContext context) {

    return message.handleMessage(context);
  }

  @Override
  public void fromBytes(ByteBuf buf) {

    try {
      Class<?> clazz = getClass();
      Field[] clFields = getClassFields(clazz);
      for (Field f : clFields) {
        Class<?> type = f.getType();
        if (acceptField(f, type))
          readField(f, type, buf);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error at reading packet " + this, e);
    }
  }

  @Override
  public final void toBytes(ByteBuf buf) {

    try {
      Class<?> clazz = getClass();
      Field[] clFields = getClassFields(clazz);
      for (Field f : clFields) {
        Class<?> type = f.getType();
        if (acceptField(f, type))
          writeField(f, type, buf);
      }
    } catch (Exception e) {
      throw new RuntimeException("Error at writing packet " + this, e);
    }
  }

  @SuppressWarnings("unlikely-arg-type")
  private static Field[] getClassFields(Class<?> clazz) {

    if (fieldCache.containsValue(clazz))
      return fieldCache.get(clazz);
    else {
      Field[] fields = clazz.getFields();
      Arrays.sort(fields, (Field f1, Field f2) -> {
        return f1.getName().compareTo(f2.getName());
      });
      fieldCache.put(clazz, fields);
      return fields;
    }
  }

  private final void writeField(Field f, Class clazz, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException {

    Pair<Reader, Writer> handler = getHandler(clazz);
    handler.getRight().write(f.get(this), buf);
  }

  private final void readField(Field f, Class clazz, ByteBuf buf) throws IllegalArgumentException, IllegalAccessException {

    Pair<Reader, Writer> handler = getHandler(clazz);
    f.set(this, handler.getLeft().read(buf));
  }

  private static Pair<Reader, Writer> getHandler(Class<?> clazz) {

    Pair<Reader, Writer> pair = handlers.get(clazz);
    if (pair == null)
      throw new RuntimeException("No R/W handler for  " + clazz);
    return pair;
  }

  private static boolean acceptField(Field f, Class<?> type) {

    int mods = f.getModifiers();
    if (Modifier.isFinal(mods) || Modifier.isStatic(mods) || Modifier.isTransient(mods))
      return false;

    return handlers.containsKey(type);
  }

  private static <T extends Object> void map(Class<T> type, Reader<T> reader, Writer<T> writer) {

    handlers.put(type, Pair.of(reader, writer));
  }

  private static byte readByte(ByteBuf buf) {

    return buf.readByte();
  }

  private static void writeByte(byte b, ByteBuf buf) {

    buf.writeByte(b);
  }

  private static short readShort(ByteBuf buf) {

    return buf.readShort();
  }

  private static void writeShort(short s, ByteBuf buf) {

    buf.writeShort(s);
  }

  private static int readInt(ByteBuf buf) {

    return buf.readInt();
  }

  private static void writeInt(int i, ByteBuf buf) {

    buf.writeInt(i);
  }

  private static long readLong(ByteBuf buf) {

    return buf.readLong();
  }

  private static void writeLong(long l, ByteBuf buf) {

    buf.writeLong(l);
  }

  private static float readFloat(ByteBuf buf) {

    return buf.readFloat();
  }

  private static void writeFloat(float f, ByteBuf buf) {

    buf.writeFloat(f);
  }

  private static double readDouble(ByteBuf buf) {

    return buf.readDouble();
  }

  private static void writeDouble(double d, ByteBuf buf) {

    buf.writeDouble(d);
  }

  private static boolean readBoolean(ByteBuf buf) {

    return buf.readBoolean();
  }

  private static void writeBoolean(boolean b, ByteBuf buf) {

    buf.writeBoolean(b);
  }

  private static char readChar(ByteBuf buf) {

    return buf.readChar();
  }

  private static void writeChar(char c, ByteBuf buf) {

    buf.writeChar(c);
  }

  private static String readString(ByteBuf buf) {

    return ByteBufUtils.readUTF8String(buf);
  }

  private static void writeString(String s, ByteBuf buf) {

    ByteBufUtils.writeUTF8String(buf, s);
  }

  private static NBTTagCompound readNBT(ByteBuf buf) {

    return ByteBufUtils.readTag(buf);
  }

  private static void writeNBT(NBTTagCompound cmp, ByteBuf buf) {

    ByteBufUtils.writeTag(buf, cmp);
  }

  private static ItemStack readItemStack(ByteBuf buf) {

    return ByteBufUtils.readItemStack(buf);
  }

  private static void writeItemStack(ItemStack stack, ByteBuf buf) {

    ByteBufUtils.writeItemStack(buf, stack);
  }

  private static BlockPos readBlockPos(ByteBuf buf) {

    return BlockPos.fromLong(buf.readLong());
  }

  private static void writeBlockPos(BlockPos pos, ByteBuf buf) {

    buf.writeLong(pos.toLong());
  }

  // Functional interfaces
  public static interface Writer<T extends Object> {

    public void write(T t, ByteBuf buf);
  }

  public static interface Reader<T extends Object> {

    public T read(ByteBuf buf);
  }
}
