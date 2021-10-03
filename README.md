![BlockEditAPI Logo](https://github.com/byteful/BlockEditAPI/blob/master/BlockEditAPI.gif)

# Why use BlockEditAPI?

BlockEditAPI is a super easy-to-use library that saves a lot of time. It already does the NMS work for you, and it
contains useful utilities that save plenty of time and reduce boilerplate code. BlockEditAPI performs really well. It
can modify up to **15 million blocks per second**!

# How does BlockEditAPI perform?

- Specifications for system: AMD Ryzen 3600X with 3200 MHZ DDR4 16 GB RAM <br>
- Specifications for MC server: PaperSpigot 1.8.8 with 4 GB of allocated RAM
- How it was tested: 200x200x200 cube was set to stone and time was calculated in ms.

### "Bukkit" Option:

Modified 50k-60k blocks per second.

### "NMS_Safe" Option:

Modified 80k-90k blocks per second.

### "NMS_Fast" Option:

Modified 1.7m-2.2m blocks per second.

### "NMS_Unsafe" Option:

Modified 12.7m-14.1m blocks per second.

# Supported Versions & Software:

- CraftBukkit > 1.8-1.17.1
- Spigot > 1.8-1.17.1
- PaperSpigot > 1.8-1.17.1 <br>

**Note: Other forks of software listed above may work. The software will need the Bukkit API present and optionally, NMS.**

# Maven/Gradle Dependency:

### Maven:

```xml
<repository>
  <id>jitpack.io</id>
  <url>https://jitpack.io</url>
</repository>
```
```xml
<dependency>
  <groupId>com.github.byteful</groupId>
  <artifactId>BlockEditAPI</artifactId>
  <version>1.0.3</version>
</dependency>
```

### Gradle

```groovy
repositories {
  maven { url 'https://jitpack.io' }
}
```
```groovy
dependencies {
  implementation 'com.github.byteful:BlockEditAPI:1.0.3'
}
```

**[ ! ] Note: Make sure you relocate BlockEditAPI to prevent any dependency collisions during runtime. [ ! ]**
