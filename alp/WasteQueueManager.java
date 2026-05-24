import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class WasteQueueManager {
    // Implementasi Queue menggunakan LinkedList agar bisa addFirst dan addLast
    private LinkedList<WasteItem> wasteQueue = new LinkedList<>();
    private List<WasteCenter> centers = new ArrayList<>();

    public WasteQueueManager() {
        initializeCenters();
    }

    // Inisialisasi data dummy untuk membandingkan kapasitas pabrik (Recommendation System)
    private void initializeCenters() {
        centers.add(new WasteCenter("Plastic Recycling Center A", "Plastic Waste", 950, 1000)); // Load 95%
        centers.add(new WasteCenter("Plastic Recycling Center B", "Plastic Waste", 400, 1000)); // Load 40%
        
        centers.add(new WasteCenter("Organic Waste Center A", "Organic Waste", 800, 1000));
        centers.add(new WasteCenter("Organic Waste Center B", "Organic Waste", 150, 1000));
        
        centers.add(new WasteCenter("E-Waste Processing Center A", "E-Waste", 300, 1000));
        
        centers.add(new WasteCenter("Metal Recycling Center", "Metal Waste", 500, 1000));

        centers.add(new WasteCenter("Glass Processing Center", "Glass Waste", 600, 1000));
    }

    // Menambah sampah ke antrian
    public void addWasteToQueue(WasteItem waste) {
        // Priority check: OrganicWaste dan EWaste masuk depan antrian
        if (waste instanceof OrganicWaste || waste instanceof EWaste) {
            wasteQueue.addFirst(waste);
            System.out.println(waste.wasteName + " Has been added to the FRONT of the queue (Priority Waste)");
        } else {
            wasteQueue.addLast(waste);
            System.out.println(waste.wasteName + " Has been added to the BACK of the queue");
        }
    }

    // Sistem Rekomendasi berdasarkan Load terkecil (kapasitas paling optimal)
    public WasteCenter recommendBestCenter(String wasteName) {
        WasteCenter bestCenter = null;
        double lowestLoad = 101.0; // Set di atas 100% sebagai nilai inisial

        for (WasteCenter center : centers) {
            if (center.getHandledWasteType().equals(wasteName)) {
                double currentLoadPct = center.getLoadPercentage();
                
                // Cari load paling kecil / kapasitas tersisa paling banyak
                if (currentLoadPct < lowestLoad) {
                    lowestLoad = currentLoadPct;
                    bestCenter = center;
                }
            }
        }
        return bestCenter;
    }

    // Memproses seluruh antrian dengan prinsip FIFO
    public void processQueue() {
        if (wasteQueue.isEmpty()) {
            System.out.println("Waste Queue is empty. No waste to process");
            return;
        }

        System.out.println("====================================");
        System.out.println(" Processing Waste Queue");
        System.out.println("====================================");

        int size = wasteQueue.size();
        for (int i = 0; i < size; i++) {
        WasteItem wasteToProcess = wasteQueue.removeFirst();

        boolean centerExists = false;
        for (WasteCenter c : centers) {
            if (c.getHandledWasteType().equals(wasteToProcess.wasteName)) {
                centerExists = true;
                break;
            }
        }

        if (!centerExists) {
            System.out.println("Status: FAILED. No Waste Center found for: " + wasteToProcess.wasteName);
            // Karena tidak ada center yang cocok, kita bisa membuang atau menyimpannya di list terpisah
            // Di sini kita masukkan ke antrian lagi dengan pesan peringatan
            wasteQueue.addLast(wasteToProcess);
        } else {
            // 2. Jika center ada, cari yang terbaik
            WasteCenter optimalCenter = recommendBestCenter(wasteToProcess.wasteName);
            double weightAdded = wasteToProcess.weight;
            double newLoadPercentage = ((optimalCenter.getCurrentLoad() + weightAdded) / optimalCenter.getMaxCapacity()) * 100;
        
        // Pengecekan apakah ada center yang tersedia DAN kapasitasnya belum penuh
            if (optimalCenter != null && newLoadPercentage < 100.0) {
                System.out.println("Processing: " + wasteToProcess.wasteName);
                System.out.printf("Optimal Recommendation : %s (Load: %.2f%%)\n", optimalCenter.getName(), optimalCenter.getLoadPercentage());
                
                optimalCenter.addLoad(wasteToProcess.weight);
                System.out.println("Status              : Successfully sent to " + optimalCenter.getName());
            } else {
                wasteQueue.addLast(wasteToProcess);
                System.out.println("Status: FAILED. Center for " + wasteToProcess.wasteName + " is FULL. Re-queued for later processing.");
                System.out.println("Action Required: Please upgrade capacity for " + wasteToProcess.wasteName + " centers.");
            }
            System.out.println("====================================\n");
        }
        }
    }

    public void displayCenters() {
        System.out.println("=== WASTE CENTERS STATUS ===");
        for (int i = 0; i < centers.size(); i++) {
            WasteCenter c = centers.get(i);
            System.out.printf("%d. %s [%s] | Load: %.2f%% | Max: %.2f Kg\n", 
                (i + 1), c.getName(), c.getHandledWasteType(), c.getLoadPercentage(), c.getMaxCapacity());
        }
    }

    public void upgradeCenter(int index, double amount) {
        if (index >= 0 && index < centers.size()) {
            centers.get(index).upgradeCapacity(amount);
        } else {
            System.out.println("Error: Center tidak ditemukan.");
        }
    }

    public void displayQueue() {
    System.out.println("\n=== CURRENT WASTE QUEUE ===");
    if (wasteQueue.isEmpty()) {
        System.out.println("The waste queue is currently empty");
    } else {
        int i = 1;
        for (WasteItem w : wasteQueue) {
            System.out.printf("%d. %s (Weight: %.2f Kg)\n", i++, w.wasteName, w.weight);
        }
    }
    System.out.println("===========================\n");
}
}