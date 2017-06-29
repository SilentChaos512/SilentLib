package net.silentchaos512.lib.registry;

/**
 * Implement on classes like ModBlocks/ModItems. Register them with your SRegistry to have them automatically called for
 * the appropriate event. Creating blocks/items/etc should probably be done statically or in a constructor.
 * 
 * @author SilentChaos512
 * @since 2.2.2
 */
public interface IRegistrationHandler<T> {

  public void registerAll(SRegistry reg);
}
