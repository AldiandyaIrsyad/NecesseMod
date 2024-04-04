package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Color;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.function.BooleanSupplier;
import java.util.stream.Collectors;
import necesse.engine.Settings;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.entity.mobs.friendly.HusbandryMob;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementHusbandryZoneConfigForm;
import necesse.gfx.forms.presets.containerComponent.settlement.WorkZoneConfigComponent;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementHusbandryZoneUpdateEvent;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.jobs.MilkHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.ShearHusbandryMobLevelJob;
import necesse.level.maps.levelData.jobs.SlaughterHusbandryMobLevelJob;

public class SettlementHusbandryZone extends SettlementWorkZone {
   protected int maxAnimalsBeforeSlaughter = -1;

   public SettlementHusbandryZone() {
   }

   public void addSaveData(SaveData var1) {
      super.addSaveData(var1);
      var1.addInt("maxAnimalsBeforeSlaughter", this.maxAnimalsBeforeSlaughter);
   }

   public void applySaveData(LoadData var1, Collection<SettlementWorkZone> var2) {
      super.applySaveData(var1, var2);
      this.maxAnimalsBeforeSlaughter = var1.getInt("maxAnimalsBeforeSlaughter", this.maxAnimalsBeforeSlaughter);
   }

   public void writePacket(PacketWriter var1) {
      super.writePacket(var1);
      var1.putNextInt(this.maxAnimalsBeforeSlaughter);
   }

   public void applyPacket(PacketReader var1) {
      super.applyPacket(var1);
      this.maxAnimalsBeforeSlaughter = var1.getNextInt();
   }

   public void tickJobs() {
      Rectangle var1 = this.getTileBounds();
      Rectangle var2 = new Rectangle(var1.x * 32, var1.y * 32, var1.width * 32, var1.height * 32);
      List var3 = (List)this.manager.data.getLevel().entityManager.mobs.streamInRegionsShape(var2, 0).filter((var1x) -> {
         return !this.containsTile(var1x.getTileX(), var1x.getTileY()) ? false : var1x instanceof HusbandryMob;
      }).map((var0) -> {
         return (HusbandryMob)var0;
      }).collect(Collectors.toList());
      int var4 = 0;

      for(Iterator var5 = var3.iterator(); var5.hasNext(); ++var4) {
         HusbandryMob var6 = (HusbandryMob)var5.next();
         MilkHusbandryMobLevelJob var7 = var6.milkJob;
         var6.milkJob = new MilkHusbandryMobLevelJob(var6, this);
         if (var7 != null) {
            var6.milkJob.reservable = var7.reservable;
         }

         ShearHusbandryMobLevelJob var8 = var6.shearJob;
         var6.shearJob = new ShearHusbandryMobLevelJob(var6, this);
         if (var8 != null) {
            var6.shearJob.reservable = var8.reservable;
         }
      }

      if (this.maxAnimalsBeforeSlaughter >= 0) {
         int var9 = var4 - this.maxAnimalsBeforeSlaughter;
         Iterator var10 = var3.iterator();

         HusbandryMob var11;
         while(var10.hasNext()) {
            var11 = (HusbandryMob)var10.next();
            if (var11.slaughterJob != null) {
               if (var9 <= 0) {
                  var11.slaughterJob.remove();
                  var11.slaughterJob = null;
               } else {
                  SlaughterHusbandryMobLevelJob var12 = var11.slaughterJob;
                  var11.slaughterJob = new SlaughterHusbandryMobLevelJob(var11, this);
                  var11.slaughterJob.reservable = var12.reservable;
                  --var9;
               }
            }
         }

         if (var9 > 0) {
            var10 = var3.iterator();

            while(var10.hasNext()) {
               var11 = (HusbandryMob)var10.next();
               if (var11.isGrown() && var11.slaughterJob == null) {
                  var11.slaughterJob = new SlaughterHusbandryMobLevelJob(var11, this);
                  --var9;
                  if (var9 <= 0) {
                     break;
                  }
               }
            }
         }
      }

   }

   public boolean isHiddenSetting() {
      return (Boolean)Settings.hideSettlementHusbandryZones.get();
   }

   public GameMessage getDefaultName(int var1) {
      return new LocalMessage("ui", "settlementhusbandryzonedefname", new Object[]{"number", var1});
   }

   public GameMessage getAbstractName() {
      return new LocalMessage("ui", "settlementhusbandryzone");
   }

   public HudDrawElement getHudDrawElement(int var1, BooleanSupplier var2) {
      return this.getHudDrawElement(var1, var2, new Color(239, 194, 238, 150), new Color(213, 93, 212, 75));
   }

   public int getMaxAnimalsBeforeSlaughter() {
      return this.maxAnimalsBeforeSlaughter;
   }

   public void setMaxAnimalsBeforeSlaughter(int var1) {
      this.maxAnimalsBeforeSlaughter = var1;
      if (this.manager != null && !this.isRemoved()) {
         (new SettlementHusbandryZoneUpdateEvent(this)).applyAndSendToClientsAt(this.manager.data.getLevel());
      }

   }

   public void subscribeConfigEvents(SettlementContainer var1, BooleanSupplier var2) {
      super.subscribeConfigEvents(var1, var2);
      var1.subscribeEvent(SettlementHusbandryZoneUpdateEvent.class, (var1x) -> {
         return var1x.uniqueID == this.getUniqueID();
      }, var2);
   }

   public void writeSettingsForm(PacketWriter var1) {
      var1.putNextInt(this.maxAnimalsBeforeSlaughter);
   }

   public WorkZoneConfigComponent getSettingsForm(SettlementAssignWorkForm<?> var1, Runnable var2, PacketReader var3) {
      this.maxAnimalsBeforeSlaughter = var3.getNextInt();
      return new SettlementHusbandryZoneConfigForm(var1, this, var2);
   }
}
