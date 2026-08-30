# OpenPrinter

OpenPrinter adds a network-attached printer, shredder, file cabinet, folders, and portable briefcases for OpenComputers.

This branch targets Minecraft 1.21.1 and NeoForge 21.1. It is developed against the sibling `../OpenComputers` checkout so the compile-time API and development runtime use the same OpenComputers build.

## Development

Requirements:

- JDK 21
- A built Minecraft 1.21.1 OpenComputers checkout at `../OpenComputers`

Build both projects from PowerShell:

```powershell
cd ..\OpenComputers
.\gradlew.bat build
cd ..\OpenPrinter
.\gradlew.bat build
```

The OpenPrinter jar is written to `build/libs/openprinter-0.2.0.jar`. Client and server development runs are available through `runClient` and `runServer`.

### IntelliJ IDEA

Open the repository directory or `build.gradle` as a Gradle project and select a JDK 21 installation when prompted. The shared IntelliJ configurations provide:

- `Minecraft Client`
- `Minecraft Server`
- `GameTest Server`
- `Data Generation`
- `Build OpenPrinter`

To regenerate the IntelliJ module and NeoForge launch metadata from PowerShell, run:

```powershell
.\gradlew.bat idea neoForgeIdeSync createLaunchScripts
```

The original 1.12.1 Java implementation is retained under `reference/1.12.1/java` for porting and behavior comparisons. Maintained 1.21.1 code lives under `src/main/java`.

## Rendering

The printer uses NeoForge's built-in `neoforge:obj` model loader with the original OBJ and material files. OpenPrinter does not ship a custom OBJ renderer.

## Lua component

The printer is exposed as `component.openprinter`. API version 4 accepts complete documents and map images and runs them through a persistent FIFO spooler:

```lua
local printer = require("component").openprinter

local job, reason = printer.print("Line 1\nLine 2\nLine 3", {
  title = "Report",
  copies = 2,
  wrap = true
})
assert(job, reason)
```

Long lines wrap to the printable width and documents paginate after 20 lines. `print` returns immediately with a job ID. A job pauses when it runs out of paper, required ink, power, or output space and resumes after the player fixes the blocker. Paper and ink are consumed only when a completed physical page enters the output tray. Black-only pages do not require color ink.

Structured documents can specify per-line formatting:

```lua
printer.print({
  title = "Status",
  lines = {
    {text = "OpenPrinter", color = 0x3366FF, alignment = "center"},
    {text = "Ready", color = 0, alignment = "left"}
  }
})
```

An empty vanilla map in the paper input slot can be printed with a custom image. `printMap` accepts 1-128 rows of 1-128 `0xRRGGBB` pixels; rows must have equal widths. The printer scales the image to 128x128, converts it to Minecraft's map palette, consumes one empty map and 20 color-ink durability, and outputs a locked filled map so terrain cannot overwrite the image:

```lua
local image = {}
for y = 1, 64 do
  image[y] = {}
  for x = 1, 64 do
    image[y][x] = (x * 4) * 0x10000 + (y * 4) * 0x100 + 0x80
  end
end

local job, reason = printer.printMap(image, {title = "Gradient", copies = 1})
assert(job, reason)
```

For ordinary image files, the green **OpenPrinter** tools disk in the OpenPrinter creative tab includes `printmap`. Install all of its programs with OpenOS's standard installer:

```sh
install OpenPrinter
```

You can also run `install.lua` directly from the mounted disk. Like OpenComputers' IRC disk, OpenPrinter registers its program locations with OpenOS, so attempting to run a missing tool tells you which disk to install. `printmap` accepts PNG, JPEG, GIF, and BMP files from the OpenComputers filesystem, or downloads them when an Internet Card is installed:

```sh
printmap --fit=contain --filter=bicubic --dither /home/logo.png
printmap --fit=cover --title="Server Art" https://example.com/art.jpg
```

`contain` preserves the whole image and letterboxes it, `cover` preserves aspect ratio while cropping the edges, and `stretch` fills the map without preserving aspect ratio. Transparent pixels are composited over white by default; use `--background=RRGGBB` to change it. Inputs are limited to 8 MiB, 4096 pixels on either axis, and 16 megapixels. Animated GIFs use their first frame. Internet access follows the Internet Card's normal server configuration and HTTP restrictions.

Callbacks:

- `greet()` returns the traditional OpenPrinter component signature.
- `print(document[, options])` queues a string or structured document.
- `printMap(image[, options])` prints an RGB pixel table onto an empty vanilla map.
- `printImage(data[, options])` decodes PNG, JPEG, GIF, or BMP bytes and applies scaling/filter options.
- `printLabel(text[, options])` queues printed name tags.
- `status([jobId])`, `queue()`, and `cancel(jobId)` manage jobs.
- `scan()` returns a structured document that can be passed directly to `print`.
- `supplies()` and `capabilities()` report consumables, output space, features, and limits.

State changes emit `openprinter_job` signals with the printer address, job ID, state, and reason. The loot disk includes:

- `print [--copies=N] [--title=Title] [file]` to print a file or standard input.
- `xerox [copies]` to duplicate the printed page or book in the scanner slot.
- `printerstatus` to inspect jobs and supplies.
- `printmap [options] <file|url>` to print a local or downloaded image onto a map.
- `/usr/share/openprinter/examples/print-v3.lua`, a commented migration example covering structured documents and job waiting.

`printercopypage` remains available as an alias for worlds that used the short-lived port command. The API intentionally replaces the old page-buffer callbacks (`writeln`, `setTitle`, `clear`, and the split scan helpers).

Project history and documentation: [PC-Logix/OpenPrinter](https://github.com/PC-Logix/OpenPrinter)
