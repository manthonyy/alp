public class PlasticWaste extends WasteItem {
    public PlasticWaste(double weight){
        super("Plastic Waste",weight,10);
    }
    @Override
    public void displayWasteInfo(){
        System.out.println("Plastic Waste");
        System.out.println("High Pollution Impact");
        System.out.println("Standard Recycling Process");
    }
}
