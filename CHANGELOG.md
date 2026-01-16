# Changelog

## [1.21.11-11.0.4] - 2026-01-16
- Add `LibBlockItemTagsProvider`

## [1.21.11-11.0.3] - 2026-01-04
- Ported to Minecraft 1.21.11 (NeoForge)

## [1.21.10-11.0.3] - 2025-10-18
- Ported to Minecraft 1.21.10 (NeoForge)

## [1.21.7-11.0.0] - 2025-07-04
- Ported to Minecraft 1.21.7 (NeoForge)

## [1.21.5-11.0.0] - 2025-05-27
- Ported to Minecraft 1.21.5 (NeoForge)
- Added LibModelProvider and LibWorldGenProvider (WIP)

## [1.21.1-10.5.1] - 2025-05-25
- Add DimPos#getPosLevel

## [1.21.1-10.5.0] - 2025-05-24
- Refactored DimPos and TeleportUtils
- Add CODEC and STREAM_CODEC to DimPos
- Deprecated DimensionId

## [1.21.1-10.4.0] - 2025-03-09
- Built against latest 1.21.1 NeoForge version, no significant changes

## [1.21-10.3.1] - 2024-12-31
- Fixed loot table location tooltip for `LootContainerItem`s

## [1.21-10.3.0] - 2024-09-16
- Added methods to ExtendedShapedRecipeBuilder and ExtendedShapelessRecipeBuilder that accepts ICustomIngredients

## [1.21-10.2.0] - 2024-08-04
- Added equals and hashCode to Color, so it can be used as a DataComponent
- Added StackList.from(CraftingInput)

## [1.21-10.1.0] - 2024-07-13
- Added stream codec for Color

## [1.21-10.0.0] - 2024-06-23
- Updated for Minecraft 1.21 (NeoForge)
- Removed Lazy and EnchantmentWithLevel (pending investigation of enchantment changes)

## [1.20.6-9.2.0] - 2024-06-21
- Updated for Minecraft 1.20.6 (NeoForge)
- Removed: sl_nbt command
- Removed: BlockUtils, NBTSerializer, TriConsumer, TriFunction
- Various renames and refactoring

## [9.1.2] - 2024-06-16
- Assorted internal changes and fixes

## [9.0.0] - 2024-03-31
- Updated for NeoForge and Minecraft 1.20.4

## [8.0.0] - 2023-06-13
- Updated for Minecraft 1.20.1 (Cyborgmas) [#41]

## [7.2.0] - 2023-05-16
- Updated for Minecraft 1.19.4

## [7.1.0] - 2023-03-11
- Updated for Minecraft 1.19.3
- Changed extended recipe builder method names to match vanilla ones and added RecipeCategory parameters everywhere

## [7.0.3] - 2022-09-04
- Added some additional methods to `NameUtils`

## [7.0.2] - 2022-08-30
- Bump to Forge 43.1.3 (Minecraft 1.19.2) (No code changes, should be compatible with 1.19)

## [7.0.1] - 2022-08-15
- Updated to Forge 41.1.0 (Minecraft 1.19)

## [7.0.0] - 2022-07-10
- Updated to 1.19
