/*
 * SilentLib - EntityMatchList
 * Copyright (C) 2018 SilentChaos512
 *
 * This library is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package net.silentchaos512.lib.collection;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.util.ResourceLocation;

public class EntityMatchList extends AbstractMatchList<Entity> {
    public EntityMatchList(boolean whitelist, boolean allowUserToChangeType, String... defaultValues) {
        super(whitelist, allowUserToChangeType, defaultValues);
    }

    @Override
    protected boolean contains(Entity entity) {
        ResourceLocation resource = EntityList.getKey(entity);
        if (resource == null)
            return false;

        String id = resource.toString();
        String idOld = EntityList.getEntityString(entity);

        return getList().stream().anyMatch(entry -> keyMatches(entry, id)
                || entry.equalsIgnoreCase(idOld)
                || entry.equalsIgnoreCase("minecraft:" + id));
    }
}
