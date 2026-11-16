# GitHub patch instructions — v0.1.0 → v0.2.0

Apply these project-relative files from the patch ZIP to the same paths in **EternalColoniesCompat** repository. Keep your existing Minecraft mod JARs unchanged.

REPLACE:
- `gradle.properties`
- `src/main/java/dev/foreverworld/eternalcoloniescompat/CompatConfig.java`
- `src/main/java/dev/foreverworld/eternalcoloniescompat/EternalColoniesCompat.java`
- `src/main/resources/META-INF/neoforge.mods.toml`

ADD:
- `src/main/java/dev/foreverworld/eternalcoloniescompat/WorkerAccess.java`
- `src/main/java/dev/foreverworld/eternalcoloniescompat/NpcMastery.java`
- `src/main/java/dev/foreverworld/eternalcoloniescompat/CitizenCombatXp.java`
- `src/main/java/dev/foreverworld/eternalcoloniescompat/mixin/WorkerMiningMixin.java`
- `src/main/resources/eternalcoloniescompat.mixins.json`

Commit all files together and check Actions. No edits to Eternal Tool or MineColonies. This release is an unverified prototype; test it separately.
