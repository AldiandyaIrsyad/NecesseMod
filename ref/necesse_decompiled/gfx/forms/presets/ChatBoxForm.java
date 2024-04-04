package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.commands.AutoComplete;
import necesse.engine.commands.ParsedCommand;
import necesse.engine.control.InputEvent;
import necesse.engine.network.client.Client;
import necesse.engine.network.packet.PacketChatMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.chat.FormChatInput;
import necesse.gfx.forms.components.chat.FormChatLog;
import necesse.level.maps.hudManager.floatText.ChatBubbleText;

public class ChatBoxForm extends Form {
   private FormChatLog log;
   private FormChatInput input;

   public ChatBoxForm(Client var1, String var2) {
      super((String)var2, 510, 280);
      this.shouldLimitDrawArea = false;
      this.onWindowResized();
      this.log = (FormChatLog)this.addComponent(new FormChatLog(0, 0, this.getWidth(), this.getHeight() - 20, var1.chat) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            this.setActive(ChatBoxForm.this.input.isTyping() || ChatBoxForm.this.input.isControllerFocus() || ChatBoxForm.this.input.isControllerTyping());
            super.draw(var1, var2, var3);
         }
      });
      this.input = (FormChatInput)this.addComponent(new FormChatInput(0, this.getHeight() - 20, var1, this.getWidth()), Integer.MAX_VALUE);
      this.input.onSubmit((var2x) -> {
         String var3 = this.input.getText();
         if (var3.length() != 0) {
            if (var1 != null) {
               boolean var4 = true;
               if (var3.startsWith("/")) {
                  String var5 = var3.substring(1);
                  var4 = !var1.commandsManager.runClientCommand(new ParsedCommand(var5));
               } else {
                  PlayerMob var7 = var1.getPlayer();
                  String var6 = var7.getDisplayName() + ": ";
                  var1.chat.addMessage(var6 + var3);
                  var7.getLevel().hudManager.addElement(new ChatBubbleText(var7, var3));
               }

               if (var4) {
                  var1.network.sendPacket(new PacketChatMessage(var1.getSlot(), var3));
               }
            }

            this.input.clearAndAddToLog();
         }

         this.input.setTyping(false);
      });
      this.drawBase = false;
   }

   public void onAutocompletePacket(List<AutoComplete> var1) {
      this.input.onAutocompletePacket(var1);
   }

   public void submitEscapeEvent(InputEvent var1) {
      if (this.isTyping()) {
         this.input.submitEscapeEvent(var1);
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.setPosition(10, Screen.getHudHeight() - this.getHeight() - 80);
   }

   public boolean isMouseOver(InputEvent var1) {
      return false;
   }

   public boolean isTyping() {
      return this.input.isTyping();
   }

   public void setTyping(boolean var1) {
      this.input.setTyping(var1);
   }

   public void refreshBoundingBoxes() {
      this.log.refreshBoundingBoxes();
   }
}
