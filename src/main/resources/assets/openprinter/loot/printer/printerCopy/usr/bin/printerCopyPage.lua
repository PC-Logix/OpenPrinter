-- Copies the document in the scanner slot using the OpenPrinter job API.
local component = require("component")
local shell = require("shell")

local printer = component.openprinter
local args = shell.parse(...)
local copies = tonumber(args[1]) or 1

local document, scanReason = printer.scan()
if not document then
  io.stderr:write((scanReason or "please load a document into the scanner slot") .. "\n")
  os.exit(1)
end

local job, printReason = printer.print(document, {copies = copies})
if not job then
  io.stderr:write((printReason or "could not queue print job") .. "\n")
  os.exit(1)
end

io.write("Queued print job " .. job .. "\n")
