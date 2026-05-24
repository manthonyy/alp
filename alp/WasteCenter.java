public class WasteCenter {
    private String name;
    private String handledWasteType;
    private double currentLoad;
    private double maxCapacity;

    public WasteCenter(String name, String handledWasteType, double currentLoad, double maxCapacity) {
        this.name = name;
        this.handledWasteType = handledWasteType;
        this.currentLoad = currentLoad;
        this.maxCapacity = maxCapacity;
    }

    public String getName() {
        return name;
    }

    public String getHandledWasteType() {
        return handledWasteType;
    }

    // Menghitung persentase kapasitas saat ini
    public double getLoadPercentage() {
        return (currentLoad / maxCapacity) * 100;
    }

    // Menambahkan muatan baru ke center
    public void addLoad(double weight) {
        this.currentLoad += weight;
    }

    public void upgradeCapacity(double additionalCapacity) {
        this.maxCapacity += additionalCapacity;
        System.out.println(name + " has been upgraded with an additional capacity of " + additionalCapacity + " Kg.");
        System.out.println(name + "'s max capacity is now " + maxCapacity + " Kg.");
    }
    
    public double getMaxCapacity() { 
        return maxCapacity; 
    }

    public double getCurrentLoad() { 
        return currentLoad; 
    }
}