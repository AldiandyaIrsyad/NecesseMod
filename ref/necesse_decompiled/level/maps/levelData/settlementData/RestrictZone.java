package necesse.level.maps.levelData.settlementData;

import java.awt.Rectangle;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.Zoning;
import necesse.engine.util.ZoningChange;

public class RestrictZone {
   public final SettlementLevelData data;
   public final int uniqueID;
   public int index;
   public GameMessage name = new LocalMessage("ui", "settlementareadefname", new Object[]{"number", 0});
   public int colorHue = 120;
   private Zoning zoning;

   public RestrictZone(SettlementLevelData var1, int var2, int var3, GameMessage var4) {
      this.data = var1;
      this.uniqueID = var2;
      this.index = var3;
      this.name = var4;
      this.zoning = new Zoning(new Rectangle(var1.getLevel().width, var1.getLevel().height));
   }

   public RestrictZone(SettlementLevelData var1, int var2, LoadData var3) {
      this.data = var1;
      this.index = var2;

      try {
         this.uniqueID = var3.getInt("uniqueID");
      } catch (Exception var5) {
         throw new LoadDataException("Could not load restrict zone uniqueID");
      }

      LoadData var4 = var3.getFirstLoadDataByName("name");
      if (var4 != null) {
         this.name = GameMessage.loadSave(var4);
      }

      this.colorHue = var3.getInt("color", this.colorHue);
      this.zoning = new Zoning(new Rectangle(var1.getLevel().width, var1.getLevel().height));
      this.zoning.applyZoneSaveData("zone", var3);
   }

   public void addSaveData(SaveData var1) {
      var1.addInt("uniqueID", this.uniqueID);
      var1.addSaveData(this.name.getSaveData("name"));
      var1.addInt("color", this.colorHue);
      this.zoning.addZoneSaveData("zone", var1);
   }

   public ZoningChange getFullChange() {
      return ZoningChange.full(this.zoning);
   }

   public boolean isEmpty() {
      return this.zoning.isEmpty();
   }

   public boolean containsTile(int var1, int var2) {
      return this.zoning.containsTile(var1, var2);
   }

   public boolean applyChange(ZoningChange var1) {
      return var1.applyTo(this.zoning);
   }

   public boolean expand(Rectangle var1) {
      return this.zoning.addRectangle(var1);
   }

   public boolean shrink(Rectangle var1) {
      return this.zoning.removeRectangle(var1);
   }

   public void copyZoneFrom(RestrictZone var1) {
      this.zoning.addRectangles(var1.zoning.getTileRectangles());
   }
}
