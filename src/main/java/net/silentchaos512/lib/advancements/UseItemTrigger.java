package net.silentchaos512.lib.advancements;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;

import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.AbstractCriterionInstance;
import net.minecraft.advancements.critereon.ItemPredicate;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.SilentLib;

// TODO: Test me!
public class UseItemTrigger implements ICriterionTrigger<UseItemTrigger.Instance> {

  private static final ResourceLocation ID = new ResourceLocation(SilentLib.MOD_ID, "use_item");
  private final Map<PlayerAdvancements, UseItemTrigger.Listeners> listeners = Maps.<PlayerAdvancements, UseItemTrigger.Listeners> newHashMap();

  @Override
  public ResourceLocation getId() {

    return ID;
  }

  @Override
  public void addListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UseItemTrigger.Instance> listener) {

    UseItemTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

    if (consumeitemtrigger$listeners == null) {
      consumeitemtrigger$listeners = new UseItemTrigger.Listeners(playerAdvancementsIn);
      this.listeners.put(playerAdvancementsIn, consumeitemtrigger$listeners);
    }

    consumeitemtrigger$listeners.add(listener);
  }

  @Override
  public void removeListener(PlayerAdvancements playerAdvancementsIn, ICriterionTrigger.Listener<UseItemTrigger.Instance> listener) {

    UseItemTrigger.Listeners consumeitemtrigger$listeners = this.listeners.get(playerAdvancementsIn);

    if (consumeitemtrigger$listeners != null) {
      consumeitemtrigger$listeners.remove(listener);

      if (consumeitemtrigger$listeners.isEmpty()) {
        this.listeners.remove(playerAdvancementsIn);
      }
    }
  }

  @Override
  public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {

    this.listeners.remove(playerAdvancementsIn);
  }

  @Override
  public UseItemTrigger.Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {

    ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
    return new UseItemTrigger.Instance(itempredicate);
  }

  public static class Instance extends AbstractCriterionInstance {

    ItemPredicate itempredicate;

    public Instance(ItemPredicate itempredicate) {

      super(UseItemTrigger.ID);
      this.itempredicate = itempredicate;
    }

    public boolean test(ItemStack stack) {

      return itempredicate.test(stack);
    }
  }

  public void trigger(EntityPlayerMP player, ItemStack stack) {

    UseItemTrigger.Listeners triggerListeners = this.listeners.get(player.getAdvancements());

    if (triggerListeners != null) {
      triggerListeners.trigger(stack);
    }
  }

  static class Listeners {

    private final PlayerAdvancements playerAdvancements;
    private final Set<ICriterionTrigger.Listener<UseItemTrigger.Instance>> listeners = Sets.<ICriterionTrigger.Listener<UseItemTrigger.Instance>> newHashSet();

    public Listeners(PlayerAdvancements playerAdvancementsIn) {

      this.playerAdvancements = playerAdvancementsIn;
    }

    public boolean isEmpty() {

      return this.listeners.isEmpty();
    }

    public void add(ICriterionTrigger.Listener<UseItemTrigger.Instance> listener) {

      this.listeners.add(listener);
    }

    public void remove(ICriterionTrigger.Listener<UseItemTrigger.Instance> listener) {

      this.listeners.remove(listener);
    }

    public void trigger(ItemStack stack) {

      List<ICriterionTrigger.Listener<UseItemTrigger.Instance>> list = null;

      for (ICriterionTrigger.Listener<UseItemTrigger.Instance> listener : this.listeners) {
        if (((UseItemTrigger.Instance) listener.getCriterionInstance()).test(stack)) {
          if (list == null) {
            list = Lists.<ICriterionTrigger.Listener<UseItemTrigger.Instance>> newArrayList();
          }

          list.add(listener);
        }
      }

      if (list != null) {
        for (ICriterionTrigger.Listener<UseItemTrigger.Instance> listener1 : list) {
          listener1.grantCriterion(this.playerAdvancements);
        }
      }
    }
  }
}
