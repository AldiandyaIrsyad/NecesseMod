package necesse.engine.network.packet;

import necesse.engine.Screen;
import necesse.engine.Settings;
import necesse.engine.network.NetworkPacket;
import necesse.engine.network.Packet;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.network.client.Client;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.sound.SoundEffect;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.GameResources;
import necesse.inventory.InventoryItem;
import necesse.inventory.item.Item;
import necesse.level.maps.hudManager.floatText.ItemPickupText;

public class PacketShowPickupText extends Packet {
   public final Item item;
   public final int amount;
   public final boolean playPickupSound;

   public PacketShowPickupText(byte[] var1) {
      super(var1);
      PacketReader var2 = new PacketReader(this);
      this.item = ItemRegistry.getItem(var2.getNextShortUnsigned());
      this.amount = var2.getNextInt();
      this.playPickupSound = var2.getNextBoolean();
   }

   public PacketShowPickupText(Item var1, int var2, boolean var3) {
      this.item = var1;
      this.amount = var2;
      this.playPickupSound = var3;
      PacketWriter var4 = new PacketWriter(this);
      var4.putNextShortUnsigned(var1.getID());
      var4.putNextInt(var2);
      var4.putNextBoolean(var3);
   }

   public PacketShowPickupText(InventoryItem var1, boolean var2) {
      this(var1.item, var1.getAmount(), var2);
   }

   public void processClient(NetworkPacket var1, Client var2) {
      PlayerMob var3 = var2.getPlayer();
      if (var3 != null && var3.getLevel() != null) {
         if (Settings.showPickupText) {
            var3.getLevel().hudManager.addElement(new ItemPickupText(var3, new InventoryItem(this.item, this.amount)));
            if (this.playPickupSound) {
               Screen.playSound(GameResources.pop, SoundEffect.effect(var3));
            }

         }
      }
   }
}
