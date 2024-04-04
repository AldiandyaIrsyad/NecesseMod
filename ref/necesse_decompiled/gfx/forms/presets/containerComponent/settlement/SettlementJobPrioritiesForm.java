package necesse.gfx.forms.presets.containerComponent.settlement;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.NavigableSet;
import java.util.function.Supplier;
import necesse.engine.ClipboardTracker;
import necesse.engine.GameTileRange;
import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.localization.Language;
import necesse.engine.localization.Localization;
import necesse.engine.localization.LocalizationChangeListener;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.MobRegistry;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameUtils;
import necesse.engine.util.MultiValueWatcher;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.JobPriority;
import necesse.entity.mobs.job.JobType;
import necesse.gfx.GameColor;
import necesse.gfx.forms.Form;
import necesse.gfx.forms.components.FormContentBox;
import necesse.gfx.forms.components.FormContentIconButton;
import necesse.gfx.forms.components.FormFairTypeLabel;
import necesse.gfx.forms.components.FormFlow;
import necesse.gfx.forms.components.FormIconButton;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.FormLabel;
import necesse.gfx.forms.components.FormMouseHover;
import necesse.gfx.forms.components.localComponents.FormLocalLabel;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.gameFont.FontOptions;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.gfx.ui.ButtonColor;
import necesse.inventory.container.settlement.data.SettlementSettlerPrioritiesData;

public abstract class SettlementJobPrioritiesForm extends Form {
   private final HumanMob mob;
   private final HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities;
   private ClipboardTracker<PrioritiesData> listClipboard;
   private FormContentIconButton pasteButton;
   private FormContentBox prioritiesContent;
   private FormFlow prioritiesContentFlow;
   private ArrayList<PriorityForm> forms;

   public SettlementJobPrioritiesForm(String var1, int var2, int var3, HumanMob var4, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> var5) {
      this(var1, var2, var3, var4, MobRegistry.getLocalization(var4.getID()), var5);
   }

   public SettlementJobPrioritiesForm(String var1, int var2, int var3, HumanMob var4, GameMessage var5, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> var6) {
      super(var1, var2, var3);
      this.forms = new ArrayList();
      this.mob = var4;
      this.priorities = var6;
      FormFlow var7 = new FormFlow(4);
      int var8 = this.getWidth() - 28;
      this.pasteButton = (FormContentIconButton)this.addComponent(new FormContentIconButton(var8, var7.next(), FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.paste_button, new GameMessage[]{new LocalMessage("ui", "pastebutton")}));
      this.pasteButton.onClicked((var1x) -> {
         PrioritiesData var2 = (PrioritiesData)this.listClipboard.getValue();
         if (var2 != null) {
            Iterator var3 = var2.priorities.entrySet().iterator();

            label44:
            while(var3.hasNext()) {
               Map.Entry var4 = (Map.Entry)var3.next();
               SettlementSettlerPrioritiesData.TypePriority var5 = (SettlementSettlerPrioritiesData.TypePriority)var4.getValue();
               Iterator var6 = this.forms.iterator();

               while(true) {
                  PriorityForm var7;
                  do {
                     do {
                        if (!var6.hasNext()) {
                           continue label44;
                        }

                        var7 = (PriorityForm)var6.next();
                     } while(var7.data.disabledBySettler);
                  } while(var7.type.getID() != ((JobType)var4.getKey()).getID());

                  boolean var8 = var7.data.priority != var5.priority || var7.data.disabledByPlayer != var5.disabledByPlayer;
                  var7.data.priority = var5.priority;
                  var7.data.disabledByPlayer = var5.disabledByPlayer;
                  if (var8) {
                     this.onSubmitUpdate(var7.type, var7.data);
                  }
               }
            }

            this.updatePrioritiesContent();
         }

      });
      var8 -= 28;
      this.listClipboard = new ClipboardTracker<PrioritiesData>() {
         public PrioritiesData parse(String var1) {
            try {
               return new PrioritiesData(new LoadData(var1));
            } catch (Exception var3) {
               return null;
            }
         }

         public void onUpdate(PrioritiesData var1) {
            SettlementJobPrioritiesForm.this.pasteButton.setActive(var1 != null && !var1.priorities.isEmpty());
         }

         // $FF: synthetic method
         // $FF: bridge method
         public void onUpdate(Object var1) {
            this.onUpdate((PrioritiesData)var1);
         }

         // $FF: synthetic method
         // $FF: bridge method
         public Object parse(String var1) {
            return this.parse(var1);
         }
      };
      ((FormContentIconButton)this.addComponent(new FormContentIconButton(var8, var7.next(), FormInputSize.SIZE_24, ButtonColor.BASE, Settings.UI.copy_button, new GameMessage[]{new LocalMessage("ui", "copybutton")}))).onClicked((var2x) -> {
         HashMap var3 = new HashMap();
         Iterator var4 = var6.entrySet().iterator();

         while(var4.hasNext()) {
            Map.Entry var5 = (Map.Entry)var4.next();
            if (!((SettlementSettlerPrioritiesData.TypePriority)var5.getValue()).disabledBySettler) {
               var3.put((JobType)var5.getKey(), (SettlementSettlerPrioritiesData.TypePriority)var5.getValue());
            }
         }

         PrioritiesData var7 = new PrioritiesData(var3);
         SaveData var6x = new SaveData("jobs");
         var7.addSaveData(var6x);
         Screen.putClipboard(var6x.getScript());
         this.listClipboard.forceUpdate();
      });
      if (var5 != null) {
         String var9 = var5.translate();
         FontOptions var10 = new FontOptions(20);
         String var11 = GameUtils.maxString(var9, var10, var8 - 10);
         this.addComponent(new FormLabel(var11, var10, -1, 5, var7.next(30)));
      }

      int var12 = var7.next();
      this.prioritiesContent = (FormContentBox)this.addComponent(new FormContentBox(0, var12, var2, var3 - var12 - 28));
      this.prioritiesContentFlow = new FormFlow();
      Iterator var13 = var6.entrySet().iterator();

      while(var13.hasNext()) {
         Map.Entry var14 = (Map.Entry)var13.next();
         this.forms.add(new PriorityForm(this.prioritiesContent, (JobType)var14.getKey(), (SettlementSettlerPrioritiesData.TypePriority)var14.getValue()));
      }

      this.updatePrioritiesContent();
      ((FormLocalTextButton)this.addComponent(new FormLocalTextButton("ui", "backbutton", var2 / 2, this.getHeight() - 28, var2 / 2 - 4, FormInputSize.SIZE_24, ButtonColor.BASE))).onClicked((var1x) -> {
         this.onBack();
      });
   }

   public void updatePrioritiesContent() {
      Comparator var1 = Comparator.comparingInt((var0) -> {
         return var0.data.disabledBySettler ? 1 : -1;
      });
      var1 = var1.thenComparingInt((var0) -> {
         return -var0.type.getID();
      });
      this.forms.sort(var1);
      this.prioritiesContentFlow = new FormFlow();

      Iterator var2;
      PriorityForm var3;
      for(var2 = this.forms.iterator(); var2.hasNext(); var3.setPosition(0, this.prioritiesContentFlow.next(var3.getHeight()))) {
         var3 = (PriorityForm)var2.next();
         if (!this.prioritiesContent.hasComponent(var3)) {
            this.prioritiesContent.addComponent(var3);
         }
      }

      var2 = this.forms.iterator();

      while(var2.hasNext()) {
         var3 = (PriorityForm)var2.next();
         var3.updateSubtitle();
      }

      this.prioritiesContent.setContentBox(new Rectangle(this.prioritiesContent.getWidth(), this.prioritiesContentFlow.next()));
      Screen.submitNextMoveEvent();
   }

   public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
      this.listClipboard.update();
      super.draw(var1, var2, var3);
   }

   public abstract void onSubmitUpdate(JobType var1, SettlementSettlerPrioritiesData.TypePriority var2);

   public abstract void onBack();

   private class PriorityForm extends Form {
      public final JobType type;
      public final SettlementSettlerPrioritiesData.TypePriority data;
      private final MultiValueWatcher changeWatcher;
      public FormLocalLabel label;
      public FormFairTypeLabel subtitle;
      public FormIconButton incPriorityButton;
      public FormIconButton decPriorityButton;

      public PriorityForm(FormContentBox var2, final JobType var3, SettlementSettlerPrioritiesData.TypePriority var4) {
         super(var2.getWidth(), 32);
         this.type = var3;
         this.data = var4;
         this.drawBase = false;
         int var5 = this.getWidth() - 26 - 8;
         this.incPriorityButton = (FormIconButton)this.addComponent(new FormIconButton(5, 3, Settings.UI.button_moveup, 16, 13, new GameMessage[]{new LocalMessage("jobs", "incpriority")}));
         this.incPriorityButton.onClicked((var3x) -> {
            if (var4.priority < ((JobPriority)JobPriority.priorities.first()).priority || var4.disabledByPlayer) {
               if (var4.disabledByPlayer) {
                  var4.priority = ((JobPriority)JobPriority.priorities.last()).priority;
                  var4.disabledByPlayer = false;
               } else {
                  JobPriority var4x = JobPriority.getJobPriority(var4.priority);
                  NavigableSet var5 = JobPriority.priorities.headSet(var4x, false);
                  if (var5.isEmpty()) {
                     var4.priority = ((JobPriority)JobPriority.priorities.first()).priority;
                  } else {
                     var4.priority = ((JobPriority)var5.last()).priority;
                  }
               }

               this.updateSubtitle();
               SettlementJobPrioritiesForm.this.updatePrioritiesContent();
               SettlementJobPrioritiesForm.this.onSubmitUpdate(var3, var4);
            }

         });
         this.decPriorityButton = (FormIconButton)this.addComponent(new FormIconButton(5, this.getHeight() - 3 - 13, Settings.UI.button_movedown, 16, 13, new GameMessage[]{new LocalMessage("jobs", "decpriority")}));
         this.decPriorityButton.onClicked((var3x) -> {
            if (var4.priority > ((JobPriority)JobPriority.priorities.last()).priority || !var4.disabledByPlayer) {
               JobPriority var4x = JobPriority.getJobPriority(var4.priority);
               NavigableSet var5 = JobPriority.priorities.tailSet(var4x, false);
               if (var5.isEmpty()) {
                  var4.disabledByPlayer = true;
                  var4.priority = ((JobPriority)JobPriority.priorities.last()).priority;
               } else {
                  var4.priority = ((JobPriority)var5.first()).priority;
               }

               this.updateSubtitle();
               SettlementJobPrioritiesForm.this.updatePrioritiesContent();
               SettlementJobPrioritiesForm.this.onSubmitUpdate(var3, var4);
            }

         });
         FontOptions var6 = (new FontOptions(16)).color(Settings.UI.activeTextColor);
         this.label = (FormLocalLabel)this.addComponent(new FormLocalLabel(var3.displayName, var6, -1, 25, 0, var5 - 4 - 12));
         this.subtitle = (FormFairTypeLabel)this.addComponent(new FormFairTypeLabel("", 25, 18));
         this.subtitle.setFontOptions(new FontOptions(12));
         this.updateSubtitle();
         this.addComponent(new FormMouseHover(25, 0, var5 - 4, this.getHeight()) {
            public GameTooltips getTooltips(PlayerMob var1) {
               StringTooltips var2 = new StringTooltips();
               if (var3.tooltip != null) {
                  var2.add(var3.tooltip.translate(), 400);
               }

               if (var1 != null) {
                  var2.add(Localization.translate("jobs", "jobrange", "range", (Object)((GameTileRange)var3.tileRange.apply(var1.getLevel())).maxRange));
               }

               return var2;
            }
         });
         this.changeWatcher = new MultiValueWatcher(new Supplier[]{() -> {
            return var4.priority;
         }, () -> {
            return var4.disabledByPlayer;
         }}) {
            public void onChange() {
               PriorityForm.this.updateSubtitle();
            }
         };
      }

      protected void init() {
         super.init();
         Localization.addListener(new LocalizationChangeListener() {
            public void onChange(Language var1) {
               PriorityForm.this.updateSubtitle();
            }

            public boolean isDisposed() {
               return PriorityForm.this.isDisposed();
            }
         });
      }

      public void draw(TickManager var1, PlayerMob var2, Rectangle var3) {
         this.changeWatcher.update();
         super.draw(var1, var2, var3);
      }

      public void updateSubtitle() {
         if (this.data.disabledBySettler) {
            this.subtitle.setText(Localization.translate("jobs", "settlerincapable"));
            this.incPriorityButton.setActive(false);
            this.decPriorityButton.setActive(false);
         } else {
            if (this.data.disabledByPlayer) {
               this.subtitle.setText(GameColor.getCustomColorCode(new Color(150, 50, 50)) + Localization.translate("ui", "prioritydisabled"));
            } else {
               JobPriority var1 = JobPriority.getJobPriority(this.data.priority);
               this.subtitle.setText(var1.getFullDisplayName());
            }

            this.incPriorityButton.setActive(this.data.priority < ((JobPriority)JobPriority.priorities.first()).priority || this.data.disabledByPlayer);
            this.decPriorityButton.setActive(this.data.priority > ((JobPriority)JobPriority.priorities.last()).priority || !this.data.disabledByPlayer);
         }

      }
   }

   public static class PrioritiesData {
      public final HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities;

      public PrioritiesData(HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> var1) {
         this.priorities = var1;
      }

      public PrioritiesData(LoadData var1) {
         this.priorities = new HashMap();
         Iterator var2 = var1.getLoadData().iterator();

         while(var2.hasNext()) {
            LoadData var3 = (LoadData)var2.next();
            if (var3.isArray()) {
               String var4 = var3.getName();
               int var5 = var3.getInt("priority");
               boolean var6 = var3.getBoolean("disabled");
               JobType var7 = JobTypeRegistry.getJobType(var4);
               if (var7 != null) {
                  this.priorities.put(var7, new SettlementSettlerPrioritiesData.TypePriority(false, var5, var6));
               }
            }
         }

      }

      public void addSaveData(SaveData var1) {
         Iterator var2 = this.priorities.entrySet().iterator();

         while(var2.hasNext()) {
            Map.Entry var3 = (Map.Entry)var2.next();
            SaveData var4 = new SaveData(((JobType)var3.getKey()).getStringID());
            var4.addInt("priority", ((SettlementSettlerPrioritiesData.TypePriority)var3.getValue()).priority);
            var4.addBoolean("disabled", ((SettlementSettlerPrioritiesData.TypePriority)var3.getValue()).disabledByPlayer);
            var1.addSaveData(var4);
         }

      }
   }
}
