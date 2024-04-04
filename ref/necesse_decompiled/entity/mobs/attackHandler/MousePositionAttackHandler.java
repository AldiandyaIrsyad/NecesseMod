package necesse.entity.mobs.attackHandler;

import java.awt.Point;
import java.awt.geom.Point2D;
import necesse.engine.GlobalData;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.PlayerInventorySlot;

public abstract class MousePositionAttackHandler extends AttackHandler {
   protected int lastX;
   protected int lastY;

   public MousePositionAttackHandler(PlayerMob var1, PlayerInventorySlot var2, int var3) {
      super(var1, var2, var3);
   }

   public Point getNextLevelPos(GameCamera var1) {
      if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
         Point2D.Float var2 = this.player.getControllerAimDir();
         return this.item.item.getControllerAttackLevelPos(this.player.getLevel(), var2.x, var2.y, this.player, this.item);
      } else {
         return new Point(var1.getMouseLevelPosX(), var1.getMouseLevelPosY());
      }
   }

   protected void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      GameCamera var2 = GlobalData.getCurrentState().getCamera();
      Point var3 = this.getNextLevelPos(var2);
      var1.putNextInt(var3.x);
      var1.putNextInt(var3.y);
   }

   public void onPacketUpdate(PacketReader var1) {
      super.onPacketUpdate(var1);
      this.lastX = var1.getNextInt();
      this.lastY = var1.getNextInt();
   }

   public void onUpdate() {
      if (this.player.isClient()) {
         GameCamera var1 = GlobalData.getCurrentState().getCamera();
         Point var2 = this.getNextLevelPos(var1);
         if (var2.x != this.lastX || var2.y != this.lastY) {
            this.sendPacketUpdate(true);
         }
      }

   }
}
