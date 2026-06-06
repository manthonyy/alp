public class GlassWaste extends WasteItem {
    public GlassWaste(double weight){
        super("Glass Waste",weight,8);
    }
    @Override
    public void displayWasteInfo(){
        System.out.println("Glass Waste");
        System.out.println("Low Pollution Impact");
        System.out.println("Longer Processing Time");
    }
}
