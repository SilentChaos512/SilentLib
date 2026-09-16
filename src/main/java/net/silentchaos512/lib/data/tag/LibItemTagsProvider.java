package net.silentchaos512.lib.data.tag;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagAppender;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.neoforged.neoforge.common.data.ItemTagsProvider;

import java.util.concurrent.CompletableFuture;

public abstract class LibItemTagsProvider extends ItemTagsProvider {
    public LibItemTagsProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, String modId) {
        super(output, lookupProvider, modId);
    }

    protected static class BlockToItemConverter implements TagAppender<Block> {
        private final TagAppender<Item> itemAppender;

        public BlockToItemConverter(TagAppender<Item> itemAppender) {
            this.itemAppender = itemAppender;
        }

        public BlockToItemConverter add(Block block) {
            this.itemAppender.add(BuiltInRegistries.ITEM.getResourceKey(block.asItem()).orElseThrow());
            return this;
        }

        public BlockToItemConverter addOptional(Block block) {
            this.itemAppender.addOptional(BuiltInRegistries.ITEM.getResourceKey(block.asItem()).orElseThrow());
            return this;
        }

        private static TagKey<Item> blockTagToItemTag(TagKey<Block> tag) {
            return TagKey.create(Registries.ITEM, tag.location());
        }

        @Override
        public TagAppender<Block> add(ResourceKey<Block> block) {
            this.itemAppender.add(ResourceKey.create(Registries.ITEM, block.identifier()));
            return this;
        }

        @Override
        public TagAppender<Block> addOptional(ResourceKey<Block> block) {
            this.itemAppender.addOptional(ResourceKey.create(Registries.ITEM, block.identifier()));
            return this;
        }

        @Override
        public TagAppender<Block> addTag(TagKey<Block> tag) {
            this.itemAppender.addTag(blockTagToItemTag(tag));
            return this;
        }

        @Override
        public TagAppender<Block> addOptionalTag(TagKey<Block> tag) {
            this.itemAppender.addOptionalTag(blockTagToItemTag(tag));
            return this;
        }

        @Override
        public TagAppender<Block> add(net.minecraft.tags.TagEntry entry) {
            itemAppender.add(entry);
            return this;
        }

        @Override
        public TagAppender<Block> replace(boolean value) {
            itemAppender.replace(value);
            return this;
        }

        @Override
        public TagAppender<Block> remove(ResourceKey<Block> block) {
            itemAppender.remove(ResourceKey.create(Registries.ITEM, block.identifier()));
            return this;
        }

        @Override
        public TagAppender<Block> remove(TagKey<Block> tag) {
            itemAppender.remove(blockTagToItemTag(tag));
            return this;
        }
    }
}
