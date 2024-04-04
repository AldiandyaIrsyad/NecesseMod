package necesse.inventory.item;

import java.awt.Color;
import java.util.Iterator;
import necesse.engine.localization.message.GameMessage;
import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.util.GameLinkedList;
import necesse.engine.util.GameUtils;

public class ItemStatTipList extends ItemStatTip implements Iterable<ItemStatTip> {
   private GameLinkedList<Element> list = new GameLinkedList();

   public ItemStatTipList() {
   }

   public void add(int var1, ItemStatTip var2) {
      if (this.list.isEmpty()) {
         this.list.add(new Element(var2, var1));
      } else {
         int var3 = var1 - ((Element)this.list.getFirst()).priority;
         int var4 = ((Element)this.list.getLast()).priority - var1;
         GameLinkedList.Element var5;
         if (var3 < var4) {
            for(var5 = this.list.getFirstElement(); var5 != null; var5 = var5.next()) {
               if (var1 <= ((Element)var5.object).priority) {
                  var5.insertBefore(new Element(var2, var1));
                  return;
               }
            }

            this.list.addLast(new Element(var2, var1));
         } else {
            for(var5 = this.list.getLastElement(); var5 != null; var5 = var5.prev()) {
               if (var1 >= ((Element)var5.object).priority) {
                  var5.insertAfter(new Element(var2, var1));
                  return;
               }
            }

            this.list.addFirst(new Element(var2, var1));
         }
      }

   }

   public Iterator<ItemStatTip> iterator() {
      return GameUtils.mapIterator(this.list.iterator(), (var0) -> {
         return var0.tip;
      });
   }

   public GameMessage toMessage(Color var1, Color var2, Color var3, boolean var4) {
      GameMessageBuilder var5 = new GameMessageBuilder();
      boolean var6 = false;

      for(Iterator var7 = this.iterator(); var7.hasNext(); var6 = true) {
         ItemStatTip var8 = (ItemStatTip)var7.next();
         if (var6) {
            var5.append("\n");
         }

         var5.append(var8.toMessage(var1, var2, var3, var4));
      }

      return var5;
   }

   private static class Element {
      public final ItemStatTip tip;
      public final int priority;

      public Element(ItemStatTip var1, int var2) {
         this.tip = var1;
         this.priority = var2;
      }
   }
}
