package necesse.gfx.forms.presets;

import java.awt.Point;
import java.awt.Rectangle;
import necesse.engine.GameDeathPenalty;
import necesse.engine.GameLog;
import necesse.engine.GameRaidFrequency;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.util.GameRandom;
import necesse.engine.world.World;
import necesse.engine.world.WorldSettings;
import necesse.gfx.forms.ButtonOptions;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormDropdownSelectionButton;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormTextInput;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalSlider;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public class NewSaveWorldSettingsForm extends Form {
   protected FormContentBox settingsContent;
   protected FormTextInput spawnX;
   protected FormTextInput spawnY;
   protected FormLocalCheckBox customSpawn;
   protected FormTextInput spawnSeed;
   protected FormLocalTextButton spawnSeedNew;
   protected FormLocalCheckBox spawnStarterHouse;
   protected FormDropdownSelectionButton<GameDeathPenalty> deathPenalty;
   protected FormDropdownSelectionButton<GameRaidFrequency> raidFrequency;
   protected FormLocalCheckBox survivalMode;
   protected FormLocalCheckBox playerHunger;
   protected FormLocalSlider dayTimeMod;
   protected FormLocalSlider nightTimeMod;

   public NewSaveWorldSettingsForm(Runnable var1, Runnable var2) {
      this(new ButtonOptions("ui", "createworld", var1), ButtonOptions.backButton(var2));
   }

   public NewSaveWorldSettingsForm(ButtonOptions var1, ButtonOptions var2) {
      super((String)"saveSettings", 500, 535);
      int var3 = Math.min(Math.max(this.getWidth() - 50, 350), this.getWidth() - 20);
      int var4 = this.getWidth() / 2 - var3 / 2;
      this.settingsContent = (FormContentBox)this.addComponent(new FormContentBox(0, 0, this.getWidth(), this.getHeight() - 40));
      this.settingsContent.controllerScrollPadding = 60;
      int var5 = Math.min(Math.max(this.getWidth() - 50, 350), this.getWidth() - 20);
      int var6 = (this.settingsContent.getWidth() - var5) / 2;
      FormFlow var7 = new FormFlow(10);
      WorldSettings var8 = new WorldSettings((World)null);
      this.settingsContent.addComponent(new FormLocalLabel("ui", "worldsettings", new FontOptions(20), 0, this.settingsContent.getWidth() / 2, var7.next(30)));
      this.settingsContent.addComponent((FormLocalLabel)var7.nextY(new FormLocalLabel("ui", "wschangetip", new FontOptions(12), 0, this.settingsContent.getWidth() / 2, 0, var5), 10));
      this.settingsContent.addComponent(new FormLocalLabel("ui", "deathpenalty", new FontOptions(16), 0, this.settingsContent.getWidth() / 2, var7.next(20)));
      this.deathPenalty = (FormDropdownSelectionButton)this.settingsContent.addComponent(new FormDropdownSelectionButton(var4, var7.next(35), FormInputSize.SIZE_24, ButtonColor.BASE, var3));
      GameDeathPenalty[] var9 = GameDeathPenalty.values();
      int var10 = var9.length;

      int var11;
      for(var11 = 0; var11 < var10; ++var11) {
         GameDeathPenalty var12 = var9[var11];
         this.deathPenalty.options.add(var12, var12.displayName, () -> {
            return var12.description;
         });
      }

      this.deathPenalty.setSelected(var8.deathPenalty, var8.deathPenalty.displayName);
      this.settingsContent.addComponent(new FormLocalLabel("ui", "raidfrequency", new FontOptions(16), 0, this.settingsContent.getWidth() / 2, var7.next(20)));
      this.raidFrequency = (FormDropdownSelectionButton)this.settingsContent.addComponent(new FormDropdownSelectionButton(var4, var7.next(35), FormInputSize.SIZE_24, ButtonColor.BASE, var3));
      GameRaidFrequency[] var14 = GameRaidFrequency.values();
      var10 = var14.length;

      for(var11 = 0; var11 < var10; ++var11) {
         GameRaidFrequency var17 = var14[var11];
         this.raidFrequency.options.add(var17, var17.displayName, () -> {
            return var17.description;
         });
      }

      this.raidFrequency.setSelected(var8.raidFrequency, var8.raidFrequency.displayName);
      var7.next(10);
      this.survivalMode = (FormLocalCheckBox)this.settingsContent.addComponent((new FormLocalCheckBox("ui", "survivalmode", 10, var7.next(), var5)).useButtonTexture());
      this.survivalMode.onClicked((var1x) -> {
         this.playerHunger.setActive(!((FormCheckBox)var1x.from).checked);
         if (!this.playerHunger.checked) {
            this.playerHunger.checked = ((FormCheckBox)var1x.from).checked;
         }

      });
      this.survivalMode.checked = var8.survivalMode;
      Rectangle var15 = this.survivalMode.getBoundingBox();
      this.survivalMode.setPosition(this.settingsContent.getWidth() / 2 - var15.width / 2, var7.next(var15.height + 10));
      this.settingsContent.addComponent((FormLocalLabel)var7.nextY(new FormLocalLabel("ui", "survivalmodetip", new FontOptions(12), 0, this.settingsContent.getWidth() / 2, 0, var5), 8));
      this.playerHunger = (FormLocalCheckBox)this.settingsContent.addComponent((new FormLocalCheckBox("ui", "playerhungerbox", 10, var7.next(), var5)).useButtonTexture());
      this.playerHunger.handleClicksIfNoEventHandlers = true;
      if (this.survivalMode.checked) {
         this.playerHunger.setActive(false);
      }

      this.playerHunger.checked = var8.playerHunger;
      Rectangle var16 = this.playerHunger.getBoundingBox();
      this.playerHunger.setPosition(this.settingsContent.getWidth() / 2 - var16.width / 2, var7.next(var16.height + 15));
      this.playerHunger.controllerUpFocus = this.survivalMode;
      this.survivalMode.controllerDownFocus = this.playerHunger;
      this.raidFrequency.controllerDownFocus = this.survivalMode;
      var7.next(10);
      var11 = var5 / 2;
      this.settingsContent.addComponent(new FormLocalLabel("ui", "spawnsettings", new FontOptions(20), 0, this.settingsContent.getWidth() / 2, var7.next(30)));
      this.settingsContent.addComponent(new FormLocalLabel("ui", "spawnseed", new FontOptions(16), -1, var6 + 6, var7.next(20)));
      int var18 = var7.next(50);
      this.spawnSeed = (FormTextInput)this.settingsContent.addComponent(new FormTextInput(var6, var18, FormInputSize.SIZE_32_TO_40, var11, 50));
      this.spawnSeed.setRegexMatchFull("[a-zA-Z0-9 ]+");
      this.spawnSeedNew = (FormLocalTextButton)this.settingsContent.addComponent(new FormLocalTextButton("ui", "resetseed", var6 + var11, var18, var11));
      this.spawnSeedNew.onClicked((var1x) -> {
         this.setNewRandomSpawnSeed();
      });
      this.customSpawn = (FormLocalCheckBox)this.settingsContent.addComponent((new FormLocalCheckBox("ui", "customspawn", var6 + 6, var7.next(20))).useButtonTexture());
      this.customSpawn.onClicked((var1x) -> {
         this.updateSpawnMethodActives();
      });
      int var13 = var7.next(45);
      this.spawnX = (FormTextInput)this.settingsContent.addComponent(new FormTextInput(var6, var13, FormInputSize.SIZE_32_TO_40, var11, 10));
      this.spawnX.placeHolder = new LocalMessage("ui", "xcoord");
      this.spawnX.setRegexMatchFull("-?([0-9]+)?");
      this.spawnX.setActive(false);
      this.spawnSeed.controllerDownFocus = this.customSpawn;
      this.spawnX.controllerUpFocus = this.customSpawn;
      this.spawnY = (FormTextInput)this.settingsContent.addComponent(new FormTextInput(var6 + var11, var13, FormInputSize.SIZE_32_TO_40, var11, 10));
      this.spawnY.placeHolder = new LocalMessage("ui", "ycoord");
      this.spawnY.setRegexMatchFull("-?([0-9]+)?");
      this.spawnY.setActive(false);
      this.spawnX.tabTypingComponent = this.spawnY;
      this.spawnY.tabTypingComponent = this.spawnX;
      this.spawnStarterHouse = (FormLocalCheckBox)this.settingsContent.addComponent((new FormLocalCheckBox("ui", "spawnguide", var6 + 6, var7.next(24))).useButtonTexture());
      this.spawnStarterHouse.handleClicksIfNoEventHandlers = true;
      this.spawnStarterHouse.checked = true;
      this.spawnX.controllerDownFocus = this.spawnStarterHouse;
      var7.next(10);
      this.settingsContent.addComponent(new FormLocalLabel("ui", "worldadvanced", new FontOptions(20), 0, this.settingsContent.getWidth() / 2, var7.next(30)));
      this.dayTimeMod = (FormLocalSlider)this.settingsContent.addComponent((<undefinedtype>)var7.nextY(new FormLocalSlider("ui", "daymodnew", var6, 10, 10, 5, 50, var5, new FontOptions(12)) {
         public String getValueText() {
            return this.getValue() * 10 + "%";
         }
      }, 5));
      this.dayTimeMod.allowScroll = false;
      this.nightTimeMod = (FormLocalSlider)this.settingsContent.addComponent((<undefinedtype>)var7.nextY(new FormLocalSlider("ui", "nightmodnew", var6, 10, 10, 5, 50, var5, new FontOptions(12)) {
         public String getValueText() {
            return this.getValue() * 10 + "%";
         }
      }, 5));
      this.nightTimeMod.allowScroll = false;
      this.settingsContent.setContentBox(new Rectangle(0, 0, this.settingsContent.getWidth(), var7.next()));
      ((FormLocalTextButton)this.addComponent(new FormLocalTextButton(var1.text, 4, this.getHeight() - 40, this.getWidth() / 2 - 6))).onClicked((var1x) -> {
         var2.pressed.run();
      });
      ((FormLocalTextButton)this.addComponent(new FormLocalTextButton(var2.text, this.getWidth() / 2 + 2, this.getHeight() - 40, this.getWidth() / 2 - 6))).onClicked((var1x) -> {
         var2.pressed.run();
      });
      this.setNewRandomSpawnSeed();
   }

   private void updateSpawnMethodActives() {
      boolean var1 = this.customSpawn.checked;
      this.spawnX.setActive(var1);
      this.spawnY.setActive(var1);
      this.spawnSeed.setActive(!var1);
      this.spawnSeedNew.setActive(!var1);
   }

   public void setNewRandomSpawnSeed() {
      String var1 = "abcdefghijklmnopqrstuvwyxzABCDEFGHIJKLMNOPQRSTUVWYXZ1234567890";
      StringBuilder var2 = new StringBuilder();

      for(int var3 = 0; var3 < 5; ++var3) {
         var2.append(var1.charAt(GameRandom.globalRandom.nextInt(var1.length())));
      }

      this.spawnSeed.setText(var2.toString());
   }

   public int getSpawnSeed() {
      return this.spawnSeed.getText().hashCode();
   }

   public void reset() {
      this.customSpawn.checked = false;
      this.setNewRandomSpawnSeed();
      this.spawnX.setText("");
      this.spawnY.setText("");
   }

   public boolean isCustomSpawnSelected() {
      return this.customSpawn.checked;
   }

   public Point getSpawnIsland() {
      if (!this.isCustomSpawnSelected()) {
         return null;
      } else {
         int var1;
         try {
            var1 = Integer.parseInt(this.spawnX.getText());
         } catch (NumberFormatException var5) {
            GameLog.warn.println("Could not parse custom X island spawn.");
            return null;
         }

         int var2;
         try {
            var2 = Integer.parseInt(this.spawnY.getText());
         } catch (NumberFormatException var4) {
            GameLog.warn.println("Could not parse custom Y island spawn.");
            return null;
         }

         return new Point(var1, var2);
      }
   }

   public boolean shouldSpawnStarterHouse() {
      return this.spawnStarterHouse.checked;
   }

   public void applyToWorldSettings(WorldSettings var1) {
      var1.deathPenalty = (GameDeathPenalty)this.deathPenalty.getSelected();
      var1.raidFrequency = (GameRaidFrequency)this.raidFrequency.getSelected();
      var1.survivalMode = this.survivalMode.checked;
      var1.playerHunger = this.playerHunger.checked;
      var1.dayTimeMod = (float)this.dayTimeMod.getValue() / 10.0F;
      var1.nightTimeMod = (float)this.nightTimeMod.getValue() / 10.0F;
   }
}
