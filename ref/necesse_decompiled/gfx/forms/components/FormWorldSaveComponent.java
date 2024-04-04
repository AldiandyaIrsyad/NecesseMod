package necesse.gfx.forms.components;

import java.awt.Rectangle;
import java.util.List;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Localization;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.WorldSave;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.gameTooltips.TooltipLocation;
import necesse.gfx.ui.ButtonColor;

public abstract class FormWorldSaveComponent extends Form {
   public final WorldSave worldSave;

   public FormWorldSaveComponent(int var1, WorldSave var2) {
      super((String)("world" + var2.filePath.getName()), var1, 64);
      this.worldSave = var2;
      boolean var3 = WorldSave.isLatestBackup(var2.filePath.getName());
      boolean var4 = var2.filePath.getName().endsWith(".zip") || var2.filePath.getName().endsWith(".rar");
      this.drawBase = false;
      FormInputSize var5 = FormInputSize.SIZE_24;
      FormFlow var6 = new FormFlow(this.getWidth() - 5);
      if (!var3) {
         ((FormContentIconButton)this.addComponent((FormContentIconButton)var6.prevX(new FormContentIconButton(0, 5, var5, ButtonColor.GREEN, Settings.UI.button_collapsed_24, new GameMessage[]{new LocalMessage("ui", "loadsave")}), 2))).onClicked((var1x) -> {
            this.onSelectPressed();
         });
         ((FormContentIconButton)this.addComponent((FormContentIconButton)var6.prevX(new FormContentIconButton(0, 5, var5, ButtonColor.BASE, Settings.UI.container_rename, new GameMessage[]{new LocalMessage("ui", "renamebutton")}), 2))).onClicked((var1x) -> {
            this.onRenamePressed();
         });
      }

      FormContentIconButton var7 = (FormContentIconButton)this.addComponent((FormContentIconButton)var6.prevX(new FormContentIconButton(0, 5, var5, ButtonColor.BASE, var3 ? Settings.UI.add_existing_button : Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", var3 ? "restoresave" : "backupsave")}), 2));
      var7.onClicked((var2x) -> {
         this.onBackupPressed(var3);
      });
      if (!var3) {
         ((FormContentIconButton)this.addComponent((FormContentIconButton)var6.prevX(new FormContentIconButton(0, 5, var5, ButtonColor.RED, Settings.UI.button_trash_24, new GameMessage[]{new LocalMessage("ui", "deletebutton")}), 2))).onClicked((var1x) -> {
            this.onDeletePressed();
         });
      }

      byte var8 = 5;
      FormFlow var9 = new FormFlow(5);
      int var10 = var6.next() - var8 - 5;
      FontOptions var11 = new FontOptions(20);
      if (!Settings.zipSaves && var4) {
         var11.color(Settings.UI.warningTextColor);
         this.addComponent(new FormMouseHover(var8, var9.next(), var10, this.getHeight() - var9.next() - 5, false) {
            public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
               super.draw(var1, var2, var3);
               Screen.addTooltip(new StringTooltips(Localization.translate("misc", "saveiscompressed"), GameColor.ITEM_LEGENDARY, 400), TooltipLocation.FORM_FOCUS);
            }
         }, Integer.MAX_VALUE);
      }

      String var12 = GameUtils.maxString(var2.displayName, var11, var10);
      this.addComponent((FormLabel)var9.nextY(new FormLabel(var12, var11, -1, var8, 0), 5));
      FontOptions var13 = new FontOptions(12);
      LocalMessage var14 = new LocalMessage("ui", "savetip", new Object[]{"day", var2.getWorldDay(), "time", var2.getWorldTimeReadable()});
      this.addComponent((FormLocalLabel)var9.nextY(new FormLocalLabel(var14, var13, -1, var8, 0, var10), 2));
      this.addComponent((FormLabel)var9.nextY(new FormLabel(var2.getDate(), var13, -1, var8, 0, var10), 2));
   }

   public List<Rectangle> getHitboxes() {
      return singleBox(new Rectangle(this.getX(), this.getY(), this.getWidth(), this.getHeight()));
   }

   public abstract void onSelectPressed();

   public abstract void onRenamePressed();

   public abstract void onBackupPressed(boolean var1);

   public abstract void onDeletePressed();
}
