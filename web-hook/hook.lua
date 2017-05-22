local data = ngx.req.get_body_data()
local repo_key = string.gsub(ngx.var.uri, "/webhook/", "")

ngx.say("RUN WEBHOOK")

ngx.say("webhook :" .. repo_key)

function exec (cmd)
  local handle = io.popen("bash -c '" .. cmd .. "'")
  local result = handle:read("*a")
  handle:close()
  return result
end

local result = exec("source /envars && export KUBECONFIG=/root/.kube/config && /usr/local/bin/kubectl get configmaps repositories -o json")

ngx.say("repos :" .. result)

local cjson = require("cjson")
local value = cjson.new().decode(result)

local repo_url = value.data[(repo_key .. ".repo")]

if repo_url == nil then
  ngx.say("no repo registered for " .. repo_key .. ", please update ConfigMap")
  ngx.exit(ngx.HTTP_INTERNAL_SERVER_ERROR)
  return
end

ngx.say("clone: " .. repo_url );
ngx.say(exec("mkdir -p /workspace && cd /workspace && git clone " .. repo_url .. " " .. repo_key))
ngx.say("pull repo");
ngx.say(exec("cd /workspace/" .. repo_key .. " && git pull"))
ngx.say("ls repo");
ngx.say(exec("ls -lah /workspace/" .. repo_key))

local repo_cmd = value.data[(repo_key .. ".command")] or "./runme"

ngx.say(exec("cd /workspace/" .. repo_key .. " && " .. repo_cmd))

ngx.say("DONE")

ngx.exit(200)

