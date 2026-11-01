# Five Minutes In The Bathroom (ห้านาทีที่ห้องน้ำ)
**วิชาการเขียนโปรแกรมเชิงวัตถุ (Object-Oriented Programming - OOP)**  
**ผู้จัดทำ**: พิพัฒน์ โชคโคกกรวด รหัสนักศึกษา 6804062612277 เซค 2

---

## 1. ข้อมูลภาพรวมของเกม (Game Overview)
เกมแนว Survival Horror สไตล์ Point-and-Click (ได้รับแรงบันดาลใจจาก Five Nights at Freddy's):
- **เนื้อเรื่องย่อ**: ในช่วงดึกคืนหนึ่ง ณ หอพัก คุณตื่นขึ้นมากลางดึกด้วยอาการปวดท้องอย่างรุนแรงจึงรีบวิ่งไปเข้าห้องน้ำ แต่ในระหว่างที่กำลังทำธุระ คุณได้ยินเสียงแปลกๆ ดังมาจากชั้นล่าง และเมื่อเปิดแท็บเล็ตตรวจกล้องวงจรปิด (CCTV) คุณพบว่ามีสิ่งมีชีวิตประหลาด 2 ตัวกำลังมุ่งหน้ามายังห้องน้ำที่คุณอยู่!
- **เป้าหมาย**: เอาชีวิตรอดในห้องน้ำให้ครบ **5 นาทีจริง (300 วินาที)** โดยมีเวลานับถอยหลังดิจิทัลจาก `05:00` สู่ `00:00`
- **การควบคุมการป้องกัน**:
  - **ประตูห้องน้ำ (Door)**: ป้องกันมอนสเตอร์ตัวที่ 1 (*Tung Tung Tung Sahur*) โดยกดปุ่ม `[ E ]`
  - **หน้าต่างระบายอากาศ (Vent)**: ป้องกันมอนสเตอร์ตัวที่ 2 (*Tralalero Tralala*) โดยกดปุ่ม `[ Q ]`

---

## 2. การประยุกต์ใช้หลักการ Object-Oriented Programming (OOP)

โครงสร้างซอร์สโค้ดถูกออกแบบตามหลักการ **4 เสาหลักของ OOP** และ Design Patterns อย่างเคร่งครัด:

```text
src/
├── Main.java                          // จุดเริ่มต้นโปรแกรม (Entry Point บน Swing EDT)
├── core/
│   ├── GameState.java                 // คลาสเก็บค่าคงที่สถานะของเกม (static final int: MENU, HOW_TO_PLAY, BATHROOM, TABLET, GAME_OVER, GAME_WIN)
│   ├── StateManager.java              // จัดการการเปลี่ยนสถานะ การเริ่มเกมใหม่ และการรีเซ็ตข้อมูล
│   ├── GameWindow.java                // หน้าต่างหลักที่สืบทอดจาก JFrame (extends JFrame) กำหนดขนาด 1280x720
│   └── GamePanel.java                 // Game Loop (60 FPS) ด้วย Thread/Runnable และการจัดการ Mouse/Key Input
├── entity/
│   ├── Monster.java                   // [Abstraction & Encapsulation] Abstract Class แม่ของมอนสเตอร์
│   ├── SahurMonster.java              // [Inheritance & Polymorphism] มอนสเตอร์เดินผ่านกล้อง CAM 1->2->3->4 มาที่ประตู
│   └── TralalaMonster.java            // [Inheritance & Polymorphism] มอนสเตอร์ไต่ท่อระบายอากาศจาก CAM 1 มาที่หน้าต่าง
├── system/
│   ├── GameTimer.java                 // ระบบนับเวลาถอยหลัง 5 นาที (05:00 -> 00:00)
│   ├── StaminaManager.java            // ระบบบริหาร Stamina 100 หน่วย และสถานะเหนื่อยหมดแรง (Exhaustion)
│   ├── BathroomManager.java           // ควบคุมสถานะเปิด/ปิด ประตู และหน้าต่างระบายอากาศ
│   ├── CameraId.java                  // คลาสระบุข้อมูลกล้องวงจรปิด CAM1 - CAM4 (รหัสกล้อง, ชื่อห้อง, ชั้น)
│   ├── CameraManager.java             // ควบคุมตำแหน่งกล้องที่เลือก การสลับชั้น 1/2 และระบบเลือกภาพอัตโนมัติตามตำแหน่งมอนสเตอร์
│   ├── MonsterAI.java                 // AI Loop จัดการมอนสเตอร์ผ่านอาเรย์ Monster[] ด้วยหลัก Polymorphism
│   ├── AssetManager.java              // โหลดและแคชรูปภาพด้วย ImageIcon, Image และอาเรย์ 1 มิติ (Linear Search)
│   └── SoundManager.java              // จัดการไฟล์เสียง WAV ด้วยอาเรย์ 1 มิติ, Scanner อ่านไฟล์ config และ Thread/Runnable
└── ui/
    ├── Renderer.java                  // [Abstraction] Interface ต้นแบบของการวาดผลกราฟิก
    ├── MenuRenderer.java              // วาดหน้าหลักสไตล์เกมสยองขวัญ (Start, How To Play, Exit)
    ├── HowToPlayRenderer.java         // หน้าคู่มืออธิบายวิธีเล่น 3 หัวข้อ (Objective, How to Survive, Controls)
    ├── BathroomRenderer.java          // วาดมุมมองบุคคลที่หนึ่งในห้องน้ำ นาฬิกานับถอยหลัง ประตู หน้าต่าง และมอนสเตอร์
    ├── TabletRenderer.java            // วาดภาพกล้อง CCTV 10 จุด แผนที่ย่อ Minimap และเวลาถอยหลัง
    └── EndingRenderer.java            // วาดภาพฉากจบ Game Over / Game Win พร้อมตรวจจับการวางเมาส์ (Hover Effect)
```

### รายละเอียดการประยุกต์ใช้ OOP:
1. **Encapsulation (การห่อหุ้มข้อมูล)**:
   - ตัวแปรสถานะที่สำคัญทั้งหมดถูกตั้งเป็น `private` เช่น ค่า Stamina, เวลาที่เหลือ, สถานะประตู/หน้าต่าง และ Stage ของมอนสเตอร์
   - การเข้าถึงหรือเปลี่ยนแปลงต้องผ่าน Getter / Setter และเมธอดที่มีการตรวจสอบความถูกต้อง (Data Validation) เช่น ไม่สามารถปิดประตูได้หากติดสถานะ Exhausted
2. **Inheritance (การสืบทอดคุณสมบัติ)**:
   - `SahurMonster` และ `TralalaMonster` สืบทอดคุณสมบัติและพฤติกรรมพื้นฐานมาจากคลาสแม่ `Monster` ทำให้โค้ดมีความเป็นระเบียบและลดความซ้ำซ้อน
3. **Polymorphism (การพ้องรูป)**:
   - มีการ Override เมธอด `updateAI()`, `renderOnCamera()`, และ `renderInBathroom()` ให้มีพฤติกรรมเฉพาะตามมอนสเตอร์แต่ละตัว
   - คลาส `MonsterAI` ควบคุมการทำงานของมอนสเตอร์ผ่านอาเรย์ `Monster[]` โดยใช้ Polymorphism จัดการพฤติกรรมของมอนสเตอร์ทุกตัวได้อย่างเป็นเอกภาพ
   - การวาดหน้าจอทุกหน้าทำผ่าน Interface `Renderer`
4. **Abstraction (การซ่อนความซับซ้อน)**:
   - ซ่อนความซับซ้อนของการคำนวณเดซิเบลเสียง การโหลดไฟล์แคชรูปภาพ และการวาดกราฟิกไว้เบื้องหลังคลาส `SoundManager`, `AssetManager` และ `Renderer`

---

## 3. กฎและระบบการเล่น (Game Mechanics)

### 3.1 ระบบเอาชีวิตรอด 5 นาที (5-Minute Countdown)
- ผู้เล่นต้องเอาชีวิตรอดในห้องน้ำตั้งแต่เวลา **05:00 จนถึง 00:00**
- เวลาถอยหลังจะแสดงอยู่ที่มุมขวาบนของหน้าจอห้องน้ำและหน้าจอแท็บเล็ต

### 3.2 ระบบค่าความเหนื่อย (Stamina & Exhaustion)
- ผู้เล่นมีหลอด Stamina เริ่มต้น **100 หน่วย**
- การกดปิดประตูหรือหน้าต่างจะใช้แรง ทำให้ค่า Stamina ลดลงอย่างต่อเนื่อง
- หากฝืนปิดจน Stamina เหลือ **0 หน่วย** จะเข้าสู่ **สภาวะหมดแรง (Exhausted)**:
  - ประตูและหน้าต่างจะถูกเปิดออกทันที
  - ผู้เล่นจะไม่สามารถกดปิดเพื่อป้องกันตัวได้ชั่วคราว
- Stamina จะค่อยๆ ฟื้นฟูคืนมาเมื่อปล่อยมือ หรือระหว่างที่ยกแท็บเล็ตขึ้นมาดูกล้องวงจรปิด

### 3.3 มอนสเตอร์และเส้นทางการบุก
1. **Tung Tung Tung Sahur** (บุกทางประตูห้องน้ำ):
   - เดินลาดตระเวนตามกล้อง: **CAM 1 $\rightarrow$ CAM 2 (ชั้น 1) $\rightarrow$ CAM 3 (ชั้น 2) $\rightarrow$ CAM 4 (ชั้น 2) $\rightarrow$ ประตูห้องน้ำ**
   - เมื่อมาถึงหน้าประตู ผู้เล่นต้องรีบปิดประตูห้องน้ำ `[ E ]` ค้างไว้จนกว่ามันจะยอมถอยกลับไป
   - หากเปิดประตูทิ้งไว้ หรือหมดแรงก่อนที่มันจะถอยไป $\rightarrow$ โดน Sahur บุกจู่โจม (Jumpscare) $\rightarrow$ **Game Over!**
2. **Tralalero Tralala** (บุกทางหน้าต่างระบายอากาศ):
   - ไต่ท่อระบายอากาศ: จากห้อง **CAM 1 $\rightarrow$ หน้าต่างระบายอากาศห้องน้ำ**
   - เมื่อโผล่หัวที่หน้าต่าง ผู้เล่นต้องรีบปิดหน้าต่าง `[ Q ]` ค้างไว้จนกว่ามันจะยอมล่าถอยกลับไป
   - หากปิดไม่ทัน หรือเปิดหน้าต่างออกก่อน $\rightarrow$ โดน Tralala พุ่งใส่ $\rightarrow$ **Game Over!**

### 3.4 ประสบการณ์สยองขวัญแบบสมจริง (No UI Spoilers)
- ตัวเกมไม่มีกล่องแจ้งเตือนหรือหลอดบอกเวลาถอยของมอนสเตอร์ เพื่อให้ผู้เล่นใช้การสังเกตเสียง (เสียงเคาะประตู, เสียงข่วนท่อ, เสียงฝีเท้า) และการเช็กกล้อง CCTV ด้วยตัวเอง
- หน้าจอนิ่ง มั่นคง ไม่มีการสั่นไหวรบกวนสายตา

---

## 4. ปุ่มควบคุม (Controls)

| การกระทำ | ปุ่มคีย์บอร์ด | การคลิกเมาส์ |
| :--- | :---: | :--- |
| **เปิด / ปิด ประตูห้องน้ำ (Door)** | `E` | คลิกที่บานประตูห้องน้ำ |
| **เปิด / ปิด หน้าต่างระบายอากาศ (Vent)** | `Q` | คลิกที่บานหน้าต่างระบายอากาศ |
| **เปิด / ปิด แท็บเล็ตดูกล้อง (Tablet)** | `SPACEBAR` | คลิกแถบด้านล่างจอแท็บเล็ต |
| **เลือกกล้อง CAM 1 - CAM 4** | - | คลิกที่ปุ่มกล้องบนแผนที่ Minimap |
| **สลับชั้น 1F / 2F (บนแท็บเล็ต)** | - | คลิกที่ปุ่มสลับชั้นบนแผนที่ Minimap |
| **เลือก Retry หรือ Main Menu (หน้าจบเกม)** | - | คลิกที่ปุ่มบนหน้าจอ (มีแสงไฮไลต์เมื่อชี้) |
| **ย้อนกลับ / เมนูหลัก** | `ESC` | - |

---

## 5. วิธีการรันโปรแกรม (How to Run)

### วิธีที่ 1: ดับเบิลคลิกไฟล์ JAR สำเร็จรูป (แนะนำ)
สามารถดับเบิลคลิกไฟล์ **`FiveMinutesInTheBathroom.jar`** หรือเปิด Command Prompt แล้วพิมพ์:
```bash
java -jar FiveMinutesInTheBathroom.jar
```

### วิธีที่ 2: รันผ่านสคริปต์ Batch File
ดับเบิลคลิกที่ไฟล์ **`run.bat`** เพื่อคอมไพล์โค้ดและเริ่มเกมโดยอัตโนมัติ

### วิธีที่ 3: รันผ่าน PowerShell
```powershell
.\run.ps1
```

---

## 6. โครงสร้างไฟล์และไฟล์เสียง (Assets & Audio Config)

- **โฟลเดอร์ภาพ (`assets/images/`)**:
  - หน้าจอหลักและฉากจบ: `menu_bg.jpg`, `game_over.png`, `game_win.png`
  - ห้องน้ำ: `bathroom_bg.png`, `door_open.png`, `door_closed.png`, `vent_open.png`, `vent_closed.png`, `sahur_at_door.png`, `tralala_at_vent.png`
  - กล้องวงจรปิด 10 สถานการณ์:
    - ชั้น 1: `cam1_empty.png`, `cam1_sahur.png`, `cam1_tralala.png`, `cam1_tralala_sahur.png`, `cam2_empty.png`, `cam2_sahur.png`
    - ชั้น 2: `cam3_empty.png`, `cam3_sahur.png`, `cam4_empty.png`, `cam4_sahur.png`

- **โฟลเดอร์เสียง (`assets/sounds/`)**:
  - ไฟล์เสียงมาตรฐาน: `ambient.wav`, `door_close.wav`, `door_open.wav`, `vent_close.wav`, `vent_open.wav`, `monster_banging.wav`, `jumpscare.wav`, `win.wav`
  - **ไฟล์ปรับแต่งระดับเสียง [`volume_config.txt`](file:///d:/FMITB/assets/sounds/volume_config.txt)**:
    ผู้เล่นสามารถเปิดแก้ไขระดับเสียง Master Volume และปรับเพิ่ม/ลดเดซิเบล (dB) ของแต่ละไฟล์เสียงได้ด้วยโปรแกรม Notepad โดยไม่ต้องคอมไพล์โปรแกรมใหม่
