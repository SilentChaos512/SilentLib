# SilentLib
A common library for my Minecraft mods to use, to make updating and creating new mods easier and reducing code duplication.

## Links and Downloads

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/silent-lib) (downloads and more information)
- [GitHub repository](https://github.com/SilentChaos512/SilentLib) (source code)
- [Issue Tracker on GitHub](https://github.com/SilentChaos512/SilentLib/issues) (bug reports and feature requests)
- [Discord Server](https://discord.gg/Adyk9zHnUn) (easiest way to get quick questions answered, do not use to report bugs)

### Note on Downloads

**I only upload builds to Minecraft CurseForge.** If you downloaded the mod from somewhere other than Curse/CurseForge or the Twitch launcher (or as part of a modpack in some cases), I cannot make any guarantees about the file or its contents, as it was uploaded without my permission.

## Making an Add-on

I currently upload to Bintray and will switch to GitHub Packages sometime soon.

### Bintray Instructions

If you want to use Silent Lib in your mod, add Silent Lib and silent-utils to your dependencies:

```groovy
repositories {
    maven {
        url "https://dl.bintray.com/silentchaos512/silent-lib"
    }
    maven {
        url "https://dl.bintray.com/silentchaos512/silent-utils"
    }
}

dependencies {
    compile("net.silentchaos512:silent-lib-{mc-version}:{version}")
    compile("net.silentchaos512:silent-utils:{version}")
}
```

