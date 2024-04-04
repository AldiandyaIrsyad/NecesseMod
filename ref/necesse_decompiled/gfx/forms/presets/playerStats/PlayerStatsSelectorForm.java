package necesse.gfx.forms.presets.playerStats;

import java.awt.Rectangle;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.function.Consumer;
import necesse.engine.GlobalData;
import necesse.engine.Screen;
import necesse.engine.control.InputEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.playerStats.PlayerStats;
import necesse.engine.steam.SteamData;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.FormSwitcher;
import necesse.gfx.forms.components.FormComponent;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.AchievementsContentForm;
import necesse.gfx.forms.presets.GlobalStatsForm;
import necesse.gfx.gameFont.FontOptions;

public class PlayerStatsSelectorForm extends FormSwitcher {
   private Form menuForm;
   private Form contentForm;
   private FormLocalLabel contentLabel;
   private FormComponent loadingTip;
   private FormContentBox contentBox;
   private FormSwitcher contentSwitcher;
   private LinkedList<StatsOption> options;
   private StatsOption currentSelected;
   public StatsOption achievementsOption;
   public AchievementsContentForm achievements;
   public StatsOption playerStatsOption;
   public PlayerStatsForm playerStats;

   public PlayerStatsSelectorForm(int var1, int var2, int var3, boolean var4) {
      this.options = new LinkedList();
      this.currentSelected = null;
      this.achievements = null;
      this.playerStats = null;
      this.menuForm = (Form)this.addComponent(new Form(var1, 0));
      this.updateMenu();
      this.contentForm = (Form)this.addComponent(new Form(var2, var3));
      ((FormLocalTextButton)this.contentForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, this.contentForm.getHeight() - 40, this.contentForm.getWidth() - 8))).onClicked((var1x) -> {
         if (this.currentSelected == null || !(this.currentSelected.statsComponent instanceof PlayerStatsSelected) || !((PlayerStatsSelected)this.currentSelected.statsComponent).backPressed()) {
            this.makeCurrent(this.menuForm);
         }
      });
      this.contentLabel = (FormLocalLabel)this.contentForm.addComponent(new FormLocalLabel(new StaticMessage("N/A"), new FontOptions(20), 0, this.contentForm.getWidth() / 2, 5));
      this.contentBox = (FormContentBox)this.contentForm.addComponent(new FormContentBox(0, 30, this.getContentWidth(), this.getContentHeight()));
      this.contentSwitcher = (FormSwitcher)this.contentBox.addComponent(new FormSwitcher());
      this.loadingTip = this.contentSwitcher.addComponent(new FormLocalLabel("ui", "loadingdotdot", new FontOptions(20), 0, this.contentForm.getWidth() / 2, 30, this.contentForm.getWidth() - 10));
      if (var4) {
         this.addDefaultOptions();
      }

      this.makeCurrent(this.menuForm);
      this.onWindowResized();
   }

   public PlayerStatsSelectorForm(boolean var1) {
      this(400, 400, 480, var1);
   }

   private void updateMenu() {
      this.menuForm.clearComponents();
      int var1 = 0;

      for(Iterator var2 = this.options.iterator(); var2.hasNext(); var1 += 40) {
         StatsOption var3 = (StatsOption)var2.next();
         FormLocalTextButton var4 = (FormLocalTextButton)this.menuForm.addComponent(new FormLocalTextButton(var3.displayName, 4, var1, this.menuForm.getWidth() - 8));
         if (var3.tooltip != null) {
            var4.setLocalTooltip(var3.tooltip);
         }

         var4.onClicked((var1x) -> {
            var3.makeCurrent();
         });
      }

      ((FormLocalTextButton)this.menuForm.addComponent(new FormLocalTextButton("ui", "backbutton", 4, var1, this.menuForm.getWidth() - 8))).onClicked((var1x) -> {
         this.backPressed();
      });
      var1 += 40;
      this.menuForm.setHeight(var1);
   }

   public void addDefaultOptions() {
      this.achievementsOption = this.addComponentOption(new LocalMessage("ui", "achievements"), (GameMessage)null, (FormComponent)(this.achievements = new AchievementsContentForm("achievements", this.getContentWidth(), this.getContentHeight(), this)));
      this.playerStatsOption = this.addComponentOption(new LocalMessage("ui", "playerstats"), new LocalMessage("ui", "playerstatstip"), (FormComponent)(this.playerStats = (PlayerStatsForm)this.fromStatsToComponent(GlobalData.stats())));
      if (GlobalData.isDevMode()) {
         this.addComponentOption(new LocalMessage("ui", "globalstats"), (GameMessage)null, new GlobalStatsForm(0, 0, this.getContentWidth(), this.getContentHeight()), SteamData::updateGlobalStats);
      }

   }

   public void submitEscapeEvent(InputEvent var1) {
      if (this.currentSelected != null && this.currentSelected.statsComponent instanceof PlayerStatsSelected && ((PlayerStatsSelected)this.currentSelected.statsComponent).backPressed()) {
         var1.use();
      } else {
         if (!this.isCurrent(this.menuForm)) {
            this.makeCurrent(this.menuForm);
            var1.use();
         }

      }
   }

   public Consumer<PlayerStats> addStatsOption(GameMessage var1, GameMessage var2, Runnable var3) {
      StatsOption var4 = this.addOption(new StatsOption(var1, var2, var3));
      return (var2x) -> {
         var4.loadStats.accept(this.fromStatsToComponent(var2x));
      };
   }

   public StatsOption addStatsOption(GameMessage var1, GameMessage var2, PlayerStats var3) {
      return this.addOption(new StatsOption(var1, var2, () -> {
      }, this.fromStatsToComponent(var3)));
   }

   public StatsOption addComponentOption(GameMessage var1, GameMessage var2, FormComponent var3) {
      return this.addOption(new StatsOption(var1, var2, () -> {
      }, var3));
   }

   public StatsOption addComponentOption(GameMessage var1, GameMessage var2, FormComponent var3, Runnable var4) {
      return this.addOption(new StatsOption(var1, var2, var4, var3));
   }

   public Consumer<FormComponent> addComponentOption(GameMessage var1, GameMessage var2, Runnable var3) {
      StatsOption var4 = this.addOption(new StatsOption(var1, var2, var3));
      return var4.loadStats;
   }

   private StatsOption addOption(StatsOption var1) {
      this.options.add(var1);
      this.updateMenu();
      this.onWindowResized();
      return var1;
   }

   public FormComponent fromStatsToComponent(PlayerStats var1) {
      return (FormComponent)(var1 != null && var1.mode != PlayerStats.Mode.WRITE_ONLY ? new PlayerStatsForm(0, 0, this.getContentWidth(), this.getContentHeight(), var1) : new FormLocalLabel("ui", "statsnotfound", new FontOptions(20), 0, this.getContentWidth() / 2, 10, this.getContentWidth() - 10));
   }

   public int getContentWidth() {
      return this.contentForm.getWidth();
   }

   public int getContentHeight() {
      return this.contentForm.getHeight() - 70;
   }

   public void onWindowResized() {
      super.onWindowResized();
      this.menuForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
      this.contentForm.setPosMiddle(Screen.getHudWidth() / 2, Screen.getHudHeight() / 2);
   }

   public void backPressed() {
   }

   public void reset() {
      this.options.forEach(StatsOption::reset);
      this.makeCurrent(this.menuForm);
   }

   public void disableAchievements() {
      if (this.achievements != null) {
         this.achievements.disableAchievements();
      }

      if (this.playerStats != null) {
         this.playerStats.setDisabledTip(new LocalMessage("ui", "psdisabled"), new LocalMessage("ui", "achdisabledhelp"));
      }

   }

   public class StatsOption {
      public final GameMessage displayName;
      public final GameMessage tooltip;
      private boolean resetComponent;
      private FormComponent statsComponent;
      private Runnable startLoading;
      public final Consumer<FormComponent> loadStats;

      public StatsOption(GameMessage var2, GameMessage var3, Runnable var4) {
         this.resetComponent = true;
         this.statsComponent = null;
         this.displayName = var2;
         this.tooltip = var3;
         this.startLoading = var4;
         this.loadStats = (var1x) -> {
            this.reset();
            if (this.resetComponent) {
               this.statsComponent = PlayerStatsSelectorForm.this.contentSwitcher.addComponent(var1x);
            }

            if (PlayerStatsSelectorForm.this.currentSelected == this) {
               this.makeCurrent();
            }

         };
      }

      public StatsOption(GameMessage var2, GameMessage var3, Runnable var4, FormComponent var5) {
         this(var2, var3, var4);
         this.statsComponent = PlayerStatsSelectorForm.this.contentSwitcher.addComponent(var5);
         this.resetComponent = false;
      }

      public void reset() {
         if (this.resetComponent && this.statsComponent != null) {
            PlayerStatsSelectorForm.this.contentSwitcher.removeComponent(this.statsComponent);
            this.statsComponent = null;
         }

      }

      public void makeCurrent() {
         PlayerStatsSelectorForm.this.contentLabel.setLocalization(this.displayName);
         PlayerStatsSelectorForm.this.makeCurrent(PlayerStatsSelectorForm.this.contentForm);
         PlayerStatsSelectorForm.this.currentSelected = this;
         if (this.statsComponent == null) {
            PlayerStatsSelectorForm.this.contentSwitcher.makeCurrent(PlayerStatsSelectorForm.this.loadingTip);
            PlayerStatsSelectorForm.this.contentBox.setContentBox(new Rectangle(PlayerStatsSelectorForm.this.getContentWidth(), PlayerStatsSelectorForm.this.getContentHeight()));
            this.startLoading.run();
         } else {
            PlayerStatsSelectorForm.this.contentSwitcher.makeCurrent(this.statsComponent);
            PlayerStatsSelectorForm.this.contentBox.fitContentBoxToComponents();
            PlayerStatsSelectorForm.this.contentBox.centerContentHorizontal();
            if (this.statsComponent instanceof PlayerStatsSelected) {
               ((PlayerStatsSelected)this.statsComponent).onSelected();
            }
         }

      }
   }
}
