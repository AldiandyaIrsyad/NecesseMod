package necesse.entity.levelEvent;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.level.maps.LevelObject;
import necesse.level.maps.regionSystem.RegionPosition;

public class FlameTrapEvent extends LevelEvent implements Attacker {
   public static GameDamage damage = new GameDamage(20.0F, 10.0F, 0.0F, 2.0F, 1.0F);
   private int tileX;
   private int tileY;
   private int dir;
   private int ticks;
   private int range;
   private int maxRange;
   private ArrayList<Integer> hits;

   public FlameTrapEvent() {
      this.range = 8;
      this.maxRange = this.range;
      this.hits = new ArrayList();
   }

   public FlameTrapEvent(int var1, int var2, int var3) {
      this.tileX = var1;
      this.tileY = var2;
      this.dir = Math.abs(var3) % 4;
      this.range = 8;
      this.maxRange = this.range;
      this.hits = new ArrayList();
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.tileX = var1.getNextInt();
      this.tileY = var1.getNextInt();
      this.dir = var1.getNextByte();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.tileX);
      var1.putNextInt(this.tileY);
      var1.putNextByte((byte)this.dir);
   }

   public void init() {
      super.init();
      this.ticks = 0;
   }

   public void clientTick() {
      Point var1 = this.getDir(this.dir);
      int var2 = this.ticks % this.range;
      if (var2 == 0) {
         this.maxRange = this.range;
      }

      int var3 = this.tileX + var1.x * var2;
      int var4 = this.tileY + var1.y * var2;
      if (this.maxRange >= var2 && !this.level.getObject(var3, var4).isWall && !this.level.getObject(var3, var4).isRock) {
         for(int var5 = 0; var5 < 5; ++var5) {
            Particle.GType var6 = var5 <= 2 ? Particle.GType.CRITICAL : Particle.GType.COSMETIC;
            this.level.entityManager.addParticle((float)(var3 * 32 + 16 + (int)(GameRandom.globalRandom.nextGaussian() * 8.0)), (float)(var4 * 32 + 16 + (int)(GameRandom.globalRandom.nextGaussian() * 8.0)), var6).movesConstant((float)var1.x * GameRandom.globalRandom.nextFloat() * 10.0F, (float)var1.y * GameRandom.globalRandom.nextFloat() * 10.0F).color(new Color(255, 186, 0)).givesLight(0.0F, 0.5F).height(12.0F);
         }
      } else {
         this.maxRange = var2;
      }

      ++this.ticks;
      if (this.ticks >= this.range * 2) {
         this.over();
      }

   }

   public void serverTick() {
      Point var1 = this.getDir(this.dir);
      int var2 = this.ticks % this.range;
      if (var2 == 0) {
         this.hits.clear();
         this.maxRange = this.range;
      }

      int var3 = this.tileX + var1.x * var2;
      int var4 = this.tileY + var1.y * var2;
      if (this.maxRange >= var2 && !this.level.getObject(var3, var4).isWall && !this.level.getObject(var3, var4).isRock) {
         Rectangle var5 = new Rectangle(var3 * 32, var4 * 32, 32, 32);
         LevelObject var6 = this.level.getLevelObject(var3, var4);
         if (var6.object.attackThrough) {
            Stream var10000 = var6.getAttackThroughCollisions().stream();
            Objects.requireNonNull(var5);
            if (var10000.anyMatch(var5::intersects)) {
               var6.attackThrough(damage, this);
            }
         }

         Iterator var7 = this.getTargets(var5).iterator();

         while(var7.hasNext()) {
            Mob var8 = (Mob)var7.next();
            if (var8.canBeHit(this) && var5.intersects(var8.getHitBox())) {
               this.hit(var8);
            }
         }
      } else {
         this.maxRange = var2;
      }

      ++this.ticks;
      if (this.ticks >= this.range * 2) {
         this.over();
      }

   }

   private void hit(Mob var1) {
      if (!this.hits.contains(var1.getUniqueID())) {
         if (var1.getLevel().isTrialRoom) {
            GameDamage var2 = new GameDamage(DamageTypeRegistry.TRUE, (float)var1.getMaxHealth() / 4.0F);
            var1.isServerHit(var2, 0.0F, 0.0F, 0.0F, this);
            this.hits.add(var1.getUniqueID());
         } else {
            var1.isServerHit(damage, 0.0F, 0.0F, 0.0F, this);
            ActiveBuff var3 = new ActiveBuff("onfire", var1, 10.0F, this);
            var1.addBuff(var3, true);
            this.hits.add(var1.getUniqueID());
         }

      }
   }

   private ArrayList<Mob> getTargets(Rectangle var1) {
      ArrayList var2 = this.level.entityManager.mobs.getInRegionRangeByTile((int)var1.getCenterX() / 32, (int)var1.getCenterY() / 32, 1);
      this.level.getServer().streamClients().filter((var0) -> {
         return var0 != null && var0.playerMob != null;
      }).filter((var1x) -> {
         return var1x.isSamePlace(this.getLevel());
      }).forEach((var1x) -> {
         var2.add(var1x.playerMob);
      });
      return var2;
   }

   public GameMessage getAttackerName() {
      return new LocalMessage("deaths", "flametrapname");
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("flametrap", 2);
   }

   public Mob getFirstAttackOwner() {
      return null;
   }

   public Point getDir(int var1) {
      if (var1 == 0) {
         return new Point(0, -1);
      } else if (var1 == 1) {
         return new Point(1, 0);
      } else if (var1 == 2) {
         return new Point(0, 1);
      } else {
         return var1 == 3 ? new Point(-1, 0) : new Point(0, 0);
      }
   }

   public Collection<RegionPosition> getRegionPositions() {
      return Collections.singleton(this.level.regionManager.getRegionPosByTile(this.tileX, this.tileY));
   }
}
