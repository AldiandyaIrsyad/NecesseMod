package necesse.level.maps.levelData.settlementData.zones;

import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiPredicate;
import java.util.function.BooleanSupplier;
import java.util.function.Predicate;
import necesse.engine.GameLog;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.IDData;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.tickManager.TickManager;
import necesse.engine.util.GameRandom;
import necesse.engine.util.Zoning;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.drawOptions.texture.SharedTextureDrawOptions;
import necesse.gfx.drawables.SortedDrawable;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementAssignWorkForm;
import necesse.gfx.forms.presets.containerComponent.settlement.SettlementBasicZoneConfigForm;
import necesse.gfx.forms.presets.containerComponent.settlement.WorkZoneConfigComponent;
import necesse.inventory.container.settlement.SettlementContainer;
import necesse.inventory.container.settlement.events.SettlementWorkZoneNameEvent;
import necesse.level.maps.Level;
import necesse.level.maps.hudManager.HudDrawElement;
import necesse.level.maps.levelData.settlementData.SettlementWorkZoneManager;

public abstract class SettlementWorkZone {
   public final IDData idData = new IDData();
   protected SettlementWorkZoneManager manager;
   protected final Zoning zoning = new Zoning();
   protected boolean removed;
   protected GameMessage name = this.getDefaultName(0);
   private int uniqueID;

   public final String getStringID() {
      return this.idData.getStringID();
   }

   public final int getID() {
      return this.idData.getID();
   }

   public SettlementWorkZone() {
   }

   public void init(SettlementWorkZoneManager var1) {
      this.manager = var1;
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("uniqueID", this.uniqueID);
      var1.addSaveData(this.name.getSaveData("name"));
      this.zoning.addZoneSaveData("areas", var1);
   }

   public void applySaveData(LoadData var1, Collection<SettlementWorkZone> var2) {
      this.uniqueID = var1.getInt("uniqueID", this.uniqueID);
      LoadData var3 = var1.getFirstLoadDataByName("name");
      if (var3 != null) {
         this.name = GameMessage.loadSave(var3);
      } else {
         this.generateDefaultName(var2);
      }

      try {
         this.zoning.applyZoneSaveData("areas", var1);
      } catch (LoadDataException var5) {
         GameLog.warn.println("Error loading settlement zone: " + var5.getMessage());
      }

   }

   public void writePacket(PacketWriter var1) {
      synchronized(this.zoning) {
         this.zoning.writeZonePacket(var1);
      }

      this.name.writePacket(var1);
   }

   public void applyPacket(PacketReader var1) {
      synchronized(this.zoning) {
         this.zoning.readZonePacket(var1);
      }

      this.name = GameMessage.fromPacket(var1);
   }

   public int getUniqueID() {
      if (this.uniqueID == 0) {
         this.uniqueID = GameRandom.globalRandom.nextInt();
      }

      return this.uniqueID;
   }

   public void generateUniqueID(Predicate<Integer> var1) {
      if (this.uniqueID != 0) {
         throw new IllegalStateException("Cannot change the uniqueID of a zone once already set");
      } else {
         this.uniqueID = 1;

         for(int var2 = 0; var2 < 1000; ++var2) {
            this.uniqueID = GameRandom.globalRandom.nextInt();
            if (this.uniqueID != 0 && this.uniqueID != 1 && !var1.test(this.uniqueID)) {
               break;
            }
         }

      }
   }

   public void setUniqueID(int var1) {
      if (this.uniqueID != 0) {
         throw new IllegalStateException("Cannot change the uniqueID of a zone once already set");
      } else {
         this.uniqueID = var1;
      }
   }

   public void tickSecond() {
   }

   public void tickJobs() {
   }

   public boolean fixOverlaps(BiPredicate<Integer, Integer> var1) {
      LinkedList var2 = new LinkedList();
      Iterator var3 = this.zoning.getTiles().iterator();

      while(var3.hasNext()) {
         Point var4 = (Point)var3.next();
         if (var1.test(var4.x, var4.y)) {
            var2.add(var4);
         }
      }

      synchronized(this.zoning) {
         Iterator var8 = var2.iterator();

         while(var8.hasNext()) {
            Point var5 = (Point)var8.next();
            this.zoning.removeTile(var5.x, var5.y);
         }

         return !var2.isEmpty();
      }
   }

   public boolean expandZone(Level var1, Rectangle var2, Point var3, BiPredicate<Integer, Integer> var4) {
      boolean var5 = false;
      var2 = var2.intersection(new Rectangle(var1.width, var1.height));

      for(int var6 = 0; var6 < var2.width; ++var6) {
         int var7 = var2.x + var6;

         for(int var8 = 0; var8 < var2.height; ++var8) {
            int var9 = var2.y + var8;
            if (!var4.test(var7, var9)) {
               synchronized(this.zoning) {
                  var5 = this.zoning.addTile(var7, var9) || var5;
               }
            }
         }
      }

      if (var5) {
         synchronized(this.zoning) {
            if (var3 != null) {
               this.zoning.removeDisconnected(var3.x, var3.y);
            } else {
               this.zoning.removeDisconnected();
            }

            return true;
         }
      } else {
         return false;
      }
   }

   public boolean shrinkZone(Level var1, Rectangle var2) {
      boolean var3 = false;
      var2 = var2.intersection(new Rectangle(var1.width, var1.height));

      for(int var4 = 0; var4 < var2.width; ++var4) {
         int var5 = var2.x + var4;

         for(int var6 = 0; var6 < var2.height; ++var6) {
            int var7 = var2.y + var6;
            synchronized(this.zoning) {
               var3 = this.zoning.removeTile(var5, var7) || var3;
            }
         }
      }

      if (var3) {
         synchronized(this.zoning) {
            this.zoning.removeDisconnected();
            return true;
         }
      } else {
         return false;
      }
   }

   public boolean containsTile(int var1, int var2) {
      return this.zoning.containsTile(var1, var2);
   }

   public boolean isEmpty() {
      return this.zoning.isEmpty();
   }

   public int size() {
      return this.zoning.size();
   }

   public Rectangle getTileBounds() {
      return this.zoning.getTileBounds();
   }

   public void remove() {
      this.removed = true;
   }

   public boolean isRemoved() {
      return this.removed;
   }

   protected abstract GameMessage getDefaultName(int var1);

   public abstract GameMessage getAbstractName();

   public GameMessage getName() {
      return this.name;
   }

   public void setName(GameMessage var1) {
      if (!this.name.translate().equals(var1.translate())) {
         this.name = var1;
         if (this.manager != null && !this.isRemoved()) {
            (new SettlementWorkZoneNameEvent(this)).applyAndSendToClientsAt(this.manager.data.getLevel());
         }
      }

   }

   public void generateDefaultName(Collection<SettlementWorkZone> var1) {
      int var2 = (int)var1.stream().filter((var1x) -> {
         return !var1x.isRemoved() && var1x.getID() == this.getID();
      }).count();
      AtomicInteger var3 = new AtomicInteger(var2 + 1);

      while(var1.stream().anyMatch((var2x) -> {
         return !var2x.isRemoved() && var2x.getName().translate().equals(this.getDefaultName(var3.get()).translate());
      })) {
         var3.addAndGet(1);
      }

      this.name = this.getDefaultName(var3.get());
   }

   public HudDrawElement getHudDrawElement(int var1, BooleanSupplier var2) {
      return this.getHudDrawElement(var1, var2, new Color(0, 0, 255, 170), new Color(0, 0, 255, 100));
   }

   public HudDrawElement getHudDrawElement(final int var1, final BooleanSupplier var2, final Color var3, final Color var4) {
      return new HudDrawElement() {
         public void addDrawables(List<SortedDrawable> var1x, GameCamera var2x, PlayerMob var3x) {
            if (!SettlementWorkZone.this.isHiddenSetting() || var2 != null && var2.getAsBoolean()) {
               synchronized(SettlementWorkZone.this.zoning) {
                  final SharedTextureDrawOptions var5 = SettlementWorkZone.this.zoning.getDrawOptions(var3, var4, var2x);
                  if (var5 != null) {
                     var1x.add(new SortedDrawable() {
                        public int getPriority() {
                           return var1;
                        }

                        public void draw(TickManager var1x) {
                           var5.draw();
                        }
                     });
                  }

               }
            }
         }
      };
   }

   public boolean isHiddenSetting() {
      return false;
   }

   public boolean shouldRemove() {
      return this.isEmpty();
   }

   public void subscribeConfigEvents(SettlementContainer var1, BooleanSupplier var2) {
      var1.subscribeEvent(SettlementWorkZoneNameEvent.class, (var1x) -> {
         return var1x.uniqueID == this.getUniqueID();
      }, var2);
   }

   public boolean canConfigure() {
      return true;
   }

   public void writeSettingsForm(PacketWriter var1) {
   }

   public WorkZoneConfigComponent getSettingsForm(SettlementAssignWorkForm<?> var1, Runnable var2, PacketReader var3) {
      return new SettlementBasicZoneConfigForm(var1, this, var2);
   }
}
