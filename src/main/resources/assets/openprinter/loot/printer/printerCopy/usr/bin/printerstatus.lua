-- Shows the active OpenPrinter queue and supply levels.
local component = require("component")
local printer = component.openprinter
local queue = printer.queue()
local supplies = printer.supplies()

if #queue == 0 then
  io.write("Printer is idle\n")
else
  for index, job in ipairs(queue) do
    local detail = job.state
    if job.reason and job.reason ~= "" then detail = detail .. ": " .. job.reason end
    io.write(string.format("%d. %s  %d/%d pages  %s\n",
      index, job.id, job.pagesComplete, job.pagesTotal, detail))
  end
end
io.write(string.format("Paper: %d  Black: %d  Color: %d  Output: %d/%d free\n",
  supplies.paper, supplies.blackInk, supplies.colorInk,
  supplies.outputFree, supplies.outputSlots))
