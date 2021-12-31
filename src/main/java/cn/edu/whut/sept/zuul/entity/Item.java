package cn.edu.whut.sept.zuul.entity;

/**
 * 该类表示一个物体
 * @author txg
 * @version 2021.12.31
 */
public class Item {
    private String description;
    private float weight;

    /**
     * 构造方法，对物体对象初始化
     * @param description 物体的描述
     * @param weight 物体的重量
     */
    public Item(String description, float weight) {
        this.description = description;
        this.weight = weight;
    }

    /**
     * setter方法，获得这个物品的描述
     * @return
     */
    public String getDescription(){
        return this.description;
    }

    public float getWeight() {
        return weight;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }
}
