package net.silentchaos512.lib.network;

import net.minecraftforge.fml.common.network.IGuiHandler;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

/**
 * Network handler class, mostly copied from Silents' Gems.
 * @author SilentChaos512
 * @since 2.1.3
 */
public class NetworkHandlerSL {

  public final SimpleNetworkWrapper wrapper;

  private int lastMessageId = -1;

  public NetworkHandlerSL(String modId) {

    wrapper = NetworkRegistry.INSTANCE.newSimpleChannel(modId);
  }

  public void register(Class clazz, Side handlerSide) {

    wrapper.registerMessage(clazz, clazz, ++lastMessageId, handlerSide);
  }
}
