# Minions Plugin

A Minecraft plugin that introduces placeable "Farming Blocks" (Minions) which automatically farm crops in a 3x3 area.

## Features

* **Automated Farming:** Place a Farming Block, and it will automatically plant, grow, and harvest crops in the surrounding 3x3 area (excluding the block's location).
* **Multiple Tiers:** Two levels of Farming Blocks with potentially different farming speeds (Level 1 and Level 2).
* **Configurable Crops:** Interact with the Farming Block via a GUI to select which type of crop it should farm (Wheat, Carrots, Potatoes).
* **Integrated Storage:** Farming Blocks have an internal inventory to store the harvested items.
* **Persistence:** Farming Blocks and their contents are saved and loaded when the server restarts.
* **Custom Recipes:** Craft the Farming Blocks using specific recipes.
* **Protection:** Farming Blocks cannot be broken by normal means (only via the in-game GUI option).

## Installation

1.  Download the latest release of the Minions plugin JAR file from the (https://github.com/PengiSarkus/Minions/releases) 
2.  Place the `Minions.jar` file into your server's `plugins` folder.
3.  Restart or reload your server.

## Usage

1.  Craft a Farmer Minion block using the recipes below.
2.  Place the Farmer Minion block on a suitable location (it requires farmland around it for crops).
3.  Right-click the placed Farmer Minion block to open its control panel GUI.
4.  From the control panel, you can:
    * Access the **Harvests Storage** (a chest icon) to collect harvested items.
    * Select the **Crop Type** (seeds icon) to change which crop the minion farms.
    * Use the **Break Block** option (barrier icon) to safely remove the minion and drop it as an item.

## Crafting Recipes

The plugin adds custom crafting recipes for the Farmer Minion blocks.

### Farmer Level 1

Crafted using Potatoes, Wheat, Diamond Block, and Carrots in a specific pattern:
P C P
W D W
P C P
* `P` = Potato
* `C` = Carrot
* `W` = Wheat
* `D` = Diamond Block

The result is an `END_STONE` item with the display name "FarmerSeviye1".

### Farmer Level 2

Crafted using Wheat, Diamond Blocks, and a Farmer Level 1 block:
W W W
D F D
W W W
* `W` = Wheat
* `D` = Diamond Block
* `F` = FarmerSeviye1 (the Level 1 Minion block item)

The result is an `END_STONE` item with the display name "FarmerSeviye2" and Unbreaking I enchantment (purely cosmetic on the item).

## Configuration

The plugin automatically saves and loads active Farming Blocks and their inventory data to `plugins/Minions/farming_blocks.yml`. There are currently no user-editable configuration options directly in a `config.yml`.

## Building from Source

If you want to build the plugin yourself:

1.  Clone the repository: `git clone https://github.com/PengiSarkus/Minions.git`
2.  Navigate into the cloned directory: `cd Minions`
3.  Build the project using Maven: `mvn clean package`
4.  The compiled JAR file will be located in the `target/` directory.
