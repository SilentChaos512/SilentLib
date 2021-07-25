package net.silentchaos512.lib.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.PlayerAdvancements;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.ItemStack;
import net.silentchaos512.lib.SilentLib;

import java.util.*;

public class UseItemTrigger implements CriterionTrigger<UseItemTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(SilentLib.MOD_ID, "use_item");
    private final Map<PlayerAdvancements, UseItemTrigger.Listeners> listeners = new HashMap<>();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listenerIn) {
        Listeners triggerListeners = this.listeners.get(playerAdvancementsIn);
        if (triggerListeners == null) {
            triggerListeners = new UseItemTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, triggerListeners);
        }
        triggerListeners.add(listenerIn);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listenerIn) {
        Listeners triggerListeners = this.listeners.get(playerAdvancementsIn);
        if (triggerListeners != null) {
            triggerListeners.remove(listenerIn);
            if (triggerListeners.isEmpty())
                this.listeners.remove(playerAdvancementsIn);
        }
    }

    @Override
    public void removePlayerListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    @Override
    public Instance createInstance(JsonObject json, DeserializationContext p_230307_2_) {
        ItemPredicate itempredicate = ItemPredicate.fromJson(json.get("item"));
        Target target = Target.fromString(GsonHelper.getAsString(json, "target", "any"));
        return new UseItemTrigger.Instance(itempredicate, target);
    }

    public static class Instance extends AbstractCriterionTriggerInstance {
        ItemPredicate itempredicate;
        Target target;

        Instance(ItemPredicate itempredicate, Target target) {
            super(UseItemTrigger.ID, EntityPredicate.Composite.ANY);
            this.itempredicate = itempredicate;
            this.target = target;
        }

        public static Instance instance(ItemPredicate predicate, Target target) {
            return new Instance(predicate, target);
        }

        public boolean test(ItemStack stack, Target target) {
            return itempredicate.matches(stack) && (this.target == target || this.target == Target.ANY);
        }

        @Override
        public JsonObject serializeToJson(SerializationContext p_230240_1_) {
            JsonObject json = new JsonObject();
            json.add("item", this.itempredicate.serializeToJson());
            json.addProperty("target", this.target.name());
            return json;
        }
    }

    public void trigger(ServerPlayer player, ItemStack stack, Target target) {
        UseItemTrigger.Listeners triggerListeners = this.listeners.get(player.getAdvancements());
        if (triggerListeners != null)
            triggerListeners.trigger(stack, target);
    }

    static class Listeners {
        private final PlayerAdvancements playerAdvancements;
        private final Set<Listener<Instance>> listeners = new HashSet<>();

        Listeners(PlayerAdvancements playerAdvancementsIn) {
            this.playerAdvancements = playerAdvancementsIn;
        }

        public boolean isEmpty() {
            return this.listeners.isEmpty();
        }

        public void add(CriterionTrigger.Listener<UseItemTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(CriterionTrigger.Listener<UseItemTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(ItemStack stack, Target target) {
            List<Listener<Instance>> list = null;

            for (Listener<Instance> listener : this.listeners) {
                if (listener.getTriggerInstance().test(stack, target)) {
                    if (list == null) list = new ArrayList<>();
                    list.add(listener);
                }
            }

            if (list != null) {
                for (CriterionTrigger.Listener<UseItemTrigger.Instance> listener1 : list)
                    listener1.run(this.playerAdvancements);
            }
        }
    }

    public enum Target {
        BLOCK, ENTITY, ITEM, ANY;

        static Target fromString(String str) {
            for (Target t : values())
                if (t.name().equalsIgnoreCase(str))
                    return t;
            return ANY;
        }
    }
}
