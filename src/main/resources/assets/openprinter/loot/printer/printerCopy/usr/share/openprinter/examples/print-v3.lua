-- OpenPrinter API v3 migration example.
local component = require("component")
local event = require("event")
local printer = component.openprinter

-- API v2 required a mutable page buffer:
-- printer.setTitle("Migration Example")
-- printer.writeln("OpenPrinter API v3", 0x3366FF, "center")
-- printer.writeln("No more manual line buffering.")
-- printer.print()

-- API v3 submits the complete document and returns a persistent job ID.
local document = {
  title = "Migration Example",
  lines = {
    {text = "OpenPrinter API v3", color = 0x3366FF, alignment = "center"},
    {text = "No more manual line buffering."},
    {text = "Long lines wrap and pages paginate automatically."}
  }
}

local job, reason = printer.print(document, {copies = 1, wrap = true})
if not job then error(reason or "could not queue print job") end
io.write("Queued job " .. job .. "\n")

-- Waiting is optional. Programs may save the job ID and check it later.
while true do
  local status, statusReason = printer.status(job)
  if not status then error(statusReason or "job status unavailable") end
  if status.state == "complete" then
    io.write("Print complete\n")
    break
  elseif status.state == "cancelled" then
    error(status.reason or "print job cancelled")
  elseif status.state == "blocked" then
    io.write("Printer blocked: " .. status.reason .. "\n")
  end
  event.pull(1, "openprinter_job")
end
