rem Workaround for maven downloader fatjar lockup.
rem Not sure what task to run, but runClient works.
gradlew runClient --no-daemon --refresh-dependencies
