package necesse.level.maps.levelData.jobs;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.function.Supplier;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.LocalMessage;
import necesse.engine.localization.message.StaticMessage;
import necesse.engine.save.LoadData;
import necesse.engine.util.GameObjectReservable;
import necesse.entity.mobs.job.EntityJobWorker;
import necesse.entity.mobs.job.FoundJob;
import necesse.entity.mobs.job.JobSequence;
import necesse.entity.mobs.job.JobTypeHandler;
import necesse.entity.mobs.job.LinkedListJobSequence;
import necesse.entity.mobs.job.activeJob.ActiveJob;
import necesse.entity.mobs.job.activeJob.ActiveJobResult;
import necesse.entity.mobs.job.activeJob.CraftSettlementRecipeActiveJob;
import necesse.entity.mobs.job.activeJob.DropOffSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.PickupSettlementStorageActiveJob;
import necesse.entity.mobs.job.activeJob.SimplePerformActiveJob;
import necesse.inventory.InventoryItem;
import necesse.inventory.recipe.Ingredient;
import necesse.level.gameObject.GameObject;
import necesse.level.maps.levelData.settlementData.SettlementInventory;
import necesse.level.maps.levelData.settlementData.SettlementStoragePickupSlot;
import necesse.level.maps.levelData.settlementData.SettlementWorkstation;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationLevelObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationObject;
import necesse.level.maps.levelData.settlementData.SettlementWorkstationRecipe;
import necesse.level.maps.levelData.settlementData.StorageDropOff;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageGlobalIngredientIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageItemIDIndex;
import necesse.level.maps.levelData.settlementData.storage.SettlementStorageRecords;

public class UseWorkstationLevelJob extends LevelJob {
   public SettlementWorkstation workstation;
   private Supplier<Boolean> validCheck;

   public UseWorkstationLevelJob(SettlementWorkstation var1, Supplier<Boolean> var2) {
      super(var1.tileX, var1.tileY);
      this.workstation = var1;
      this.validCheck = var2;
   }

   public UseWorkstationLevelJob(LoadData var1) {
      super(var1);
   }

   public boolean shouldSave() {
      return false;
   }

   public boolean isValid() {
      GameObject var1 = this.getLevel().getObject(this.tileX, this.tileY);
      return var1 instanceof SettlementWorkstationObject && (Boolean)this.validCheck.get();
   }

   public static <T extends UseWorkstationLevelJob> JobSequence getJobSequence(EntityJobWorker var0, final FoundJob<T> var1) {
      SettlementWorkstationLevelObject var2 = ((UseWorkstationLevelJob)var1.job).workstation.getWorkstationObject();
      if (var2 != null) {
         Iterator var3 = ((UseWorkstationLevelJob)var1.job).workstation.recipes.iterator();

         while(true) {
            final SettlementWorkstationRecipe var4;
            int var6;
            int var7;
            boolean var8;
            SettlementInventory var9;
            do {
               int var5;
               do {
                  do {
                     if (!var3.hasNext()) {
                        return null;
                     }

                     var4 = (SettlementWorkstationRecipe)var3.next();
                  } while(!var2.canCurrentlyCraft(var4.recipe));

                  var5 = 0;
                  switch (var4.mode) {
                     case DO_COUNT:
                        var5 = var4.modeCount;
                        break;
                     case DO_UNTIL:
                        if (var4.modeCount != 0) {
                           var6 = HasStorageLevelJob.getItemCount(var0, (var2x) -> {
                              return var2x.equals(var2.level, var4.recipe.resultItem, true, true, "equals");
                           }, var4.modeCount, true, true);
                           var5 = var4.modeCount - var6;
                        }
                        break;
                     case DO_FOREVER:
                        var5 = Integer.MAX_VALUE;
                  }
               } while(var5 <= 0);

               var6 = Math.min(var2.getMaxCraftsAtOnce(var4.recipe), var5);
               var7 = 0;
               var8 = ((UseWorkstationLevelJob)var1.job).workstation.isProcessingWorkstation();
               var9 = null;
               if (!var8) {
                  break;
               }

               var9 = ((UseWorkstationLevelJob)var1.job).workstation.getProcessingInputInventory();
            } while(var9 == null);

            ArrayList var10 = new ArrayList();
            ArrayList var11 = new ArrayList();
            ArrayList var12 = new ArrayList(var0.getWorkInventory().getTotalItemStacks());
            Iterator var13 = var0.getWorkInventory().items().iterator();

            while(var13.hasNext()) {
               InventoryItem var14 = (InventoryItem)var13.next();
               var12.add(var14.copy());
            }

            int var17;
            for(int var28 = 0; var28 < var6; ++var28) {
               boolean var30 = true;
               Ingredient[] var15 = var4.recipe.ingredients;
               int var16 = var15.length;

               label213:
               for(var17 = 0; var17 < var16; ++var17) {
                  Ingredient var18 = var15[var17];
                  ArrayList var19 = new ArrayList();
                  int var20 = 0;
                  ListIterator var21 = var12.listIterator();

                  while(var21.hasNext()) {
                     InventoryItem var22 = (InventoryItem)var21.next();
                     if (var4.canUseItem(var18, var22)) {
                        int var23 = var18.getIngredientAmount() - var20;
                        InventoryItem var24 = var22.copy(Math.min(var23, var22.getAmount()));
                        var19.add(var24);
                        var20 += var24.getAmount();
                        var22.setAmount(var22.getAmount() - var24.getAmount());
                        if (var22.getAmount() <= 0) {
                           var21.remove();
                        }

                        if (var20 >= var18.getIngredientAmount()) {
                           break;
                        }
                     }
                  }

                  int var43;
                  if (var20 >= var18.getIngredientAmount()) {
                     if (var8) {
                        int var38 = 0;
                        Iterator var40 = var19.iterator();

                        while(var40.hasNext()) {
                           InventoryItem var42 = (InventoryItem)var40.next();
                           var43 = var9.canAddFutureDropOff(var42);
                           if (var43 <= 0) {
                              var30 = false;
                              break;
                           }

                           var38 += var43;
                           var11.add(var9.addFutureDropOff(() -> {
                              return var42;
                           }));
                        }

                        if (var38 < var18.getIngredientAmount()) {
                           var30 = false;
                           break;
                        }
                     }
                  } else {
                     SettlementStorageRecords var37 = PickupSettlementStorageActiveJob.getStorageRecords(var0);
                     if (var37 == null) {
                        var30 = false;
                        break;
                     }

                     int var39 = var18.getIngredientAmount() - var20;
                     LinkedList var41;
                     if (var18.isGlobalIngredient()) {
                        var41 = ((SettlementStorageGlobalIngredientIDIndex)var37.getIndex(SettlementStorageGlobalIngredientIDIndex.class)).findPickupSlots(var18.getIngredientID(), var0, (var1x) -> {
                           return var4.ingredientFilter.isItemAllowed(var1x.item);
                        }, var39, var39);
                     } else {
                        var41 = ((SettlementStorageItemIDIndex)var37.getIndex(SettlementStorageItemIDIndex.class)).findPickupSlots(var18.getIngredientID(), var0, (Predicate)null, var39, var39);
                     }

                     if (var41 == null) {
                        var30 = false;
                        break;
                     }

                     if (var8) {
                        var43 = 0;
                        Iterator var25 = var19.iterator();

                        int var27;
                        while(var25.hasNext()) {
                           InventoryItem var26 = (InventoryItem)var25.next();
                           var27 = var9.canAddFutureDropOff(var26);
                           if (var27 <= 0) {
                              var30 = false;
                              break;
                           }

                           var43 += var27;
                           var11.add(var9.addFutureDropOff(() -> {
                              return var26;
                           }));
                        }

                        var25 = var41.iterator();

                        while(true) {
                           if (var25.hasNext()) {
                              SettlementStoragePickupSlot var44 = (SettlementStoragePickupSlot)var25.next();
                              var27 = var9.canAddFutureDropOff(var44.item);
                              if (var27 > 0) {
                                 var43 += var27;
                                 var11.add(var9.addFutureDropOff(() -> {
                                    return var44.item;
                                 }));
                                 if (var43 < var18.getIngredientAmount()) {
                                    continue;
                                 }
                              } else {
                                 var30 = false;
                              }
                           }

                           if (!var30 || var43 < var18.getIngredientAmount()) {
                              var30 = false;
                              break label213;
                           }
                           break;
                        }
                     }

                     var10.addAll(var41);
                  }
               }

               if (!var30) {
                  break;
               }

               ++var7;
            }

            if (var7 > 0) {
               GameMessage var29 = var2.object.getLocalization();
               Object var33 = var4.name != null && !var4.name.isEmpty() ? new StaticMessage(var4.name) : var4.recipe.resultItem.getItemLocalization();
               LocalMessage var31 = new LocalMessage("activities", "crafting", new Object[]{"item", var33, "target", var29});
               LinkedListJobSequence var32 = new LinkedListJobSequence(var31);
               Iterator var34 = var10.iterator();

               while(var34.hasNext()) {
                  SettlementStoragePickupSlot var35 = (SettlementStoragePickupSlot)var34.next();
                  var32.add(var35.toPickupJob(var0, var1.priority));
               }

               if (var8) {
                  var34 = var11.iterator();

                  while(var34.hasNext()) {
                     StorageDropOff var36 = (StorageDropOff)var34.next();
                     JobTypeHandler.TypePriority var10004 = var1.priority;
                     GameObjectReservable var10005 = ((UseWorkstationLevelJob)var1.job).reservable;
                     UseWorkstationLevelJob var10006 = (UseWorkstationLevelJob)var1.job;
                     Objects.requireNonNull(var10006);
                     var32.add(new DropOffSettlementStorageActiveJob(var0, var10004, var10005, var10006::isRemoved, true, var36));
                  }

                  var32.add(new SimplePerformActiveJob(var0, (JobTypeHandler.TypePriority)null) {
                     public ActiveJobResult perform() {
                        var4.onCrafted(((UseWorkstationLevelJob)var1.job).workstation, 1);
                        return ActiveJobResult.FINISHED;
                     }
                  });
               } else {
                  for(var17 = 0; var17 < var7; ++var17) {
                     var32.add(((UseWorkstationLevelJob)var1.job).getCraftActiveJob(var0, var1.priority, var4, var32));
                  }
               }

               return var32;
            }

            var10.forEach(SettlementStoragePickupSlot::remove);
            var11.forEach(StorageDropOff::remove);
         }
      } else {
         return null;
      }
   }

   public ActiveJob getCraftActiveJob(EntityJobWorker var1, JobTypeHandler.TypePriority var2, SettlementWorkstationRecipe var3, LinkedListJobSequence var4) {
      float var5 = (float)Math.pow((double)var3.recipe.ingredients.length, 0.699999988079071) + 1.0F;
      return new CraftSettlementRecipeActiveJob(var1, var2, this.tileX, this.tileY, this.workstation, var3, var5, this.reservable, this::isRemoved, var4);
   }
}
