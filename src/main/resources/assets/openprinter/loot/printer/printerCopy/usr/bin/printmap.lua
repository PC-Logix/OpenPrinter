-- Prints a local or HTTP(S) image onto an empty vanilla map.
local component = require("component")
local filesystem = require("filesystem")
local shell = require("shell")

local MAX_BYTES = 8 * 1024 * 1024

local function usage()
  io.write("Usage: printmap [options] <file|url>\n")
  io.write("  --fit=contain|cover|stretch   Scaling mode (default: contain)\n")
  io.write("  --filter=nearest|bilinear|bicubic (default: bicubic)\n")
  io.write("  --background=RRGGBB          Background for transparency\n")
  io.write("  --dither / --no-dither       Map-palette dithering\n")
  io.write("  --copies=N, --title=TITLE\n")
end

local function append(chunks, chunk, total)
  total = total + #chunk
  if total > MAX_BYTES then error("image exceeds 8 MiB", 0) end
  chunks[#chunks + 1] = chunk
  return total
end

local function readLocal(path)
  local file, reason = io.open(shell.resolve(path), "rb")
  if not file then error(reason or ("cannot open " .. path), 0) end
  local chunks, total = {}, 0
  local ok, failure = pcall(function()
    while true do
      local chunk = file:read(8192)
      if not chunk then break end
      total = append(chunks, chunk, total)
    end
  end)
  file:close()
  if not ok then error(failure, 0) end
  return table.concat(chunks)
end

local function readUrl(url)
  if not component.isAvailable("internet") then
    error("an Internet Card is required for URLs", 0)
  end
  local internet = require("internet")
  local ok, response = pcall(internet.request, url, nil, {
    ["user-agent"] = "OpenPrinter/0.2 printmap"
  })
  if not ok then error("HTTP request failed: " .. tostring(response), 0) end
  local chunks, total = {}, 0
  local readOk, failure = pcall(function()
    for chunk in response do total = append(chunks, chunk, total) end
  end)
  if not readOk then
    if response.close then pcall(response.close) end
    error(failure, 0)
  end
  return table.concat(chunks)
end

local function parseColor(value)
  if not value then return 0xFFFFFF end
  value = tostring(value):gsub("^#", ""):gsub("^0[xX]", "")
  local color = tonumber(value, 16)
  if not color or color < 0 or color > 0xFFFFFF then
    error("background must be a six-digit RGB color", 0)
  end
  return color
end

local args, options = shell.parse(...)
if options.help or options.h or #args ~= 1 then
  usage()
  if #args ~= 1 then os.exit(1) end
  return
end

local source = args[1]
local isUrl = source:match("^https?://") ~= nil
local ok, data = pcall(isUrl and readUrl or readLocal, source)
if not ok then
  io.stderr:write(tostring(data) .. "\n")
  os.exit(1)
end

local inferredTitle
if isUrl then
  inferredTitle = source:gsub("[?#].*$", ""):match("([^/]+)$") or "Printed Map"
else
  inferredTitle = filesystem.name(source)
end

local dither = not options["no-dither"]
if options.dither then dither = true end
local job, reason = component.openprinter.printImage(data, {
  title = options.title or options.t or inferredTitle,
  copies = tonumber(options.copies or options.c or 1),
  fit = options.fit or "contain",
  filter = options.filter or "bicubic",
  background = parseColor(options.background),
  dither = dither
})
if not job then
  io.stderr:write((reason or "could not queue map image") .. "\n")
  os.exit(1)
end

io.write("Queued map image job " .. job .. "\n")
