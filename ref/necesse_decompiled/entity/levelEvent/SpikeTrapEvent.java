package necesse.entity.levelEvent;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.DamageTypeRegistry;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.DeathMessageTable;
import necesse.entity.mobs.GameDamage;
import necesse.entity.mobs.Mob;
import necesse.level.maps.regionSystem.RegionPosition;

public class SpikeTrapEvent extends LevelEvent implements Attacker {
   public static GameDamage damage = new GameDamage(25.0F, 20.0F, 0.0F, 2.0F, 1.0F);
   private int tileX;
   private int tileY;
   private int ticks;
   private int range;
   private ArrayList<Integer> hits;

   public SpikeTrapEvent() {
      this.range = 1;
      this.hits = new ArrayList();
   }

   public SpikeTrapEvent(int var1, int var2) {
      this.tileX = var1;
      this.tileY = var2;
      this.range = 1;
      this.hits = new ArrayList();
   }

   public void applySpawnPacket(PacketReader var1) {
      super.applySpawnPacket(var1);
      this.tileX = var1.getNextInt();
      this.tileY = var1.getNextInt();
   }

   public void setupSpawnPacket(PacketWriter var1) {
      super.setupSpawnPacket(var1);
      var1.putNextInt(this.tileX);
      var1.putNextInt(this.tileY);
   }

   public void init() {
      super.init();
      this.ticks = 0;
   }

   public void serverTick() {
      int var1 = this.ticks % this.range;
      if (var1 == 0) {
         this.hits.clear();
      }

      Rectangle var2 = new Rectangle(this.tileX * 32 + 11, this.tileY * 32 + 11, 10, 10);
      Iterator var3 = this.getTargets(var2).iterator();

      while(var3.hasNext()) {
         Mob var4 = (Mob)var3.next();
         if (var4.canBeHit(this) && var2.intersects(var4.getHitBox())) {
            this.hit(var4);
         }
      }

      this.over();
   }

   private void hit(Mob var1) {
      if (!this.hits.contains(var1.getUniqueID())) {
         if (var1.getLevel().isTrialRoom) {
            GameDamage var2 = new GameDamage(DamageTypeRegistry.TRUE, (float)var1.getMaxHealth() / 4.0F);
            var1.isServerHit(var2, 0.0F, 0.0F, 0.0F, this);
         } else {
            var1.isServerHit(damage, 0.0F, 0.0F, 0.0F, this);
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
      return new LocalMessage("deaths", "spiketrapname");
   }

   public DeathMessageTable getDeathMessages() {
      return this.getDeathMessages("spiketrap", 2);
   }

   public Mob getFirstAttackOwner() {
      return null;
   }

   public Collection<RegionPosition> getRegionPositions() {
      return Collections.singleton(this.level.regionManager.getRegionPosByTile(this.tileX, this.tileY));
   }
}
