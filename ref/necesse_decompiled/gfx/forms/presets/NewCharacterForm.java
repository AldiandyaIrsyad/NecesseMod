package necesse.gfx.forms.presets;

import java.util.Objects;
import necesse.engine.Screen;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.steam.SteamData;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.HumanLook;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.componentPresets.FormNewPlayerPreset;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;

public abstract class NewCharacterForm extends Form {
   protected FormNewPlayerPreset newPlayer;
   protected FormTextInput nameInput;
   protected FormLocalTextButton createButton;
   protected FormLocalTextButton cancelButton;

   public NewCharacterForm(String var1) {
      super((String)var1, 400, 10);
      FormFlow var2 = new FormFlow();
      this.newPlayer = (FormNewPlayerPreset)this.addComponent((FormNewPlayerPreset)var2.nextY(new FormNewPlayerPreset(0, 0, this.getWidth()), 10));
      this.addComponent(new FormLocalLabel("ui", "playername", new FontOptions(16), -1, 5, var2.next(18)));
      this.nameInput = (FormTextInput)this.addComponent(new FormTextInput(4, var2.next(40), FormInputSize.SIZE_32_TO_40, this.getWidth() - 8, GameUtils.getPlayerNameLength().height));
      this.nameInput.placeHolder = new LocalMessage("ui", "playername");
      this.nameInput.setRegexMatchFull(GameUtils.playerNameSymbolsPattern);
      if (SteamData.isCreated()) {
         String var3 = SteamData.getSteamName();
         if (GameUtils.isValidPlayerName(var3) == null) {
            this.nameInput.setText(Objects.toString(var3).trim());
         }
      }

      this.nameInput.onChange((var1x) -> {
         this.updateCreateButton();
      });
      int var4 = var2.next(40);
      this.createButton = (FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "charcreate", 4, var4, this.getWidth() / 2 - 6));
      this.createButton.onClicked((var1x) -> {
         this.onCreatePressed(this.getPlayer());
      });
      this.cancelButton = (FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "connectcancel", this.getWidth() / 2 + 2, var4, this.getWidth() / 2 - 6));
      this.cancelButton.onClicked((var1x) -> {
         this.onCancelPressed();
      });
      this.updateCreateButton();
      this.setHeight(var2.next());
      this.onWindowResized();
   }

   public abstract void onCreatePressed(PlayerMob var1);

   public abstract void onCancelPressed();

   public void reset() {
      this.newPlayer.reset();
      this.nameInput.setText("");
      if (SteamData.isCreated()) {
         String var1 = SteamData.getSteamName();
         if (GameUtils.isValidPlayerName(var1) == null) {
            this.nameInput.setText(Objects.toString(var1).trim());
         }
      }

      this.updateCreateButton();
   }

   public void setLook(HumanLook var1) {
      this.newPlayer.setLook(var1);
   }

   public void updateCreateButton() {
      boolean var1 = true;
      String var2 = this.nameInput.getText().trim();
      GameMessage var3 = GameUtils.isValidPlayerName(var2);
      if (var3 != null) {
         var1 = false;
         this.createButton.setLocalTooltip(var3);
      }

      if (var1) {
         this.createButton.setLocalTooltip((GameMessage)null);
      }

      this.createButton.setActive(var1);
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public PlayerMob getPlayer() {
      PlayerMob var1 = this.newPlayer.getNewPlayer();
      var1.playerName = this.nameInput.getText().trim();
      return var1;
   }
}
