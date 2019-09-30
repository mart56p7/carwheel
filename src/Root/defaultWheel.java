package Root;

public class defaultWheel implements WheelInterface {
    private String name;
    private int productiontime;

    public defaultWheel(String name, int productiontime){
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
