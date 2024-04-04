package necesse.inventory.container.settlement.events;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.registries.JobTypeRegistry;
import necesse.entity.mobs.job.JobType;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.settlement.data.SettlementSettlerPrioritiesData;

public class SettlementSettlerPrioritiesChangedEvent extends ContainerEvent {
   public final int mobUniqueID;
   public final boolean includeDisabledBySettler;
   public final HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> priorities;

   public SettlementSettlerPrioritiesChangedEvent(int var1, JobType var2, int var3, boolean var4) {
      this.mobUniqueID = var1;
      this.includeDisabledBySettler = false;
      this.priorities = new HashMap();
      this.priorities.put(var2, new SettlementSettlerPrioritiesData.TypePriority(false, var3, var4));
   }

   public SettlementSettlerPrioritiesChangedEvent(int var1, boolean var2, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> var3) {
      this.mobUniqueID = var1;
      this.includeDisabledBySettler = var2;
      this.priorities = var3;
   }

   public SettlementSettlerPrioritiesChangedEvent(int var1, HashMap<JobType, SettlementSettlerPrioritiesData.TypePriority> var2) {
      this(var1, false, var2);
   }

   public SettlementSettlerPrioritiesChangedEvent(PacketReader var1) {
      super(var1);
      this.mobUniqueID = var1.getNextInt();
      this.includeDisabledBySettler = var1.getNextBoolean();
      int var2 = var1.getNextShortUnsigned();
      this.priorities = new HashMap(var2);

      for(int var3 = 0; var3 < var2; ++var3) {
         int var4 = var1.getNextShortUnsigned();
         boolean var5 = this.includeDisabledBySettler && var1.getNextBoolean();
         JobType var6 = JobTypeRegistry.getJobType(var4);
         int var7 = var1.getNextInt();
         boolean var8 = var1.getNextBoolean();
         if (var6 != null) {
            this.priorities.put(var6, new SettlementSettlerPrioritiesData.TypePriority(var5, var7, var8));
         }
      }

   }

   public void write(PacketWriter var1) {
      var1.putNextInt(this.mobUniqueID);
      var1.putNextBoolean(this.includeDisabledBySettler);
      var1.putNextShortUnsigned(this.priorities.size());
      Iterator var2 = this.priorities.entrySet().iterator();

      while(var2.hasNext()) {
         Map.Entry var3 = (Map.Entry)var2.next();
         var1.putNextShortUnsigned(((JobType)var3.getKey()).getID());
         if (this.includeDisabledBySettler) {
            var1.putNextBoolean(((SettlementSettlerPrioritiesData.TypePriority)var3.getValue()).disabledBySettler);
         }

         var1.putNextInt(((SettlementSettlerPrioritiesData.TypePriority)var3.getValue()).priority);
         var1.putNextBoolean(((SettlementSettlerPrioritiesData.TypePriority)var3.getValue()).disabledByPlayer);
      }

   }
}
