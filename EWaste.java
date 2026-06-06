public class EWaste extends WasteItem {
    public EWaste(double weight){
        super("E-Waste",weight,30);
    }
    @Override
    public void displayWasteInfo(){
        System.out.println("E-Waste");
        System.out.println("Hazardous Waste");
        System.out.println("Emergency Processing");
    }
}
