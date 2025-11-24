# Smart-Vacuum-Robot-Simulation

A Java-based simulation of a smart vacuum robot’s perception and mapping stack, implemented as part of the SPL 225 course at Ben-Gurion University. The system models cameras, LiDAR workers, GPS/IMU, and a Fusion-SLAM module communicating via a custom concurrent MicroServices framework.

## 🔧 Technologies Used

* **Java 8** (core implementation)
* **Custom MicroServices framework** (MessageBus, MicroService, Future)
* **Concurrent collections & synchronization** (blocking queues, thread-safe maps)
* **Event-Driven architecture & publish/subscribe messaging**
* **JSON parsing** using Gson
* **Maven** for build & dependency management
* **JUnit** tests (MessageBus, Camera/LiDAR, Fusion-Slam)

## 💡 Project Structure

### Core Framework – `bgu.spl.mics`

Implements the message-driven concurrency framework used by all services:

* **Message / Event / Broadcast** – typed messages enabling request/response or publish/subscribe behavior.
* **Future<T>** – async result holder allowing services to continue running while awaiting computation.
* **MessageBus / MessageBusImpl** – central router managing queues per microservice with safe concurrent dispatch.
* **MicroService** – base runnable class that manages subscriptions, message loops, and lifecycle termination.

### Application Layer – `bgu.spl.mics.application`

Provides runner and microservices simulating sensors and mapping via distributed event-driven processing. – `bgu.spl.mics.application`
Holds simulation runner, service classes, and data objects for the robot system.

* `GurionRockRunner` - main entry point for the simulation; responsible for parsing the configuration JSON, instantiating objects & services, registering them, and starting the threads.

##### Application Objects - `application.objects`

Lightweight data structures storing sensors, pose, and mapping info.

#### Application Services - `application.services`

Microservices wrapping each sensor and the SLAM process. – `bgu.spl.mics.application.services`
MicroServices that orchestrate the simulation:

* **TimeService** – global clock; publishes `TickBroadcast` on each tick and stops after a configured duration
* **CameraService** – wraps a `Camera` object, subscribes to ticks, and sends `DetectObjectEvent`s when camera data becomes available (based on detection time + camera frequency)
* **LiDarService** – wraps `LiDarWorkerTracker` and `LiDarDataBase`; subscribes to `DetectObjectEvent` and ticks, fetches point clouds, and emits `TrackedObjectEvent`s
* **PoseService** – provides the robot pose for each tick by reading pose JSON and sending `PoseEvent`s to Fusion-SLAM
* **FusionSlamService** – subscribes to `TrackedObjectEvent`, `PoseEvent`, `TickBroadcast`, and termination/crash broadcasts; integrates sensor data into global landmarks and updates statistics
* **CrashedBroadcast** – broadcast used to signal fatal sensor errors to all services
* **TrackedObjectEvent** – event used by LiDar workers to send tracked objects (with coordinates) towards Fusion-SLAM

### Resources

* `example input/` – reference input set with:

  * `configuration_file.json` – sensors, frequencies, file paths, timing
  * `camera_data.json` – camera detections by time and camera key
  * `lidar_data.json` – LiDAR point clouds by time
  * `pose_data.json` – robot poses per tick
  * `output.json` – example output structure
* `example_input_2/` – an additional input scenario with similar structure

### Root Layout

```text
.
├── pom.xml
├── example input/
├── example_input_2/
├── src/
│   ├── main/java/bgu/spl/mics/...
│   ├── main/java/bgu/spl/mics/application/objects/...
│   ├── main/java/bgu/spl/mics/application/services/...
│   └── main/java/bgu/spl/mics/example/...
└── src/test/java/...
```

## 🚀 How to Run

### 1. Build the project

From the project root:

```bash
mvn clean package
```

### 2. Run the simulation

Use `GurionRockRunner` as the main class. The first argument is the path to the **configuration JSON file**:

```bash
mvn exec:java \
  -Dexec.mainClass="bgu.spl.mics.application.GurionRockRunner" \
  -Dexec.args="example input/configuration_file.json"
```

You can also point to the second scenario:

```bash
mvn exec:java \
  -Dexec.mainClass="bgu.spl.mics.application.GurionRockRunner" \
  -Dexec.args="example_input_2/configuration_file.json"
```

### 3. Output

The simulation writes `output_file.json` next to the configuration file. It contains:

* `statistics` – runtime, number of detected/tracked objects, landmarks
* `landMarks` – final world map
* In error cases: `error`, `faultySensor`, `lastFrames`, and `poses`

## 📊 Workflow

Robot mapping emerges from asynchronous event exchange between sensors and SLAM:

```text
┌────────────┐      DetectObjectEvent       ┌──────────────┐
│  Camera(s) │ ───────────────────────────► │ LiDAR Worker │
└──────┬─────┘                               └──────┬───────┘
       │  StampedDetectedObjects                    │  TrackedObjectEvent
       ▼                                            ▼
┌────────────┐     PoseEvent + Mapping       ┌──────────────────┐
│  Pose Unit │──────────────────────────────►│  Fusion-SLAM     │
└──────┬─────┘                               └─────────┬────────┘
       │   TickBroadcast (Global Clock)               │  LandMarks + Stats
       ▼                                              ▼
┌────────────┐                                ┌──────────────────┐
│ TimeService│───────────────────────────────►│ Output JSON File │
└────────────┘                                └──────────────────┘
```

### Workflow Summary

1. Cameras detect objects at assigned ticks and publish events.
2. LiDAR workers fetch matching cloud points and track detected objects.
3. Pose service streams robot position over time.
4. Fusion-SLAM fuses tracking + pose into stable global landmarks.
5. Statistics update continuously, final output written on termination.

## 🏗️ Root Layout & Data File Formats

### 1. Configuration JSON (top-level)

Defines sensors, file paths, and simulation timing. Example structure:

```json
{
  "cameras": [
    {
      "id": 1,
      "frequency": 2,
      "camera_datas_path": "example input/camera_data.json",
      "camera_key": "camera1"
    }
  ],
  "LiDarConfigurations": [
    {
      "id": 1,
      "frequency": 3,
      "lidars_data_path": "example input/lidar_data.json"
    }
  ],
  "poseJsonFile": "example input/pose_data.json",
  "TickTime": 1,
  "Duration": 100
}
```

### 2. Camera Data JSON

Organized by camera key (e.g., `camera1`, `camera2`), each containing time-stamped detections:

```json
{
  "camera1": [
    {
      "time": 2,
      "detectedObjects": [
        { "id": "obj_1", "description": "chair" },
        { "id": "obj_2", "description": "table" }
      ]
    }
  ]
}
```

### 3. LiDAR Data JSON

Time-stamped point clouds per object (z is present but not used in calculations):

```json
[
  {
    "time": 4,
    "cloudPoints": [
      [0.1, 1.2, 0.0],
      [0.3, 1.4, 0.0]
    ]
  }
]
```

### 4. Pose Data JSON

Pose per tick in the charging-station frame:

```json
[
  {
    "time": 0,
    "pose": [0.0, 0.0, 0.0]
  },
  {
    "time": 1,
    "pose": [0.1, 0.0, 5.0]
  }
]
```

### 5. Output JSON

The simulation outputs a single `output_file.json` with:

* `statistics` – runtime and counts
* `landMarks` – final world map (ids, descriptions, global coordinates)
* Optional error section with faulty sensor and last frames

## 🧪 Tests & Debugging

* **Unit tests** under `src/test/java` for:

  * `MessageBusImpl` concurrency and routing
  * Camera/LiDar data preparation logic
  * Fusion-SLAM transformation of tracked objects into landmarks
* **Running tests**:

```bash
mvn test
```

* You can enable additional logging inside services (e.g., when handling TickBroadcasts or events) to trace the flow of messages.

## 🎓 Course Information

* **Course:** SPL 225 – Advanced Systems Programming / Concurrency
* **Institution:** Ben-Gurion University of the Negev
* **Year:** 2024–2025
* **Environment:** CS Lab Linux machines (Maven-based), Docker-compatible

## 🧑‍💻 Authors

* **Lior Lotan** – [LinkedIn](https://www.linkedin.com/in/lior-lotan/)
* **Noy Sela** – [LinkedIn](https://www.linkedin.com/in/noy-sela-659a32366/)

---

### 📝 Important Note

This project focuses on the design and correctness of the concurrent MicroServices framework and the perception/mapping logic. Make sure your implementation compiles and runs on the CS Lab Linux machines, uses the provided package structure, and respects the concurrency and synchronization constraints described in the assignment instructions.
