package necesse.gfx.forms.presets;

import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import necesse.engine.GlobalData;
import necesse.engine.Settings;
import necesse.engine.achievements.Achievement;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.tickManager.TickManager;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormAchievementComponent;
import necesse.gfx.forms.components.FormCheckBox;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormCustomDraw;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.localComponents.FormLocalCheckBox;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelected;
import necesse.gfx.forms.presets.playerStats.PlayerStatsSelectorForm;
import necesse.gfx.forms.presets.sidebar.TrackedSidebarForm;
import necesse.gfx.ui.ButtonColor;

public class AchievementsContentForm extends Form implements PlayerStatsSelected {
   private PlayerStatsSelectorForm selectorForm;
   private FormLocalCheckBox achievementsShowCompleted;
   private FormCustomDraw progressBar;
   private ContentBoxListManager achievementList;
   private Form disabledContent;

   public AchievementsContentForm(String var1, int var2, int var3, PlayerStatsSelectorForm var4) {
      super(var1, var2, var3);
      this.selectorForm = var4;
      this.drawBase = false;
      this.progressBar = (FormCustomDraw)this.addComponent(new FormCustomDraw(0, 0, var2, 20) {
         public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
            int var4 = GlobalData.achievements().getCompleted();
            int var5 = GlobalData.achievements().getTotal();
            float var6 = (float)var4 / (float)var5;
            String var7 = var4 + "/" + var5;
            Achievement.drawProgressbarText(this.getX() + 10, this.getY(), AchievementsContentForm.this.getWidth() - 20, 5, var6, var7, Settings.UI.activeTextColor);
         }
      });
      this.achievementList = ((FormContentBox)this.addComponent(new FormContentBox(0, 20, var2, var3 - 50))).listManager();
      this.achievementsShowCompleted = (FormLocalCheckBox)this.addComponent(new FormLocalCheckBox("ui", "showcompleted", 10, var3 - 20, true));
      this.achievementsShowCompleted.onClicked((var1x) -> {
         this.updateList();
      });
      this.updateList();
   }

   private void updateList() {
      this.achievementList.clear();
      ArrayList var1 = new ArrayList(GlobalData.achievements().getAchievements());
      var1.sort(Comparator.comparing((var0) -> {
         return var0.name.translate();
      }));
      Iterator var2 = var1.iterator();

      while(true) {
         Achievement var3;
         do {
            if (!var2.hasNext()) {
               this.achievementList.fit(5, 5, 5, 5);
               return;
            }

            var3 = (Achievement)var2.next();
         } while(!this.achievementsShowCompleted.checked && var3.isCompleted());

         this.achievementList.add(new FormAchievementComponent(5, 0, this.getWidth() - 10, var3));
         if (!var3.isCompleted()) {
            ((FormLocalCheckBox)this.achievementList.add(new FormLocalCheckBox(new LocalMessage("achievement", "track", "achievement", var3.name), 55, 0, TrackedSidebarForm.isAchievementTracked(var3)), 5)).onClicked((var1x) -> {
               if (((FormCheckBox)var1x.from).checked) {
                  TrackedSidebarForm.addTrackedAchievement(var3);
               } else {
                  TrackedSidebarForm.removeTrackedAchievement(var3);
               }

            });
         }

         if (var3 == GlobalData.achievements().OBTAIN_ITEMS) {
            ((FormLocalTextButton)this.achievementList.add(new FormLocalTextButton(new LocalMessage("stats", "show_item_list"), 55, 0, this.achievementList.contentBox.getWidth() - 75, FormInputSize.SIZE_20, ButtonColor.BASE), 5)).onClicked((var1x) -> {
               this.selectorForm.playerStatsOption.makeCurrent();
               this.selectorForm.playerStats.switcher.makeCurrent(this.selectorForm.playerStats.itemsObtained);
               this.selectorForm.playerStats.subMenuBackPressed = () -> {
                  this.selectorForm.achievementsOption.makeCurrent();
               };
            });
         }
      }
   }

   public void removeDisabledTip() {
      if (this.disabledContent != null) {
         this.removeComponent(this.disabledContent);
      }

      this.progressBar.setPosition(0, 0);
      this.achievementsShowCompleted.setPosition(10, this.getHeight() - 20);
      this.achievementList.contentBox.setPosition(0, 20);
      this.achievementList.contentBox.setHeight(this.getHeight() - 20);
   }

   public void disableAchievements() {
      if (this.disabledContent != null) {
         this.removeComponent(this.disabledContent);
      }

      this.disabledContent = (Form)this.addComponent(new DisabledPreForm(this.getWidth(), new LocalMessage("ui", "achdisabled"), new LocalMessage("ui", "achdisabledhelp")));
      this.progressBar.setPosition(0, this.disabledContent.getHeight());
      this.achievementsShowCompleted.setPosition(10, this.getHeight() - 20);
      this.achievementList.contentBox.setPosition(0, 20 + this.disabledContent.getHeight());
      this.achievementList.contentBox.setHeight(this.getHeight() - 50 - this.disabledContent.getHeight());
   }

   public void onSelected() {
      this.updateList();
   }
}
