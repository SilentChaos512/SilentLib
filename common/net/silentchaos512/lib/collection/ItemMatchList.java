package net.silentchaos512.lib.collection;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

import java.util.Arrays;
import java.util.Objects;

/**
 * Can be used as a blacklist/whitelist for Items. Matches to the Item's registry name.
 */
public class ItemMatchList extends AbstractMatchList<Item> {

    public ItemMatchList(boolean whitelist, boolean allowUserToChangeType, String... defaultValues) {
        super(whitelist, allowUserToChangeType, defaultValues);
    }

    public ItemMatchList(boolean whitelist, boolean allowUserToChangeType, Item... defaultValues) {
        super(whitelist, allowUserToChangeType, Arrays.stream(defaultValues).map(Item::getRegistryName)
                .filter(Objects::nonNull).map(ResourceLocation::toString).toArray(String[]::new));
    }

    @Override
    protected boolean contains(Item item) {
        ResourceLocation name = item.getRegistryName();
        return name != null && containsKey(name.toString());
    }
}
