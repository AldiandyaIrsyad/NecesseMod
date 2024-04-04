package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Point;
import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Function;
import necesse.engine.Screen;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketLevelEvent;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameRandom;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.util.TeleportResult;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.level.maps.Level;
import necesse.level.maps.regionSystem.RegionPosition;

public class TeleportEvent extends LevelEvent {
   private int x;
   private int y;
   private Mob mobTarget;
   private int mobUniqueID;
   private ServerClient clientTarget;
   private LevelIdentifier targetLevelIdentifier;
   private int delay;
   private float sicknessTime;
   private long teleportTime;
   private Function<LevelIdentifier, Level> destinationGenerator;
   private Function<Level, TeleportResult> destinationCheck;

   public TeleportEvent() {
   }

   public TeleportEvent(int var1, int var2, int var3) {
      this.x = var1;
      this.y = var2;
      this.mobUniqueID = var3;
   }

   public TeleportEvent(Mob var1, int var2, LevelIdentifier var3, float var4, Function<LevelIdentifier, Level> var5, Function<Level, TeleportResult> var6) {
      this(var1.getX(), var1.getY(), var1.getUniqueID());
      this.mobTarget = var1;
      this.delay = var2;
      this.targetLevelIdentifier = var3;
      this.sicknessTime = var4;
      this.destinationGenerator = var5;
      this.destinationCheck = var6;
   }

   public TeleportEvent(ServerClient var1, int var2, LevelIdentifier var3, float var4, Function<LevelIdentifier, Level> var5, Function<Level, TeleportResult> var6) {
      this((Mob)var1.playerMob, var2, var3, var4, var5, var6);
      this.clientTarget = var1;
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.x = var1.getNextInt();
      this.y = var1.getNextInt();
      this.mobUniqueID = var1.getNextInt();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.x);
      var1.putNextInt(this.y);
      var1.putNextInt(this.mobUniqueID);
   }

   public void init() {
      super.init();
      if (this.isClient()) {
         boolean var1 = this.mobUniqueID == this.level.getClient().getSlot();

         for(int var2 = 0; var2 < 10; ++var2) {
            Particle.GType var3 = var1 ? null : (var2 <= 3 ? Particle.GType.CRITICAL : Particle.GType.COSMETIC);
            this.level.entityManager.addParticle((float)(this.x + (int)(GameRandom.globalRandom.nextGaussian() * 8.0)), (float)this.y, var3).movesConstant((float)GameRandom.globalRandom.nextGaussian() * 5.0F, (float)GameRandom.globalRandom.nextGaussian() * 5.0F).color(new Color(255, 245, 198)).height((float)GameRandom.globalRandom.nextInt(40)).givesLight(50.0F, 0.5F).lifeTime(600);
         }

         if (var1) {
            Screen.playSound(GameResources.teleport, SoundEffect.globalEffect().volume(0.7F));
         } else {
            Screen.playSound(GameResources.teleport, SoundEffect.effect((float)this.x, (float)this.y));
         }

         this.over();
      } else if (this.isServer()) {
         if (this.mobTarget == null && this.clientTarget == null) {
            this.over();
         } else {
            if (this.mobTarget != null && this.mobTarget.isPlayer && this.clientTarget == null) {
               System.err.println("Cannot teleport player without knowing client");
               this.over();
               return;
            }

            if (this.delay <= 0) {
               this.performTeleport();
            } else {
               this.teleportTime = this.level.getWorldEntity().getTime() + (long)this.delay;
            }
         }
      } else {
         this.over();
      }

   }

   public void serverTick() {
      super.serverTick();
      if (this.level.getWorldEntity().getTime() >= this.teleportTime) {
         this.performTeleport();
      }

   }

   private void performTeleport() {
      if (this.isServer()) {
         if (this.clientTarget != null) {
            AtomicBoolean var1 = new AtomicBoolean();
            this.clientTarget.changeLevelCheck(this.targetLevelIdentifier, this.destinationGenerator, (var2x) -> {
               boolean var3 = this.destinationCheck == null;
               LevelIdentifier var4 = null;
               Point var5 = null;
               if (this.destinationCheck != null) {
                  TeleportResult var6 = (TeleportResult)this.destinationCheck.apply(var2x);
                  var3 = var6.isValid;
                  var4 = var6.newDestination;
                  var5 = var6.targetPosition;
               }

               if (var3) {
                  this.sendTeleportEvent(this.mobTarget);
                  var1.set(true);
               }

               return new TeleportResult(var3, var4, var5);
            }, true);
            if (var1.get()) {
               this.sendTeleportEvent(this.mobTarget);
            }
         } else {
            Level var5 = this.level.getServer().world.getLevel(this.targetLevelIdentifier, this.destinationGenerator == null ? null : () -> {
               return (Level)this.destinationGenerator.apply(this.targetLevelIdentifier);
            });
            boolean var2 = this.destinationCheck == null;
            Point var3 = null;
            if (this.destinationCheck != null) {
               TeleportResult var4 = (TeleportResult)this.destinationCheck.apply(var5);
               var2 = var4.isValid;
               var3 = var4.targetPosition;
            }

            if (var2) {
               this.sendTeleportEvent(this.mobTarget);
               if (var5.isSamePlace(this.mobTarget.getLevel())) {
                  if (var3 != null) {
                     this.mobTarget.setPos((float)var3.x, (float)var3.y, true);
                     this.mobTarget.sendMovementPacket(true);
                  }
               } else if (var3 != null) {
                  this.level.entityManager.changeMobLevel(this.mobTarget, var5, var3.x, var3.y, true);
               } else {
                  this.level.entityManager.changeMobLevel(this.mobTarget, var5, this.mobTarget.getX(), this.mobTarget.getY(), true);
               }

               this.sendTeleportEvent(this.mobTarget);
            }
         }

         if (this.sicknessTime > 0.0F) {
            this.mobTarget.addBuff(new ActiveBuff("teleportsickness", this.mobTarget, this.sicknessTime, (Attacker)null), true);
         }
      }

      this.over();
   }

   protected void sendTeleportEvent(Mob var1) {
      TeleportEvent var2 = new TeleportEvent(var1.getX(), var1.getY() + 5, var1.getUniqueID());
      var1.getLevel().getServer().network.sendToClientsWithTile(new PacketLevelEvent(var2), var1.getLevel(), var1.getTileX(), var1.getTileY());
   }

   public Collection<RegionPosition> getRegionPositions() {
      return Collections.singleton(this.level.regionManager.getRegionPosByTile(this.x / 32, this.y / 32));
   }
}
