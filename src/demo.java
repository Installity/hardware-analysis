/**
 * A short example that shows how to call each provided class. It prints raw
 * numbers and a few names. It is NOT a solution to the assignment: you still
 * have to measure load, store the devices, sort them, analyse them and present
 * the results.
 *
 * Build:  javac *.java
 * Run:    java demo        (run it from the folder that contains the ids folder)
 */
public class demo
{
    public static void main(String[] args) throws Exception
    {
        double totalMem = memInfo.getTotal() / (1024.0 * 1024);
        double used = memInfo.getUsed() / (1024.0 * 1024);
        double available = memInfo.getAvailable() / (1024.0 * 1024);

        System.out.println("cores:  " + cpuInfo.getCount());
        System.out.println("model:  " + cpuInfo.getModel());
        //System.out.println("core 0: busy=" + cpuInfo.getBusy(0) + " idle=" + cpuInfo.getIdle(0));
        System.out.println("Memory Statistics:\nmemory (KiB): total=" + memInfo.getTotal() + " KiB, used=" + memInfo.getUsed() + " KiB");
        System.out.printf("Total: %.2f GiB%n", totalMem);
        System.out.printf("Used: %.2f GiB%n", used);
        System.out.printf("Available: %.2f GiB%n", available);

        idLookup pci = idLookup.pci();
        System.out.println("PCI devices: " + pciInfo.getCount());
        for (int i = 0; i < pciInfo.getCount(); i++) {
            int vendor = pciInfo.getVendorId(i);
            int device = pciInfo.getDeviceId(i);
            System.out.printf("  %s  %04x:%04x  %s %s%n", pciInfo.getAddress(i), vendor, device,
                              pci.vendorName(vendor), pci.deviceName(vendor, device));
        }

        idLookup usb = idLookup.usb();
        System.out.println("USB devices: " + usbInfo.getCount());
        for (int i = 0; i < usbInfo.getCount(); i++) {
            int vendor = usbInfo.getVendorId(i);
            int product = usbInfo.getProductId(i);
            System.out.printf("  Bus %03d Device %03d: ID %04x:%04x  %s %s%n", usbInfo.getBusNumber(i),
                              usbInfo.getDeviceNumber(i), vendor, product,
                              usb.vendorName(vendor), usb.deviceName(vendor, product));
        }


        long[][] before = cpuInfo.readTicks();
        Thread.sleep(1000);
        long[][] after = cpuInfo.readTicks();

        double totalUsage = 0.0;
        int numCores = 0;

        for (int core = 0; core < before.length; core++) {
            long busy = cpuInfo.getBusy(after[core])
                    - cpuInfo.getBusy(before[core]);

            long idle = cpuInfo.getIdle(after[core])
                    - cpuInfo.getIdle(before[core]);

            long total = busy + idle;

            if (busy < 0 || idle < 0 || total <= 0) {
                System.out.printf("CPU %d: reading unavailable%n", core);
                continue;
            }

            double usage = 100.0 * busy / total;
            System.out.printf("CPU %d: %.1f%%%n", core, usage);
            totalUsage = totalUsage + usage;
            numCores++;
        }
        double avgUsage = totalUsage/numCores;
        System.out.printf("Average CPU usage: %.1f%%%n", avgUsage);


    }
}
