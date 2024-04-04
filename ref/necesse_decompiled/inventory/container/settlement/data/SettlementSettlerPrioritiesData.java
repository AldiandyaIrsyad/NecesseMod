package necesse.inventory.container.settlement.data;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.JobTypeRegistry;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.JobType;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.level.maps.levelData.settlementData.LevelSettler;

public class SettlementSettlerPrioritiesData extends SettlementSettlerData {
   public final HashMap<JobType, TypePriority> priorities;

   public SettlementSettlerPrioritiesData(LevelSettler var1, EntityJobWorker var2) {
      super(var1);
      this.priorities = new HashMap();
      Iterator var3 = var2.getJobTypeHandler().getTypePriorities().iterator();

      while(var3.hasNext()) {
         JobTypeHandler.TypePriority var4 = (JobTypeHandler.TypePriority)var3.next();
         if (var4.type.canChangePriority) {
            this.priorities.put(var4.type, new TypePriority(var4.disabledBySettler, var4.priority, var4.disabledByPlayer));
         }
      }

   }

   public SettlementSettlerPrioritiesData(PacketReader var1) {
      super(var1);
      int var2 = var1.getNextShortUnsigned();
      this.priorities = new HashMap(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         JobType var5 = JobTypeRegistry.getJobType(var4);
         boolean var6 = var1.getNextBoolean();
         int var7 = var1.getNextInt();
         boolean var8 = var1.getNextBoolean();
         this.priorities.put(var5, new TypePriority(var6, var7, var8));
      }

   }

   public void writeContentPacket(PacketWriter var1) {
      super.writeContentPacket(var1);
      var1.putNextShortUnsigned(this.priorities.size());
      Iterator var2 = this.priorities.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         var1.putNextShortUnsigned(((JobType)var3.getKey()).getID());
         TypePriority var4 = (TypePriority)var3.getValue();
         var1.putNextBoolean(var4.disabledBySettler);
         var1.putNextInt(var4.priority);
         var1.putNextBoolean(var4.disabledByPlayer);
      }

   }

   public static class TypePriority {
      public final boolean disabledBySettler;
      public int priority;
      public boolean disabledByPlayer;

      public TypePriority(boolean var1, int var2, boolean var3) {
         this.disabledBySettler = var1;
         this.priority = var2;
         this.disabledByPlayer = var3;
      }
   }
}
