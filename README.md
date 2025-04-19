# 🌱 Minions Plugin

Say hello to your new little farming buddies! 🧑‍🌾✨  
**Minions** are placeable **Farming Blocks** that automatically tend to crops in a 3x3 area — so you can kick back while they do the dirty work.

---

## 🌾 What Do Minions Do?

These tiny but mighty farmhands help you live the *lazy farmer life*:

- 🌱 **Auto-Farming:** Once placed, Minions plant, grow, and harvest crops in a 3x3 area around them — hands-free!
- 📈 **Level Up:** Two tiers of Minions (Level 1 and Level 2) with different speeds and efficiencies.
- 🌽 **Crop Choice:** Use a friendly GUI to tell your Minion what to grow: Wheat, Carrots, or Potatoes.
- 🧺 **Built-in Storage:** Each Minion has its own inventory to stash harvested goodies.
- 🔒 **Safe & Sound:** Minions are saved across server restarts and can’t be broken by accident — you’ll need to use the GUI to remove them properly.
- 🧪 **Custom Crafting:** Special recipes to create both levels of Minions.
- 💾 **Auto Save:** All data lives safely in `farming_blocks.yml`.

---

## 📦 Installation

1. Download the latest Minions plugin from [GitHub Releases](https://github.com/PengiSarkus/Minions/releases).
2. Drop the `Minions.jar` into your server's `plugins` folder.
3. Restart (or reload) your server.
4. You're all set! 🎉

---

## 🛠️ How to Use

1. Craft a **Farmer Minion Block** (see recipes below).
2. Place it on farmland, surrounded by space for crops.
3. Right-click it to open its friendly **control panel**.
4. In the GUI, you can:
   - 🧺 Check **Harvest Storage** (chest icon)
   - 🌽 Pick the **Crop Type** (seeds icon)
   - ❌ Safely **Remove the Minion** (barrier icon)

---

## 🧾 Crafting Recipes

### 👩‍🌾 Farmer Minion - Level 1

Use this layout in a crafting table:

```
P C P
W D W
P C P
```

- `P` = Potato  
- `C` = Carrot  
- `W` = Wheat  
- `D` = Diamond Block  

🎁 Result: An `END_STONE` named **"FarmerSeviye1"**

---

### 🚜 Farmer Minion - Level 2

Ready for an upgrade? Here’s how:

```
W W W
D F D
W W W
```

- `W` = Wheat  
- `D` = Diamond Block  
- `F` = FarmerSeviye1 (Level 1 Minion)

🎁 Result: An `END_STONE` named **"FarmerSeviye2"**, with a touch of **Unbreaking I** enchantment (just for looks!).

---

## ⚙️ Configuration

No complex setup needed! Minions automatically save and load their data in:

```
plugins/Minions/farming_blocks.yml
```

Easy, breezy, farm-life squeezy. 🌤️

---

## 🔧 Building It Yourself

Wanna peek under the hood? Here's how to build it from source:

```bash
git clone https://github.com/PengiSarkus/Minions.git
cd Minions
mvn clean package
```

The final JAR will appear in the `target/` folder. Magic! 🪄

---

## 🧑‍💻 Contributing

Got ideas? Found a bug? Want to sprinkle in some code?  
Contributions, suggestions, and feedback are **super welcome**. Let’s grow this plugin together! 💚

---

> Made with ❤️ by Can  
> Keep an eye out for updates – this is just the beginning! 🌟
