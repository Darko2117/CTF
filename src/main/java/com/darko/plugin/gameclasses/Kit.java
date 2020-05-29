package com.darko.plugin.gameclasses;

import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class Kit {

    String inventory;

    String name;

    ItemStack icon;

    List<String> availableAbilities = new ArrayList<>();


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

}
