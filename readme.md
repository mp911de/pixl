pixl
======

pixl is an operating system for a notification and display device inspired by IoT ideas. pixl pushes a display output
to a display device (e. g. LED matrix), can play notification sounds and you can interact with pixel by pushing a
trigger button. pixl is open for extension. It allows to define a [playlist](#playlist), add [applications](#applications)
 and [output devices](#devices). 
 
Here are some samples created the internal debug UI (Swing GUI)

![docs/time.png](docs/time.png)

![docs/progress.png](docs/progress.png)

![docs/weather.png](docs/weather.png)


Configuration
-------------

application.properties


playlist.json

Override frames config using application.json
-------------


Playlist
-------------


Extending pixl
-------------
application.json

Applications
-------------


How to build
-------------
Pixl is built using Gradle.

```
$ ./gradle
```

Currently the Swing Gui requires to be built using IntelliJ IDEA.