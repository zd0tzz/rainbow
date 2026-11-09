# Trippy Rainbow (Fabric, Minecraft 26.3)

Client-side Fabric mod that washes the whole game view in an animated, psychedelic rainbow:
a rippling hue field, scrolling rainbow bars top and bottom, and pulsing hue-cycling side borders.

## Requirements
- Minecraft 26.3
- Fabric Loader 0.19.5+
- Fabric API 0.161.0+26.3 (or newer for 26.3)
- Java 25

## Get the jar from GitHub
1. Create a new empty repo on GitHub and push this folder to it.
2. The **Build mod** workflow runs automatically. Open the **Actions** tab, click the latest run, and download the `trippy-rainbow-mod` artifact.
3. Unzip it and put `trippyrainbow-1.0.0.jar` (not the `-sources` one) in your `mods` folder.

To make a proper Release with the jar attached, push a tag: `git tag v1.0.0 && git push origin v1.0.0`.

## Tweaking
Edit the constants at the top of `src/client/java/com/trippyrainbow/client/TrippyRainbowClient.java`
(`BASE_ALPHA`, `PULSE_ALPHA`, `SPEED`, `CELL`, `BAR`).

## Photosensitivity
This mod produces constantly shifting, high-saturation color. If you are sensitive to flashing or
moving patterns, lower `SPEED` and `BASE_ALPHA` or skip it.

## Local build
Install JDK 25 and Gradle 9.6.0, then run `gradle build`. The jar lands in `build/libs/`.
(Run `gradle wrapper` once if you want a `gradlew` script.)
