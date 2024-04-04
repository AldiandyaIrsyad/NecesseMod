package necesse.entity.mobs.attackHandler;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.util.Iterator;
import necesse.engine.Screen;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlayerStopAttack;
import necesse.engine.network.packet.PacketShowAttack;
import necesse.engine.network.packet.PacketShowAttackOnlyItem;
import necesse.engine.network.server.Server;
import necesse.engine.network.server.ServerClient;
import necesse.engine.sound.SoundEffect;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.Entity;
import necesse.entity.ParticleTypeSwitcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.particle.Particle;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.toolItem.projectileToolItem.bowProjectileToolItem.greatbowProjectileToolItem.GreatbowProjectileToolItem;

public class GreatbowAttackHandler extends MousePositionAttackHandler {
   public int chargeTime;
   public boolean fullyCharged;
   public GreatbowProjectileToolItem toolItem;
   public long startTime;
   public InventoryItem item;
   public int seed;
   public Color particleColors;
   public boolean endedByInteract;

   public GreatbowAttackHandler(PlayerMob var1, PlayerInventorySlot var2, InventoryItem var3, GreatbowProjectileToolItem var4, int var5, Color var6, int var7) {
      super(var1, var2, 20);
      this.item = var3;
      this.toolItem = var4;
      this.chargeTime = var5;
      this.particleColors = var6;
      this.seed = var7;
      this.startTime = var1.getWorldEntity().getLocalTime();
   }

   public long getTimeSinceStart() {
      return this.player.getWorldEntity().getLocalTime() - this.startTime;
   }

   public float getChargePercent() {
      return (float)this.getTimeSinceStart() / (float)this.chargeTime;
   }

   public void onUpdate() {
      super.onUpdate();
      Point2D.Float var1 = GameMath.normalize((float)this.lastX - this.player.x, (float)this.lastY - this.player.y);
      float var2 = this.getChargePercent();
      InventoryItem var3 = this.item.copy();
      var3.getGndData().setFloat("chargePercent", var2);
      Packet var4 = new Packet();
      this.player.showAttack(var3, this.lastX, this.lastY, this.seed, var4);
      if (this.player.isServer()) {
         ServerClient var5 = this.player.getServerClient();
         this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketShowAttack(this.player, var3, this.lastX, this.lastY, this.seed, var4), (ServerClient)var5, var5);
      }

      if (var2 >= 1.0F) {
         if (this.player.isClient()) {
            this.player.getLevel().entityManager.addParticle(this.player.x + var1.x * 16.0F + (float)(GameRandom.globalRandom.nextGaussian() * 6.0), this.player.y + 4.0F + (float)(GameRandom.globalRandom.nextGaussian() * 8.0), Particle.GType.IMPORTANT_COSMETIC).movesConstant(this.player.dx / 10.0F, this.player.dy / 10.0F).color(this.particleColors).height(20.0F - var1.y * 16.0F);
         }

         if (!this.fullyCharged) {
            this.fullyCharged = true;
            if (this.player.isClient()) {
               byte var12 = 35;
               float var6 = 360.0F / (float)var12;
               ParticleTypeSwitcher var7 = new ParticleTypeSwitcher(new Particle.GType[]{Particle.GType.CRITICAL, Particle.GType.IMPORTANT_COSMETIC, Particle.GType.COSMETIC});

               for(int var8 = 0; var8 < var12; ++var8) {
                  int var9 = (int)((float)var8 * var6 + GameRandom.globalRandom.nextFloat() * var6);
                  float var10 = (float)Math.sin(Math.toRadians((double)var9)) * (float)GameRandom.globalRandom.getIntBetween(30, 50);
                  float var11 = (float)Math.cos(Math.toRadians((double)var9)) * (float)GameRandom.globalRandom.getIntBetween(30, 50) * 0.8F;
                  this.player.getLevel().entityManager.addParticle((Entity)this.player, var7.next()).movesFriction(var10, var11, 0.8F).color(this.particleColors).heightMoves(0.0F, 30.0F).lifeTime(500);
               }

               Screen.playSound(GameResources.magicbolt4, SoundEffect.effect(this.player).volume(0.1F).pitch(2.5F));
            }
         }
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
      float var2 = this.getChargePercent();
      if (!this.endedByInteract && (this.getTimeSinceStart() >= 200L || var2 >= 0.5F)) {
         this.player.constantAttack = true;
         GameMath.normalize((float)this.lastX - this.player.x, (float)this.lastY - this.player.y);
         InventoryItem var4 = this.item.copy();
         var4.getGndData().setBoolean("shouldFire", true);
         var4.getGndData().setFloat("chargePercent", var2);
         Packet var5 = new Packet();
         PacketWriter var6 = new PacketWriter(var5);
         this.toolItem.setupAttackContentPacket(var6, this.player.getLevel(), this.lastX, this.lastY, this.player, var4);
         var4.item.showAttack(this.player.getLevel(), this.lastX, this.lastY, this.player, this.player.getCurrentAttackHeight(), var4, this.seed, new PacketReader(var5));
         this.toolItem.superOnAttack(this.player.getLevel(), this.lastX, this.lastY, this.player, this.player.getCurrentAttackHeight(), var4, this.slot, 0, this.seed, new PacketReader(var5));
         Iterator var7 = this.player.buffManager.getArrayBuffs().iterator();

         while(var7.hasNext()) {
            ActiveBuff var8 = (ActiveBuff)var7.next();
            var8.onItemAttacked(this.lastX, this.lastY, this.player, this.player.getCurrentAttackHeight(), var4, this.slot, 0);
         }

         if (this.player.isServer()) {
            ServerClient var9 = this.player.getServerClient();
            Server var10 = this.player.getLevel().getServer();
            var10.network.sendToClientsAtExcept(new PacketShowAttackOnlyItem(this.player, var4, this.lastX, this.lastY, this.seed, var5), (ServerClient)var9, var9);
         }
      }

      this.player.stopAttack();
      if (this.player.isServer()) {
         ServerClient var3 = this.player.getServerClient();
         this.player.getLevel().getServer().network.sendToClientsAtExcept(new PacketPlayerStopAttack(var3.slot), (ServerClient)var3, var3);
      }

   }
}
