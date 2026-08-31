package net.silentchaos512.lib.data.tag;

import net.minecraft.data.tags.BlockItemTagsProvider;
import net.minecraft.tags.BlockItemTagId;

import java.util.function.Function;

public abstract class LibBlockItemTagsProvider extends BlockItemTagsProvider {
    protected LibBlockItemTagsProvider(Function<BlockItemTagId, CombinedAppender> tagSupplier) {
        super(tagSupplier);
    }

    @Override
    public abstract void run();
}
