# Eternal Tool × MineColonies Compatibility (v0.1.0 prototype)

A small **standalone NeoForge 1.21.1 compatibility add-on** for the exact uploaded binaries:

- `eternaltool-1.5.0.jar` (mod ID `eternaltool`, item `eternaltool:eternal_tool`)
- `minecolonies-1.1.1420-1.21.1.jar` (mod ID `minecolonies`)

**Do not overwrite or edit either mod.** Install this *additional* jar alongside both.

## Why it exists

Eternal Tool implements NeoForge default pickaxe, axe, shovel, hoe, and sword **ItemAbilities**. MineColonies already recognizes these types by item abilities. However, Eternal Tool inherits `Item` rather than `TieredItem`, so MineColonies does not automatically assign it a conventional equipment level.

This bridge calls MineColonies' public `Compatibility.registerItemTier(Item, int)` API when the server starts. It then reads the registered tier back and logs the result.

The default equipment tier is **1 (stone)** so early workers can qualify. Set `equipmentTier` to 0–5 in the server configuration to change the eligible hut level. Tier assignment does **not** change Eternal Tool's power, mastery, durability or item data.

## Compilation via GitHub

1. Create a new GitHub repository `EternalColoniesCompat`. Do not upload the third-party JARs.
2. Upload **all contents** of this project, including `.github/workflows/build.yml`.
3. Open **Actions**. The workflow runs with Java 21 and Gradle 8.14.3.
4. Download the artifact `eternalcoloniescompat-jar` and unzip it.
5. Place the compiled jar in your NeoForge 1.21.1 `mods` folder **alongside** Eternal Tool and MineColonies, plus MineColonies' normal dependencies.
6. Start a test world/server. Check `latest.log` for `Eternal Tool registered with MineColonies: tier ...`.

## First gameplay tests

1. Back up the world or use a new test world.
2. Supply an Eternal Tool to a Knight's inventory. Check whether it is accepted/equipped and used to strike mobs. The Knight must qualify for the configured tier.
3. Supply a separate Eternal Tool to a Miner, Lumberjack, Digger, or Farmer and check each job's normal tool-use behavior.
4. Check that tools preserve their existing data and are not destroyed or duplicated by the compatibility code.
5. Test with your existing mastered Eternal Tool item, not only newly crafted ones.

## Important limitations

- **This is a targeted first compatibility test, not a verified full integration.** Both binaries were inspected, but the add-on itself has not been built against NeoForge or run in Minecraft here.
- A single Eternal Tool will qualify as multiple tool types. MineColonies job AI may still impose restrictions in particular professions; report those with logs.
- Eternal Tool's XP gain, Gathering Surge, magnet, breathing, fall and several combat bonuses have Player-specific code. Citizens **do not automatically gain player mastery XP or all player-only powers** simply by equipping it.
- The add-on does not manufacture extra Eternal Tools or distribute them automatically. Supply tools through normal colony inventories and requests.
- Archers requiring a Bow will not use Eternal Tool as a bow, because it is not a bow.
- The first version registers a fixed equipment level per item type, **not** a mastery-dependent MineColonies tier. That is intentional for consistent recognition.

## Adjusting the tier

After first run, look for `serverconfig/eternalcoloniescompat-server.toml` in the active world/server configuration directory. The exact folder depends on launcher/server setup. Stop the server before editing, set `equipmentTier = 0` to allow wood-level citizens or increase it to restrict the tool. Restart for registration to take effect.

## Next version, if needed

If citizens recognize the tool but their job AI rejects the item, we can add targeted MineColonies job integration for the specific worker rather than modifying the original JARs. If you want citizens to *earn mastery XP*, that is a separate gameplay feature and needs explicit safeguards to preserve the existing player progression system.
