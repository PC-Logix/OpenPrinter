--[[
    xerox.lua  A simple program for copying OpenPrinter Printed Pages
    Version 0.25
	Author: Kodos
   
    TODO:
      Add scan to file via my custom lib
      Probably fix the amount counting
]]
 
local component = require("component")
local shell = require("shell")
 
local printer = component.openprinter
 
printer.clear()
 
local args, options = shell.parse(...)
 
local amt = (tonumber(args[1]) or 1)
 
 
if amt > 9 then
	error("cannot print more than 9 items")
	elseif amt < 1 then
		error("invalid count")
end
 
local pageTitle, lines = printer.scan()
printer.setTitle(pageTitle)
for cnt = 1,amt do
	for x = 0,#lines do
		printer.writeln(lines[x])
	end
	printer.print()
end
