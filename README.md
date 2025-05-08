# Fruit Ninja Swing Game

A Java Swing remake of the classic “Fruit Ninja” arcade game. Slice flying fruits, avoid bombs, rack up combos, and survive as long as you can!

---

## 🎮 Features

- **Smooth slicing**  
  Draw your mouse across the screen to slice fruits in real time with a multi‑layered glow blade trail.

- **Combo system**  
  Three consecutive slices within 0.8 seconds triggers a combo pop‑up effect at the fruit’s location.

- **Power‑ups**  
  - **Extra Life** star grants you one additional life  
  - **Slow Motion** clock slows down all objects for a short duration

- **Bombs**  
  - **Fatal Bomb** ends the game if sliced  
  - **Time Bomb** deducts seconds from your timer

- **Customizable**  
  All screen dimensions, speeds, spawn rates, resource paths, and score rules live in `Constants.java`—just tweak values and drop in new images/sounds.

- **Sound effects & music**  
  Slice, explosion, power‑up, and game‑over clips powered by Java Sound API.

---

## 🚀 Getting Started

1. **Clone the repo**  
   ```bash
   git clone https://github.com/<your‑username>/Fruit‑Ninja.git
   cd Fruit‑Ninja
2. **Build**
   javac -d bin src/com/fruitninja/*.java
3. **Run**
   java -cp bin com.fruitninja.FruitNinjaGame

--- 

##📂 Project Structure

src/com/fruitninja/
├── Bomb.java           # defines Bomb and explosion logic
├── BonusItem.java      # power‑up items: extra life, slow motion
├── ComboEffect.java    # animated combo pop‑up effect
├── Constants.java      # all game constants (sizes, paths, timings, scores)
├── Explosion.java      # renders explosion visuals
├── Fruit.java          # whole fruit entity, handles splitting
├── FruitHalf.java      # half‑fruit piece trajectory
├── FruitNinjaGame.java # main launcher & UI manager
├── GameObject.java     # abstract base for moving objects
└── GamePanel.java      # core game loop: spawn/update/render/collisions

resource/ – background images, UI panels
sound/ – .wav clips for slicing, bombs, power‑ups, music

--- 

##🎛 Controls

Left‑click & drag – slice fruits
Pause/Resume – toggle with on‑screen buttons
Restart – click RESTART after Game Over

---

##📝 License

This project is released under the MIT License. Feel free to fork, tweak, and share!
