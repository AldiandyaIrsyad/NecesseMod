package necesse.gfx.forms.presets.debug.tools;

import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.level.gameObject.GameObject;
import necesse.level.gameTile.GameTile;

public class TileInfoGameTool extends MouseDebugGameTool {
   public TileInfoGameTool(DebugForm var1) {
      super(var1, "Tile info");
      this.onLeftClick((var2) -> {
         GameTile var3 = this.getLevel().getTile(this.getMouseTileX(), this.getMouseTileY());
         if (var3 != null) {
            var1.client.chat.addMessage("Tile (" + this.getMouseTileX() + ", " + this.getMouseTileY() + "):");
            var1.client.chat.addMessage("Name: " + var3.getDisplayName());
            var1.client.chat.addMessage("ID: " + var3.getID());
            var1.client.chat.addMessage("NameID: " + var3.getStringID());
            var1.client.chat.addMessage("Health: " + var3.tileHealth);
            var1.client.chat.addMessage("ToolTier: " + var3.toolTier);
         } else {
            var1.client.chat.addMessage("Tile (" + this.getMouseTileX() + ", " + this.getMouseTileY() + ") is N/A");
         }

         return true;
      }, "Get tile info");
      this.onRightClick((var2) -> {
         GameObject var3 = this.getLevel().getObject(this.getMouseTileX(), this.getMouseTileY());
         if (var3 != null) {
            var1.client.chat.addMessage("Object (" + this.getMouseTileX() + ", " + this.getMouseTileY() + "):");
            var1.client.chat.addMessage("Rotation: " + this.getLevel().getObjectRotation(this.getMouseTileX(), this.getMouseTileY()));
            var1.client.chat.addMessage("Name: " + var3.getDisplayName());
            var1.client.chat.addMessage("ID: " + var3.getID());
            var1.client.chat.addMessage("NameID: " + var3.getStringID());
            var1.client.chat.addMessage("Health: " + var3.objectHealth);
            var1.client.chat.addMessage("ToolTier: " + var3.toolTier);
         } else {
            var1.client.chat.addMessage("Object (" + this.getMouseTileX() + ", " + this.getMouseTileY() + ") is N/A");
         }

         return true;
      }, "Get object info");
   }

   public void init() {
   }
}
