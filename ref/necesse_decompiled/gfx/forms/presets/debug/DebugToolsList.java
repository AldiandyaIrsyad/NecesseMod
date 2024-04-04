package necesse.gfx.forms.presets.debug;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import necesse.engine.GameAuth;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.expeditions.SettlerExpedition;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.postProcessing.PostProcessGaussBlur;
import necesse.engine.quest.KillMobsQuest;
import necesse.engine.quest.KillMobsTitleQuest;
import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ExpeditionMissionRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.SettlerRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.engine.steam.SteamData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.engine.util.ObjectValue;
import necesse.engine.world.WorldEntity;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.settlementRaidEvent.SettlementRaidLevelEvent;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.humanShop.ExplorerHumanMob;
import necesse.entity.particle.Particle;
import necesse.entity.particle.ParticleOption;
import necesse.entity.particle.RandomSpinningLightParticle;
import necesse.entity.particle.fireworks.FireworksExplosion;
import necesse.entity.particle.fireworks.FireworksPath;
import necesse.entity.particle.fireworks.FireworksRocketParticle;
import necesse.gfx.GameResources;
import necesse.gfx.forms.FormManager;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.floatMenu.FloatMenu;
import necesse.gfx.forms.floatMenu.SelectionFloatMenu;
import necesse.gfx.forms.presets.debug.tools.BezierCurveTestGameTool;
import necesse.gfx.forms.presets.debug.tools.CastRayGameTool;
import necesse.gfx.forms.presets.debug.tools.ChaikinSmoothTestGameTool;
import necesse.gfx.forms.presets.debug.tools.CollisionPointGameTool;
import necesse.gfx.forms.presets.debug.tools.CollisionRectangleGameTool;
import necesse.gfx.forms.presets.debug.tools.DebugGameTool;
import necesse.gfx.forms.presets.debug.tools.DelaunayTriangulatorGameTool;
import necesse.gfx.forms.presets.debug.tools.ExpandingPolygonGameTool;
import necesse.gfx.forms.presets.debug.tools.FindClosestHeightGameTool;
import necesse.gfx.forms.presets.debug.tools.GenerationTesterGameTool;
import necesse.gfx.forms.presets.debug.tools.InverseKinematicsGameTool;
import necesse.gfx.forms.presets.debug.tools.LinePathGenerationTestGameTool;
import necesse.gfx.forms.presets.debug.tools.LootTableTestGameTool;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.forms.presets.debug.tools.PresetCopyGameTool;
import necesse.gfx.forms.presets.debug.tools.PresetPasteGameTool;
import necesse.gfx.forms.presets.debug.tools.RegionPathFindGameTool;
import necesse.gfx.forms.presets.debug.tools.RoomAnalyzerGameTool;
import necesse.gfx.forms.presets.debug.tools.SoundTestGameTool;
import necesse.gfx.forms.presets.debug.tools.TileInfoGameTool;
import necesse.gfx.forms.presets.debug.tools.TilePathFindGameTool;
import necesse.gfx.forms.presets.debug.tools.TrailTestGameTool;
import necesse.gfx.forms.presets.debug.tools.UpdateLightGameTool;
import necesse.gfx.forms.presets.debug.tools.ZoneSelectorDebugGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.HUD;
import necesse.inventory.InventoryItem;
import necesse.level.gameObject.GameObject;
import necesse.level.gameObject.RockObject;
import necesse.level.maps.Level;
import necesse.level.maps.biomes.Biome;
import necesse.level.maps.generationModules.GenerationTools;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementClientQuests;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;
import necesse.level.maps.levelData.settlementData.TravelingHumanArrive;
import necesse.level.maps.levelData.settlementData.settlementQuestTiers.SettlementQuestTier;
import necesse.level.maps.levelData.settlementData.settler.Settler;
import necesse.level.maps.levelData.settlementData.settler.SettlerMob;
import necesse.level.maps.presets.Preset;
import necesse.level.maps.presets.PresetCopyFilter;

public class DebugToolsList {
   public final DebugForm parent;
   private ArrayList<Tool> tools;

   public DebugToolsList(DebugForm var1) {
      this.parent = var1;
      this.tools = new ArrayList();
      this.addTool(new PresetCopyGameTool(var1));
      this.addTool(new PresetPasteGameTool(var1));
      this.addTool("Copy level", () -> {
         Level var1 = this.getLevel();
         Preset var2 = Preset.copyFromLevel(var1, 0, 0, var1.width, var1.height);
         Screen.putClipboardDefault(var2.getSaveData(new PresetCopyFilter()).getScript());
      });
      this.addSubMenu("Loot table tests", LootTableTestGameTool.getSelectionMenu(var1), true);
      this.addTool(new TileInfoGameTool(var1));
      this.addTool("Show regions", () -> {
         HUD.debugShow = HUD.debugShow == HUD.DebugShow.REGIONS ? HUD.DebugShow.NOTHING : HUD.DebugShow.REGIONS;
         var1.client.setMessage("Showing " + HUD.debugShow, new Color(255, 255, 255));
      });
      this.addTool("Show connected regions", () -> {
         HUD.debugShow = HUD.debugShow == HUD.DebugShow.CONNECTED_REGIONS ? HUD.DebugShow.NOTHING : HUD.DebugShow.CONNECTED_REGIONS;
         var1.client.setMessage("Showing " + HUD.debugShow, new Color(255, 255, 255));
      });
      this.addTool("Show rooms", () -> {
         HUD.debugShow = HUD.debugShow == HUD.DebugShow.ROOMS ? HUD.DebugShow.NOTHING : HUD.DebugShow.ROOMS;
         var1.client.setMessage("Showing " + HUD.debugShow, new Color(255, 255, 255));
      });
      this.addTool("Show house", () -> {
         HUD.debugShow = HUD.debugShow == HUD.DebugShow.HOUSE ? HUD.DebugShow.NOTHING : HUD.DebugShow.HOUSE;
         var1.client.setMessage("Showing " + HUD.debugShow, new Color(255, 255, 255));
      });
      this.addTool("Show paths", () -> {
         HUD.debugShow = HUD.debugShow == HUD.DebugShow.PATHS ? HUD.DebugShow.NOTHING : HUD.DebugShow.PATHS;
         var1.client.setMessage("Showing " + HUD.debugShow, new Color(255, 255, 255));
      });
      this.addTool("Show heights", () -> {
         HUD.debugShow = HUD.debugShow == HUD.DebugShow.HEIGHT ? HUD.DebugShow.NOTHING : HUD.DebugShow.HEIGHT;
         var1.client.setMessage("Showing " + HUD.debugShow, new Color(255, 255, 255));
      });
      this.addTool("Show water types", () -> {
         HUD.debugShow = HUD.debugShow == HUD.DebugShow.WATER_TYPE ? HUD.DebugShow.NOTHING : HUD.DebugShow.WATER_TYPE;
         var1.client.setMessage("Showing " + HUD.debugShow, new Color(255, 255, 255));
      });
      this.addTool(new UpdateLightGameTool(var1));
      this.addTool(new TilePathFindGameTool(var1));
      this.addTool(new RegionPathFindGameTool(var1));
      this.addTool(new GenerationTesterGameTool(var1));
      this.addTool("Reset tutorial", () -> {
         var1.client.tutorial.reset();
         var1.client.setMessage("Tutorial reset!", Color.white);
      });
      this.addTool(new RoomAnalyzerGameTool(var1));
      this.addTool(new SoundTestGameTool(var1));
      this.addTool("Update splatting", () -> {
         Level var1x = var1.client.getLevel();
         var1x.splattingManager.updateSplatting(0, 0, var1x.width - 1, var1x.height - 1);
      });
      this.addTool("Change music", Screen::forceChangeMusic);
      if (GlobalData.isDevMode()) {
         this.addTool("Reset auth", GameAuth::generateNewAuth);
         this.addTool("Reset stats and achievements", () -> {
            GlobalData.resetStatsAndAchievements();
            SteamData.resetStatsAndAchievements(true);
            Server var1 = this.getServer();
            if (var1 != null) {
               var1.getLocalServerClient().resetStats();
            }

         });
      }

      this.addTool("Toggle blur", () -> {
         PostProcessGaussBlur.enabled = !PostProcessGaussBlur.enabled;
      });
      SelectionFloatMenu var2;
      SelectionFloatMenu var8;
      if (this.getServer() != null) {
         this.addTool("Spawn traveling human", () -> {
            Level var2 = this.getServerLevel();
            if (var2 != null) {
               SettlementLevelData var3 = SettlementLevelData.getSettlementData(var2);
               if (var3 != null && var3.spawnTravelingHuman()) {
                  var1.client.chat.addMessage("Spawned traveling human");
               } else {
                  var1.client.chat.addMessage("Could not spawn traveling human");
               }
            } else {
               var1.client.chat.addMessage("Must be singleplayer or host");
            }

         });
         var2 = new SelectionFloatMenu(var1);

         for(int var3 = 50; var3 <= 150; var3 += 5) {
            float var4 = (float)var3 / 100.0F;
            var2.add(var3 + "%", () -> {
               Level var4x = this.getServerLevel();
               if (var4x != null) {
                  SettlementLevelData var5 = SettlementLevelData.getSettlementData(var4x);
                  if (var5 != null) {
                     var5.setRaidDifficultyMod(var4);
                     var1.client.chat.addMessage("Set raid difficulty modifier to " + var5.getNextRaidDifficultyMod());
                  }
               } else {
                  var1.client.chat.addMessage("Must be singleplayer or host");
               }

               var2.remove();
            });
         }

         this.addSubMenu("Set raid difficulty", var2, true);
         var8 = new SelectionFloatMenu(var1);
         SettlementRaidLevelEvent.RaidDir[] var9 = SettlementRaidLevelEvent.RaidDir.values();
         int var5 = var9.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            SettlementRaidLevelEvent.RaidDir var7 = var9[var6];
            var8.add(var7.displayName.translate(), () -> {
               Level var4 = this.getServerLevel();
               if (var4 != null) {
                  SettlementLevelData var5 = SettlementLevelData.getSettlementData(var4);
                  if (var5 == null || !var5.spawnRaid(var7)) {
                     var1.client.chat.addMessage("Could not spawn raid");
                  }
               } else {
                  var1.client.chat.addMessage("Must be singleplayer or host");
               }

               var8.remove();
            });
         }

         var8.add("Random", () -> {
            Level var3 = this.getServerLevel();
            if (var3 != null) {
               SettlementLevelData var4 = SettlementLevelData.getSettlementData(var3);
               SettlementRaidLevelEvent.RaidDir var5 = (SettlementRaidLevelEvent.RaidDir)GameRandom.globalRandom.getOneOf((Object[])SettlementRaidLevelEvent.RaidDir.values());
               if (var4 == null || !var4.spawnRaid(var5)) {
                  var1.client.chat.addMessage("Could not spawn raid");
               }
            } else {
               var1.client.chat.addMessage("Must be singleplayer or host");
            }

            var8.remove();
         });
         this.addSubMenu("Spawn raid", var8, true);
         this.addTool("End raid", () -> {
            Iterator var1 = this.getServerLevel().entityManager.getLevelEvents().iterator();

            while(var1.hasNext()) {
               LevelEvent var2 = (LevelEvent)var1.next();
               if (var2 instanceof SettlementRaidLevelEvent) {
                  var2.over();
               }
            }

         });
         this.addTool("Start test quest", () -> {
            KillMobsTitleQuest var1 = new KillMobsTitleQuest(new StaticMessage("TEST QUEST"), new KillMobsQuest.KillObjective("zombie", 5), new KillMobsQuest.KillObjective[]{new KillMobsQuest.KillObjective("zombiearcher", 2)});
            this.getServer().world.getQuests().addQuest(var1, true);
            var1.makeActiveFor(this.getServer(), this.getServer().getLocalServerClient());
         });
         this.addTool("Clear server quests", () -> {
            this.getServer().world.getQuests().removeAll();
         });
         this.addTool("Complete settlement quest", () -> {
            ServerClient var1 = this.getServer().getLocalServerClient();
            if (var1 != null) {
               Level var2 = this.getServer().world.getLevel(var1);
               SettlementLevelData var3 = SettlementLevelData.getSettlementData(var2);
               if (var3 != null) {
                  SettlementClientQuests var4 = var3.getClientsQuests(var1);
                  Iterator var5 = var4.completeQuestAndGetReward().iterator();

                  while(var5.hasNext()) {
                     InventoryItem var6 = (InventoryItem)var5.next();
                     var2.entityManager.pickups.add(var6.getPickupEntity(var2, var1.playerMob.x, var1.playerMob.y));
                  }
               }
            }

         });
         SelectionFloatMenu var10 = new SelectionFloatMenu(var1);
         Iterator var12 = SettlementQuestTier.questTiers.iterator();

         while(var12.hasNext()) {
            SettlementQuestTier var14 = (SettlementQuestTier)var12.next();
            var10.add(var14.stringID, () -> {
               ServerClient var4 = this.getServer().getLocalServerClient();
               if (var4 != null) {
                  Level var5 = this.getServer().world.getLevel(var4);
                  SettlementLevelData var6 = SettlementLevelData.getSettlementData(var5);
                  if (var6 != null) {
                     var6.setCurrentQuestTierDebug(var4, var14);
                     var6.resetQuestsDebug();
                     SettlementQuestTier var7 = var6.getCurrentQuestTier();
                     if (var7 != null) {
                        var1.client.chat.addMessage("Set quest tier to " + var7.stringID);
                     } else {
                        var1.client.chat.addMessage("Completed all quest tiers");
                     }
                  }
               }

               var10.remove();
            });
         }

         var10.add("All", () -> {
            ServerClient var3 = this.getServer().getLocalServerClient();
            if (var3 != null) {
               Level var4 = this.getServer().world.getLevel(var3);
               SettlementLevelData var5 = SettlementLevelData.getSettlementData(var4);
               if (var5 != null) {
                  var5.setCurrentQuestTierDebug(var3, (SettlementQuestTier)null);
                  var5.resetQuestsDebug();
                  SettlementQuestTier var6 = var5.getCurrentQuestTier();
                  if (var6 != null) {
                     var1.client.chat.addMessage("Set quest tier to " + var6.stringID);
                  } else {
                     var1.client.chat.addMessage("Completed all quest tiers");
                  }
               }
            }

            var10.remove();
         });
         this.addSubMenu("Set settlement quest tier", var10, true);
         this.addTool(new MouseDebugGameTool(var1, "Debug mob AI") {
            public void init() {
               this.onLeftClick((var1) -> {
                  Level var2 = DebugToolsList.this.getServerLevel();
                  if (var2 != null) {
                     int var3 = this.getMouseX();
                     int var4 = this.getMouseY();
                     Iterator var5 = this.parent.client.getLevel().entityManager.mobs.getInRegionRangeByTile(var3 / 32, var4 / 32, 1).iterator();

                     while(var5.hasNext()) {
                        Mob var6 = (Mob)var5.next();
                        if (var6.getSelectBox().contains(var3, var4)) {
                           Mob var7 = GameUtils.getLevelMob(var6.getUniqueID(), var2);
                           if (var7 != null) {
                              this.parent.getManager().addComponent(new DebugMobAIForm(var6.getLevel(), var7, (var1x) -> {
                                 this.parent.getManager().removeComponent(var1x);
                              }));
                           } else {
                              this.parent.client.setMessage("Could not find mob on server", Color.RED);
                           }
                           break;
                        }
                     }
                  } else {
                     this.parent.client.chat.addMessage("Must be singleplayer or host");
                  }

                  return true;
               }, "Select mob");
            }
         });
      }

      this.addTool(new MouseDebugGameTool(var1, "Play test sound") {
         public void init() {
            this.onLeftClick((var1) -> {
               Screen.playSound(GameResources.tap, SoundEffect.effect((float)this.getMouseX(), (float)this.getMouseY()));
               return true;
            }, "Play tap sound at position");
            this.onRightClick((var1) -> {
               Screen.playSound(GameResources.roar, SoundEffect.effect((float)this.getMouseX(), (float)this.getMouseY()));
               return true;
            }, "Play roar sound at position");
         }
      });
      this.addTool(new CollisionPointGameTool(var1, "Collision point"));
      this.addTool(new CollisionRectangleGameTool(var1, "Collision rectangle"));
      this.addTool(new CastRayGameTool(var1, "Cast ray"));
      this.addTool(new LinePathGenerationTestGameTool(var1, "Line path generation"));
      this.addTool(new FindClosestHeightGameTool(var1, "Find closest height"));
      this.addTool(new ExpandingPolygonGameTool(var1, "Expanding polygon"));
      this.addTool(new MouseDebugGameTool(var1, "Test particle") {
         public void init() {
            this.onMouseMove((var1) -> {
               int var2;
               if (Screen.isKeyDown(-100)) {
                  for(var2 = 0; var2 < 50; ++var2) {
                     this.getLevel().entityManager.addParticle((float)this.getMouseX(), (float)this.getMouseY(), Particle.GType.CRITICAL).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 50.0F, (float)GameRandom.globalRandom.nextGaussian() * 50.0F).color(Color.getHSBColor(GameRandom.globalRandom.nextFloat(), 1.0F, 1.0F)).rotates(100.0F, 200.0F).givesLight(300.0F, 1.0F).lifeTime(1000);
                  }

                  return true;
               } else if (!Screen.isKeyDown(-99)) {
                  return false;
               } else {
                  for(var2 = 0; var2 < 50; ++var2) {
                     this.getLevel().entityManager.addParticle((Particle)(new RandomSpinningLightParticle(this.getLevel(), Color.getHSBColor(GameRandom.globalRandom.nextFloat(), 1.0F, 1.0F), (float)this.getMouseX(), (float)this.getMouseY(), (float)GameRandom.globalRandom.nextGaussian() * 50.0F, (float)GameRandom.globalRandom.nextGaussian() * 50.0F, 0, 1000)), Particle.GType.CRITICAL);
                  }

                  return true;
               }
            });
            this.onLeftClick((var1) -> {
               for(int var2 = 0; var2 < 50; ++var2) {
                  this.getLevel().entityManager.addParticle((float)this.getMouseX(), (float)this.getMouseY(), Particle.GType.CRITICAL).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 50.0F, (float)GameRandom.globalRandom.nextGaussian() * 50.0F).color(Color.getHSBColor(GameRandom.globalRandom.nextFloat(), 1.0F, 1.0F)).rotates(100.0F, 200.0F).givesLight(300.0F, 1.0F).lifeTime(1000);
               }

               return true;
            }, "Spawn new particles");
            this.onRightClick((var1) -> {
               for(int var2 = 0; var2 < 50; ++var2) {
                  this.getLevel().entityManager.addParticle((Particle)(new RandomSpinningLightParticle(this.getLevel(), Color.getHSBColor(GameRandom.globalRandom.nextFloat(), 1.0F, 1.0F), (float)this.getMouseX(), (float)this.getMouseY(), (float)GameRandom.globalRandom.nextGaussian() * 50.0F, (float)GameRandom.globalRandom.nextGaussian() * 50.0F, 0, 1000)), Particle.GType.CRITICAL);
               }

               return true;
            }, "Spawn old particles");
         }
      });
      var2 = new SelectionFloatMenu(var1);
      var8 = new SelectionFloatMenu(var1);
      Iterator var11 = SettlerRegistry.getSettlers().iterator();

      while(var11.hasNext()) {
         Settler var13 = (Settler)var11.next();
         var2.add(var13.getGenericMobName(), () -> {
            Level var4 = this.getServerLevel();
            if (var4 != null) {
               SettlementLevelData var5 = SettlementLevelData.getSettlementData(var4);
               if (var5 != null) {
                  SettlerMob var6 = var13.getNewSettlerMob(var4);
                  var6.setSettlerSeed(GameRandom.globalRandom.nextInt());
                  var4.entityManager.mobs.add(var6.getMob());
                  var5.moveIn(new LevelSettler(var5, var6));
               }
            } else {
               var1.client.chat.addMessage("Must be singleplayer or host");
            }

            var2.remove();
         });
         var8.add(var13.getGenericMobName(), () -> {
            Level var4 = this.getServerLevel();
            if (var4 != null) {
               SettlementLevelData var5 = SettlementLevelData.getSettlementData(var4);
               if (var5 != null) {
                  SettlerMob var6 = var13.getNewSettlerMob(var4);
                  var6.setSettlerSeed(GameRandom.globalRandom.nextInt());
                  HumanMob var7 = (HumanMob)var6.getMob();
                  var5.spawnTravelingHuman(new TravelingHumanArrive(SettlementLevelData.travelingRecruits, var7));
               }
            } else {
               var1.client.chat.addMessage("Must be singleplayer or host");
            }

            var8.remove();
         });
      }

      this.addSubMenu("Spawn new settler", var2, true);
      this.addSubMenu("Spawn new recruit", var8, true);
      this.addTool(new MouseDebugGameTool(var1, "Fireworks") {
         public void init() {
            this.onLeftClick((var1) -> {
               FireworksExplosion var2 = new FireworksExplosion((FireworksRocketParticle.ParticleGetter)null);
               GameRandom.globalRandom.runOneOf(() -> {
                  var2.colorGetter = (var0, var1, var2x) -> {
                     return Color.getHSBColor(var2x.nextFloat(), 1.0F, 1.0F);
                  };
               }, () -> {
                  var2.colorGetter = (var0, var1, var2x) -> {
                     return Color.getHSBColor(var2x.nextFloat(), 1.0F, 1.0F);
                  };
               }, () -> {
                  var2.colorGetter = (var0, var1, var2x) -> {
                     return ParticleOption.randomFlameColor(var2x);
                  };
               }, () -> {
                  var2.colorGetter = (var0, var1, var2x) -> {
                     return ParticleOption.randomFlameColor(var2x, 0.0F);
                  };
               }, () -> {
                  var2.colorGetter = (var0, var1, var2x) -> {
                     return ParticleOption.randomFlameColor(var2x, 110.0F);
                  };
               }, () -> {
                  var2.colorGetter = (var0, var1, var2x) -> {
                     return ParticleOption.randomFlameColor(var2x, 240.0F);
                  };
               }, () -> {
                  var2.colorGetter = (var0, var1, var2x) -> {
                     return ParticleOption.randomFlameColor(var2x, 310.0F);
                  };
               });
               GameRandom.globalRandom.runOneOf(() -> {
                  var2.popChance = 0.5F;
               }, () -> {
                  var2.popChance = 0.0F;
               });
               GameRandom.globalRandom.runOneOf(() -> {
                  var2.trailChance = 0.5F;
               }, () -> {
                  var2.trailChance = 0.2F;
               });
               GameRandom.globalRandom.runOneOf(() -> {
                  var2.pathGetter = FireworksPath.sphere((float)GameRandom.globalRandom.getIntBetween(150, 250));
               }, () -> {
                  var2.pathGetter = FireworksPath.shape(FireworksPath.star, (float)GameRandom.globalRandom.getIntBetween(150, 250), (var0) -> {
                     return Math.min(1.0F, var0.nextFloat() * 1.2F);
                  });
               }, () -> {
                  var2.pathGetter = FireworksPath.shape(FireworksPath.heart, (float)GameRandom.globalRandom.getIntBetween(150, 250), (var0) -> {
                     return Math.min(1.0F, var0.nextFloat() * 1.2F);
                  });
               }, () -> {
                  var2.pathGetter = FireworksPath.disc((float)GameRandom.globalRandom.getIntBetween(150, 250));
                  var2.minSize = 20;
                  var2.maxSize = 30;
                  var2.trailSize = 15.0F;
                  var2.trailFadeTime = 1000;
                  var2.particles = 50;
                  var2.trailChance = 1.0F;
               }, () -> {
                  var2.pathGetter = FireworksPath.splash((float)GameRandom.globalRandom.getIntBetween(0, 360), (float)GameRandom.globalRandom.getIntBetween(150, 250));
               });
               this.getLevel().entityManager.addParticle((Particle)(new FireworksRocketParticle(this.getLevel(), (float)this.getMouseX(), (float)this.getMouseY(), 1200L, GameRandom.globalRandom.getIntBetween(300, 400), var2, GameRandom.globalRandom)), Particle.GType.CRITICAL);
               return true;
            }, "Spawn fireworks");
         }
      });
      this.addTool(new InverseKinematicsGameTool(var1));
      this.addTool(new DelaunayTriangulatorGameTool(var1));
      this.addTool(new ZoneSelectorDebugGameTool(var1, "Zone selector"));
      this.addTool("Print room sizes", () -> {
         HashMap var1 = this.getLevel().regionManager.getRoomSizes();
         var1.forEach((var0, var1x) -> {
            System.out.println(var0 + ": " + var1x);
         });
      });
      this.addTool("Toggle controller boxes", () -> {
         FormManager.drawControllerFocusBoxes = !FormManager.drawControllerFocusBoxes;
      });
      this.addTool("Toggle controller areas", () -> {
         FormManager.drawControllerAreaBoxes = !FormManager.drawControllerAreaBoxes;
      });
      if (GlobalData.isDevMode()) {
         this.addTool(new TrailTestGameTool(var1, "Trail test"));
         this.addTool(new ChaikinSmoothTestGameTool(var1, "Smooth test"));
         this.addTool(new BezierCurveTestGameTool(var1, "B\u00e9zier Curve test"));
         this.addTool("Test generation", () -> {
            ExecutorService var1 = Executors.newFixedThreadPool(4, (var0) -> {
               return new Thread(var0, "test-generation");
            });
            Biome var2 = this.getLevel().biome;
            GameRandom var3 = new GameRandom(1234567890L);
            WorldEntity var4 = WorldEntity.getDebugWorldEntity();
            HashMap var5 = new HashMap();
            HashMap var6 = new HashMap();
            HashMap var7 = new HashMap();
            byte var8 = 50;

            for(int var9 = 0; var9 < var8; ++var9) {
               GameRandom var10 = var3.nextSeeded(var9);
               var1.submit(() -> {
                  Level var8 = var2.getNewLevel(var10.nextInt(), var10.nextInt(), this.getLevel().getIslandDimension(), (Server)null, var4);
                  synchronized(var1) {
                     GenerationTools.collectLevelContent(var8, var5, var6, var7);
                  }
               });
            }

            try {
               var1.shutdown();
               var1.awaitTermination(1L, TimeUnit.HOURS);
            } catch (InterruptedException var11) {
               var11.printStackTrace();
            }

            System.out.println("Biome " + var2.getStringID() + " at dimension " + this.getLevel().getIslandDimension() + " over " + var8 + " samples");
            GenerationTools.printLevelContent(var5, var6, var7);
         });
         this.addTool("Test ores", () -> {
            ArrayList var0 = (ArrayList)BiomeRegistry.getBiomes().stream().filter((var0x) -> {
               return BiomeRegistry.isDiscoverable(var0x.getID());
            }).collect(Collectors.toCollection(ArrayList::new));
            WorldEntity var1 = WorldEntity.getDebugWorldEntity();
            byte var2 = 50;
            ExecutorService var3 = Executors.newFixedThreadPool(4, (var0x) -> {
               return new Thread(var0x, "test-ores");
            });
            LinkedList var4 = new LinkedList();
            Iterator var5 = var0.iterator();

            while(var5.hasNext()) {
               Biome var6 = (Biome)var5.next();
               var4.add(var3.submit(() -> {
                  GameRandom var3 = new GameRandom(1234567890L);
                  HashMap var4 = new HashMap();
                  HashMap var5 = new HashMap();
                  ExecutorService var6x = Executors.newFixedThreadPool(4, (var1x) -> {
                     return new Thread(var1x, var6.getStringID() + "-generator");
                  });

                  for(int var7 = 0; var7 < var2; ++var7) {
                     GameRandom var8 = var3.nextSeeded(var7);
                     var6x.submit(() -> {
                        Level var6xx = var6.getNewCaveLevel(var8.nextInt(), var8.nextInt(), -1, (Server)null, var1);
                        Level var7 = var6.getNewDeepCaveLevel(var8.nextInt(), var8.nextInt(), -2, (Server)null, var1);
                        synchronized(var6x) {
                           GenerationTools.collectLevelContent(var6xx, (HashMap)null, var4, (HashMap)null);
                           GenerationTools.collectLevelContent(var7, (HashMap)null, var5, (HashMap)null);
                        }
                     });
                  }

                  var6x.shutdown();
                  var6x.awaitTermination(1L, TimeUnit.HOURS);
                  List var10 = (List)Stream.of(var4.keySet().stream(), var5.keySet().stream()).flatMap((var0) -> {
                     return var0;
                  }).filter((var0) -> {
                     GameObject var1 = ObjectRegistry.getObject(var0);
                     return !(var1 instanceof RockObject);
                  }).collect(Collectors.toList());
                  Iterator var11 = var10.iterator();

                  while(var11.hasNext()) {
                     int var9 = (Integer)var11.next();
                     var4.remove(var9);
                     var5.remove(var9);
                  }

                  return new ArrayList(Arrays.asList(new ObjectValue("Biome " + var6.getStringID() + " cave:", var4), new ObjectValue("Biome " + var6.getStringID() + " deep cave:", var5)));
               }));
            }

            var5 = var4.iterator();

            while(var5.hasNext()) {
               Future var12 = (Future)var5.next();

               try {
                  ArrayList var7 = (ArrayList)var12.get();
                  Predicate var8 = (var0x) -> {
                     return var0x instanceof RockObject;
                  };
                  Iterator var9 = var7.iterator();

                  while(var9.hasNext()) {
                     ObjectValue var10 = (ObjectValue)var9.next();
                     System.out.println((String)var10.object);
                     GenerationTools.printLevelContent((HashMap)null, (Predicate)null, (HashMap)var10.value, var8, (HashMap)null, (Predicate)null);
                  }
               } catch (ExecutionException | InterruptedException var11) {
                  var11.printStackTrace();
               }
            }

            var3.shutdown();
         });
         this.addTool("Test explorer expeditions", () -> {
            Level var2 = this.getServerLevel();
            if (var2 == null) {
               var1.client.chat.addMessage("Must be singleplayer or host");
            } else {
               ServerClient var3 = this.getLocalClient();
               SettlementLevelData var4 = SettlementLevelData.getSettlementData(var2);
               if (var4 == null) {
                  System.out.println("Could not find settlement data");
               } else {
                  System.out.println("Using settlement: " + var2.settlementLayer.getSettlementName().translate());
               }

               ExplorerHumanMob var5 = (ExplorerHumanMob)var2.entityManager.mobs.streamArea(var3.playerMob.x, var3.playerMob.y, 9600).filter((var0) -> {
                  return var0 instanceof ExplorerHumanMob;
               }).map((var0) -> {
                  return (ExplorerHumanMob)var0;
               }).filter(HumanMob::isSettler).findBestDistance(0, Comparator.comparingDouble((var1x) -> {
                  return (double)var1x.getDistance(var3.playerMob);
               })).orElse((Object)null);
               if (var5 == null) {
                  System.out.println("Could not find explorer settler");
               } else {
                  System.out.println("Using explorer: " + var5.getDisplayName());
               }

               Iterator var6 = ExpeditionMissionRegistry.explorerExpeditionIDs.iterator();

               while(var6.hasNext()) {
                  int var7 = (Integer)var6.next();
                  SettlerExpedition var8 = ExpeditionMissionRegistry.getExpedition(var7);
                  byte var9 = 100;
                  float var10 = 0.0F;

                  int var11;
                  for(var11 = 0; var11 < 100; ++var11) {
                     List var12 = var8.getRewardItems(var4, var5);
                     var10 += (Float)var12.stream().reduce(0.0F, (var0, var1x) -> {
                        return var0 + var1x.getBrokerValue();
                     }, Float::sum);
                  }

                  var11 = var8.getBaseCost(var4, var5);
                  float var14 = GameMath.toDecimals(var10 / (float)var9, 2);
                  float var13 = GameMath.toDecimals((var14 - (float)var11) / (float)var11 * 100.0F, 2);
                  System.out.println(var8.getDisplayName().translate() + " average " + var14 + " broker value which is " + var13 + "% above base cost of " + var11);
               }

            }
         });
      }

   }

   public void addTool(String var1, Runnable var2) {
      this.tools.add(new RunnableTool(var1, () -> {
         try {
            var2.run();
         } catch (Exception var3) {
            System.err.println(var1 + " debug tool error:");
            var3.printStackTrace();
         }

      }));
   }

   public void addTool(DebugGameTool var1) {
      this.addTool(var1.name, () -> {
         Screen.clearGameTools(this.parent);
         Screen.setGameTool(var1, this.parent);
      });
   }

   public void addSubMenu(String var1, SelectionFloatMenu var2, boolean var3) {
      this.tools.add((var3x) -> {
         var3x.add(var1, var2, var3);
      });
   }

   public Level getLevel() {
      return this.parent.client.getLevel();
   }

   public Server getServer() {
      return this.parent.client.getLocalServer();
   }

   public ServerClient getLocalClient() {
      return this.getServer() == null ? null : this.getServer().getLocalServerClient();
   }

   public Level getServerLevel() {
      ServerClient var1 = this.getLocalClient();
      return var1 == null ? null : this.getServer().world.getLevel(var1.getLevelIdentifier());
   }

   public FloatMenu getFloatMenu(FormComponent var1) {
      SelectionFloatMenu var2 = new SelectionFloatMenu(var1, SelectionFloatMenu.Solid(new FontOptions(16)));
      Iterator var3 = this.tools.iterator();

      while(var3.hasNext()) {
         Tool var4 = (Tool)var3.next();
         var4.addToMenu(var2);
      }

      return var2;
   }

   private static class RunnableTool implements Tool {
      public final String name;
      public final Runnable onClicked;

      public RunnableTool(String var1, Runnable var2) {
         this.name = var1;
         this.onClicked = var2;
      }

      public void addToMenu(SelectionFloatMenu var1) {
         var1.add(this.name, () -> {
            this.onClicked.run();
            var1.remove();
         });
      }
   }

   private interface Tool {
      void addToMenu(SelectionFloatMenu var1);
   }
}
