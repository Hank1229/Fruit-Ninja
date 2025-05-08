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
   ```bash
   javac -d bin src/com/fruitninja/*.java
4. **Run**
   ```bash
   java -cp bin com.fruitninja.FruitNinjaGame

--- 

## 📂 Project Structure

src/com/fruitninja/
- FruitNinjaGame.java – main launcher & UI manager
- GamePanel.java – game loop, spawning, rendering, input
- GameObject.java – base class for moving objects
- Fruit.java / FruitHalf.java – whole fruits & sliced halves
- Bomb.java – bomb behavior & explosion logic
- BonusItem.java – extra‑life & slow‑motion items
- ComboEffect.java – combo pop‑up animation
- Explosion.java – explosion visuals
- Constants.java – all sizes, timings, paths, scores
- resource/ – background images & UI panels
- sound/ – .wav files for slices, bombs, power‑ups, music

--- 

## 🎛 Controls

1. Left‑click & drag – slice fruits
2. Pause/Resume – toggle with on‑screen buttons
3. Restart – click RESTART after Game Over
