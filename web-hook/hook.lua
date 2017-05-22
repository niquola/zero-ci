local data = ngx.req.get_body_data()
local uri = string.gsub(ngx.var.uri, "/webhook/", "")
ngx.say("webhook :" .. uri)
ngx.say("body " .. (data or ""))

os.execute("cd /workspace && git pull")

local handle = io.popen("bash /workspace/" .. uri .. " 2>&1")
local result = handle:read("*a")
handle:close()

ngx.say(result)
ngx.exit(200)
