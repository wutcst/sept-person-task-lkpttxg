package cn.edu.whut.sept.zuul.entity;

import java.util.Objects;

/**
 * 该类表示一个物体.
 *
 * @author txg
 * @version 2021.12.31
 */
public class Item {
    private String name;
    private String description;
    private float weight;

    /**
     * 构造方法，对物体对象初始化.
     *
     *@param description 物体的描述
     * @param weight 物体的重量
     * @param name 物品名字
     */
    public Item(String name, String description, float weight) {
        this.name = name;
        this.description = description;
        this.weight = weight;
    }

    /**
     * 获取物体的长描述：name + 描述 + 重量.
     *
     *@return 返回长描述字符串
     */
    public String getLongDescription(){
        return name + "\t" + description + "\t" + weight;
    }

    /**
     * 获取物体的短描述：name + 重量.
     *
     *@return 返回短描述字符串
     */
    public String getShortDescription() {
        return name + "\t" + weight;
    }
    /**
     * setter方法，获得这个物品的描述.
     *
     *@return 返回物品描述
     */
    public String getDescription() {
        return this.description;
    }
    /**
     * setter方法，获得物体的重量.
     *
     * @return 获得物体重量
     */

    public float getWeight() {
        return weight;
    }

    public void setDescription(String description) {
        this.description = description;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {return true;}
        if (o == null || getClass() != o.getClass()) {return false;}
        Item item = (Item) o;
        return Float.compare(item.weight, weight) == 0 &&
                Objects.equals(name, item.name) &&
                Objects.equals(description, item.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, description, weight);
    }
}
