package Root;

public class wheelNormal implements WheelInterface {
    private String name;
    private int productiontime;

    public wheelNormal(String name, int productiontime){
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
