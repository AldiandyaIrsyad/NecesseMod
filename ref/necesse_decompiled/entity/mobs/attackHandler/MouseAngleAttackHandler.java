package necesse.entity.mobs.attackHandler;

import java.awt.geom.Point2D;
import necesse.engine.GlobalData;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.Input;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.util.GameMath;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.inventory.PlayerInventorySlot;

public abstract class MouseAngleAttackHandler extends AttackHandler {
   protected float currentAngle;
   protected int targetAngle;
   protected float speed;

   public MouseAngleAttackHandler(PlayerMob var1, PlayerInventorySlot var2, int var3, float var4, int var5, int var6) {
      super(var1, var2, var3);
      this.speed = var4;
      this.targetAngle = (int)GameMath.fixAngle(GameMath.getAngle(new Point2D.Float((float)var5 - var1.x, (float)var6 - var1.y)));
      this.currentAngle = (float)this.targetAngle;
   }

   public int getNextAngle(GameCamera var1) {
      if (Input.lastInputIsController && !ControllerInput.isCursorVisible()) {
         Point2D.Float var2 = this.player.getControllerAimDir();
         return (int)GameMath.fixAngle(GameMath.getAngle(var2));
      } else {
         return (int)GameMath.fixAngle(GameMath.getAngle(new Point2D.Float((float)var1.getMouseLevelPosX() - this.player.x, (float)var1.getMouseLevelPosY() - this.player.y)));
      }
   }

   protected void setupContentPacket(PacketWriter var1) {
      super.setupContentPacket(var1);
      GameCamera var2 = GlobalData.getCurrentState().getCamera();
      var1.putNextInt(this.getNextAngle(var2));
   }

   public void onPacketUpdate(PacketReader var1) {
      super.onPacketUpdate(var1);
      this.targetAngle = var1.getNextInt();
   }

   public void onUpdate() {
      if (this.player.isClient()) {
         GameCamera var1 = GlobalData.getCurrentState().getCamera();
         if (this.getNextAngle(var1) != this.targetAngle) {
            this.sendPacketUpdate(true);
         }
      }

      if (this.currentAngle != (float)this.targetAngle) {
         float var3 = GameMath.getAngleDifference(this.currentAngle, (float)this.targetAngle);
         float var2 = this.speed * (float)this.updateInterval / 250.0F;
         if (Math.abs(var3) < var2) {
            this.currentAngle = (float)this.targetAngle;
         } else if (var3 < 0.0F) {
            this.currentAngle = GameMath.fixAngle(this.currentAngle + var2);
            if (GameMath.getAngleDifference(this.currentAngle, (float)this.targetAngle) > 0.0F) {
               this.currentAngle = (float)this.targetAngle;
            }
         } else if (var3 > 0.0F) {
            this.currentAngle = GameMath.fixAngle(this.currentAngle - var2);
            if (GameMath.getAngleDifference(this.currentAngle, (float)this.targetAngle) < 0.0F) {
               this.currentAngle = (float)this.targetAngle;
            }
         }
      }

   }
}
