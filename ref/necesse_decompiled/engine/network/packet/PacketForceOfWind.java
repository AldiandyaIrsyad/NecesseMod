package necesse.engine.network.packet;

import java.awt.Color;
import java.awt.geom.Point2D;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.registries.BuffRegistry;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.entity.mobs.Attacker;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.buffs.ActiveBuff;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.entity.particle.Particle;
import necesse.level.maps.Level;

public class PacketForceOfWind extends Packet {
   public final int slot;
   public final float dirX;
   public final float dirY;
   public final float strength;

   public PacketForceOfWind(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.dirX = var2.getNextFloat();
      this.dirY = var2.getNextFloat();
      this.strength = var2.getNextFloat();
   }

   public PacketForceOfWind(int var1, float var2, float var3, float var4) {
      this.slot = var1;
      this.dirX = var2;
      this.dirY = var3;
      this.strength = var4;
      PacketWriter var5 = new PacketWriter(this);
      var5.putNextByteUnsigned(var1);
      var5.putNextFloat(var2);
      var5.putNextFloat(var3);
      var5.putNextFloat(var4);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      if (var2.getLevel() != null) {
         ClientClient var3 = var2.getClient(this.slot);
         if (var3 != null && var3.isSamePlace(var2.getLevel())) {
            applyToPlayer(var3.playerMob.getLevel(), var3.playerMob, this.dirX, this.dirY, this.strength);
         } else {
            var2.network.sendPacket(new PacketRequestPlayerData(this.slot));
         }

      }
   }

   public static Point2D.Float getMobDir(Mob var0) {
      if (var0.moveX == 0.0F && var0.moveY == 0.0F) {
         if (var0.dir == 0) {
            return new Point2D.Float(0.0F, -1.0F);
         } else if (var0.dir == 1) {
            return new Point2D.Float(1.0F, 0.0F);
         } else if (var0.dir == 2) {
            return new Point2D.Float(0.0F, 1.0F);
         } else {
            return var0.dir == 3 ? new Point2D.Float(-1.0F, 0.0F) : new Point2D.Float(0.0F, 0.0F);
         }
      } else {
         return GameMath.normalize(var0.moveX, var0.moveY);
      }
   }

   public static boolean isOnCooldown(Mob var0) {
      return var0.buffManager.getStacks(BuffRegistry.Debuffs.DASH_COOLDOWN) >= (Integer)var0.buffManager.getModifier(BuffModifiers.DASH_STACKS);
   }

   public static void addCooldownStack(Mob var0, float var1, boolean var2) {
      var1 = var1 * (Float)var0.buffManager.getModifier(BuffModifiers.DASH_COOLDOWN) + (Float)var0.buffManager.getModifier(BuffModifiers.DASH_COOLDOWN_FLAT);
      var1 = Math.max(var1, 0.5F);
      float var3 = 0.0F;
      ActiveBuff var4 = var0.buffManager.getBuff(BuffRegistry.Debuffs.DASH_COOLDOWN);
      if (var4 != null) {
         var3 = (float)((ActiveBuff.BuffTime)var4.getStackTimes().getLast()).getDurationLeft() / 1000.0F;
      }

      ActiveBuff var5 = new ActiveBuff(BuffRegistry.Debuffs.DASH_COOLDOWN, var0, var3 + var1, (Attacker)null);
      var0.buffManager.addBuff(var5, var2);
   }

   public static void applyToPlayer(Level var0, Mob var1, float var2, float var3, float var4) {
      float var5 = var2 * var4;
      float var6 = var3 * var4;
      if (Math.abs(var1.dx) < Math.abs(var5)) {
         var1.dx = var5;
      }

      if (Math.abs(var1.dy) < Math.abs(var6)) {
         var1.dy = var6;
      }

      if (var0 != null && var0.isClient()) {
         for(int var7 = 0; var7 < 30; ++var7) {
            var0.entityManager.addParticle(var1.x + (float)GameRandom.globalRandom.nextGaussian() * 15.0F + var5 / 10.0F, var1.y + (float)GameRandom.globalRandom.nextGaussian() * 20.0F + var6 / 10.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(var5 * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 10.0F, var6 * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 10.0F).color(new Color(200, 200, 220)).height(18.0F).lifeTime(700);
         }
      }

   }
}
