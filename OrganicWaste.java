public class OrganicWaste extends WasteItem {
    public OrganicWaste(double weight){
        super("Organic Waste",weight,15);
    }
    @Override
    public void displayWasteInfo(){
        System.out.println("Organic Waste");
        System.out.println("Can Become Compost");
        System.out.println("Priority Waste");
    }
}
