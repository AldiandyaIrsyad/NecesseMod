package necesse.engine.network.packet;

import java.awt.Color;
import java.awt.Shape;
import java.awt.geom.Point2D;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.network.client.ClientClient;
import necesse.engine.util.GameMath;
import necesse.engine.util.GameRandom;
import necesse.engine.util.MovedRectangle;
import necesse.entity.mobs.Mob;
import necesse.entity.particle.Particle;
import necesse.level.maps.CollisionFilter;
import necesse.level.maps.Level;

public class PacketBlinkScepter extends Packet {
   public final int slot;
   public final float dirX;
   public final float dirY;
   public final float range;

   public PacketBlinkScepter(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.slot = var2.getNextByteUnsigned();
      this.dirX = var2.getNextFloat();
      this.dirY = var2.getNextFloat();
      this.range = var2.getNextFloat();
   }

   public PacketBlinkScepter(int var1, float var2, float var3, float var4) {
      this.slot = var1;
      this.dirX = var2;
      this.dirY = var3;
      this.range = var4;
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
            applyToPlayer(var3.playerMob.getLevel(), var3.playerMob, this.dirX, this.dirY, this.range);
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

   public static void applyToPlayer(Level var0, Mob var1, float var2, float var3, float var4) {
      if (var0 != null) {
         int var5 = var1.getX();
         int var6 = var1.getY();

         int var7;
         int var8;
         while(true) {
            var7 = var1.getX() + (int)(var2 * var4);
            var8 = var1.getY() + (int)(var3 * var4);
            MovedRectangle var9 = new MovedRectangle(var1, var1.getX(), var1.getY(), var7, var8);
            if (!var0.collides((Shape)var9, (CollisionFilter)var1.getLevelCollisionFilter())) {
               break;
            }

            var4 -= 4.0F;
            if (var4 <= 0.0F) {
               var7 = var1.getX();
               var8 = var1.getY();
               break;
            }
         }

         var1.setPos((float)var7, (float)var8, true);
         if (var0.isClient()) {
            int var10;
            if (var5 != var7 || var6 != var8) {
               for(var10 = 0; var10 < 30; ++var10) {
                  var0.entityManager.addParticle((float)var5 + (float)GameRandom.globalRandom.nextGaussian() * 10.0F + var1.dx / 2.0F, (float)var6 + (float)GameRandom.globalRandom.nextGaussian() * 15.0F + var1.dy / 2.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(var1.dx * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 10.0F, var1.dy * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 10.0F).color(new Color(236, 221, 197)).height(18.0F).lifeTime(700);
               }
            }

            for(var10 = 0; var10 < 30; ++var10) {
               var0.entityManager.addParticle(var1.x + (float)GameRandom.globalRandom.nextGaussian() * 10.0F + var1.dx / 2.0F, var1.y + (float)GameRandom.globalRandom.nextGaussian() * 15.0F + var1.dy / 2.0F, Particle.GType.IMPORTANT_COSMETIC).movesConstant(var1.dx * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 10.0F, var1.dy * GameRandom.globalRandom.getFloatBetween(0.8F, 1.2F) / 10.0F).color(new Color(236, 221, 197)).height(18.0F).lifeTime(700);
            }
         }

      }
   }
}
