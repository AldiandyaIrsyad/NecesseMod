package necesse.entity.mobs.mobMovement;

import java.awt.Rectangle;
import java.awt.geom.Point2D;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketRequestMobData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.Mob;

public class MobMovementCircleRelative extends MobMovementCircle {
   public int targetUniqueID;
   protected Mob targetMob;
   private long requestTargetMobTime;

   public MobMovementCircleRelative() {
   }

   public MobMovementCircleRelative(Mob var1, Mob var2, int var3, float var4, float var5, boolean var6) {
      super(var1, var3, var4, var5, var6);
      this.targetMob = var2;
      this.targetUniqueID = var2.getUniqueID();
   }

   public MobMovementCircleRelative(Mob var1, Mob var2, int var3, float var4, float var5, float var6, boolean var7) {
      super(var1, var5, var6, var3, var4, var7);
      this.targetMob = var2;
      this.targetUniqueID = var2.getUniqueID();
   }

   public MobMovementCircleRelative(Mob var1, Mob var2, int var3, float var4, boolean var5) {
      super(var1, var2.x, var2.y, var3, var4, var5);
      this.targetMob = var2;
      this.targetUniqueID = var2.getUniqueID();
   }

   public void setupPacket(Mob var1, PacketWriter var2) {
      var2.putNextInt(this.targetUniqueID);
      super.setupPacket(var1, var2);
   }

   public void applyPacket(Mob var1, PacketReader var2) {
      this.targetUniqueID = var2.getNextInt();
      super.applyPacket(var1, var2);
   }

   public Point2D.Float getCenterPos() {
      if (this.targetMob == null) {
         return null;
      } else {
         Rectangle var1 = this.targetMob.getSelectBox();
         return new Point2D.Float((float)var1.x + (float)var1.width / 2.0F, (float)var1.y + (float)var1.height / 2.0F);
      }
   }

   public boolean tick(Mob var1) {
      if (this.targetMob == null || this.targetMob.getUniqueID() != this.targetUniqueID) {
         this.targetMob = GameUtils.getLevelMob(this.targetUniqueID, var1.getLevel());
         if (this.targetMob == null && var1.isClient() && this.requestTargetMobTime < var1.getWorldEntity().getTime()) {
            var1.getLevel().getClient().network.sendPacket(new PacketRequestMobData(this.targetUniqueID));
            this.requestTargetMobTime = var1.getWorldEntity().getTime() + 1000L;
         }
      }

      return super.tick(var1);
   }

   public boolean equals(Object var1) {
      if (!(var1 instanceof MobMovementCircleRelative)) {
         return false;
      } else {
         MobMovementCircleRelative var2 = (MobMovementCircleRelative)var1;
         return this.targetUniqueID == var2.targetUniqueID && super.equals(var1);
      }
   }
}
