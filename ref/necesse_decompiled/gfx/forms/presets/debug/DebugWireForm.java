package necesse.gfx.forms.presets.debug;

import necesse.engine.Screen;
import necesse.engine.network.packet.PacketChangeWire;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.presets.debug.tools.MouseDebugGameTool;
import necesse.gfx.gameFont.FontOptions;
import necesse.level.maps.Level;

public class DebugWireForm extends Form {
   public FormLocalCheckBox[] wires;
   public final DebugForm parent;

   public DebugWireForm(String var1, DebugForm var2) {
      super((String)var1, 160, 200);
      this.parent = var2;
      ((FormTextButton)this.addComponent(new FormTextButton("Back", 0, this.getHeight() - 40, this.getWidth()))).onClicked((var1x) -> {
         var2.makeCurrent(var2.world);
      });
      this.addComponent(new FormLabel("Wires", new FontOptions(20), 0, this.getWidth() / 2, 10));
      this.wires = new FormLocalCheckBox[4];

      for(int var3 = 0; var3 < 4; ++var3) {
         this.wires[var3] = (FormLocalCheckBox)this.addComponent(new FormLocalCheckBox("ui", "wire" + var3, 10, 40 + var3 * 20));
         this.wires[var3].handleClicksIfNoEventHandlers = true;
      }

      ((FormTextButton)this.addComponent(new FormTextButton("Start tool", 0, this.getHeight() - 80, this.getWidth()))).onClicked((var2x) -> {
         MouseDebugGameTool var3 = new MouseDebugGameTool(var2, (String)null) {
            public void init() {
               this.onLeftClick((var1) -> {
                  int var2 = this.getMouseTileX();
                  int var3 = this.getMouseTileY();
                  Level var4 = this.parent.client.getLevel();
                  boolean var5 = false;

                  for(int var6 = 0; var6 < 4; ++var6) {
                     if (DebugWireForm.this.wires[var6].checked && !var4.wireManager.hasWire(var2, var3, var6)) {
                        var4.wireManager.setWire(var2, var3, var6, true);
                        var5 = true;
                     }
                  }

                  if (var5) {
                     this.parent.client.network.sendPacket(new PacketChangeWire(var4, var2, var3, var4.wireManager.getWireData(var2, var3)));
                  }

                  return true;
               }, "Place wire");
               this.onRightClick((var1) -> {
                  int var2 = this.getMouseTileX();
                  int var3 = this.getMouseTileY();
                  Level var4 = this.parent.client.getLevel();
                  boolean var5 = false;

                  for(int var6 = 0; var6 < 4; ++var6) {
                     if (DebugWireForm.this.wires[var6].checked && var4.wireManager.hasWire(var2, var3, var6)) {
                        var4.wireManager.setWire(var2, var3, var6, false);
                        var5 = true;
                     }
                  }

                  if (var5) {
                     this.parent.client.network.sendPacket(new PacketChangeWire(var4, var2, var3, var4.wireManager.getWireData(var2, var3)));
                  }

                  return true;
               }, "Remove wire");
               this.onMouseMove((var1) -> {
                  int var2;
                  int var3;
                  Level var4;
                  boolean var5;
                  int var6;
                  if (Screen.isKeyDown(-100)) {
                     var2 = this.getMouseTileX();
                     var3 = this.getMouseTileY();
                     var4 = this.parent.client.getLevel();
                     var5 = false;

                     for(var6 = 0; var6 < 4; ++var6) {
                        if (DebugWireForm.this.wires[var6].checked && !var4.wireManager.hasWire(var2, var3, var6)) {
                           var4.wireManager.setWire(var2, var3, var6, true);
                           var5 = true;
                        }
                     }

                     if (var5) {
                        this.parent.client.network.sendPacket(new PacketChangeWire(var4, var2, var3, var4.wireManager.getWireData(var2, var3)));
                     }

                     return true;
                  } else {
                     if (Screen.isKeyDown(-99)) {
                        var2 = this.getMouseTileX();
                        var3 = this.getMouseTileY();
                        var4 = this.parent.client.getLevel();
                        var5 = false;

                        for(var6 = 0; var6 < 4; ++var6) {
                           if (DebugWireForm.this.wires[var6].checked && var4.wireManager.hasWire(var2, var3, var6)) {
                              var4.wireManager.setWire(var2, var3, var6, false);
                              var5 = true;
                           }
                        }

                        if (var5) {
                           this.parent.client.network.sendPacket(new PacketChangeWire(var4, var2, var3, var4.wireManager.getWireData(var2, var3)));
                        }
                     }

                     return false;
                  }
               });
            }
         };
         Screen.clearGameTools(var2);
         Screen.setGameTool(var3, var2);
      });
   }
}
