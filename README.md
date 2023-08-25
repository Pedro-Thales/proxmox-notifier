# Proxmox Notifier

This project was created to be an integration to the Proxmox api to sent notifications via some webhooks and
integrations, with some status about our server


## Running
Running with the docker image you need to override the properties with environment variables as below:
```
PROXMOX_API_URL=https://proxmoxapi-url.com/
PROXMOX_AUTH_TOKEN=Token
```

---
#### You can do the same thing for each property in the applications.properties file.

This property
```
feign.client.insecure=true
```
Will be this environment variable:
```
FEIGN_CLIENT_INSECURE=true
```

Spring knows implicitly to look for env_variables of the same name as property with the . and camelcase replaced by _

#### As explained in the spring boot documentation https://docs.spring.io/spring-boot/docs/3.1.2/reference/html/features.html#features.external-config.typesafe-configuration-properties.relaxed-binding.environment-variables

---

## Build
### Requirements

In order to use this project you need to create these environment variables:

```
PROXMOX_API_URL=https://proxmoxapi-url.com/
PROXMOX_AUTH_TOKEN=Token
```

### For further information read the file HELP.md
