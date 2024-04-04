package necesse.engine.world.worldData.incursions;

import java.util.Objects;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.engine.util.LevelIdentifier;
import necesse.level.maps.incursion.IncursionData;

public class OpenIncursion {
   public final LevelIdentifier incursionLevelIdentifier;
   public final IncursionData incursionData;
   public boolean canComplete;

   public OpenIncursion(LevelIdentifier var1, IncursionData var2) {
      Objects.requireNonNull(var1);
      Objects.requireNonNull(var2);
      this.incursionLevelIdentifier = var1;
      this.incursionData = var2;
   }

   public OpenIncursion(LoadData var1) {
      String var2 = var1.getUnsafeString("incursionLevel", (String)null, false);
      if (var2 == null) {
         throw new LoadDataException("Open incursion did not have level identifier");
      } else {
         this.incursionLevelIdentifier = new LevelIdentifier(var2);
         LoadData var3 = var1.getFirstLoadDataByName("incursionData");
         this.incursionData = IncursionData.fromLoadData(var3);
         this.canComplete = var1.getBoolean("canComplete", this.canComplete, false);
      }
   }

   public void addSaveData(SaveData var1) {
      var1.addUnsafeString("incursionLevel", this.incursionLevelIdentifier.stringID);
      SaveData var2 = new SaveData("incursionData");
      this.incursionData.addSaveData(var2);
      var1.addSaveData(var2);
      var1.addBoolean("canComplete", this.canComplete);
   }

   public OpenIncursion(PacketReader var1) {
      this.incursionLevelIdentifier = new LevelIdentifier(var1);
      this.incursionData = IncursionData.fromPacket(var1);
      this.canComplete = var1.getNextBoolean();
   }

   public void writePacket(PacketWriter var1) {
      this.incursionLevelIdentifier.writePacket(var1);
      IncursionData.writePacket(this.incursionData, var1);
      var1.putNextBoolean(this.canComplete);
   }
}
