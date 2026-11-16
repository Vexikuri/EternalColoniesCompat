# Build status

- **Source created and structurally checked.** Java not compiled against the live NeoForge 21.1.235 development API in this environment.
- MineColonies 1.1.1420-1.21.1 and Eternal Tool 1.5.0 JARs were inspected using the Java class signature/disassembly utilities (`javap`) and resource metadata.
- Critical integration target confirmed: `com.minecolonies.api.compatibility.Compatibility.registerItemTier(Item, int)`.
- Other mods' original JARs are not included or changed.
- Build via the provided GitHub Actions workflow and test on a **backup/test** Minecraft world.
