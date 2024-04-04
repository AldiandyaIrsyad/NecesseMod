package necesse.gfx.forms.presets;

import java.io.File;
import java.io.IOException;
import java.util.zip.ZipError;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.control.ControllerEvent;
import necesse.engine.control.ControllerInput;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.network.server.ServerSettings;
import necesse.engine.save.WorldSave;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.world.FileSystemClosedException;
import necesse.engine.world.World;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.FormSwitcher;

public abstract class NewSaveForm extends FormSwitcher {
   protected LabeledTextInputForm worldNameForm;
   protected NewSaveWorldSettingsForm settingsForm;
   protected DifficultySelectForm difficultyForm;

   public NewSaveForm() {
      this.worldNameForm = (LabeledTextInputForm)this.addComponent(new LabeledTextInputForm("worldName", new LocalMessage("ui", "chooseworldname"), true, GameUtils.validFileNamePattern, new LocalMessage("ui", "continuebutton"), new LocalMessage("ui", "backbutton")) {
         public GameMessage getInputError(String var1) {
            return !var1.isEmpty() && !WorldSave.isLatestBackup(var1) ? null : new StaticMessage("");
         }

         public void onConfirmed(String var1) {
            NewSaveForm.this.makeCurrent(NewSaveForm.this.difficultyForm);
         }

         public void onCancelled() {
            NewSaveForm.this.backPressed();
         }
      });
      this.difficultyForm = (DifficultySelectForm)this.addComponent(new DifficultySelectForm(this::createPressed, () -> {
         this.makeCurrent(this.worldNameForm);
      }, () -> {
         this.makeCurrent(this.settingsForm);
      }));
      this.settingsForm = (NewSaveWorldSettingsForm)this.addComponent(new NewSaveWorldSettingsForm(this::createPressed, () -> {
         this.makeCurrent(this.difficultyForm);
      }));
      this.makeCurrent(this.worldNameForm);
      this.onWindowResized();
   }

   public void handleInputEvent(InputEvent var1, TickManager var2, PlayerMob var3) {
      super.handleInputEvent(var1, var2, var3);
      if (!var1.isUsed() && var1.state && var1.getID() == 256) {
         if (this.isCurrent(this.difficultyForm)) {
            var1.use();
            this.makeCurrent(this.worldNameForm);
         } else if (this.isCurrent(this.settingsForm)) {
            var1.use();
            this.makeCurrent(this.difficultyForm);
         }
      }

   }

   public void handleControllerEvent(ControllerEvent var1, TickManager var2, PlayerMob var3) {
      super.handleControllerEvent(var1, var2, var3);
      if (!var1.isUsed() && var1.buttonState && var1.getState() == ControllerInput.MENU_BACK) {
         if (this.isCurrent(this.difficultyForm)) {
            var1.use();
            this.makeCurrent(this.worldNameForm);
         } else if (this.isCurrent(this.settingsForm)) {
            var1.use();
            this.makeCurrent(this.difficultyForm);
         }
      }

   }

   public void onWindowResized() {
      super.onWindowResized();
      this.worldNameForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.difficultyForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.settingsForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void onStarted() {
      this.makeCurrent(this.worldNameForm);
      this.worldNameForm.startTyping();
   }

   public void reset() {
      this.worldNameForm.setInput("");
      this.settingsForm.reset();
   }

   private void createPressed() {
      String var1 = this.worldNameForm.getInputText();
      int var2 = 1;

      while(true) {
         String var3 = var1 + (var2 == 1 ? "" : "" + var2);
         if (World.worldExistsWithName(var3) == null) {
            try {
               World var4 = World.getSaveDataWorld(new File(World.getWorldsPath() + var3 + (Settings.zipSaves ? ".zip" : "")));
               var4.settings.difficulty = this.difficultyForm.selectedDifficulty;
               this.settingsForm.applyToWorldSettings(var4.settings);
               var4.settings.saveSettings();
               var4.closeFileSystem();
            } catch (ZipError | IOException var5) {
               this.error(Localization.translate("misc", "createworldfailed") + "\n\n\"" + var5.getMessage() + "\"");
               var5.printStackTrace();
               return;
            } catch (FileSystemClosedException var6) {
               this.error(Localization.translate("misc", "createworldfailed") + "\n\n" + Localization.translate("misc", "savenotclosed"));
               var6.printStackTrace();
            }

            ServerSettings var7 = ServerSettings.SingleplayerServer(new File(World.getWorldsPath() + var3 + (Settings.zipSaves ? ".zip" : "")));
            var7.spawnSeed = this.settingsForm.getSpawnSeed();
            var7.spawnIsland = this.settingsForm.getSpawnIsland();
            var7.spawnGuide = this.settingsForm.shouldSpawnStarterHouse();
            this.createPressed(var7);
            return;
         }

         ++var2;
      }
   }

   public abstract void createPressed(ServerSettings var1);

   public abstract void error(String var1);

   public abstract void backPressed();
}
