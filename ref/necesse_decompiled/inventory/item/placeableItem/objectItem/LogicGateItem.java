package necesse.inventory.item.placeableItem.objectItem;

import necesse.engine.GameEvents;
import necesse.engine.events.players.ItemPlaceEvent;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.packet.PacketPlaceLogicGate;
import necesse.engine.registries.LogicGateRegistry;
import necesse.engine.util.GameBlackboard;
import necesse.entity.mobs.Mob;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.mobs.buffs.BuffModifiers;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTooltips.ListGameTooltips;
import necesse.inventory.InventoryItem;
import necesse.inventory.PlayerInventorySlot;
import necesse.inventory.item.Item;
import necesse.inventory.item.placeableItem.PlaceableItem;
import necesse.level.gameLogicGate.GameLogicGate;
import necesse.level.maps.Level;

public class LogicGateItem extends PlaceableItem {
   public int gateID;

   public LogicGateItem(GameLogicGate var1, int var2, Item.Rarity var3) {
      super(var2, true);
      this.gateID = var1.getID();
      this.rarity = var3;
      this.dropsAsMatDeathPenalty = true;
      this.setItemCategory(new String[]{"wiring", "logicgates"});
      this.keyWords.add("logic");
      this.keyWords.add("gate");
      this.keyWords.add("logicgate");
      this.keyWords.add("wire");
   }

   public LogicGateItem(GameLogicGate var1) {
      this(var1, 100, Item.Rarity.NORMAL);
   }

   public void loadItemTextures() {
      this.itemTexture = this.getLogicGate().generateItemTexture();
   }

   public GameMessage getNewLocalization() {
      return this.getLogicGate().getLocalization();
   }

   public ListGameTooltips getTooltips(InventoryItem var1, PlayerMob var2, GameBlackboard var3) {
      ListGameTooltips var4 = super.getTooltips(var1, var2, var3);
      var4.add((Object)this.getLogicGate().getItemTooltips());
      return var4;
   }

   public void setupAttackContentPacket(PacketWriter var1, Level var2, int var3, int var4, PlayerMob var5, InventoryItem var6) {
      super.setupAttackContentPacket(var1, var2, var3, var4, var5, var6);
      var1.putNextByteUnsigned(var5.isAttacking ? var5.beforeAttackDir : var5.dir);
   }

   public String canPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      if (var2 >= 0 && var3 >= 0 && var2 < var1.width * 32 && var3 < var1.height * 32) {
         int var7 = var2 / 32;
         int var8 = var3 / 32;
         if (var1.isProtected(var7, var8)) {
            return "protected";
         } else {
            return var4.getPositionPoint().distance((double)(var7 * 32 + 16), (double)(var8 * 32 + 16)) > (double)this.getPlaceRange(var5, var4) ? "outofrange" : this.getLogicGate().canPlace(var1, var7, var8);
         }
      } else {
         return "outsidelevel";
      }
   }

   public InventoryItem onPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6) {
      int var7 = var2 / 32;
      int var8 = var3 / 32;
      ItemPlaceEvent var9 = new ItemPlaceEvent(var1, var7, var8, var4, var5);
      GameEvents.triggerEvent(var9);
      if (!var9.isPrevented()) {
         if (var1.isServer()) {
            this.getLogicGate().placeGate(var1, var7, var8);
            var1.getServer().network.sendToClientsWithTile(new PacketPlaceLogicGate(var1, var4.getServerClient(), this.gateID, var7, var8), var1, var7, var8);
            var4.getServerClient().newStats.objects_placed.increment(1);
            var1.getTile(var7, var8).checkAround(var1, var7, var8);
            var1.getObject(var7, var8).checkAround(var1, var7, var8);
         }

         if (this.singleUse) {
            var5.setAmount(var5.getAmount() - 1);
         }
      }

      return var5;
   }

   public InventoryItem onAttemptPlace(Level var1, int var2, int var3, PlayerMob var4, InventoryItem var5, PacketReader var6, String var7) {
      this.getLogicGate().attemptPlace(var1, var2 / 32, var3 / 32, var4, var7);
      return var5;
   }

   public float getAttackSpeedModifier(InventoryItem var1, Mob var2) {
      return var2 == null ? 1.0F : (Float)var2.buffManager.getModifier(BuffModifiers.BUILDING_SPEED);
   }

   public GameLogicGate getLogicGate() {
      return LogicGateRegistry.getLogicGate(this.gateID);
   }

   public void drawPlacePreview(Level var1, int var2, int var3, GameCamera var4, PlayerMob var5, InventoryItem var6, PlayerInventorySlot var7) {
      if (this.canPlace(var1, var2, var3, var5, var6, (PacketReader)null) == null) {
         float var8 = 0.5F;
         int var9 = var2 / 32;
         int var10 = var3 / 32;
         this.getLogicGate().drawPreview(var1, var9, var10, var8, var5, var4);
      }

   }

   public boolean showWires() {
      return true;
   }
}
