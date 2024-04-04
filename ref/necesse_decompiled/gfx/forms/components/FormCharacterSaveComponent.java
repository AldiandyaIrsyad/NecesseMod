package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.io.File;
import java.util.List;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.CharacterSave;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.PlayerSprite;
import necesse.gfx.fairType.FairType;
import necesse.gfx.forms.Form;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.ui.ButtonColor;

public abstract class FormCharacterSaveComponent extends Form {
   public final File file;
   public final CharacterSave character;

   public FormCharacterSaveComponent(int var1, File var2, CharacterSave var3, boolean var4, boolean var5) {
      super((String)("character" + var3.characterUniqueID), var1, 74);
      this.file = var2;
      this.character = var3;
      this.drawBase = false;
      this.addComponent(new FormCustomDraw(5, 5, 64, 64) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            PlayerSprite.getIconDrawOptions(5, 0, 64, 64, FormCharacterSaveComponent.this.character.player, 0, 2).draw();
         }
      });
      LocalMessage var6 = null;
      LocalMessage var7 = null;
      if (!var4) {
         if (var3.cheatsEnabled) {
            if (var5) {
               var7 = new LocalMessage("ui", "characterhascheats");
            } else {
               var6 = new LocalMessage("ui", "charactercheatserror");
            }
         }
      } else if (!var3.cheatsEnabled) {
         var7 = new LocalMessage("ui", "characterisclean");
      }

      FormInputSize var8 = FormInputSize.SIZE_24;
      FormFlow var9 = new FormFlow(this.getWidth() - 5);
      FormContentIconButton var10 = (FormContentIconButton)this.addComponent((FormContentIconButton)var9.prevX(new FormContentIconButton(0, 5, var8, ButtonColor.GREEN, Settings.UI.button_collapsed_24, new GameMessage[]{new LocalMessage("ui", "selectbutton")}), 2));
      var10.onClicked((var1x) -> {
         this.onSelectPressed();
      });
      if (var6 != null) {
         var10.setActive(false);
         var10.setTooltips(var6);
      }

      if (var2 != null) {
         ((FormContentIconButton)this.addComponent((FormContentIconButton)var9.prevX(new FormContentIconButton(0, 5, var8, ButtonColor.BASE, Settings.UI.container_rename, new GameMessage[]{new LocalMessage("ui", "renamebutton")}), 2))).onClicked((var1x) -> {
            this.onRenamePressed();
         });
         ((FormContentIconButton)this.addComponent((FormContentIconButton)var9.prevX(new FormContentIconButton(0, 5, var8, ButtonColor.RED, Settings.UI.button_trash_24, new GameMessage[]{new LocalMessage("ui", "deletebutton")}), 2))).onClicked((var1x) -> {
            this.onDeletePressed();
         });
      } else {
         ((FormContentIconButton)this.addComponent((FormContentIconButton)var9.prevX(new FormContentIconButton(0, 5, var8, ButtonColor.BASE, Settings.UI.container_loot_all, new GameMessage[]{new LocalMessage("ui", "downloadcharacter")}), 2))).onClicked((var1x) -> {
            this.onDownloadPressed();
            ((FormButton)var1x.from).startCooldown(5000);
         });
      }

      byte var11 = 74;
      FormFlow var12 = new FormFlow(5);
      int var13 = var9.next() - var11 - 5;
      FontOptions var14 = new FontOptions(20);
      String var15 = GameUtils.maxString(var3.player.playerName, var14, var13);
      this.addComponent((FormLabel)var12.nextY(new FormLabel(var15, var14, -1, var11, 0), 5));
      FontOptions var16 = new FontOptions(12);
      int var17 = this.getWidth() - var11 - 5;
      if (var2 == null) {
         this.addComponent((FormFairTypeLabel)var12.nextY((new FormFairTypeLabel(new LocalMessage("ui", "characterfromworld"), var16, FairType.TextAlign.LEFT, var11, 0)).setMax(var17, 1, true), 2));
      } else {
         if (var3.characterStats != null) {
            LocalMessage var18 = new LocalMessage("ui", "characterplaytime", "time", GameUtils.formatSeconds((long)var3.characterStats.time_played.get()));
            this.addComponent((FormFairTypeLabel)var12.nextY((new FormFairTypeLabel(var18, var16, FairType.TextAlign.LEFT, var11, 0)).setMax(var17, 1, true), 2));
         }

         Object var19 = var3.lastUsed;
         if (var19 == null) {
            var19 = new LocalMessage("ui", "characternotplayed");
         }

         this.addComponent((FormFairTypeLabel)var12.nextY((new FormFairTypeLabel((GameMessage)var19, var16, FairType.TextAlign.LEFT, var11, 0)).setMax(var17, 1, true), 2));
         if (var7 != null) {
            this.addComponent((FormFairTypeLabel)var12.nextY((new FormFairTypeLabel(var7, var16.copy().color(Settings.UI.errorTextColor), FairType.TextAlign.LEFT, var11, 0)).setMax(var17, 1, true), 2));
         }
      }

      var12.next(8);
      this.setHeight(Math.max(this.getHeight(), var12.next()));
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
   }

   public abstract void onSelectPressed();

   public abstract void onRenamePressed();

   public abstract void onDeletePressed();

   public abstract void onDownloadPressed();
}
