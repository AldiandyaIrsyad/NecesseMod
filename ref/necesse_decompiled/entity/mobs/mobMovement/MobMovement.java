package necesse.entity.mobs.mobMovement;

import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.EmptyConstructorGameRegistry;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.Mob;

public abstract class MobMovement implements IDDataContainer {
   public final IDData idData = new IDData();
   public static EmptyConstructorGameRegistry<MobMovement> registry = new EmptyConstructorGameRegistry<MobMovement>("MobMovement", 32767) {
      public void registerCore() {
         this.registerClass("constant", MobMovementConstant.class);
         this.registerClass("levelpos", MobMovementLevelPos.class);
         this.registerClass("relative", MobMovementRelative.class);
         this.registerClass("circlerelative", MobMovementCircleRelative.class);
         this.registerClass("circlelevel", MobMovementCircleLevelPos.class);
         this.registerClass("spiralrelative", MobMovementSpiralRelative.class);
         this.registerClass("spirallevel", MobMovementSpiralLevelPos.class);
      }

      protected void onRegistryClose() {
      }
   };

   public IDData getIDData() {
      return this.idData;
   }

   public MobMovement() {
      registry.applyIDData(this.getClass(), this.idData);
   }

   public abstract void setupPacket(Mob var1, PacketWriter var2);

   public abstract void applyPacket(Mob var1, PacketReader var2);

   public abstract boolean tick(Mob var1);

   public abstract boolean equals(Object var1);

   protected boolean moveTo(Mob var1, float var2, float var3, float var4) {
      float var5 = var1.getFriction();
      float var6 = var2 - var1.x;
      float var7 = var3 - var1.y;
      float var8 = var1.getDistance(var2, var3);
      if (var8 > var4) {
         Point2D.Float var9 = GameMath.normalize(var6, var7);
         var1.moveX = var9.x;
         var1.moveY = var9.y;
         if (Math.abs(var6) < (float)var1.stoppingDistance(var5, Math.abs(var1.dx)) / 2.0F) {
            var1.moveX = 0.0F;
         }

         if (Math.abs(var7) < (float)var1.stoppingDistance(var5, Math.abs(var1.dy)) / 2.0F) {
            var1.moveY = 0.0F;
         }

         return false;
      } else {
         return var8 <= var4;
      }
   }
}
