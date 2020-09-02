# Changelog

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
