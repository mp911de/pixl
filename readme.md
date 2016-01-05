pixl
======

pixl is an operating system for a notification and display device inspired by IoT ideas. pixl pushes a display output
to a display device (e. g. LED matrix), can play notification sounds and you can interact with pixel by pushing a
trigger button. pixl is open for extension. It allows to define a [playlist](#playlistjson), add [applications](#applications)
 and output devices. 
 
Here are some samples created the internal debug UI (Swing GUI)

![docs/time.png](docs/time.png)

![docs/progress.png](docs/progress.png)

![docs/weather.png](docs/weather.png)


How pixl works
-------------
pixl is a platform for applications, playlists and output devices. Applications provide data which is displayed on output devices
 by using [frame specifications](#applicationjson). It's as simple as that. An output device can show the output, can play a sound
 and has a trigger button for further interaction.
 
pixl creates in realtime the display data which is nice to see because it's animated. 

Configuration
-------------

pixl configuration is located within two files:

* `config/application.properties`
* `config/playlist.json`


### application.properties

This file contains basic settings that are related to the system configuration. This file contains properties to enable/disable plugins, set API keys and the locale.


### playlist.json

This file contains the playlist configuration. Every `item` in the playlist references an `application`. Applications are
the main data source and every application brings its own user interface. An `item` allows to specify a maximal duration for the application
(i. e. when to switch to the next application) and optionally a configuration which depends on the application itself.
The duration is specified as [ISO-8601](https://en.wikipedia.org/wiki/ISO_8601#Durations) period string. 

Examples:
  * `PT20.345S` -- parses as "20.345 seconds"
  * `PT15M`     -- parses as "15 minutes" (where a minute is 60 seconds)
  * `PT10H`     -- parses as "10 hours" (where an hour is 3600 seconds)
  * `P2D`       -- parses as "2 days" (where a day is 24 hours or 86400 seconds)
  * `P2DT3H4M`  -- parses as "2 days, 3 hours and 4 minutes"
  * `P-6H3M`    -- parses as "-6 hours and +3 minutes"
  * `-P6H3M`    -- parses as "-6 hours and -3 minutes"
  * `-P-6H+3M`  -- parses as "+6 hours and -3 minutes"

```json
"application": "weather",
"duration": "PT10S",
"configuration": {
  "location": "Weinheim,de",
  "unit": "C"
}
```


Override frames config using application.json
-------------

You can override the frame configuration in case you are not happy with the frame configuration provided within
the application. Create an `application.json` file within the `config` directory and add an override configuration.
See [application.json](#applicationjson) for details on the structure.


Extending pixl
-------------
pixl is open for extension by plugins. There are two categories of plugins:

* Devices
* Applications

A plugin is a class that implements the `pixl.api.plugin.Plugin` marker interface and provides one or more devices, applications or both.
The plugin class is not necessarily the device or the application itself but allows access to the devices/applications.

A `Device` plugin implements the `pixl.api.delivery.HasDevices` interface which returns a collection of `Device`s.
An `Application` plugin implements the `pixl.api.application.HasApplications` interface to return a collection of `Application`s.

Every plugin class must reside below the `pixl` base package to be found. The plugin discovery relies on Spring annotated classes (using `@ComponentScan`).

Injectable infrastructure resources are:

* `HttpClient` (Apache Http Client 4)
* `HttpAsyncClient` (Apache Async Http Client 4)
* `RestTemplate`
* Spring infrastructure types (`ApplicationContext`, `ResourceLoader`, ...)


Applications
-------------
Applications provide one or more values that are displayed using the definition in the [application frames](#applicationjson).

An application implements the `pixl.api.application.Application` interface and is required to provide the applicationId (must be unique within the application)
and a `List` of values. A `Value` (and its subclasses) is a container for the data transported within the application. Following types are available:

* `Value` one generic value that can be formatted. The type can be `String`, `Number` or `Date` which can be later on formatted by a format pattern
* `Progress` a progress indicator value that can be used to render a progress bar
* `IconValue` a value with an icon. The icon from the value overrides an icon specified by the frame configuration.

The user interface is defined within the [application frames](#applicationjson). Every frame can have its own value. 
In the case of more frames than values, the last value will be used for all frames that come after the last value index. 

A bundled application contains all required plugin classes, the [application.json](#applicationjson) and optional icon resources. 

### application.json

The application.json file specifies applications and frames. One file can contain specifications for multiple applications.

The format is:

```json
{
  "time": {
    "frames": [
      {
        "type": "text",
        "icon": "time/clock.gif",
        "prefix": {
          "en": "The time is ",
          "de_DE": "Es ist "
        },
        "suffix": {
          "en": "",
          "de_DE": " Uhr" 
        },
        "active": true,
        "duration": "PT20S",
        "getValueOnRepaint": true,
        "formatPattern": {
          "en": "hh:mm a",
          "de": "HH:mm"
        }
      }
    ]
  }
}  
```

`icon`: Optional icon resource path, must not exceed 8x8 in size and must be on the class-path as PNG, GIF or JPEG image.

`type`: mandatory frame type. Available types:
* `TEXT`
* `METRIC`
* `PROGRESS`

`prefix`/`suffix`/`formatPattern`: Optional localizable items. Accept either a map containing a mapping 
between localizations, a string or not present.

`active`: optional, to enable/disable the frame. Useful for override configurations. Defaults to `true`.

`getValueOnRepaint`: Optional, to retrieve the value on each frame refresh. This is handy for applications with real-time
value updates such as a timer, a clock, but not for remote service (appx. 50 to 100 calls/sec to `getValue`)
If `false`, then the value will be retrieved once when the application/frame is activated. Defaults to `false`.


How to build
-------------
Pixl is built using Gradle.

```
$ ./gradle
```