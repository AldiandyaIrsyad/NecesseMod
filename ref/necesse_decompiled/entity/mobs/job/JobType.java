package necesse.entity.mobs.job;

import java.util.function.Function;
import necesse.engine.GameTileRange;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.registries.IDData;
import necesse.engine.registries.IDDataContainer;
import necesse.engine.registries.JobTypeRegistry;
import necesse.engine.registries.RegistryClosedException;
import necesse.level.maps.Level;

public class JobType implements IDDataContainer {
   public final IDData idData = new IDData();
   public boolean canChangePriority;
   public boolean defaultDisabledBySettler;
   public GameMessage displayName;
   public GameMessage tooltip;
   public Function<Level, GameTileRange> tileRange;

   public IDData getIDData() {
      return this.idData;
   }

   public final int getID() {
      return this.idData.getID();
   }

   public String getStringID() {
      return this.idData.getStringID();
   }

   public JobType(boolean var1, boolean var2, Function<Level, GameTileRange> var3, GameMessage var4, GameMessage var5) {
      if (JobTypeRegistry.instance.isClosed()) {
         throw new RegistryClosedException("Cannot construct JobType objects when job type registry is closed, since they are a static registered objects. Use JobTypeRegistry.getJobType(...) to get job types.");
      } else {
         this.canChangePriority = var1;
         this.defaultDisabledBySettler = var2;
         this.tileRange = var3;
         this.displayName = var4;
         this.tooltip = var5;
      }
   }

   public void onJobTypeRegistryClosed() {
   }
}
