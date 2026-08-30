--[[
    printerQueue.lua  View and manage the OpenPrinter print queue.
    Usage: printerQueue [list|clear|remove <index>]
]]

local component = require("component")
local shell = require("shell")

local printer = component.openprinter
local args = shell.parse(...)
local command = args[1] or "list"

if command == "list" then
    local queue = printer.getPrintQueue()
    if #queue == 0 then
        print("Print queue is empty.")
        return
    end

    for i, job in ipairs(queue) do
        local description = string.format("%d: %s, %d page(s)/copy, %d copy/copies remaining, %s",
            i, job.type, job.pages, job.copiesRemaining, job.status)
        if job.status == "printing" then
            description = description .. string.format(" (%d/%d ticks)", job.progress, job.duration)
        end
        print(description)
        if job.blocker then
            print("   waiting: " .. job.blocker)
        end
    end
elseif command == "clear" then
    local cleared = printer.clearPrintQueue()
    print("Cleared " .. cleared .. " print job(s).")
elseif command == "remove" then
    local index = tonumber(args[2])
    if not index then
        error("usage: printerQueue remove <index>")
    end

    local success, message = printer.removePrintJob(index)
    if not success then
        error(message)
    end
    print("Removed print job " .. index .. ".")
else
    error("usage: printerQueue [list|clear|remove <index>]")
end
