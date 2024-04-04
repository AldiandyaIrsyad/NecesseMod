package necesse.inventory.container;

public class ContainerActionResult {
   public int value;
   public String error;

   public ContainerActionResult(String var1) {
      this.value = 0;
      this.error = var1;
   }

   public ContainerActionResult(int var1) {
      this.value = var1;
   }

   public ContainerActionResult(int var1, String var2) {
      this.value = var1;
      this.error = var2;
   }
}
