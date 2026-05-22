public abstract class WasteItem {
    protected String wasteName;
    protected double weight;
    protected int pointsPerKg;
    public WasteItem(String wasteName,double weight,int pointsPerKg)
    {
        this.wasteName = wasteName;
        this.weight = weight;
        this.pointsPerKg = pointsPerKg;
    }
    public int calculatePoints(){
        return (int) weight * pointsPerKg;
    }
    public abstract void displayWasteInfo();
}
