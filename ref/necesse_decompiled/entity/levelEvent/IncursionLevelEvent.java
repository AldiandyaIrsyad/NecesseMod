package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Point;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Objects;
import java.util.function.Supplier;
import necesse.engine.EventStatusBarData;
import necesse.engine.Screen;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.GameUtils;
import necesse.entity.Entity;
import necesse.entity.levelEvent.actions.CustomIteratorLevelEventAction;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.hostile.bosses.BossSpawnPortalMob;
import necesse.entity.mobs.hostile.bosses.ReturnPortalMob;
import necesse.gfx.fairType.FairType;
import necesse.gfx.fairType.FairTypeDrawOptions;
import necesse.gfx.fairType.TypeParsers;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.IncursionLevel;
import necesse.level.maps.Level;

public abstract class IncursionLevelEvent extends LevelEvent {
   public boolean isDirty;
   public boolean isFighting;
   public boolean isDone;
   public boolean isCompleted;
   public boolean bossPortalSpawned;
   public int bossPortalAttemptsRemaining;
   public boolean onlySpawnOnePortal;
   public ArrayList<Integer> bossPortalUniqueIDs;
   public boolean countdownStarted;
   public int countdownTotal;
   public int countdownTimer;
   public String bossStringID;
   public int bossUniqueID;
   public final CustomIteratorLevelEventAction updateProgressAction;

   public IncursionLevelEvent() {
      this.bossPortalAttemptsRemaining = 5;
      this.onlySpawnOnePortal = true;
      this.bossPortalUniqueIDs = new ArrayList();
      this.countdownTotal = 5000;
      this.shouldSave = true;
      this.updateProgressAction = (CustomIteratorLevelEventAction)this.registerAction(new CustomIteratorLevelEventAction() {
         protected void write(PacketWriter var1) {
            IncursionLevelEvent.this.setupUpdatePacket(var1);
         }

         protected void read(PacketReader var1) {
            IncursionLevelEvent.this.applyUpdatePacket(var1);
         }
      });
   }

   public IncursionLevelEvent(String var1) {
      this();
      this.bossStringID = var1;
   }

   public boolean isNetworkImportant() {
      return true;
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addSafeString("bossStringID", this.bossStringID);
      var1.addBoolean("bossPortalSpawned", this.bossPortalSpawned);
      var1.addInt("bossPortalAttemptsRemaining", this.bossPortalAttemptsRemaining);
      var1.addIntArray("bossPortalUniqueIDs", this.bossPortalUniqueIDs.stream().mapToInt((var0) -> {
         return var0;
      }).toArray());
      var1.addBoolean("countdownStarted", this.countdownStarted);
      var1.addInt("countdownTimer", this.countdownTimer);
      var1.addInt("bossUniqueID", this.bossUniqueID);
      var1.addBoolean("isFighting", this.isFighting);
      var1.addBoolean("isDone", this.isDone);
      var1.addBoolean("isCompleted", this.isCompleted);
   }

   public void applyLoadData(LoadData var1) {
      super.applyLoadData(var1);
      this.bossStringID = var1.getSafeString("bossStringID", this.bossStringID, false);
      this.bossPortalSpawned = var1.getBoolean("bossPortalSpawned", this.bossPortalSpawned, false);
      this.bossPortalAttemptsRemaining = var1.getInt("bossPortalAttemptsRemaining", this.bossPortalAttemptsRemaining, false);
      this.bossPortalUniqueIDs = new ArrayList();
      int[] var2 = var1.getIntArray("bossPortalUniqueIDs", new int[0], false);
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         int var5 = var2[var4];
         this.bossPortalUniqueIDs.add(var5);
      }

      this.countdownStarted = var1.getBoolean("countdownStarted", this.countdownStarted, false);
      this.countdownTimer = var1.getInt("countdownTimer", this.countdownTimer, false);
      this.bossUniqueID = var1.getInt("bossUniqueID", this.bossUniqueID, false);
      this.isFighting = var1.getBoolean("isFighting", this.isFighting, false);
      this.isDone = var1.getBoolean("isDone", this.isDone, false);
      this.isCompleted = var1.getBoolean("isCompleted", this.isCompleted, false);
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      this.setupUpdatePacket(var1);
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.applyUpdatePacket(var1);
   }

   public void setupUpdatePacket(PacketWriter var1) {
      var1.putNextBoolean(this.countdownStarted);
      if (this.countdownStarted) {
         var1.putNextInt(this.countdownTimer);
      }

      var1.putNextBoolean(this.bossPortalSpawned);
      var1.putNextBoolean(this.isFighting);
      var1.putNextBoolean(this.isDone);
      var1.putNextBoolean(this.isCompleted);
   }

   public void applyUpdatePacket(PacketReader var1) {
      this.countdownStarted = var1.getNextBoolean();
      if (this.countdownStarted) {
         this.countdownTimer = var1.getNextInt();
      }

      this.bossPortalSpawned = var1.getNextBoolean();
      this.isFighting = var1.getNextBoolean();
      this.isDone = var1.getNextBoolean();
      this.isCompleted = var1.getNextBoolean();
   }

   public void clientTick() {
      super.clientTick();
      if (!this.countdownStarted) {
         this.countdownTimer = 3000;
      }

      if (this.countdownTimer > 0) {
         this.countdownTimer -= 50;
      }

      if (!this.isFighting) {
         int var1;
         int var2;
         if (this.isDone) {
            var1 = 100;
            var2 = 100;
         } else if (this.countdownStarted) {
            var1 = this.countdownTimer;
            var2 = this.countdownTotal;
         } else if (this.bossPortalSpawned) {
            var1 = 100;
            var2 = 100;
         } else {
            var1 = this.getObjectiveCurrent();
            var2 = this.getObjectiveMax();
         }

         Screen.registerEventStatusBar(this.getUniqueID(), var1, var2, () -> {
            return new EventStatusBarData((GameMessage)null) {
               public FairTypeDrawOptions getDisplayNameDrawOptions() {
                  FairType var1 = new FairType();
                  FontOptions var2 = (new FontOptions(16)).outline();
                  if (IncursionLevelEvent.this.isCompleted) {
                     var1.append(var2, Localization.translate("ui", "incursionnowcomplete"));
                  } else if (IncursionLevelEvent.this.isDone) {
                     var1.append(var2, Localization.translate("ui", "incursionnowclose"));
                  } else if (IncursionLevelEvent.this.countdownStarted) {
                     var1.append(var2, Localization.translate("misc", "bossapproaching"));
                  } else if (IncursionLevelEvent.this.bossPortalSpawned) {
                     var1.append(var2, Localization.translate("misc", "bossportal"));
                  } else if (IncursionLevelEvent.this.level instanceof IncursionLevel && ((IncursionLevel)IncursionLevelEvent.this.level).incursionData != null) {
                     Iterator var3 = ((IncursionLevel)IncursionLevelEvent.this.level).incursionData.getObjectives(((IncursionLevel)IncursionLevelEvent.this.level).incursionData, var2).iterator();

                     while(var3.hasNext()) {
                        FairType var4 = (FairType)var3.next();
                        var1.append(var2, "\n").append(var4.getGlyphsArray());
                     }
                  }

                  if (var1.getLength() <= 0) {
                     return null;
                  } else {
                     var1.applyParsers(TypeParsers.STRIP_GAME_COLOR, TypeParsers.ItemIcon(var2.getSize()));
                     return var1.getDrawOptions(FairType.TextAlign.CENTER);
                  }
               }

               public GameMessage getStatusText(EventStatusBarData.StatusAtTime var1) {
                  if (!IncursionLevelEvent.this.isCompleted && !IncursionLevelEvent.this.isDone && !IncursionLevelEvent.this.bossPortalSpawned && !IncursionLevelEvent.this.countdownStarted) {
                     String var2 = (int)(var1.getPercent() * 100.0F) + "%";
                     return new LocalMessage("ui", "incursionprogressbar", "percent", var2);
                  } else {
                     return null;
                  }
               }

               public Color getBufferColor() {
                  return null;
               }

               public Color getFillColor() {
                  if (IncursionLevelEvent.this.isDone) {
                     return new Color(10, 117, 8);
                  } else if (IncursionLevelEvent.this.countdownStarted) {
                     return new Color(150, 13, 38);
                  } else {
                     return IncursionLevelEvent.this.bossPortalSpawned ? new Color(201, 104, 24) : new Color(24, 70, 201);
                  }
               }
            };
         });
      }

   }

   public void serverTick() {
      super.serverTick();
      boolean var1 = this.isDirty;
      if ((this.isObjectiveDone() || this.isFighting || this.bossPortalSpawned) && !this.isDone) {
         if (!this.bossPortalSpawned) {
            boolean var2 = GameUtils.streamNetworkClients(this.level).anyMatch((var0) -> {
               return var0.playerMob != null;
            });
            if (var2) {
               this.spawnBossPortals(true);
               var1 = true;
            }
         } else if (this.countdownStarted) {
            if (this.bossUniqueID == 0) {
               this.countdownTimer -= 50;
               if (this.countdownTimer <= 0) {
                  if (this.bossStringID != null) {
                     Point var10 = new Point(this.level.width / 2 * 32 + 16, this.level.height / 2 * 32 + 16);
                     Mob var3 = (Mob)this.level.entityManager.mobs.stream().filter((var0) -> {
                        return var0 instanceof ReturnPortalMob;
                     }).min(Comparator.comparingDouble((var1x) -> {
                        return GameMath.diagonalMoveDistance((double)((float)var10.x - var1x.x), (double)((float)var10.y - var1x.y));
                     })).orElse((Object)null);
                     if (var3 != null) {
                        var10.x = var3.getX();
                        var10.y = var3.getY();
                     }

                     PlayerMob var4 = (PlayerMob)GameUtils.streamNetworkClients(this.level).map((var0) -> {
                        return var0.playerMob;
                     }).filter(Objects::nonNull).min(Comparator.comparingDouble((var1x) -> {
                        return GameMath.diagonalMoveDistance((double)((float)var10.x - var1x.x), (double)((float)var10.y - var1x.y));
                     })).orElse((Object)null);
                     if (var4 != null) {
                        var10.x = var4.getX();
                        var10.y = var4.getY();
                     }

                     float var5 = (float)GameRandom.globalRandom.nextInt(360);
                     float var6 = GameMath.cos(var5);
                     float var7 = GameMath.sin(var5);
                     float var8 = 960.0F;
                     Mob var9 = MobRegistry.getMob(this.bossStringID, this.level);
                     this.level.entityManager.addMob(var9, (float)(var10.x + (int)(var6 * var8)), (float)(var10.y + (int)(var7 * var8)));
                     this.onBossSummoned(var9);
                     if (this.isServer()) {
                        this.level.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bosssummon", "name", var9.getLocalization())), (Level)this.level);
                     }
                  } else {
                     if (this.level instanceof IncursionLevel) {
                        ((IncursionLevel)this.level).markCanComplete(() -> {
                           return null;
                        }, (var0) -> {
                           return null;
                        });
                     }

                     this.isCompleted = true;
                     this.isDone = true;
                     this.isFighting = false;
                     this.bossUniqueID = -1;
                  }
               }
            } else {
               Mob var11 = this.bossUniqueID == -1 ? null : (Mob)this.level.entityManager.mobs.get(this.bossUniqueID, true);
               if (this.isFighting && (var11 == null || var11.removed())) {
                  boolean var12 = true;
                  if (this.isServer()) {
                     if (var11 != null && var11.hasDied()) {
                        if (this.level instanceof IncursionLevel) {
                           IncursionLevel var10000 = (IncursionLevel)this.level;
                           Supplier var10001 = () -> {
                              return var11.getLootDropsPosition((ServerClient)null);
                           };
                           Objects.requireNonNull(var11);
                           var10000.markCanComplete(var10001, var11::getLootDropsPosition);
                        }

                        this.isCompleted = true;
                     } else if (this.bossPortalAttemptsRemaining > 0) {
                        this.spawnBossPortals(true);
                        var12 = false;
                     } else {
                        this.level.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("ui", "incursionnowclose")), (Level)this.level);
                     }
                  }

                  this.isDone = var12;
                  this.countdownStarted = false;
                  this.isFighting = false;
                  this.isDirty = true;
                  this.bossUniqueID = 0;
               }
            }
         }
      }

      if (var1 && this.isServer()) {
         this.updateProgressAction.runAndSend();
         this.isDirty = false;
      }

   }

   public abstract boolean isObjectiveDone();

   public abstract int getObjectiveCurrent();

   public abstract int getObjectiveMax();

   public float getRandomTabletDropChance(Mob var1) {
      return 0.014285714F;
   }

   public GameMessage canSpawnBoss(String var1) {
      if (this.bossStringID != null && !this.bossStringID.equals(var1)) {
         return new LocalMessage("misc", "cannotsummonhere");
      } else if (this.isCompleted) {
         return new LocalMessage("misc", "cannotsummoncomplete");
      } else {
         return this.isFighting ? new LocalMessage("misc", "cannotsummonnow") : null;
      }
   }

   public void removeExistingSpawnPortal() {
      Iterator var1 = this.bossPortalUniqueIDs.iterator();

      while(var1.hasNext()) {
         int var2 = (Integer)var1.next();
         Mob var3 = var2 == -1 ? null : (Mob)this.level.entityManager.mobs.get(var2, false);
         if (var3 != null && !var3.removed()) {
            var3.remove();
         }
      }

      this.bossPortalUniqueIDs.clear();
   }

   public void spawnBossPortals(boolean var1) {
      this.removeExistingSpawnPortal();
      Point var2 = (Point)this.level.entityManager.mobs.stream().filter((var0) -> {
         return var0 instanceof ReturnPortalMob;
      }).map(Entity::getPositionPoint).findFirst().orElse((Object)null);
      if (var2 == null) {
         var2 = new Point(this.level.width * 32 / 2, this.level.height * 32 / 2);
      }

      float var3 = 90.0F;
      int var4 = this.onlySpawnOnePortal ? 1 : Math.max(this.bossPortalAttemptsRemaining, 1);
      float var5 = 360.0F / (float)var4;

      for(int var6 = 0; var6 < var4; ++var6) {
         float var7 = GameMath.fixAngle(var3 + (float)var6 * var5);
         Point2D.Float var8 = GameMath.getAngleDir(var7);
         BossSpawnPortalMob var9 = (BossSpawnPortalMob)MobRegistry.getMob("bossspawnportal", this.level);
         if (this.onlySpawnOnePortal) {
            var9.remainingAttemptsTip = this.bossPortalAttemptsRemaining;
         }

         this.level.entityManager.addMob(var9, (float)var2.x + var8.x * 3.0F * 32.0F, (float)var2.y + var8.y * 3.0F * 32.0F);
         this.bossPortalUniqueIDs.add(var9.getUniqueID());
      }

      this.bossPortalSpawned = true;
      this.countdownStarted = false;
      if (var1) {
         --this.bossPortalAttemptsRemaining;
         this.level.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bossportal")), (Level)this.level);
      }

   }

   public void onBossSpawnTriggered(BossSpawnPortalMob var1) {
      if (var1 != null) {
         var1.remove(0.0F, 0.0F, (Attacker)null, true);
         this.bossPortalUniqueIDs.remove(var1.getUniqueID());
      }

      this.countdownStarted = true;
      this.countdownTimer = this.countdownTotal;
      this.level.getServer().network.sendToClientsAt(new PacketChatMessage(new LocalMessage("misc", "bossapproaching")), (Level)this.level);
      this.removeExistingSpawnPortal();
      this.isDirty = true;
   }

   public void onBossSummoned(Mob var1) {
      this.bossUniqueID = var1.getUniqueID();
      this.bossPortalSpawned = true;
      this.removeExistingSpawnPortal();
      this.countdownStarted = true;
      this.countdownTimer = 0;
      this.isFighting = true;
      this.isDone = false;
      this.isDirty = true;
   }
}
