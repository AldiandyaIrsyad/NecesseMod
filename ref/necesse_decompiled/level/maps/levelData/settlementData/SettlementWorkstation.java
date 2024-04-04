package necesse.level.maps.levelData.settlementData;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Iterator;
import necesse.engine.GameLog;
import necesse.engine.network.PacketReader;
import necesse.engine.save.LoadData;
import necesse.engine.save.LoadDataException;
import necesse.engine.save.SaveData;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.settlement.events.SettlementWorkstationEvent;
import necesse.inventory.container.settlement.events.SettlementWorkstationRecipeUpdateEvent;
import necesse.inventory.itemFilter.ItemCategoriesFilter;
import necesse.level.gameObject.GameObject;

public class SettlementWorkstation {
   public static int maxRecipes = 10;
   public final SettlementLevelData data;
   public final int tileX;
   public final int tileY;
   public ArrayList<SettlementWorkstationRecipe> recipes = new ArrayList();
   public SettlementRequestInventory fuelInventory;
   public SettlementInventory processingInputInventory;
   public SettlementInventory processingOutputInventory;

   public SettlementWorkstation(SettlementLevelData var1, int var2, int var3) {
      this.data = var1;
      this.tileX = var2;
      this.tileY = var3;
   }

   public void addSaveData(SaveData var1) {
      var1.addPoint("tile", new Point(this.tileX, this.tileY));
      SaveData var2 = new SaveData("RECIPES");
      Iterator var3 = this.recipes.iterator();

      while(var3.hasNext()) {
         SettlementWorkstationRecipe var4 = (SettlementWorkstationRecipe)var3.next();
         SaveData var5 = new SaveData("RECIPE");
         var4.addSaveData(var5, true);
         var2.addSaveData(var5);
      }

      var1.addSaveData(var2);
   }

   public SettlementWorkstation(SettlementLevelData var1, LoadData var2) throws LoadDataException {
      this.data = var1;
      Point var3 = var2.getPoint("tile", (Point)null);
      if (var3 == null) {
         throw new LoadDataException("Missing position");
      } else {
         this.tileX = var3.x;
         this.tileY = var3.y;
         this.recipes = new ArrayList();
         LoadData var4 = var2.getFirstLoadDataByName("RECIPES");
         if (var4 != null) {
            var4.getLoadDataByName("RECIPE").stream().filter((var0) -> {
               return var0.isArray();
            }).forEach((var2x) -> {
               try {
                  SettlementWorkstationRecipe var3 = new SettlementWorkstationRecipe(var2x, true);
                  this.recipes.add(var3);
               } catch (LoadDataException var4) {
                  System.err.println("Could not load settlement work station recipe at level " + var1.getLevel().getIdentifier() + ": " + var4.getMessage());
               } catch (Exception var5) {
                  System.err.println("Unknown error loading settlement work station recipe at level " + var1.getLevel().getIdentifier());
                  var5.printStackTrace();
               }

            });
         }

      }
   }

   public void updateRecipe(int var1, int var2, PacketReader var3) {
      if (var1 > this.recipes.size()) {
         GameLog.warn.println("Received invalid settlement recipe update index");
         (new SettlementWorkstationEvent(this)).applyAndSendToClientsAt(this.data.getLevel());
      } else {
         int var4 = -1;

         for(int var5 = 0; var5 < this.recipes.size(); ++var5) {
            if (((SettlementWorkstationRecipe)this.recipes.get(var5)).uniqueID == var2) {
               var4 = var5;
               break;
            }
         }

         SettlementWorkstationRecipe var6;
         if (var4 != -1) {
            if (var1 == var4) {
               ((SettlementWorkstationRecipe)this.recipes.get(var1)).applyPacket(var3);
            } else {
               var6 = (SettlementWorkstationRecipe)this.recipes.remove(var4);
               var6.applyPacket(var3);
               this.recipes.add(var1, var6);
            }

            (new SettlementWorkstationRecipeUpdateEvent(this.tileX, this.tileY, var1, (SettlementWorkstationRecipe)this.recipes.get(var1))).applyAndSendToClientsAt(this.data.getLevel());
         } else if (this.recipes.size() >= maxRecipes) {
            (new SettlementWorkstationEvent(this)).applyAndSendToClientsAt(this.data.getLevel());
         } else {
            var6 = new SettlementWorkstationRecipe(var2, var3);
            this.recipes.add(var1, var6);
            (new SettlementWorkstationRecipeUpdateEvent(this.tileX, this.tileY, var1, var6)).applyAndSendToClientsAt(this.data.getLevel());
         }

      }
   }

   public boolean isValid() {
      return this.getWorkstationObject() != null;
   }

   public SettlementRequestInventory getFuelInventory() {
      SettlementWorkstationLevelObject var1 = this.getWorkstationObject();
      if (var1 != null) {
         SettlementRequestOptions var2 = var1.getFuelRequestOptions();
         if (var2 != null) {
            if (this.fuelInventory == null) {
               this.fuelInventory = new SettlementRequestInventory(this.data.getLevel(), this.tileX, this.tileY, var2) {
                  public InventoryRange getInventoryRange() {
                     SettlementWorkstationLevelObject var1 = SettlementWorkstation.this.getWorkstationObject();
                     return var1 != null ? var1.getFuelInventoryRange() : null;
                  }
               };
            }

            this.fuelInventory.filter = new ItemCategoriesFilter(var2.minAmount, var2.maxAmount, true);
            if (!this.fuelInventory.isValid()) {
               this.fuelInventory = null;
            }

            return this.fuelInventory;
         }
      }

      this.fuelInventory = null;
      return null;
   }

   public SettlementInventory getProcessingInputInventory() {
      if (this.isProcessingWorkstation()) {
         if (this.processingInputInventory == null) {
            this.processingInputInventory = new SettlementInventory(this.data.getLevel(), this.tileX, this.tileY) {
               public InventoryRange getInventoryRange() {
                  SettlementWorkstationLevelObject var1 = SettlementWorkstation.this.getWorkstationObject();
                  return var1 != null ? var1.getProcessingInputRange() : null;
               }
            };
         }

         if (!this.processingInputInventory.isValid()) {
            this.processingInputInventory = null;
         }

         return this.processingInputInventory;
      } else {
         this.processingInputInventory = null;
         return null;
      }
   }

   public SettlementInventory getProcessingOutputInventory() {
      if (this.isProcessingWorkstation()) {
         if (this.processingOutputInventory == null) {
            this.processingOutputInventory = new SettlementInventory(this.data.getLevel(), this.tileX, this.tileY) {
               public InventoryRange getInventoryRange() {
                  SettlementWorkstationLevelObject var1 = SettlementWorkstation.this.getWorkstationObject();
                  return var1 != null ? var1.getProcessingOutputRange() : null;
               }
            };
         }

         this.processingOutputInventory.filter = new ItemCategoriesFilter(0, 0, false);
         if (!this.processingOutputInventory.isValid()) {
            this.processingOutputInventory = null;
         }

         return this.processingOutputInventory;
      } else {
         this.processingOutputInventory = null;
         return null;
      }
   }

   public boolean isProcessingWorkstation() {
      SettlementWorkstationLevelObject var1 = this.getWorkstationObject();
      return var1 != null ? var1.isProcessingInventory() : false;
   }

   public SettlementWorkstationLevelObject getWorkstationObject() {
      GameObject var1 = this.data.getLevel().getObject(this.tileX, this.tileY);
      return var1 instanceof SettlementWorkstationObject ? new SettlementWorkstationLevelObject(this.data.getLevel(), this.tileX, this.tileY) : null;
   }
}
