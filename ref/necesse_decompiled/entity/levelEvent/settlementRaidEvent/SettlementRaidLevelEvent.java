package necesse.entity.levelEvent.settlementRaidEvent;

import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import necesse.engine.MusicOptions;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MusicRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.levelEvent.LevelEvent;
import necesse.entity.levelEvent.actions.EmptyLevelEventAction;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.RaiderMob;
import necesse.level.maps.biomes.MobChance;
import necesse.level.maps.biomes.MobSpawnTable;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementBed;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public abstract class SettlementRaidLevelEvent extends LevelEvent {
   public int preparingTicks;
   public int remainingApproachTicks;
   public int remainingPreparingSpawnTicks;
   public int remainingPreparingIdleTicks;
   public int remainingActiveTicks;
   public int totalSpawnTicks;
   protected int startSettlers = -1;
   protected float difficultyModifier = 1.0F;
   protected Point centerSpawnTile;
   protected RaidDir direction;
   protected boolean started;
   protected float additionalRaiderBuffer = 0.0F;
   public boolean combatStarted = false;
   protected LinkedList<RaiderMob> raiders = new LinkedList();
   protected int[] loadedRaiderUniqueIDs;
   protected float nextSpawnTick = 1.0F;
   protected int raidGroupCounter;
   protected SettlementLevelData levelData;
   protected int preparingMessageTicks;
   public final EmptyLevelEventAction combatTriggeredAction;
   protected LinkedList<Point> potentialSpawnTiles = new LinkedList();
   protected ArrayList<Point> potentialAttackTiles = new ArrayList();
   protected HashSet<Point> illegalSpawnTiles = new HashSet();
   protected HashSet<Point> foundSpawnTiles = new HashSet();
   protected ArrayList<Point> spawnTiles;

   public SettlementRaidLevelEvent(int var1, int var2, int var3, int var4) {
      super(true);
      this.shouldSave = true;
      this.remainingApproachTicks = var1 * 20;
      this.remainingPreparingSpawnTicks = var2 * 20;
      this.totalSpawnTicks = this.remainingPreparingSpawnTicks;
      this.remainingPreparingIdleTicks = var3 * 20;
      this.preparingTicks = (var2 + var3) * 20;
      this.remainingActiveTicks = var4 * 20;
      this.combatTriggeredAction = (EmptyLevelEventAction)this.registerAction(new EmptyLevelEventAction() {
         protected void run() {
            SettlementRaidLevelEvent.this.combatStarted = true;
         }
      });
   }

   public void triggerCombatEvent() {
      if (!this.combatStarted) {
         this.combatTriggeredAction.runAndSend();
      }

   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("remainingApproachTicks", this.remainingApproachTicks);
      var1.addInt("remainingPreparingSpawnTicks", this.remainingPreparingSpawnTicks);
      var1.addInt("remainingPreparingIdleTicks", this.remainingPreparingIdleTicks);
      var1.addInt("preparingMessageTicks", this.preparingMessageTicks);
      var1.addInt("preparingTicks", this.preparingTicks);
      var1.addInt("remainingActiveTicks", this.remainingActiveTicks);
      var1.addInt("totalSpawnTicks", this.totalSpawnTicks);
      var1.addInt("startSettlers", this.startSettlers);
      if (this.centerSpawnTile != null) {
         var1.addPoint("centerSpawnTile", this.centerSpawnTile);
      }

      var1.addFloat("nextSpawnTick", this.nextSpawnTick);
      var1.addFloat("difficultyModifier", this.difficultyModifier);
      if (this.direction != null) {
         var1.addEnum("direction", this.direction);
      }

      var1.addInt("raidGroupCounter", this.raidGroupCounter);
      var1.addBoolean("started", this.started);
      var1.addBoolean("combatStarted", this.combatStarted);
      int[] var2 = this.raiders.stream().mapToInt((var0) -> {
         return ((Mob)var0).getUniqueID();
      }).toArray();
      var1.addIntArray("raiders", var2);
      var1.addFloat("additionalRaiderBuffer", this.additionalRaiderBuffer);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.remainingApproachTicks = var1.getInt("remainingApproachTicks", this.remainingApproachTicks);
      this.remainingPreparingSpawnTicks = var1.getInt("remainingPreparingSpawnTicks", this.remainingPreparingSpawnTicks);
      this.remainingPreparingIdleTicks = var1.getInt("remainingPreparingIdleTicks", this.remainingPreparingIdleTicks);
      this.preparingMessageTicks = var1.getInt("preparingMessageTicks", this.preparingMessageTicks);
      this.preparingTicks = var1.getInt("preparingTicks", this.preparingTicks);
      this.remainingActiveTicks = var1.getInt("remainingActiveTicks", this.remainingActiveTicks);
      this.totalSpawnTicks = var1.getInt("totalSpawnTicks", this.totalSpawnTicks);
      this.startSettlers = var1.getInt("startSettlers", this.startSettlers);
      this.centerSpawnTile = var1.getPoint("centerSpawnTile", this.centerSpawnTile, false);
      this.nextSpawnTick = var1.getFloat("nextSpawnTick", this.nextSpawnTick);
      this.difficultyModifier = var1.getFloat("difficultyModifier", this.difficultyModifier);
      this.direction = (RaidDir)var1.getEnum(RaidDir.class, "direction", (RaidDir)GameRandom.globalRandom.getOneOf((Object[])SettlementRaidLevelEvent.RaidDir.values()), false);
      this.raidGroupCounter = var1.getInt("raidGroupCounter", this.raidGroupCounter);
      this.started = var1.getBoolean("started", this.started);
      this.combatStarted = var1.getBoolean("combatStarted", this.combatStarted);
      this.loadedRaiderUniqueIDs = var1.getIntArray("raiders", new int[0]);
      this.additionalRaiderBuffer = var1.getFloat("additionalRaiderBuffer", this.additionalRaiderBuffer);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.remainingApproachTicks = var1.getNextInt();
      this.remainingPreparingSpawnTicks = var1.getNextInt();
      this.remainingPreparingIdleTicks = var1.getNextInt();
      this.remainingActiveTicks = var1.getNextInt();
      this.totalSpawnTicks = var1.getNextInt();
      this.difficultyModifier = var1.getNextFloat();
      if (var1.getNextBoolean()) {
         int var2 = var1.getNextInt();
         int var3 = var1.getNextInt();
         this.centerSpawnTile = new Point(var2, var3);
      } else {
         this.centerSpawnTile = null;
      }

      this.direction = SettlementRaidLevelEvent.RaidDir.values()[var1.getNextByteUnsigned()];
      this.started = var1.getNextBoolean();
      this.combatStarted = var1.getNextBoolean();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.remainingApproachTicks);
      var1.putNextInt(this.remainingPreparingSpawnTicks);
      var1.putNextInt(this.remainingPreparingIdleTicks);
      var1.putNextInt(this.remainingActiveTicks);
      var1.putNextInt(this.totalSpawnTicks);
      var1.putNextFloat(this.difficultyModifier);
      var1.putNextBoolean(this.centerSpawnTile != null);
      if (this.centerSpawnTile != null) {
         var1.putNextInt(this.centerSpawnTile.x);
         var1.putNextInt(this.centerSpawnTile.y);
      }

      var1.putNextByteUnsigned(this.direction.ordinal());
      var1.putNextBoolean(this.started);
      var1.putNextBoolean(this.combatStarted);
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void init() {
      super.init();
      if (this.isServer()) {
         GameMessage var1 = this.getApproachMessage(this.level.settlementLayer.getSettlementName(), true);
         if (var1 != null) {
            this.level.settlementLayer.streamTeamMembersAndOnIsland().forEach((var1x) -> {
               var1x.sendChatMessage(var1);
            });
         }

         this.levelData = SettlementLevelData.getSettlementData(this.level);
         if (this.levelData != null && this.startSettlers == -1) {
            this.startSettlers = this.getCurrentSettlerMobs();
         }

         int var3;
         if (this.centerSpawnTile == null) {
            HashSet var2 = new HashSet();
            if (this.direction == null) {
               this.direction = (RaidDir)GameRandom.globalRandom.getOneOf((Object[])SettlementRaidLevelEvent.RaidDir.values());
            }

            int var7;
            var3 = 2;
            int var4 = var3 + 5;
            float var5 = 0.3F;
            int var6;
            int var8;
            int var9;
            int var10;
            int var11;
            label295:
            switch (this.direction) {
               case North:
                  var6 = (int)((float)this.level.width * var5);
                  var7 = (int)((float)this.level.width * (1.0F - var5));
                  var8 = var6;

                  while(true) {
                     if (var8 >= var7) {
                        break label295;
                     }

                     for(var9 = var3; var9 < var4; ++var9) {
                        var2.add(new Point(var8, var9));
                     }

                     ++var8;
                  }
               case East:
                  var6 = (int)((float)this.level.height * var5);
                  var7 = (int)((float)this.level.height * (1.0F - var5));
                  var8 = var6;

                  while(true) {
                     if (var8 >= var7) {
                        break label295;
                     }

                     for(var9 = this.level.width - var4; var9 < this.level.width - var3; ++var9) {
                        var2.add(new Point(var9, var8));
                     }

                     ++var8;
                  }
               case South:
                  var6 = (int)((float)this.level.width * var5);
                  var7 = (int)((float)this.level.width * (1.0F - var5));
                  var8 = var6;

                  while(true) {
                     if (var8 >= var7) {
                        break label295;
                     }

                     for(var9 = this.level.height - var4; var9 < this.level.height - var3; ++var9) {
                        var2.add(new Point(var8, var9));
                     }

                     ++var8;
                  }
               case West:
                  var6 = (int)((float)this.level.height * var5);
                  var7 = (int)((float)this.level.height * (1.0F - var5));
                  var8 = var6;

                  while(true) {
                     if (var8 >= var7) {
                        break label295;
                     }

                     for(var9 = var3; var9 < var4; ++var9) {
                        var2.add(new Point(var9, var8));
                     }

                     ++var8;
                  }
               case NorthEast:
                  var6 = (int)((float)this.level.width * (1.0F - var5));
                  var7 = this.level.width;
                  byte var17 = 0;
                  var9 = (int)((float)this.level.height * var5);

                  for(var10 = var6; var10 < var7; ++var10) {
                     for(var11 = var3; var11 < var4; ++var11) {
                        var2.add(new Point(var10, var11));
                     }
                  }

                  var10 = var17;

                  while(true) {
                     if (var10 >= var9) {
                        break label295;
                     }

                     for(var11 = this.level.width - var4; var11 < this.level.width - var3; ++var11) {
                        var2.add(new Point(var11, var10));
                     }

                     ++var10;
                  }
               case SouthEast:
                  var6 = (int)((float)this.level.width * (1.0F - var5));
                  var7 = this.level.width;
                  var8 = (int)((float)this.level.height * (1.0F - var5));
                  var9 = this.level.height;

                  for(var10 = var6; var10 < var7; ++var10) {
                     for(var11 = this.level.height - var4; var11 < this.level.height - var3; ++var11) {
                        var2.add(new Point(var10, var11));
                     }
                  }

                  var10 = var8;

                  while(true) {
                     if (var10 >= var9) {
                        break label295;
                     }

                     for(var11 = this.level.width - var4; var11 < this.level.width - var3; ++var11) {
                        var2.add(new Point(var11, var10));
                     }

                     ++var10;
                  }
               case SouthWest:
                  var6 = 0;
                  var7 = (int)((float)this.level.width * var5);
                  var8 = (int)((float)this.level.height * (1.0F - var5));
                  var9 = this.level.height;

                  for(var10 = var6; var10 < var7; ++var10) {
                     for(var11 = this.level.height - var4; var11 < this.level.height - var3; ++var11) {
                        var2.add(new Point(var10, var11));
                     }
                  }

                  var10 = var8;

                  while(true) {
                     if (var10 >= var9) {
                        break label295;
                     }

                     for(var11 = var3; var11 < var4; ++var11) {
                        var2.add(new Point(var11, var10));
                     }

                     ++var10;
                  }
               case NorthWest:
                  var6 = 0;
                  var7 = (int)((float)this.level.width * var5);
                  var8 = 0;
                  var9 = (int)((float)this.level.height * var5);

                  for(var10 = var6; var10 < var7; ++var10) {
                     for(var11 = var3; var11 < var4; ++var11) {
                        var2.add(new Point(var10, var11));
                     }
                  }

                  for(var10 = var8; var10 < var9; ++var10) {
                     for(var11 = var3; var11 < var4; ++var11) {
                        var2.add(new Point(var11, var10));
                     }
                  }
            }

            ArrayList var16 = new ArrayList(var2);

            while(!var16.isEmpty()) {
               var7 = GameRandom.globalRandom.nextInt(var16.size());
               Point var18 = (Point)var16.remove(var7);
               if (!this.level.isSolidTile(var18.x, var18.y)) {
                  this.centerSpawnTile = new Point(var18.x, var18.y);
                  break;
               }
            }
         }

         if (this.centerSpawnTile != null) {
            if (this.levelData != null) {
               Iterator var12 = this.levelData.getBeds().iterator();

               while(var12.hasNext()) {
                  SettlementBed var15 = (SettlementBed)var12.next();
                  if (var15.getSettler() != null) {
                     this.potentialAttackTiles.add(new Point(var15.tileX, var15.tileY));
                  }
               }

               Point var13 = this.levelData.getObjectEntityPos();
               if (var13 != null) {
                  this.potentialAttackTiles.add(var13);
               }
            }

            this.potentialSpawnTiles.add(this.centerSpawnTile);

            for(int var14 = this.centerSpawnTile.x - 5; var14 < this.centerSpawnTile.x + 5; ++var14) {
               for(var3 = this.centerSpawnTile.y - 5; var3 < this.centerSpawnTile.y + 5; ++var3) {
                  if ((new Rectangle(3, 3, this.level.width - 6, this.level.height - 6)).contains(var14, var3)) {
                     this.potentialSpawnTiles.add(new Point(var14, var3));
                  }
               }
            }

            System.out.println("Raid spawned at " + this.level.getIdentifier() + " on tile " + this.centerSpawnTile.x + ", " + this.centerSpawnTile.y + " with difficulty " + this.difficultyModifier);
            if (this.remainingApproachTicks <= 0) {
               this.onRaidApproached();
            }
         } else {
            this.over();
         }
      }

   }

   public void setDifficultyModifier(float var1) {
      this.difficultyModifier = var1;
   }

   public void setDirection(RaidDir var1) {
      this.direction = var1;
   }

   public void clientTick() {
      super.clientTick();
      if (!this.isOver()) {
         this.level.getWorldEntity().preventSleep();
         if (this.remainingApproachTicks > 0) {
            int var1 = this.level.presentPlayers > 0 ? 3 : 1;
            this.remainingApproachTicks -= var1;
         } else if (this.remainingPreparingSpawnTicks > 0) {
            --this.remainingPreparingSpawnTicks;
         } else if (this.remainingPreparingIdleTicks > 0) {
            --this.remainingPreparingIdleTicks;
         } else if (this.remainingActiveTicks > 0) {
            this.started = true;
            --this.remainingActiveTicks;
         } else {
            this.over();
         }

         if (this.started) {
            if (this.combatStarted) {
               Screen.setMusic((new MusicOptions(MusicRegistry.StormingTheHamletPart2)).volume(1.5F).fadeInTime(250), Screen.MusicPriority.EVENT);
            } else {
               Screen.setMusic((new MusicOptions(MusicRegistry.StormingTheHamletPart1)).volume(1.5F).fadeInTime(500).fadeOutTime(750), Screen.MusicPriority.EVENT);
            }

            this.level.settlementLayer.refreshRaidActive();
         } else {
            this.level.settlementLayer.refreshRaidApproaching();
            Screen.setMusic((new MusicOptions(MusicRegistry.StormingTheHamletPart1)).volume(1.5F).fadeInTime(500).fadeOutTime(750), Screen.MusicPriority.EVENT);
         }

      }
   }

   public void serverTick() {
      super.serverTick();
      if (!this.isOver()) {
         int var2;
         int var4;
         if (this.loadedRaiderUniqueIDs != null) {
            int[] var1 = this.loadedRaiderUniqueIDs;
            var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               var4 = var1[var3];
               Mob var5 = (Mob)this.level.entityManager.mobs.get(var4, false);
               if (var5 instanceof RaiderMob) {
                  this.raiders.add((RaiderMob)var5);
               }
            }

            this.loadedRaiderUniqueIDs = null;
         }

         this.level.getWorldEntity().preventSleep();
         int var7 = this.getCurrentSettlerMobs();
         var2 = this.startSettlers - var7;
         float var8 = 0.5F;
         switch (this.level.getWorldSettings().difficulty) {
            case HARD:
               var8 = 0.65F;
               break;
            case BRUTAL:
               var8 = 0.75F;
         }

         var4 = Math.max(1, (int)Math.floor(Math.pow((double)(this.startSettlers - 3), (double)var8)));
         if (var7 > 0 && var2 < var4) {
            if (this.remainingApproachTicks > 0) {
               int var9 = this.level.presentPlayers > 0 ? 3 : 1;
               this.tickRaidApproach(var9);
               this.remainingApproachTicks -= var9;
               if (this.remainingApproachTicks >= var9 && this.remainingApproachTicks % 1200 < var9) {
                  GameMessage var6 = this.getApproachMessage(this.level.settlementLayer.getSettlementName(), false);
                  if (var6 != null) {
                     this.level.settlementLayer.streamTeamMembersAndOnIsland().forEach((var1x) -> {
                        var1x.sendChatMessage(var6);
                     });
                  }
               }

               if (this.remainingApproachTicks <= 0) {
                  this.onRaidApproached();
               }
            } else if (this.remainingPreparingSpawnTicks > 0) {
               --this.remainingPreparingSpawnTicks;
               this.tickRaidSpawning();
               this.tickPreparingMessage();
            } else if (this.remainingPreparingIdleTicks > 0) {
               --this.remainingPreparingIdleTicks;
               this.tickPreparingMessage();
            } else if (this.remainingActiveTicks > 0) {
               this.startRaid();
               this.raiders.removeIf((var0) -> {
                  return ((Mob)var0).removed();
               });
               if (this.raiders.isEmpty()) {
                  GameMessage var10 = this.getDefeatedMessage();
                  if (var10 != null) {
                     GameUtils.streamServerClients(this.level).forEach((var1x) -> {
                        var1x.sendChatMessage(var10);
                     });
                  }

                  this.over();
               }

               --this.remainingActiveTicks;
            } else {
               this.over();
            }

            if (this.started) {
               this.level.settlementLayer.refreshRaidActive();
            } else {
               this.level.settlementLayer.refreshRaidApproaching();
            }

         } else {
            this.over();
         }
      }
   }

   public void tickRaidApproach(int var1) {
      int var2 = Math.min(this.potentialSpawnTiles.size() / this.remainingApproachTicks * var1, this.potentialSpawnTiles.size());

      for(int var3 = 0; var3 < var2; ++var3) {
         this.processPotentialTile((Point)this.potentialSpawnTiles.removeFirst());
      }

   }

   protected void processPotentialTile(Point var1) {
      if (!this.illegalSpawnTiles.contains(var1)) {
         if (this.level.isSolidTile(var1.x, var1.y)) {
            this.illegalSpawnTiles.add(var1);
         } else {
            this.foundSpawnTiles.add(var1);
         }
      }
   }

   public void tickPreparingMessage() {
      if (!this.started) {
         if (this.preparingMessageTicks % 1200 == 0) {
            GameMessage var1 = this.getPreparingMessage(this.level.settlementLayer.getSettlementName());
            if (var1 != null) {
               this.level.settlementLayer.streamTeamMembersAndOnIsland().forEach((var1x) -> {
                  var1x.sendChatMessage(var1);
               });
            }
         }

         ++this.preparingMessageTicks;
      }

   }

   public void onRaidApproached() {
      while(!this.potentialSpawnTiles.isEmpty()) {
         this.processPotentialTile((Point)this.potentialSpawnTiles.removeFirst());
      }

      this.illegalSpawnTiles.clear();
      this.spawnTiles = new ArrayList(this.foundSpawnTiles);
      this.foundSpawnTiles.clear();
   }

   public void startRaid() {
      if (this.isServer()) {
         if (!this.started) {
            this.started = true;
            long var1 = 2147483647L;

            Iterator var3;
            RaiderMob var4;
            for(var3 = this.raiders.iterator(); var3.hasNext(); var1 = Math.min(var4.getRaidingStartTicks(), var1)) {
               var4 = (RaiderMob)var3.next();
            }

            if (var1 > 0L) {
               var3 = this.raiders.iterator();

               while(var3.hasNext()) {
                  var4 = (RaiderMob)var3.next();
                  var4.updateRaidingStartTicks(var4.getRaidingStartTicks() - var1);
               }

               this.preparingTicks = (int)((long)this.preparingTicks - var1);
            }

            GameMessage var5 = this.getStartMessage(this.level.settlementLayer.getSettlementName());
            if (var5 != null) {
               this.level.settlementLayer.streamTeamMembersAndOnIsland().forEach((var1x) -> {
                  var1x.sendChatMessage(var5);
               });
            }

            this.level.getServer().network.sendToClientsWithEntity(new PacketLevelEvent(this), this);
         }

      }
   }

   public void tickRaidSpawning() {
      if (this.isServer()) {
         float var1 = this.getSpawnWaves();
         this.nextSpawnTick += var1 / (float)this.totalSpawnTicks;
         if (this.nextSpawnTick >= 1.0F) {
            --this.nextSpawnTick;
            this.raiders.removeIf((var0) -> {
               return ((Mob)var0).removed();
            });
            ++this.raidGroupCounter;
            this.additionalRaiderBuffer += this.getSpawnsPerWave() / var1;
            boolean var2 = false;

            while(true) {
               while(true) {
                  Point var3;
                  MobSpawnTable var4;
                  do {
                     if (!(this.additionalRaiderBuffer >= 1.0F)) {
                        if (!var2) {
                           this.nextSpawnTick += 0.5F;
                        }

                        return;
                     }

                     --this.additionalRaiderBuffer;
                     var3 = (Point)GameRandom.globalRandom.getOneOf((List)this.spawnTiles);
                     var4 = this.getSpawnTable();
                  } while(var4 == null);

                  while(true) {
                     MobChance var5 = var4.getRandomMob(this.level, (ServerClient)null, var3, GameRandom.globalRandom);
                     if (var5 == null) {
                        break;
                     }

                     Mob var6 = var5.getMob(this.level, (ServerClient)null, var3);
                     if (var6 instanceof RaiderMob) {
                        RaiderMob var7 = (RaiderMob)var6;
                        Point var8 = var6.getPathMoveOffset();
                        Point var9 = new Point(var3.x * 32 + var8.x, var3.y * 32 + var8.y);
                        if (!var6.collidesWith(this.level, var9.x, var9.y) && var7.canRaiderSpawnAt(var9.x, var9.y)) {
                           Point var10 = (Point)GameRandom.globalRandom.getOneOf((List)this.potentialAttackTiles);
                           var7.makeRaider(this, this.centerSpawnTile, var10, (long)this.preparingTicks, this.raidGroupCounter, this.difficultyModifier);
                           var7.setRaidEvent(this);
                           this.level.entityManager.addMob(var6, (float)var9.x, (float)var9.y);
                           this.raiders.add(var7);
                           var2 = true;
                           break;
                        }
                     }

                     var4 = var4.withoutRandomMob(var5);
                  }
               }
            }
         }
      }

   }

   public void onRaidOver() {
      this.raiders.forEach(RaiderMob::raidOver);
      this.raiders.removeIf((var0) -> {
         return ((Mob)var0).removed();
      });
      GameMessage var1;
      if (this.remainingApproachTicks <= 0 && !this.raiders.isEmpty()) {
         var1 = this.getLeavingMessage();
         if (var1 != null) {
            GameUtils.streamServerClients(this.level).forEach((var1x) -> {
               var1x.sendChatMessage(var1);
            });
         }
      } else {
         var1 = this.getAbruptEndingMessage();
         if (var1 != null) {
            GameUtils.streamServerClients(this.level).forEach((var1x) -> {
               var1x.sendChatMessage(var1);
            });
         }
      }

      if (this.levelData != null) {
         int var7 = this.getCurrentSettlerMobs();
         int var2 = this.startSettlers - var7;
         float var3 = this.difficultyModifier;
         if (var2 > 0) {
            var3 = GameMath.limit(this.difficultyModifier - 0.05F, 0.5F, 1.0F);
         } else {
            double var4 = this.levelData.settlers.stream().map(LevelSettler::getMob).filter(Objects::nonNull).mapToDouble((var0) -> {
               return (double)var0.getMob().getHealth() / (double)var0.getMob().getMaxHealth();
            }).min().orElse(0.0);
            float var6 = (float)Math.min(Math.pow(var4, 2.0) / 8.0 + 0.009999999776482582, 0.05000000074505806);
            var3 = Math.min(var3 + var6, 1.25F);
         }

         this.levelData.onRaidOver(this, var3);
      }

   }

   public void over() {
      if (!this.isOver()) {
         this.onRaidOver();
      }

      super.over();
   }

   public int getCurrentSettlers() {
      return this.levelData != null ? this.levelData.countTotalSettlers() : 0;
   }

   public int getCurrentSettlerMobs() {
      return this.levelData != null ? (int)this.levelData.settlers.stream().map(LevelSettler::getMob).filter(Objects::nonNull).count() : 0;
   }

   public abstract GameMessage getApproachMessage(GameMessage var1, boolean var2);

   public abstract GameMessage getPreparingMessage(GameMessage var1);

   public abstract GameMessage getStartMessage(GameMessage var1);

   public abstract GameMessage getDefeatedMessage();

   public abstract GameMessage getLeavingMessage();

   public GameMessage getAbruptEndingMessage() {
      return this.getLeavingMessage();
   }

   public abstract MobSpawnTable getSpawnTable();

   public abstract float getSpawnWaves();

   public abstract float getSpawnsPerWave();

   public static enum RaidDir {
      NorthWest("ui", "dirnorthwest"),
      North("ui", "dirnorth"),
      NorthEast("ui", "dirnortheast"),
      West("ui", "dirwest"),
      East("ui", "direast"),
      SouthWest("ui", "dirsouthwest"),
      South("ui", "dirsouth"),
      SouthEast("ui", "dirsoutheast");

      public final GameMessage displayName;

      private RaidDir(GameMessage var3) {
         this.displayName = var3;
      }

      private RaidDir(String var3, String var4) {
         this(new LocalMessage(var3, var4));
      }

      // $FF: synthetic method
      private static RaidDir[] $values() {
         return new RaidDir[]{NorthWest, North, NorthEast, West, East, SouthWest, South, SouthEast};
      }
   }

   protected static class PotentialSpawnTile {
      public final Point center;
      public final Point tile;

      public PotentialSpawnTile(Point var1, Point var2) {
         this.center = var1;
         this.tile = var2;
      }
   }
}
