package necesse.gfx.forms.presets.debug;

import necesse.engine.Screen;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.presets.debug.tools.PlaceObjectGameTool;
import necesse.gfx.forms.presets.debug.tools.PlaceTileGameTool;
import necesse.gfx.gameFont.FontOptions;

public class DebugWorldForm extends Form {
   public final DebugForm parent;

   public DebugWorldForm(String var1, DebugForm var2) {
      super((String)var1, 160, 280);
      this.parent = var2;
      this.addComponent(new FormLabel("World", new FontOptions(20), 0, this.getWidth() / 2, 10));
      ((FormTextButton)this.addComponent(new FormTextButton("Tiles", 0, 40, this.getWidth()))).onClicked((var1x) -> {
         var2.tiles.tileList.populateIfNotAlready();
         var2.makeCurrent(var2.tiles);
         PlaceTileGameTool var2x = new PlaceTileGameTool(var2, TileRegistry.getTile(TileRegistry.waterID));
         Screen.clearGameTools(var2);
         Screen.setGameTool(var2x, var2);
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Objects", 0, 80, this.getWidth()))).onClicked((var1x) -> {
         var2.objects.objectList.populateIfNotAlready();
         var2.makeCurrent(var2.objects);
         PlaceObjectGameTool var2x = new PlaceObjectGameTool(var2, ObjectRegistry.getObject(0));
         Screen.clearGameTools(var2);
         Screen.setGameTool(var2x, var2);
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Wires", 0, 120, this.getWidth()))).onClicked((var1x) -> {
         var2.makeCurrent(var2.wire);
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Time", 0, 160, this.getWidth()))).onClicked((var1x) -> {
         var2.makeCurrent(var2.time);
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Toggle rain", 0, 200, this.getWidth()))).onClicked((var1x) -> {
         String var2x = var2.client.getLevel().rainingLayer.isRaining() ? "clear" : "start";
         var2.client.network.sendPacket(new PacketChatMessage(var2.client.getSlot(), "/rain " + var2x));
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Back", 0, 240, this.getWidth()))).onClicked((var1x) -> {
         var2.makeCurrent(var2.mainMenu);
      });
   }
}
