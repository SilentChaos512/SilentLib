/*
 * Silent Lib -- GenericIntTrigger
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation version 3
 * of the License.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.advancements;

import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.criterion.CriterionInstance;
import net.minecraft.advancements.criterion.EntityPredicate;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.loot.ConditionArrayParser;
import net.minecraft.loot.ConditionArraySerializer;
import net.minecraft.util.JSONUtils;
import net.minecraft.util.ResourceLocation;
import net.silentchaos512.lib.SilentLib;

import java.util.*;

public class GenericIntTrigger implements ICriterionTrigger<GenericIntTrigger.Instance> {
    private static final ResourceLocation ID = new ResourceLocation(SilentLib.MOD_ID, "generic_int");
    private final Map<PlayerAdvancements, GenericIntTrigger.Listeners> listeners = new HashMap<>();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addPlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<GenericIntTrigger.Instance> listenerIn) {
        Listeners triggerListeners = this.listeners.get(playerAdvancementsIn);
        if (triggerListeners == null) {
            triggerListeners = new Listeners(playerAdvancementsIn);
            this.listeners.put(playerAdvancementsIn, triggerListeners);
        }
        triggerListeners.add(listenerIn);
    }

    @Override
    public void removePlayerListener(PlayerAdvancements playerAdvancementsIn, Listener<GenericIntTrigger.Instance> listenerIn) {
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
    public Instance createInstance(JsonObject json, ConditionArrayParser p_230307_2_) {
        String type = JSONUtils.getAsString(json, "type", "unknown");
        int value = JSONUtils.getAsInt(json, "value", 0);
        return new Instance(type, value);
    }

    public static class Instance extends CriterionInstance {
        String type;
        int value;

        Instance(String type, int value) {
            super(GenericIntTrigger.ID, EntityPredicate.AndPredicate.ANY);
            this.type = type;
            this.value = value;
        }

        public static Instance instance(ResourceLocation type, int value) {
            return new Instance(type.toString(), value);
        }

        public boolean test(String typeIn, int valueIn) {
            return this.type.equals(typeIn) && this.value <= valueIn;
        }

        @Override
        public JsonObject serializeToJson(ConditionArraySerializer p_230240_1_) {
            JsonObject json = new JsonObject();
            json.addProperty("type", this.type);
            json.addProperty("value", this.value);
            return json;
        }
    }

    public void trigger(ServerPlayerEntity player, ResourceLocation type, int value) {
        GenericIntTrigger.Listeners triggerListeners = this.listeners.get(player.getAdvancements());
        if (triggerListeners != null)
            triggerListeners.trigger(type.toString(), value);
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

        public void add(Listener<Instance> listener) {
            this.listeners.add(listener);
        }

        public void remove(Listener<Instance> listener) {
            this.listeners.remove(listener);
        }

        public void trigger(String typeIn, int valueIn) {
            List<Listener<Instance>> list = new ArrayList<>();

            for (Listener<Instance> listener : this.listeners)
                if (listener.getTriggerInstance().test(typeIn, valueIn))
                    list.add(listener);

            for (Listener<Instance> listener : list)
                listener.run(this.playerAdvancements);
        }
    }
}
