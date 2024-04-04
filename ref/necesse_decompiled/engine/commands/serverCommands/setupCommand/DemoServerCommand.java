package necesse.engine.commands.serverCommands.setupCommand;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.GameTileRange;
import necesse.engine.commands.CmdParameter;
import necesse.engine.commands.CommandLog;
import necesse.engine.commands.ModularChatCommand;
import necesse.engine.commands.PermissionLevel;
import necesse.engine.commands.parameterHandlers.BoolParameterHandler;
import necesse.engine.commands.parameterHandlers.ServerClientParameterHandler;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketPlayerMovement;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.miscItem.InternalInventoryItemInterface;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.presets.AncientVultureArenaPreset;

public class DemoServerCommand extends ModularChatCommand {
   public static HashMap<String, WorldSetup> setups = new HashMap();
   public static HashMap<String, CharacterBuild> builds;
   public static ArrayList<String> combatPotions;
   public static Function<ServerClient, InventoryItem> combatPouchItemConstructor;

   public static CharacterBuild extraPotions(final String... var0) {
      return new CharacterBuild(Integer.MAX_VALUE) {
         public void apply(ServerClient var1) {
            String[] var2 = var0;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String var5 = var2[var4];
               InventoryItem var6 = new InventoryItem(var5);
               var6.setAmount(var6.itemStackSize());
               var1.playerMob.getInv().addItem(var6, true, "itempickup", (InventoryAddConsumer)null);
            }

         }
      };
   }

   public DemoServerCommand() {
      super("demo", "Setups up a world and/or build for player", PermissionLevel.ADMIN, true, new CmdParameter("player", new ServerClientParameterHandler(true, false), true, new CmdParameter[0]), new CmdParameter("setup", new WorldSetupParameterHandler(), true, new CmdParameter[]{new CmdParameter("forceNew", new BoolParameterHandler(), true, new CmdParameter[0])}), new CmdParameter("builds", new CharacterBuildsParameterHandler(), true, new CmdParameter[0]));
   }

   public void runModular(Client var1, Server var2, ServerClient var3, Object[] var4, String[] var5, CommandLog var6) {
      ServerClient var7 = (ServerClient)var4[0];
      WorldSetupEntry var8 = (WorldSetupEntry)var4[1];
      boolean var9 = (Boolean)var4[2];
      CharacterBuilds var10 = (CharacterBuilds)var4[3];
      if (var7 == null) {
         var6.add("Missing player");
      } else {
         if (var8 != null) {
            var8.setup.apply(var2, var7, var9, var6);
         }

         if (var10.builds.length > 0) {
            var10.apply(var7);
            var6.add("Applied " + GameUtils.join(var10.builds, (var0) -> {
               return var0.name;
            }, ", ", " and "));
         }

         if (var8 == null && var10.builds.length <= 0) {
            if (var5[3] != null) {
               var6.add(var5[3]);
            } else {
               var6.add("Missing either setup or build");
            }
         }

      }
   }

   static {
      setups.put("evilsprotector", new FindBiomeAndBuildArena(10, 0, new String[]{"plains", "forest"}));
      setups.put("queenspider", new FindBiomeAndBuildArena(14, -1, new String[]{"snow", "snowvillage"}));
      setups.put("voidwizard", new FindArenaWorldSetup(-100, new String[]{BiomeRegistry.DUNGEON_ISLAND.getStringID()}) {
         public Point findArenaSpawnTile(Server var1, ServerClient var2, Level var3) {
            int var4 = ObjectRegistry.getObjectID("dungeonentrance");

            for(int var5 = 0; var5 < var3.width; ++var5) {
               for(int var6 = 0; var6 < var3.height; ++var6) {
                  if (var3.getObjectID(var5, var6) == var4) {
                     return new Point(var5, var6);
                  }
               }
            }

            return null;
         }
      });
      setups.put("voidwizardarena", new FindArenaWorldSetup(-101, new String[]{BiomeRegistry.DUNGEON_ISLAND.getStringID()}) {
         public Point findArenaSpawnTile(Server var1, ServerClient var2, Level var3) {
            return new Point(var3.width / 2, var3.height / 2);
         }
      });
      setups.put("swampguardian", new FindBiomeAndBuildArena(14, -1, new String[]{"swamp"}));
      setups.put("ancientvulture", new FindAndBuildArenaCustom(36, -1, new String[]{"desert"}) {
         public void buildArena(Server var1, ServerClient var2, Level var3, int var4, int var5, int var6) {
            (new AncientVultureArenaPreset(var6, GameRandom.globalRandom)).applyToLevelCentered(var3, var4, var5 - 1);
            WorldSetup.clearBreakableObjects(var3, var4, var5, var6 / 2);
            WorldSetup.placeTorches(var3, GameRandom.globalRandom, var4, var5 - 1, var6 / 2);
         }
      });
      setups.put("piratecaptain", (var0, var1, var2, var3) -> {
         Point var4 = WorldSetup.findClosestBiome(var1, 0, var2, (Biome[])(BiomeRegistry.PIRATE_ISLAND));
         if (var4 == null) {
            var3.add("Could not find a close pirate biome");
         } else {
            var1.changeIsland(var4.x, var4.y, 0);
         }
      });
      setups.put("reaper", new FindBiomeAndBuildArena(14, -2, new String[]{"plains", "forest"}));
      setups.put("cryoqueen", new FindBiomeAndBuildArena(16, -2, new String[]{"snow", "snowvillage"}));
      setups.put("pestwarden", new FindBiomeAndBuildArena(18, -2, new String[]{"swamp"}));
      setups.put("sagegrit", (var0, var1, var2, var3) -> {
         Point var4 = WorldSetup.findClosestBiome(var1, -2, var2, (Biome[])(BiomeRegistry.DESERT, BiomeRegistry.DESERT_VILLAGE));
         if (var4 == null) {
            var3.add("Could not find a close desert biome");
         } else {
            var1.changeIsland(var4.x, var4.y, -2, (var1x) -> {
               Point var2 = null;
               int var3x = ObjectRegistry.getObjectID("templepedestal");

               for(int var4 = 0; var4 < var1x.width; ++var4) {
                  for(int var5 = 0; var5 < var1x.height; ++var5) {
                     if (var1x.getObjectID(var4, var5) == var3x) {
                        var2 = new Point(var4, var5);
                        break;
                     }
                  }

                  if (var2 != null) {
                     break;
                  }
               }

               if (var2 != null) {
                  WorldSetup.clearBreakableObjects(var1x, var2.x, var2.y, 16);
                  WorldSetup.placeTorches(var1x, GameRandom.globalRandom, var2.x, var2.y, 16);
                  WorldSetup.updateClientsLevel(var1x, var2.x, var2.y, 40);
                  return new Point(var2.x * 32 + 16, var2.y * 32 + 16 + 32);
               } else {
                  WorldSetup.buildRandomArena(var1x, GameRandom.globalRandom, var1x.width / 2, var1x.height / 2, 10, 20);
                  WorldSetup.updateClientsLevel(var1x, var1x.width / 2, var1x.height / 2, 35);
                  var3.add("Could not find temple pedestal");
                  return new Point(var1x.width / 2 * 32 + 16, var1x.height / 2 * 32 + 16);
               }
            }, true);
         }
      });
      setups.put("fallenwizard", new FindArenaWorldSetup(-200, new String[]{"desert", "desertvillage"}) {
         public Point findArenaSpawnTile(Server var1, ServerClient var2, Level var3) {
            int var4 = ObjectRegistry.getObjectID("templeentrance");

            for(int var5 = 0; var5 < var3.width; ++var5) {
               for(int var6 = 0; var6 < var3.height; ++var6) {
                  if (var3.getObjectID(var5, var6) == var4) {
                     return new Point(var5, var6);
                  }
               }
            }

            return null;
         }
      });
      setups.put("fallenwizardarena", new FindArenaWorldSetup(-201, new String[]{"desert", "desertvillage"}) {
         public Point findArenaSpawnTile(Server var1, ServerClient var2, Level var3) {
            return new Point(var3.width / 2, var3.height / 2 + 10);
         }
      });
      setups.put("arena10", (var0, var1, var2, var3) -> {
         WorldSetup.buildRandomArena(var1.getLevel(), GameRandom.globalRandom, var1.playerMob.getX() / 32, var1.playerMob.getY() / 32, 5, 15);
         WorldSetup.updateClientsLevel(var1.getLevel(), var1.playerMob.getX() / 32, var1.playerMob.getY() / 32, 30);
      });
      setups.put("arena15", (var0, var1, var2, var3) -> {
         WorldSetup.buildRandomArena(var1.getLevel(), GameRandom.globalRandom, var1.playerMob.getX() / 32, var1.playerMob.getY() / 32, 10, 20);
         WorldSetup.updateClientsLevel(var1.getLevel(), var1.playerMob.getX() / 32, var1.playerMob.getY() / 32, 35);
      });
      setups.put("arena20", (var0, var1, var2, var3) -> {
         WorldSetup.buildRandomArena(var1.getLevel(), GameRandom.globalRandom, var1.playerMob.getX() / 32, var1.playerMob.getY() / 32, 15, 25);
         WorldSetup.updateClientsLevel(var1.getLevel(), var1.playerMob.getX() / 32, var1.playerMob.getY() / 32, 40);
      });
      builds = new HashMap();
      combatPotions = new ArrayList(Arrays.asList("speedpotion", "healthregenpotion", "attackspeedpotion", "battlepotion", "resistancepotion", "thornspotion", "accuracypotion", "rapidpotion", "knockbackpotion"));
      combatPouchItemConstructor = (var0) -> {
         InventoryItem var1 = new InventoryItem("potionpouch");
         Inventory var2 = new Inventory(combatPotions.size());

         for(int var3 = 0; var3 < combatPotions.size(); ++var3) {
            InventoryItem var4 = new InventoryItem((String)combatPotions.get(var3));
            var4.setAmount(var4.itemStackSize());
            var2.setItem(var3, var4);
         }

         InternalInventoryItemInterface.setInternalInventory(var1, var2);
         return var1;
      };
      builds.put("clearinv", new CharacterBuild(-1000000) {
         public void apply(ServerClient var1) {
            var1.playerMob.getInv().clearInventories();
         }
      });
      builds.put("hp100", new CharacterBuild() {
         public void apply(ServerClient var1) {
            var1.playerMob.setMaxHealth(100);
            var1.playerMob.setHealth(var1.playerMob.getMaxHealth());
         }
      });
      builds.put("hp200", new CharacterBuild() {
         public void apply(ServerClient var1) {
            var1.playerMob.setMaxHealth(200);
            var1.playerMob.setHealth(var1.playerMob.getMaxHealth());
         }
      });
      builds.put("hp300", new CharacterBuild() {
         public void apply(ServerClient var1) {
            var1.playerMob.setMaxHealth(300);
            var1.playerMob.setHealth(var1.playerMob.getMaxHealth());
         }
      });
      builds.put("woodtools", new SimpleToolSetBuild("woodpickaxe", "woodaxe", "woodshovel"));
      builds.put("coppertools", new SimpleToolSetBuild("copperpickaxe", "copperaxe", "coppershovel"));
      builds.put("irontools", new SimpleToolSetBuild("ironpickaxe", "ironaxe", "ironshovel"));
      builds.put("frosttools", new SimpleToolSetBuild("frostpickaxe", "frostaxe", "frostshovel"));
      builds.put("demonictools", new SimpleToolSetBuild("demonicpickaxe", "demonicaxe", "demonicshovel"));
      builds.put("tungstentools", new SimpleToolSetBuild("tungstenpickaxe", "tungstenaxe", "tungstenshovel"));
      builds.put("glacialtools", new SimpleToolSetBuild("glacialpickaxe", "glacialaxe", "glacialshovel"));
      builds.put("myceliumtools", new SimpleToolSetBuild("myceliumpickaxe", "myceliumaxe", "myceliumshovel"));
      builds.put("ancientfossiltools", new SimpleToolSetBuild("ancientfossilpickaxe", "ancientfossilaxe", "ancientfossilshovel"));
      builds.put("leatherarmor", new SimpleArmorSetBuild("leatherhood", "leathershirt", "leatherboots"));
      builds.put("copperarmor", new SimpleArmorSetBuild("copperhelmet", "copperchestplate", "copperboots"));
      builds.put("ironarmor", new SimpleArmorSetBuild("ironhelmet", "ironchestplate", "ironboots"));
      builds.put("spiderarmor", new SimpleArmorSetBuild("spiderhelmet", "spiderchestplate", "spiderboots"));
      builds.put("frostarmor", new SimpleArmorSetBuild("frosthelmet", "frostchestplate", "frostboots"));
      builds.put("demonicarmor", new SimpleArmorSetBuild("demonichelmet", "demonicchestplate", "demonicboots"));
      builds.put("voidmaskarmor", new SimpleArmorSetBuild("voidmask", "voidrobe", "voidboots"));
      builds.put("voidhatarmor", new SimpleArmorSetBuild("voidhat", "voidrobe", "voidboots"));
      builds.put("ivyhelmetarmor", new SimpleArmorSetBuild("ivyhelmet", "ivychestplate", "ivyboots"));
      builds.put("ivyhoodarmor", new SimpleArmorSetBuild("ivyhood", "ivychestplate", "ivyboots"));
      builds.put("ivycircletarmor", new SimpleArmorSetBuild("ivycirclet", "ivychestplate", "ivyboots"));
      builds.put("quartzhelmetarmor", new SimpleArmorSetBuild("quartzhelmet", "quartzchestplate", "quartzboots"));
      builds.put("quartzcrownarmor", new SimpleArmorSetBuild("quartzcrown", "quartzchestplate", "quartzboots"));
      builds.put("tungstenarmor", new SimpleArmorSetBuild("tungstenhelmet", "tungstenchestplate", "tungstenboots"));
      builds.put("shadowhatarmor", new SimpleArmorSetBuild("shadowhat", "shadowmantle", "shadowboots"));
      builds.put("shadowhoodarmor", new SimpleArmorSetBuild("shadowhood", "shadowmantle", "shadowboots"));
      builds.put("ninjaarmor", new SimpleArmorSetBuild("ninjahood", "ninjarobe", "ninjashoes"));
      builds.put("glacialcircletarmor", new SimpleArmorSetBuild("glacialcirclet", "glacialchestplate", "glacialboots"));
      builds.put("glacialhelmetarmor", new SimpleArmorSetBuild("glacialhelmet", "glacialchestplate", "glacialboots"));
      builds.put("myceliumhoodarmor", new SimpleArmorSetBuild("myceliumhood", "myceliumchestplate", "myceliumboots"));
      builds.put("ancientfossilarmor", new SimpleArmorSetBuild("ancientfossilhelmet", "ancientfossilchestplate", "ancientfossilboots"));
      builds.put("leatherset", new CharacterBuildConcat(new Object[]{"woodtools", "leatherarmor"}));
      builds.put("copperset", new CharacterBuildConcat(new Object[]{"coppertools", "copperarmor"}));
      builds.put("ironset", new CharacterBuildConcat(new Object[]{"irontools", "ironarmor"}));
      builds.put("frostset", new CharacterBuildConcat(new Object[]{"frosttools", "frostarmor"}));
      builds.put("spiderset", new CharacterBuildConcat(new Object[]{"irontools", "spiderarmor"}));
      builds.put("demonicset", new CharacterBuildConcat(new Object[]{"demonictools", "demonicarmor"}));
      builds.put("voidmaskset", new CharacterBuildConcat(new Object[]{"demonictools", "voidmaskarmor"}));
      builds.put("voidhatset", new CharacterBuildConcat(new Object[]{"demonictools", "voidhatarmor"}));
      builds.put("ivyhelmetset", new CharacterBuildConcat(new Object[]{"demonictools", "ivyhelmetarmor"}));
      builds.put("ivyhoodset", new CharacterBuildConcat(new Object[]{"demonictools", "ivyhoodarmor"}));
      builds.put("ivycircletset", new CharacterBuildConcat(new Object[]{"demonictools", "ivycircletarmor"}));
      builds.put("quartzhelmetset", new CharacterBuildConcat(new Object[]{"demonictools", "quartzhelmetarmor"}));
      builds.put("quartzcrownset", new CharacterBuildConcat(new Object[]{"demonictools", "quartzcrownarmor"}));
      builds.put("tungstenset", new CharacterBuildConcat(new Object[]{"tungstentools", "tungstenarmor"}));
      builds.put("shadowhatset", new CharacterBuildConcat(new Object[]{"tungstentools", "shadowhatarmor"}));
      builds.put("shadowhoodset", new CharacterBuildConcat(new Object[]{"tungstentools", "shadowhoodarmor"}));
      builds.put("ninjaset", new CharacterBuildConcat(new Object[]{"glacialtools", "ninjaarmor"}));
      builds.put("glacialcircletset", new CharacterBuildConcat(new Object[]{"glacialtools", "glacialcircletarmor"}));
      builds.put("glacialhelmetset", new CharacterBuildConcat(new Object[]{"glacialtools", "glacialhelmetarmor"}));
      builds.put("myceliumhoodset", new CharacterBuildConcat(new Object[]{"myceliumtools", "myceliumhoodarmor"}));
      builds.put("ancientfossilset", new CharacterBuildConcat(new Object[]{"ancientfossiltools", "ancientfossilarmor"}));
      builds.put("forceofwind", new SimpleItemBuild((var0) -> {
         return new PlayerInventorySlot(var0.equipment, 1);
      }, "forceofwind"));
      builds.put("zephyrboots", new SimpleItemBuild((var0) -> {
         return new PlayerInventorySlot(var0.equipment, 1);
      }, "zephyrboots"));
      builds.put("blinkscepter", new SimpleItemBuild((var0) -> {
         return new PlayerInventorySlot(var0.equipment, 1);
      }, "blinkscepter"));
      builds.put("healthpotions", new SimpleItemBuild(18, "healthpotion", 100));
      builds.put("greaterhealthpotions", new SimpleItemBuild(18, "greaterhealthpotion", 100));
      builds.put("summonevilsprotector", new SummonBossBuild("evilsprotector", 10));
      builds.put("evilsprotectormelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp100", "generalset", "healthpotions", "zephyrboots", "ironset", new SimpleItemBuild(28, "mysteriousportal", 5), new SimpleItemBuild(29, "juniorburger", 50), new SimpleTrinketSetBuild(new String[]{"vampiresgift", "fuzzydice", "regenpendant", "leatherglove"}), new SimpleItemBuild("ironsword", 1), new SimpleItemBuild("ironspear", 1), new SimpleItemBuild("spiderboomerang", 1), new SimpleItemBuild("heavyhammer", 1)}));
      builds.put("evilsprotectorranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp100", "generalset", "healthpotions", "zephyrboots", "leatherset", new SimpleItemBuild(28, "mysteriousportal", 5), new SimpleItemBuild(29, "blueberrycake", 50), new SimpleTrinketSetBuild(new String[]{"vampiresgift", "fuzzydice", "regenpendant", "trackerboot"}), new SimpleItemBuild("ironbow", 1), new SimpleItemBuild("handgun", 1), new SimpleItemBuild("stonearrow", 1000), new SimpleItemBuild("simplebullet", 1000)}));
      builds.put("evilsprotectormagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp100", "generalset", "healthpotions", "zephyrboots", "copperset", new SimpleItemBuild(28, "mysteriousportal", 5), new SimpleItemBuild(29, "smokedfillet", 50), new SimpleTrinketSetBuild(new String[]{"vampiresgift", "fuzzydice", "regenpendant", "trackerboot"}), new SimpleItemBuild("woodstaff", 1), new SimpleItemBuild("bloodbolt", 1), new SimpleItemBuild("venomstaff", 1)}));
      builds.put("summonqueenspider", new SummonBossBuild("queenspider"));
      builds.put("queenspidermelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "demonicset", new SimpleItemBuild(28, "royalegg", 5), new SimpleItemBuild(29, "nachos", 50), new SimpleTrinketSetBuild(new String[]{"meleefoci", "vampiresgift", "regenpendant", "trackerboot"}), new SimpleItemBuild("frostsword", 1), new SimpleItemBuild("frostspear", 1), new SimpleItemBuild("frostboomerang", 1), new SimpleItemBuild("heavyhammer", 1), new SimpleItemBuild("frostglaive", 1)}));
      builds.put("queenspiderranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "frostset", new SimpleItemBuild(28, "royalegg", 5), new SimpleItemBuild(29, "juniorburger", 50), new SimpleTrinketSetBuild(new String[]{"rangefoci", "vampiresgift", "regenpendant", "leatherglove"}), new SimpleItemBuild("demonicbow", 1), new SimpleItemBuild("handgun", 1), new SimpleItemBuild("firearrow", 1000), new SimpleItemBuild("simplebullet", 1000)}));
      builds.put("queenspidermagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidhatset", new SimpleItemBuild(28, "royalegg", 5), new SimpleItemBuild(29, "meatballs", 50), new SimpleTrinketSetBuild(new String[]{"magicfoci", "vampiresgift", "regenpendant", "leatherglove"}), new SimpleItemBuild("woodstaff", 1), new SimpleItemBuild("bloodvolley", 1), new SimpleItemBuild("venomstaff", 1)}));
      builds.put("queenspidersummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "spiderset", new SimpleItemBuild(28, "royalegg", 5), new SimpleItemBuild(29, "candyapple", 50), new SimpleTrinketSetBuild(new String[]{"summonfoci", "vampiresgift", "regenpendant", "trackerboot"}), new SimpleItemBuild("spiderstaff", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("summonvoidwizard", new SummonBossBuild("voidwizard", 5));
      builds.put("voidwizardmelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "demonicset", new SimpleItemBuild(28, "voidcaller", 5), new SimpleItemBuild(29, "nachos", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "trackerboot"}), new SimpleItemBuild("demonicsword", 1), new SimpleItemBuild("voidspear", 1), new SimpleItemBuild("voidboomerang", 2), new SimpleItemBuild("lightninghammer", 1)}));
      builds.put("voidwizardranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "frostset", new SimpleItemBuild(28, "voidcaller", 5), new SimpleItemBuild(29, "fishtaco", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "leatherglove"}), new SimpleItemBuild("demonicbow", 1), new SimpleItemBuild("webbedgun", 1), new SimpleItemBuild("firearrow", 1000), new SimpleItemBuild("voidbullet", 1000)}));
      builds.put("voidwizardmagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidhatset", new SimpleItemBuild(28, "voidcaller", 5), new SimpleItemBuild(29, "smokedfillet", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "leatherglove"}), new SimpleItemBuild("voidmissile", 1), new SimpleItemBuild("bloodvolley", 1), new SimpleItemBuild("venomstaff", 1)}));
      builds.put("voidwizardsummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidmaskset", new SimpleItemBuild(28, "voidcaller", 5), new SimpleItemBuild(29, "blueberrycake", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "mesmertablet", "trackerboot"}), new SimpleItemBuild("spiderstaff", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("summonswampguardian", new SummonBossBuild("swampguardian"));
      builds.put("swampguardianmelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhelmetset", new SimpleItemBuild(28, "spikedfossil", 5), new SimpleItemBuild(29, "freshpotatosalad", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "frozenheart", "trackerboot"}), new SimpleItemBuild("ivysword", 1), new SimpleItemBuild("voidspear", 1), new SimpleItemBuild("voidboomerang", 2), new SimpleItemBuild("lightninghammer", 1)}));
      builds.put("swampguardianranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhoodset", new SimpleItemBuild(28, "spikedfossil", 5), new SimpleItemBuild(29, "eggplantparmesan", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "luckycape", "frozenwave"}), new SimpleItemBuild("ivybow", 1), new SimpleItemBuild("webbedgun", 1), new SimpleItemBuild("ironarrow", 1000), new SimpleItemBuild("voidbullet", 1000)}));
      builds.put("swampguardianmagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidhatset", new SimpleItemBuild(28, "spikedfossil", 5), new SimpleItemBuild(29, "juniorburger", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "noblehorseshoe", "trackerboot"}), new SimpleItemBuild("voidstaff", 1), new SimpleItemBuild("swamptome", 1), new SimpleItemBuild("voidmissile", 1), new SimpleItemBuild("venomstaff", 1)}));
      builds.put("swampguardiansummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidmaskset", new SimpleItemBuild(28, "spikedfossil", 5), new SimpleItemBuild(29, "fishandchips", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "mesmertablet", "regenpendant", "luckycape"}), new SimpleItemBuild("spiderstaff", 1), new SimpleItemBuild("slimecanister", 1), new SimpleItemBuild("bannerofsummonspeed", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("summonancientvulture", new SummonBossBuild("ancientvulture"));
      builds.put("ancientvulturemelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhelmetset", new SimpleItemBuild(28, "ancientstatue", 5), new SimpleItemBuild(29, "freshpotatosalad", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "frozenheart", "luckycape"}), new SimpleItemBuild("ivysword", 1), new SimpleItemBuild("ivyspear", 1), new SimpleItemBuild("quartzglaive", 1), new SimpleItemBuild("razorbladeboomerang", 1), new SimpleItemBuild("voidboomerang", 2)}));
      builds.put("ancientvultureranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhoodset", new SimpleItemBuild(28, "ancientstatue", 5), new SimpleItemBuild(29, "freshpotatosalad", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "noblehorseshoe", "trackerboot"}), new SimpleItemBuild("ivybow", 1), new SimpleItemBuild("shotgun", 1), new SimpleItemBuild("firearrow", 1000), new SimpleItemBuild("voidbullet", 1000)}));
      builds.put("ancientvulturemagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "quartzhelmetset", new SimpleItemBuild(28, "ancientstatue", 5), new SimpleItemBuild(29, "ricepudding", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "luckycape", "frozenwave"}), new SimpleItemBuild("quartzstaff", 1), new SimpleItemBuild("voidstaff", 1), new SimpleItemBuild("swamptome", 1), new SimpleItemBuild("dredgingstaff", 1)}));
      builds.put("ancientvulturesummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "quartzcrownset", new SimpleItemBuild(28, "ancientstatue", 5), new SimpleItemBuild(29, "fishandchips", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "mesmertablet", "regenpendant", "luckycape"}), new SimpleItemBuild("spiderstaff", 1), new SimpleItemBuild("slimecanister", 1), new SimpleItemBuild("bannerofsummonspeed", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("summonpiratecaptain", new SummonBossBuild("piratecaptain", 8));
      builds.put("piratecaptainmelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhelmetset", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "frozenheart", "trackerboot"}), new SimpleItemBuild("cutlass", 1), new SimpleItemBuild("vulturestalon", 1), new SimpleItemBuild("razorbladeboomerang", 1), new SimpleItemBuild("icejavelin", 250)}));
      builds.put("piratecaptainranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "zephyrboots", "ivyhoodset", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "noblehorseshoe", "frozenwave"}), new SimpleItemBuild("vulturesburst", 1), new SimpleItemBuild("machinegun", 1), new SimpleItemBuild("firearrow", 1000), new SimpleItemBuild("voidbullet", 1000)}));
      builds.put("piratecaptainmagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "quartzhelmetset", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "regenpendant", "luckycape", "trackerboot"}), new SimpleItemBuild("quartzstaff", 1), new SimpleItemBuild("voidstaff", 1), new SimpleItemBuild("dredgingstaff", 1)}));
      builds.put("piratecaptainsummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp200", "generalset", "healthpotions", "forceofwind", "voidmaskset", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "mesmertablet", "regenpendant", "luckycape"}), new SimpleItemBuild("vulturestaff", 1), new SimpleItemBuild("slimecanister", 1), new SimpleItemBuild("bannerofsummonspeed", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("summonreaper", new SummonBossBuild("reaper"));
      builds.put("reapermelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "healthpotions", extraPotions("strengthpotion"), "zephyrboots", "tungstenset", "tungstentools", new SimpleItemBuild(28, "shadowgate", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "bonehilt", "frozensoul", "lifependant"}), new SimpleItemBuild("tungstensword", 1), new SimpleItemBuild("tungstenspear", 1), new SimpleItemBuild("tungstenboomerang", 4), new SimpleItemBuild("icejavelin", 250)}));
      builds.put("reaperranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "healthpotions", extraPotions("rangerpotion"), "zephyrboots", "shadowhoodset", "tungstentools", new SimpleItemBuild(28, "shadowgate", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "bonehilt", "noblehorseshoe", "lifependant"}), new SimpleItemBuild("glacialbow", 1), new SimpleItemBuild("flintlock", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)}));
      builds.put("reapermagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "healthpotions", extraPotions("wisdompotion"), "forceofwind", "shadowhatset", "tungstentools", new SimpleItemBuild(28, "shadowgate", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "airvessel", "lifependant", "luckycape", "frozenwave"}), new SimpleItemBuild("shadowbolt", 1), new SimpleItemBuild("quartzstaff", 1), new SimpleItemBuild("genielamp", 1), new SimpleItemBuild("elderlywand", 1)}));
      builds.put("reapersummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "healthpotions", extraPotions("minionpotion"), "forceofwind", "voidmaskset", "tungstentools", new SimpleItemBuild(28, "shadowgate", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfoci", "vampiresgift", "hysteriatablet", "lifependant", "airvessel"}), new SimpleItemBuild("vulturestaff", 1), new SimpleItemBuild("slimecanister", 1), new SimpleItemBuild("bannerofsummonspeed", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("summoncryoqueen", new SummonBossBuild("cryoqueen"));
      builds.put("cryoqueenmelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("strengthpotion"), "zephyrboots", "glacialhelmetset", "glacialtools", new SimpleItemBuild(28, "icecrown", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "bonehilt", "frozensoul", "lifependant"}), new SimpleItemBuild("tungstensword", 1), new SimpleItemBuild("tungstenspear", 1), new SimpleItemBuild("tungstenboomerang", 4), new SimpleItemBuild("glacialboomerang", 2)}));
      builds.put("cryoqueenranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("rangerpotion"), "zephyrboots", "shadowhoodset", "glacialtools", new SimpleItemBuild(28, "icecrown", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "bonehilt", "noblehorseshoe", "trackerboot"}), new SimpleItemBuild("glacialbow", 1), new SimpleItemBuild("flintlock", 1), new SimpleItemBuild("deathripper", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)}));
      builds.put("cryoqueenmagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("wisdompotion"), "forceofwind", "shadowhatset", "glacialtools", new SimpleItemBuild(28, "icecrown", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "lifependant", "luckycape", "airvessel"}), new SimpleItemBuild("shadowbeam", 1), new SimpleItemBuild("shadowbolt", 1), new SimpleItemBuild("iciclestaff", 1), new SimpleItemBuild("elderlywand", 1)}));
      builds.put("cryoqueensummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("minionpotion"), "forceofwind", "glacialcircletset", "glacialtools", new SimpleItemBuild(28, "icecrown", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "hysteriatablet", "lifependant", "airvessel"}), new SimpleItemBuild("reaperscall", 1), new SimpleItemBuild("cryostaff", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("summonpestwarden", new SummonBossBuild("pestwarden"));
      builds.put("pestwardenmelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("strengthpotion"), "zephyrboots", "glacialhelmetset", "myceliumtools", new SimpleItemBuild(28, "decayingleaf", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "bonehilt", "frozensoul", "lifependant"}), new SimpleItemBuild("cryoglaive", 1), new SimpleItemBuild("reaperscythe", 1), new SimpleItemBuild("tungstensword", 1), new SimpleItemBuild("tungstenspear", 1), new SimpleItemBuild("tungstenboomerang", 4), new SimpleItemBuild("glacialboomerang", 2)}));
      builds.put("pestwardenranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("rangerpotion"), "zephyrboots", "myceliumhoodset", "myceliumtools", new SimpleItemBuild(28, "decayingleaf", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "bonehilt", "noblehorseshoe", "trackerboot"}), new SimpleItemBuild("druidsgreatbow", 1), new SimpleItemBuild("glacialbow", 1), new SimpleItemBuild("cryoblaster", 1), new SimpleItemBuild("deathripper", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)}));
      builds.put("pestwardenmagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("wisdompotion"), "forceofwind", "shadowhatset", "myceliumtools", new SimpleItemBuild(28, "decayingleaf", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "lifependant", "luckycape", "airvessel"}), new SimpleItemBuild("cryoquake", 1), new SimpleItemBuild("shadowbeam", 1), new SimpleItemBuild("shadowbolt", 1), new SimpleItemBuild("swampdwellerstaff", 1), new SimpleItemBuild("iciclestaff", 1), new SimpleItemBuild("elderlywand", 1)}));
      builds.put("pestwardensummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("minionpotion"), "forceofwind", "glacialcircletset", "myceliumtools", new SimpleItemBuild(28, "decayingleaf", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "hysteriatablet", "lifependant", "airvessel"}), new SimpleItemBuild("reaperscall", 1), new SimpleItemBuild("cryostaff", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("summonsageandgrit", new SummonBossBuild("sageandgrit"));
      builds.put("sagegritmelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("strengthpotion"), "zephyrboots", "ancientfossilset", "ancientfossiltools", new SimpleItemBuild(28, "dragonsouls", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "bonehilt", "frozensoul", "lifependant"}), new SimpleItemBuild("venomslasher", 1), new SimpleItemBuild("tungstenspear", 1), new SimpleItemBuild("reaperscythe", 1), new SimpleItemBuild("cryoglaive", 1), new SimpleItemBuild("tungstensword", 1), new SimpleItemBuild("glacialboomerang", 1)}));
      builds.put("sagegritranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("rangerpotion"), "zephyrboots", "myceliumhoodset", "ancientfossiltools", new SimpleItemBuild(28, "dragonsouls", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "bonehilt", "noblehorseshoe", "lifependant"}), new SimpleItemBuild("glacialbow", 1), new SimpleItemBuild("livingshotty", 1), new SimpleItemBuild("antiquerifle", 1), new SimpleItemBuild("deathripper", 1), new SimpleItemBuild("druidsgreatbow", 1), new SimpleItemBuild("cryoblaster", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)}));
      builds.put("sagegritmagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("wisdompotion"), "forceofwind", "shadowhatset", "ancientfossiltools", new SimpleItemBuild(28, "dragonsouls", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "bonehilt", "luckycape", "airvessel"}), new SimpleItemBuild("cryoquake", 1), new SimpleItemBuild("venomshower", 1), new SimpleItemBuild("shadowbeam", 1), new SimpleItemBuild("ancientdredgingstaff", 1), new SimpleItemBuild("swampdwellerstaff", 1), new SimpleItemBuild("shadowbolt", 1)}));
      builds.put("sagegritsummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("minionpotion"), "forceofwind", "glacialcircletset", "ancientfossiltools", new SimpleItemBuild(28, "dragonsouls", 5), new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "hysteriatablet", "scryingmirror", "airvessel"}), new SimpleItemBuild("reaperscall", 1), new SimpleItemBuild("cryostaff", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("summonfallenwizard", new SummonBossBuild("fallenwizard", 5));
      builds.put("fallenwizardmelee", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("strengthpotion"), "blinkscepter", "ancientfossilset", "ancientfossiltools", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "ancientrelics", "frozensoul", "lifependant"}), new SimpleItemBuild("antiquesword", 1), new SimpleItemBuild("dragonsrebound", 1), new SimpleItemBuild("venomslasher", 1), new SimpleItemBuild("reaperscythe", 1), new SimpleItemBuild("cryospear", 1), new SimpleItemBuild("cryoglaive", 1), new SimpleItemBuild("glacialboomerang", 1)}));
      builds.put("fallenwizardranged", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("rangerpotion"), "blinkscepter", "shadowhoodset", "ancientfossiltools", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "bonehilt", "luckycape", "ancientrelics"}), new SimpleItemBuild("antiquebow", 1), new SimpleItemBuild("antiquerifle", 1), new SimpleItemBuild("livingshotty", 1), new SimpleItemBuild("deathripper", 1), new SimpleItemBuild("bowofdualism", 1), new SimpleItemBuild("cryoblaster", 1), new SimpleItemBuild("bonearrow", 1000), new SimpleItemBuild("voidbullet", 1000)}));
      builds.put("fallenwizardmagic", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("wisdompotion"), "blinkscepter", "shadowhatset", "ancientfossiltools", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "bonehilt", "luckycape", "ancientrelics"}), new SimpleItemBuild("dragonlance", 1), new SimpleItemBuild("cryoquake", 1), new SimpleItemBuild("venomshower", 1), new SimpleItemBuild("shadowbeam", 1), new SimpleItemBuild("ancientdredgingstaff", 1), new SimpleItemBuild("swampdwellerstaff", 1), new SimpleItemBuild("shadowbolt", 1)}));
      builds.put("fallenwizardsummon", new CharacterBuildConcat(new Object[]{"clearinv", "hp300", "generalset", "greaterhealthpotions", extraPotions("minionpotion"), "blinkscepter", "glacialcircletset", "ancientfossiltools", new SimpleItemBuild(29, "sushirolls", 50), new SimpleTrinketSetBuild(new String[]{"balancedfrostfirefoci", "spikedbatboots", "hysteriatablet", "scryingmirror", "ancientrelics"}), new SimpleItemBuild("reaperscall", 1), new SimpleItemBuild("skeletonstaff", 1), new SimpleItemBuild("cryostaff", 1), new SimpleItemBuild("bannerofspeed", 1)}));
      builds.put("combatpouch", new SimpleItemBuild(combatPouchItemConstructor));
      builds.put("generalset", new CharacterBuildConcat(new Object[]{new SimpleItemBuild(19, combatPouchItemConstructor), new SimpleItemBuild(49, "torch", 100), new SimpleItemBuild(39, "recallscroll", 50), new SimpleItemBuild(38, "travelscroll", 50)}));
      builds.put("timeday", new RunCommandBuild("time day"));
      builds.put("timedawn", new RunCommandBuild("time dawn"));
      builds.put("timemorning", new RunCommandBuild("time morning"));
      builds.put("timenoon", new RunCommandBuild("time noon"));
      builds.put("timemidday", new RunCommandBuild("time midday"));
      builds.put("timedusk", new RunCommandBuild("time dusk"));
      builds.put("timenight", new RunCommandBuild("time night"));
      builds.put("timemidnight", new RunCommandBuild("time midnight"));
      builds.put("teleportothers", new CharacterBuild() {
         public void apply(ServerClient var1) {
            ArrayList var2 = null;
            Iterator var3 = var1.getServer().getClients().iterator();

            while(true) {
               ServerClient var4;
               do {
                  if (!var3.hasNext()) {
                     return;
                  }

                  var4 = (ServerClient)var3.next();
               } while(var1 == var4);

               if (var2 == null) {
                  var2 = new ArrayList();
                  byte var5 = 4;
                  Iterator var6 = (new GameTileRange(var5, new Point[0])).getValidTiles(var1.playerMob.getTileX(), var1.playerMob.getTileY()).iterator();

                  while(var6.hasNext()) {
                     Point var7 = (Point)var6.next();
                     int var8 = var7.x * 32 + 16;
                     int var9 = var7.y * 32 + 16;
                     if (!var1.playerMob.collidesWith(var1.getLevel(), var8, var9)) {
                        var2.add(new Point(var8, var9));
                     }
                  }
               }

               Point var10;
               if (var2.isEmpty()) {
                  var10 = new Point(var1.playerMob.getX(), var1.playerMob.getY());
               } else {
                  var10 = (Point)var2.remove(GameRandom.globalRandom.nextInt(var2.size()));
               }

               if (var4.isSamePlace(var1)) {
                  var4.playerMob.setPos((float)var10.x, (float)var10.y, true);
                  var1.getServer().network.sendToClientsAt(new PacketPlayerMovement(var4, true), (ServerClient)var4);
               } else {
                  var4.changeIsland(var1.getLevelIdentifier(), (var1x) -> {
                     return var10;
                  }, true);
               }
            }
         }
      });
   }
}
