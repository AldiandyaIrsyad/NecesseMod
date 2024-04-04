package necesse.entity;

import necesse.engine.util.GameLinkedList;
import necesse.entity.particle.Particle;

public class ParticleTypeSwitcher {
   protected GameLinkedList<CountType> types;
   protected GameLinkedList<CountType>.Element currentType;
   protected long currentCount = 0L;

   public ParticleTypeSwitcher(int var1, Particle.GType var2, Object... var3) {
      this.types = new GameLinkedList();
      this.currentType = this.types.addLast(new CountType(var1, var2));
      int var4 = 1;
      Object[] var5 = var3;
      int var6 = var3.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         Object var8 = var5[var7];
         if (var8 instanceof Integer) {
            var4 = (Integer)var8;
         } else if (var8 instanceof Particle.GType) {
            this.types.addLast(new CountType(var4, (Particle.GType)var8));
            var4 = 1;
         }
      }

   }

   public ParticleTypeSwitcher(Particle.GType... var1) {
      if (var1.length == 0) {
         throw new IllegalArgumentException("Must have at least one type");
      } else {
         this.types = new GameLinkedList();
         Particle.GType var2 = null;
         int var3 = 0;
         Particle.GType[] var4 = var1;
         int var5 = var1.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            Particle.GType var7 = var4[var6];
            if (var2 != null && var7 != var2) {
               this.types.addLast(new CountType(var3, var2));
               var3 = 0;
            }

            var2 = var7;
            ++var3;
         }

         if (var3 > 0) {
            this.types.addLast(new CountType(var3, var2));
         }

         this.currentType = this.types.getFirstElement();
      }
   }

   public Particle.GType next() {
      Particle.GType var1 = ((CountType)this.currentType.object).type;
      ++this.currentCount;
      if (this.currentCount >= (long)((CountType)this.currentType.object).count) {
         this.currentType = this.currentType.nextWrap();
         this.currentCount = 0L;
      }

      return var1;
   }

   protected static class CountType {
      public final int count;
      public final Particle.GType type;

      public CountType(int var1, Particle.GType var2) {
         this.count = var1;
         this.type = var2;
      }
   }
}
