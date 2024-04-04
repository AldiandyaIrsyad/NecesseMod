package necesse.gfx.forms.presets.debug.tools;

import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.gameTool.GameTool;
import necesse.engine.network.server.ServerClient;
import necesse.gfx.forms.presets.debug.DebugForm;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.level.maps.Level;

public abstract class DebugGameTool implements GameTool {
   public final String name;
   public final DebugForm parent;

   public DebugGameTool(DebugForm var1, String var2) {
      this.parent = var1;
      this.name = var2;
   }

   public abstract void init();

   public abstract boolean inputEvent(InputEvent var1);

   public void isCancelled() {
   }

   public void isCleared() {
   }

   public GameTooltips getTooltips() {
      return this.name == null ? null : new StringTooltips(this.name);
   }

   public Level getLevel() {
      return this.parent.client.getLevel();
   }

   public Level getServerLevel() {
      if (this.parent.client.getLocalServer() == null) {
         return null;
      } else {
         ServerClient var1 = this.parent.client.getLocalServer().getLocalServerClient();
         return this.parent.client.getLocalServer().world.getLevel(var1.getLevelIdentifier());
      }
   }

   public int getMouseX() {
      return Screen.mousePos().sceneX + this.parent.mainGame.getCamera().getX();
   }

   public int getMouseTileX() {
      return this.getMouseX() / 32;
   }

   public int getMouseY() {
      return Screen.mousePos().sceneY + this.parent.mainGame.getCamera().getY();
   }

   public int getMouseTileY() {
      return this.getMouseY() / 32;
   }
}
