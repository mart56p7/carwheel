package Root;

public class wheelHigh implements WheelInterface {
    private String name;
    private int productiontime;

    public wheelHigh(String name, int productiontime){
        this.name = name;
        this.productiontime = productiontime;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public int getProductionTime() {
        return productiontime;
    }

    @Override
    public void doWork() {

    }
}
