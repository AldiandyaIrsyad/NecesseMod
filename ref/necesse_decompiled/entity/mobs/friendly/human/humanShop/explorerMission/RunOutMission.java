package necesse.entity.mobs.friendly.human.humanShop.explorerMission;

import java.awt.Point;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.friendly.human.MoveToPoint;
import necesse.gfx.gameTooltips.ListGameTooltips;

public abstract class RunOutMission extends SettlerMission {
   protected boolean isOut;
   protected Point moveOutPoint;

   public RunOutMission() {
   }

   public void start(HumanMob var1) {
      this.isOut = false;
      this.moveOutPoint = var1.getNewEdgeOfMapPoint();
   }

   public void addSaveData(HumanMob var1, SaveData var2) {
      var2.addBoolean("isOut", this.isOut);
      var2.addPoint("moveOutPoint", this.moveOutPoint);
   }

   public void applySaveData(HumanMob var1, LoadData var2) {
      this.isOut = var2.getBoolean("isOut", this.isOut);
      this.moveOutPoint = var2.getPoint("moveOutPoint", this.moveOutPoint);
      if (this.moveOutPoint == null) {
         this.moveOutPoint = var1.getNewEdgeOfMapPoint();
      }

   }

   public void setupMovementPacket(HumanMob var1, PacketWriter var2) {
      var2.putNextBoolean(this.isOut);
   }

   public void applyMovementPacket(HumanMob var1, PacketReader var2) {
      this.isOut = var2.getNextBoolean();
   }

   public MoveToPoint getMoveOutPoint(final HumanMob var1) {
      if (this.isOut) {
         return null;
      } else if (var1.isAtEdgeOfMap(5)) {
         this.isOut = true;
         var1.sendMovementPacket(true);
         return null;
      } else {
         return new MoveToPoint(this.moveOutPoint, false) {
            public boolean moveIfPathFailed(float var1x) {
               return var1x >= 30.0F;
            }

            public boolean isAtLocation(float var1x, boolean var2) {
               if (var2) {
                  return var1x < 2.0F;
               } else {
                  return var1x < 30.0F;
               }
            }

            public void onAtLocation() {
               RunOutMission.this.isOut = true;
               var1.sendMovementPacket(true);
            }
         };
      }
   }

   public boolean isMobVisible(HumanMob var1) {
      return !this.isOut;
   }

   public boolean isMobIdle(HumanMob var1) {
      return this.isOut;
   }

   public void addDebugTooltips(ListGameTooltips var1) {
      super.addDebugTooltips(var1);
      var1.add("isOut: " + this.isOut);
      if (this.moveOutPoint != null) {
         var1.add("moveOutPoint: [" + this.moveOutPoint.x + ", " + this.moveOutPoint.y + "]");
      }

   }
}
