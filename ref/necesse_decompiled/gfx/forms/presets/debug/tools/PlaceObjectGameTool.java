package necesse.gfx.forms.presets.debug.tools;

import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.packet.PacketChangeObject;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class PlaceObjectGameTool extends MouseDebugGameTool {
   public GameObject object;
   public HudDrawElement hudDrawElement;

   public PlaceObjectGameTool(DebugForm var1, GameObject var2) {
      super(var1, (String)null);
      this.object = var2;
   }

   public void init() {
      if (this.hudDrawElement != null) {
         this.hudDrawElement.remove();
      }

      this.getLevel().hudManager.addElement(this.hudDrawElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, PlayerMob var3) {
            final int var4 = PlaceObjectGameTool.this.getMouseTileX();
            final int var5 = PlaceObjectGameTool.this.getMouseTileY();
            final PlayerMob var6 = PlaceObjectGameTool.this.parent.client.getPlayer();
            final Level var7 = this.getLevel();
            final int var8 = PlaceObjectGameTool.this.object.getPlaceRotation(var7, PlaceObjectGameTool.this.getMouseX(), PlaceObjectGameTool.this.getMouseY(), var6, var6.isAttacking ? var6.beforeAttackDir : var6.dir);
            if (var7.getObject(var4, var5) != PlaceObjectGameTool.this.object || var7.getObjectRotation(var4, var5) != var8) {
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return -100000;
                  }

                  public void draw(TickManager var1) {
                     PlaceObjectGameTool.this.object.drawPreview(var7, var4, var5, var8, 0.5F, var6, var2);
                  }
               });
            }

         }
      });
      this.onLeftClick((var1) -> {
         int var2 = this.getMouseTileX();
         int var3 = this.getMouseTileY();
         PlayerMob var4 = this.parent.client.getPlayer();
         int var5 = this.object.getPlaceRotation(this.getLevel(), this.getMouseX(), this.getMouseY(), var4, var4.isAttacking ? var4.beforeAttackDir : var4.dir);
         if (this.getLevel().getObject(var2, var3) != this.object || this.getLevel().getObjectRotation(var2, var3) != var5) {
            this.parent.client.network.sendPacket(new PacketChangeObject(this.getLevel(), var2, var3, this.object.getID(), var5));
         }

         return true;
      }, "Place object");
      this.updatePlaceUsage();
      this.onRightClick((var1) -> {
         int var2 = this.getMouseTileX();
         int var3 = this.getMouseTileY();
         this.object = this.getLevel().getObject(var2, var3);
         this.updatePlaceUsage();
         return true;
      }, "Select object");
      this.onMouseMove((var1) -> {
         if (Screen.isKeyDown(-100)) {
            int var2 = this.getMouseTileX();
            int var3 = this.getMouseTileY();
            PlayerMob var4 = this.parent.client.getPlayer();
            int var5 = this.object.getPlaceRotation(this.getLevel(), this.getMouseX(), this.getMouseY(), var4, var4.isAttacking ? var4.beforeAttackDir : var4.dir);
            if (this.getLevel().getObject(var2, var3) != this.object || this.getLevel().getObjectRotation(var2, var3) != var5) {
               this.parent.client.network.sendPacket(new PacketChangeObject(this.getLevel(), var2, var3, this.object.getID(), var5));
            }

            return true;
         } else {
            return false;
         }
      });
   }

   public void updatePlaceUsage() {
      this.keyUsages.put(-100, "Place " + this.object.getDisplayName());
   }

   public void isCancelled() {
      super.isCancelled();
      if (this.hudDrawElement != null) {
         this.hudDrawElement.remove();
      }

   }

   public void isCleared() {
      super.isCleared();
      if (this.hudDrawElement != null) {
         this.hudDrawElement.remove();
      }

   }
}
