package necesse.inventory.item;

import java.io.PrintStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Objects;
import java.util.stream.Stream;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.util.CategoryString;
import necesse.engine.util.GameUtils;

public class ItemCategory implements Comparable<ItemCategory> {
   public static final ItemCategory masterCategory = new ItemCategory();
   public static final ItemCategoryManager masterManager;
   public static final ItemCategory foodQualityMasterCategory;
   public static final ItemCategoryManager foodQualityManager;
   public static final ItemCategory equipmentMasterCategory;
   public static final ItemCategoryManager equipmentManager;
   protected ItemCategoryManager manager;
   public final int id;
   public final int depth;
   public final String stringID;
   public final ItemCategory parent;
   public GameMessage displayName;
   protected String[] sortCategories;
   protected HashMap<String, ItemCategory> children;
   protected HashMap<Integer, Item> itemIDs;
   protected HashMap<Integer, Item> thisOrChildrenItemIDs;

   public static ItemCategory createCategory(String var0, String... var1) {
      return masterManager.createCategory(var0, var1);
   }

   public static ItemCategory createCategory(String var0, GameMessage var1, String... var2) {
      return masterManager.createCategory(var0, var1, var2);
   }

   public static ItemCategory getCategory(String... var0) {
      return masterManager.getCategory(var0);
   }

   public static ItemCategory getCategory(int var0) {
      return masterManager.getCategory(var0);
   }

   public ItemCategory setItemCategory(Item var1, ItemCategory var2) {
      return masterManager.setItemCategory(var1, var2);
   }

   public static ItemCategory setItemCategory(Item var0, String... var1) {
      return masterManager.setItemCategory(var0, var1);
   }

   public static ItemCategory getItemsCategory(Item var0) {
      return masterManager.getItemsCategory(var0);
   }

   protected ItemCategory(int var1, String var2, ItemCategory var3, String var4, GameMessage var5) {
      this.children = new HashMap();
      this.itemIDs = new HashMap();
      this.thisOrChildrenItemIDs = new HashMap();
      this.id = var1;
      this.stringID = var2;
      this.parent = var3;
      this.depth = var3 == null ? 0 : var3.depth + 1;
      this.displayName = var5;
      this.setSortCategories(var4);
   }

   public ItemCategory() {
      this(0, "master", (ItemCategory)null, "Z-Z-Z", new StaticMessage("MASTER"));
   }

   public void setSortCategories(String var1) {
      this.sortCategories = CategoryString.getCategories(var1);
   }

   public Iterable<String> getChildrenStringIDs() {
      return this.children.keySet();
   }

   public Iterable<ItemCategory> getChildren() {
      return this.children.values();
   }

   public Stream<ItemCategory> streamChildren() {
      return this.children.values().stream();
   }

   public ItemCategory getChild(String var1) {
      return (ItemCategory)this.children.get(var1);
   }

   public Collection<Item> getItems() {
      return this.itemIDs.values();
   }

   public Stream<Item> streamItems() {
      return this.itemIDs.values().stream();
   }

   public boolean containsItem(Item var1) {
      return this.itemIDs.containsKey(var1.getID());
   }

   public boolean containsItemOrInChildren(Item var1) {
      return this.thisOrChildrenItemIDs.containsKey(var1.getID());
   }

   public String[] getStringIDTree(boolean var1) {
      LinkedList var2 = new LinkedList();
      var2.addFirst(this.stringID);

      for(ItemCategory var3 = this.parent; var3 != null && (var3.parent != null || var1); var3 = var3.parent) {
         var2.addFirst(var3.stringID);
      }

      return (String[])var2.toArray(new String[0]);
   }

   public void printTree(PrintStream var1, String var2) {
      var1.println(var2 + this.displayName.translate() + " (" + GameUtils.join(this.sortCategories, "-") + ")");
      this.children.values().stream().sorted().forEach((var2x) -> {
         var2x.printTree(var1, var2 + "\t");
      });
      this.itemIDs.values().stream().filter((var0) -> {
         return ItemRegistry.isObtainable(var0.getID());
      }).map((var0) -> {
         return ItemRegistry.getDisplayName(var0.getID());
      }).filter(Objects::nonNull).sorted().forEach((var2x) -> {
         var1.println(var2 + "\t" + var2x);
      });
   }

   public boolean isOrHasParent(String var1) {
      if (this.stringID.equals(var1)) {
         return true;
      } else {
         return this.parent != null ? this.parent.isOrHasParent(var1) : false;
      }
   }

   private static int compare(ItemCategory var0, ItemCategory var1) {
      return (new CategoryString(var0.sortCategories, var0.displayName.translate())).compareTo(new CategoryString(var1.sortCategories, var1.displayName.translate()));
   }

   public int compareTo(ItemCategory var1) {
      if (var1 == null) {
         return -1;
      } else if (var1 == this) {
         return 0;
      } else if (this.manager != null && this.manager == var1.manager) {
         if (this.parent == var1.parent) {
            return compare(this, var1);
         } else {
            ItemCategory var2;
            for(var2 = this; var1.parent != null && var1.parent.depth > this.depth; var1 = var1.parent) {
            }

            if (var1.parent == null) {
               return 1;
            } else {
               while(var2.parent != null && var2.parent.depth > var1.depth) {
                  var2 = var2.parent;
               }

               return var2.parent == null ? -1 : compare(var2, var1);
            }
         }
      } else {
         return compare(this, var1);
      }
   }

   // $FF: synthetic method
   // $FF: bridge method
   public int compareTo(Object var1) {
      return this.compareTo((ItemCategory)var1);
   }

   static {
      masterManager = new ItemCategoryManager(masterCategory);
      foodQualityMasterCategory = new ItemCategory();
      foodQualityManager = new ItemCategoryManager(foodQualityMasterCategory);
      equipmentMasterCategory = new ItemCategory();
      equipmentManager = new ItemCategoryManager(equipmentMasterCategory);
      createCategory("A-A-A", "equipment");
      createCategory("A-A-A", "equipment", "tools");
      createCategory("A-A-A", "equipment", "tools", "pickaxes");
      createCategory("A-A-B", "equipment", "tools", "axes");
      createCategory("A-A-C", "equipment", "tools", "shovels");
      createCategory("A-A-D", "equipment", "tools", "fishingrods");
      createCategory("A-B-A", "equipment", "weapons");
      createCategory("A-B-A", "equipment", "weapons", "meleeweapons");
      createCategory("A-B-B", "equipment", "weapons", "rangedweapons");
      createCategory("A-B-C", "equipment", "weapons", "magicweapons");
      createCategory("A-B-D", "equipment", "weapons", "summonweapons");
      createCategory("A-B-E", "equipment", "weapons", "throwweapons");
      createCategory("A-C-A", "equipment", "ammo");
      createCategory("A-C-B", "equipment", "bait");
      createCategory("A-D-A", "equipment", "armor");
      createCategory("A-E-A", "equipment", "trinkets");
      createCategory("A-F-A", "equipment", "cosmetics");
      createCategory("A-G-A", "equipment", "banners");
      createCategory("B-A-A", "consumable");
      createCategory("B-A-A", "consumable", "rawfood");
      createCategory("B-B-A", "consumable", "commonfish");
      createCategory("B-C-A", "consumable", "food");
      createCategory("B-D-A", "consumable", "potions");
      createCategory("B-E-A", "consumable", "bossitems");
      createCategory("C-A-A", "materials");
      createCategory("C-A-A", "materials", "ore");
      createCategory("C-B-A", "materials", "minerals");
      createCategory("C-C-A", "materials", "bars");
      createCategory("C-D-A", "materials", "stone");
      createCategory("C-E-A", "materials", "logs");
      createCategory("C-F-A", "materials", "specialfish");
      createCategory("C-G-A", "materials", "flowers");
      createCategory("C-H-A", "materials", "mobdrops");
      createCategory("C-I-A", "materials", "essences");
      createCategory("D-A-A", "tiles");
      createCategory("D-A-A", "tiles", "floors");
      createCategory("D-B-A", "tiles", "liquids");
      createCategory("D-C-A", "tiles", "terrain");
      createCategory("E-A-A", "objects");
      createCategory("E-A-A", "objects", "seeds");
      createCategory("E-B-A", "objects", "saplings");
      createCategory("E-C-A", "objects", "craftingstations");
      createCategory("E-D-A", "objects", "furniture");
      createCategory("E-E-A", "objects", "wallsanddoors");
      createCategory("E-F-A", "objects", "fencesandgates");
      createCategory("E-G-A", "objects", "traps");
      createCategory("F-A-A", "wiring");
      createCategory("F-A-A", "wiring", "logicgates");
      createCategory("Z-Z-Z", "misc");
      createCategory("A-A-A", "misc", "mounts");
      createCategory("A-B-A", "misc", "pets");
      createCategory("A-C-A", "misc", "pouches");
      createCategory("A-D-A", "misc", "vinyls");
      createCategory("A-E-A", "misc", "questitems");
      equipmentManager.createCategory("A-A-A", "weapons");
      equipmentManager.createCategory("A-B-A", "weapons", "meleeweapons");
      equipmentManager.createCategory("A-C-A", "weapons", "rangedweapons");
      equipmentManager.createCategory("A-D-A", "weapons", "magicweapons");
      equipmentManager.createCategory("A-E-A", "weapons", "summonweapons");
      equipmentManager.createCategory("A-F-A", "weapons", "throwweapons");
      equipmentManager.createCategory("B-A-A", "armor");
   }
}
