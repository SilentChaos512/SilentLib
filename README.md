# SilentLib
A common library for my Minecraft mods to use, to make updating and creating new mods easier and reducing code duplication.

## Downloads

- [CurseForge](https://www.curseforge.com/minecraft/mc-mods/silent-lib)
- [Modrinth](https://modrinth.com/mod/silent-lib)

If you downloaded the mod from somewhere other than Curseforge or Modrinth (or as part of a modpack in some cases), I cannot make any guarantees about the file or its contents, as it may have been uploaded without my permission.

## Other Links

- [GitHub repository](https://github.com/SilentChaos512/SilentLib) (source code)
- [Issue Tracker on GitHub](https://github.com/SilentChaos512/SilentLib/issues) (bug reports and feature requests)
- [Discord Server](https://discord.gg/Adyk9zHnUn) (easiest way to get quick questions answered, do not use to report bugs)

## Developers

To use Silent Lib in your project, add the following to your `build.gradle`.

You alse need to generate a GitHub token and add it along with your GitHub username to your personal `gradle.properties` file in `C:\Users\YOUR_USERNAME\.gradle` or `~/.gradle/gradle.properties`. This file may not exist, and you would have to create it yourself.

GitHub tokens can be generated [here](https://github.com/settings/tokens). Click _Generate New Token_ and click the checkmark for _read:packages_

Example of `gradle.properties` file in `C:\Users\YOUR_USERNAME\.gradle` or `~/.gradle/gradle.properties`

```gradle
//Your GitHub username
gpr.username=SilentChaos512

// Your GitHub generated token (bunch of hex digits) with read permission
gpr.token=paste_your_token_here
```

-----------------------------------

Code to add to `build.gradle`

```gradle
repositories {
    maven {
        url = uri("https://maven.pkg.github.com/silentchaos512/silentlib")
        credentials {
            username = property('gpr.username')
            password = property('gpr.token')
        }
    }
    maven {
        url = uri("https://maven.pkg.github.com/silentchaos512/silent-utils")
        credentials {
            username = property('gpr.username')
            password = property('gpr.token')
        }
    }
}
```

```gradle
dependencies {
    // Replace VERSION with the version you need, in the form of "MC_VERSION-MOD_VERSION"
    // Example: compile fg.deobf("net.silentchaos512:silentlib:1.16.3-4.+")
    // Available builds can be found here: https://github.com/SilentChaos512/silentlib/packages
    compile fg.deobf("net.silentchaos512:silent-lib:VERSION") {
        exclude module: "forge"
    }
}
```
