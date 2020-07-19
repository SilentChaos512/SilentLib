package net.silentchaos512.lib.advancements;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.advancements.criterion.ItemPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.SilentLib;

import java.util.*;

// TODO: Triggers for wrong items?
public class UseItemTrigger implements ICriterionTrigger<UseItemTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(SilentLib.MOD_ID, "use_item");
    private final Map<PlayerAdvancements, UseItemTrigger.Listeners> listeners = new HashMap<>();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listenerIn) {
        Listeners triggerListeners = this.listeners.get(playerAdvancementsIn);
        if (triggerListeners == null) {
            triggerListeners = new UseItemTrigger.Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, triggerListeners);
        }
        triggerListeners.add(listenerIn);
    }

    @Override
    public void removeListener(PlayerAdvancements playerAdvancementsIn, Listener<Instance> listenerIn) {
        Listeners triggerListeners = this.listeners.get(playerAdvancementsIn);
        if (triggerListeners != null) {
            triggerListeners.remove(listenerIn);
            if (triggerListeners.isEmpty())
                this.listeners.remove(playerAdvancementsIn);
        }
    }

    @Override
    public void removeAllListeners(PlayerAdvancements playerAdvancementsIn) {
        this.listeners.remove(playerAdvancementsIn);
    }

    // deserializeInstance
    @Override
    public Instance func_230307_a_(JsonObject json, ConditionArrayParser p_230307_2_) {
        ItemPredicate itempredicate = ItemPredicate.deserialize(json.get("item"));
        Target target = Target.fromString(JSONUtils.getString(json, "target", "any"));
        return new UseItemTrigger.Instance(itempredicate, target);
    }

    public static class Instance extends CriterionInstance {
        ItemPredicate itempredicate;
        Target target;

        Instance(ItemPredicate itempredicate, Target target) {
            super(UseItemTrigger.ID, EntityPredicate.AndPredicate.field_234582_a_);
            this.itempredicate = itempredicate;
            this.target = target;
        }

        public static Instance instance(ItemPredicate predicate, Target target) {
            return new Instance(predicate, target);
        }

        public boolean test(ItemStack stack, Target target) {
            return itempredicate.test(stack) && (this.target == target || this.target == Target.ANY);
        }

        @Override
        public JsonObject func_230240_a_(ConditionArraySerializer p_230240_1_) {
            JsonObject json = new JsonObject();
            json.add("item", this.itempredicate.serialize());
            json.addProperty("target", this.target.name());
            return json;
        }
    }

    public void trigger(ServerPlayerEntity player, ItemStack stack, Target target) {
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

        public void add(ICriterionTrigger.Listener<UseItemTrigger.Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(ICriterionTrigger.Listener<UseItemTrigger.Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(ItemStack stack, Target target) {
            List<Listener<Instance>> list = null;

            for (Listener<Instance> listener : this.listeners) {
                if (listener.getCriterionInstance().test(stack, target)) {
                    if (list == null) list = new ArrayList<>();
                    list.add(listener);
                }
            }

            if (list != null) {
                for (ICriterionTrigger.Listener<UseItemTrigger.Instance> listener1 : list)
                    listener1.grantCriterion(this.playerAdvancements);
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
