package necesse.engine.commands.serverCommands.setupCommand;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import necesse.engine.network.packet.PacketPlayerGeneral;
import necesse.engine.network.server.ServerClient;

public abstract class CharacterBuild implements Comparable<CharacterBuild> {
   public final int applyPriority;

   public CharacterBuild(int var1) {
      this.applyPriority = var1;
   }

   public CharacterBuild() {
      this(0);
   }

   public abstract void apply(ServerClient var1);

   public void addApplies(List<CharacterBuild> var1) {
      var1.add(this);
   }

   public int compareTo(CharacterBuild var1) {
      return Integer.compare(this.applyPriority, var1.applyPriority);
   }

   public static void apply(ServerClient var0, CharacterBuildEntry... var1) {
      ArrayList var2 = new ArrayList();
      CharacterBuildEntry[] var3 = var1;
      int var4 = var1.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         CharacterBuildEntry var6 = var3[var5];
         var6.build.addApplies(var2);
      }

      var2.sort((Comparator)null);
      if (var1.length > 0) {
         Iterator var7 = var2.iterator();

         while(var7.hasNext()) {
            CharacterBuild var8 = (CharacterBuild)var7.next();
            var8.apply(var0);
         }

         if (var0.hasSpawned()) {
            var0.getServer().network.sendToAllClients(new PacketPlayerGeneral(var0));
         }
      }

   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((CharacterBuild)var1);
   }
}
