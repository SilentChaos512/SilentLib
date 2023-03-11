# Changelog

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

## [6.2.0] - 2022-03-22
- Updated to 1.18.2 (Cyborgmas) [#34]
- Complete rewrite of `TagUtils` class (breaks compatibility)

## [6.1.0] - 2022-02-24
### Fixed
- Some block entities not saving their inventories [[Silent Gear #487]](https://github.com/SilentChaos512/Silent-Gear/issues/487)

## [6.0.0] - 2021-11-30
- Updated to 1.18 (Cyborgmas) [#33]

## [5.0.0] - 2021-07-24
- Updated to 1.17.1 (Cyborgmas) [#31]

## [4.10.0] - 2021-07-12
### Changed
- Updated source code to use official mappings. This has no impact on a normal Minecraft instance, it's only for developers and 1.17 prep.

## [4.9.6] - 2021-03-11
- Also contains changes from unreleased 4.9.5 build
### Added
- LibRecipeProvider registerCustomRecipe methods (for SpecialRecipes)

## [4.9.5] - 2021-03-10
- Not released on Curse. Bumped version when diagnosing GitHub Packages errors.
### Changed
- More methods in TagUtils, deprecate `isInSafe` to rename for consistency

## [4.9.4] - 2021-03-08
### Added
- ExclusionIngredient (moving to Lib from Silent Gear with some small changes)

## [4.9.3] - 2021-03-04
### Added
- ExtendedSingleItemRecipe class and data builder
- TagUtils class, with a copy of the "tag not bound" workaround hack

## [4.9.2] - 2021-02-27
### Added
- LibRecipeProvider class (reduces code duplication for data generators)

## [4.9.1] - 2021-01-11
- Attempt to fix an occasional crash when registering compostable items [Silent Gear #307](https://github.com/SilentChaos512/Silent-Gear/issues/307)
- Update zh_cn.json (XuyuEre) [#27]

## [4.9.0] - 2020-11-06
- The `sl_tp` command can now correctly teleport to other dimensions (no safety checks, use with caution)
- Add new TeleportUtils class (for Silent's Gems teleporter fix)
- Move Silent's Gems' DimensionFilterPlacement into Lib

## [4.8.1] - 2020-09-29
- Compatible with 1.16.2 and 1.16.3
- Update to 20200916-1.16.2 mappings

## [4.8.0] - 2020-09-02
- Ported to Minecraft 1.16.2 (Cyborgmas) [#26]

## [4.7.1] - 2020-09-02
- Partially fix teleporting... still doesn't quite work right across dimensions

## [4.6.6] - 2020-07-13
### Added
- GenericIntTrigger and UseItemTrigger: static factory methods and override serialize

## [4.6.5] - 2020-07-10
### Added
- Extended recipe builders (used by data generators)
- More methods to RegistryObjectWrapper to make it easier to use

## [4.6.4] - 2020-06-23
### Changed
- Add `BlockRegistryObject` and `ItemRegistryObject`. These are wrappers for Forge's `RegistryObject`s which provide some additional interfaces for convenience.
- Rename a couple conflicting methods in `NameUtils` (hope I wasn't using those...)

## [4.6.3] - 2020-06-13
### Fixed
- `InventoryUtils.canItemsStack`

## [4.6.2] - 2020-06-10
### Added
- `DamageItemRecipe`
- `EntityHelper.spawnWithClientPacket`

## [4.6.1] - 2020-05-02
### Fixed
- Error in log when typing just `/sl_nbt` with no further arguments [#22]
- Export to JSON button covering first entry in `/sl_nbt` command [#21]

## [4.5.0] - 2019-01-08
- Updated to Minecraft 1.15.1

## [4.4.0] - 2019-09-12
- Fix Entity.getPersistentData name in Forge 28.1.0

## [4.3.3] - 2019-09-12
- Fix crash caused by javax reference in SyncVariable

## [4.3.1] - 2019-08-06
- Update for Forge 28.0.45

## [4.3.0] - 2019-07-22
- Update to 1.14.4

## [4.2.3] - 2019-07-14
- Added `sl_nbt` command. Can target a block, entity, or item. Displays the object's NBT in an easy to read format. You can also export the NBT to a JSON file.

## [4.2.0]
- Port to 1.14.3
- Update silent-utils to 1.0.7

## [4.1.0]
- Port to 1.14.2

## [4.0.10]
- Add zh_cn translation (XuyuEre)
- Implement ILeftClickItem
- Improved teleporter code

## [4.0.8] - 2019-04-20
- Updated Night Config for compatibility with Forge 149 (updates silent-utils to 1.0.5)
- Added WorldUtils

## [4.0.7] - 2019-03-31
- May fix Silent Gear giving blueprint packages on each login

## [4.0.4] - 2019-03-06
- Disable tag file generator, which was not supposed to work outside of dev. You can delete the `.minecraft/output` folder it created. There are some other things which could also create this folder, but I believe they have all been fixed.

## [4.0.3] - 2019-03-02
- Re-enable mod advancement triggers

## [4.0.2] - 2019-03-01
- Fixes a silent-utils bug by updating to 1.0.4

## [4.0.1] - 2019-02-28
- Fixes Silent Gear [issue #22](https://github.com/SilentChaos512/Silent-Gear/issues/22)
