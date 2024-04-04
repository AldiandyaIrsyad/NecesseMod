package necesse.entity.mobs.friendly.human.humanWorkSetting;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.server.ServerClient;
import necesse.entity.mobs.friendly.human.HumanMob;
import necesse.gfx.forms.ContainerComponent;
import necesse.gfx.forms.presets.containerComponent.mob.DialogueForm;
import necesse.gfx.forms.presets.containerComponent.mob.EquipmentFiltersForm;
import necesse.gfx.forms.presets.containerComponent.mob.ShopContainerForm;
import necesse.inventory.container.customAction.BooleanCustomAction;
import necesse.inventory.container.mob.ShopContainer;
import necesse.inventory.container.settlement.events.SettlementSettlerEquipmentFilterChangedEvent;
import necesse.inventory.item.Item;
import necesse.inventory.item.ItemCategory;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.inventory.itemFilter.ItemCategoriesFilterChange;
import necesse.level.maps.levelData.settlementData.LevelSettler;
import necesse.level.maps.levelData.settlementData.SettlementLevelData;

public class HumanEquipmentFilterSetting extends HumanWorkSetting<AtomicBoolean> {
   public final HumanMob mob;

   public HumanEquipmentFilterSetting(HumanMob var1) {
      this.mob = var1;
   }

   public void writeContainerPacket(ServerClient var1, PacketWriter var2) {
      SettlementLevelData var3 = this.mob.isSettler() ? this.mob.getSettlementLevelData() : null;
      var2.putNextBoolean(var3 != null && var3.getLevel().settlementLayer.doesClientHaveAccess(var1) && this.mob.levelSettler != null);
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
      private SetEquipmentFilterSettingAction setEquipmentFilterSetting;

      public ContainerHandler(final ShopContainer var2, ShopContainer.ContainerWorkSetting<AtomicBoolean> var3) {
         super(var2, var3);
         this.subscribe = (BooleanCustomAction)var2.registerAction(new BooleanCustomAction() {
            protected void run(boolean var1) {
               ContainerHandler.this.subscribed.set(var1);
               if (var1) {
                  ShopContainer var10000 = var2;
                  Predicate var10002 = (var1x) -> {
                     return var1x.mobUniqueID == HumanEquipmentFilterSetting.thisx.mob.getUniqueID();
                  };
                  AtomicBoolean var10003 = ContainerHandler.this.subscribed;
                  Objects.requireNonNull(var10003);
                  var10000.subscribeEvent(SettlementSettlerEquipmentFilterChangedEvent.class, var10002, var10003::get);
                  if (var2.client.isServer()) {
                     LevelSettler var2x = HumanEquipmentFilterSetting.this.mob.levelSettler;
                     if (var2x != null) {
                        (new SettlementSettlerEquipmentFilterChangedEvent(HumanEquipmentFilterSetting.this.mob.getUniqueID(), var2x.preferArmorSets, ItemCategoriesFilterChange.fullChange(var2x.equipmentFilter))).applyAndSendToClient(var2.client.getServerClient());
                     }
                  }
               }

            }
         });
         this.setEquipmentFilterSetting = (SetEquipmentFilterSettingAction)var2.registerAction(new SetEquipmentFilterSettingAction(var2));
      }

      public boolean setupWorkForm(ShopContainerForm<?> var1, DialogueForm var2) {
         if (((AtomicBoolean)this.setting.data).get()) {
            var1.equipmentForm.filterEquipmentButtonPressed = () -> {
               AtomicBoolean var2 = new AtomicBoolean(true);
               AtomicReference var3 = new AtomicReference();
               ItemCategoriesFilter var4 = new ItemCategoriesFilter(ItemCategory.equipmentMasterCategory, true);
               AtomicBoolean var5 = new AtomicBoolean();
               ShopContainer var10000 = this.container;
               Consumer var10002 = (var6) -> {
                  if (var3.get() == null) {
                     if (var6.change != null) {
                        var6.change.applyTo(var4);
                     }

                     var5.set(var6.preferArmorSets);
                     HumanMob var10006 = HumanEquipmentFilterSetting.this.mob;
                     Objects.requireNonNull(var5);
                     EquipmentFiltersForm var7 = new EquipmentFiltersForm("equipmentFilter", 408, 300, var10006, var4, var5::get, var1.getClient()) {
                        public void onSetPreferArmorSets(boolean var1) {
                           var3.set(var1);
                           ContainerHandler.this.setEquipmentFilterSetting.runAndSendPreferArmorSets(var1);
                        }

                        public void onItemsChanged(Item[] var1, boolean var2) {
                           ContainerHandler.this.setEquipmentFilterSetting.runAndSendChange(ItemCategoriesFilterChange.itemsAllowed(var1, var2));
                        }

                        public void onCategoryChanged(ItemCategoriesFilter.ItemCategoryFilter var1, boolean var2) {
                           ContainerHandler.this.setEquipmentFilterSetting.runAndSendChange(ItemCategoriesFilterChange.categoryAllowed(var1, var2));
                        }

                        public void onFullChange(ItemCategoriesFilter var1) {
                           ContainerHandler.this.setEquipmentFilterSetting.runAndSendChange(ItemCategoriesFilterChange.fullChange(var1));
                        }

                        public void onButtonPressed() {
                           var4.makeCurrent(var4.equipmentForm);
                        }

                        public void onWindowResized() {
                           super.onWindowResized();
                           ContainerComponent.setPosFocus(this);
                        }
                     };
                     var1.addAndMakeCurrentTemporary(var7, () -> {
                        var2.set(false);
                        var3.set((Object)null);
                        this.subscribe.runAndSend(false);
                     });
                     var7.onWindowResized();
                     var3.set(var7);
                  } else {
                     var5.set(var6.preferArmorSets);
                     if (var6.change != null) {
                        var6.change.applyTo(var4);
                     }
                  }

               };
               Objects.requireNonNull(var2);
               var10000.onEvent(SettlementSettlerEquipmentFilterChangedEvent.class, var10002, var2::get);
               this.subscribe.runAndSend(true);
            };
         }

         return false;
      }
   }
}
