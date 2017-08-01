package com.tongren.pojo;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

public class Surgery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String code;

    private String name;

    private String alias;

    private Float price;

    private String category;

    @Column(name = "charge_code")
    private String chargeCode;

    @Column(name = "charge_name")
    private String chargeName;

    @Column(name = "charge_count")
    private String chargeCount;

    @Column(name = "charge_price")
    private Float chargePrice;

    @Column(name = "extra_price")
    private Float extraPrice;

    private Integer level;

    /**
     * @return id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return code
     */
    public String getCode() {
        return code;
    }

    /**
     * @param code
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return alias
     */
    public String getAlias() {
        return alias;
    }

    /**
     * @param alias
     */
    public void setAlias(String alias) {
        this.alias = alias;
    }

    /**
     * @return price
     */
    public Float getPrice() {
        return price;
    }

    /**
     * @param price
     */
    public void setPrice(Float price) {
        this.price = price;
    }

    /**
     * @return category
     */
    public String getCategory() {
        return category;
    }

    /**
     * @param category
     */
    public void setCategory(String category) {
        this.category = category;
    }

    /**
     * @return charge_code
     */
    public String getChargeCode() {
        return chargeCode;
    }

    /**
     * @param chargeCode
     */
    public void setChargeCode(String chargeCode) {
        this.chargeCode = chargeCode;
    }

    /**
     * @return charge_name
     */
    public String getChargeName() {
        return chargeName;
    }

    /**
     * @param chargeName
     */
    public void setChargeName(String chargeName) {
        this.chargeName = chargeName;
    }

    /**
     * @return charge_count
     */
    public String getChargeCount() {
        return chargeCount;
    }

    /**
     * @param chargeCount
     */
    public void setChargeCount(String chargeCount) {
        this.chargeCount = chargeCount;
    }

    /**
     * @return charge_price
     */
    public Float getChargePrice() {
        return chargePrice;
    }

    /**
     * @param chargePrice
     */
    public void setChargePrice(Float chargePrice) {
        this.chargePrice = chargePrice;
    }

    /**
     * @return extra_price
     */
    public Float getExtraPrice() {
        return extraPrice;
    }

    /**
     * @param extraPrice
     */
    public void setExtraPrice(Float extraPrice) {
        this.extraPrice = extraPrice;
    }

    /**
     * @return level
     */
    public Integer getLevel() {
        return level;
    }

    /**
     * @param level
     */
    public void setLevel(Integer level) {
        this.level = level;
    }
}