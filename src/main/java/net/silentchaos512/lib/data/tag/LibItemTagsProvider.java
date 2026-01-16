package net.silentchaos512.lib.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.Objects;
import java.util.concurrent.CompletableFuture;

public abstract class LibItemTagsProvider extends ItemTagsProvider {
    public LibItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    protected static class BlockToItemConverter implements TagAppender<Block, Block> {
        private final TagAppender<Item, Item> itemAppender;

        public BlockToItemConverter(TagAppender<Item, Item> itemAppender) {
            this.itemAppender = itemAppender;
        }

        public TagAppender<Block, Block> add(Block p_422488_) {
            this.itemAppender.add(Objects.requireNonNull(p_422488_.asItem()));
            return this;
        }

        public TagAppender<Block, Block> addOptional(Block p_422522_) {
            this.itemAppender.addOptional(Objects.requireNonNull(p_422522_.asItem()));
            return this;
        }

        private static TagKey<Item> blockTagToItemTag(TagKey<Block> tag) {
            return TagKey.create(Registries.ITEM, tag.location());
        }

        @Override
        public TagAppender<Block, Block> addTag(TagKey<Block> p_422282_) {
            this.itemAppender.addTag(blockTagToItemTag(p_422282_));
            return this;
        }

        @Override
        public TagAppender<Block, Block> addOptionalTag(TagKey<Block> p_422609_) {
            this.itemAppender.addOptionalTag(blockTagToItemTag(p_422609_));
            return this;
        }

        @Override
        public TagAppender<Block, Block> add(net.minecraft.tags.TagEntry entry) {
            itemAppender.add(entry);
            return this;
        }

        @Override
        public TagAppender<Block, Block> replace(boolean value) {
            itemAppender.replace(value);
            return this;
        }

        @Override
        public TagAppender<Block, Block> remove(Block block) {
            itemAppender.remove(block.asItem());
            return this;
        }

        @Override
        public TagAppender<Block, Block> remove(TagKey<Block> tag) {
            itemAppender.remove(blockTagToItemTag(tag));
            return this;
        }
    }
}
