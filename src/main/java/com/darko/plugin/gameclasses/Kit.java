package com.darko.plugin.gameclasses;

import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    private String inventory;

    private String name;

    private ItemStack icon;

    private List<String> availableAbilities = new ArrayList<>();

    private List<PotionEffect> potionEffects = new ArrayList<>();


    public String getInventory() {
        return this.inventory;
    }

    public void setInventory(String inventory) {
        this.inventory = inventory;
    }


    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public ItemStack getIcon() {
        return this.icon;
    }

    public void setIcon(ItemStack item){
        this.icon = item;
    }


    public List<String> getAvailableAbilities() {
        return this.availableAbilities;
    }

    public void setAvailableAbilities(List<String> abilities){
        this.availableAbilities = abilities;
    }


    public List<PotionEffect> getPotionEffects() {
        return potionEffects;
    }

    public void addPotionEffect(PotionEffect potionEffect){this.potionEffects.add(potionEffect);}

}
