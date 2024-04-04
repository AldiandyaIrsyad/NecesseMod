package necesse.gfx.forms.presets.debug.tools;

import java.util.List;
import necesse.engine.Screen;
import necesse.engine.network.packet.PacketChangeTile;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.gameTile.GameTile;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;

public class PlaceTileGameTool extends MouseDebugGameTool {
   public GameTile tile;
   public HudDrawElement hudDrawElement;

   public PlaceTileGameTool(DebugForm var1, GameTile var2) {
      super(var1, (String)null);
      this.tile = var2;
   }

   public void init() {
      if (this.hudDrawElement != null) {
         this.hudDrawElement.remove();
      }

      this.getLevel().hudManager.addElement(this.hudDrawElement = new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1, final GameCamera var2, PlayerMob var3) {
            final int var4 = PlaceTileGameTool.this.getMouseTileX();
            final int var5 = PlaceTileGameTool.this.getMouseTileY();
            final PlayerMob var6 = PlaceTileGameTool.this.parent.client.getPlayer();
            final Level var7 = this.getLevel();
            if (this.getLevel().getTile(var4, var5) != PlaceTileGameTool.this.tile) {
               var1.add(new SortedDrawable() {
                  public int getPriority() {
                     return -100000;
                  }

                  public void draw(TickManager var1) {
                     PlaceTileGameTool.this.tile.drawPreview(var7, var4, var5, 0.5F, var6, var2);
                  }
               });
            }

         }
      });
      this.onLeftClick((var1) -> {
         int var2 = this.getMouseTileX();
         int var3 = this.getMouseTileY();
         if (this.getLevel().getTile(var2, var3) != this.tile) {
            this.parent.client.network.sendPacket(new PacketChangeTile(this.getLevel(), var2, var3, this.tile.getID()));
         }

         return true;
      }, "Place tile");
      this.updatePlaceUsage();
      this.onRightClick((var1) -> {
         int var2 = this.getMouseTileX();
         int var3 = this.getMouseTileY();
         this.tile = this.getLevel().getTile(var2, var3);
         this.updatePlaceUsage();
         return true;
      }, "Select tile");
      this.onMouseMove((var1) -> {
         if (Screen.isKeyDown(-100)) {
            int var2 = this.getMouseTileX();
            int var3 = this.getMouseTileY();
            if (this.getLevel().getTile(var2, var3) != this.tile) {
               this.parent.client.network.sendPacket(new PacketChangeTile(this.getLevel(), var2, var3, this.tile.getID()));
            }

            return true;
         } else {
            return false;
         }
      });
   }

   public void updatePlaceUsage() {
      this.keyUsages.put(-100, "Place " + this.tile.getDisplayName());
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
