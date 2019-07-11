--[[
    printerCopy.lua  A simple program for copying OpenPrinter Printed Pages
    Version 0.5
	Author: Kodos, ben_mkiv
   
    TODO:
      Add scan to file via my custom lib
      Probably fix the amount counting
]]
 
local component = require("component")
local shell = require("shell")
 
local printer = component.openprinter
 
local args, options = shell.parse(...)
 
local copyCount = (tonumber(args[1]) or 1)

if copyCount > 9 then
	error("cannot print more than 9 items")
	elseif copyCount < 1 then
		error("invalid count")
end
 
local pageTitle, lines = printer.scan()

if pageTitle == false then
	print("please check if theres a printed page to scan in the scanner slot!")
	os.exit(1)
end

printer.clear()

if pageTitle ~= nil then
	printer.setTitle(pageTitle)
end

for x = 0,#lines do
	printer.writeln(lines[x])
end

printer.print(copyCount)
