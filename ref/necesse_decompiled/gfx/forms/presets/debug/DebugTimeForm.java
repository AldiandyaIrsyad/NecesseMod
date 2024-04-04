package necesse.gfx.forms.presets.debug;

import necesse.engine.network.packet.PacketChatMessage;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormTextButton;
import necesse.gfx.gameFont.FontOptions;

public class DebugTimeForm extends Form {
   public final DebugForm parent;

   public DebugTimeForm(String var1, DebugForm var2) {
      super((String)var1, 240, 200);
      this.parent = var2;
      ((FormTextButton)this.addComponent(new FormTextButton("Back", 0, 160, this.getWidth()))).onClicked((var1x) -> {
         var2.makeCurrent(var2.world);
      });
      this.addComponent(new FormLabel("Time", new FontOptions(20), 0, this.getWidth() / 2, 10));
      ((FormTextButton)this.addComponent(new FormTextButton("Morning", 0, 40, this.getWidth() / 2))).onClicked((var1x) -> {
         this.sendChat("/time morning");
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Night", this.getWidth() / 2, 40, this.getWidth() / 2))).onClicked((var1x) -> {
         this.sendChat("/time night");
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Noon", 0, 80, this.getWidth() / 2))).onClicked((var1x) -> {
         this.sendChat("/time noon");
      });
      ((FormTextButton)this.addComponent(new FormTextButton("Midnight", this.getWidth() / 2, 80, this.getWidth() / 2))).onClicked((var1x) -> {
         this.sendChat("/time midnight");
      });
      ((FormTextButton)this.addComponent(new FormTextButton("+10", 0, 120, this.getWidth() / 2))).onClicked((var1x) -> {
         this.sendChat("/time add 10");
      });
      ((FormTextButton)this.addComponent(new FormTextButton("+100", this.getWidth() / 2, 120, this.getWidth() / 2))).onClicked((var1x) -> {
         this.sendChat("/time add 100");
      });
   }

   private void sendChat(String var1) {
      this.parent.client.network.sendPacket(new PacketChatMessage(this.parent.client.getSlot(), var1));
   }
}
