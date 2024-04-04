package necesse.gfx.forms.presets.debug.tools;

import java.util.Iterator;
import java.util.function.Function;
import necesse.engine.control.InputEvent;
import necesse.engine.control.MouseWheelBuffer;
import necesse.engine.network.packet.PacketMobDebugMove;
import necesse.entity.mobs.Mob;
import necesse.gfx.forms.presets.debug.DebugForm;

public class MoveMobDebugGameTool extends MouseDebugGameTool {
   private Mob selectedMob;
   private MouseWheelBuffer wheelBuffer = new MouseWheelBuffer(false);

   public MoveMobDebugGameTool(DebugForm var1) {
      super(var1, "Move mob");
      this.updateActions();
   }

   public void updateActions() {
      if (this.selectedMob != null && !this.selectedMob.removed()) {
         this.onLeftClick(this::teleportMob, "Move " + this.selectedMob.getDisplayName());
         this.onRightClick(this::clearMob, "Clear mob");
         this.onScroll(this::changeDir, "Change mob dir");
      } else {
         this.onLeftClick(this::selectMob, "Select mob");
         this.onRightClick((Function)null, (String)null);
         this.onScroll((Function)null, (String)null);
      }

   }

   private boolean selectMob(InputEvent var1) {
      int var2 = this.getMouseX();
      int var3 = this.getMouseY();
      Iterator var4 = this.parent.client.getLevel().entityManager.mobs.getInRegionRangeByTile(var2 / 32, var3 / 32, 1).iterator();

      while(var4.hasNext()) {
         Mob var5 = (Mob)var4.next();
         if (var5.getSelectBox().contains(var2, var3)) {
            this.selectedMob = var5;
            this.updateActions();
            break;
         }
      }

      return true;
   }

   private boolean clearMob(InputEvent var1) {
      this.selectedMob = null;
      this.updateActions();
      return true;
   }

   private boolean changeDir(InputEvent var1) {
      this.wheelBuffer.add(var1);
      this.wheelBuffer.useScrollY((var1x) -> {
         if (this.selectedMob != null && !this.selectedMob.removed()) {
            int var2 = this.selectedMob.dir + (var1x ? 1 : -1);
            if (var2 > 3) {
               var2 = 0;
            } else if (var2 < 0) {
               var2 = 3;
            }

            this.parent.client.network.sendPacket(new PacketMobDebugMove(this.selectedMob, this.selectedMob.getX(), this.selectedMob.getY(), var2));
         } else {
            this.selectedMob = null;
            this.updateActions();
         }

      });
      return true;
   }

   private boolean teleportMob(InputEvent var1) {
      if (this.selectedMob != null && !this.selectedMob.removed()) {
         this.parent.client.network.sendPacket(new PacketMobDebugMove(this.selectedMob, this.getMouseX(), this.getMouseY(), this.selectedMob.dir));
      } else {
         this.selectedMob = null;
         this.updateActions();
      }

      return true;
   }

   public void init() {
      this.selectedMob = null;
   }
}
