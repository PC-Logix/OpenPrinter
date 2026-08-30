-- Copies the document in an OpenPrinter scanner slot.
-- Usage: xerox [copies]
local component = require("component")
local shell = require("shell")

local args = shell.parse(...)
if #args > 1 or args[1] == "help" then
  io.write("Usage: xerox [copies]\n")
  io.write("Copies the printed page or book in the scanner slot.\n")
  return
end

local copies = tonumber(args[1] or "1")
if not copies or copies < 1 or copies > 64 or copies % 1 ~= 0 then
  io.stderr:write("copies must be a whole number from 1 to 64\n")
  os.exit(1)
end

local printer = component.openprinter
local document, scanReason = printer.scan()
if not document then
  io.stderr:write((scanReason or "please load a document into the scanner slot") .. "\n")
  os.exit(1)
end

local job, printReason = printer.print(document, {copies = copies})
if not job then
  io.stderr:write((printReason or "could not queue copies") .. "\n")
  os.exit(1)
end

io.write(string.format("Queued %d %s as job %s\n",
  copies, copies == 1 and "copy" or "copies", job))
