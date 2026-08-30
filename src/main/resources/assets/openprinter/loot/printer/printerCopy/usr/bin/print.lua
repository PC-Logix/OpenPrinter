-- Queues a text file or standard input on an OpenPrinter.
local component = require("component")
local filesystem = require("filesystem")
local shell = require("shell")

local args, options = shell.parse(...)
local text
if args[1] then
  local path = shell.resolve(args[1])
  local file, reason = io.open(path, "r")
  if not file then
    io.stderr:write((reason or "cannot open " .. path) .. "\n")
    os.exit(1)
  end
  text = file:read("*a")
  file:close()
else
  text = io.read("*a")
end

local copies = tonumber(options.copies or options.c or 1)
local title = options.title or options.t or (args[1] and filesystem.name(args[1]) or "")
local job, reason = component.openprinter.print(text, {
  title = title,
  copies = copies,
  wrap = not options.nowrap
})
if not job then
  io.stderr:write((reason or "could not queue print job") .. "\n")
  os.exit(1)
end

io.write("Queued print job " .. job .. "\n")
