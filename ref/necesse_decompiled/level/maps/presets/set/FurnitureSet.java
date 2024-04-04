package necesse.level.maps.presets.set;

import necesse.engine.registries.ObjectRegistry;
import necesse.level.maps.presets.Preset;

public class FurnitureSet implements PresetSet<FurnitureSet> {
   public static FurnitureSet oak = loadByStringID("oak");
   public static FurnitureSet spruce = loadByStringID("spruce");
   public static FurnitureSet pine = loadByStringID("pine");
   public static FurnitureSet palm = loadByStringID("palm");
   public static FurnitureSet dungeon = loadByStringID("dungeon");
   public static FurnitureSet deadwood = loadByStringID("deadwood");
   public int chest = -1;
   public int dinnerTable = -1;
   public int dinnerTable2 = -1;
   public int desk = -1;
   public int modularTable = -1;
   public int chair = -1;
   public int bench = -1;
   public int bench2 = -1;
   public int bookshelf = -1;
   public int cabinet = -1;
   public int bed = -1;
   public int bed2 = -1;
   public int dresser = -1;
   public int clock = -1;
   public int candelabra = -1;
   public int display = -1;
   public int bathtub = -1;
   public int bathtub2 = -1;
   public int toilet = -1;

   public FurnitureSet() {
   }

   public static FurnitureSet loadByStringID(String var0) {
      FurnitureSet var1 = new FurnitureSet();
      var1.chest = ObjectRegistry.getObjectID(var0 + "chest");
      var1.dinnerTable = ObjectRegistry.getObjectID(var0 + "dinnertable");
      var1.dinnerTable2 = ObjectRegistry.getObjectID(var0 + "dinnertable2");
      var1.desk = ObjectRegistry.getObjectID(var0 + "desk");
      var1.modularTable = ObjectRegistry.getObjectID(var0 + "modulartable");
      var1.chair = ObjectRegistry.getObjectID(var0 + "chair");
      var1.bench = ObjectRegistry.getObjectID(var0 + "bench");
      var1.bench2 = ObjectRegistry.getObjectID(var0 + "bench2");
      var1.bookshelf = ObjectRegistry.getObjectID(var0 + "bookshelf");
      var1.cabinet = ObjectRegistry.getObjectID(var0 + "cabinet");
      var1.bed = ObjectRegistry.getObjectID(var0 + "bed");
      var1.bed2 = ObjectRegistry.getObjectID(var0 + "bed2");
      var1.dresser = ObjectRegistry.getObjectID(var0 + "dresser");
      var1.clock = ObjectRegistry.getObjectID(var0 + "clock");
      var1.candelabra = ObjectRegistry.getObjectID(var0 + "candelabra");
      var1.display = ObjectRegistry.getObjectID(var0 + "display");
      var1.bathtub = ObjectRegistry.getObjectID(var0 + "bathtub");
      var1.bathtub2 = ObjectRegistry.getObjectID(var0 + "bathtub2");
      var1.toilet = ObjectRegistry.getObjectID(var0 + "toilet");
      return var1;
   }

   public <C extends Preset> C replacePreset(FurnitureSet var1, C var2) {
      if (var1 == this) {
         return var2;
      } else {
         var2.replaceObject(var1.chest, this.chest);
         var2.replaceObject(var1.dinnerTable, this.dinnerTable);
         var2.replaceObject(var1.dinnerTable2, this.dinnerTable2);
         var2.replaceObject(var1.desk, this.desk);
         var2.replaceObject(var1.modularTable, this.modularTable);
         var2.replaceObject(var1.chair, this.chair);
         var2.replaceObject(var1.bench, this.bench);
         var2.replaceObject(var1.bench2, this.bench2);
         var2.replaceObject(var1.bookshelf, this.bookshelf);
         var2.replaceObject(var1.cabinet, this.cabinet);
         var2.replaceObject(var1.bed, this.bed);
         var2.replaceObject(var1.bed2, this.bed2);
         var2.replaceObject(var1.dresser, this.dresser);
         var2.replaceObject(var1.clock, this.clock);
         var2.replaceObject(var1.candelabra, this.candelabra);
         var2.replaceObject(var1.display, this.display);
         var2.replaceObject(var1.bathtub, this.bathtub);
         var2.replaceObject(var1.bathtub2, this.bathtub2);
         var2.replaceObject(var1.toilet, this.toilet);
         return var2;
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Preset replacePreset(Object var1, Preset var2) {
      return this.replacePreset((FurnitureSet)var1, var2);
   }
}
