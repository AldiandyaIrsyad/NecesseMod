package necesse.entity.mobs.friendly.human.humanWorkSetting;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;
import java.util.function.Predicate;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.gfx.forms.presets.containerComponent.settlement.diets.SettlementDietFilterForm;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerDietChangedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class HumanDietFilterSetting extends HumanWorkSetting<AtomicBoolean> {
   public final HumanMob mob;

   public HumanDietFilterSetting(HumanMob var1) {
      this.mob = var1;
   }

   public void writeContainerPacket(ServerClient var1, PacketWriter var2) {
      SettlementLevelData var3 = this.mob.isSettler() ? this.mob.getSettlementLevelData() : null;
      var2.putNextBoolean(var3 != null && var3.getLevel().settlementLayer.doesClientHaveAccess(var1) && this.mob.doesEatFood());
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
      private SetDietSettingAction setDietSetting;

      public ContainerHandler(final ShopContainer var2, ShopContainer.ContainerWorkSetting<AtomicBoolean> var3) {
         super(var2, var3);
         this.subscribe = (BooleanCustomAction)var2.registerAction(new BooleanCustomAction() {
            protected void run(boolean var1) {
               ContainerHandler.this.subscribed.set(var1);
               if (var1) {
                  ShopContainer var10000 = var2;
                  Predicate var10002 = (var1x) -> {
                     return var1x.mobUniqueID == HumanDietFilterSetting.thisx.mob.getUniqueID();
                  };
                  AtomicBoolean var10003 = ContainerHandler.this.subscribed;
                  Objects.requireNonNull(var10003);
                  var10000.subscribeEvent(SettlementSettlerDietChangedEvent.class, var10002, var10003::get);
                  if (var2.client.isServer()) {
                     LevelSettler var2x = HumanDietFilterSetting.this.mob.levelSettler;
                     if (var2x != null) {
                        (new SettlementSettlerDietChangedEvent(HumanDietFilterSetting.this.mob.getUniqueID(), ItemCategoriesFilterChange.fullChange(var2x.dietFilter))).applyAndSendToClient(var2.client.getServerClient());
                     }
                  }
               }

            }
         });
         this.setDietSetting = (SetDietSettingAction)var2.registerAction(new SetDietSettingAction(var2));
      }

      public boolean setupWorkForm(ShopContainerForm<?> var1, DialogueForm var2) {
         if (((AtomicBoolean)this.setting.data).get()) {
            var2.addDialogueOption(new LocalMessage("ui", "settlersetdiet"), () -> {
               AtomicBoolean var3 = new AtomicBoolean(true);
               AtomicBoolean var4 = new AtomicBoolean(false);
               ItemCategoriesFilter var5 = new ItemCategoriesFilter(ItemCategory.foodQualityMasterCategory, true);
               ShopContainer var10000 = this.container;
               Consumer var10002 = (var6) -> {
                  var6.change.applyTo(var5);
                  if (!var4.get()) {
                     SettlementDietFilterForm var7 = new SettlementDietFilterForm("diet", 408, 250, HumanDietFilterSetting.this.mob, var5, var1.getClient()) {
                        public void onItemsChanged(Item[] var1, boolean var2) {
                           ContainerHandler.this.setDietSetting.runAndSendChange(ItemCategoriesFilterChange.itemsAllowed(var1, var2));
                        }

                        public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2) {
                           ContainerHandler.this.setDietSetting.runAndSendChange(ItemCategoriesFilterChange.categoryAllowed(var1, var2));
                        }

                        public void onFullChange(ItemCategoriesFilter var1) {
                           ContainerHandler.this.setDietSetting.runAndSendChange(ItemCategoriesFilterChange.fullChange(var1));
                        }

                        public void onButtonPressed() {
                           var3.makeCurrent(var4);
                        }

                        public void onWindowResized() {
                           super.onWindowResized();
                           ContainerComponent.setPosFocus(this);
                        }
                     };
                     var1.addAndMakeCurrentTemporary(var7, () -> {
                        var3.set(false);
                        this.subscribe.runAndSend(false);
                     });
                     var7.onWindowResized();
                     var4.set(true);
                  }

               };
               Objects.requireNonNull(var3);
               var10000.onEvent(SettlementSettlerDietChangedEvent.class, var10002, var3::get);
               this.subscribe.runAndSend(true);
            });
            return true;
         } else {
            return false;
         }
      }
   }
}
