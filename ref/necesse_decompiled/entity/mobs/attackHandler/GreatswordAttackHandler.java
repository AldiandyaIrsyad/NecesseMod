package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.Iterator;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.server.ServerClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.toolItem.swordToolItem.greatswordToolItem.GreatswordToolItem;

public class GreatswordAttackHandler extends MouseAngleAttackHandler {
   public GreatswordToolItem toolItem;
   public InventoryItem item;
   public int seed;
   public long startTime;
   public GreatswordChargeLevel[] chargeLevels;
   public int chargeTimeRemaining;
   public int currentChargeLevel;
   public int timeSpentUpToCurrentChargeLevel;
   public boolean endedByInteract;

   public GreatswordAttackHandler(PlayerMob var1, PlayerInventorySlot var2, InventoryItem var3, GreatswordToolItem var4, int var5, int var6, int var7, GreatswordChargeLevel... var8) {
      super(var1, var2, 20, 1000.0F, var6, var7);
      this.toolItem = var4;
      this.item = var3;
      this.seed = var5;
      this.chargeLevels = var8;
      this.currentChargeLevel = -1;
      this.chargeTimeRemaining = Arrays.stream(var8).mapToInt((var0) -> {
         return var0.timeToCharge;
      }).sum();
      this.timeSpentUpToCurrentChargeLevel = 0;
      if (var8.length == 0) {
         throw new IllegalArgumentException("Must have at least one charge level for greatswords");
      } else {
         this.startTime = var1.getLevel().getWorldEntity().getLocalTime();
      }
   }

   public long getTimeSinceStart() {
      return this.player.getWorldEntity().getLocalTime() - this.startTime;
   }

   public void updateCurrentChargeLevel() {
      while(true) {
         if (this.currentChargeLevel < this.chargeLevels.length - 1) {
            long var1 = this.getTimeSinceStart();
            long var3 = var1 - (long)this.timeSpentUpToCurrentChargeLevel;
            GreatswordChargeLevel var5 = this.chargeLevels[this.currentChargeLevel + 1];
            long var6 = (long)Math.round((float)var5.timeToCharge * (1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player)));
            if (var3 >= var6) {
               this.timeSpentUpToCurrentChargeLevel = (int)((long)this.timeSpentUpToCurrentChargeLevel + var6);
               this.chargeTimeRemaining -= var5.timeToCharge;
               ++this.currentChargeLevel;
               var5.onReachedLevel(this);
               continue;
            }
         }

         return;
      }
   }

   public float getChargePercent() {
      int var1 = this.timeSpentUpToCurrentChargeLevel + Math.round((float)this.chargeTimeRemaining * (1.0F / this.toolItem.getAttackSpeedModifier(this.item, this.player)));
      return (float)Math.min(this.getTimeSinceStart(), (long)var1) / (float)var1;
   }

   public void onUpdate() {
      super.onUpdate();
      this.updateCurrentChargeLevel();
      Point2D.Float var1 = GameMath.getAngleDir(this.currentAngle);
      int var2 = this.player.getX() + (int)(var1.x * 100.0F);
      int var3 = this.player.getY() + (int)(var1.y * 100.0F);
      float var4 = this.getChargePercent();
      Packet var5 = new Packet();
      InventoryItem var6 = this.item.copy();
      var6.getGndData().setFloat("chargePercent", var4);
      var6.getGndData().setBoolean("IsCharging", true);
      this.toolItem.setupAttackContentPacket(new PacketWriter(var5), this.player.getLevel(), var2, var3, this.player, var6);
      this.player.showAttack(var6, var2, var3, this.seed, var5);
      if (this.player.isServer()) {
         ServerClient var7 = this.player.getServerClient();
         this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowAttack(this.player, var6, var2, var3, this.seed, var5), (ServerClient)var7, var7);
      }

      if (this.currentChargeLevel >= 0) {
         this.chargeLevels[this.currentChargeLevel].updateAtLevel(this, var6);
      }

   }

   public void drawWeaponParticles(InventoryItem var1, Color var2) {
      float var3 = var1.getGndData().getFloat("chargePercent");
      float var4 = this.toolItem.getSwingRotation(var1, this.player.dir, var3);
      int var5 = this.player.dir;
      byte var6 = 0;
      byte var7 = 0;
      if (var5 == 0) {
         var4 = -var4 - 90.0F;
         var7 = -8;
      } else if (var5 == 1) {
         var4 = -var4 + 180.0F + 45.0F;
         var6 = 8;
      } else if (var5 == 2) {
         var4 = -var4 + 90.0F;
         var7 = 12;
      } else {
         var4 = var4 + 90.0F + 45.0F;
         var6 = -8;
      }

      float var8 = GameMath.sin(var4);
      float var9 = GameMath.cos(var4);
      int var10 = GameRandom.globalRandom.getIntBetween(0, this.toolItem.getAttackRange(this.item));
      this.player.getLevel().entityManager.addParticle(this.player.x + (float)var6 + var8 * (float)var10 + GameRandom.globalRandom.floatGaussian() * 3.0F, this.player.y + 4.0F + GameRandom.globalRandom.floatGaussian() * 4.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.player.dx, this.player.dy).color(var2).height(20.0F - var9 * (float)var10 - (float)var7);
   }

   public void drawParticleExplosion(int var1, Color var2, int var3, int var4) {
      ParticleTypeSwitcher var5 = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});
      float var6 = 360.0F / (float)var1;

      for(int var7 = 0; var7 < var1; ++var7) {
         int var8 = (int)((float)var7 * var6 + GameRandom.globalRandom.nextFloat() * var6);
         float var9 = (float)Math.sin(Math.toRadians((double)var8)) * (float)GameRandom.globalRandom.getIntBetween(var3, var4);
         float var10 = (float)Math.cos(Math.toRadians((double)var8)) * (float)GameRandom.globalRandom.getIntBetween(var3, var4) * 0.8F;
         this.player.getLevel().entityManager.addParticle((Entity)this.player, var5.next()).movesFriction(var9, var10, 0.8F).color(var2).heightMoves(0.0F, 30.0F).lifeTime(500);
      }

   }

   public void onMouseInteracted(int var1, int var2) {
      this.endedByInteract = true;
      this.player.endAttackHandler(false);
   }

   public void onControllerInteracted(float var1, float var2) {
      this.endedByInteract = true;
      this.player.endAttackHandler(false);
   }

   public void onEndAttack(boolean var1) {
      this.updateCurrentChargeLevel();
      if (this.currentChargeLevel >= 0 && !this.endedByInteract) {
         this.player.constantAttack = true;
         Point2D.Float var10 = GameMath.getAngleDir(this.currentAngle);
         int var3 = this.player.getX() + (int)(var10.x * 100.0F);
         int var4 = this.player.getY() + (int)(var10.y * 100.0F);
         Packet var5 = new Packet();
         InventoryItem var6 = this.item.copy();
         GreatswordChargeLevel var7 = this.chargeLevels[this.currentChargeLevel];
         var6.getGndData().setBoolean("shouldFire", true);
         var6.getGndData().setInt("cooldown", this.toolItem.getAttackAnimTime(var6, this.player) + 100);
         var7.setupAttackItem(this, var6);
         this.toolItem.setupAttackContentPacket(new PacketWriter(var5), this.player.getLevel(), var3, var4, this.player, var6);
         this.player.showAttack(var6, var3, var4, this.seed, var5);
         if (this.player.isServer()) {
            ServerClient var8 = this.player.getServerClient();
            this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowAttack(this.player, var6, var3, var4, this.seed, var5), (ServerClient)var8, var8);
         }

         this.toolItem.superOnAttack(this.player.getLevel(), var3, var4, this.player, this.player.getCurrentAttackHeight(), var6, this.slot, 0, this.seed, new PacketReader(var5));
         Iterator var11 = this.player.buffManager.getArrayBuffs().iterator();

         while(var11.hasNext()) {
            ActiveBuff var9 = (ActiveBuff)var11.next();
            var9.onItemAttacked(var3, var4, this.player, this.player.getCurrentAttackHeight(), var6, this.slot, 0);
         }
      } else {
         this.player.stopAttack();
         if (this.player.isServer()) {
            ServerClient var2 = this.player.getServerClient();
            this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketPlayerStopAttack(var2.slot), (ServerClient)var2, var2);
         }
      }

   }
}
