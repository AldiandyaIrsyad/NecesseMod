package necesse.entity.mobs.friendly.human.humanWorkSetting;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.entity.mobs.job.JobType;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementJobPrioritiesForm;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.data.SettlementSettlerPrioritiesData;
import necesse.inventory.container.settlement.events.SettlementSettlerPrioritiesChangedEvent;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class HumanWorkPrioritiesSetting extends HumanWorkSetting<AtomicBoolean> {
   public final HumanMob mob;

   public HumanWorkPrioritiesSetting(HumanMob var1) {
      this.mob = var1;
   }

   public void writeContainerPacket(ServerClient var1, PacketWriter var2) {
      SettlementLevelData var3 = this.mob.isSettler() ? this.mob.getSettlementLevelData() : null;
      boolean var4 = var3 != null && var3.getLevel().settlementLayer.doesClientHaveAccess(var1) && this.mob.jobTypeHandler.getTypePriorities().stream().anyMatch((var0) -> {
         return var0.type.canChangePriority;
      });
      var2.putNextBoolean(var4);
   }

   public AtomicBoolean readContainer(ShopContainer var1, PacketReader var2) {
      return new AtomicBoolean(var2.getNextBoolean());
   }

   public HumanWorkContainerHandler<AtomicBoolean> setupHandler(ShopContainer var1, ShopContainer.ContainerWorkSetting<AtomicBoolean> var2) {
      return new ContainerHandler(var1, var2);
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object readContainer(ShopContainer var1, PacketReader var2) {
      return this.readContainer(var1, var2);
   }

   private class ContainerHandler extends HumanWorkContainerHandler<AtomicBoolean> {
      private AtomicBoolean subscribed = new AtomicBoolean();
      private BooleanCustomAction subscribe;
      private SetPrioritySettingAction setPrioritySetting;

      public ContainerHandler(final ShopContainer var2, ShopContainer.ContainerWorkSetting<AtomicBoolean> var3) {
         super(var2, var3);
         this.subscribe = (BooleanCustomAction)var2.registerAction(new BooleanCustomAction() {
            protected void run(boolean var1) {
               ContainerHandler.this.subscribed.set(var1);
               if (var1) {
                  ShopContainer var10000 = var2;
                  Predicate var10002 = (var1x) -> {
                     return var1x.mobUniqueID == HumanWorkPrioritiesSetting.thisx.mob.getUniqueID();
                  };
                  AtomicBoolean var10003 = ContainerHandler.this.subscribed;
                  Objects.requireNonNull(var10003);
                  var10000.subscribeEvent(SettlementSettlerPrioritiesChangedEvent.class, var10002, var10003::get);
                  if (var2.client.isServer()) {
                     LevelSettler var2x = HumanWorkPrioritiesSetting.this.mob.levelSettler;
                     if (var2x != null) {
                        SettlementSettlerPrioritiesData var3 = new SettlementSettlerPrioritiesData(var2x, HumanWorkPrioritiesSetting.this.mob);
                        (new SettlementSettlerPrioritiesChangedEvent(HumanWorkPrioritiesSetting.this.mob.getUniqueID(), true, var3.priorities)).applyAndSendToClient(var2.client.getServerClient());
                     }
                  }
               }

            }
         });
         this.setPrioritySetting = (SetPrioritySettingAction)var2.registerAction(new SetPrioritySettingAction(var2));
      }

      public boolean setupWorkForm(ShopContainerForm<?> var1, DialogueForm var2) {
         if (((AtomicBoolean)this.setting.data).get()) {
            var2.addDialogueOption(new LocalMessage("ui", "settlersetworkpriority"), () -> {
               AtomicBoolean var3 = new AtomicBoolean(true);
               HashMap var4 = new HashMap();
               AtomicReference var5 = new AtomicReference();
               ShopContainer var10000 = this.container;
               Consumer var10002 = (var6) -> {
                  if (var5.get() == null) {
                     if (var6.includeDisabledBySettler) {
                        var4.putAll(var6.priorities);
                        var5.set(new SettlementJobPrioritiesForm("priorities", 408, 250, HumanWorkPrioritiesSetting.this.mob, var4) {
                           public void onSubmitUpdate(JobType var1, SettlementSettlerPrioritiesData.TypePriority var2) {
                              ContainerHandler.this.setPrioritySetting.runAndSendChange(var1, var2.priority, var2.disabledByPlayer);
                           }

                           public void onBack() {
                              var3.makeCurrent(var4);
                           }

                           public void onWindowResized() {
                              super.onWindowResized();
                              ContainerComponent.setPosFocus(this);
                           }
                        });
                        var1.addAndMakeCurrentTemporary((SettlementJobPrioritiesForm)var5.get(), () -> {
                           var3.set(false);
                           this.subscribe.runAndSend(false);
                        });
                        ((SettlementJobPrioritiesForm)var5.get()).onWindowResized();
                     }
                  } else {
                     Iterator var7 = var6.priorities.entrySet().iterator();

                     while(var7.hasNext()) {
                        Map.Entry var8 = (Map.Entry)var7.next();
                        SettlementSettlerPrioritiesData.TypePriority var9 = (SettlementSettlerPrioritiesData.TypePriority)var4.get(var8.getKey());
                        if (var9 != null) {
                           var9.priority = ((SettlementSettlerPrioritiesData.TypePriority)var8.getValue()).priority;
                           var9.disabledByPlayer = ((SettlementSettlerPrioritiesData.TypePriority)var8.getValue()).disabledByPlayer;
                           ((SettlementJobPrioritiesForm)var5.get()).updatePrioritiesContent();
                        }
                     }
                  }

               };
               Objects.requireNonNull(var3);
               var10000.onEvent(SettlementSettlerPrioritiesChangedEvent.class, var10002, var3::get);
               this.subscribe.runAndSend(true);
            });
            return true;
         } else {
            return false;
         }
      }
   }
}
