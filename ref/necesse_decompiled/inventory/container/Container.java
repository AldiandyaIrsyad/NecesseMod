package necesse.inventory.container;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Objects;
import java.util.function.BooleanSupplier;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;
import necesse.engine.GameLog;
import necesse.engine.GameTileRange;
import necesse.engine.GlobalData;
import necesse.engine.localization.Localization;
import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.engine.network.client.ClientClient;
import necesse.engine.network.packet.PacketContainerAction;
import necesse.engine.network.server.ServerClient;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.HashMapGameLinkedList;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.gfx.forms.ContainerComponent;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryAddConsumer;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryItemsRemoved;
import necesse.inventory.InventoryRange;
import necesse.inventory.ItemCombineResult;
import necesse.inventory.PlayerInventory;
import necesse.inventory.PlayerInventoryManager;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.container.customAction.ContainerCustomAction;
import necesse.inventory.container.customAction.ContainerCustomActionRegistry;
import necesse.inventory.container.events.ContainerEvent;
import necesse.inventory.container.events.ContainerEventHandler;
import necesse.inventory.container.events.ContainerEventSubscription;
import necesse.inventory.container.slots.ArmorContainerSlot;
import necesse.inventory.container.slots.ContainerSlot;
import necesse.inventory.container.slots.MountContainerSlot;
import necesse.inventory.container.slots.TrashContainerSlot;
import necesse.inventory.container.slots.TrinketAbilityContainerSlot;
import necesse.inventory.container.slots.TrinketContainerSlot;
import necesse.inventory.item.armorItem.ArmorItem;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.ContainerRecipeCraftedEvent;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

public class Container {
   public static final int DROP_ITEM_SLOT = -1;
   public static final int SORT_INVENTORY_SLOT = -2;
   public static final int QUICK_STACK_SLOT = -3;
   public static final int RESTOCK_SLOT = -4;
   protected boolean shouldClose;
   protected boolean isClosed;
   private ArrayList<ContainerSlot> slots = new ArrayList();
   private HashSet<Integer> lockedSlots = new HashSet();
   private ArrayList<ContainerRecipe> recipes;
   private ContainerCustomActionRegistry actionRegistry;
   protected LinkedHashSet<Inventory> craftInventories;
   protected LinkedList<QuickTransferOption> quickTransferOptions;
   protected LinkedList<ContainerEventSubscription<?>> eventSubscriptions;
   protected HashMapGameLinkedList<Class<?>, ContainerEventHandler<?>> eventHandlers;
   public final NetworkClient client;
   public final int uniqueSeed;
   public int CLIENT_DRAGGING_SLOT = -1;
   public int CLIENT_HOTBAR_START = -1;
   public int CLIENT_HOTBAR_END = -1;
   public int CLIENT_INVENTORY_START = -1;
   public int CLIENT_INVENTORY_END = -1;
   public int CLIENT_HELMET_SLOT = -1;
   public int CLIENT_CHEST_SLOT = -1;
   public int CLIENT_FEET_SLOT = -1;
   public int CLIENT_COSM_HELMET_SLOT = -1;
   public int CLIENT_COSM_CHEST_SLOT = -1;
   public int CLIENT_COSM_FEET_SLOT = -1;
   public int CLIENT_MOUNT_SLOT = -1;
   public int CLIENT_TRINKET_ABILITY_SLOT = -1;
   public int CLIENT_TRINKET_START = -1;
   public int CLIENT_TRINKET_END = -1;
   public int CLIENT_TRASH_SLOT = -1;
   public ContainerComponent<?> form;

   public Container(NetworkClient var1, int var2) {
      this.client = var1;
      this.uniqueSeed = var2;
      this.recipes = new ArrayList();
      this.craftInventories = new LinkedHashSet();
      this.quickTransferOptions = new LinkedList();
      this.eventSubscriptions = new LinkedList();
      this.eventHandlers = new HashMapGameLinkedList();
      this.actionRegistry = new ContainerCustomActionRegistry(this);
      this.isClosed = false;
      this.CLIENT_DRAGGING_SLOT = this.addSlot(new ContainerSlot(var1.playerMob.getInv().drag, 0));
      PlayerInventory var3 = var1.playerMob.getInv().main;

      int var4;
      int var5;
      for(var4 = 0; var4 < 10; ++var4) {
         var5 = this.addSlot(new ContainerSlot(var3, var4));
         if (this.CLIENT_HOTBAR_START == -1) {
            this.CLIENT_HOTBAR_START = var5;
         }

         if (this.CLIENT_HOTBAR_END == -1) {
            this.CLIENT_HOTBAR_END = var5;
         }

         this.CLIENT_HOTBAR_START = Math.min(this.CLIENT_HOTBAR_START, var5);
         this.CLIENT_HOTBAR_END = Math.max(this.CLIENT_HOTBAR_END, var5);
      }

      for(var4 = 10; var4 < var3.getSize(); ++var4) {
         var5 = this.addSlot(new ContainerSlot(var3, var4));
         if (this.CLIENT_INVENTORY_START == -1) {
            this.CLIENT_INVENTORY_START = var5;
         }

         if (this.CLIENT_INVENTORY_END == -1) {
            this.CLIENT_INVENTORY_END = var5;
         }

         this.CLIENT_INVENTORY_START = Math.min(this.CLIENT_INVENTORY_START, var5);
         this.CLIENT_INVENTORY_END = Math.max(this.CLIENT_INVENTORY_END, var5);
      }

      PlayerInventory var10 = var1.playerMob.getInv().armor;
      this.CLIENT_HELMET_SLOT = this.addSlot(new ArmorContainerSlot(var10, 0, ArmorItem.ArmorType.HEAD));
      this.CLIENT_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(var10, 1, ArmorItem.ArmorType.CHEST));
      this.CLIENT_FEET_SLOT = this.addSlot(new ArmorContainerSlot(var10, 2, ArmorItem.ArmorType.FEET));
      PlayerInventory var11 = var1.playerMob.getInv().cosmetic;
      this.CLIENT_COSM_HELMET_SLOT = this.addSlot(new ArmorContainerSlot(var11, 0, ArmorItem.ArmorType.HEAD));
      this.CLIENT_COSM_CHEST_SLOT = this.addSlot(new ArmorContainerSlot(var11, 1, ArmorItem.ArmorType.CHEST));
      this.CLIENT_COSM_FEET_SLOT = this.addSlot(new ArmorContainerSlot(var11, 2, ArmorItem.ArmorType.FEET));
      PlayerInventory var6 = var1.playerMob.getInv().equipment;
      this.CLIENT_MOUNT_SLOT = this.addSlot(new MountContainerSlot(var6, 0));
      this.CLIENT_TRINKET_ABILITY_SLOT = this.addSlot(new TrinketAbilityContainerSlot(var6, 1));
      PlayerInventory var7 = var1.playerMob.getInv().trinkets;

      for(int var8 = 0; var8 < var7.getSize(); ++var8) {
         int var9 = this.addSlot(new TrinketContainerSlot(var7, var8));
         if (this.CLIENT_TRINKET_START == -1) {
            this.CLIENT_TRINKET_START = var9;
         }

         if (this.CLIENT_TRINKET_END == -1) {
            this.CLIENT_TRINKET_END = var9;
         }

         this.CLIENT_TRINKET_START = Math.min(this.CLIENT_TRINKET_START, var9);
         this.CLIENT_TRINKET_END = Math.max(this.CLIENT_TRINKET_END, var9);
      }

      this.CLIENT_TRASH_SLOT = this.addSlot(new TrashContainerSlot(var1.playerMob.getInv().trash, 0));
      Recipes.streamRecipes().filter((var0) -> {
         return var0.matchTech(RecipeTechRegistry.NONE);
      }).forEach((var1x) -> {
         this.addRecipe(var1x, true);
      });
      this.quickTransferOptions.addFirst(new QuickTransferOption((Predicate)null, true, new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END), new SlotIndexRange[]{new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END)}));
      this.quickTransferOptions.addFirst(new QuickTransferOption((Predicate)null, true, new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END), new SlotIndexRange[]{new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END)}));
   }

   public void init() {
      this.actionRegistry.closeRegistry();
   }

   public void tick() {
      if (this.client.isClient()) {
         boolean var1 = false;

         Inventory var3;
         for(Iterator var2 = this.craftInventories.iterator(); var2.hasNext(); var3.clean()) {
            var3 = (Inventory)var2.next();
            if (var3.isDirty()) {
               var1 = true;
            }
         }

         if (var1) {
            GlobalData.updateCraftable();
         }
      }

   }

   public void lootAllControlPressed() {
   }

   public void sortInventoryControlPressed() {
      if (this.client.isClient()) {
         ClientClient var1 = this.client.getClientClient();
         if (var1.isLocalClient()) {
            var1.getClient().network.sendPacket(new PacketContainerAction(-2, ContainerAction.LEFT_CLICK, 1));
         }
      }

   }

   public void quickStackControlPressed() {
      if (this.client.isClient()) {
         ClientClient var1 = this.client.getClientClient();
         if (var1.isLocalClient()) {
            var1.getClient().network.sendPacket(new PacketContainerAction(-3, ContainerAction.LEFT_CLICK, 1));
         }
      }

   }

   public ContainerActionResult applyContainerAction(int var1, ContainerAction var2) {
      if (this.isSlotLocked(var1)) {
         return new ContainerActionResult(255);
      } else if (var1 == -4) {
         this.restockPlayerInventory();
         return new ContainerActionResult(1);
      } else if (var1 == -3) {
         this.quickStackPlayerInventory();
         return new ContainerActionResult(1);
      } else if (var1 == -2) {
         PlayerInventory var6 = this.getClientInventory().main;
         var6.sortItems(10, var6.getSize() - 1);
         return new ContainerActionResult(1);
      } else {
         ContainerSlot var3;
         int var7;
         if (var1 == -1) {
            var3 = this.getClientDraggingSlot();
            if (!var3.isClear()) {
               if (var3.isItemLocked()) {
                  return new ContainerActionResult(Localization.translate("misc", "cannotdroplocked"));
               }

               switch (var2) {
                  case RIGHT_CLICK:
                     var7 = var3.getItemAmount();
                     if (this.client.isServer()) {
                        this.client.playerMob.dropDraggingItem(var7);
                     }

                     return new ContainerActionResult(var7);
                  case TAKE_ONE:
                     if (this.client.isServer()) {
                        this.client.playerMob.dropDraggingItem(1);
                     }

                     return new ContainerActionResult(1);
               }
            }

            return new ContainerActionResult((String)null);
         } else {
            var3 = this.getSlot(var1);
            if (var3 == null) {
               GameLog.warn.println("Tried to apply container action on invalid slot.");
               return new ContainerActionResult((String)null);
            } else {
               switch (var2) {
                  case RIGHT_CLICK:
                     return this.applyRightClick(var1, var3);
                  case TAKE_ONE:
                     return this.applyTakeOne(var1, var3);
                  case LEFT_CLICK:
                     return this.applyLeftClick(var1, var3);
                  case QUICK_MOVE:
                     return this.applyQuickMove(var1, var3);
                  case QUICK_TRASH:
                     return this.applyQuickTrash(var1, var3);
                  case QUICK_TRASH_ONE:
                     return this.applyQuickTrashOne(var1, var3);
                  case QUICK_DROP:
                     if (!var3.isClear() && !var3.isItemLocked()) {
                        var7 = var3.getItemAmount();
                        if (this.client.isServer()) {
                           this.client.playerMob.dropItem(var3.getItem());
                        }

                        var3.setItem((InventoryItem)null);
                        return new ContainerActionResult(var7);
                     }

                     return new ContainerActionResult((String)null);
                  case QUICK_DROP_ONE:
                     if (!var3.isClear() && !var3.isItemLocked()) {
                        var7 = 1;
                        if (this.client.isServer()) {
                           this.client.playerMob.dropItem(var3.getItem().copy(var7));
                        }

                        var3.setAmount(var3.getItemAmount() - var7);
                        if (var3.getItemAmount() <= 0) {
                           var3.setItem((InventoryItem)null);
                        }

                        return new ContainerActionResult(var7);
                     }

                     return new ContainerActionResult((String)null);
                  case TOGGLE_LOCKED:
                     if (!var3.isClear() && var3.canLockItem()) {
                        var3.setItemLocked(!var3.isItemLocked());
                        return new ContainerActionResult(var3.getItem().isLocked() ? 1 : 2);
                     }

                     return new ContainerActionResult((String)null);
                  case QUICK_MOVE_ONE:
                     return this.applyQuickMoveOne(var1, var3);
                  case QUICK_GET_ONE:
                     return this.applyQuickGetOne(var1, var3);
                  case RIGHT_CLICK_ACTION:
                     if (!var3.isClear()) {
                        InventoryItem var4 = var3.getItem();
                        Supplier var5 = var4.item.getInventoryRightClickAction(this, var4, var1, var3);
                        if (var5 != null) {
                           return (ContainerActionResult)var5.get();
                        }
                     }

                     return new ContainerActionResult((String)null);
                  default:
                     return new ContainerActionResult((String)null);
               }
            }
         }
      }
   }

   public ContainerActionResult applyLeftClick(int var1, ContainerSlot var2) {
      if (this.getClientDraggingSlot().isClear()) {
         if (!var2.isClear()) {
            this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, var2, true, false, "leftclick");
            return new ContainerActionResult(1);
         } else {
            return new ContainerActionResult((String)null);
         }
      } else {
         if (var2.isClear()) {
            String var3 = var2.getItemInvalidError(this.getClientDraggingSlot().getItem());
            if (var3 != null) {
               return new ContainerActionResult(5, var3);
            }
         }

         ItemCombineResult var6 = var2.combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, this.getClientDraggingSlot(), true, false, "leftclick");
         if (!var6.success) {
            ItemCombineResult var4 = this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, var2, true, false, "leftclickinv");
            if (!var4.success) {
               ItemCombineResult var5 = var2.swapItems(this.getClientDraggingSlot());
               return new ContainerActionResult(3, var5.error);
            } else {
               return new ContainerActionResult(4, var4.error);
            }
         } else {
            return new ContainerActionResult(2, var6.error);
         }
      }
   }

   public ContainerActionResult applyRightClick(int var1, ContainerSlot var2) {
      if (this.getClientDraggingSlot().isClear()) {
         if (!var2.isClear()) {
            if (var2.getItemAmount() <= 1) {
               this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, var2, true, false, "leftclick");
               return new ContainerActionResult(1);
            } else {
               int var4 = (var2.getItemAmount() + 1) / 2;
               this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, var2, var4, false, false, "rightclick");
               return new ContainerActionResult(2);
            }
         } else {
            return new ContainerActionResult((String)null);
         }
      } else {
         ItemCombineResult var3 = var2.combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, this.getClientDraggingSlot(), 1, false, false, "rightclick");
         return !var3.success ? new ContainerActionResult(4, var3.error) : new ContainerActionResult(3, var3.error);
      }
   }

   public ContainerActionResult transferFromAmount(int var1, ContainerSlot var2, int var3) {
      if (var2.isClear()) {
         return new ContainerActionResult((String)null);
      } else {
         LinkedList var4 = new LinkedList();
         boolean var5 = false;
         Iterator var6 = this.quickTransferOptions.iterator();

         while(true) {
            QuickTransferOption var7;
            while(true) {
               do {
                  if (!var6.hasNext()) {
                     ContainerTransferResult var8 = this.transferToSlots(var2, var4, var3);
                     return new ContainerActionResult(var3 - var8.amount, var8.error);
                  }

                  var7 = (QuickTransferOption)var6.next();
               } while(var7.filter != null && !var7.filter.test(var2));

               if (var7.onlyIfNoOtherFilterValid) {
                  if (var5) {
                     continue;
                  }
                  break;
               }

               var5 = true;
               break;
            }

            if (var1 >= var7.input.fromIndex && var1 <= var7.input.toIndex) {
               var4.addAll(Arrays.asList(var7.outputs));
            }
         }
      }
   }

   public ContainerActionResult transferIntoAmount(int var1, ContainerSlot var2, int var3) {
      if (!var2.isClear()) {
         boolean var5 = false;
         Iterator var6 = this.quickTransferOptions.iterator();

         QuickTransferOption var7;
         do {
            while(true) {
               do {
                  if (!var6.hasNext()) {
                     return new ContainerActionResult((String)null);
                  }

                  var7 = (QuickTransferOption)var6.next();
               } while(var7.filter != null && !var7.filter.test(var2));

               if (var7.onlyIfNoOtherFilterValid) {
                  if (var5) {
                     continue;
                  }
                  break;
               }

               var5 = true;
               break;
            }
         } while(var1 < var7.input.fromIndex || var1 > var7.input.toIndex);

         String var8 = null;
         SlotIndexRange[] var9 = var7.outputs;
         int var10 = var9.length;

         for(int var11 = 0; var11 < var10; ++var11) {
            SlotIndexRange var12 = var9[var11];
            ContainerTransferResult var13 = this.transferFromSlots(var2, var12.fromIndex, var12.toIndex, var3);
            var3 = var13.amount;
            if (var13.error != null) {
               var8 = var13.error;
            }
         }

         return new ContainerActionResult(var3 - var3, var8);
      } else {
         return new ContainerActionResult((String)null);
      }
   }

   public ContainerActionResult applyQuickMove(int var1, ContainerSlot var2) {
      return this.transferFromAmount(var1, var2, Integer.MAX_VALUE);
   }

   public ContainerActionResult applyTakeOne(int var1, ContainerSlot var2) {
      if (!var2.isClear()) {
         ItemCombineResult var3 = this.getClientDraggingSlot().combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, var2, 1, var2.getItemAmount() == 1, false, "rightclick");
         return new ContainerActionResult(var3.success ? 2 : 1, var3.error);
      } else {
         return new ContainerActionResult((String)null);
      }
   }

   public ContainerActionResult applyQuickMoveOne(int var1, ContainerSlot var2) {
      return this.transferFromAmount(var1, var2, 1);
   }

   public ContainerActionResult applyQuickGetOne(int var1, ContainerSlot var2) {
      return this.transferIntoAmount(var1, var2, 1);
   }

   public ContainerActionResult applyQuickTrash(int var1, ContainerSlot var2) {
      if (!var2.isClear() && !var2.isItemLocked()) {
         if (var1 == this.CLIENT_TRASH_SLOT) {
            this.transferToSlots(var2, Arrays.asList(new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END), new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END)));
            return new ContainerActionResult(1);
         } else {
            this.getSlot(this.CLIENT_TRASH_SLOT).combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, var2, true, false, "trash");
            return new ContainerActionResult(2);
         }
      } else {
         return new ContainerActionResult(0);
      }
   }

   public ContainerActionResult applyQuickTrashOne(int var1, ContainerSlot var2) {
      if (!var2.isClear() && !var2.isItemLocked()) {
         if (var1 == this.CLIENT_TRASH_SLOT) {
            this.transferToSlots(var2, Arrays.asList(new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END), new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END)), 1);
            return new ContainerActionResult(1);
         } else {
            this.getSlot(this.CLIENT_TRASH_SLOT).combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, var2, 1, true, false, "trash");
            return new ContainerActionResult(2);
         }
      } else {
         return new ContainerActionResult(0);
      }
   }

   public void addQuickTransferOption(Predicate<ContainerSlot> var1, int var2, int var3, int var4, int var5) {
      this.quickTransferOptions.addFirst(new QuickTransferOption(var1, false, new SlotIndexRange(var2, var3), new SlotIndexRange[]{new SlotIndexRange(var4, var5)}));
   }

   public void addQuickTransferOption(int var1, int var2, int var3, int var4) {
      this.addQuickTransferOption((Predicate)null, var1, var2, var3, var4);
   }

   public void addQuickTransferOption(Predicate<ContainerSlot> var1, int var2, int var3, SlotIndexRange... var4) {
      this.quickTransferOptions.addFirst(new QuickTransferOption(var1, false, new SlotIndexRange(var2, var3), var4));
   }

   public void addQuickTransferOption(int var1, int var2, SlotIndexRange... var3) {
      this.addQuickTransferOption((Predicate)null, var1, var2, var3);
   }

   public void addInventoryQuickTransfer(Predicate<ContainerSlot> var1, int var2, int var3) {
      this.addQuickTransferOption(var1, this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END, var2, var3);
      this.addQuickTransferOption(var1, this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END, var2, var3);
      this.addQuickTransferOption(var1, var2, var3, new SlotIndexRange(this.CLIENT_HOTBAR_START, this.CLIENT_HOTBAR_END), new SlotIndexRange(this.CLIENT_INVENTORY_START, this.CLIENT_INVENTORY_END));
   }

   public void addInventoryQuickTransfer(int var1, int var2) {
      this.addInventoryQuickTransfer((Predicate)null, var1, var2);
   }

   public int addSlot(ContainerSlot var1) {
      int var2 = this.slots.size();
      this.craftInventories.add(var1.getInventory());
      this.slots.add(var1);
      var1.init(this, var2);
      return var2;
   }

   public ContainerSlot getSlot(int var1) {
      return var1 >= 0 && var1 < this.slots.size() ? (ContainerSlot)this.slots.get(var1) : null;
   }

   public void lockSlot(int var1) {
      this.lockedSlots.add(var1);
   }

   public void lockSlot(PlayerInventorySlot var1) {
      for(int var2 = 0; var2 < this.slots.size(); ++var2) {
         ContainerSlot var3 = (ContainerSlot)this.slots.get(var2);
         if (var3.getInventory() instanceof PlayerInventory) {
            PlayerInventory var4 = (PlayerInventory)var3.getInventory();
            if (var4.getInventoryID() == var1.inventoryID && var3.getInventorySlot() == var1.slot) {
               this.lockSlot(var2);
            }
         }
      }

   }

   public boolean isSlotLocked(int var1) {
      return this.lockedSlots.contains(var1);
   }

   public boolean isSlotLocked(ContainerSlot var1) {
      return this.isSlotLocked(var1.getContainerIndex());
   }

   public PlayerInventoryManager getClientInventory() {
      return this.client.playerMob.getInv();
   }

   public ContainerSlot getClientDraggingSlot() {
      return this.getSlot(this.CLIENT_DRAGGING_SLOT);
   }

   public ContainerTransferResult transferToSlots(ContainerSlot var1, int var2, int var3) {
      return this.transferToSlots(var1, var2, var3, Integer.MAX_VALUE);
   }

   public ContainerTransferResult transferToSlots(ContainerSlot var1, int var2, int var3, String var4) {
      return this.transferToSlots(var1, var2, var3, Integer.MAX_VALUE, var4);
   }

   public ContainerTransferResult transferToSlots(ContainerSlot var1, Iterable<SlotIndexRange> var2) {
      return this.transferToSlots(var1, var2, "transfer");
   }

   public ContainerTransferResult transferToSlots(ContainerSlot var1, Iterable<SlotIndexRange> var2, String var3) {
      return this.transferToSlots(var1, var2, Integer.MAX_VALUE, var3);
   }

   public ContainerTransferResult transferToSlots(ContainerSlot var1, int var2, int var3, int var4) {
      return this.transferToSlots(var1, var2, var3, var4, "transfer");
   }

   public ContainerTransferResult transferToSlots(ContainerSlot var1, int var2, int var3, int var4, String var5) {
      return this.transferToSlots(var1, Collections.singleton(new SlotIndexRange(var2, var3)), var4, var5);
   }

   public ContainerTransferResult transferToSlots(ContainerSlot var1, Iterable<SlotIndexRange> var2, int var3) {
      return this.transferToSlots(var1, var2, var3, "transfer");
   }

   public ContainerTransferResult transferToSlots(ContainerSlot var1, Iterable<SlotIndexRange> var2, int var3, String var4) {
      String var5 = null;
      boolean var6 = false;

      for(int var7 = 0; var7 < 2; ++var7) {
         Iterator var8 = var2.iterator();

         while(var8.hasNext()) {
            SlotIndexRange var9 = (SlotIndexRange)var8.next();

            for(int var10 = var9.fromIndex; var10 <= var9.toIndex; ++var10) {
               if (var1.isClear() || var3 <= 0) {
                  return new ContainerTransferResult(var3, var5);
               }

               ContainerSlot var11 = this.getSlot(var10);
               if (var7 != 0 || !var11.isClear()) {
                  int var12 = var1.getItemAmount();
                  ItemCombineResult var13 = var11.combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, var1, var3, true, false, var4);
                  if (var13.success) {
                     int var14 = var12 - var1.getItemAmount();
                     var3 -= var14;
                     var6 = true;
                     var5 = null;
                  } else if (!var6 && var13.error != null) {
                     var5 = var13.error;
                  }
               }
            }
         }
      }

      return new ContainerTransferResult(var3, var5);
   }

   public ContainerTransferResult transferFromSlots(ContainerSlot var1, int var2, int var3, int var4) {
      return this.transferFromSlots(var1, var2, var3, var4, "transfer");
   }

   public ContainerTransferResult transferFromSlots(ContainerSlot var1, int var2, int var3, int var4, String var5) {
      String var6 = null;

      for(int var7 = var3; var7 >= var2; --var7) {
         if (var4 <= 0) {
            return new ContainerTransferResult(var4, var6);
         }

         ContainerSlot var8 = this.getSlot(var7);
         if (!var8.isClear()) {
            int var9 = var8.getItemAmount();
            ItemCombineResult var10 = var1.combineSlots(this.client.playerMob.getLevel(), this.client.playerMob, var8, var4, true, false, var5);
            if (var10.success) {
               int var11 = var9 - var8.getItemAmount();
               var4 -= var11;
            } else if (var10.error != null) {
               var6 = var10.error;
            }
         }
      }

      return new ContainerTransferResult(var4, var6);
   }

   public ArrayList<InventoryRange> getNearbyInventories(Level var1, int var2, int var3, int var4) {
      return this.getNearbyInventories(var1, var2, var3, var4, (Predicate)null);
   }

   public ArrayList<InventoryRange> getNearbyInventories(Level var1, int var2, int var3, int var4, Predicate<OEInventory> var5) {
      if (var1 == null) {
         return new ArrayList();
      } else {
         int var6 = var4 / 32;
         int var7 = var2 / 32 - var6 - 1;
         int var8 = var3 / 32 - var6 - 1;
         int var9 = var2 / 32 + var6 + 1;
         int var10 = var3 / 32 + var6 + 1;
         ArrayList var11 = new ArrayList();

         for(int var12 = var8; var12 <= var10; ++var12) {
            if (var12 >= 0 && var12 < var1.height) {
               for(int var13 = var7; var13 <= var9; ++var13) {
                  if (var13 >= 0 && var13 < var1.width && (new Point(var2, var3)).distance((double)(var13 * 32 + 16), (double)(var12 * 32 + 16)) <= (double)var4) {
                     ObjectEntity var14 = var1.entityManager.getObjectEntity(var13, var12);
                     if (var14 instanceof OEInventory && (var5 == null || var5.test((OEInventory)var14))) {
                        Inventory var15 = ((OEInventory)var14).getInventory();
                        if (!var11.stream().anyMatch((var1x) -> {
                           return var1x.inventory == var15;
                        })) {
                           var11.add(new InventoryRange(var15));
                        }
                     }
                  }
               }
            }
         }

         return var11;
      }
   }

   public ArrayList<InventoryRange> getNearbyInventories(Level var1, int var2, int var3, GameTileRange var4, Predicate<OEInventory> var5) {
      if (var1 == null) {
         return new ArrayList();
      } else {
         ArrayList var6 = new ArrayList();
         Iterator var7 = var4.getValidTiles(var2, var3).iterator();

         while(true) {
            ObjectEntity var9;
            do {
               do {
                  if (!var7.hasNext()) {
                     return var6;
                  }

                  Point var8 = (Point)var7.next();
                  var9 = var1.entityManager.getObjectEntity(var8.x, var8.y);
               } while(!(var9 instanceof OEInventory));
            } while(var5 != null && !var5.test((OEInventory)var9));

            Inventory var10 = ((OEInventory)var9).getInventory();
            if (!var6.stream().anyMatch((var1x) -> {
               return var1x.inventory == var10;
            })) {
               var6.add(new InventoryRange(var10));
            }
         }
      }
   }

   public void quickStackPlayerInventory() {
      ArrayList var1 = this.getNearbyInventories(this.client.playerMob.getLevel(), this.client.playerMob.getX(), this.client.playerMob.getY(), 192, OEInventory::canQuickStackInventory);
      this.quickStackToInventories(var1, (Inventory)this.client.playerMob.getInv().main);
   }

   public void quickStackToInventories(ArrayList<InventoryRange> var1, InventoryRange var2) {
      for(int var3 = var2.startSlot; var3 <= var2.endSlot; ++var3) {
         if (!var2.inventory.isSlotClear(var3) && !var2.inventory.isItemLocked(var3)) {
            Iterator var4 = var1.iterator();

            while(var4.hasNext()) {
               InventoryRange var5 = (InventoryRange)var4.next();
               if (var5.inventory.getAmount(this.client.playerMob.getLevel(), this.client.playerMob, var2.inventory.getItemSlot(var3), var5.startSlot, var5.endSlot, "quickstackto") > 0) {
                  int var6 = var2.inventory.getAmount(var3);
                  var5.inventory.addItem(this.client.playerMob.getLevel(), this.client.playerMob, var2.inventory.getItem(var3), var5.startSlot, var5.endSlot, "quickstackto", (InventoryAddConsumer)null);
                  if (var6 != var2.inventory.getAmount(var3)) {
                     var2.inventory.markDirty(var3);
                  }

                  if (var2.inventory.getAmount(var3) <= 0) {
                     var2.inventory.clearSlot(var3);
                     break;
                  }
               }
            }
         }
      }

   }

   public void quickStackToInventories(ArrayList<InventoryRange> var1, Inventory var2) {
      this.quickStackToInventories(var1, new InventoryRange(var2));
   }

   public void restockPlayerInventory() {
      ArrayList var1 = this.getNearbyInventories(this.client.playerMob.getLevel(), this.client.playerMob.getX(), this.client.playerMob.getY(), 192, OEInventory::canRestockInventory);
      this.restockFromInventories(var1, (Inventory)this.client.playerMob.getInv().main);
   }

   public void restockFromInventories(ArrayList<InventoryRange> var1, InventoryRange var2) {
      Iterator var3 = var1.iterator();

      while(var3.hasNext()) {
         InventoryRange var4 = (InventoryRange)var3.next();

         for(int var5 = var4.startSlot; var5 <= var4.endSlot; ++var5) {
            if (!var4.inventory.isSlotClear(var5) && !var4.inventory.isItemLocked(var5)) {
               InventoryItem var6 = var4.inventory.getItem(var5);
               if (var2.inventory.restockFrom(this.client.playerMob.getLevel(), this.client.playerMob, var6, var2.startSlot, var2.endSlot, "restockfrom", false, (InventoryAddConsumer)null)) {
                  if (var6.getAmount() <= 0) {
                     var4.inventory.setItem(var5, (InventoryItem)null);
                  }

                  var4.inventory.updateSlot(var5);
               }
            }
         }
      }

   }

   public void restockFromInventories(ArrayList<InventoryRange> var1, Inventory var2) {
      this.restockFromInventories(var1, new InventoryRange(var2));
   }

   public final void runCustomAction(int var1, PacketReader var2) {
      this.actionRegistry.runAction(var1, var2);
   }

   public final <T extends ContainerCustomAction> T registerAction(T var1) {
      return this.actionRegistry.registerAction(var1);
   }

   public final <T extends ContainerEvent> void subscribeEvent(Class<T> var1, final Predicate<T> var2, final BooleanSupplier var3) {
      this.eventSubscriptions.removeIf((var0) -> {
         return !var0.isActive();
      });
      this.eventSubscriptions.add(new ContainerEventSubscription<T>(var1) {
         public boolean shouldReceiveEvent(T var1) {
            return var2.test(var1);
         }

         public boolean isActive() {
            return var3.getAsBoolean();
         }
      });
   }

   public final <T extends ContainerEvent> ContainerEventHandler<T> onEvent(Class<T> var1, ContainerEventHandler<T> var2) {
      if (var2.isDisposed()) {
         return var2;
      } else {
         var2.init(var1, this.eventHandlers.addLast(var1, var2));
         return var2;
      }
   }

   public final <T extends ContainerEvent> ContainerEventHandler<T> onEvent(Class<T> var1, final Consumer<T> var2, final BooleanSupplier var3) {
      return this.onEvent(var1, new ContainerEventHandler<T>() {
         public void handleEvent(T var1) {
            if (!var3.getAsBoolean()) {
               this.dispose();
            } else {
               var2.accept(var1);
            }

         }
      });
   }

   public final <T extends ContainerEvent> ContainerEventHandler<T> onEvent(Class<T> var1, final Consumer<T> var2) {
      return this.onEvent(var1, new ContainerEventHandler<T>() {
         public void handleEvent(T var1) {
            var2.accept(var1);
         }
      });
   }

   public final boolean shouldReceiveEvent(ContainerEvent var1) {
      boolean var2 = false;
      ListIterator var3 = this.eventSubscriptions.listIterator();

      while(var3.hasNext()) {
         ContainerEventSubscription var4 = (ContainerEventSubscription)var3.next();
         if (!var4.isActive()) {
            var3.remove();
         } else if (var4.testUntypedEvent(var1)) {
            var2 = true;
         }
      }

      return var2;
   }

   public final void handleEvent(ContainerEvent var1) {
      GameLinkedList var2 = (GameLinkedList)this.eventHandlers.get(var1.getClass());

      GameLinkedList.Element var4;
      for(GameLinkedList.Element var3 = var2.getFirstElement(); var3 != null; var3 = var4) {
         var4 = var3.next();
         ((ContainerEventHandler)var3.object).handleEventUntyped(var1);
      }

   }

   public int applyCraftingAction(int var1, int var2, int var3, boolean var4) {
      Recipe var5 = this.getRecipe(var1);
      if (var5 == null) {
         GameLog.warn.println(this.client.playerMob.getDisplayName() + " tried to craft a not existing recipe with id " + var1);
         return 0;
      } else {
         ContainerSlot var6 = this.getClientDraggingSlot();
         if (var2 != var5.getRecipeHash()) {
            return 0;
         } else {
            Collection var7 = this.getCraftInventories();
            int var8 = 0;

            for(int var9 = 0; var9 < var3 && this.canCraftRecipe(var5, var7, false).canCraft(); ++var9) {
               ContainerRecipeCraftedEvent var10 = new ContainerRecipeCraftedEvent(var5, var5.craft(this.client.playerMob.getLevel(), this.client.playerMob, (Iterable)var7), this);
               var5.submitCraftedEvent(var10);
               InventoryItem var11 = var10.resultItem;
               InventoryItem var12 = var11;
               InventoryItem var13 = null;
               if (var4) {
                  int var14 = this.client.playerMob.getInv().canAddItem(var11, true, "crafting");
                  if (var14 >= 0) {
                     int var15 = var11.getAmount() - var14;
                     if (var15 > 0) {
                        var12 = var11.copy(var15);
                        var13 = var11.copy(var14);
                     } else {
                        var13 = var11;
                        var12 = null;
                     }
                  }
               }

               if (var12 != null && !var6.isClear() && (!var6.getItem().canCombine(this.client.playerMob.getLevel(), this.client.playerMob, var12, "crafting") || var6.getItemAmount() + var12.getAmount() > var6.getItemStackLimit(var6.getItem()))) {
                  var10.itemsUsed.forEach(InventoryItemsRemoved::revert);
                  break;
               }

               boolean var16 = false;
               ++var8;
               if (var13 != null) {
                  var13.setNew(true);
                  this.client.playerMob.getInv().addItem(var13, true, "crafting", (InventoryAddConsumer)null);
                  var16 = true;
               }

               if (var12 != null) {
                  if (var6.isClear()) {
                     var6.setItem(var12);
                  } else {
                     var6.getItem().combine(this.client.playerMob.getLevel(), this.client.playerMob, var6.getInventory(), var6.getInventorySlot(), var12, "crafting", (InventoryAddConsumer)null);
                  }

                  var16 = true;
               }

               if (!var16) {
                  var10.itemsUsed.forEach(InventoryItemsRemoved::revert);
               } else {
                  var6.markDirty();
               }
            }

            if (var8 > 0 && this.client.isServer()) {
               this.client.getServerClient().newStats.crafted_items.increment(var8);
            }

            return var8;
         }
      }
   }

   private int addRecipe(Recipe var1, boolean var2) {
      ContainerRecipe var3 = new ContainerRecipe(this.recipes.size(), var1, var2);
      this.recipes.add(var3);
      return var3.id;
   }

   public int addRecipe(Recipe var1) {
      return this.addRecipe(var1, false);
   }

   public void addRecipes(Collection<Recipe> var1) {
      var1.forEach(this::addRecipe);
   }

   public Recipe getRecipe(int var1) {
      return this.recipes.size() <= var1 ? null : ((ContainerRecipe)this.recipes.get(var1)).recipe;
   }

   public Stream<ContainerRecipe> streamRecipes(Tech... var1) {
      return this.recipes.stream().filter((var1x) -> {
         Stream var10000 = Arrays.stream(var1);
         Recipe var10001 = var1x.recipe;
         Objects.requireNonNull(var10001);
         return var10000.anyMatch(var10001::matchTech);
      });
   }

   public CanCraft canCraftRecipe(Recipe var1, Collection<Inventory> var2, boolean var3) {
      return var1.canCraft(this.client.playerMob.getLevel(), this.client.playerMob, (Iterable)var2, var3);
   }

   public boolean doesShowRecipe(Recipe var1, Collection<Inventory> var2) {
      return var1.doesShow(this.client.playerMob.getLevel(), this.client.playerMob, (Iterable)var2);
   }

   public void markFullDirty() {
      this.getCraftInventories().forEach(Inventory::markFullDirty);
   }

   public Collection<Inventory> getCraftInventories() {
      return this.craftInventories;
   }

   public boolean isValid(ServerClient var1) {
      return !this.shouldClose;
   }

   public void close() {
      this.shouldClose = true;
   }

   public void onClose() {
      this.isClosed = true;
      if (this.client.isClient()) {
         GlobalData.updateCraftable();
      }

   }

   public boolean isClosed() {
      return this.isClosed;
   }

   public NetworkClient getClient() {
      return this.client;
   }

   protected static class QuickTransferOption {
      public final Predicate<ContainerSlot> filter;
      public final boolean onlyIfNoOtherFilterValid;
      public final SlotIndexRange input;
      public final SlotIndexRange[] outputs;

      public QuickTransferOption(Predicate<ContainerSlot> var1, boolean var2, SlotIndexRange var3, SlotIndexRange... var4) {
         this.filter = var1;
         this.onlyIfNoOtherFilterValid = var2;
         this.input = var3;
         this.outputs = var4;
      }
   }
}
