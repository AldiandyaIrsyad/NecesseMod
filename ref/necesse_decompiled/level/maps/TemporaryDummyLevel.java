package necesse.level.maps;

import necesse.engine.registries.BiomeRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.TileRegistry;
import necesse.engine.util.LevelIdentifier;
import necesse.engine.world.WorldEntity;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.SignObjectEntity;

public class TemporaryDummyLevel extends Level {
   public TemporaryDummyLevel(LevelIdentifier var1, int var2, int var3, WorldEntity var4) {
      super(var1, var2, var3, var4);
   }

   public TemporaryDummyLevel(LevelIdentifier var1, WorldEntity var2) {
      super(var1, 20, 20, var2);
      this.biome = BiomeRegistry.UNKNOWN;
      int var3 = TileRegistry.grassID;

      for(int var4 = 0; var4 < this.width; ++var4) {
         for(int var5 = 0; var5 < this.height; ++var5) {
            this.setTile(var4, var5, var3);
         }
      }

      this.setObject(this.width / 2, this.height / 2, ObjectRegistry.getObjectID("sign"), 2);
      ObjectEntity var6 = this.entityManager.getObjectEntity(this.width / 2, this.height / 2);
      if (var6 instanceof SignObjectEntity) {
         ((SignObjectEntity)var6).setText("This is a temporary level generated because this coordinate does not have a world generator that handles it.\n\nAnything that you put in here will be deleted when it is unloaded.");
      }

   }

   public boolean shouldSave() {
      return false;
   }
}
