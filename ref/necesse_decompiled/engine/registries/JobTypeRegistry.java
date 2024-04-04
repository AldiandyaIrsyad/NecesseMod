package necesse.engine.registries;

import java.util.Iterator;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.entity.mobs.job.JobType;

public class JobTypeRegistry extends StaticObjectGameRegistry<JobType> {
   public static final JobTypeRegistry instance = new JobTypeRegistry();

   private JobTypeRegistry() {
      super("JobType", 32766);
   }

   public void registerCore() {
      registerType("needs", new JobType(false, false, (var0) -> {
         return var0.getWorldSettings().jobSearchRange;
      }, (GameMessage)null, (GameMessage)null));
      registerType("hauling", new JobType(true, false, (var0) -> {
         return var0.getWorldSettings().jobSearchRange;
      }, new LocalMessage("jobs", "haulingname"), new LocalMessage("jobs", "haulingtip")));
      registerType("crafting", new JobType(true, false, (var0) -> {
         return var0.getWorldSettings().jobSearchRange;
      }, new LocalMessage("jobs", "craftingname"), new LocalMessage("jobs", "craftingtip")));
      registerType("forestry", new JobType(true, false, (var0) -> {
         return var0.getWorldSettings().jobSearchRange;
      }, new LocalMessage("jobs", "forestryname"), new LocalMessage("jobs", "forestrytip")));
      registerType("farming", new JobType(true, false, (var0) -> {
         return var0.getWorldSettings().jobSearchRange;
      }, new LocalMessage("jobs", "farmingname"), new LocalMessage("jobs", "farmingtip")));
      registerType("fertilize", new JobType(true, true, (var0) -> {
         return var0.getWorldSettings().jobSearchRange;
      }, new LocalMessage("jobs", "fertilizename"), new LocalMessage("jobs", "fertilizetip")));
      registerType("husbandry", new JobType(true, true, (var0) -> {
         return var0.getWorldSettings().jobSearchRange;
      }, new LocalMessage("jobs", "husbandryname"), new LocalMessage("jobs", "husbandrytip")));
      registerType("fishing", new JobType(true, true, (var0) -> {
         return var0.getWorldSettings().jobSearchRange;
      }, new LocalMessage("jobs", "fishingname"), (GameMessage)null));
      registerType("hunting", new JobType(true, true, (var0) -> {
         return var0.getWorldSettings().jobSearchRange;
      }, new LocalMessage("jobs", "huntingname"), new LocalMessage("jobs", "huntingtip")));
   }

   protected void onRegister(JobType var1, int var2, String var3, boolean var4) {
   }

   protected void onRegistryClose() {
      Iterator var1 = this.getElements().iterator();

      while(var1.hasNext()) {
         JobType var2 = (JobType)var1.next();
         var2.onJobTypeRegistryClosed();
      }

   }

   public static int registerType(String var0, JobType var1) {
      return instance.register(var0, var1);
   }

   public static JobType getJobType(int var0) {
      return (JobType)instance.getElement(var0);
   }

   public static JobType getJobType(String var0) {
      return (JobType)instance.getElement(var0);
   }

   public static int getJobTypeID(String var0) {
      return instance.getElementID(var0);
   }

   public static Iterable<JobType> getTypes() {
      return instance.getElements();
   }

   public static Stream<JobType> streamTypes() {
      return instance.streamElements();
   }

   // $FF: synthetic method
   // $FF: bridge method
   protected void onRegister(IDDataContainer var1, int var2, String var3, boolean var4) {
      this.onRegister((JobType)var1, var2, var3, var4);
   }
}
