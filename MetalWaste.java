public class MetalWaste extends WasteItem {
    public MetalWaste(double weight){
        super("Metal Waste",weight,20);
    }
    @Override
    public void displayWasteInfo(){
        System.out.println("Metal Waste");
        System.out.println("High Recycling Value");
        System.out.println("Fast Processing");
    }
}
